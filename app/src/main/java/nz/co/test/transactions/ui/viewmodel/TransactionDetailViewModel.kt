package nz.co.test.transactions.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nz.co.test.transactions.Screen
import nz.co.test.transactions.data.TransactionRepository
import nz.co.test.transactions.model.Transaction
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TransactionRepository,
) : ViewModel() {
    private val transactionId: Int = savedStateHandle.get<String>(Screen.transactionId)?.toInt()?: -1
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions = _transactions.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null) // Store error messages
    val errorMessage = _errorMessage.asStateFlow()

    init {
        fetchTransactions()
    }

    fun getTransactionId(): Int = transactionId

    fun fetchTransactions() {
        viewModelScope.launch {
            try {
                val transactions = repository.fetchTransactions().sortedByDescending { it.id }
                // Find the transaction with the given id
                _transactions.value = transactions.filter {
                    it.id == transactionId
                }
                // Clear error on success
                _errorMessage.value = null
            } catch (e: IOException) {
                _errorMessage.value = "Network error. Please check your connection."
            } catch (e: HttpException) {
                _errorMessage.value = "Server error: ${e.message}"
            } catch (e: Exception) {
                _errorMessage.value = "An unexpected error occurred: ${e.localizedMessage}"
            }
        }
    }
}