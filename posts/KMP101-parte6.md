No último artigo, criamos um projeto utilizando o KMP Wizard, e sem muitos esforços executamos nosso app em aparelhos Android, iOS e Desktop.

Dessa vez, vamos nos aprofundar em um aspecto fundamental do KMP: o Plugin KMP pro Gradle.

---

## O que é o Gradle?

O Gradle é uma ferramenta crucial em projetos Kotlin, e é um tópico onde você irá precisar investir bastante do seu tempo aprendendo, especialmente se você não tiver um background como dev Android.

Pense no Gradle como o NPM/Yarn/Webpack no mundo JavaScript, ou CocoaPods/Swift Package Manager no mundo iOS. Vamos utilizar da seguinte tabela para compararmos um pouco cada ferramenta:

| Funcionalidade                | Gradle | NPM      | Yarn     | Webpack | CocoaPods | SwiftPM  |
|-------------------------------|--------|----------|----------|---------|-----------|----------|
| Gerenciamento de dependências | ✅      | ✅        | ✅        | ❌       | ✅         | ✅        |
| Automação de build            | ✅      | ❌        | ❌        | ✅       | ❌         | ✅        |
| Compilação de código          | ✅      | ❌        | ❌        | ❌       | ❌         | ❌        |
| Execução de scripts           | ✅      | ✅        | ✅        | ✅       | ✅         | ✅        |
| Customização de builds        | ✅      | Limitada | Limitada | ✅       | Limitada  | Limitada |
| Gestão de repositórios        | ✅      | ✅        | ✅        | ❌       | ✅         | ✅        |
| Plug-ins e extensões          | ✅      | ✅        | ✅        | ✅       | ✅         | ✅        |
| Pacotes Distribuíveis         | ✅      | ✅        | ✅        | ✅       | ✅         | ✅        |


### Por que o Gradle é tão importante no KMP?
Um dos pilares do Kotlin Multiplatform Project (KMP) é a integração profunda com o Gradle, através do uso do [Plugin KMP](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.multiplatform). O KMP utiliza extensivamente o Gradle para gerenciar diversos aspectos antes, durante e após o processo de desenvolvimento. O Gradle não apenas facilita a configuração do projeto, mas também fornece tarefas especializadas que auxiliam na integração de módulos compartilhados do KMP com aplicativos iOS, por exemplo.

A integração com Android é realizada de forma suave e direta. No entanto, para outras plataformas, como o iOS, configurações adicionais são muitas vezes necessárias. Antes da versão `1.5.20` do Kotlin, a integração do módulo compartilhado no iOS podia exigir a configuração manual de tarefas no Gradle e ajustes no projeto Xcode para utilizar essas tarefas durante o processo de build. Agora, com as atualizações no plugin KMP, uma dessas tarefas simplificadoras, a `embedAndSignAppleFrameworkForXcode`, é usada diretamente do Xcode para conectar o módulo KMP à parte iOS do projeto.

Além da integração com o Xcode, o Plugin do KMP oferece uma ampla gama de integrações com outras plataformas, como o uso do ***Webpack*** para projetos JS. Tudo orquestrado e executado pelo Gradle. 

### Groovy vs Kotlin
A linguagem original do Gradle é o Groovy. Porém, na atualidade, a comunidade Kotlin utiliza o [Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html) que permite manipular o Gradle através do Kotlin.

Importante notar que:
- Arquivos `.gradle` são em Groovy
- Arquivos `.gradle.kts` são em Kotlin, utilizando o Kotlin DSL.

### Recado para iniciantes em Gradle
Recomendo muito dar uma pausa na leitura e pesquisar mais sobre o Gradle.

> - [🔗 Começando com o Gradle: Tasks e comandos básicos | #AluraMais com o Alex Felipe](https://www.youtube.com/watch?v=uX6Ezf73OEY)
> - [Getting Started with the Gradle Kotlin DSL com o Paul Merlin e Rodrigo B. de Oliveira](https://www.youtube.com/watch?v=KN-_q3ss4l0)

## Dissecando os arquivos Gradle
Assumindo que você tenha compreendido alguns aspectos-chave do Gradle, vamos analisar os arquivos mais importantes do projeto que criamos no [artigo anterior](https://dev.to/rsicarelli/kmp-101-criando-e-executando-seu-primeiro-projeto-multiplataforma-no-fleet-4ep7).

![Demo em todas as plataformas](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/anatomy-of-the-gradle-build.png?raw=true)

### O arquivo `settings.gradle.kts`

Para não te deixar no escuro, é importante você entender alguns aspectos importantes:
1. **O arquivo `settings.gradle.kts`** é responsável pela configuração global do projeto. Aqui definimos aspectos como o nome do projeto, quais repositórios de artefatos iremos utilizar, etc
2. **O arquivo `build.gradle.kts` da raiz** é responsável pela configuração de todos os módulos Gradle do projeto. Aqui definimos aspectos cruciais, como a declaração/adição dos plugins Gradle
3. **O arquivo `build.gradle.kts` da pasta `composeApp`** representa a configuração específica do módulo no projeto. Aqui realizamos configurações como os alvos da compilação, source sets, dependencias, etc.

---



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

- [Gradle vs. Other Build Tools - unrepo.com](https://www.unrepo.com)
- [Gradle vs. npm - Gradle Hero](https://gradlehero.com)
- [Yarn vs NPM - phoenixnap.com](https://phoenixnap.com/kb/yarn-vs-npm)
- [Webpack Comparison - webpack.js.org](https://webpack.js.org/comparison/)
- [Multiplatform Gradle Plugin Improved for Connecting KMM Modules | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2021/07/multiplatform-gradle-plugin-improved-for-connecting-kmm-modules/)
