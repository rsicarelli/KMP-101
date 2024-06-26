import CheckBalanceForTransferUseCase.CheckBalanceForTransferResult.HasSufficientFunds
import CheckBalanceForTransferUseCase.CheckBalanceForTransferResult.InsufficientFunds
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun App() {
    MaterialTheme {
        var greetingText by remember { mutableStateOf("Hello World!") }
        var showImage by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                greetingText = "Compose: ${Greeting().greet()}"
                showImage = !showImage
            }) {
                Text(greetingText)
            }
        }
    }
}

interface AccountRepository {
    val currentBalance: Double
}


private class FakeAccountRepository(val balance: Double) : AccountRepository {
    override val currentBalance: Double
        get() = balance
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
