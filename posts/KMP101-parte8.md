## Aprendendo sobre como interpretar e utilizar depêndencias no KMP

Nos últimos artigos, criamos uma fundação de conhecimento sobre o Kotlin Multiplataforma e compreendemos como o Kotlin compila para múltiplas plataformas. 

Nesse artigo, vamos explorar sobre a utilização de bibliotecas de código livre (open-source), como entender se é possível utilizar no seu projeto, e, finalmente, as utilizando.

---

## Depêndencias e os Source Sets
Aprendemos que o Kotlin se utiliza de uma estrutura de Source Sets para orquestrar as diferentes compilações.

Cada source set do Kotlin, seja o `common` ou específicos `android`, `native/ios`, `desktop`, `js`, possuem flexibilidade para declarar apenas as depêndencias utilizadas naquele source set.

Exemplo:
```kotlin
commonMain.dependencies {
    // compartilhado por todos os source sets 
}
androidMain.dependencies {
    // common + Android 
}
appleMain.dependencies {
    // common + família Apple
}
iosMain.dependencies {
    // common + apple + iOS 
}
```

### Source Set é um ambiente único
Cada Source Set do Kotlin se torna um ambiente isolado dos outros Source Sets. 

Por exemplo, dentro do Source Set do Android você tem acesso ao Android SDK.

No iOS, você ganha acesso ao DarwinOS e ao SDK da Apple como `platform.UiKit` como `UIViewController`, 
componentes do `platform.Foundation` como `NSBundle`, `NSFileManager`, `NSError`, etc.  

O código a seguir, implementamos um Logger em KMP de forma totalmente nativa e sem depêndencias externas. Ou seja, apenas com os SDKs nativos:
```kotlin
commonMain.dependencies {
    // vazio 
}
androidMain.dependencies {
    // vazio
}
appleMain.dependencies {
    // vazio
}
```
```kotlin
// src/commonMain/Logger.kt

interface Logger {
    fun e(message: String, error: Throwable)
}
```
```kotlin
// src/androidMain/Logger.android.kt

import android.util.Log
 
class AndroidLogger : Logger {
    override fun e(message: String, error: Throwable) {
        Log.e("TAG", message)
        error.printStackTrace()
    }
}
```
```kotlin
// src/appleMain/Exemplo.apple.kt

import kotlinx.cinterop.ptr
import platform.darwin.OS_LOG_DEFAULT
import platform.darwin.OS_LOG_TYPE_ERROR
import platform.darwin.__dso_handle
import platform.darwin._os_log_internal

class DarwinLogger : Logger {
    override fun e(message: String, error: Throwable) {
        _os_log_internal(
            __dso_handle.ptr,
            OS_LOG_DEFAULT,
            OS_LOG_TYPE_ERROR,
            "%s",
            message
        )
        error.printStackTrace()
    }
}
```

## Entendendo como as depêndencias no KMP funcionam
Vamos partir do `build.gradle.kts` a seguir, onde aplicamos o [ktor-client](`https://ktor.io/docs/getting-started-ktor-client-multiplatform-mobile.html`) e declaramos as depêndencias.
```kotlin
kotlin {
    androidTarget()
    
    jvm("desktop")
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    macosArm64()
    tvosArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
        }
    }
}
```

Ao sincronizar o projeto, observamos que uma série de depêndencias foram incluídas.

A imagem a seguir representa apenas uma parte dessas depêndencias:

![Dependencia com todos os source sets](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-all-targets-imported.png?raw=true)

Ao declarar os targets e importar uma depêndencia no `commonMain` essas depêndencias são importadas no projeto.

Se removêssemos alguns targets do nosso `build.gradle.kts` e sincronizar o projeto, observamos que as depêndencias específicas de cada source set sumiram:
```kotlin
// removidos:
watchosArm32()
watchosArm64()
watchosSimulatorArm64()
macosArm64()
tvosArm64()
```
![Dependencia com alguns dos source sets](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-limited-imports.png?raw=true)

Ou seja, cada target declarado espera que uma depêndencia exista, seja ela publicada em algum artefato como Maven, ou depêndencia de um módulo interno.

### Relação entre depêndencias externas e os targets do módulo
Para utilizar uma depêndencia um source set, é obrigatório que essa depêndencia exista para o target em específico.

Por exemplo, para você declarar depêndencias no `commonMain`, um artefato (interno ou externo) específico para o common main deve existir.

O mesmo se aplica para os outros targets. Por exemplo, se você declara o `watchosArm32()` como target, e seu módulo interno ou biblioteca não possuem esses alvos declarados, você recebe um erro.

Vamos entender melhor as peculiaridades das depêndencias de cada Source Set

### Common
A `commonMain` funciona de forma singular em relação aos outros Source Sets. No momento da compilação, ela funciona 
apenas como `metadata` (termo muito utilizado internamente para a `commonMain`), ou seja, não é compilado diretamente
em código executável para uma plataforma específica, mas sim em um formato intermediário que contém metadados. 

Estes metadados são então usados pelos backends do Kotlin (Kotlin/JVM, Kotlin/Native, Kotlin/JS, Kotlin/WASM) 
específica para gerar o código executável correspondente para cada plataforma.  

#### Dissecando a depêndencia `commonMain`
Ao explorar o conteúdo dessa depêndencia, notamos uma extensão especial do KMP: a `.klib`. 

![Dependencia do ktor client common](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-ktor-client-common-klib.png?raw=true)

O arquivo `.klib` no KMP é uma biblioteca que contém código compartilhável entre diferentes plataformas. 
No contexto do `commonMain`, o `.klib` funciona como uma coleção de código-fonte e recursos que podem ser compilados 
para várias plataformas utilizando os diferentes backends.

Se expandirmos a pasta `linkdata`, vamos nos deparar com outro formato de arquivo especial do KMP: `.knm`

![Dependencia do ktor client common](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-ktor-client-common-knm.png?raw=true)

O formato de arquivo `.knm` é um formato binário utilizado internamente pelas 
bibliotecas `klib` do Kotlin/Native, especialmente em conjunto com a ferramenta `cinterop`. 

Esse formato contém metadados e informações que o compilador do Kotlin usa para compilar e interligar bibliotecas nativas.
Os arquivos `.knm` são detalhes de implementação para facilitar a interoperabilidade e a criação de bibliotecas no contexto do Kotlin/Native.

O último arquivo é o `manifest`:
```
unique_name=ktor-client-core_commonMain
compiler_version=1.8.22
abi_version=1.7.0
metadata_version=1.4.1
```

Esse arquivo contém metadados sobre a própria biblioteca. Isso inclui informações como a versão da biblioteca, 
dependências necessárias, e outros metadados usados pelo sistema de build e pelo compilador para entender como integrar 
e usar a biblioteca no projeto. 

Cada `.klib` tem um manifesto que descreve seu conteúdo e como ele deve ser tratado durante a compilação e o link de execução.

### Dissecando a depêndencia do iOS
Dependendo de quais plataforma Apple você inclui no seu Source Set, uma depêndencia diferente é importada no projeto.

Note que, além dos Source Sets declarados no nosso `build.gradle.kts`, também existe a depêndencia `posix`. 

A dependência "posix" em um contexto de Kotlin Multiplatform para iOS se refere a interfaces de programação de aplicativos
para sistemas operacionais compatíveis com POSIX (Portable Operating System Interface), um conjunto de padrões especificados
pela IEEE para manter a compatibilidade entre sistemas operacionais.
No caso de iOS, `posixMain` indica que essa biblioteca está usando APIs POSIX, comuns em sistemas baseados em Unix, como o iOS.

![Dependencia do iOS no projeto](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-ktor-client-ios-imports.png?raw=true)

#### Explorando arquivos do `.klib` do iOS
Ao analisarmos o conteúdo da `.klib` de um target iOS, verificamos uma estrutura similar ao `commonMain`, porém com uma pasta `ir` 
e outra `targets.ios_X`.

A pasta `ir` representa diferentes componentes do código e metadados compilados:
- `bodies.knb`: Contém os corpos das funções compiladas.
- `debugInfo.knd`: Informações de depuração que permitem o rastreamento de erros e a inspeção do código durante o desenvolvimento e a depuração.
- `files.knf`: Lista dos arquivos de origem compilados na biblioteca.
- `irDeclarations.knd`: Declarações intermediárias da Representação Intermediária (IR) que o compilador utiliza para compilar o código Kotlin.
- `signatures.knt`: Assinaturas das funções e tipos na biblioteca, usadas para identificação única dentro do código compilado.
- `strings.knt`: Strings literais usadas no código da biblioteca.
- `types.knt`: Informações sobre os tipos usados na biblioteca, como classes, interfaces e tipos primitivos.

A pasta `targets.ios_X` não possuí nenhum conteúdo nesse caso. Mas, nessa pasta reside arquivos de "bitcode" LLVM, que 
contém código intermediário utilizado pelo compilador LLVM.

![Dependencia do iosarm64 no projeto](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-ktor-client-iosarm64-klib.png?raw=true)

### Dissecando a depêndencia do JS
Já que estamos aqui, vamos dar uma olhada em como fica a depêndencia do JS no projeto.

Note que ainda é um arquivo `.klib`, mas com um `package.json` declarado.

![Dependencia do JS](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-ktor-client-js-include-klib.png?raw=true)

### Dissecando a depêndencia do Android
No caso do Android e JVM, a depêndencia não é um `.klib`, mas sim um `.jar` convencional do mundo JVM.

Nesse caso, observamos um formato de `.jar` normal de qualquer programa em Java/Kotlin.

Note que essa depêndencia é utilizada tanto pelo Source Set `android` quanto ao `desktop`:

![Dependencia do Android e JVM](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-ktor-client-jvm-jar.png?raw=true)

### Como descobri se uma biblioteca open-source é compatível com meu target?

O jeito mais fácil é verificar onde essa biblioteca está hospedada, e verificar quais artefatos estão disponíveis.

No caso do `ktor-client-core`, ao acessar o Maven Central e pesquisar pelo grupo, encontramos uma lista de artefatos para cada source set.

![Demo em todas as plataformas](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-maven-ktor-ezgif.com-video-to-gif-converter.gif?raw=true)

## Depêndencias de módulos internos
Agora que aprendemos sobre como são as depêndencias externas do Kotlin, chegou a hora de falarmos sobre as depêndencias 
internas, ou seja, de módulos diferentes.

Vamos supor que o módulo `:bar` quer consumir o módulo `:foo`. Note que o módulo `:foo` possuí os mesmos targets do `:bar` + o `js()`.

Nesse caso, o `:bar` consegue consumir o `:foo` já que o `:foo` compila para o target que o `:bar` precisa.

Agora, o contrário já não é possível: o módulo `:foo` espera um target `js()` que o módulo `:bar` não oferece! Nesse caso, há um erro de compilação.
```kotlin
// :bar build.gradle.kts
kotlin {
    androidTarget()
    iosARM64()
}

// :foo build.gradle.kts
kotlin {
    androidTarget()
    iosARM64()
    js()
}
```



## Conclusões


Até a próxima!

---

> 🤖 Artigo foi escrito com o auxílio do ChatGPT 4, utilizando o plugin Web.
>
> As fontes e o conteúdo são revisados para garantir a relevância das informações fornecidas, assim como as fontes utilizadas em cada prompt.
>
> No entanto, caso encontre alguma informação incorreta ou acredite que algum crédito está faltando, por favor, entre em contato!

---

> Referências
> - [Rules for expected and actual declarations](https://kotlinlang.org/docs/multiplatform-expect-actual.html)
> https://slack-chats.kotlinlang.org/t/5013792/u02k3a6e6kd-i-have-some-questions-about-the-knm-kotlin-nativ
