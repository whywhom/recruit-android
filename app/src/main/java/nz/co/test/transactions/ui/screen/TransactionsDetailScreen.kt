package nz.co.test.transactions.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nz.co.test.transactions.ui.viewmodel.TransactionDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsDetailScreen(
    viewModel: TransactionDetailViewModel = hiltViewModel(),
    onBackPress: () -> Unit,
) {
    val tId = viewModel.getTransactionId()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    Log.d("TransactionsDetailScreen", "Transaction Id = $tId")
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions id = $tId") },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val darkGreen = Color(0xFF006400)
            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage ?: "Default",
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {}) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        transactions?.let {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Summary:", fontWeight = FontWeight.Bold)
                                Text(text = it.summary, style = MaterialTheme.typography.bodyLarge)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Date:", fontWeight = FontWeight.Bold)
                                Text(
                                    text = it.transactionDate.toString(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Credit:", fontWeight = FontWeight.Bold, color = darkGreen)
                                Text(text = it.credit.toString(), color = darkGreen)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Debit:", fontWeight = FontWeight.Bold, color = Color.Red)
                                Text(text = it.debit.toString(), color = Color.Red)
                            }
                        } ?: run {
                            Text("Transaction not found", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}
