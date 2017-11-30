package io.github.ranolp.latte.compiler.backend

class ParseError : Error {
    constructor(message: String) : super(message)
    constructor(message: String, throwable: Throwable) : super(message, throwable)
}