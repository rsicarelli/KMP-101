## Explorando a Modularização no KMP

No último artigo, entramos em detalhes e aprendemos sobre as peculiaridades do código exportado nos headers do Objective-C, assim como as boas práticas no que exportar.

Nesse artigo, vamos entender melhor sobre o comportamento da modularização em projetos KMP, e como isso pode ser feito de forma eficiente e organizada.

## O que é modularização?

Não irei me alongar muito nesse tópico, pois já abordamos esse assunto no [Android Plataforma - Parte 1: Modularização](https://dev.to/rsicarelli/android-plataforma-parte-1-modularizacao-2016). Se não tem certeza do que é modularização, recomendo uma pausa para a leitura do artigo.

Em resumo, modularização é a prática de dividir um projeto em módulos menores e independentes, que podem ser desenvolvidos, testados e mantidos separadamente.

Essa prática é crucial para escalar projetos KMP, já que a modularização impacta diretamente na autonomia e independência dos times internos, evitando que um time dependa do outro para realizar suas tarefas.

## Modularização no KMP

No KMP, a modularização é feita através de módulos compartilhados, que são responsáveis por compartilhar código entre as plataformas.

Vamos elaborar uma estrutura de módulos que respeite a separação de responsabilidades e possibilite a reutilização de código de forma eficiente entre módulos. Nosso contexto aqui é pensando em uma aplicação que irá escalar, no sentido de mais features e mais plataformas:

<img src="https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-modularization-pt1.png?raw=true" />

Essa estrutura segue algumas ideias do Domain Driven Design (DDD), onde cada módulo representa um domínio independente e isolado da aplicação. Não irei entrar em muitos detalhes sobre o DDD, mas recomendo a leitura do livro [Domain-Driven Design: Tackling Complexity in the Heart of Software](https://www.amazon.com.br/Domain-Driven-Design-Tackling-Complexity-Software/dp/0321125215/ref=sr_1_1?dib=eyJ2IjoiMSJ9.Lo7-Md3VvIV38Rzn-ytmnX1FyJz_hHxG_c3ocyge7LEEkMf9J0QQUC_vNRqM-bly1FEW6JDWiQjxRiR4Ip4uOSi5BDadwwQLRq-qGmgXmoG36NnUp66mVBVEOL-xFpHChmTWdyWDB5EZGboxu2dOIVTrzRS54KI4S6rDRsLLLoSAkU9bCl81j0cePEicQvqB.QPWgwg7lUfTottKjOov5grb2CciIICVV12MWxs8bueA&dib_tag=se&keywords=Domain-Driven-Design-Tackling-Complexity-Software&qid=1739362218&sr=8-1&ufe=app_do%3Aamzn1.fos.4bddec23-2dcf-4403-8597-e1a02442043d) para entender melhor sobre o assunto.

Com essa estrutura, conseguimos:

- Escalar de forma eficiente sem duplicação de código. Ao criar uma nova feature, basta criar um novo módulo e adicionar as dependências necessárias.
- Ter uma granulalidade no que será exportado para as outras plataformas, especialmente para o XCFramework.
- Ter a independencia de domínio para times específicos, evitando conflitos de código e responsabilidades. Por exemplo, times podem criar um `CODEOWNER` para um módulo específico, e serem responsáveis por manter e evoluir esse módulo.

## Pavimentando flexibilidade da UI

Uma dos super poderes do KMP é compartilhar muito, ou pouco código. Essa habilidade implica que podemos escolher qual UI iremos utilizar em cada plataforma. Dependendo da sua estratégia de construção de UI, você irá precisar de uma abordagem específica de módulos para criar essa flexibilidade.

Vamos pensar que cada feature pode ser separada em um "frontend" e "backend". Seguindo o padrão de arquitetura MVVM, o "frontend" seria a nossa UI (Compose, SwiftUI) e o "backend" seria a nossa lógica de negócio (ViewModel/UiModel + Domain + Data). Ou seja, partes da camada de apresentação pode ser compartilhada, mas damos a liberdade para cada plataforma de escolher a sua UI.

Com isso em mente, uma abordagem que pode ser utilizada é a seguinte:

<img src="https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-modularization-pt2.png?raw=true" />

Aqui, nós separamos cada feature que possuí uma tela em 3 módulos:

- `common`, nosso "backend" que contém a lógica de negócio da feature.
- `android-ui`, nosso "frontend" apenas em Android, que contém a UI da feature.
- `common-ui`, nosso "frontend" multiplataforma, que contém a UI da feature compartilhada entre as plataformas.

Com essa abordagem, é possível:

- Iniciar migrações de telas em SwiftUI gradualmente, sem a necessidade de migrar toda a feature de uma vez.
- Flexibilidade de migrar features Jetpack Compose (apenas Android) enquanto compartilha o "backend" com outras plataformas.
- Flexibilidade de iniciar telas em Compose Multiplatform (Android, iOS, Desktop, ...) enquanto compartilha o "backend" com outras plataformas.

## Exportando para o XCFramework

Agora que exploramos um modelo de modularização que permite flexibilidade na escolha da UI, podemos avançar e exportar nosso código Kotlin para o XCFramework.

Para utilizarmos nosso código Kotlin no iOS, precisamos de um módulo que represente nosso XCFramework. Esse é um módulo "cola", ou seja, um módulo que coleta vários módulos que serão exportados para o XCFramework.

Esse módulo não será utilizado diretamente pelo app Android ou outras plataformas, mas irá representar nossa exportação para o iOS. Esse módulo comumente é chamado de `ios-interop`.

Para exemplificar o poder da modularização e a flexibilidade do KMP, vamos explorar alguns cenários de compartilhamento:

### Cenário 1: "backend" KMP compartilhado, "frontend" flexível

Nesse cenário, temos um módulo `common` que contém a lógica de negócio da feature. O módulo `android-ui` contém a UI da feature apenas para Android que é utilizada pelo app Android.

Características desse modelo:

1. A lógica de negócio é compartilhada entre as plataformas.
2. A UI é específica para Android utilizando Jetpack Compose.
3. A UI não é compartilhada entre as plataformas.
4. No iOS, a lógica de negócio é utilizada, mas a UI é específica para iOS utilizando SwiftUI.
5. Esse modelo é ideal para projetos que desejam migrar para Compose gradualmente, ou para projetos que desejam manter a UI específica para cada plataforma.

<img src="https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-modularization-scenario-1.png?raw=true" />

### Cenário 2: híbrido, migrando para Compose Multiplatform

Nesse cenário, temos um módulo `common` que contém a lógica de negócio da feature. O módulo `common-ui` contém a UI da feature compartilhada entre as plataformas.

Aqui, estamos iniciando a migração para Compose Multiplatform, enquanto ainda temos a feature `android-ui` específica para Android.

Características desse modelo:

1. A lógica de negócio é compartilhada entre as plataformas.
2. Parte da UI é compartilhada entre as plataformas.
3. No `android-ui`, partes da UI são específicas para Android utilizando Jetpack Compose.
4. No `common-ui`, partes da UI são compartilhadas entre as plataformas utilizando Compose Multiplatform.
5. Esse modelo é ideal para projetos que desejam iniciar a migração para Compose Multiplatform, com a habilidade de migrar partes da UI gradualmente.

<img src="https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-modularization-scenario-2.png?raw=true" />

### Cenário 3: 100% Compose Multiplatform

Nesse cenário, temos um módulo `common` que contém a lógica de negócio da feature. O módulo `common-ui` contém a UI da feature compartilhada entre as plataformas.

Aqui, não temos distinção por plataforma, e toda a UI é compartilhada entre as plataformas utilizando Compose Multiplatform.

Características desse modelo:
1. A lógica de negócio é compartilhada entre as plataformas.
2. A UI é compartilhada entre as plataformas utilizando Compose Multiplatform.
3. Esse modelo é ideal para projetos que desejam compartilhar toda a UI entre as plataformas, sem distinção.

<img src="https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-modularization-scenario-3.png?raw=true" />