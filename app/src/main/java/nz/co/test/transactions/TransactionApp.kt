package nz.co.test.transactions

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.scaleOut
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import nz.co.test.transactions.ui.screen.TransactionsDetailScreen
import nz.co.test.transactions.ui.screen.TransactionsScreen

@Composable
fun TransactionApp(
    appState: TransactionAppState = rememberTransactionAppState()
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    NavHost(
        navController = appState.navController,
        startDestination = Screen.TransactionList.route,
        popExitTransition = { scaleOut(targetScale = 0.9f) },
        popEnterTransition = { EnterTransition.None }
    ) {
        composable(Screen.TransactionList.route) { backStackEntry->
            TransactionsScreen(
                navigateToPodcastDetails = { id ->
                    appState.navigateToTransactionDetail(id, backStackEntry)
                }
            )
        }
        composable(Screen.TransactionDetail.route) {
            TransactionsDetailScreen(
                onBackPress = appState::navigateBack
            )
        }
    }
}
