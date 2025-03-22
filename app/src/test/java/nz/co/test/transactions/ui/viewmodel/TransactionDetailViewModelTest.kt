package nz.co.test.transactions.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nz.co.test.transactions.data.TransactionRepository
import nz.co.test.transactions.model.Transaction
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import retrofit2.HttpException
import java.io.IOException
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionDetailViewModelTest {

    private lateinit var repository: TransactionRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: TransactionDetailViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Set the main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Mocking the repository
        repository = mockk(relaxed = true)

        // Mocking SavedStateHandle with transactionId = "1"
        savedStateHandle = SavedStateHandle(mapOf("id" to "1"))

        // Initializing the ViewModel
        viewModel = TransactionDetailViewModel(savedStateHandle, repository)
    }

    @Test
    fun `getTransactionId should return correct transactionId`() {
        // Verify the ViewModel correctly fetches the transactionId from the SavedStateHandle
        assertEquals(1, viewModel.getTransactionId())
    }

    @Test
    fun `fetchTransactions should update transactions state flow on success`() = runTest {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val transactions = listOf(
            Transaction(id = 1, transactionDate = LocalDateTime.parse("2022-01-23T09:12:32", formatter), summary = "Transaction 1", debit = BigDecimal("100.0"), credit = BigDecimal.ZERO),
            Transaction(id = 2, transactionDate = LocalDateTime.parse("2022-01-24T09:12:32", formatter), summary = "Transaction 2", debit = BigDecimal("200.0"), credit = BigDecimal.ZERO)
        )
        // Mocking the repository response
        coEvery { repository.fetchTransactions() } returns transactions

        // Trigger the fetch transactions logic
        viewModel.fetchTransactions()

        advanceUntilIdle()

        // Testing if the transactions flow has been updated correctly
        viewModel.transactions.test {
            assertEquals(
                1,
                awaitItem().size
            ) // Only transaction with ID = 1 should be returned
        }
        viewModel.transactions.test {
            assertEquals(
                1,
                awaitItem()[0].id
            ) // Verifying the correct transaction is returned
        }
    }

    @Test
    fun `fetchTransactions should update errorMessage on IOException`() = runTest {
        // Mocking an IOException from the repository
        coEvery { repository.fetchTransactions() } throws IOException()

        // Trigger the fetch transactions logic
        viewModel.fetchTransactions()

        advanceUntilIdle()

        // Testing if the error message was updated correctly
        viewModel.errorMessage.test {
            assertEquals(
                "Network error. Please check your connection.",
                awaitItem()
            )
        }
    }

    @Test
    fun `fetchTransactions should update errorMessage on HttpException`() = runTest {
        val mockResponse = mockk<retrofit2.Response<Any>>(relaxed = true)
        val httpException = HttpException(mockResponse)
        // Mocking an HttpException from the repository
        coEvery { repository.fetchTransactions() } throws httpException

        // Trigger the fetch transactions logic
        viewModel.fetchTransactions()

        advanceUntilIdle()

        // Testing if the error message was updated correctly
        viewModel.errorMessage.test {
            assertTrue(awaitItem().toString().startsWith("Server error:"))
        }
    }

    @Test
    fun `fetchTransactions should update errorMessage on unexpected exception`() = runTest {
        // Mocking a generic exception from the repository
        coEvery { repository.fetchTransactions() } throws Exception("Unexpected error")

        // Trigger the fetch transactions logic
        viewModel.fetchTransactions()

        advanceUntilIdle()

        // Testing if the error message was updated correctly
        viewModel.errorMessage.test {
            assertEquals(
                "An unexpected error occurred: Unexpected error",
                awaitItem()
            )
        }
    }

    @Test
    fun `fetchTransactions should clear errorMessage on success`() = runTest {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val transactions = listOf(
            Transaction(id = 1, transactionDate = LocalDateTime.parse("2022-01-23T09:12:32", formatter), summary = "Transaction 1", debit = BigDecimal("100.0"), credit = BigDecimal.ZERO)
        )
        // Mocking the repository response
        coEvery { repository.getTransactions() } returns transactions

        // Trigger the fetch transactions logic
        viewModel.fetchTransactions()

        // Testing if errorMessage is cleared after successful fetch
        assertNull(viewModel.errorMessage.first())
    }

    @Test
    fun `fetchTransactions should filter transactions by transactionId`() = runTest {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val transactions = listOf(
            Transaction(id = 1, transactionDate = LocalDateTime.parse("2022-01-23T09:12:32", formatter), summary = "Transaction 1", debit = BigDecimal("100.0"), credit = BigDecimal.ZERO),
            Transaction(id = 2, transactionDate = LocalDateTime.parse("2022-01-24T09:12:32", formatter), summary = "Transaction 2", debit = BigDecimal("200.0"), credit = BigDecimal.ZERO)
        )
        // Mocking the repository response
        coEvery { repository.fetchTransactions() } returns transactions

        // Trigger the fetch transactions logic
        viewModel.fetchTransactions()
        advanceUntilIdle()
        // Testing if only the transaction with the correct ID is returned
        viewModel.transactions.test {
            // Verifying the correct transaction is returned
            assertEquals(
                1,
                awaitItem()[0].id
            )
        }
    }
}
