## Guia prático para configurar e entender o ambiente no Kotlin Multiplataforma

> * [Exigências das plataformas](#exigências-das-plataformas)
> * [JDK como requisito mínimo no KMP](#jdk-como-requisito-mínimo-no-kmp)
>   * [JDK e suas versões](#jdk-e-suas-versões)
>   * [Diversidade de distribuidores do JDK](#diversidade-de-distribuidores-do-jdk)
> * [Configurando seu Mac para o ambiente KMP](#configurando-seu-mac-para-o-ambiente-kmp)
>   * [1. Instalando o `kdoctor`](#1-instalando-o-kdoctor)
>   * [2. Instalando o JetBrains Toolbox](#2-instalando-o-jetbrains-toolbox)
>   * [3. Preparando o ambiente KMP com o JDK](#3-preparando-o-ambiente-kmp-com-o-jdk)
>   * [4. Preparando o ambiente Android no KMP](#4-preparando-o-ambiente-android-no-kmp)
>   * [5. Preparando o ambiente Apple no KMP](#5-preparando-o-ambiente-apple-no-kmp)
>   * [✅ Verificando o ambiente KMP](#-verificando-o-ambiente-kmp)
> * [Escolhendo o ambiente de desenvolvimento integrado (IDE)](#escolhendo-o-ambiente-de-desenvolvimento-integrado-ide)
>   * [Android Studio](#android-studio)
>   * [IntelliJ Ultimate](#intellij-ultimate)
>   * [VSCode](#vscode)
>   * [Xcode](#xcode)
>   * [Fleet](#fleet)
>   * [Afinal, qual escolher?](#afinal-qual-escolher)
> * [Conclusão](#conclusão)

O desenvolvimento de software é um processo complexo que exige não apenas habilidade e criatividade, mas também um ambiente de desenvolvimento bem configurado.

No mundo do Kotlin Multiplataforma, esta necessidade é ainda mais evidente: configurar adequadamente o ambiente de desenvolvimento para KMP é crucial para garantir que devs possam aproveitar ao máximo as capacidades multiplataforma da linguagem.

Neste artigo, vamos explorar os componentes chave desse ambiente, desde a escolha do JDK até o uso de IDEs específicas, proporcionando um guia prático para configurar seu ambiente para projetos KMP.

---

## Exigências das plataformas

Como uma ferramenta destinada ao desenvolvimento nativo, o KMP exige uma configuração de ambiente semelhante àquela usada em métodos de desenvolvimento convencionais.

Por exemplo, para a família Apple usando Kotlin/Native, é fundamental ter um Mac com Xcode instalado.

Por outro lado, para outras plataformas, como web, desktop ou Android, sistemas operacionais como Windows ou Linux são suficientes.

Dada a abrangência do KMP, escolhemos o Mac como o sistema operacional para este e os próximos artigos.

## JDK como requisito mínimo no KMP

Um aspecto crucial no desenvolvimento multiplataforma com Kotlin, independentemente do sistema operacional, é a necessidade de um [JDK (Java Development Kit)](https://en.wikipedia.org/wiki/Java_Development_Kit). O JDK é vital em várias etapas do desenvolvimento Kotlin, como:

- **Compilação para Bytecode:** Assim como o Java, o Kotlin é uma linguagem de alto nível compilada para bytecode. Esse processo de compilação, realizado pelo compilador Java incluído no JDK, é crucial para executar o código Kotlin em uma JVM (Java Virtual Machine), especialmente em plataformas baseadas em JVM, como backend e Android.

- **Interdependência de JDK e IDEs:** Dada a integração profunda do Kotlin com ambientes de desenvolvimento como IntelliJ IDEA e Android Studio, o JDK é usado para realizar tarefas essenciais como a execução de tarefas do [Gradle](https://gradle.org/), uma ferramenta fundamental para a construção de projetos Kotlin Multiplataforma.

### JDK e suas versões

Escolher a versão correta do JDK depende muito das dependências do seu projeto, desde o nível de infraestrutura, como a versão do Kotlin e Gradle, até bibliotecas open-source.

Por padrão, o compilador [Kotlin/JVM produz bytecode compatível com Java 8](https://kotlinlang.org/docs/faq.html#which-versions-of-jvm-does-kotlin-target). Contudo, para usufruir das otimizações presentes em versões mais recentes do Java, você pode definir explicitamente a versão alvo do Java, que varia de 9 a 21.

Optar pelas versões mais recentes do JDK oferece vantagens significativas, incluindo melhor compatibilidade com atualizações recentes de linguagens e frameworks, otimizações de desempenho, acesso a recursos mais atuais, reforço na segurança por correções de vulnerabilidades e alinhamento com as práticas mais recentes da comunidade de desenvolvimento.

Como dev KMP, é comum precisar de múltiplas versões do JDK no seu ambiente de desenvolvimento, desde a mais antiga `JDK8/1.8` até versões mais recentes como `JDK17`, `JDK20` ou `JDK21`.

### Diversidade de distribuidores do JDK

A variedade de fornecedores de JDK para Mac é uma resposta às alterações nas políticas da Oracle e à procura por opções mais versáteis e adaptáveis a diferentes necessidades. Alguns exemplos incluem:

- **[JetBrains Runtime](https://github.com/JetBrains/JetBrainsRuntime):** Uma versão do OpenJDK disponível para Windows, Mac OS X e Linux. Oferece recursos como redefinição aprimorada de classes, um framework para navegadores baseados em Chromium (JCEF) e melhorias na renderização de fontes e suporte a HiDPI.

- **[AdoptOpenJDK](https://adoptium.net/en-GB/):** Fornece binários do OpenJDK com a JVM HotSpot ou OpenJ9, desenvolvida pela IBM. Segue a programação de atualizações do OpenJDK, oferecendo atualizações regulares de recursos e segurança.

- **[Amazon Corretto](https://aws.amazon.com/corretto/?filtered-posts.sort-by=item.additionalFields.createdDate&filtered-posts.sort-order=desc):** Distribuição certificada do OpenJDK pela Amazon, livre para uso em produção sob licença GPL + CE. Disponível para Linux, macOS e Windows, com atualizações trimestrais.

- **[Zulu da Azul Systems](https://www.azul.com/downloads/#zulu):** Build certificado do JDK para múltiplas plataformas, incluindo macOS. Gratuito para download e uso, com atualizações de segurança e correções de bugs disponíveis mediante assinatura do Zulu Enterprise.

> [🔗 Lista de distribuidores do JDK do SDKMAN!](https://sdkman.io/jdks)

## Configurando seu Mac para o ambiente KMP

Vamos seguir a [documentação oficial](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-setup.html) para configurar o ambiente, utilizando as tecnologias a seguir:

- [Homebrew](https://brew.sh/): Será utilizado para facilitar a instalação de scripts.
- [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/): Uma ferramenta eficaz para gerenciar IDEs da JetBrains, desenvolvida em KMP.
- [kdoctor](https://github.com/Kotlin/kdoctor): Uma ferramenta da JetBrains que realiza diagnósticos do ambiente KMP.
- [Xcode](https://developer.apple.com/xcode/resources/): Fundamental para acessar ferramentas específicas da Apple.
- [SDKMAN!](https://sdkman.io/): Usado para gerenciar diferentes versões do JDK.

### 1. Instalando o `kdoctor`

Assumindo que você já tenha o [Homebrew](https://brew.sh/) instalado, use o seguinte comando para instalar o `kdoctor` e executá-lo no terminal. Esse comando será útil durante o processo para verificar se a configuração foi bem-sucedida.

```shell
brew install kdoctor && kdoctor
```

### 2. Instalando o JetBrains Toolbox

Instale o [Toolbox](https://www.jetbrains.com/toolbox-app/) com o comando abaixo.

```shell
brew install --cask jetbrains-toolbox
```

### 3. Preparando o ambiente KMP com o JDK

O JDK é um requisito essencial para programar em KMP, e a configuração do JDK no Mac pode ser desafiadora, especialmente se você desejar ter múltiplas versões instaladas. O [SDKMAN!](https://sdkman.io) simplifica este processo, oferecendo uma CLI que facilita a instalação e a troca entre diferentes versões e distribuidores de JDKs.

|   | Passo                                               | Descrição                                       |
|---|-----------------------------------------------------|-------------------------------------------------|
| 1 | Instale o SDKMAN! seguindo a documentação oficial   | [sdkman.io/install](https://sdkman.io/install)  |
| 2 | Instale o JDK 17.0.9-jbr                            | `sdk install java 17.0.9-jbr`                   |
| 3 | Defina a versão global do JDK                       | `sdk default java 17.0.9-jbr`                   |
| 4 | Verifique a versão atual do JDK                     | `sdk current java`                              |
| 5 | Para instalar outras versões, repita o processo     | `sdk list java`                                 |
| 6 | Verifique se a configuração foi bem-sucedida        | `kdoctor`                                       |

### 4. Preparando o ambiente Android no KMP

Para desenvolver para Android em Kotlin Multiplataforma, é necessário configurar adequadamente o ambiente. Isso inclui a instalação do [Android Studio](https://developer.android.com/studio), que fornece ferramentas essenciais para o desenvolvimento Android, como o [Android SDK](https://en.wikipedia.org/wiki/Android_SDK) e um [Emulador](https://developer.android.com/studio/run/emulator).

|   | Passo                                      | Descrição                                                              |
|---|--------------------------------------------|------------------------------------------------------------------------|
| 1 | Instale o Android Studio pelo Toolbox      | Procure por "Android Studio" na lista do Toolbox                       |
| 2 | Ou instale o Android Studio via Homebrew   | `brew install --cask android-studio`                                   |
| 3 | Conclua a instalação do Android Studio     | Abra o app do Android Studio e siga o passo a passo para a instalação. |
| 4 | Verifique se a instalação foi bem-sucedida | `kdoctor`                                                              |

### 5. Preparando o ambiente Apple no KMP

Para ambientes da Apple, precisamos da coleção de ferramentas do Xcode.

|   | Passo                                         | Descrição                                                                                                              |
|---|-----------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| 1 | Verifique o mapa de compatibilidade do Kotlin | [🔗 Guia de compatibilidade](https://kotlinlang.org/docs/multiplatform-compatibility-guide.html#version-compatibility) |
| 2 | Instale o Xcode                               | [🔗 Xcode na App Store](https://apps.apple.com/app/xcode/id497799835)                                                  |
| 3 | Abra o Xcode                                  | Procure e abra app do Xcode nas suas aplicações.                                                                       |
| 4 | Aceitar os termos de uso da Apple             | Siga os passos para concluir a instalação no Xcode. <br/> Você deve ir avançando até ver a tela de criar um projeto.   |
| 5 | Verifique se deu certo                        | `kdoctor`                                                                                                              |

> ℹ️ O KMP não precisa do Xcode aberto para funcionar. Após a configuração, você pode fechar o Xcode se preferir.

#### 5.1 (Opcional) Configure o CocoaPods

Opcionalmente, você pode realizar a configuração do CocoaPods. Não iremos precisar para esse artigo. Mas caso precise, siga a [🔗 documentação oficial](https://kotlinlang.org/docs/native-cocoapods.html).

### ✅ Verificando o ambiente KMP

Ao rodar o `kdoctor`, é importante que nenhum erro em vermelho apareça. Já que nosso foco é o mínimo para o funcionamento, itens em amarelo não é necessariamente um problema por hora.

## Escolhendo o ambiente de desenvolvimento integrado (IDE)

Nesta etapa, estamos quase prontos para iniciar um novo projeto em KMP. Só falta entendermos um pouco melhor sobre as ferramentas disponíveis para uso atualmente (novembro de 2023)

Devido à profunda integração nativa do KMP, gerenciar múltiplas bases de código apresenta desafios práticos, tais como:

- A capacidade de buscar uma referência em arquivos Swift ou JavaScript e encontrar a declaração correspondente em Kotlin.
- A habilidade de executar seu projeto em um emulador.
- Suporte a depuração de código, independente da linguagem.
- Execução de testes com relatórios de sucesso e falha, além de logs no console.
- Ferramentas de análise, como desempenho, qualidade de código, etc.

A tabela a seguir mapeia o suporte atual a cada linguagem no mundo KMP:

| IDE                                                                       | Kotlin | Swift | JS/TS | Gratuíto |
|---------------------------------------------------------------------------|--------|-------|-------|----------|
| [Android Studio](https://developer.android.com/studio)                    | ✅      |       |       | ✅        |
| [IntelliJ Ultimate](https://www.jetbrains.com/idea/download/?section=mac) | ✅      |       | ✅     |          |
| [Xcode](https://developer.apple.com/xcode/)                               |        | ✅     |       | ✅        |
| [VSCode](https://code.visualstudio.com/)                                  |        |       | ✅     | ✅        |
| [Fleet (Preview Beta)](https://www.jetbrains.com/fleet/) 🔥               | ✅      | ✅     | ✅     | ✅        |

Vamos elaborar um pouco sobre cada uma dessas opções.

### Android Studio

Android Studio é amplamente apreciado pela comunidade Android, oferecendo excelente suporte para o ecossistema Android. Baseado no *[IntelliJ Community](https://www.jetbrains.com/products/compare/?product=idea&product=idea-ce)* e mantido pela Google, ele é especialmente adaptado para uma integração completa com o Android. Esta opção é popular, gratuita e atualmente recomendada pela JetBrains para desenvolvimento KMP.

O suporte ao KMP no Android Studio requer a instalação manual do plugin [🔗 Kotlin Multiplatform Mobile](https://kotlinlang.org/docs/multiplatform-plugin-releases.html).

### IntelliJ Ultimate

O IntelliJ Ultimate é um ambiente robusto não só para Kotlin, mas também para uma série de frameworks (Spring, React/Native, Angular, Vue.js, Django, etc) e outras linguagens (HTML, Ruby, PHP, Go, SQL, Markdown, etc), com recursos suficientes para preencher um artigo inteiro.

Esta versão é paga, portanto, mais adequada para devs experientes que buscam uma IDE como ferramenta de produtividade completa para escalar projetos.

Contudo, em termos de suporte KMP, o IntelliJ Ultimate não oferece grandes vantagens em comparação ao Android Studio, que provavelmente possui a maioria das funcionalidades e suporte ao KMP disponíveis no IntelliJ Ultimate.

### VSCode

Caso você esteja em um ambiente web/js e planeja adotar o KMP, você vai precisar usar outras ferramentas como VSCode, ou até outras pagas como WebStorm e o próprio IntelliJ Ultimate.

Com as ferramentas atuais, pode ser que você nem precise trabalhar com código JS, já que Kotlin/JS tem excelente suporte no IntelliJ e no Android Studio, permitindo escrever todo o código em Kotlin utilizando [🔗 wrappers](https://github.com/JetBrains/kotlin-wrappers).

### Xcode

Independentemente do uso do IntelliJ ou Android Studio, o Xcode é necessário para navegar por código Swift/Obj-C. O Xcode, a IDE gratuita da Apple (embora requeira um Mac), é essencial no ambiente KMP.

A frequência de uso do Xcode varia conforme o projeto. Por exemplo:

| Frequência de Uso | Contexto                        | Detalhes                                                                                                                                                                   |
|-------------------|---------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Moderado          | Bibliotecas KMP                 | Criação de bibliotecas multiplataforma com código comum e implementações específicas para JVM, web e plataformas nativas, utilizadas como dependências em outros projetos. |
| Mais Frequente    | Repositórios Swift adotando KMP | Em projetos existentes em Swift que estão migrando para KMP, o uso do Xcode é intensivo.                                                                                   |
| Frequente         | Análises de desempenho          | Uso das ferramentas do Xcode para realizar análises de desempenho na aplicação.                                                                                            |
| Frequente         | Aplicações KMP Android e iOS    | Compartilhamento de código entre plataformas móveis para implementar lógica de aplicativos como rede, armazenamento de dados e validação, análises e cálculos.             |
| Variável          | Aplicações Desktop              | Compartilhamento de UIs entre plataformas desktop como Windows, macOS e Linux usando Compose Multiplatform.                                                                |

Devs frequentemente criticam a experiência do Xcode, especialmente em comparação ao IntelliJ ou Android Studio, e o desafio de manter duas IDEs poderosas em execução simultânea.

Como uma alternativa ao Xcode, a JetBrains desenvolveu o [AppCode](https://www.jetbrains.com/objc/), mas enfrentou diversos problemas e não teve aceitação significativa na comunidade KPM. Em dezembro de 2022, a JetBrains anunciou oficialmente a aposentadoria do AppCode.

### Fleet

Lançado em novembro de 2021, Fleet surgiu como uma proposta similar ao VSCode, sendo um editor de texto leve e flexível.

Dois anos depois, em novembro de 2023, [🔗 foi anunciado](https://blog.jetbrains.com/kotlin/2023/11/kotlin-multiplatform-tooling-in-fleet/) o suporte Multiplataforma no Fleet, trazendo uma série de recursos notáveis:

- **Fácil integração e execução do app** Ao abrir um projeto KMP, o Fleet configura automaticamente as execuções para Android, iOS e Desktop, baseado no arquivo de build do projeto.

- **Poliglota:** Fleet permite trabalhar com código nativo em projetos multiplataforma sem mudar de editor, oferecendo suporte aprimorado para edição de código Swift e integração com projetos Xcode.

- **Navegação de código:** Permite navegação cruzada entre Swift e Kotlin, facilitando a localização de declarações e usos de funções entre as linguagens.

- **Refatoração:** Suporta refatorações que afetam tanto módulos Kotlin quanto Swift.

- **Depuração integrada:** Com suporte ferramentas como pontos de interrupção (break-points) tanto em Swift quando em Kotlin, além da visualização de valores e pilhas de chamadas.

Fleet se apresenta como uma ferramenta poderosa para desenvolvimento multiplataforma, unificando diferentes linguagens e plataformas em um ambiente integrado.

#### Fleet em "Beta Preview"

Atualmente, o acesso ao Fleet é gratuito, pois a ferramenta ainda está em fase experimental. Também vale notar que inúmeras ferramentas de análises para aplicações para a família Apple ainda são exclusivas do Xcode.

> [📹 Veja o Fleet em ação: Build Apps for iOS, Android, and Desktop With Compose Multiplatform](https://www.youtube.com/watch?v=IGuVIRZzVTk)

### Afinal, qual escolher?

Se você está começando com o KMP, sugiro a utilização do Fleet que conta com um suporte excelente para iniciantes.

Caso você pretenda adotar o KMP em produção, você irá precisar utilizar as outras IDEs como Android Studio, IntelliJ e Xcode.

| IDE            | Uso recomendado                                               | Característica                                                                                  |
|----------------|---------------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| IntelliJ       | Ideal para projetos complexos e para devs experientes.        | Ampla gama de recursos, suporte a plugins de vários frameworks e linguagens, etc.               |
| Android Studio | Recomendado para desenvolvimento Android e aplicações mobile. | Integração com Android SDK, ferramentas de UI, análise de desempenho para apps em Android, etc. |
| Xcode          | Essencial para desenvolvimento iOS e macOS.                   | Ferramentas nativas Apple, interface intuitiva, análise de desempenho para família Apple, etc.  |
| Fleet          | Boa escolha para iniciantes e projetos mais leves.            | Interface simplificada e pensado para desenvolvimento KMP.                                      | 


## Conclusão

Este artigo ofereceu uma visão abrangente sobre a configuração do ambiente de desenvolvimento com Kotlin Multiplataforma. Discutimos desde a seleção do JDK ideal até a escolha da IDE mais adequada, incluindo o emergente Fleet, que se destaca como uma opção promissora, especialmente para iniciantes. 

A seleção cuidadosa das ferramentas e configurações corretas não apenas simplifica o processo de desenvolvimento, mas também eleva significativamente a eficiência e a produtividade. 

No próximo artigo, abordaremos outro aspecto fundamental do KMP: o Plugin do Gradle, uma ferramenta chave para aproveitar ao máximo o potencial do desenvolvimento multiplataforma.

Até a próxima!

---

> 🤖 Artigo foi escrito com o auxílio do ChatGPT 4, utilizando o plugin Web. 
>
> As fontes e o conteúdo são revisados para garantir a relevância das informações fornecidas, assim como as fontes utilizadas em cada prompt.
> 
> No entanto, caso encontre alguma informação incorreta ou acredite que algum crédito está faltando, por favor, entre em contato!

---

> Referências:
> 
> - [An Overview of JDK Vendors - DZone](https://dzone.com/articles/an-overview-on-jdk-vendors)
> - [Android Developers - Kotlin Multiplatform](https://developer.android.com/kotlin/multiplatform)
> - [Baeldung on Kotlin - Introduction to Multiplatform Programming in Kotlin](https://www.baeldung.com/kotlin/multiplatform-programming)
> - [Bito.ai - Java SDK vs JDK](https://bito.ai/java-sdk-vs-jdk-java-explained/)
> - [Building cross-platform mobile apps with Kotlin Multiplatform - LogRocket Blog](https://blog.logrocket.com/building-cross-platform-mobile-apps-kotlin-multiplatform)
> - [Choosing a configuration for your Kotlin Multiplatform project - KMP Docs](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-project-configuration.html)
> - [Jeff Bruchado - Kotlin Multiplataforma: Crie uma vez, execute em qualquer lugar](https://jeffbruchado.com.br/blog/kotlin-multiplataforma)
> - [Kotlin Documentation: Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
> - [Kotlin Multiplatform Development Documentation - Set up an environment](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-setup.html#install-the-necessary-tools)
> - [Recommended IDEs - KotlinLang](https://www.jetbrains.com/help/kotlin-multiplatform-dev/recommended-ides.html)
> - [Sharing More Logic Between iOS and Android - Kotlin Multiplatform Development Documentation](https://www.jetbrains.com/lp/mobilecrossplatform)

---

