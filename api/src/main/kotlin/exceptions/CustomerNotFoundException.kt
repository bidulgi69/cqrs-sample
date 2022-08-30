package exceptions

class CustomerNotFoundException : Throwable {
    constructor(message: String): super(message)
}