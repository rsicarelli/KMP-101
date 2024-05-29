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
![XCFramework registered task](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/xcframework-gradle-task.png?raw=true)

### Analisando o resultado da task `assemble...XCFramework`

Ao executarmos a task `assembleKotlinSharedXCFramework`, o Kotlin/Native irá gerar os `.xcframework` para todos os targets que definimos no `build.gradle.kts`.

Esse artefato é justamente o arquivo que precisamos linkar no projeto Xcode para consumir nosso código KMP compilado para Objective-C!

> **Nota**: tome cuidado com o nome do projeto! Caso haja caracteres especiais, como "-", irá resultar em um erro (apesar do XCFramework ser gerado). 

![XCFramework task result](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/xcframework-task-result.png?raw=true)

## NativeBuildTypes: debug e release
Observe que temos 2 frameworks gerados: a versão `debug` e `release`. Esses dois tipos possuem características especiais, e são provenientes da classe [NativeBinaryType](https://github.com/JetBrains/kotlin/blob/master/libraries/tools/kotlin-gradle-plugin-api/src/common/kotlin/org/jetbrains/kotlin/gradle/plugin/mpp/NativeBinaryTypes.kt):

Analisando esse enum, entendemos que a versão `release` possui a flag `optimized = true` e `debuggable = false`. Em contrapartida, a versão `debug` possuí `optimized = false` e `debuggable = true`. 

Como você pode imaginar, devemos ter cuidado na hora de escolher qual `XCFramework` utilizar no fluxo de desenvolvimento:
- Para ambiente de desenvolvimento local, a versão `debug` é a escolha, já que podemos debuggar nosso código KMP.
- Para ambiente de produção, a versão `release` é a escolha correta, já que o binário é otimizado, além de evitar shipar informações de debug no seu produto final. 

```kotlin
// kotlin/libraries/tools/kotlin-gradle-plugin-api/src/common/kotlin/org/jetbrains/kotlin/gradle/plugin/mpp/NativeBinaryTypes.kt

enum class NativeBuildType(
    val optimized: Boolean,
    val debuggable: Boolean
) : Named {
    RELEASE(true, false),
    DEBUG(false, true);
}
```

## Controlando qual tipo de build gerar
A configuração de gerar os tipos de binário são provenientes da função `iosTarget.binaries.framework()`. Ao analisarmos a classe [AbstractKotlinNativeBinaryContainer](https://github.com/JetBrains/kotlin/blob/master/libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/dsl/AbstractKotlinNativeBinaryContainer.kt), observamos que a função `framework()` possuí um argumento `buildTypes` com um valor padrão.
```kotlin
// kotlin/libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/dsl/AbstractKotlinNativeBinaryContainer.kt

/** Creates an Objective-C framework with the given [namePrefix] for each build type and configures it. */
@JvmOverloads
fun framework(
    namePrefix: String,
    buildTypes: Collection<NativeBuildType> = NativeBuildType.DEFAULT_BUILD_TYPES,
    configure: Framework.() -> Unit = {}
) = createBinaries(namePrefix, namePrefix, NativeOutputKind.FRAMEWORK, buildTypes, ::Framework, configure)

// kotlin/libraries/tools/kotlin-gradle-plugin-api/src/common/kotlin/org/jetbrains/kotlin/gradle/plugin/mpp/NativeBinaryTypes.kt
enum class NativeBuildType(...) : Named {
    ...
    companion object {
        val DEFAULT_BUILD_TYPES = setOf(DEBUG, RELEASE)
    }
}
```

Durante o fluxo de desenvolvimento, você pode querer evitar compilar as duas versões devido ao aumento do tempo de compilação. Para isso, basta adaptarmos nosso `build.gradle.kts`:

```kotlin
kotlin {
    val compileOnlyDebug = true // some gradle.properties flag will help you here!

    val buildType = if (compileOnlyDebug)
        NativeBuildType.DEBUG
    else NativeBuildType.RELEASE

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework(
            buildTypes = listOf(buildType)
        ) {
            baseName = "KotlinShared"
            isStatic = true

            xcFramework.add(this)
        }
    }
}
```

## Conclusões
O XCFramework é um tema central no universo KMP. Entender o que é, como funciona e como gerar nos possibilita um maior controle e compreensão dos bastidores do KMP.

No próximo artigo, vamos explorar melhor a função `framework()`!

## Fontes

- https://medium.com/@mihail_salari/embracing-the-power-of-xcframeworks-a-comprehensive-guide-for-ios-developers-77fe192d47fe
