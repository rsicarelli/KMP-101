## KMP102 - XCFramework para Devs Kotlin Multiplataforma

Olá! Dou as boas-vindas a série do KMP-102. Vamos avançar nos conceitos do Kotlin Multiplatform aprendendo mais como integrar nosso código Kotlin no iOS e outras plataformas.

Como início dessa série, vamos aprender mais sobre um formato de arquivo especial para compartilhar código com a família Apple: O XCFramework.

### Introdução ao `.framework` da Apple

Um [framework](https://developer.apple.com/library/archive/documentation/MacOSX/Conceptual/BPFrameworks/Concepts/WhatAreFrameworks.html) é um pacote que representa um conjunto de recursos e código-fonte a ser utilizado em projetos para a família Apple. No mundo da JVM, é como se fosse um `.jar` ou `.aar` no caso do Android. 

É um formato pré-compilado que pode ser utilizado entre projetos do Xcode livremente. Esse formato de arquivo possibilita a criação de bibliotecas para a família Apple, possibilitando a distribuição e consumação por gerenciadores de pacotes (como Cocoapods ou o Swift Package Manager)

![AppKit.framework](https://developer.apple.com/library/archive/documentation/General/Conceptual/DevPedia-CocoaCore/Art/framework_2x.png)

### Introdução ao XCFramework

O [XCFramework](https://developer.apple.com/documentation/xcode/creating-a-multi-platform-binary-framework-bundle) é um tipo de pacote ou artefato que facilita a distribuição de bibliotecas para a família Apple. Basicamente, ao invés de distribuírmos vários `.framework` para cada plataforma, temos apenas um único `.xcframework` com múltiplos `.framework` lá dentro, cada um representando uma plataforma específica que a biblioteca suporta.

O Kotlin Multiplataforma (mais especificadamente, o Kotlin/Native) se utiliza desse artefato para pre-compilar código Kotlin para Objective-C, possuindo total interoperabilidade com Swift. Com isso, nosso código Kotlin é facilmente compartilhado entre todos os alvos suportados do seu projeto, e facilita muito o processo de desenvolvimento: ao invés de compilar vários `.framework` para cada alvo suportado no KMP, temos apenas `.xcframework` para cada alvo ou arquitetura de processador.



### Gerando um XCFramework no KMP

Por de baixo dos panos, o KGP (Kotlin Gradle Plugin) se utiliza da toolchain do Xcode e nos fornece uma API que possibilita a criação do XCFramework através dos nossos `build.gradle.kts`:

```kotlin
kotlin {
    val xcFramework = XCFramework(xcFrameworkName = "KotlinShared")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "KotlinShared"
            isStatic = true

            xcFramework.add(this)
        }
    }
}
```

Ao sincronizar o projeto, observamos que a task `assembleKotlinSharedXCFramework` foi registrada no nosso projeto. Observe que a task tem o miolo `KotlinShared`, que corresponde com o parâmetro `xcFrameworkName` da classe `XCFramework`:
![XCFramework task registered](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/xcframework-gradle-task?raw=true)

### Analisando o resultado da task `assemble...XCFramework`

![XCFramework task result](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/xcframework-task-result?raw=true)

## Fontes

- https://medium.com/@mihail_salari/embracing-the-power-of-xcframeworks-a-comprehensive-guide-for-ios-developers-77fe192d47fe
