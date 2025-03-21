package nz.co.test.transactions.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import nz.co.test.transactions.TransactionApp
import nz.co.test.transactions.ui.theme.TransactionApplicationTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            TransactionApplicationTheme {
                TransactionApp()
            }
        }
    }
}