package core.values

data class CreditCard(
    val cvc: String = "000",
    val number: String = "0000000000",
    val yy: String = "00",
    val mm: String = "00",
)
