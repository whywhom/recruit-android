import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nz.co.test.transactions.data.TransactionRepository
import nz.co.test.transactions.model.Transaction
import nz.co.test.transactions.ui.viewmodel.TransactionViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.IOException
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    private lateinit var repository: TransactionRepository
    private lateinit var viewModel: TransactionViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = TransactionViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchTransactions should update transactions state flow on success`() = runTest {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val transactions = listOf(
            Transaction(id = 1, transactionDate = LocalDateTime.parse("2022-01-23T09:12:32", formatter), summary = "Kilback-Murazik", debit = BigDecimal("7073.65") ,credit = BigDecimal.ZERO),
            Transaction(id = 2, transactionDate = LocalDateTime.parse("2022-01-12T00:20:03", formatter), summary = "Hermiston, Bradtke and Senger", debit = BigDecimal.ZERO ,credit = BigDecimal("5333.1"))
        )
        coEvery { repository.getTransactions() } returns transactions
        viewModel.fetchTransactions()
        advanceUntilIdle()
        viewModel.transactions.test {
            assertEquals(transactions.sortedByDescending { it.id }, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchTransactions should update errorMessage on IOException`() = runTest {
        coEvery { repository.getTransactions() } throws IOException()
        viewModel.fetchTransactions()
        advanceUntilIdle()
        viewModel.errorMessage.test {
            assertEquals("Network error. Please check your connection.", awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `sortedList should sort transactions by ID`() = runTest {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val transactions = listOf(
            Transaction(id = 1, transactionDate = LocalDateTime.parse("2022-01-23T09:12:32", formatter), summary = "Kilback-Murazik", debit = BigDecimal("7073.65") ,credit = BigDecimal.ZERO),
            Transaction(id = 2, transactionDate = LocalDateTime.parse("2022-01-12T00:20:03", formatter), summary = "Hermiston, Bradtke and Senger", debit = BigDecimal.ZERO ,credit = BigDecimal("5333.1"))
        )
        coEvery { repository.getTransactions() } returns transactions
        viewModel.fetchTransactions()
        advanceUntilIdle()
        viewModel.transactions.test {
            assertEquals(transactions.sortedByDescending { it.id }, awaitItem())
        }
    }
}