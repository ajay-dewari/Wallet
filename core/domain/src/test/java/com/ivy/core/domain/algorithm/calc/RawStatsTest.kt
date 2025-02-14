package com.ivy.core.domain.algorithm.calc

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class RawStatsTest {
    private lateinit var transactionList: List<CalcTrn>
    private lateinit var recentTime5: Instant
    private lateinit var recentTime7: Instant
    private lateinit var recentTime10: Instant

    @BeforeEach
    fun setUp() {
        recentTime5 = Instant.now().minusSeconds(5)
        recentTime7 = Instant.now().minusSeconds(7)
        recentTime10 = Instant.now().minusSeconds(10)
        transactionList = listOf(
            CalcTrn(
                amount = 10.0,
                currency = "INR",
                type = TransactionType.Income,
                time = recentTime10
            ),
            CalcTrn(
                amount = 20.0,
                currency = "EUR",
                type = TransactionType.Income,
                time = recentTime5
            ),
            CalcTrn(
                amount = 30.0,
                currency = "USD",
                type = TransactionType.Income,
                time = recentTime7
            ),
        )
    }

    @Test
    fun `Test RawStats from  CalcTrn`() {
        val output = rawStats(transactionList)
        assertThat(output.incomesCount).isEqualTo(3)
        assertThat(output.expensesCount).isEqualTo(0)
        assertThat(output.newestTrnTime).isEqualTo(recentTime5)
        assertThat(output.incomes["USD"]).isEqualTo(30.0)
        assertThat(output.expenses).isEqualTo(mapOf())
        assertThat(output.incomes).isEqualTo(mapOf("USD" to 30.0, "INR" to 10.0, "EUR" to 20.0))
    }
}