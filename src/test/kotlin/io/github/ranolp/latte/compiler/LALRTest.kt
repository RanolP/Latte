package io.github.ranolp.latte.compiler

import io.github.ranolp.latte.compiler.backend.howtomakeparser.OrSyntax
import io.github.ranolp.latte.compiler.backend.howtomakeparser.Syntax
import io.github.ranolp.latte.compiler.backend.howtomakeparser.or
import io.github.ranolp.latte.compiler.backend.howtomakeparser.plus
import io.github.ranolp.latte.compiler.core.TokenType.*
import org.junit.Test

class LALRTest {
    val paren: Syntax by lazy {
        LEFT_BRACKET + addsub + RIGHT_BRACKET
    }
    val number: OrSyntax by lazy {
        INTEGER or DECIMAL
    }
    val muldiv by lazy {
        paren or number + (ASTERISK or SLASH) + number
    }
    val addsub: Syntax by lazy {
        muldiv + (PLUS or MINUS) + muldiv
    }

    @Test
    fun `test lalr tree generate`() {

        println(addsub.debug())
    }
}