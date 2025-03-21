package nz.co.test.transactions.data

import android.util.Log
import nz.co.test.transactions.data.services.TransactionsService
import nz.co.test.transactions.model.Transaction
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val api: TransactionsService
) {
    suspend fun getTransactions(): List<Transaction> {
        try {
            val transaction = api.retrieveTransactions()
            Log.d("AndyTest", "transaction.size = ${transaction.size}")
            return transaction
        } catch (e: Exception) {
            Log.e("API", "Error fetching transactions", e)
            return emptyList()
        }
    }
}