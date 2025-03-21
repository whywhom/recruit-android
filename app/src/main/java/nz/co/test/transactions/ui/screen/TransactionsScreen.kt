package nz.co.test.transactions.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import nz.co.test.transactions.model.Transaction
import nz.co.test.transactions.ui.TransactionViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: TransactionViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
            .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.fetchTransactions() }) {
                    Text("Retry")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(transactions) { transaction ->
                        TransactionItem(transaction) { }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {
    val color = if (transaction.credit > BigDecimal.ZERO) Color.Green else Color.Red
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = transaction.summary, style = MaterialTheme.typography.headlineMedium)
            Text(text = "Credit: ${transaction.credit}", color = color)
            Text(text = "Debit: ${transaction.debit}", color = color)
            Text(text = "Date: ${transaction.transactionDate}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}