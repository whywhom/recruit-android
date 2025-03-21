package nz.co.test.transactions.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nz.co.test.transactions.data.TransactionRepository
import nz.co.test.transactions.model.Transaction
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

enum class SortType {
    ID, DATE, SUMMARY
}

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
) : ViewModel() {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions = _transactions.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null) // Store error messages
    val errorMessage = _errorMessage.asStateFlow()

    init {
        fetchTransactions()
    }

    fun fetchTransactions() {
        viewModelScope.launch {
            try {
                _transactions.value = withContext(ioDispatcher) {
                    repository.getTransactions().sortedByDescending { it.id }
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

    fun sortedList(type: SortType) {
        viewModelScope.launch {
            _transactions.value = withContext(ioDispatcher) {
                repository.fetchTransactions().sortedWith { t1, t2 ->
                    when (type) {
                        SortType.ID -> t2.id.compareTo(t1.id)  // Compare Int (Comparable)
                        SortType.DATE -> t2.transactionDate.compareTo(t1.transactionDate)  // Compare LocalDateTime (Comparable)
                        SortType.SUMMARY -> t2.summary.compareTo(t1.summary)  // Compare String (Comparable)
                    }
                }
            }
        }
    }
}