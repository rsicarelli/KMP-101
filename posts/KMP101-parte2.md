# KMP 101: Kotlin e suas múltiplas compilações

No último post, aprendemos um pouco sobre o paradigma de multiplataforma e como o KMP é uma das tecnologias mais promissoras para devs nativos na atualidade.

Nesse post, aprenderemos alguns conceitos básicos do compilador do Kotlin, e como o compilador consegue realizar compilações para múltiplas plataformas.

---

## Introdução ao compilador Kotlin

Um compilador é um software que converte um código escrito em uma linguagem de programação para outra linguagem. Um dos usos mais comum dos compiladores é utilização para transformar programas escritos em linguagens de alto nível para uma linguagem de nível mais baixo, visando criar um programa executável.

O Kotlin, assim como outros compiladores como o [LLVM](https://llvm.org/) e o [GCC](https://gcc.gnu.org/), adota uma arquitetura de compilador que divide o processo de compilação em duas partes principais: o **frontend** e o **backend**. Essas duas partes se comunicam por uma linguagem intermediária, conhecida como **Intermediate Representation (IR)**.

### Frontend do compilador do Kotlin

O frontend do compilador é responsável por analisar o código-fonte e prepará-lo para compilação.

Essa etapa utiliza o código-fonte `.kt` e transforma em uma representação intermediária pronta para ser processada pelo backend.

Atualmente, o Kotlin possui dois frontends distintos: **K1** e **K2**.

#### Frontend K1 (FE10)

É o frontend original do compilador Kotlin, também conhecido como **FE10**. Este frontend tem sido o padrão até agora, tratando da análise léxica, sintática e semântica do código Kotlin.

- **Análise Léxica (Lexer):** divide o código-fonte Kotlin em tokens, elementos fundamentais para a construção da linguagem.
- **Análise Sintática (Parser):** organiza os tokens em uma estrutura sintática, geralmente uma árvore de análise sintática (AST), que representa a estrutura lógica do código.
- **Árvores PSI/AST:** utiliza a `Abstract Syntax Tree` (AST) e a `Program Structure Interface` (PSI) para representar e manipular a estrutura do código, essencial para análises subsequentes.
- **Análise Semântica:** verifica a corretude do uso dos elementos da linguagem, como tipos e escopos, garantindo que o código segue as regras semânticas do Kotlin.
- **Algoritmo de Inferência de Tipo:** melhorado no Kotlin 1.4.0, contribuindo para uma análise de tipo mais eficiente durante a compilação.
- **Backends de JVM e JS IR:** a introdução de novos backends de IR (Intermediate Representation) para JVM e JavaScript no Kotlin 1.5.0 e 1.6.*, respectivamente, proporcionando uma base para compilações mais eficientes e suporte a futuras extensões da linguagem.

![K1 Frontend](https://github.com/ahinchman1/Kotlin-Compiler-Crash-Course/blob/master/res/k1_frontend.png?raw=true)

> [ahinchman1/Kotlin-Compiler-Crash-Course](https://github.com/ahinchman1/Kotlin-Compiler-Crash-Course)
> 
> [The Road to the K2 Compiler | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2021/11/the-road-to-the-k2-compiler/)
> 
> [Seven Highlights from the Kotlin Roadmap Autumn 2021 | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2021/11/kotlin-roadmap-autumn-2021/)

#### Frontend K2 (FIR)

Representa a nova geração do frontend para o compilador Kotlin, que atualmente ainda está em processo de desenvolvimento. Conhecido como **FIR** (Frontend Intermediate Representation), visa substituir o frontend K1 FE10. 

O primeiro suporte beta ao K2 veio no Kotlin `1.9.20` lançado em novembro de 2023, e o suporte oficial irá ser lançada junto ao Kotlin `2.0.0` em 2024

Suas principais melhorias estão no desempenho e na arquitetura, com uma representação semântica mais limpa e eficiente do código. Muitos dos conceitos são reaproveitados do K1, porém com alguns pontos-chave:

- **Reescrita completa**: o FIR oferece uma reescrita completa do Frontend que visa ser mais rápida e extensível, tratando débitos técnicos do FE10 e preparando para extensões futuras da linguagem.
- **Análise de fluxo de dados aprimorada:** introduz um algoritmo de análise de fluxo de dados mais preciso, resultando em melhores conversões inteligentes (smart casts).
- **Suporte a Plugins:** Inclui suporte para uma variedade de plugins, como `kapt`, `serialization`, `all-open`, `no-arg`, `SAM with receiver`, `Lombok`, `AtomicFU`, `Jetpack Compose compiler plugin`, e `Kotlin Symbol Processing (KSP) plugin`.
- **Compatibilidade entre plataformas:** O K2 oferece suporte unificado para todas as plataformas que o Kotlin suporta, incluindo `JVM`, `Native`, `Wasm` e `JS`, e é otimizado para projetos multiplataforma.

![K2 FIR](https://github.com/ahinchman1/Kotlin-Compiler-Crash-Course/blob/master/res/k2_frontend.png?raw=true)

> [ahinchman1/Kotlin-Compiler-Crash-Course](https://github.com/ahinchman1/Kotlin-Compiler-Crash-Course)
> 
> [The Kotlin Blog: The K2 Compiler Is Going Stable in Kotlin 2.0](https://blog.jetbrains.com/kotlin/2021/11/the-k2-compiler-is-going-stable-in-kotlin-2-0/)
> 
> [GitHub: kotlin/docs/fir/fir-basics.md](https://github.com/JetBrains/kotlin/blob/master/docs/fir/fir-basics.md)
> 
> [Kotlin Documentation: What's new in Kotlin 2.0.0-Beta1](https://kotlinlang.org/docs/whatsnew-eap.html)

### Backend

O backend, por outro lado, lida com a conversão desta representação abstrata em código de máquina ou em outro formato de baixo nível, como bytecode para a JVM (Java Virtual Machine). É no backend que ocorrem as otimizações de código e a geração de saída específica para a plataforma alvo.

## Intermediate Representation (IR)

Para realizar a ponte entre o frontend e o backend, os compiladores utilizam uma estrutura de dados conhecida como Intermediate Representation (IR). A IR serve como uma linguagem comum, permitindo que diferentes frontends (suportando várias linguagens de programação) se comuniquem com diferentes backends (gerando código para várias plataformas e arquiteturas de hardware).

### Características da IR

- **Independência de Linguagem:** A IR é desenhada para ser neutra em relação à linguagem de programação, permitindo que um único backend suporte múltiplas linguagens.
- **Independência de Plataforma:** Da mesma forma, ela é independente da plataforma alvo, o que facilita a geração de código para diferentes sistemas e arquiteturas.
- **Facilita Otimizações:** A IR permite a implementação de otimizações de forma mais eficiente, uma vez que a representação do código é mais abstrata e padronizada.

## Exemplos Notáveis de Uso de IR

1. **LLVM:** Um projeto de compilador que fornece uma infraestrutura para desenvolver compiladores (ou toolchains) que usam uma IR comum para otimizações e geração de código.
2. **GCC (GNU Compiler Collection):** Um compilador que também implementa uma forma de IR, permitindo otimizações e suporte para múltiplas linguagens e plataformas.

## Conclusão

A utilização de Intermediate Representation é um aspecto crucial na arquitetura de compiladores modernos, como o Kotlin. Ela não apenas facilita a comunicação entre diferentes partes do compilador, mas também aumenta a eficiência do processo de compilação, otimização e geração de código para diversas plataformas.
