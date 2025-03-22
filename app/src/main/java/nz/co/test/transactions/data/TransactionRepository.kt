package nz.co.test.transactions.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nz.co.test.transactions.data.services.TransactionsService
import nz.co.test.transactions.model.Transaction
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val api: TransactionsService
) : DefaultRepository {
    private var cachedTransactions: List<Transaction> = emptyList()

    override suspend fun getTransactions(): List<Transaction> = withContext(Dispatchers.IO) {
        try {
            if (cachedTransactions.isNotEmpty()) {
                Log.d(TransactionRepository::class.java.simpleName, "Loaded transactions from in-memory cache")
                return@withContext cachedTransactions
            }
            val transactions = api.retrieveTransactions()
            cachedTransactions = transactions
            transactions
        } catch (e: Exception) {
            Log.e(TransactionRepository::class.java.simpleName, "Error fetching transactions: ${e.message}")
            throw Exception("Failed to fetch transactions: ${e.message}", e)
        }
    }

    override fun fetchTransactions(): List<Transaction> = cachedTransactions

    // Optionally, provide a method to clear the cache if needed
    override fun clearCache() {
        cachedTransactions = emptyList()
        Log.d(TransactionRepository::class.java.simpleName, "In-memory cache cleared")
    }
}