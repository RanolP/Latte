package io.github.ranolp.latte.compiler.frontend

import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType
import java.lang.StringBuilder

object Lexer {
    private val TYPES = mapOf('+' to TokenType.PLUS,
            '-' to TokenType.MINUS,
            '*' to TokenType.ASTERISK,
            '/' to TokenType.SLASH,
            '%' to TokenType.PERCENT,
            '&' to TokenType.AND,
            '|' to TokenType.OR,
            '?' to TokenType.QUESTION,
            ':' to TokenType.COLON,
            ';' to TokenType.SEMICOLON,
            '\n' to TokenType.SEMICOLON,
            '.' to TokenType.DOT,
            ',' to TokenType.COMMA,
            '!' to TokenType.EXCLAMATION,
            '(' to TokenType.LEFT_BRACKET,
            ')' to TokenType.RIGHT_BRACKET,
            '[' to TokenType.LEFT_BRACE,
            ']' to TokenType.RIGHT_BRACE,
            '{' to TokenType.LEFT_CURLY_BRACE,
            '}' to TokenType.RIGHT_CURLY_BRACE,
            '=' to TokenType.ASSIGN)
    private val KEYWORDS = mapOf("abstract" to TokenType.ABSTRACT,
            "as" to TokenType.AS,
            "break" to TokenType.BREAK,
            "case" to TokenType.CASE,
            "catch" to TokenType.CATCH,
            "class" to TokenType.CLASS,
            "companion" to TokenType.COMPANION,
            "const" to TokenType.CONST,
            "continue" to TokenType.CONTINUE,
            "crossinline" to TokenType.CROSSINLINE,
            "data" to TokenType.DATA,
            "dynamic" to TokenType.DYNAMIC,
            "else" to TokenType.ELSE,
            "enum" to TokenType.ENUM,
            "false" to TokenType.FALSE,
            "finally" to TokenType.FINALLY,
            "fn" to TokenType.FN,
            "for" to TokenType.FOR,
            "get" to TokenType.GET,
            "if" to TokenType.IF,
            "import" to TokenType.IMPORT,
            "in" to TokenType.IN,
            "infix" to TokenType.INFIX,
            "inner" to TokenType.INNER,
            "interface" to TokenType.INTERFACE,
            "is" to TokenType.IS,
            "lateinit" to TokenType.LATEINIT,
            "let" to TokenType.LET,
            "mut" to TokenType.MUT,
            "noinline" to TokenType.NOINLINE,
            "null" to TokenType.NULL,
            "object" to TokenType.OBJECT,
            "open" to TokenType.OPEN,
            "operator" to TokenType.OPERATOR,
            "out" to TokenType.OUT,
            "override" to TokenType.OVERRIDE,
            "package" to TokenType.PACKAGE,
            "private" to TokenType.PRIVATE,
            "protected" to TokenType.PROTECTED,
            "public" to TokenType.PUBLIC,
            "reified" to TokenType.REIFIED,
            "return" to TokenType.RETURN,
            "set" to TokenType.SET,
            "super" to TokenType.SUPER,
            "this" to TokenType.THIS,
            "true" to TokenType.TRUE,
            "try" to TokenType.TRY,
            "when" to TokenType.WHEN,
            "while" to TokenType.WHILE)

    private fun query(line: String, from: Int): Pair<String, TokenType>? {
        if (from > 0 && !line[from - 1].isWhitespace()) {
            return null
        }
        val temp = KEYWORDS.keys.toMutableSet()
        var match: String? = null
        for ((i, c) in line.substring(from).withIndex()) {
            if (!c.isLetter()) break
            val iterator = temp.iterator()
            if (!iterator.hasNext()) {
                break
            }
            while (iterator.hasNext()) {
                val value = iterator.next()
                if (value.length - 1 == i) {
                    match = value
                } else if (value.length <= i || value[i] != c) {
                    iterator.remove()
                }
            }
        }
        return match?.to(KEYWORDS[match]!!)
    }

    @JvmStatic
    fun lex(source: String): List<Token> {
        val result = mutableListOf<Token>()
        val lines = source.split('\n')
        val unknownBuilder = StringBuilder()
        var line = 0
        fun flush(position: Int) {
            if (unknownBuilder.isNotEmpty()) {
                result.add(Token(TokenType.UNKNOWN, unknownBuilder.toString(), line, position - unknownBuilder.length))
                unknownBuilder.setLength(0)
            }
        }
        while (line < lines.size) {
            var position = 0
            val currentLine = lines[line]
            while (position < currentLine.length) {
                val currentChar = currentLine[position]
                fun hasNext(pos: Int = position) = pos + 1 < currentLine.length
                fun nextValue(pos: Int = position) = currentLine[pos + 1]
                fun next(char: Char, pos: Int = position) = hasNext(pos) && nextValue(pos) == char
                fun nextCursor(char: Char, pos: Int = position) = next(char, pos).also { if (it) position++ }
                fun current(char: Char) = currentChar == char
                fun currentCursor(char: Char) = current(char).also { if (it) position++ }
                fun flush() = flush(position)
                val queried = query(currentLine, position)
                if (queried != null) {
                    val temp = position
                    flush()
                    position += queried.first.length - 1
                    result.add(Token(queried.second, queried.first, line, temp))
                } else if (TYPES[currentChar] != null) {
                    flush()
                    result.add(Token(TYPES[currentChar]!!, currentChar.toString(), line, position))
                } else if (Character.isWhitespace(currentChar)) {
                    flush()
                } else if (current('/') && next('/')) {
                    flush()
                    break
                } else if (currentCursor('\"')) {
                    if (!hasNext()) {
                        throw LexError("Cannot found end quote in a line")
                    }
                    val literalPosition = position
                    if (next('\"') && nextCursor('\"', position + 1)) {
                        // todo: multiline string literal
                    } else {
                        val literalBuilder = StringBuilder()
                        var backslash = false
                        var lastFound = false
                        for (pos in position until currentLine.length) {
                            val ch = currentLine[pos]
                            if (ch == '\\') {
                                if (backslash) {
                                    literalBuilder.append('\\')
                                } else {
                                    backslash = true
                                }
                                continue
                            }
                            if (backslash) {
                                when (ch) {
                                    '\'' -> literalBuilder.append('\'')
                                    '\"' -> literalBuilder.append('\"')
                                    'n' -> literalBuilder.append('\n')
                                    'r' -> literalBuilder.append('\r')
                                    't' -> literalBuilder.append('\t')
                                    'b' -> literalBuilder.append('\b')
                                    else -> throw LexError("Undefined escape sequence \'\\$ch\' found")
                                }
                            }
                            if (ch == '\"') {
                                lastFound = true
                                break
                            }
                            literalBuilder.append(ch)
                        }
                        if (!lastFound) {
                            throw LexError("Cannot found end quote in a line")
                        }
                        result.add(Token(TokenType.STRING, literalBuilder.toString(), line, literalPosition))
                        position += literalBuilder.length
                    }
                } else if (currentCursor('\'')) {
                    // todo: char literal
                } else {
                    unknownBuilder.append(currentChar)
                }
                position++
            }
            line++
            if (unknownBuilder.isNotEmpty()) {
                flush(currentLine.length)
            }
        }
        return result.toList()
    }
}