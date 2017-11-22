package io.github.ranolp.latte.compiler;

import io.github.ranolp.latte.compiler.core.Token;
import io.github.ranolp.latte.compiler.frontend.Lexer;
import org.junit.Test;

import java.util.List;

public class CompilerTest {
    @Test
    public void test() {
        List<Token> tokens = Lexer.lex("package latte.test\nfn main {\nprintln(\"Hello, Latte!\")\n}");
        for (Token token : tokens) {
            System.out.println(token);
            return;
        }
    }
}
