package exceptions

class MenuItemNotFoundException : Throwable {
    constructor(message: String): super(message)
}