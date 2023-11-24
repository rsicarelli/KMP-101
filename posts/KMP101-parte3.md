<!-- TOC -->
  * [Introdução aos *source sets* no KMP](#introdução-aos-source-sets-no-kmp)
  * [Entendendo a função e a estrutura básica de um *source set*](#entendendo-a-função-e-a-estrutura-básica-de-um-source-set)
    * [A natureza hierárquica dos *source sets*](#a-natureza-hierárquica-dos-source-sets)
  * [*Source sets* comuns vs. específicos](#source-sets-comuns-vs-específicos)
    * [*Source set* comum (`commonMain`)](#source-set-comum-commonmain)
    * [*Source sets* específicos de plataforma](#source-sets-específicos-de-plataforma)
    * [Escolhendo entre comum e específico](#escolhendo-entre-comum-e-específico)
  * [*Source set* intermediário](#source-set-intermediário)
  * [*Source sets* de teste](#source-sets-de-teste)
  * [Convenções adotadas pela comunidade](#convenções-adotadas-pela-comunidade)
    * [1: Nomes utilizando "camelCase"](#1-nomes-utilizando-camelcase)
    * [2: Sufixo "main"](#2-sufixo-main)
    * [3: Código compartilhado usando o `commonMain`](#3-código-compartilhado-usando-o-commonmain)
    * [4: Utilizando os "Source set conventions"](#4-utilizando-os-source-set-conventions)
  * [Feedbacks](#feedbacks)
<!-- TOC -->

No último artigo (🔗 [KMP 101: Entendendo como o Kotlin compila para multiplas plataformas](https://dev.to/rsicarelli/kotlin-multiplataforma-101-entendendo-como-o-kotlin-compila-para-multiplas-plataformas-5hba)), aprendemos sobre o frontend, IR e backend do compilador do Kotlin.

Dessa vez, vamos entender um conceito chave para codar em KMP: os `*source sets*`

---

## Introdução aos *source sets* no KMP

Os *source sets* no Kotlin são essenciais para o desenvolvimento multiplataforma. Utilizando uma arquitetura hierárquica, os *source sets* nos permite organizar nosso código-fonte, declarar depêndencias específicas para cada alvo, e também de nos permite configurar opções de compilação de forma isolada para diferentes plataformas em um mesmo projeto.

Pense em um *source set* no KMP como uma "pasta especial" em um projeto, onde cada pasta têm um propósito (ou plataforma) específica. Por exemplo, a pasta "comum" contém arquivos usados em todas as plataformas, enquanto pastas específicas, como "android" ou "iOS", abrigam arquivos exclusivos para essas plataformas.

O compilador do Kotlin identifica essas pastas especiais e se encarrega de compilar seu conteúdo (código-fonte), conforme as estratégias de compilação exploradas em 🔗 [KMP 101: Entendendo como o Kotlin compila para multiplas plataformas](https://dev.to/rsicarelli/kotlin-multiplataforma-101-entendendo-como-o-kotlin-compila-para-multiplas-plataformas-5hba).

## Entendendo a função e a estrutura básica de um *source set*

Cada *source set* em um projeto multiplataforma possui **um nome único** e contém um conjunto de arquivos de código-fonte e recursos (arquivos, ícones, etc). Ele especifica **um alvo** (*target*) para o qual o código será compilado.

Assumindo que as configurações necessárias foram aplicadas (iremos abordada-las em artigos futuros), a estrutura de pastas abaixo orienta o compilador do Kotlin a:

1. Inicializar e compilar os seguintes alvos: `android`, `iOS`, `watchOS`, `tvOS`, `js`, `wasm` e `desktop`.
2. Compilar o código-fonte dentro do *source set* `common` para todas as plataformas, tornando os membros do arquivo `Common.kt` disponíveis nativamente para cada plataforma definida.
3. Ao final da compilação, gerar arquivos específicos para cada plataforma (`.class`, `.so`, `.js`, `.wasm`), com todos os membros do `Common.kt` disponíveis.

![Estrutura basica source set](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp101-sourcesets-basic.png?raw=true)

### A natureza hierárquica dos *source sets*

Os *source sets* do KMP funcionam como uma árvore genealógica.

Na base da árvore, temos os ancestrais comuns (o *source set* `commonMain`), cujas características são compartilhadas por todos na família. À medida que avançamos para os galhos, encontramos os *source sets* intermediários, que representam ramos da família com características únicas compartilhadas por um subconjunto de membros (por exemplo, `apple` ou `native`).

Finalmente, nas extremidades dos galhos, estão os membros individuais da família (os *source sets* específicos da plataforma, como `iosArm64` ou `iosSimulatorArm64`), cada um com suas próprias características únicas.

Isso permite organizar uma hierarquia de *source sets* intermediários com total controle do que cada *source set* irá compartilhar.

![Hierarquia padrão do KMP](https://kotlinlang.org/docs/images/default-hierarchy-example.svg)

## *Source sets* comuns vs. específicos

No KMP, a distinção entre *source sets* comuns e específicos de plataforma é fundamental para entender como o código é compartilhado e gerenciado entre diferentes alvos.

### *Source set* comum (`commonMain`)

O *source set* comum, geralmente localizados no diretório `commonMain`, representa a base do compartilhamento de código no Kotlin Multiplataforma. Aqui, você escreve o código Kotlin que é compartilhado entre todas as plataformas-alvo do projeto. Este código pode incluir lógica de negócios, modelos de dados, e funcionalidades agnósticas em relação à plataforma subjacente.

É importante notar que, embora este código seja compartilhado, ele não deve conter nenhuma funcionalidade ou chamada de API que seja específica a uma plataforma. O compilador Kotlin assegura isso, evitando o uso de funções ou classes específicas de plataforma no código comum, uma vez que esse código é compilado para diferentes alvos.

### *Source sets* específicos de plataforma

Enquanto o código comum oferece uma grande vantagem na reutilização de código, nem tudo pode ser generalizado para todas as plataformas. É aqui que entram os *source sets* específicos de plataforma, como `androidMain`, `iosMain`, `desktopMain`, entre outros. Esses *source sets* contêm código específico para uma plataforma, sendo compilados apenas para seu respectivo alvo.

Por exemplo, o *source set* `androidMain` pode conter chamadas de API Android, enquanto `iosMain` pode utilizar APIs específicas do iOS. Isso permite que você tire proveito das características e APIs únicas de cada plataforma, mantendo simultaneamente, uma base de código comum significativa no `commonMain`.

### Escolhendo entre comum e específico

Ao desenvolver um projeto Kotlin Multiplataforma, uma parte significativa do seu esforço será dedicada a decidir o que vai ao código comum e o que precisa ser implementado de forma específica para cada plataforma. A regra geral é maximizar o código comum, recorrendo a *source sets* específicos de plataforma apenas quando for necessário acessar funcionalidades ou APIs que não estão disponíveis de forma genérica.

Essa abordagem não só simplifica a manutenção do código, como também assegura a consistência em todas as plataformas, aproveitando ao máximo o potencial do Kotlin Multiplataforma.

## *Source set* intermediário

Vamos supor que temos um projeto KMP com os *source sets* `commonMain`, `androidMain` e `appleMain`. Dentro do *source set* comúm, temos uma interface definida chamada `InterfaceComum` que funciona como um contrato que todas as plataformas precisam aderir.

Derivando da `InterfaceComum`, temos `InterfaceApple` e `InterfaceAndroid`: a `InterfaceApple` adiciona funcionalidades específicas para o ecossistema Apple, enquanto `InterfaceAndroid` faz o mesmo para dispositivos Android.

Esse design garante que, embora compartilhemos a lógica comum pela `InterfaceComum`, cada plataforma pode ter suas próprias extensões e funcionalidades, mantendo a separação e a especialização do código conforme necessário.

Esse conceito é chamado de [intermediary *source sets*](https://kotlinlang.org/docs/multiplatform-discover-project.html#intermediate-source-sets):

> Um *source set* intermediário é um conjunto de *source set* que compila para alguns, mas não para todos os alvos do projeto.

![Exemplo *source sets*](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/mermaid-diagram-2023-11-24-110205.png?raw=true)

## *Source set* de teste

Testes no Kotlin multiplataforma também é tratado como um *source set*. O que significa que cada plataforma pode ter seus próprios testes específicos se utilizando, por exemplo, o SDK nativo ou outras bibliotecas open source nativas.

O *source set* comum também pode (e deve!) ter seus próprios testes, porém você irá precisar utilizar outras bibliotecas KMP para a escrita multiplataforma, como, por exemplo, o [🔗 kotlin.test](https://kotlinlang.org/api/latest/kotlin.test/), [🔗 turbine](https://github.com/cashapp/turbine) ou [🔗 assertk](https://github.com/willowtreeapps/assertk).

![Exemplo *source sets*](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/test-source-set-kmp.png?raw=true)

## Gerenciando dependências nos *source sets*

Em projetos Kotlin Multiplataforma, a gestão eficiente de dependências nos source sets é crucial para manter a modularidade e a eficiência do código. 

O KMP nos permite ter controle individual das dependências de cada *source set*, nos possibilitando ainda criar relações/dependências entre elas.

### Dependências no *source set* comum

No source set comum (`commonMain`), as dependências incluem bibliotecas utilizáveis em todas as plataformas suportadas pelo projeto. Estas bibliotecas fornecem funcionalidades que são independentes de qualquer plataforma específica, como lógica de negócios, algoritmos ou utilitários comuns. A inclusão de uma biblioteca no source set comum significa que essa funcionalidade estará disponível para todos os alvos do projeto, promovendo a reutilização do código e a consistência entre plataformas.

Isso significa que, ao declarar uma depêndencia comum, todos os outros *source sets* também terão essa dependencia, que, por sua vez, é uma dependência KMP que oferece funcionalidades agnósticas de plataforma.

### Dependências em *source sets* específicos

Contrastando com o *source set* comum, os *source sets* específicos de plataforma, como `androidMain` ou `iosMain`, focam em dependências que são relevantes apenas para uma plataforma particular. Essas dependências são utilizadas para acessar APIs, bibliotecas ou recursos que são exclusivos a uma plataforma, permitindo que os desenvolvedores aproveitem as funcionalidades nativas e otimizem a experiência do usuário em cada plataforma.

![Exemplo *source sets*](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/mermaid-diagram-2023-11-24-125307.png?raw=true)

## Convenções adotadas pela comunidade

O KMP é extremamente flexível, nos possibilitando nomear e manipular nossos *source sets* como preferirmos.

Porém, no decorrer dos anos, a comunidade foi adotando algumas convenções, e o próprio KMP foi se adequando ao redor dessas convenções, oferecendo algumas facilidades na configuração do projeto. Vamos explorar as principais delas

### 1: Nomes utilizando "camelCase"

A comunidade geralmente adota a nomenclatura `cammelCase` para a definição dos *source sets*.

### 2: Sufixo "main"

O diretório `main` em projetos que utilizam linguagens da JVM, como Java e Kotlin, é tradicionalmente usado para armazenar o código-fonte principal da aplicação. Este diretório é parte de uma estrutura de pastas convencional, onde `main` geralmente contém os pacotes e classes que implementam a lógica principal do programa.

Em projetos KMP, essa tradição foi levada adiante e se utiliza o `main` como sufixo para declarar nossos *source sets*: `commonMain`, `androidMain`, `nativeMain`, `desktopMain`, etc.

### 3: Código compartilhado usando o `commonMain`

O código compartilhado geralmente reside em um *source set* chamado `commonMain`. Não é comum, mas alguns projetos também adotam a nomenclatura `sharedMain`.

### 4: Utilizando os "Source set conventions"

Como aprendemos, o próprio KMP foi se ajustando ao redor dessas definições da comunidade.

A partir do Kotlin `1.9.20`, o plugin Gradle do KMP oferece **um modelo de hierarquia padrão**, que contém *source sets* intermediários predefinidos para casos de uso comuns. Esse modelo é automaticamente configurado com base nos alvos especificados no projeto.

Dentro do KPM Gradle Plugin, temos uma classe chamada [🔗 KotlinMultiplatformSourceSetConventions](https://github.com/JetBrains/kotlin/blob/master/libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/dsl/KotlinMultiplatformSourceSetConventions.kt) que reduz e muito a tarefa tediosa de definir e controlar os *source sets*:

| Source Set               | Plataforma   |
|--------------------------|--------------|
| `androidMain`            | Android      |
| `androidNativeMain`      | Android      |
| `androidNativeTest`      | Android      |
| `appleMain`              | Apple        |
| `appleTest`              | Apple        |
| `commonMain`             | Comum        |
| `commonTest`             | Comum        |
| `iosMain`                | iOS          |
| `iosTest`                | iOS          |
| `jsMain`                 | JavaScript   |
| `jsTest`                 | JavaScript   |
| `jvmMain`                | JVM          |
| `jvmTest`                | JVM          |
| `linuxMain`              | Linux        |
| `linuxTest`              | Linux        |
| `macosMain`              | macOS        |
| `macosTest`              | macOS        |
| `mingwMain`              | Windows      |
| `mingwTest`              | Windows      |
| `nativeMain`             | Nativo       |
| `nativeTest`             | Nativo       |
| `tvosMain`               | tvOS         |
| `tvosTest`               | tvOS         |
| `wasmJsMain`             | WebAssembly  |
| `wasmJsTest`             | WebAssembly  |
| `wasmWasiMain`           | WebAssembly  |
| `wasmWasiTest`           | WebAssembly  |
| `watchosMain`            | watchOS      |
| `watchosTest`            | watchOS      |

---

## Feedbacks

🔗 [Nova issue no repositório KMP-101](https://github.com/rsicarelli/KMP101/issues/new/choose)

Sua opinião e contribuição fazem desse conteúdo uma fonte de aprendizado mais completo para todo mundo!

Qualquer dúvida, crítica ou sugestão podem ser feitas no repositório [KMP-101](https://github.com/rsicarelli/KMP101)


> Referencias
> - [Hierarchical project structure | Kotlin Documentation](https://kotlinlang.org/docs/multiplatform-hierarchy.html)
> - [The basics of Kotlin Multiplatform project structure | Kotlin Documentation](https://kotlinlang.org/docs/multiplatform-basic-project-structure.html)
> - [Create your multiplatform project | Kotlin Multiplatform Development Documentation](https://www.jetbrains.com/lp/mobile-multiplatform/)
> - [Adding dependencies on multiplatform libraries | Kotlin Documentation](https://kotlinlang.org/docs/mpp-add-dependencies.html)
> - [Use platform-specific APIs | Kotlin Multiplatform Development Documentation](https://www.jetbrains.com/lp/mobile-multiplatform/platform-specific-apis/)
