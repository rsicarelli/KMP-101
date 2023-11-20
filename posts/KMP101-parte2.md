# KMP 101: Kotlin e suas múltiplas compilações

No último post, exploramos o paradigma multiplataforma e como o KMP se destaca no ecossistema.

Neste post, vamos desvendar os conceitos básicos do compilador Kotlin e sua habilidade de compilar para múltiplas plataformas.

---

## Introdução ao compilador Kotlin

Um compilador é um software que traduz código de uma linguagem de programação para outra. Frequentemente, os compiladores são utilizados para transform programas de linguagens de alto nível em linguagens de baixo nível para gerar programas executáveis.

O Kotlin, a exemplo de compiladores como [LLVM](https://llvm.org/) e [GCC](https://gcc.gnu.org/), possui uma arquitetura dividida em **frontend** e **backend**, comunicando-se por uma **Intermediate Representation (IR)**.

### Frontend do compilador Kotlin

Responsável por analisar e preparar o código-fonte `.kt` para a compilação, o Kotlin apresenta dois frontends: **K1** e **K2**.

#### Frontend K1 (FE10)

O frontend K1, ou FE10, é o frontend original do compilador Kotlin que é o padrão utilizado na atualidade.

Principais características:

- **Análise Léxica (Lexer):** divide o código-fonte Kotlin em tokens, elementos fundamentais para a construção da linguagem.
- **Análise Sintática (Parser):** organiza os tokens em uma estrutura sintática, geralmente uma árvore de análise sintática (AST), que representa a estrutura lógica do código.
- **Árvores PSI/AST:** utiliza a `Abstract Syntax Tree` (AST) e a `Program Structure Interface` (PSI) para representar e manipular a estrutura do código, essencial para análises subsequentes.
- **Análise Semântica:** verifica a corretude do uso dos elementos da linguagem, como tipos e escopos, garantindo que o código segue as regras semânticas do Kotlin.
- **Algoritmo de Inferência de Tipo:** melhorado no Kotlin 1.4.0, contribuindo para uma análise de tipo mais eficiente durante a compilação.
- **Backends de JVM e JS IR:** a introdução de novos backends de IR (Intermediate Representation) para JVM e JavaScript no Kotlin 1.5.0 e 1.6.*, respectivamente, proporcionando uma base para compilações mais eficientes e suporte a futuras extensões da linguagem.

![Frontend K1](https://github.com/ahinchman1/Kotlin-Compiler-Crash-Course/blob/master/res/k1_frontend.png?raw=true)

Referências:
- [Curso Intensivo sobre o Compilador Kotlin](https://github.com/ahinchman1/Kotlin-Compiler-Crash-Course)
- [Rumo ao Compilador K2 | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2021/11/the-road-to-the-k2-compiler/)
- [Destaques do Roteiro Kotlin Outono 2021 | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2021/11/kotlin-roadmap-autumn-2021/)

#### Frontend K2 (FIR)

Com o codinome **FIR** (Frontend Intermediate Representation), o K2 representa a nova geração do frontend do compilador Kotlin. Ainda em desenvolvimento, ele substituirá o K1 (FE10).

A primeira versão beta do K2 foi introduzida no Kotlin `1.9.20`, lançado em novembro de 2023, e o lançamento oficial está previsto para o Kotlin `2.0.0` em 2024. Suas melhorias abrangem desempenho, arquitetura e uma representação semântica mais limpa do código. Alguns destaques são:

- **Reescrita total:** o FIR apresenta uma reescrita completa do frontend, visando rapidez e extensibilidade.
- **Análise de fluxo de dados aprimorada:** um algoritmo mais preciso que resulta em melhores conversões inteligentes (smart casts).
- **Suporte a plugins:** inclui suporte a uma variedade de plugins, como `kapt`, `serialization`, `all-open`, e outros.
- **Compatibilidade entre plataformas:** suporta `JVM`, `Native`, `Wasm`, e `JS`, otimizado para projetos multiplataforma.

![Frontend K2 FIR](https://github.com/ahinchman1/Kotlin-Compiler-Crash-Course/blob/master/res/k2_frontend.png?raw=true)

Referências:
- [Curso Intensivo sobre o Compilador Kotlin](https://github.com/ahinchman1/Kotlin-Compiler-Crash-Course)
- [O Compilador K2 Estabilizando no Kotlin 2.0 | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2021/11/the-k2-compiler-is-going-stable-in-kotlin-2-0/)
- [Documentação Básica FIR | GitHub](https://github.com/JetBrains/kotlin/blob/master/docs/fir/fir-basics.md)
- [Novidades no Kotlin 2.0.0-Beta1 | Documentação Kotlin](https://kotlinlang.org/docs/whatsnew-eap.html)

### Backends do compilador Kotlin

Após o frontend de um compilador processar e preparar o código-fonte, entra em cena o backend.

O backend converte a representação intermediária (IR) em código de máquina, realizando otimizações e gerando a saída para a plataforma alvo (`*.class`, `*.js`, `*.so`, `*.wasm`)

O Kotlin é projetado para ser multiplataforma, o que significa que ele pode ser compilado para rodar em diferentes tipos de dispositivos e sistemas operacionais. Cada backend do compilador Kotlin é otimizado para uma plataforma-alvo específica, permitindo devs escreverem um código que pode ser executado em qualquer lugar.

- **Kotlin/JVM:**: compila o código Kotlin para Bytecode da JVM, que pode ser executado em qualquer máquina virtual Java. Este backend é utilizado para aplicações que rodam em aparelhos Android, servidores, desktops e qualquer sistema que suporte a JVM.
- **Kotlin/Native:** compila o código Kotlin para código de máquina nativo, utilizando a toolchain **LLVM**. Suporta uma variedade de plataformas, incluindo iOS, macOS, Windows, Linux e até sistemas embarcados.
- **Kotlin/JS:** converte o código Kotlin para JavaScript, permitindo o uso de Kotlin em desenvolvimento web. Pode ser executado em navegadores como código do lado do cliente ou em ambientes do lado do servidor como Node.js.
- **Kotlin/Wasm:** permite a compilação de código Kotlin para WebAssembly, uma forma binária para a web. Permite que o código Kotlin seja executado em navegadores web com desempenho próximo ao nativo, adequado para aplicações web complexas e jogos.

## Intermediate Representation (IR)

A IR facilita a comunicação entre o frontend e o backend, sendo independente de linguagem e plataforma, o que possibilita otimizações mais eficientes.

### Características da IR

- **Independência de Linguagem:** Neutra quanto à linguagem de programação.
- **Independência de Plataforma:** Facilita a geração de código para diferentes sistemas.
- **Otimizações Eficientes:** A representação abstrata e padronizada permite otimizações mais eficazes.

## Exemplos Notáveis de Uso de IR

1. **LLVM:** Fornece infraestrutura para compiladores com IR comum.
2. **GCC:** Suporta otimizações e múltiplas linguagens e plataformas.

## Conclusão

A IR é essencial na arquitetura de compiladores modernos, aumentando a eficiência da compilação e geração de código.
