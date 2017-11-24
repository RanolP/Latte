package io.github.ranolp.latte.compiler.backend.howtomakeparser

enum class Flags(val id: Int) {
    ABLE(0b1),
    ZERO_OR_MORE(0b10),
    MORE(0b100);
}

fun Int.flagged(flags: Flags) = and(flags.id) == flags.id
val Int.flagged
    get() = this != 0

private fun Int.stringfy(flags: Flags, string: String) = if (flagged(flags)) string else ""
val Int.flagToString: String
    get() = stringfy(Flags.ABLE, "?") + stringfy(Flags.ZERO_OR_MORE, "*") + stringfy(Flags.MORE, "+")