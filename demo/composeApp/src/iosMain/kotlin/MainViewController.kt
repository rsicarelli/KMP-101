@file:Suppress("UnusedReceiverParameter", "FunctionName")

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController() = ComposeUIViewController { App() }




        object LoginUiController

        data class LoginUiParams(val param: String)

        // New param: breaking change
        fun LoginUiController.get(params: LoginUiParams): UIViewController =
            ComposeUIViewController { LoginComposeUi(params) }

        @Composable
        private fun LoginComposeUi(params: LoginUiParams) {
            LaunchedEffect(params) {
                // Do something
            }
        }


object Teste {
    fun test() {
        LoginUiController.get(LoginUiParams("test"))
    }
}