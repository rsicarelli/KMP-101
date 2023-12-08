import CheckBalanceForTransferUseCase.CheckBalanceForTransferResult.HasSufficientFunds
import CheckBalanceForTransferUseCase.CheckBalanceForTransferResult.InsufficientFunds
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
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
            AnimatedVisibility(showImage) {
                Image(
                    painterResource("compose-multiplatform.xml"),
                    null
                )
            }
        }
    }
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
