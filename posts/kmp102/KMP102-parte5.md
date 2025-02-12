## Explorando a Modularização no KMP

No último artigo, entramos em detalhes e aprendemos sobre as peculiaridades do código exportado nos headers do Objective-C, assim como as boas práticas no que exportar. 

Nesse artigo, vamos entender melhor sobre o comportamento da modularização em  projetos KMP, e como isso pode ser feito de forma eficiente e organizada.

## O que é modularização?
Não irei me alongar muito nesse tópico, pois já abordamos esse assunto no [Android Plataforma - Parte 1: Modularização](https://dev.to/rsicarelli/android-plataforma-parte-1-modularizacao-2016). Se não tem certeza do que é modularização, recomendo uma pausa para a leitura do artigo.

Em resumo, modularização é a prática de dividir um projeto em módulos menores e independentes, que podem ser desenvolvidos, testados e mantidos separadamente.

Essa prática é crucial para escalar projetos KMP, já que a modularização impacta diretamente na autonomia e independência dos times internos, evitando que um time dependa do outro para realizar suas tarefas.

## Modularização no KMP
No KMP, a modularização é feita através de módulos compartilhados, que são responsáveis por compartilhar código entre as plataformas.

Vamos elaborar uma estrutura de módulos que respeite a separação de responsabilidades e possibilite a reutilização de código de forma eficiente entre módulos:

<p align="center">
  <img src="https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-modularization-pt1.png?raw=true" width="300" />
</p>

