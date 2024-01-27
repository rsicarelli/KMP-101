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

No `build.gradle.kts` a seguir, vamos aplicar o `kotlinx.serialization` e declarar as depêndencias:  
```kotlin
commonMain.dependencies {
    implementation(libs.kotlinx.serialization.json)
}
iosMain.dependencies {
    // vazio
}
androidMain.dependencies {
    // vazio
}
jvmMain.dependencies {
    // vazio
}
```

Ao sincronizar o projeto, observamos que trouxemos algumas depêndencias para o projeto.

Interessante notar que, automaticamente, trouxemos as depêndencias dos outros Source Sets declarados.

![Dependencias Import Kotlinx Serialization](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-dependencies-imported.png?raw=true)

#### Dissecando a depêndencia `commonMain`
Ao explorar o conteúdo dessa depêndencia, notamos uma extensão especial do KMP: a `klib`. 

![Dependencias Common Kotlinx Serialization](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-common-dependency.png?raw=true)

O arquivo `.klib` no KMP é uma biblioteca que contém código compartilhável entre diferentes plataformas. 
No contexto do `commonMain`, o `.klib` funciona como uma coleção de código-fonte e recursos que podem ser compilados 
para várias plataformas utilizando os diferentes backends.

Além do `.klib`, vamos um formato `.knm`. O formato de arquivo `.knm` é um formato binário utilizado internamente pelas 
bibliotecas `klib` do Kotlin/Native, especialmente em conjunto com a ferramenta `cinterop`. 
Esse formato contém metadados e informações que o compilador do Kotlin usa para compilar e interligar bibliotecas nativas.
O que seja código Kotlin legível por humanos é, na verdade, a saída de uma ferramenta que interpreta e exibe o 
conteúdo binário do arquivo KNM de forma compreensível. Portanto, os arquivos KNM são detalhes de implementação para facilitar a interoperabilidade e a criação de bibliotecas no contexto do Kotlin/Native.



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
