No último artigo, criamos um projeto utilizando o KMP Wizard, e sem muitos esforços executamos nosso app em aparelhos Android, iOS e Desktop.

Dessa vez, vamos nos aprofundar em um aspecto fundamental do KMP: o Plugin KMP pro Gradle.

---

## O que é o Gradle?

O Gradle é uma ferramenta crucial em projetos Kotlin, e é um tópico onde você irá precisar investir bastante do seu tempo aprendendo, especialmente se você não tiver um background como dev Android.

Pense no Gradle como o NPM/Yarn/Webpack no mundo JavaScript, ou CocoaPods/Swift Package Manager no mundo iOS. Vamos utilizar da seguinte tabela para compararmos um pouco cada ferramenta:

Claro, vou atualizar a sua tabela com as informações que encontrei. Aqui está a tabela revisada com base nas informações obtidas:

| Funcionalidade                | Gradle | NPM      | Webpack | CocoaPods |
|-------------------------------|--------|----------|---------|-----------|
| Gerenciamento de dependências | ✅      | ✅        | ❌       | ✅         | 
| Automação de build            | ✅      | ❌        | ✅       | ❌         |
| Execução de scripts           | ✅      | ✅        | ✅       | ✅         | 
| Customização de builds        | ✅      | Limitada | ✅       | Limitada  | 
| Gestão de repositórios        | ✅      | ✅        | ❌       | ✅         | 
| Plug-ins e extensões          | ✅      | ✅        | ✅       | ✅         | 
| Pacotes distribuíveis         | ✅      | ✅        | ✅       | ✅         | 

Observe que as informações sobre o Swift Package Manager não foram verificadas a tempo, então esses campos estão marcados como "Não Verificado". Se precisar de mais detalhes ou informações adicionais, fico à disposição para ajudar!

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

Recomendo muito dar uma pausa na leitura e pesquisar mais sobre o básico do Gradle. Esse conhecimento vai te auxiliar a entender os próximos conceitos.

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

Esse arquivo é um componente do Gradle cuja responsabilidade são a de definir as configurações globais do projeto como módulos e subprojetos e também a configuração de repositórios e dependências do projeto global.

Vamos dissecar o arquivo `settings.gradle.kts` do nosso projeto:

```kotlin
// Define o nome do projeto
rootProject.name = "KMP101"

// Forma de habilitar funcionalidades do Gradle. Nesse caso o "type safe project accessors" 
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

    // Todo módulo do projeto poderá utilizar dependencias desses repositórios
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

O papel principal do arquivo `build.gradle.kts` da raíz do projeto define a configuração de build de todo o projeto. É através desse arquivo que declaramos quais plugins os outros módulos poderão utilizar, assim como outras configurações de build que são aplicáveis a todo o projeto.

Vamos analisar o `build.gradle.kts` do nosso projeto.

Note o padrão `apply false`. Essa anotação se torna necessária para evitar que esses plugins sejam carregados múltiplas vezes por cada sub-projeto. Por exemplo, sem esse `apply false`, estariamos não só registrando **mas também aplicando** o plugin específico para todos os módulos. 
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

Vamos dividir o `build.gradle.kts` desse módulo em algumas partes, e analisar cada uma delas: 

#### 1. Aplicando plugins
No arquivo `build.gradle.kts` da raíz nós registramos os nossos plugins. Agora é o momento de aplicarmos eles no nosso projeto.

```kotlin

// Note que 
plugins {
    // Habilita a extensão "kotlin" nesse arquivo
    alias(libs.plugins.kotlinMultiplatform)
    
    // Habilita a extensão "android" nesse arquivo
    alias(libs.plugins.androidApplication)
    
    // Habilita a extensão "compose" nesse arquivo
    alias(libs.plugins.jetbrainsCompose)
}
```

#### 2. Extensão `kotlin` (aka [*KotlinMultiplatformExtension*](https://github.com/JetBrains/kotlin/blob/c4fe7e44534a5412463acf6bba0da9f5bf8f9cb3/libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/dsl/KotlinMultiplatformExtension.kt))
Te dou as boas-vindas a porta de entrada ao KMP. É através dessa extensão que, de fato, declaramos nossas plataformas e compilações específicas. As principais responsabilidades desse bloco são:
1. Definir os alvos (*targets*) que seu módulo irá compilar
2. Definir os source sets que seu módulo irá possuir
3. Definir as dependências comuns e específicas dos seus source sets.

##### 2.1: Definindo os alvos do módulo `composeApp`
O primeiro passo na configuração do KMP é dizer ao plugin quais alvos seu módulo irá compilar, assim como algumas configurações pontuais.

Vamos analisar esse bloco:
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
O próximo passo é instruir ao KMP quais são os source sets do seu projeto, assim como quais depêndencias precisamos em cada um dos nossos source sets.

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

#### 3. Extensão `android` (aka `BaseAppModuleExtension`)
Essa configuração é específica para o Android, imposta pelo plugin `androidApplication`. Aqui, existem inúmeras configurações específicas do Android. Ao invés de explicar todo o conteúdo, vamos focar apenas na parte que diz respeito ao KMP.

Normalmente, em projetos Android, temos apenas uma pasta `main` e o AGP não precisa de nenhuma outra informação sobre onde estão alguns recursos específicos do Android, como o `AndroidManifest.xml`.

No mundo KMP, nós temos diversos `main`, e, por hora, o AGP não sabe muito bem qual é android e qual não é.

Para isso, precisamos manualmente definir alguns caminhos:

```kotlin
android {
    ..
    // Informa a localização do AndroidManifest.xml
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    // Informa a localização da pasta `res`
    sourceSets["main"].res.srcDirs("src/androidMain/res")

    // Informa a localização da `resources`.
    // Note que essa pasta não é específica do Android, então podemos compartilhar com o "commonMain"
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    ..
}
```

#### 4. Extensão `compose` (aka `ComposeExtension`)
Nós ainda não falamos muito a fundo sobre Compose, mas já que estamos utilizando o modelo do KMP Wizard, vamos falar brevemente sobre essa extensão.

Ela se torna necessária exclusivamente para configurar a versão desktop do nosso app:

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
Já cobrimos todos os arquivos específicos do nosso projeto KMP. Temos alguns outros arquivos Gradle que precisamos analisarmos:

#### Arquivo `gradle.properties` da raíz
Esse arquivo contém várias configurações do Gradle, incluindo algumas *flags* específicas. É nesse arquivo que realizamos configurações mais profundas nos projetos Gradle.

Nos projetos KMP, existem algumas flags importantes que precisamos declarar nesse arquivo:

```properties
# Habilita o suporte do Compose Multiplatform para iOS. Baseado no Jetpack Compose, permite interoperabilidade 
# com SwiftUI e UIKit, e oferece suporte para tematização consistente em várias plataformas.
org.jetbrains.compose.experimental.uikit.enabled=true

# MPP (Multiplatform Project)
# Define a versão do layout do source set do Android para a nova estrutura introduzida no Kotlin 1.8.0 e padrão no 1.9.0.
# Este layout novo facilita a organização do código e a gestão de testes instrumentados.
kotlin.mpp.androidSourceSetLayoutVersion=2

# Habilita a "commonization" de interoperação C no Kotlin Multiplatform.
kotlin.mpp.enableCInteropCommonization=true
```

#### Arquivo `libs.versions.toml` na pasta `gradle`
Esse arquivo representa nosso catalogo de bibliotecas, versões e plugins. 

> [🔗 Confira meu artigo sobre o catalogo de versão (version catalog) do Gradle](https://dev.to/rsicarelli/android-plataforma-parte-6-version-catalog-59ob)

#### Outros arquivos
Outros arquivos e pastas como `.gradle`, `gradlew`, `gradlew.bat`, `local.properties`, `.idea` e `.fleet` são gerenciados ou pelos comandos do Gradle, ou pela própria IDE, e não há nenhuma configuração específica do KMP que precisamos analisar.


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
- https://github.com/JetBrains/amper/blob/4f2676a8328ddbca7e9b899e59a004a7d2d02197/prototype-implementation/gradle-integration/src/org/jetbrains/amper/gradle/kmpp/KMPPBindingPluginPart.kt#L101
- [Compose Multiplatform for iOS Is in Alpha | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2023/02/compose-multiplatform-for-ios-is-in-alpha/)
- [Android source set layout | Kotlin Documentation](https://kotlinlang.org/docs/mpp-android-source-set-layout.html)
