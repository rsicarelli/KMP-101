## Aprendendo sobre como interpretar e utilizar depêndencias no KMP

Nos últimos artigos, criamos uma fundação de conhecimento sobre o Kotlin Multiplataforma e compreendemos como o Kotlin compila para múltiplas plataformas. 

Nesse artigo, vamos explorar sobre a utilização de bibliotecas de código livre (open-source), como entender se é possível utilizar no seu projeto, e, finalmente, as utilizando.

---

## Código Kotlin vs Código Nativo
Aprendemos que o Kotlin se utiliza de uma estrutura de Source Sets para orquestrar as diferentes compilações.

Cada source set do Kotlin, seja o `common` ou específicos `android`, `native/ios`, `desktop`, `js`, possuem flexibilidade para declarar apenas as depêndencias utilizadas naquele source set.

Exemplo:
```kotlin
commonMain.dependencies {
    // compartilhado por todos os source sets 
}
androidMain.dependencies {
    // common + Android }
}
appleMain.dependencies {
    // common + família Apple }
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
public interface Logger {
    public fun e(message: String, error: Throwable)
}
```
```kotlin
// src/androidMain/Logger.android.kt
import android.util.Log

public class AndroidLogger : Logger {
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

public class DarwinLogger : Logger {
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
Cada Source Set do Kotlin impõe diferentes aspectos e peculiaridades. Vamos explorar brevemente cada um dos source sets e suas restrições do ambiente

### Common
A `commonMain` funciona de forma singular em relação aos outros Source Sets. No momento da compilação, ela funciona 
apenas como `metadata` (termo muito utilizado internamente para a `commonMain`), ou seja, não é compilado diretamente
em código executável para uma plataforma específica, mas sim em um formato intermediário que contém metadados. 

Estes metadados são então usados pelos backends do Kotlin (Kotlin/JVM, Kotlin/Native, Kotlin/JS, Kotlin/WASM) 
específica para gerar o código executável correspondente para cada plataforma.  

No `build.gradle.kts` a seguir, vamos aplicar o (https://ktor.io/docs/getting-started-ktor-client-multiplatform-mobile.html)[`ktor-client`] e declarar as depêndencias:  
```kotlin
kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        watchosArm32(),
        watchosArm64(),
        watchosSimulatorArm64(),
        macosArm64(),
        tvosArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
```

Ao sincronizar o projeto, observamos que uma série de depêndencias foram importadas no projeto. 

A imagem a seguir representa apenas uma parte das depêndencias para exemplificar:

![Dependencia com todos os source sets](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-all-targets-imported.png?raw=true)

Se removêssemos alguns targets do nosso `build.gradle.kts` e sincronizar o projeto, observamos que as depêndencias específicas de cada source set sumiram:  
```kotlin
// removidos:
watchosArm32()
watchosArm64()
watchosSimulatorArm64()
macosArm64()
tvosArm64()
```
![Dependencia com alguns dos source sets](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-limited-importes.png?raw=true)

#### Dissecando a depêndencia `commonMain`
Ao explorar o conteúdo dessa depêndencia, notamos uma extensão especial do KMP: a `klib`. 

![Dependencias Common Kotlinx Serialization](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-common-dependency.png?raw=true)

O arquivo `.klib` no KMP é uma biblioteca que contém código compartilhável entre diferentes plataformas. 
No contexto do `commonMain`, o `.klib` funciona como uma coleção de código-fonte e recursos que podem ser compilados 
para várias plataformas utilizando os diferentes backends.

Além do `.klib`, vamos um formato `.knm`. O formato de arquivo `.knm` é um formato binário utilizado internamente pelas 
bibliotecas `klib` do Kotlin/Native, especialmente em conjunto com a ferramenta `cinterop`. 

Esse formato contém metadados e informações que o compilador do Kotlin usa para compilar e interligar bibliotecas nativas.
Os arquivos `.knm` são detalhes de implementação para facilitar a interoperabilidade e a criação de bibliotecas no contexto do Kotlin/Native.

O último arquivo é o `manifest`:
```
unique_name=ktor-serialization-kotlinx-json_commonMain
compiler_version=1.8.22
abi_version=1.7.0
metadata_version=1.4.1
```

Esse arquivo contém metadados sobre a própria biblioteca. Isso inclui informações como a versão da biblioteca, 
dependências necessárias, e outros metadados usados pelo sistema de build e pelo compilador para entender como integrar 
e usar a biblioteca no projeto. 

Cada `.klib` tem um manifesto que descreve seu conteúdo e como ele deve ser tratado durante a compilação e o link de execução.

### Dissecando a depêndencia do iOS
Ao declarar o `kotlinx.serialization` no `commonMain`, trouxemos as depêndencias transitivas dos Source Sets do projeto.

Na imagem a seguir, observamos 4 novas depêndencias:
![Dependencias iOS Kotlinx Serialization](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-ios-imported.png?raw=true)

Você deve estar perguntando, por que trouxemos 4 dependencias, sendo que estamos declarando apenas um único `iOS`?

Uma informação omitida anteriormente foi a definição dos Source Sets na configuração do módulo.

A definição dos Source Sets influenciam em quais alvos nosso módulo KMP irá compilar. Por conta dessa declaração, 
automaticamente recebemos as depêndencias específicas de cada plataforma.

Por exemplo, se removêssemos o `iosArm64()`, apenas depêndencias do `iosArm64` e `iosSimulatorArm64` seriam importadas.

```kotlin
kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "KotlinShared"
        }
    }
}
```

#### Depêndencia "posix"
A dependência "posix" em um contexto de Kotlin Multiplatform para iOS se refere a interfaces de programação de aplicativos 
para sistemas operacionais compatíveis com POSIX (Portable Operating System Interface), um conjunto de padrões especificados 
pela IEEE para manter a compatibilidade entre sistemas operacionais. 

No caso de iOS, `posixMain` indica que a biblioteca `kotlinx-serialization` está usando APIs POSIX, comuns em sistemas 
baseados em Unix, como o iOS.

#### Explorando arquivos do `.klib` do iOS



### Regras impostas pelo compilador do Kotlin
Para garantir a separação completa desses diversos ambientes dos Source Sets, o KMP impõe algumas regras:
- Não é possível utilizar depêndencias de outras plataformas ou Source Sets. Por exemplo, ao tentar incluir uma depêndencia 


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
