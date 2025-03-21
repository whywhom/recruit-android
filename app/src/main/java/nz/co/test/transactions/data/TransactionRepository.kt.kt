package nz.co.test.transactions.data

import android.util.Log
import nz.co.test.transactions.data.services.TransactionsService
import nz.co.test.transactions.model.Transaction
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val api: TransactionsService
) {
    private var cachedTransactions: List<Transaction> = emptyList()

    suspend fun getTransactions(): List<Transaction> {
        try {
            if (cachedTransactions.isNotEmpty()) {
                Log.d(TransactionRepository::class.java.simpleName, "Loaded transactions from in-memory cache")
                return cachedTransactions
            }
            val transaction = api.retrieveTransactions()
            cachedTransactions = transaction
            Log.d(TransactionRepository::class.java.simpleName, "transaction.size = ${transaction.size}")
            return transaction
        } catch (e: Exception) {
            Log.e(TransactionRepository::class.java.simpleName, "Error fetching transactions ${e.message}")
            throw Exception("Failed to fetch transactions: ${e.message}", e)
        }
    }

    fun fetchTransactions(): List<Transaction> = cachedTransactions
    // Optionally, provide a method to clear the cache if needed
    fun clearCache() {
        cachedTransactions = emptyList()
        Log.d(TransactionRepository::class.java.simpleName, "In-memory cache cleared")
    }
}