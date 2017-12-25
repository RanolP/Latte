package io.github.ranolp.latte.compiler.frontend

import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType
import java.lang.StringBuilder

object Lexer {
    private val CHAR_MAP: Map<Char, TokenType> = TokenType.values().filter { it.char != null }.associateBy { it.char!! }
    private val KEYWORDS: Map<String, TokenType> = TokenType.values().filter { it.keyword }.associateBy { it.name.toLowerCase() }

    private fun Char.isIgnoreable() = isWhitespace() || this in CHAR_MAP.keys

    private fun query(line: String, from: Int): Pair<String, TokenType>? {
        if (from > 0 && !line[from - 1].isIgnoreable()) {
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
                result.add(Token(TokenType.IDENTIFIER,
                        unknownBuilder.toString(),
                        line,
                        position - unknownBuilder.length))
                unknownBuilder.setLength(0)
            }
        }
        while (line < lines.size) {
            var position = 0
            val currentLine = lines[line]
            while (position < currentLine.length) {
                val currentChar = currentLine[position]
                fun validateCursor(cursor: Int = position) = cursor < currentLine.length
                fun hasNext(pos: Int = position) = validateCursor(pos + 1)
                fun nextValue(pos: Int = position) = currentLine[pos + 1]
                fun next(pos: Int = position) = nextValue(pos).also { position++ }
                fun next(char: Char, pos: Int = position) = hasNext(pos) && nextValue(pos) == char
                fun nextCursor(char: Char, pos: Int = position) = next(char, pos).also { if (it) position++ }
                fun current(char: Char) = currentChar == char
                fun currentCursor(char: Char) = current(char).also { if (it) position++ }
                fun current(charRange: CharRange) = currentChar in charRange
                fun currentCursor(charRange: CharRange) = current(charRange).also { if (it) position++ }
                fun flush() = flush(position)
                val queried = query(currentLine, position)
                if (queried != null) {
                    val temp = position
                    flush()
                    position += queried.first.length - 1
                    result.add(Token(queried.second, queried.first, line, temp))
                } else if (CHAR_MAP[currentChar] != null) {
                    flush()
                    result.add(Token(CHAR_MAP[currentChar]!!, currentChar.toString(), line, position))
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
                                // todo: extract to function
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
                } else if (current('0'..'9')) {
                    val literalPosition = position
                    val first = currentChar
                    // todo: hex, oct, bin int literal
                    val literalBuilder = StringBuilder()
                    var dotted = false
                    for (pos in position until currentLine.length) {
                        val ch = currentLine[pos]
                        if (ch == '.' && !dotted) {
                            dotted = true
                        } else if (ch !in '0'..'9') {
                            break
                        }
                        literalBuilder.append(ch)
                    }
                    result.add(Token(if (dotted) TokenType.DECIMAL else TokenType.INTEGER,
                            literalBuilder.toString(),
                            line,
                            literalPosition))
                    position += literalBuilder.length - 1
                } else {
                    unknownBuilder.append(currentChar)
                }
                position++
            }
            if (unknownBuilder.isNotEmpty()) {
                flush(currentLine.length)
            }
            if (line + 1 < lines.size) {
                result.add(Token(TokenType.LINEFEED, "\n", line, currentLine.length))
            }
            line++
        }
        result += Token(TokenType.EOF, "", line, 0)
        return result.toList()
    }
}
