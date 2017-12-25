package io.github.ranolp.latte.compiler

import io.github.ranolp.latte.compiler.backend.Parser
import io.github.ranolp.latte.compiler.backend.interpreter.LatteFileInterpreter
import io.github.ranolp.latte.compiler.backend.interpreter.LatteVM
import io.github.ranolp.latte.compiler.frontend.Lexer
import org.junit.Test

class CompilerTest {
    @Test
    fun `test compiler`() {
        val tokens = Lexer.lex("""
package latte.test

fn main {
    println("Hello, Latte!")
    println(1234)
    println(12.34)
    println(true)
}
""")
        /*for (token in tokens) {
            println(token)
        }*/
        val parsed = Parser.parse(tokens)
        if(parsed !== null) {
            // println("Parse Success\n\n${parsed.debug()}")
            val vm = LatteVM()
            LatteFileInterpreter.interpret(vm, parsed)
        } else {
            println("Parse Failure")
        }

    }
}
