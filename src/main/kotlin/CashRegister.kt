/**
 * The CashRegister class holds the logic for performing transactions.
 *
 * @param change The change that the CashRegister is holding.
 */
class CashRegister(private val change: Change) {
    /**
     * Performs a transaction for a product/products with a certain price and a given amount.
     *
     * @param price The price of the product(s).
     * @param amountPaid The amount paid by the shopper.
     *
     * @return The change for the transaction.
     *
     * @throws TransactionException If the transaction cannot be performed.
     */
    fun performTransaction(price: Long, amountPaid: Change): Change {
        receiveAmountFromTheCustomer(amountPaid)
        val amountPaidInCents = amountPaid.total
        if (amountPaidInCents < price) {
            giveAmountBackToTheCustomer(amountPaid)
            throw TransactionException("Insufficient funds: Amount paid is less than the price.")
        }

        val changeInCents = amountPaidInCents - price

        // Calculate and dispense the minimal amount of change
        val dispensedChange = calculateMinimalChange(changeInCents, amountPaid)

        // Update the register's remaining change
        dispensedChange.getElements().forEach {
            change.remove(it, dispensedChange.getCount(it))
        }

        println("Dispensing change: $dispensedChange")
        println("Register after transaction: $change")

        return dispensedChange
    }

    private fun calculateMinimalChange(amountInCents: Long, amountPaid: Change): Change {
        val resultChange = Change()
        var remainingAmount = amountInCents

        // Convert to a list and sort in descending order
        //val denominations = change.getElements().sortedByDescending { it.minorValue }
        for (denomination in change.getElements()) {
            val count = (remainingAmount / denomination.minorValue).toInt()
            val availableCount = change.getCount(denomination)

            val dispenseCount = if (count > availableCount) availableCount else count

            if (dispenseCount > 0) {
                resultChange.add(denomination, dispenseCount)
                remainingAmount -= denomination.minorValue * dispenseCount.toLong()
            }
        }

        // If exact change cannot be made, throw an exception
        if (remainingAmount > 0) {
            giveAmountBackToTheCustomer(amountPaid)
            throw TransactionException("Exact change cannot be made.")
        }

        return resultChange
    }

    private fun giveAmountBackToTheCustomer(amountPaid: Change){
        amountPaid.getElements().forEach(){
            change.remove(it, amountPaid.getCount(it))
        }
    }
    private fun receiveAmountFromTheCustomer(amountReceived: Change) {
        amountReceived.getElements().forEach(){
            change.add(it, amountReceived.getCount(it))
        }

    }
    class TransactionException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
