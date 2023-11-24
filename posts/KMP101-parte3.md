> * [Introdução aos `*source sets*` do KMP](#introdução-aos-source-sets-do-kmp)
> * [Estrutura básica de um *source set*](#estrutura-básica-de-um-source-set)
>  * [A natureza hierárquica dos *source sets*](#a-natureza-hierárquica-dos-source-sets)
>  * [Source set intermediário](#source-set-intermediário)
>  * [Source sets de teste](#source-sets-de-teste)
> * [Convenções adotadas pela comunidade](#convenções-adotadas-pela-comunidade)
>  * [1: Nomes utilizando "camelCase"](#1-nomes-utilizando-camelcase)
>  * [2: Sufixo "main"](#2-sufixo-main)
>  * [3: Código compartilhado usando o `commonMain`](#3-código-compartilhado-usando-o-commonmain)
>  * [4: Utilizando os "Source set conventions"](#4-utilizando-os-source-set-conventions)
> * [Feedbacks](#feedbacks)

No último artigo (🔗 [KMP 101: Entendendo como o Kotlin compila para multiplas plataformas](https://dev.to/rsicarelli/kotlin-multiplataforma-101-entendendo-como-o-kotlin-compila-para-multiplas-plataformas-5hba)), aprendemos sobre o frontend, IR e backend do compilador do Kotlin.

Dessa vez, vamos entender um conceito chave para codar em KMP: os `*source sets*`

---

## Introdução aos **source sets** no KMP

Os *source sets* no Kotlin são essenciais para o desenvolvimento multiplataforma. Utilizando uma arquitetura hierárquica, os *source sets* nos permite organizar nosso código-fonte, declarar depêndencias específicas para cada alvo, e também de nos permite configurar opções de compilação de forma isolada para diferentes plataformas em um mesmo projeto.

Pense em um *source set* no KMP como uma "pasta especial" em um projeto, onde cada pasta têm um propósito (ou plataforma) específica. Por exemplo, a pasta "comum" contém arquivos usados em todas as plataformas, enquanto pastas específicas, como "android" ou "iOS", abrigam arquivos exclusivos para essas plataformas.

O compilador do Kotlin identifica essas pastas especiais e se encarrega de compilar seu conteúdo (código-fonte), conforme as estratégias de compilação exploradas em 🔗 [KMP 101: Entendendo como o Kotlin compila para multiplas plataformas](https://dev.to/rsicarelli/kotlin-multiplataforma-101-entendendo-como-o-kotlin-compila-para-multiplas-plataformas-5hba).

## Entendendo a função e a estrutura básica de um *source set*

Cada *source set* em um projeto multiplataforma possui **um nome único** e contém um conjunto de arquivos de código-fonte e recursos (arquivos, ícones, etc). Ele especifica **um alvo** (target) para o qual o código será compilado.

Assumindo que as configurações necessárias foram aplicadas (iremos abordada-las em artigos futuros), a estrutura de pastas abaixo orienta o compilador do Kotlin a:

1. Inicializar e compilar os seguintes alvos: `android`, `iOS`, `watchOS`, `tvOS`, `js`, `wasm` e `desktop`.
2. Compilar o código-fonte dentro do *source set* `common` para todas as plataformas, tornando os membros do arquivo `Common.kt` disponíveis nativamente para cada plataforma definida.
3. Ao final da compilação, gerar arquivos específicos para cada plataforma (`.class`, `.so`, `.js`, `.wasm`), com todos os membros do `Common.kt` disponíveis.

![Desenvolvimento nativo](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp101-sourcesets-basic.png?raw=true)

### A natureza hierárquica dos *source sets*

Os *source sets* do KMP funcionam como uma árvore genealógica.

Na base da árvore, temos os ancestrais comuns (o *source set* `commonMain`), cujas características são compartilhadas por todos na família. À medida que avançamos para os galhos, encontramos os *source sets* intermediários, que representam ramos da família com características únicas compartilhadas por um subconjunto de membros (por exemplo, `apple` ou `native`).

Finalmente, nas extremidades dos galhos, estão os membros individuais da família (os *source sets* específicos da plataforma, como `iosArm64` ou `iosSimulatorArm64`), cada um com suas próprias características únicas.

Isso permite organizar uma hierarquia de *source sets* intermediários com total controle do que cada *source set* irá compartilhar.

![Source sets intermediários KMP](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/intermediate-source-sets-diagram.png?raw=true)

#### Melhores práticas para gerenciar os *source sets*

A partir do Kotlin `1.9.20`, o plugin Gradle do KMP oferece **um modelo de hierarquia padrão**, que contém *source sets* intermediários predefinidos para casos de uso comuns. Esse modelo é automaticamente configurado com base nos alvos especificados no projeto.

Um exemplo interessante é que os *source sets* `apple` e `native` compilam apenas para os alvos `iosArm64` e `iosSimulatorArm64`, mas têm acesso à API completa do iOS. Essa abordagem hierárquica oferece flexibilidade e controle detalhado sobre como o código é compartilhado e utilizado entre diferentes plataformas e alvos.

![Hierarquia padrão do KMP](https://kotlinlang.org/docs/images/default-hierarchy-example.svg)

### *Source set* intermediário

Vamos supor que temos um projeto KMP com os *source sets* `commonMain`, `androidMain` e `appleMain`. Dentro do *source set* comúm, temos uma interface definida chamada `InterfaceComum` que funciona como um contrato que todas as plataformas precisam aderir.

Derivando da `InterfaceComum`, temos `InterfaceApple` e `InterfaceAndroid`: a `InterfaceApple` adiciona funcionalidades específicas para o ecossistema Apple, enquanto `InterfaceAndroid` faz o mesmo para dispositivos Android.

Esse design garante que, embora compartilhemos a lógica comum pela `InterfaceComum`, cada plataforma pode ter suas próprias extensões e funcionalidades, mantendo a separação e a especialização do código conforme necessário.

Esse conceito é chamado de [intermediary *source sets*](https://kotlinlang.org/docs/multiplatform-discover-project.html#intermediate-source-sets):

> Um *source set* intermediário é um conjunto de *source set* que compila para alguns, mas não para todos os alvos do projeto.

![Exemplo *source sets*](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/mermaid-diagram-2023-11-24-110205.png?raw=true)

### *Source sets* de teste

Testes no Kotlin multiplataforma também é tratado como um *source set*. O que significa que cada plataforma pode ter seus próprios testes específicos se utilizando, por exemplo, o SDK nativo ou outras bibliotecas open source nativas.

O *source set* comum também pode (e deve!) ter seus próprios testes, porém você irá precisar utilizar outras bibliotecas KMP para a escrita multiplataforma, como por exemplo o [🔗 kotlin.test](https://kotlinlang.org/api/latest/kotlin.test/), [🔗 turbine](https://github.com/cashapp/turbine) ou [🔗 assertk](https://github.com/willowtreeapps/assertk).

![Exemplo *source sets*](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/test-source-set-kmp.png?raw=true)

## Convenções adotadas pela comunidade

O KMP é extremamente flexível, nos possibilitando nomear e manipular nossos *source sets* como preferirmos.

Porém, no decorrer dos anos, a comunidade foi adotando algumas convenções, e o próprio KMP foi se adequando ao redor dessas convenções, oferecendo algumas facilidades na configuração do projeto. Vamos explorar as principais delas

#### 1: Nomes utilizando "camelCase"

A comunidade geralmente adota a nomenclatura `cammelCase` para a definição dos *source sets*.

#### 2: Sufixo "main"

O diretório `main` em projetos que utilizam linguagens da JVM, como Java e Kotlin, é tradicionalmente usado para armazenar o código-fonte principal da aplicação. Este diretório é parte de uma estrutura de pastas convencional, onde `main` geralmente contém os pacotes e classes que implementam a lógica principal do programa.

Em projetos KMP, essa tradição foi levada adiante e se utiliza o `main` como sufixo para declarar nossos *source sets*: `commonMain`, `androidMain`, `nativeMain`, `desktopMain`, etc.

#### 3: Código compartilhado usando o `commonMain`

O código compartilhado geralmente reside em um *source set* chamado `commonMain`.

#### 4: Utilizando os "Source set conventions"

Como aprendemos, o próprio KMP foi se ajustando ao redor dessas definições da comunidade. Dentro do KPM Gradle Plugin, temos uma classe chamada [🔗 KotlinMultiplatformSourceSetConventions](https://github.com/JetBrains/kotlin/blob/master/libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/dsl/KotlinMultiplatformSourceSetConventions.kt) que reduz e muito a tarefa tediosa de definir e controlar os *source sets*.

Em Nov 2023, esses são os nomes pre-definidos pelo KMP:

| Source Set          | Plataforma |
|---------------------|------------|
| `androidMain`       | Android    |
| `androidNativeMain` | Android    |
| `androidNativeTest` | Android    |
| `appleMain`         | Apple      |
| `appleTest`         | Apple      |
| `commonMain`        | Comum      |
| `commonTest`        | Comum      |
| `iosMain`           | iOS        |
| `iosTest`           | iOS        |
| `jsMain`            | JavaScript |
| `jsTest`            | JavaScript |
| `jvmMain`           | JVM        |
| `jvmTest`           | JVM        |
| `linuxMain`         | Linux      |
| `linuxTest`         | Linux      |
| `macosMain`         | macOS      |
| `macosTest`         | macOS      |
| `mingwMain`         | Windows    |
| `mingwTest`         | Windows    |
| `nativeMain`        | Nativo     |
| `nativeTest`        | Nativo     |
| `tvosMain`          | tvOS       |
| `tvosTest`          | tvOS       |
| `watchosMain`       | watchOS    |
| `watchosTest`       | watchOS    |

---

## Feedbacks

🔗 [Nova issue no repositório KMP-101](https://github.com/rsicarelli/KMP101/issues/new/choose)

Sua opinião e contribuição fazem desse conteúdo uma fonte de aprendizado mais completo para todo mundo!

Qualquer dúvida, crítica ou sugestão podem ser feitas no repositório [KMP-101](https://github.com/rsicarelli/KMP101)


> Referencias
