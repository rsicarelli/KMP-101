> * [O que é o Gradle?](#o-que-é-o-gradle)
>   * [Por que o Gradle é tão importante no KMP?](#por-que-o-gradle-é-tão-importante-no-kmp)
>   * [Groovy vs Kotlin](#groovy-vs-kotlin)
> * [Dissecando os arquivos Gradle](#dissecando-os-arquivos-gradle)
>   * [O arquivo `settings.gradle.kts` da raíz](#o-arquivo-settingsgradlekts-da-raíz)
>   * [O arquivo `build.gradle.kts` da raíz](#o-arquivo-buildgradlekts-da-raíz)
>   * [O arquivo `build.gradle.kts` do módulo `composeApp`](#o-arquivo-buildgradlekts-do-módulo-composeapp)
>     * [1. Aplicando plugins](#1-aplicando-plugins)
>     * [2. Extensão `kotlin` (aka *KotlinMultiplatformExtension*)](#2-extensão-kotlin-aka-kotlinmultiplatformextension)

>     * [3. Extensão `android` (também conhecida como `BaseAppModuleExtension`)](#3-extensão-android-também-conhecida-como-baseappmoduleextension)
>     * [4. Extensão `compose` (também conhecida como `ComposeExtension`)](#4-extensão-compose-também-conhecida-como-composeextension)
>   * [Outros arquivos Gradle](#outros-arquivos-gradle)
> * [Conclusão](#conclusão)

No último artigo, criamos um projeto utilizando o KMP Wizard e, sem muitos esforços, executamos nosso app em aparelhos Android, iOS e Desktop.

Dessa vez, vamos nos aprofundar em um aspecto fundamental do KMP: o Plugin KMP para Gradle.

---

## O que é o Gradle?

O Gradle é uma ferramenta crucial em projetos Kotlin, sendo um tópico no qual você precisará investir bastante tempo aprendendo, especialmente se não tiver experiência como dev Android.

Pense no Gradle como o equivalente ao NPM/Yarn/Webpack no mundo JavaScript, ou ao CocoaPods/Swift Package Manager no mundo iOS. Utilizaremos a seguinte tabela para comparar essas ferramentas:

| Funcionalidade                | Gradle | NPM      | Webpack | CocoaPods |
|-------------------------------|--------|----------|---------|-----------|
| Gerenciamento de dependências | ✅      | ✅        | ❌       | ✅         |
| Automação de build            | ✅      | ❌        | ✅       | ❌         |
| Execução de scripts           | ✅      | ✅        | ✅       | ✅         |
| Customização de builds        | ✅      | Limitada | ✅       | Limitada  |
| Gestão de repositórios        | ✅      | ✅        | ❌       | ✅         |
| Plug-ins e extensões          | ✅      | ✅        | ✅       | ✅         |
| Pacotes distribuíveis         | ✅      | ✅        | ✅       | ✅         |

### Por que o Gradle é tão importante no KMP?

Um dos pilares do Kotlin Multiplatform Project (KMP) é a integração profunda com o Gradle, por meio do uso do [Plugin KMP](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.multiplatform). O KMP utiliza extensivamente o Gradle para gerenciar diversos aspectos antes, durante e após o processo de desenvolvimento. O Gradle não apenas facilita a configuração do projeto, mas também oferece tarefas especializadas que auxiliam na integração de módulos compartilhados do KMP com aplicativos iOS, por exemplo.

O Plugin do KMP se encarrega de, por exemplo, vincular o projeto Xcode e KMP, além de oferecer recursos mais específicos do Xcode como o uso do `XFCFramework` para geração do distribuível.   

Além da integração com o Xcode/Apple, o Plugin do KMP oferece uma ampla gama de integrações com outras plataformas, como o uso do ***Webpack*** para projetos JS. Tudo é orquestrado e executado pelo Gradle.

### Groovy vs Kotlin

A linguagem original do Gradle é o Groovy. Porém, atualmente, a comunidade Kotlin utiliza o [Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html), que permite manipular o Gradle por meio do Kotlin.

É importante notar que:
- Arquivos `.gradle` são escritos em Groovy.
- Arquivos `.gradle.kts` são em Kotlin, utilizando o Kotlin DSL.

### Recado para iniciantes em Gradle

Recomendo fortemente que faça uma pausa na leitura e pesquise mais sobre o básico do Gradle. Esse conhecimento vai te auxiliar a compreender os próximos conceitos!

- [🔗 Começando com o Gradle: Tasks e comandos básicos | #AluraMais com o Alex Felipe](https://www.youtube.com/watch?v=uX6Ezf73OEY)
- [Getting Started with the Gradle Kotlin DSL com o Paul Merlin e Rodrigo B. de Oliveira](https://www.youtube.com/watch?v=KN-_q3ss4l0)

## Dissecando os arquivos Gradle

Assumindo que você tenha compreendido alguns aspectos-chave do Gradle, vamos analisar os arquivos mais importantes do projeto que criamos no [artigo anterior](https://dev.to/rsicarelli/kmp-101-criando-e-executando-seu-primeiro-projeto-multiplataforma-no-fleet-4ep7).

```
.
├── .gradle
├── composeApp
│   ├── build
│   └── build.gradle.kts       
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
└── settings.gradle.kt
```

### O arquivo `settings.gradle.kts` da raíz

Esse arquivo é um componente do Gradle cuja responsabilidade é definir as configurações globais do projeto, como módulos e subprojetos, além da configuração de repositórios e dependências do projeto global.

```kotlin
// Define o nome do projeto
rootProject.name = "KMP101"

// Forma de habilitar funcionalidades do Gradle. Neste caso, o "type safe project accessors"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Esse bloco inicia uma configuração dos plugins que os módulos do projeto irão compartilhar
pluginManagement {

    // Todo módulo do projeto poderá utilizar plugins desses repositórios
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

// Esse bloco inicia uma configuração das dependências que os módulos do projeto irão compartilhar
dependencyResolutionManagement {

    // Todo módulo do projeto poderá utilizar dependências desses repositórios
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

// A função `include(String)` "pluga" um módulo ao projeto
// Faz com que o arquivo `build.gradle.kts` do módulo do projeto seja executado 
include(":composeApp")
```

### O arquivo `build.gradle.kts` da raíz

O papel principal do arquivo `build.gradle.kts` da raiz do projeto é definir a configuração de build de todo o projeto. É através desse arquivo que declaramos quais plugins os outros módulos poderão utilizar, assim como outras configurações de build que são aplicáveis a todo o projeto.

Note o padrão `apply false`. Essa anotação se torna necessária para evitar que esses plugins sejam carregados múltiplas vezes por cada subprojeto. Por exemplo, sem esse `apply false`, estaríamos não só registrando, **mas também aplicando** o plugin específico para todos os módulos.
```kotlin
plugins {
    // Registra o plugin do Compose Multiplatform
    alias(libs.plugins.jetbrainsCompose) apply false
    // Registra o plugin de "application" do AGP (Android Gradle Plugin)
    alias(libs.plugins.androidApplication) apply false
    // Registra o plugin de "library" do AGP (Android Gradle Plugin)
    alias(libs.plugins.androidLibrary) apply false
    // Registra o plugin do KMP 
    alias(libs.plugins.kotlinMultiplatform) apply false
}
```

> Se perguntando o que é esse `libs`? 
> 
> [🔗 Confira meu artigo sobre o catalogo de versão (version catalog) do Gradle](https://dev.to/rsicarelli/android-plataforma-parte-6-version-catalog-59ob)
> 

### O arquivo `build.gradle.kts` do módulo `composeApp`
É aqui que as configurações específicas acontecem. O arquivo `build.gradle.kts` de um módulo Gradle aplica configurações locais apenas no módulo específico.

Vamos dividir o `build.gradle.kts` desse módulo em algumas partes e analisar cada uma delas.

#### 1. Aplicando plugins
No arquivo `build.gradle.kts` da raiz, registramos nossos plugins. Agora, vamos aplicá-los no nosso projeto.

```kotlin
plugins {
    // Habilita a extensão "kotlin" neste arquivo
    alias(libs.plugins.kotlinMultiplatform)

    // Habilita a extensão "android" neste arquivo
    alias(libs.plugins.androidApplication)

    // Habilita a extensão "compose" neste arquivo
    alias(libs.plugins.jetbrainsCompose)
}
```

#### 2. Extensão `kotlin` (aka [*KotlinMultiplatformExtension*](https://github.com/JetBrains/kotlin/blob/c4fe7e44534a5412463acf6bba0da9f5bf8f9cb3/libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/dsl/KotlinMultiplatformExtension.kt))

Bem-vindo à porta de entrada do KMP. Esta extensão permite declarar plataformas e configurações específicas de compilação. As principais responsabilidades são:

1. Definir os alvos (*targets*) do módulo
2. Estabelecer os *source sets* do módulo
3. Determinar as dependências comuns e específicas dos *source sets*

##### 2.1: Definindo os alvos do módulo `composeApp`
Inicialmente, especificamos quais alvos o módulo compilará e algumas configurações pontuais.

```kotlin
kotlin {
    // Instrui o plugin a adicionar o Android como alvo
    androidTarget {
        // Instrui qual versão da JVM seu app Android irá utilizar
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    // Instrui o plugin a adicionar Desktop como alvo
    jvm("desktop")

    // Instrui o plugin a adicionar o iOS como alvo
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget -> //do tipo KotlinNativeTarget
        // É necessário realizar uma configuração básica do framework
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    ..
}
```

##### 2.2 Definindo os *source sets*
O próximo passo é definir os *source sets* do projeto e as dependências necessárias em cada um.

Importante ressaltar o seguinte:
1. O `androidMain` e `commonMain` estão pre-definidos através da classe `KotlinMultiplatformSourceSetConventions`. Isso remove a necessidade de manualmente registrar esses source sets
2. O `desktopMain` ainda não possuí uma convenção. Por isso, precisamos criar manualmente.
3. Note não haver um `iosMain` nessa configuração. O motivo é que, por hora, não é necessário nenhuma depêndencia extra pro iOS. Mas, assim como `androidMain` e `commonMain`, temos `iosMain` disponível caso necessário. 

```kotlin
kotlin {
    ..
    
    sourceSets {
        // Define as depêndencias do source set "androidMain". 
        // Essas depêndencias apenas para o Android 
        androidMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
        }

        // Define as depêndencias do source set "commonMain". 
        // Essas depêndencias são compartilhadas entre todos os alvos
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.components.resources)
        }
        
            
        // Cria o source set "desktopMain"
        val desktopMain by getting

        // Define as depêndencias do source set "desktopMain"
        // Essas depêndencias são específicas para o Desktop
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}
```

#### 3. Extensão `android` (também conhecida como `BaseAppModuleExtension`)
Esta configuração é específica para o Android, imposta pelo plugin `androidApplication`. Aqui, definimos caminhos para recursos e manifestos. Em vez de detalhar todo o conteúdo, vamos nos concentrar apenas na parte relacionada ao KMP.

Normalmente, em projetos Android, temos apenas uma pasta `main`, e o Android Gradle Plugin (AGP) não necessita de informações adicionais sobre a localização de recursos específicos do Android, como o `AndroidManifest.xml`.

No entanto, no contexto do KMP, existem múltiplos diretórios `main`, e, atualmente, o AGP não identifica claramente qual deles é específico para o Android.

Para resolver isso, precisamos definir alguns caminhos manualmente:

```kotlin
android {
    ..
    // Informa a localização do AndroidManifest.xml
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    // Informa a localização da pasta `res`
    sourceSets["main"].res.srcDirs("src/androidMain/res")

    // Informa a localização da pasta `resources`.
    // Observe que essa pasta não é exclusiva do Android, portanto, podemos compartilhá-la com o "commonMain"
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    ..
}
```

#### 4. Extensão `compose` (também conhecida como `ComposeExtension`)
Ainda não exploramos profundamente o Compose, mas, já que estamos adotando o modelo do KMP Wizard, vale mencionar brevemente esta extensão.

Ela se torna essencial exclusivamente para configurar a versão desktop do nosso aplicativo:

```kotlin
compose.desktop {
    // Define uma nova aplicação baseada na JVM
    application {
        // Aponta para uma classe interna do source set "desktopMain"
        mainClass = "MainKt"

        // Define as informações sobre o pacote distribuível
        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg, // Mac
                TargetFormat.Msi, // Windows
                TargetFormat.Deb  // Linux
            )
            packageName = "br.com.rsicarelli"
            packageVersion = "1.0.0"
        }
    }
}
```

### Outros arquivos Gradle
Já abordamos os arquivos específicos ao nosso projeto KMP. Outros arquivos, como `gradle.properties` e `libs.versions.toml`, contêm configurações e definições importantes do Gradle e do projeto.

#### Arquivo `gradle.properties` na raiz
Este arquivo contém diversas configurações do Gradle que nos possibilitam realizar algumas alterações mais profundas no nosso projeto.

Nos projetos KMP, existem algumas *flags* importantes a serem declaradas:

```properties
# Habilita o suporte do Compose Multiplatform para iOS. 
org.jetbrains.compose.experimental.uikit.enabled=true

# Define a versão do layout do source set do Android para a nova estrutura introduzida no Kotlin 1.8.0 e padrão no 1.9.0.
kotlin.mpp.androidSourceSetLayoutVersion=2

# Habilita a "commonization" de interoperação C no Kotlin Multiplatform.
kotlin.mpp.enableCInteropCommonization=true
```

#### Arquivo `libs.versions.toml` na pasta `gradle`
Este arquivo representa nosso catálogo de bibliotecas, versões e plugins.

> [🔗 Confira meu artigo sobre o catálogo de versões (version catalog) do Gradle](https://dev.to/rsicarelli/android-plataforma-parte-6-version-catalog-59ob)

#### Outros arquivos
Arquivos e pastas como `.gradle`, `gradlew`, `gradlew.bat`, `local.properties`, `.idea` e `.fleet` são gerenciados pelos comandos do Gradle ou pela própria IDE, não havendo configurações específicas do KMP que precisem ser analisadas.


## Conclusão

Com este guia, aprendemos aspectos cruciais do Gradle em projetos KMP.  

A capacidade de gerenciar eficientemente as dependências, definir caminhos para recursos e manifestos, e configurar extensões específicas é crucial para o dia a dia como dev KMP. Além disso, a compreensão dos arquivos Gradle, como `gradle.properties` e `libs.versions.toml`, é fundamental para manter seu projeto atualizado e alinhado com as melhores práticas da indústria.

A meu ver, o Gradle em projetos KMP não é apenas uma habilidade técnica; é um ativo estratégico que potencializa o desenvolvimento de aplicações robustas e adaptáveis em várias plataformas. À medida que o KMP continua a evoluir, o conhecimento adquirido aqui será uma base sólida para explorar novas funcionalidades e integrar tecnologias emergentes em seus projetos.

No próximo artigo, vamos finalmente mexer em código Kotlin, aprendendo uma característica essencial do KMP: `expect` e `actual`.

Até a próxima!

---

> 🤖 Artigo foi escrito com o auxílio do ChatGPT 4, utilizando o plugin Web.
>
> As fontes e o conteúdo são revisados para garantir a relevância das informações fornecidas, assim como as fontes utilizadas em cada prompt.
>
> No entanto, caso encontre alguma informação incorreta ou acredite que algum crédito está faltando, por favor, entre em contato!

---

> Referências
> - [Gradle vs. Other Build Tools - unrepo.com](https://www.unrepo.com)
> - [Gradle vs. npm - Gradle Hero](https://gradlehero.com)
> - [Webpack Comparison - webpack.js.org](https://webpack.js.org/comparison/)
> - [Multiplatform Gradle Plugin Improved for Connecting KMM Modules | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2021/07/multiplatform-gradle-plugin-improved-for-connecting-kmm-modules/)
> - [Compose Multiplatform for iOS Is in Alpha | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2023/02/compose-multiplatform-for-ios-is-in-alpha/)
> - [Android source set layout | Kotlin Documentation](https://kotlinlang.org/docs/mpp-android-source-set-layout.html)
