## Aprendendo sobre como interpretar e utilizar depêndencias no KMP

Nos artigos anteriores, estabelecemos uma base sobre o Kotlin Multiplatform (KMP) e como ele compila para múltiplas plataformas. 

Neste artigo, vamos explorar o uso de bibliotecas _open-source_, compreender sua aplicabilidade em nossos projetos e, por fim, sua implementação.

---

## Depêndencias e os Source Sets
Descobrimos que o Kotlin utiliza uma estrutura de _source sets_ para gerenciar as compilações distintas.

Cada _source set_ no Kotlin, seja `commonMain` ou específicos como `androidMain`, `native/ios`, `desktop`, `js`, pode declarar dependências usadas exclusivamente nesse contexto.

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
Cada _source set_ do Kotlin se torna um ambiente isolado, com acesso a APIs e SDKs específicos da plataforma. 

Por exemplo, no _source set_ do Android, você tem acesso ao Android SDK; no iOS, ao DarwinOS e ao SDK da Apple como 
`platform.UiKit` e componentes do `platform.Foundation`.

Implementamos abaixo um Logger em KMP de forma totalmente nativa, sem dependências externas, usando apenas os SDKs nativos:

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
Considere o `build.gradle.kts` com o [ktor-client](https://ktor.io/docs/getting-started-ktor-client-multiplatform-mobile.html) 
aplicado e dependências declaradas. Ao sincronizar o projeto, dependências são incluídas conforme os _targets_ especificados.

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

A imagem a seguir representa apenas uma parte dessas depêndencias:

![Dependencia com todos os source sets](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-all-targets-imported.png?raw=true)

Ao declarar os _targets_ e importar uma depêndencia no `commonMain` todas essas depêndencias são importadas no projeto.

Se removêssemos alguns _targets_ do nosso `build.gradle.kts` e sincronizar o projeto, observamos que as depêndencias específicas de cada source set sumiram:
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
apenas como `metadata`, ou seja, não é compilado diretamente em código executável para uma plataforma específica, 
mas sim em um formato intermediário que contém metadados. 

Estes metadados são então usados pelos backends do Kotlin específica para gerar o código executável correspondente 
para cada plataforma.  

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

O último arquivo é o `manifest`. Esse arquivo contém metadados sobre a própria biblioteca. Isso inclui informações como a versão da biblioteca, 
dependências necessárias, e outros metadados usados pelo sistema de build e pelo compilador para entender como integrar 
e usar a biblioteca no projeto. Cada `.klib` tem um manifesto que descreve seu conteúdo e como ele deve ser tratado durante a compilação e o link de execução.

### Dissecando a depêndencia do iOS
Dependendo de quais plataforma Apple você inclui no seu Source Set, uma depêndencia diferente é importada no projeto.

Note que, além dos Source Sets declarados no nosso `build.gradle.kts`, também existe a depêndencia `posix`. 

A dependência "posix" em um contexto de Kotlin Multiplatform para iOS se refere a interfaces de programação de aplicativos
para sistemas operacionais compatíveis com POSIX (Portable Operating System Interface), 

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
Para o _target_ JS, ainda temos um arquivo `.klib`, mas acompanhado de um `package.json`.

![Dependencia do JS](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-ktor-client-js-include-klib.png?raw=true)

### Dissecando a depêndencia do Android
No caso do Android e JVM, a depêndencia não é um `.klib`, mas sim um `.jar` convencional do mundo JVM.

Nesse caso, observamos um formato de `.jar` normal de qualquer programa em Java/Kotlin.

Note que essa depêndencia é utilizada tanto pelo Source Set `android` quanto ao `desktop`:

![Dependencia do Android e JVM](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-ktor-client-jvm-jar.png?raw=true)

### Como descobri se uma biblioteca open-source é compatível com meu target?
Para verificar a compatibilidade de uma biblioteca _open-source_ com um _target_, é recomendável consultar onde a 
biblioteca está hospedada e quais artefatos estão disponíveis. Você também pode analisar o `build.gradle.kts` da biblioteca,
e verificar quais _targets_ aquela biblioteca compila. 

No caso do `ktor-client-core`, ao acessar o [Maven Central](https://mvnrepository.com/search?q=ktor-client-core) e pesquisar pelo grupo, encontramos uma lista de artefatos para cada source set.

![Demo em todas as plataformas](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/kmp-maven-ktor-ezgif.com-video-to-gif-converter.gif?raw=true)

## Depêndencias de módulos internos
Para módulos internos, é essencial que o módulo consumidor tenha _targets_ compatíveis com o módulo consumido.

Vamos supor que o módulo `:shared1` quer consumir o módulo `:shared2`. Note que o módulo `:shared2` possuí os mesmos targets do `:shared1` + o `js()`.

Nesse caso, o `:shared1` consegue consumir o `:shared2` já que o `:shared2` compila para o target que o `:shared1` precisa.

Agora, o contrário já não é possível: o módulo `:shared2` espera um target `js()` que o módulo `:shared1` não oferece! Nesse caso, há um erro de compilação.
```kotlin
// :shared1 build.gradle.kts
kotlin {
    androidTarget()
    iosARM64()
}

// :shared2 build.gradle.kts
kotlin {
    androidTarget()
    iosARM64()
    js()
}
```

## Conclusões
Compreender o funcionamento das dependências internas e externas no Kotlin Multiplatform (KMP) é crucial, pois isso nos 
ajuda a selecionar bibliotecas que atendam às necessidades de nossos projetos.

Neste artigo, exploramos mais profundamente as "entranhas" dessas dependências e como a declaração dos _targets_ em 
nossa aplicação influencia as dependências incluídas no projeto.

Além disso, aprofundamo-nos nos conceitos de `.klib` e `.knm`. Embora não afetem nosso dia a dia de desenvolvimento de 
forma significativa, essas peças são essenciais para entender como o KMP realiza sua "mágica".

## Fim da série KMP101!
É com grande satisfação que concluímos esta fundação no KMP! 

Espero que o conhecimento adquirido sirva como um trampolim para que você possa explorar e navegar pelo mundo do KMP com confiança.

Fique atento para a série KMP102, onde mergulharemos ainda mais em implementações, arquitetura, testes, 
interoperabilidade com Swift e outras linguagens, e muito mais!

Um abraço e até a próxima!

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
