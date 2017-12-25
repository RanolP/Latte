package io.github.ranolp.latte.compiler.backend.interpreter

class VMError : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, throwable: Throwable) : super(message, throwable)
    constructor(throwable: Throwable) : super(throwable)
}
