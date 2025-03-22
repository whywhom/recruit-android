package nz.co.test.transactions.data

import nz.co.test.transactions.model.Transaction

interface DefaultRepository {
    suspend fun getTransactions(): List<Transaction>
    fun fetchTransactions(): List<Transaction>
    fun clearCache()
}
