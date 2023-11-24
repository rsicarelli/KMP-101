> * [Introdução ao compilador do Kotlin](#introdução-ao-compilador-do-kotlin)
> * [Entendendo o Frontend do compilador do Kotlin](#entendendo-o-frontend-do-compilador-do-kotlin)
>   * [K1: codinome FE10 (Frontend 1.0)](#k1-codinome-fe10-frontend-10)
>   * [K2: codinome FIR (Frontend Intermediate Representation)](#k2-codinome-fir-frontend-intermediate-representation)
> * [Entendendo o Backend do compilador do Kotlin](#entendendo-o-backend-do-compilador-do-kotlin)
> * [Representação Intermediária ou Intermediary Representation (IR)](#representação-intermediária-ou-intermediary-representation-ir)

No último artigo (🔗 [KMP 101: Entendendo como o Kotlin compila para multiplas plataformas](https://dev.to/rsicarelli/kotlin-multiplataforma-101-entendendo-como-o-kotlin-compila-para-multiplas-plataformas-5hba)), aprendemos sobre o frontend, IR e backend do compilador do Kotlin.

Dessa vez, vamos entender um conceito chave para codar em KMP: os `source sets` 

---

## Introdução aos `source sets` do KMP

Os *source sets* no Kotlin são essenciais para o desenvolvimento multiplataforma. Utilizando uma arquitetura hierárquica, os source sets nos permite organizar nosso código-fonte, declarar depêndencias específicas para cada alvo, e também de nos permite configurar opções de compilação de forma isolada para diferentes plataformas em um mesmo projeto.

Pense em um *source set* no KMP como uma "pasta especial" em um projeto, onde cada pasta têm um propósito (ou plataforma) específica. Por exemplo, a pasta "comum" contém arquivos usados em todas as plataformas, enquanto pastas específicas, como "android" ou "iOS", abrigam arquivos exclusivos para essas plataformas.

O compilador do Kotlin identifica essas pastas especiais e se encarrega de compilar seu conteúdo (código-fonte), conforme as estratégias de compilação exploradas em 🔗 [KMP 101: Entendendo como o Kotlin compila para multiplas plataformas](https://dev.to/rsicarelli/kotlin-multiplataforma-101-entendendo-como-o-kotlin-compila-para-multiplas-plataformas-5hba).

### Estrutura básica de um source set

Cada *source set* em um projeto multiplataforma possui **um nome único** e contém um conjunto de arquivos de código-fonte e recursos (arquivos, ícones, etc). Ele especifica **um alvo** (target) para o qual o código será compilado.

Assumindo que as configurações necessárias foram aplicadas (iremos abordada-las em artigos futuros), a estrutura de pastas abaixo orienta o compilador do Kotlin a:

1. Inicializar e compilar os seguintes alvos: `android`, `iOS`, `watchOS`, `tvOS`, `js`, `wasm` e `desktop`.
2. Compilar o código-fonte dentro do source set `common` para todas as plataformas, tornando os membros do arquivo `Common.kt` disponíveis nativamente para cada plataforma definida.
3. Ao final da compilação, gerar arquivos específicos para cada plataforma (`.class`, `.so`, `.js`, `.wasm`), com todos os membros do `Common.kt` disponíveis.

![Desenvolvimento nativo](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp101-sourcesets-basic.png?raw=true)

### A natureza hierárquica dos source sets
Os source sets do KMP funcionam como uma árvore genealógica. 

Na base da árvore, temos os ancestrais comuns (o source set `commonMain`), cujas características são compartilhadas por todos na família. À medida que avançamos para os galhos, encontramos os source sets intermediários, que representam ramos da família com características únicas compartilhadas por um subconjunto de membros (por exemplo, `apple` ou `native`). 

Finalmente, nas extremidades dos galhos, estão os membros individuais da família (os source sets específicos da plataforma, como `iosArm64` ou `iosSimulatorArm64`), cada um com suas próprias características únicas.

Isso permite organizar uma hierarquia de *source sets* intermediários com total controle do que cada source set irá compartilhar.

![Source sets intermediários KMP](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/intermediate-source-sets-diagram.png?raw=true)

#### Hierarquia padrão

A partir do Kotlin `1.9.20`, o plugin Gradle do KMP oferece **um modelo de hierarquia padrão**, que contém *source sets* intermediários predefinidos para casos de uso comuns. Esse modelo é automaticamente configurado com base nos alvos especificados no projeto. 

Um exemplo interessante é que os *source sets* `apple` e `native` compilam apenas para os alvos `iosArm64` e `iosSimulatorArm64`, mas têm acesso à API completa do iOS. Essa abordagem hierárquica oferece flexibilidade e controle detalhado sobre como o código é compartilhado e utilizado entre diferentes plataformas e alvos.

![Hierarquia padrão do KMP](https://kotlinlang.org/docs/images/default-hierarchy-example.svg)

### Exemplo: compartilhando uma interface entre source sets

Vamos supor que temos um projeto KMP com os source sets `commonMain`, `androidMain` e `appleMain`. Dentro do source set comúm, temos uma interface definida chamada `InterfaceComum` que funciona como um contrato que todas as plataformas precisam aderir.  

Derivando da `InterfaceComum`, temos `InterfaceApple` e `InterfaceAndroid`: a `InterfaceApple` adiciona funcionalidades específicas para o ecossistema Apple, enquanto `InterfaceAndroid` faz o mesmo para dispositivos Android. Esse design garante que, embora compartilhemos a lógica comum pela `InterfaceComum`, cada plataforma pode ter suas próprias extensões e funcionalidades, mantendo a separação e a especialização do código conforme necessário.

![Mermaid](mermaid-diagram-2023-11-24-110205)

### Utilizando da hierarquia para compartilhar código
Como você pode perceber, Essa natureza hierarquica


#### Reutilização de Código Entre Plataformas
O Kotlin Multiplatform permite a reutilização eficiente do código entre *source sets* específicos de plataformas. Isso significa que a lógica de negócios reutilizável e partes da interface do usuário podem ser desenvolvidas uma única vez e usadas em diferentes plataformas, economizando tempo e esforços de desenvolvimento&#8203;``【oaicite:7】``&#8203;.

### Desenvolvimento de APIs Específicas de Plataforma
Utilizando o mecanismo de declarações *expected* e *actual*, o Kotlin Multiplatform oferece uma maneira eficaz de definir e acessar APIs específicas da plataforma no código comum. Isso permite que tarefas comuns sejam especializadas e otimizadas para cada plataforma, aumentando a eficiência e a eficácia do código&#8203;``【oaicite:6】``&#8203;.

### Suporte de Ferramentas e Integração com o Gradle
O IntelliJ IDEA, da JetBrains, oferece suporte integrado para a programação multiplataforma em Kotlin. Além disso, o plugin `kotlin-multiplatform` do Gradle ajuda a configurar projetos multiplataforma, facilitando a gestão de *source sets* e dependências&#8203;``【oaicite:5】``&#8203;.

### Mudanças Recentes e Práticas Recomendadas
Com as atualizações constantes do Kotlin Multiplatform, é vital estar ciente das mudanças recentes, como a nova abordagem para alvos auto-gerados pelo Gradle e alterações nos nomes das configurações de compilação. Isso garante que os projetos estejam em conformidade com as práticas atuais e evita problemas de compatibilidade&#8203;``【oaicite:4】``&#8203;&#8203;``【oaicite:3】``&#8203;&#8203;``【oaicite:2】``&#8203;.

### Suporte para Estrutura Hierárquica e API Obsoleta
O Kotlin tem introduzido suporte para estruturas de projeto hierárquicas, permitindo criar *source sets* intermediários entre o `commonMain` e os específicos da plataforma. É importante estar atento às APIs obsoletas e às propriedades do Gradle que estão sendo gradualmente descontinuadas, para garantir a estabilidade e a modernidade do projeto&#8203;``【oaicite:1】``&#8203;&#8203;``【oaicite:0】``&#8203;.


### Convencões
O KMP é extremamente flexível, nos possibilitando nomear nossos source sets com praticamente qualquer nome.

Porém, no decorrer dos anos, a comunidade foi adotando algumas convenções, e o próprio KMP foi se adequando ao redor dessas convenções também, oferecendo algumas facilidades na configuração do projeto. Vamos explorar as principais delas 

#### 1: Nomes utilizando "camelCase"
A comunidade geralmente adota a nomenclatura `cammelCase` para a definição dos source sets. 

#### 2: Sufixo "main"
O diretório `main` em projetos que utilizam linguagens da JVM, como Java e Kotlin, é tradicionalmente usado para armazenar o código-fonte principal da aplicação. Este diretório é parte de uma estrutura de pastas convencional, onde `main` geralmente contém os pacotes e classes que implementam a lógica principal do programa. 

Em projetos KMP, essa tradição foi levada adiante e se utiliza o `main` como sufixo para declarar nossos source sets: `commonMain`, `androidMain`, `nativeMain`, `desktopMain`, etc.

#### 3: Código compartilhado usando o `commonMain`
Esse source set é o "topo" da hierarquia






## Source Sets Específicos de Plataforma
- O Kotlin cria *source sets* específicos da plataforma, conhecidos como *platform source sets*. Cada alvo (target) tem um *source set* correspondente que compila apenas para esse alvo. Por exemplo, um alvo `jvm` terá o *source set* correspondente `jvmMain`&#8203;``【oaicite:7】``&#8203;.
- Durante a compilação para um alvo específico, o Kotlin coleta todos os *source sets* rotulados com esse alvo e produz binários a partir deles. Por exemplo, para o alvo JVM, ele seleciona `jvmMain` e `commonMain` e compila ambos juntos para os arquivos de classe JVM&#8203;``【oaicite:6】``&#8203;.

## Intermediate Source Sets
- Em projetos mais complexos, pode ser necessário um compartilhamento de código mais granular. Para isso, o Kotlin suporta *intermediate source sets*, que compilam para alguns, mas não todos, os alvos no projeto. Isso é útil para compartilhar código entre um subconjunto de alvos&#8203;``【oaicite:5】``&#8203;.

## Integração com Testes
- Todos os *source sets* criados por padrão têm os prefixos `Main` e `Test`. O `Main` contém código de produção, enquanto o `Test` contém testes para esse código. Por exemplo, `commonTest` é um *source set* de teste para `commonMain` e compila para todos os alvos declarados&#8203;``【oaicite:4】``&#8203;.

## Reutilização de Código entre Plataformas
- O Kotlin multiplataforma organiza o código-fonte em hierarquias, facilitando a reutilização de código entre *source sets*. Todos os *source sets* específicos de plataforma dependem do *source set* comum por padrão&#8203;``【oaicite:3】``&#8203;.

## Desenvolvimento de APIs Específicas de Plataforma
- Em alguns casos, é desejável definir e acessar APIs específicas da plataforma no código comum. Para isso, o Kotlin oferece o mecanismo de declarações *expected* e *actual*. Uma função pode ser declarada como *expected* no *source set* comum, e os *source sets* específicos da plataforma devem fornecer uma implementação *actual* correspondente&#8203;``【oaicite:2】``&#8203;.

## Suporte de Ferramentas
- O IntelliJ IDEA, da JetBrains, oferece suporte integrado para programação multiplataforma, incluindo vários modelos de projeto para criar projetos multiplataforma em Kotlin. Além disso, o plugin `kotlin-multiplatform` do Gradle é aplicado automaticamente, configurando o projeto para funcionar em várias plataformas&#8203;``【oaicite:1】``&#8203;.

## Configurações do Gradle
- Cada alvo pode ter uma ou mais compilações. Os projetos Kotlin multiplataforma usam compilações para produzir artefatos, e as compilações padrão incluem `main` e `test` para alvos JVM, JS e Nativo&#8203;``【oaicite:0】``&#8203;.

## Conclusão
Os *source sets* no Kotlin são uma ferramenta poderosa para o desenvolvimento multiplataforma, permitindo a separação e reutilização eficiente do código entre diferentes plataformas e contextos dentro de um projeto. Eles são essenciais para a organização e a modularidade em projetos Kotlin multiplataforma.


## Feedbacks

🔗 [Nova issue no repositório KMP-101](https://github.com/rsicarelli/KMP101/issues/new/choose)

Sua opinião e contribuição fazem desse conteúdo uma fonte de aprendizado mais completo para todo mundo!

Qualquer dúvida, crítica ou sugestão podem ser feitas no repositório [KMP-101](https://github.com/rsicarelli/KMP101)


> Referencias
