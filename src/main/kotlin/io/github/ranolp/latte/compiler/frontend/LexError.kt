package io.github.ranolp.latte.compiler.frontend

class LexError : Error {
    constructor(message: String) : super(message)
    constructor(message: String, throwable: Throwable) : super(message, throwable)
}