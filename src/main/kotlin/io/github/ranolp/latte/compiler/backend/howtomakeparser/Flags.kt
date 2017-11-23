package io.github.ranolp.latte.compiler.backend.howtomakeparser

enum class Flags(val id: Int) {
    ABLE(0b1),
    ZERO_OR_MORE(0b10),
    MORE(0b100)
}