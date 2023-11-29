> * [Introdução ao compilador do Kotlin](#introdução-ao-compilador-do-kotlin)
> * [Entendendo o Frontend do compilador do Kotlin](#entendendo-o-frontend-do-compilador-do-kotlin)
>   * [K1: codinome FE10 (Frontend 1.0)](#k1-codinome-fe10-frontend-10)
>   * [K2: codinome FIR (Frontend Intermediate Representation)](#k2-codinome-fir-frontend-intermediate-representation)
> * [Entendendo o Backend do compilador do Kotlin](#entendendo-o-backend-do-compilador-do-kotlin)
> * [Representação Intermediária ou Intermediary Representation (IR)](#representação-intermediária-ou-intermediary-representation-ir)

No último post ([🔗 KMP 101: Introdução ao paradigma da multiplataforma](https://dev.to/rsicarelli/kotlin-multiplataforma-101-introducao-ao-paradigma-da-multiplataforma-eo3)), exploramos o paradigma multiplataforma e como o KMP se destaca no ecossistema.

Neste artigo, vamos desvendar os conceitos básicos do compilador Kotlin e sua capacidade de compilar para múltiplas plataformas.

---

## Introdução ao compilador do Kotlin

Um compilador é um software que converte código de uma linguagem de programação para outra. Frequentemente, os compiladores são utilizados para transformar programas de linguagens de alto nível em linguagens de baixo nível.

O Kotlin, assim como alguns outros compiladores como [LLVM](https://llvm.org/) e [GCC](https://gcc.gnu.org/), possui uma arquitetura dividida em **frontend** e **backend**, comunicando-se por uma **Representação Intermediária (IR)**.

### Entendendo o Frontend do compilador do Kotlin

Responsável por analisar e preparar o código-fonte `.kt` para a compilação, o Kotlin apresenta duas versões de frontends: o **K1** e o **K2**.

#### K1: codinome FE10 (Frontend 1.0)

O frontend K1, também conhecido como FE10, é o frontend original do compilador Kotlin e é o padrão utilizado na atualidade.

Principais características:

- **Análise Léxica (Lexer):** divide o código-fonte Kotlin em tokens, elementos fundamentais para a construção da linguagem.
- **Análise Sintática (Parser):** organiza os tokens em uma estrutura sintática, geralmente uma árvore de análise sintática (AST), que representa a estrutura lógica do código.
- **Árvores PSI/AST:** utiliza a `Abstract Syntax Tree` (AST) e a `Program Structure Interface` (PSI) para representar e manipular a estrutura do código, essencial para análises subsequentes.
- **Análise Semântica:** verifica a corretude do uso dos elementos da linguagem, como tipos e escopos, garantindo que o código segue as regras semânticas do Kotlin.

![Frontend K1](https://github.com/ahinchman1/Kotlin-Compiler-Crash-Course/blob/master/res/k1_frontend.png?raw=true)

#### K2: codinome FIR (Frontend Intermediate Representation)

O K2, também conhecido como `FIR`, é a próxima grande atualização do compilador do Kotlin e promete ser o substituto do K1/FE10.

A primeira versão beta do K2 chegou com o Kotlin `1.9.20`, lançada em novembro de 2023, e a versão final está planejada para o Kotlin 2.0.0, que esperamos para 2024. Esse novo sistema traz várias melhorias importantes, como mais velocidade, uma estrutura mais organizada e uma maneira mais clara de entender o código.

![KotlinConf2023 K1 vs K2](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/k1-vs-k2-kotlinconf2023.png?raw=true)

> Dados do [KotlinConf’23 - Keynote](https://www.youtube.com/live/c4f4SCEYA5Q?si=LyH_q_6R8hjd-dRo&t=495)

Listando algumas dessas melhorias:

- **Totalmente renovado:** o K2 foi feito do zero, pensando em ser rápido e fácil de atualizar no futuro.
- **Melhor análise de código:** possui um método mais avançado para verificar o código, ajudando a identificar e usar informações importantes de maneira mais inteligente.
- **Suporte a plugins:** inclui suporte a uma variedade de plugins, como `kapt`, `serialization`, `all-open`, e outros.
- **Compatibilidade entre plataformas:** suporta `JVM`, `Native`, `Wasm`, e `JS`, otimizado para projetos multiplataforma.

![Frontend K2 FIR](https://github.com/ahinchman1/Kotlin-Compiler-Crash-Course/blob/master/res/k2_frontend.png?raw=true)

### Entendendo o Backend do compilador do Kotlin

Após o processamento e preparação do código-fonte pelo frontend de um compilador, o backend assume um papel crucial.

O backend é responsável por converter a representação intermediária (IR) em código de máquina, realizando otimizações e gerando a saída específica para a plataforma alvo (como `*.class`, `*.js`, `*.so`, `*.wasm`).

Projetado para ser multiplataforma, o Kotlin pode ser compilado para funcionar em diversos dispositivos e sistemas operacionais. Cada backend do compilador Kotlin é especialmente otimizado para uma plataforma-alvo, possibilitando que devs escrevam um código que pode ser executado em variados ambientes.

- **Kotlin/JVM:** este backend é o mais tradicional e gera bytecode compatível com a Máquina Virtual Java (`JVM`). É ideal para aplicações que serão executadas em ambientes que suportam a JVM, incluindo Android, Desktop e aplicações de servidor.
- **Kotlin/Native:** utilizando a toolchain do `LLVM`, este backend compila o código Kotlin diretamente para código de máquina nativo. Ele suporta uma ampla gama de plataformas, como iOS, macOS, Windows, Linux e sistemas embarcados, permitindo que aplicações sejam executadas diretamente no hardware.
- **Kotlin/JS:** especializado para o desenvolvimento web, este backend converte o código Kotlin em JavaScript, tornando-o compatível com navegadores web e ambientes de servidor baseados em JavaScript, como Node.js.
- **Kotlin/Wasm:** uma adição mais recente ainda em fase de desenvolvimento, este backend permite a compilação de Kotlin para WebAssembly (Wasm), facilitando a execução de aplicações Kotlin com alto desempenho em navegadores web.

![Desenvolvimento nativo](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kotlin-compiler-backend.jpg?raw=true)

## Representação Intermediária ou Intermediary Representation (IR)

O IR é uma forma de representar o código-fonte dentro do compilador que é independente tanto da linguagem de programação de origem quanto da arquitetura da máquina de destino. Ela serve como um meio-termo entre o código de alto nível e o código de máquina de baixo nível.

Essa estrutura de dados permite que o compilador Kotlin manipule o código de maneira mais abstrata, facilitando a geração de código para múltiplas plataformas. Isso é particularmente benéfico para Kotlin, projetado para ser multiplataforma.

## Conclusões

Entender como o Kotlin compila para diferentes plataformas não é nada que você precise fazer diariamente ou memorizar. No entanto, ter uma visão geral desse processo tem suas vantagens.

Esse entendimento fornece uma perspectiva sobre a versatilidade e a eficiência do Kotlin, oferecendo a você a confiança de que seu código pode operar em vários ecossistemas. Além disso, uma apreciação básica do que acontece "por debaixo dos panos" pode ser incrivelmente útil ao depurar o código e compreender mensagens de erro, economizando horas de frustração.

---

## Feedbacks

🔗 [Nova issue no repositório KMP-101](https://github.com/rsicarelli/KMP101/issues/new/choose)

Sua opinião e contribuição fazem desse conteúdo uma fonte de aprendizado mais completo para todo mundo!

Qualquer dúvida, crítica ou sugestão podem ser feitas no repositório [KMP-101](https://github.com/rsicarelli/KMP101)

---

> 🤖 Artigo foi escrito com o auxílio do ChatGPT 4, utilizando o plugin Web.
>
> As fontes e o conteúdo são revisados para garantir a relevância das informações fornecidas, assim como as fontes utilizadas em cada prompt.
>
> No entanto, caso encontre alguma informação incorreta ou acredite que algum crédito está faltando, por favor, entre em contato!

---

> Referencias
> - [Crash Course on the Kotlin Compiler by Amanda Hinchman-Dominguez - KotlinConf'23](https://www.youtube.com/watch?v=wUGfuWHCqrc), [Github repo](https://github.com/ahinchman1/Kotlin-Compiler-Crash-Course)
> - [Curso intensivo no compilador do Kotlin | K1 + K2 Frontends, Backends](https://medium.com/google-developer-experts/crash-course-on-the-kotlin-compiler-k1-k2-frontends-backends-fe2238790bd8)
> - [Rumo ao Compilador K2 | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2021/11/the-road-to-the-k2-compiler/)
> - [Destaques do Roteiro Kotlin Outono 2021 | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2021/11/kotlin-roadmap-autumn-2021/)
> - [O Compilador K2 Estabilizando no Kotlin 2.0 | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2021/11/the-k2-compiler-is-going-stable-in-kotlin-2-0/)
> - [Documentação Básica FIR | GitHub](https://github.com/JetBrains/kotlin/blob/master/docs/fir/fir-basics.md)
> - [Novidades no Kotlin 2.0.0-Beta1 | Documentação Kotlin](https://kotlinlang.org/docs/whatsnew-eap.html)
