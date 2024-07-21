## KMP-102 - Utilizando o XCFramework no Xcode

No post anterior, aprendemos sobre como o Kotlin/Native exporta uma coleção de `.frameworks` no formato XCFramework.

Agora, vamos entender as características desse XCFramework.

## Entendendo quando utilizar um XCFramework no iOS
O pacote XCFramework irá oferecer um `.framework` para cada Kotlin Native target. Lá dentro, alvos como o device físico (`iosArm64`), simulador (`iosSimulatorArm64`) e simuladores para processadores intel (`iosX`) estarão presentes.

Consumir um `.framework` varia conforme o ambiente e o codebase existente, mas de forma geral, basta criar um _build phase_ no projeto Xcode para conseguir utilizar o import das classes exportadas pelo Kotlin/Native.

- :link: (Utilizando o Swift Package Manager)[https://kotlinlang.org/docs/native-spm.html]
- :link: (CocoaPods overview and setup)[https://kotlinlang.org/docs/native-cocoapods.html]
- :link: (Kotlin/Native as an Apple framework – tutorial)[https://kotlinlang.org/docs/apple-framework.html]

Existem diversas formas que podemos utilizar para importar no projeto.

Todos esses modelos possuem características importantes a serem exploradas.

## Entendendo como o XCFramework é gerado

Atualmente no KMP, um `.framework` é "fat". Esse termo é muito bem explicado (nesse artigo)[https://dzone.com/articles/the-skinny-on-fat-thin-hollow-and-uber]:

- **Skinny** - Contém APENAS as partes que você literalmente digita no editor de código, e NADA mais.
- **Thin** - Contém todos os itens acima MAIS as dependências diretas do seu aplicativo (drivers de banco de dados, bibliotecas de utilitários etc.).
- **Hollow** - O inverso do _Thin_ - Contém apenas os bits necessários para executar seu aplicativo, mas NÃO contém o aplicativo em si.
- **Fat/Uber** - Contém a parte que você literalmente escreve, MAIS as dependências diretas do seu aplicativo, MAIS as partes necessárias para executar o aplicativo "por conta própria".

Ou seja, toda e qualquer dependência que você utilizar será empacotada em um único `.framework`.

E isso impõe um desafio para modularizar nossas distribuições, nos forçando a unificar todo código escrito em KMP em uma única exportação.

Vamos entender melhor esse cenário.

## Contexto sobre aplicações Kotlin.

Projetos Kotlin possuem uma natureza multi modular para reutilização de cache e desempenho de build. Modularizar projetos influenciam positivamente a experiência de desenvolvimento em projetos Kotlin que utilizam o Gradle.

[Nesse artigo](https://dev.to/rsicarelli/android-plataforma-parte-1-modularizacao-2016) eu exploro um pouco mais sobre modularização em projetos Android, que também se aplicam para projetos KMP.

Projetos Kotlin costumam a ter múltiplos módulos como:
```
- legado
- core/design-system
- core/logging
- core/analytics
- feature1
- feature2
```

Esses módulos podem ser utilizados individualmente em projetos Kotlin, mas isso não significa que podemos ter um `.framework` para correspondente.

Quer dizer, até podemos, porém, tem uma característica a ser observada.

Considere que a `feature1` e `feature2` utilizam as seguintes dependências em KMP:
```kotlin
// feature1
kotlinx-serialization
kotlinx-coroutines

// feature2
kotlinx-serialization
kotlinx-coroutines
```

Ao exportar o XCFramework, as dependencias do `kotlinx-serialization` e `kotlinx-coroutines` **estariam duplicadas em cada `.framework`**, causando:

- Aumento do pacote final (`.ipa`);
- Aumento de tempo de build, considerando uma escala de módulos.

Isso acontece por uma característica imposta pelo `.framework` no iOS: um `.framework` não consegue se comunicar com o outro. 

Em um cenário ideal, o `kotlinx-serialization` seria um `.framework` isolado e nosso `.framework` se comunicasse com esse `.framework`.

Então, esse modelo "fat" se torna uma característica adotada em projetos KMP, como uma forma de otimização do uso e redução do tamanho final do aplicativo.

Com isso, vamos avançar e entender melhor quais desafios esse modelo impõe.

## Utilizando um "fat" KMP no iOS
Considere um projeto iOS existente e queremos integrar código KMP.

Precisamos de uma forma de exportar todas as nossas depêndencias do projeto e compilar em um único XCFramework, algo como:

```kotlin
kotlin {
    val xcFramework = XCFramework(xcFrameworkName = "KotlinShared")
    
    val exportedDependencies = listOf(feature1, feature2, core)

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "KotlinShared"
            
            exportedDependencies.forEach { dependency ->
                export(dependency.get())
            }

            xcFramework.add(this)
        }
    }
}
```

Ao executar a task `assembleKotlinSharedXCFramework`, teremos um pacotão com todos os módulos exportados.

Ou seja, para projetos KMP, precisamos sempre de um módulo **host**, comumente nomeado `ios-interop`, que exporta todas as dependências do projeto a ser consumida no Xcode.

## Desafios para modularizar o KMP
O modelo "fat" impõe um desafio para modularizar o código KMP. Esse desafio é especialmente presentes em projetos que utilizam o SwiftUI como UI no iOS. 

Para entender melhor esse desafio, vamos assumir que a `feature1` e `feature2` expõem as seguintes classes a serem consumidas no iOS:
```kotlin
class Feature1ViewModel(
    val repository: Feature1Repository
) {
    fun fetch() = Unit
}

class Feature2ViewModel(
    val repository: Feature2Repository
) {
    fun fetch() = Unit
}
```

Ao exportar o XCFramework, todas as classes de `feature1` e `feature2` estarão presentes no `.framework`, ou seja, conseguimos utilizar ambas `Feature1ViewModel` e `Feature2ViewModel` no iOS.

```swift
import KotlinShared

class Feature1ViewModelWrapper {
    private let viewModel: KotlinSharedFeature1ViewModel

    init(repository: Feature1Repository) {
        self.viewModel = KotlinSharedFeature1ViewModel(repository: repository)
    }

    func fetch() {
        viewModel.fetch()
    }
}

class Feature2ViewModelWrapper {
    private let viewModel: KotlinSharedFeature2ViewModel

    init(repository: Feature2Repository) {
        self.viewModel = KotlinSharedFeature2ViewModel(repository: repository)
    }

    func fetch() {
        viewModel.fetch()
    }
}
```

Até aqui, tudo certo. Nosso código KMP foi integrado no iOS com sucesso e vamos assumir que esse código já está até em produção. Agora, vamos adicionar um novo parametro no `Feature1ViewModel`:

```kotlin
class Feature1ViewModel(
    val repository: Feature1Repository,
    val repository2: Feature1Repository2
) {
    fun fetch() = Unit
    fun fetchRepository2() = Unit
}
```

Ao exportar o XCFramework, **o código no iOS irá quebrar**, pois a classe `Feature1ViewModelWrapper` não possui o novo parâmetro `repository2`:

```swift
class Feature1ViewModelWrapper {
    private let viewModel: KotlinSharedFeature1ViewModel

    init(repository: Feature1Repository) {
        self.viewModel = KotlinSharedFeature1ViewModel(repository: repository) //irá quebrar, `repository2` não está sendo enviado
    }

    func fetch() {
        viewModel.fetch()
    }
}
```

Agora, vamos assumir que esse XCFramework já foi gerado e exportado, porém ainda não foi integrado no repositório do iOS. O time responsável pela `feature2` precisa de uma nova funcionalidade e também precisa realizar uma alteração na `Feature2ViewModel`:


```kotlin
class Feature2ViewModel(
    val repository: Feature2Repository,
    val repository2: Feature2Repository2,
) {
    fun fetch() = Unit
}
```

Ao exportar o XCFramework, **o código no iOS irá quebrar**, pelo mesmo motivo acima, já que a classe `Feature2ViewModelWrapper` não possui o novo parâmetro `repository2`:

```swift
class Feature2ViewModelWrapper {
    private let viewModel: KotlinSharedFeature2ViewModel

    init(repository: Feature2Repository) {
        self.viewModel = KotlinSharedFeature2ViewModel(repository: repository) //irá quebrar, `repository2` não foi passado como parametro
    }

    func fetch() {
        viewModel.fetch()
    }
}
```

Agregando esse cenário acima, temos a seguinte linha do tempo:
1. `Feature1ViewModel` e `Feature2ViewModel` estão integradas no projeto iOS.
2. `Feature1ViewModel` adiciona um parâmetro, causando uma quebra no iOS.
3. Após o merge, uma nova versão do `XFramewok` é gerada e publicada (Swift Package Manager, CocoaPods, hash de commit, etc.).
4. Essa versão contém a alteração da `Feature1ViewModel` e, consequentemente, a quebra no iOS.
5. Essa versão ainda não foi integrada no projeto iOS e, simultaneamente, o time da `feature2` altera o `Feature2ViewModel`.
6. A nova versão do `XFramewok` é gerada e publicada, contendo a alteração da `Feature2ViewModel` e, consequentemente, a quebra no iOS.
7. Essa nova versão também possúi a alteração da `Feature1ViewModel`.

Nesse cenário, o time responsável pela `feature2` precisa aguardar o time responsável pela `feature1` corrigir a quebra no iOS, para então integrar a versão que corrige a quebra da `feature2`!

Resumindo mais ainda para ficar mais claro:
1. Versão 1.0.0 do XCFramework é integrada no iOS.
2. Versão 1.1.0 contém breaking change da `feature1`.
3. Versão 1.2.0 contém breaking change da `feature2`.
4. Versão 1.2.0 só pode ser integrada no iOS após a integração e correção da `feature1` presentes na versão 1.1.0.

Ou seja, o modelo "fat" do XCFramework impõe um desafio enorme para modularizar o código KMP, pois qualquer alteração em qualquer módulo irá quebrar o código no iOS. Isso impõe um gargalo no desenvolvimento e na entrega de novas funcionalidades, já que existe um acoplamento entre os módulos exportados e, obrigatoriamente, precisamos seguir uma linha do tempo específica para incorporar nosso código KMP no repositório do iOS.

## Dores do desenvolvimento KMP
Como mencionei acima, essa dor é especialmente presente no modelo onde já temos um projeto iOS existente e queremos integrar código KMP nele. Também presente caso o projeto iOS seja desenvolvido em SwiftUI, onde a comunicação entre os módulos é feita de forma mais direta.

Para projetos escritos exclusivamente em CMP (Compose Multiplatform), essa dor é menos presente, pois a comunicação entre os módulos é feita de forma indireta (internamente em código Kotlin) e menos acoplada.

Esse modelo "fat" impõe as seguintes dores para o desenvolvimento KMP:
- Precisamos seguir uma linha do tempo específica para incorporar nosso código KMP no repositório do iOS.
- Qualquer adição ou remoção de atributos, parâmetros, argumentos, funções, etc., irá quebrar o código no iOS.
- Devs precisam aguardar outros devs corrigir o código no iOS para então integrar o código KMP.

Além disso, no dia a dia, essa dor é mais latente. Por exemplo, estamos integrando novas funcionalidades na `main` branch do projeto host KMP (geralmente o projeto Android), e queremos testar no iOS.

Iremos gerar um XCFramework localmente no nosso ambiente para integrar no iOS. Porém, o código no iOS irá quebrar, pois a `main` branch do projeto KMP contém alterações que ainda não foram integradas no iOS!

Isso gera um gargalo enorme no dia a dia, pois temos um desafio enorme de identificar qual time responsável pela quebra e, consequentemente, aguardar a correção para então integrar o código KMP no iOS. Alternativamente, podemos corrigir na nossa branch essa breaking change para conseguirmos avançar e integrar nosso código KMP no iOS. Porém, ainda sim, para mergear na `main` branch do iOS, precisamos aguardar a correção de outros times.

Em times pequenos ou projetos pessoais isso não é um problema, mas isso em escala é definitivamente o maior gargalo do desenvolvimento KMP atualmente.

## Como contornar esse problema
Existe uma estratégia que podemos adotar, porém, ficará para um artigo futuro. Primeiro, precisamos subir a escadinha de conhecimento em KMP em outros conceitos para conseguirmos compreender melhor essa estratégia alternativa.

## Conclusão
É importante sermos realistas e encararmos os problemas reais de uma tecnologia. Às vezes, no calor do "boom" de uma nova tecnologia, deixamos passar alguns aspectos cruciais para escalarmos uma solução, e se não tratarmos esses problemas, podemos ter (teremos!) um gargalo enorme no desenvolvimento. Isso pode gerar um barulho interno no seu time, como pessoas não adotando a tecnologia devido a má experiência de desenvolvimento, e constantes quebras no código causados por outros times em outros contextos.

Entender a natureza do XCFramework é crucial para termos um projeto escalável e saudável, com uma experiência de desenvolvimento de ponta a ponta sem gargalos.

Nos próximos artigos, vamos entender melhor sobre o código que que é exportado para o iOS, algumas características e limitações do código Kotlin > Objective-C e Objective-C > Swift, como escrever nosso código Kotlin para ser idiomático em Swift, e algumas abordagens para melhorarmos a integração Kotlin <--> Swift.

Nos vemos na próxima, tchau! 


### Referências

> https://dzone.com/articles/the-skinny-on-fat-thin-hollow-and-uber



