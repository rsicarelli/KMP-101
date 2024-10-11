## Utilizando código Kotlin no iOS

No último post, aprendemos a criar um `XCFramework` a partir do código Kotlin, e alguma características sobre os tipos de build gerados.

Com isso, podemos avançar e aprender as caraterísticas do código Kotlin compilado para Objective-C e como consumir esse código no iOS.

## Exportando um 'Olá mundo' em Kotlin para Objective-C

Para iniciarmos, vamos primeiro entender alguns pontos chave do código Kotlin que é convertido para Objective-C, e consequentemente, utilizar o mesmo código no iOS.

Vamos criar um simples `HelloWorld` em Kotlin:

```kotlin
//HelloWorld.kt commonMain
public expect fun helloWorld(): String

//HelloWorld.apple.kt appleMain
actual fun helloWorld(): String = "Olá mundo Apple Main"
```

Agora precisamos compilar um XCFramework, e integrar no Xcode. A internet já conta com diversos tutoriais sobre como fazer isso, para esse demo eu segui "[How to Integrate Kotlin Multiplatform (KMP) into Your iOS Project](https://jyotibhambhu.medium.com/part-3-how-to-integrate-kotlin-multiplatform-kmp-into-your-ios-project-7dc4016f7fb5)".

Para isso, precisamos basicamente:

1. Compilar o XCFramework com `./gradlew assembleKotlinSharedXCFramework`. **NOTA:** substitua "KotlinShared" pelo nome do seu XCFramework! Abordamos isso nos artigos passados.
2. Configurar o projeto Xcode para consumir o XCFramework gerado.
3. Utilizar o código Kotlin no iOS.

Depois que toda a configuração for realizada, conseguimos avançar e criar uma tela bem simples em SwiftUI para consumir o código Kotlin:

```swift
import SwiftUI
import KotlinShared

struct ContentView: View {
    @State private var showText = false

    var body: some View {
        Button("Show Text") { showText.toggle() }
        if showText { Text(HelloWorld_appleKt.helloWorld()) }
    }
}
```

Como resultado, teremos:

<img src="https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kotlin-shared-hello-world-ios.gif?raw=true" width="200" />

### O que está acontecendo aqui?

Passo a passo o que está acontendo nos bastidores:

1. O código Kotlin é compilado para Objective-C e empacotado em um XCFramework.
2. O XCFramework é integrado no projeto Xcode.
3. Com o XCFramework integrado, podemos importar o código Kotlin no iOS através `import KotlinShared`.
4. Dentro do `KotlinShared` (o nome do XCFramework!), temos acesso ao código Kotlin compilado para Objective-C.
5. A classe `HelloWorld_appleKt` é gerada automaticamente pelo Kotlin/Native, e nos permite acessar o método `helloWorld()`.
6. Com isso, podemos utilizar o código Kotlin no iOS!

```swift
import KotlinShared

let helloWorld = HelloWorld_appleKt.helloWorld()
```

Mas se notarmos, a sintaxe para acessar o código Kotlin no iOS é um pouco diferente do que estamos acostumados. `HelloWorld_appleKt.helloWorld()` é uma sintaxe nada idiomática para o Swift.

Vamos entender melhor esse ponto

## Compreendendo o código gerado pelo Kotlin/Native
A maior limitação hoje no Kotlin/Native é a interoperabilidade com Objective-C. O Kotlin/Native não consegue gerar um código que seja 100% compatível com o Swift.

Isso porque o Kotlin/Native é um compilador que gera código Objective-C, e não Swift. O código gerado é compatível com Objective-C, e não Swift.

Ou seja, temos várias funcionalidades em Kotlin traduzidas diretamente para Swift (como high order functions), mas não temos uma tradução direta de Kotlin --> Swift.

Para investigar como o código Kotlin é traduzido para Objective-C, podemos acessar o código gerado pelo Kotlin/Native. Para isso, basta dar um `cmd + click` na nossa classe `HelloWorld_appleKt`:

![Hello world em Obj-c](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kotlin-shared-hello-world-decompiled.png?raw=true)

Para melhorar a experiência de uso do código Kotlin no iOS, podemos codificar nosso código Kotlin de uma forma diferente, para ser mais idiomático ao Swift.

## Melhorando a interoperabilidade com Swift
Observamos que não podemos simplesmente escrever código Kotlin e esperar que ele seja idiomático ao Swift devida a característica do Kotlin/Native somente gerar código Objective-C.

Para isso, temos que escrever nosso código Kotlin de uma forma que seja mais amigável ao Swift. Vamos refatorar o código `HelloWorld` para ser mais idiomático ao Swift:

```kotlin
// HelloWorld.apple.kt appleMain
package br.com.rsicarelli.example

@HiddenFromObjC
actual fun helloWorld(): String = "Olá mundo Apple Main"

object HelloWorld
fun HelloWorld.get(): String = helloWorld()
```

Agora, realizamos o mesmo passo a passo para utilizar no Xcode:
1. Compilar o XCFramework com `./gradlew assembleKotlinSharedXCFramework`.
2. No Xcode, `Products` > `Build for ...` > `Running`, ou simplesmente `cmd + shift + r`

Logo após o build, notamos que a nossa classe anterior `HelloWorld_appleKt` não está mais disponível.
![Hello world quebrado no Xcode](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kotlin-shared-hello-world-changed-xcode-error.png?raw=true)

Antes de entender o porquê, vamos integrar nosso código KMP utilizando a nova abordagem:

```swift
struct ContentView: View {
    @State private var showText = false

    var body: some View {
        Button("Show Text") { showText.toggle() }
        if showText { Text(HelloWorld.shared.get()) }
    }
}
```

Sucesso! Esse código é mais idiomático ao Swift, e conseguimos utilizar o código Kotlin no iOS de uma forma mais amigável.

Se abrirmos o código Objective-C gerado pelo Kotlin/Native, notamos algumas diferenças:
![Hello world idiomático ao Swift](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kotlin-shared-hello-world-changed-idiomatic-swift.png?raw=true)

