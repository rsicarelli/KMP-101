# Estratégia de integração KMP

No último post, aprendemos sobre modularização no KMP, e como isso impacta diretamente a autonomia e independência das nossas features. Vimos como a modularização está diretamente relacionada a flexibilidade de UI, e como isso pode ser um grande diferencial para o desenvolvimento de aplicações multiplataforma.

Nesse post, vamos avançar e aprender mais sobre estratégias de integração KMP em projetos iOS existentes.

## Desafios do XCFramework

No artigo [KMP-102 - Características do XCFramework no KMP](https://dev.to/rsicarelli/kmp-102-caracteristicas-do-xcframework-no-kmp-3162), aprendemos que o XCFramework no KMP precisa seguir o modelo "fat framework" para que possamos compartilhar código com a plataforma Apple. Se você ainda não leu, recomendo uma pausa para a leitura desse artigo, pois vamos precisar desse conhecimento para explorar a estratégia de integração nesse post.

De forma geral, temos dois cenários de integração KMP em projetos iOS:
1. **Repositórios separados (multi-repo):** aqui, nosso código KMP e nosso código iOS moram em repositórios separados. Esse cenário é comum especialmente se estamos trabalhando em um projeto já existente, e queremos adicionar funcionalidades multiplataforma.
2. **Repositórios unificados (monorepo)**: aqui, nosso código KMP e nosso código iOS moram no mesmo repositório. Esse cenário é comum especialmente se estamos começando um projeto do zero, e queremos adicionar funcionalidades multiplataforma.

O **cenário 1 é o mais comum** (não quer dizer que é o ideal, já exploramos isso), já que a maioria dos projetos iOS já existem antes de pensarmos em adicionar funcionalidades multiplataforma. Nesse cenário, temos um desafio: como integrar o código KMP no projeto iOS existente?

## Cenário 1: Utilizando o XCFramework em um projeto iOS separado

Esse cenário reflete a realidade de projetos existentes que querem adicionar ou migrar para KMP:

1. App Android e iOS já existem em repositórios separados
2. Código KMP mora no repositório Android (Kotlin)
3. A integração do código KMP no projeto iOS é feita via XCFramework
4. O código KMP é compilado em um XCFramework e importado no projeto iOS via Swift Package Manager, CocoaPods ou manualmente (Build Phase/scripting)

<img src="https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-ios-integration-scenario1.png?raw=true" />

### Desafios do cenário 1
Esse cenário é o menos ideal, pois além de termos que lidar com a complexidade de gerenciar um projeto multi-repo, temos que lidar com a complexidade operacional dessa integração. Vamos explorar melhor esse desafio.

Considere o seguinte cenário: temos 3 times, cada um responsável por uma parte do app:
1. Time 1: realiza mudanças no módulo  `navigation`.
2. Time 2: realiza mudanças no módulo `login`.
3. Time 3: realiza mudanças no módulo `home`.

Exemplificando esse cenário a seguir. Para simplificar, omiti as mudanças nos outros módulos:

<img src="https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-ios-integration-scenario1-breaking-change.png?raw=true" />
