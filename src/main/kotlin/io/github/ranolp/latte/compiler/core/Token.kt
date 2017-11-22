package io.github.ranolp.latte.compiler.core

data class Token(val type: TokenType, val data: String, val line: Int, val position: Int) {}