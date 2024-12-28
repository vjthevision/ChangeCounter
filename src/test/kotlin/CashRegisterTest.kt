import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CashRegisterTest {

    @Order(1)
    @Test
    fun shouldReturnChangeWhenPaidExcess() {
        val billAmount = 1250L
        val amountPaid = Change().apply { add(Bill.TWENTY_EURO, 1) }
        val changeReturned = cashRegister.performTransaction(billAmount, amountPaid)

        val expectedChange = Change().apply {
            add(Bill.FIVE_EURO, 1)
            add(Coin.ONE_EURO, 2)
            add(Coin.FIFTY_CENT, 1)
        }

        assertEquals(expectedChange, changeReturned)
    }

    @Order(2)
    @Test
    fun shouldThrowExceptionWhenInsufficientFunds() {
        val price = 2250L
        val amountPaid = Change().apply {
            add(Bill.TWENTY_EURO, 1)
            add(Coin.FIFTY_CENT, 1)
        }

        val exception = assertThrows<CashRegister.TransactionException> {
            cashRegister.performTransaction(price, amountPaid)
        }
        assertEquals("Insufficient funds: Amount paid is less than the price.", exception.message)
    }

    @Order(3)
    @Test
    fun shouldReturnNoChangeWhenPaidWithExactAmount() {
        val price = 2000L
        val amountPaid = Change().apply { add(Bill.TWENTY_EURO, 1) }
        val changeReturned = cashRegister.performTransaction(price, amountPaid)
        assertEquals(changeReturned.total, 0L)
    }

    @Order(4)
    @Test
    fun shouldThrowInsufficientFundsWhenAmountPaidIsLessThanPrice() {
        val price = 1000L
        val amountPaid = Change().apply { add(Bill.FIVE_EURO, 1) }

        val exception = assertThrows<CashRegister.TransactionException> {
            cashRegister.performTransaction(price, amountPaid)
        }
        assertEquals(
            "Insufficient funds: Amount paid is less than the price.",
            exception.message
        )
    }
    @Order(5)
    @Test
    fun shouldThrowInsufficientChangeInTheCashRegisterWhenChangeIsNotEnough() {
        val price = 100L
        val amountPaid = Change().apply { add(Bill.ONE_HUNDRED_EURO, 1) }

        val exception = assertThrows<CashRegister.TransactionException> {
            cashRegister.performTransaction(price, amountPaid)
        }
        assertEquals("Insufficient change in the cash register.", exception.message)
    }

    @Order(6)
    @Test
    fun shouldThrowExactChangeCannotBeMadeWhenExactChangeIsNotPossible() {
        val price = 101L
        val amountPaid = Change().apply { add(Bill.TWENTY_EURO, 1) }

        val exception = assertThrows<CashRegister.TransactionException> {
            cashRegister.performTransaction(price, amountPaid)
        }
        assertEquals("Exact change cannot be made.", exception.message)

    }


    companion object {

        private lateinit var cashRegister: CashRegister

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val initialChange = Change().apply {
                add(Bill.FIVE_EURO, 2)
                add(Bill.TEN_EURO, 1)
                add(Coin.FIFTY_CENT, 4)
                add(Coin.ONE_EURO, 3)
            }
            println("Opening cash register with initial change : $initialChange")
            cashRegister = CashRegister(initialChange)
        }
    }
}
