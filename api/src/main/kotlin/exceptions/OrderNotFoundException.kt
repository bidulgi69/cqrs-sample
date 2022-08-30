package exceptions

class OrderNotFoundException : Throwable {
    constructor(message: String): super(message)
}