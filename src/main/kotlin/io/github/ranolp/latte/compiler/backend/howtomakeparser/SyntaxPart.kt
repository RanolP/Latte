package io.github.ranolp.latte.compiler.backend.howtomakeparser

import io.github.ranolp.latte.compiler.backend.core.ast.Node
import io.github.ranolp.latte.compiler.core.Token

interface SyntaxPart<out T : SyntaxPart<T>> {
    var mapping: ((List<Token>) -> Node?)?
    var name: String
    var flag: Int
    val self: T
    val able: T
        get() {
            val self = self
            self.flag = flag or Flags.ABLE.id
            return self
        }
    val zeroOrMore: T
        get() {
            val self = self
            self.flag = flag or Flags.ZERO_OR_MORE.id
            return self
        }
    val more: T
        get() {
            val self = self
            self.flag = flag or Flags.MORE.id
            return self
        }

    fun flagged(flags: Flags) = flag and flags.id == flags.id
    val flagged
        get() = flag != 0

    private fun stringfy(flags: Flags, string: String) = if (flagged(flags)) string else ""
    val flagToString: String
        get() = stringfy(Flags.ABLE, "?") + stringfy(Flags.ZERO_OR_MORE, "*") + stringfy(Flags.MORE, "+")

    fun debug(): String

    operator fun plus(syntaxPart: SyntaxPart<*>): Syntax

    operator fun invoke(name: String? = null, mapper: (List<Token>) -> Node?): T {
        if (name != null) {
            this.name = name
        }
        mapping = mapper
        return self
    }
}