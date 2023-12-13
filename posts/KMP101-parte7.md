## Explorando as palavras reservadas 'actual/expect' no KMP

Nos últimos artigos, focamos mais nos "bastidores" do KMP: paradigma, arquitetura do compilador, os source sets, ambiente de desenvolvimento, criação e execução de um projeto de exemplo, e o papel do Gradle nisso tudo.

Dessa vez, vamos começar iniciar nossa jornada para a superfície do KMP, entendendo sobre as palavras reservadas `actual` e `expect` e seu papel no compartilhamento do código.


---

## Como o KMP possibilita o compartilhamento do código

No artigo [🔗 Dominando os princípios dos Source Sets](https://dev.to/rsicarelli/kotlin-multiplataforma-101-dominando-os-principios-dos-source-sets-4pg), aprendemos que o KMP se utiliza da estrutura de source sets, e que todo source-set específico é descendente do source-set raíz `commonMain`. Todo código Kotlin dentro do source-set `commmonMain` está automagicamente disponível nos source-sets específicos como o `androidMain`, `appleMain`, etc.

O código Kotlin compartilhado no `commonMain` pode ser:

1. Genérico o bastante que conseguimos resolver apenas com o Kotlin
2. Compartilha certos comportamentos, porém a implementação difere devido à necessidade ou peculiaridade de cada plataforma

> Lembrando que, independente do tipo de compartilhamento, o Kotlin sempre irá compilar para código nativo.

Vamos entender melhor sobre cada um desse tipo.

### 1. Compartilhando código genérico utilizando 100% Kotlin

Esse tipo de compartilhamento infere que não há nenhuma implementação específica que precisa ser feita no lado nativo, nos possibilitando utilizar apenas o Kotlin para satisfazer nosso requisito.

No início do KMP, esse tipo de compartilhamento poderia não ser tão comum, já que a comunidade do código livre ainda estava se aquecendo e bibliotecas KMP estavam se formando. Atualmente, dado ao leque de código livre disponível para nosso uso, é o formato mais comum para compartilhamento de código.

#### 1.1 Constantes

Constante é aquele tipo de informação que é estática e super específica. É um tipo de informação que, geralmente, tem um tipo primitivo (`String`, `Int`, `Boolean`, etc) e se repete para todas as plataformas.

```kotlin
object AppConfig {
    const val API_KEY: String = "your_api_key"
    const val ENVIRONMENT: String = "production"
}

object AuthConfig {
    const val LOGIN_URL: String = "https://..."
    const val TOKEN_EXPIRY: Long = 3600 // 1 hora em segundos
}

object UIConfig {
    const val PRIMARY_COLOR: String = "#FF5733"
    const val FONT_SIZE: Int = 14
}

object ErrorMessages {
    const val NETWORK_ERROR: String = "Erro de conexão com a internet."
    const val LOGIN_FAILED: String = "Falha no login, tente novamente."
}

object DatabaseConfig {
    const val DB_URL: String = "jdbc:mysql://localhost:3306/mydb"
    const val TABLE_USER: String = "users"
}

object DomainSpecific {
    const val TAX_RATE: Double = 0.2
    const val MAX_DISCOUNT: Double = 50.0
    const val FAQ_URL: String = "https://..."
}

object WebServiceRoutes {
    private const val BASE_URL: String = "https://api.example.com/"
    const val USER_PROFILE: String = "${BASE_URL}user/profile"
    const val PRODUCT_LIST: String = "${BASE_URL}product/list"
}

object AnalyticsEvents {
    const val BUTTON_CLICKED_EVENT_NAME: String = "..."
}
```

#### 1.2 Modelos: entidades, DTO, objetos de valor, respostas e requisições com um servidor

Modelos geralmente refletem informações mais específicas do negócio, e na grande maioria das vezes não requerem nenhuma implementação específica de plataforma.

Compartilhar modelos vai além da conveniência, mas também reforça uma linguagem de domínio único para todo o time de frontend (mobile, web e desktop). Para os praticantes do [Domain Driven Design (DDD)](https://en.wikipedia.org/wiki/Domain-driven_design), essa prática é um artefato extremamente poderoso, já que dessa forma, o time terá um único dicionário do domínio.

```kotlin
data class User(
    val id: Int,
    val name: String,
    val email: String
) {
    init {
        require(id != 0)
        require(name.isNotBlank())
        require(email.isNotBlank())
    }
}
```
```kotlin
data class UserDTO(
    val name: String,
    val email: String
)
```
```kotlin
data class Money(
    val amount: Double,
    val currency: String
)
```

Graças ao [kotlin.serialization](https://github.com/Kotlin/kotlinx.serialization), não precisamos nos preocupar com implementações específicas de cada plataforma. Isso possibilita utilizar apenas código Kotlin para configurar a serialização e deserialização dos objetos conforme demonstrado a seguir.

```kotlin
@Serializable
data class ApiResponse<T>(
    @SerialName("data")
    val data: T,

    @SerialName("message")
    val message: String? = null,

    @SerialName("status")
    val status: Int
)
```
```kotlin
@Serializable
data class LoginRequest(
    @SerialName("username")
    val username: String,

    @SerialName("password")
    val password: String
)
```

> ⏱️ Vamos aprender sobre essa biblioteca nos próximos artigos

#### 1.3 Lógica de negócio

A natureza de uma regra de negócio é geralmente agnóstica a plataforma, e imposta pelo contexto específico do seu projeto, sendo um candidato perfeito para ser solucionado apenas com Kotlin.

Além de impor o mesmo comportamento de negócio para todas as plataformas, compartilhar a regra de negócio também significa compartilhar os testes unitários e integração dessa regra. Ao invés de repetir o mesmo teste em cada plataforma, testaremos apenas uma vez.

```kotlin
interface AccountRepository {
    val currentBalance: Double
}

class CheckBalanceForTransferUseCase(
    val accountRepository: AccountRepository
) {
    operator fun invoke(valueToTransfer: Double): CheckBalanceForTransferResult {
        require(valueToTransfer > 0)

        val currentBalance: Double = accountRepository.currentBalance

        return if (currentBalance >= valueToTransfer)
            HasSufficientFunds
        else InsufficientFunds(missingAmount = valueToTransfer - currentBalance)
    }

    sealed interface CheckBalanceForTransferResult {
        data object HasSufficientFunds : CheckBalanceForTransferResult
        data class InsufficientFunds(val missingAmount: Double) : CheckBalanceForTransferResult
    }
}
```

> No mundo do Kotlin/Android, o uso do padrão [UseCase](https://en.wikipedia.org/wiki/Use_case) se tornou uma prática comum e constantemente utilizada em projetos inner e open source.
> 
> Existem diversas formas de criar UseCases no Kotlin, caso tenha curiosidade em aprender outras formas:
> 
> [🔗 How To Avoid Use Cases Boilerplate in Android](https://betterprogramming.pub/how-to-avoid-use-cases-boilerplate-in-android-d0c9aa27ef27)

#### 1.4 Testes unitários e de integração
Uma das grandes vantagens do KMP é a possibilidade de ter seu código testado uma vez e reutilizada em todas as plataformas. Lembrando que, dentro do source-set `commonMain` ou `commonTest`, não podemos utilizar nenhuma biblioteca específica da plataforma. Ou seja, precisamos escrever testes numa infraestrutura multiplataforma.

Para isso, temos o [🔗 kotlin.test](https://kotlinlang.org/api/latest/kotlin.test/), que oferece uma API parecida com o `JUnit4/5` com suporte a anotações de `@Test`, além de recursos para verificar o conteúdo por funções como `assertEquals` e `assertContains`.

Vamos ver como seria um teste unitário para nosso use case acima:

```kotlin
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

// Implementação Fake
private class FakeAccountRepository(val balance: Double) : AccountRepository {
    override val currentBalance: Double
        get() = balance
}

// Classe de teste
class CheckBalanceForTransferUseCaseTest {

    @Test
    fun `deve retornar HasSufficientFunds quando o saldo atual é maior que o valor da transferência`() {
        // DADO: Um repositório fake com saldo suficiente
        val fakeRepository = FakeAccountRepository(balance = 1000.0)
        val useCase = CheckBalanceForTransferUseCase(fakeRepository)

        // QUANDO: Verificando o saldo para uma transferência
        val result = useCase(500.0)

        // ENTÃO: Deve retornar HasSufficientFunds
        assertTrue(result is HasSufficientFunds)
    }

    @Test
    fun `deve retornar InsufficientFunds com o valor correto faltante quando o saldo é menor que o valor da transferência`() {
        // DADO: Um repositório fake com saldo insuficiente
        val fakeRepository = FakeAccountRepository(balance = 300.0)
        val useCase = CheckBalanceForTransferUseCase(fakeRepository)

        // QUANDO: Verificando o saldo para uma transferência
        val result = useCase(500.0)

        // ENTÃO: Deve retornar InsufficientFunds com o valor faltante correto
        assertTrue(result is InsufficientFunds)
        val insufficientFundsResult = result as InsufficientFunds
        assertTrue(insufficientFundsResult.missingAmount == 200.0)
    }
}
```

> Para aprender sobre anotações no Kotlin: [The Full Guide to ANNOTATIONS In Kotlin por Philipp Lackner](https://www.youtube.com/watch?v=qdnhQzVGywQ) 
>
> Para aprender sobre o uso de "fakes" no Kotlin: [No Mocks Allowed por Marcello Galhardo](https://marcellogalhardo.dev/posts/no-mocks-allowed/)

> ⏱️ Vamos aprender mais sobre testes no KMP em artigos futuros 

#### Conclusão sobre compartilhando códigos 100% Kotlin
Como você pode perceber, podemos utilizar apenas o Kotlin em diversos aspectos do nosso projeto. Essa capacidade do KMP é extremamente poderosa, já que sem muito esforço, podemos utilizar o maquinário do KMP para gerar compilações nativas do nosso código.

Mas, como pode perceber pelos exemplos, geralmente conseguimos utilizar essa abordagem e 100% Kotlin para implementações específicas do seu domínio (camada `domain`).

E a camada de data/infra? Como podemos acessar recursos específicos e nativo da plataforma no KMP?

### 2. Compartilhando código com implementações específicas de cada plataforma
Aprendemos que cada plataforma tem uma forma específica de acessar recursos exclusivos do sistema operacional como internet, bluetooth, disco, notificações, imagens, etc. Esses recursos, apesar de na teoria terem o mesmo conceito, diferem nas suas implementações.

Para resolver esse desafio, o KMP introduz [duas novas palavras reservadas](https://kotlinlang.org/docs/multiplatform-expect-actual.html): `expect` (o contrato) e `actual` (a implementação).

#### 2.1 A palavra reservada `expect` no KMP
A palavra `expect` informa o compilador do Kotlin para ele pode "esperar" ou "exigir" uma implementação específica de cada plataforma para aquele componente específico durante a compilação de um source-set específico. Podemos utilizar a palavra `expect` para funções, propriedades, classes, objetos, interfaces, enumerações ou anotações.

Só é possível utilizar o `expect` no source set comum (`commonMain`): o source set comum declara, e os source sets específicos implementam.

Ao declarar um componente com a palavra `expect`:
1. Você tem a obrigação de declarar a implementação (`actual`) em cada source-set específico. Inclusive, ao declarar um `expect` qualquer, a IDE já informa um erro informando que precisamos declarar a versão `actual` de cada plataforma:
![Erro ao declarar expect](https://github.com/rsicarelli/KMP-101/blob/main/posts/assets/error-expect-actual-kotlin.png?raw=true)
2. 





  

