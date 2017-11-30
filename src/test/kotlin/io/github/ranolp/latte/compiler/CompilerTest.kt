package io.github.ranolp.latte.compiler

import io.github.ranolp.latte.compiler.backend.Parser
import io.github.ranolp.latte.compiler.frontend.Lexer
import org.junit.Test

class CompilerTest {
    @Test
    fun `test compiler`() {
        val tokens = Lexer.lex("package latte.test\nfn main {\nprintln(\"Hello, Latte!\")\n}")
        for (token in tokens) {
            println(token)
        }
        val parsed = Parser.parse(tokens)
        if(parsed !== null) {
            println("Parse Success : ${parsed.debug()}")
        } else {
            println("Parse Failure : $parsed")
        }
    }
}