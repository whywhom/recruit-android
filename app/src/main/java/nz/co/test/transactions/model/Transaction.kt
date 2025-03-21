package nz.co.test.transactions.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class Transaction (
    @Json(name = "id")
    val id: Int,
    @Json(name = "transactionDate")
    val transactionDate: LocalDateTime,
    @Json(name = "summary")
    val summary: String,
    @Json(name = "debit")
    val debit: BigDecimal,
    @Json(name = "credit")
    val credit: BigDecimal
)