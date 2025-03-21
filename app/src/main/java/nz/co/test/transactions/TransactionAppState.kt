package nz.co.test.transactions

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    data object TransactionList : Screen("transactionList")
    data object TransactionDetail : Screen("detail/{$transactionId}") {
        fun createRoute(id: Int) = "detail/$id"
    }
    companion object {
        const val transactionId = "id"
    }
}

@Composable
fun rememberTransactionAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(navController, context) {
    TransactionAppState(navController, context)
}

class TransactionAppState(
    val navController: NavHostController,
    private val context: Context
)
{
    fun navigateToTransactionDetail(id: Int, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.TransactionDetail.createRoute(id))
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED