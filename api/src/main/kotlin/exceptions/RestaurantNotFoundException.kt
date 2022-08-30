package exceptions

class RestaurantNotFoundException : Throwable {
    constructor(message: String): super(message)
}