package io.github.ranolp.latte.compiler

import io.github.ranolp.latte.compiler.frontend.Lexer
import org.junit.Test

class CompilerTest {
    @Test
    fun `test compiler`() {
        val tokens = Lexer.lex("package latte.test\nfn main {\nprintln(\"Hello, Latte!\")\n}")
        for (token in tokens) {
            println(token)
        }
    }
}