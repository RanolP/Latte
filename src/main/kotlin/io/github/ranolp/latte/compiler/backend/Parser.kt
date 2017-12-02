package io.github.ranolp.latte.compiler.backend

import io.github.ranolp.latte.compiler.backend.core.ast.*
import io.github.ranolp.latte.compiler.core.Token
import io.github.ranolp.latte.compiler.core.TokenType
import io.github.ranolp.latte.compiler.core.TokenType.*

private typealias Test = List<String>
object Parser {
    private data class ParseContext(val tokens: List<Token>, var cursor: Int) {
        val size: Int
            get() = tokens.size
        val current: Token
            get() = get(cursor)
        val hasNext: Boolean
            get() = safe(cursor + 1)
        val before: Token
            get() = get(cursor - 1)
        val next: Token
            get() = get(cursor + 1)
        val isEOL: Boolean
            get() = when (current.type) {
                SEMICOLON, LINEFEED -> true
                else -> false
            }

        fun safe(c: Int = cursor): Boolean = c < size
        operator fun get(index: Int): Token = tokens[index]
        fun current(tokenType: TokenType): Boolean = current.type === tokenType
        fun next(tokenType: TokenType): Boolean = next.type === tokenType
        fun current(tokenType: TokenType, cause: String): Token = if (current(tokenType)) {
            get(cursorNext())
        } else throw ParseError(cause)

        fun next(tokenType: TokenType, cause: String): Token = if (next(tokenType)) {
            cursorNext()
            current
        } else throw ParseError(cause)

        fun cursorNext(): Int = ++cursor
        fun <T> cursorNext(execution: ParseContext.() -> T): T {
            val result = execution()
            cursorNext()
            return result
        }

        fun skipWhitespace() {
            while (isEOL) {
                cursorNext()
            }
        }

        fun <T> sandbox(run: ParseContext.() -> T?): T? {
            val from = cursor
            val result = run(this)
            if (result === null) {
                cursor = from
                return null
            }
            return result
        }

        fun <T> sandboxList(run: ParseContext.() -> List<T>): List<T> {
            val from = cursor
            val result = run(this)
            if (result.isEmpty()) {
                cursor = from
            }
            return result
        }
    }


    // Preamble

    private fun packageDeclaration(parseContext: ParseContext): PackageNode? {
        // todo: package declaration can has a annotation
        return parseContext.sandbox {
            if (current(PACKAGE)) {
                val start = current
                cursorNext()
                val names = resolveIdentifiers(this, DOT)
                if (names.isEmpty()) {
                    throw ParseError("Package name must not be empty")
                }
                PackageNode(start, names)
            } else {
                null
            }
        }
    }

    private fun importDeclaration(parseContext: ParseContext): ImportNode? {
        return parseContext.sandbox {
            if (current(IMPORT)) {
                val start = current
                cursorNext()
                val names = resolveIdentifiers(this, DOT)
                if (names.isEmpty()) {
                    throw ParseError("Imported name must not be empty")
                }
                val asToken = if (current(AS)) {
                    cursorNext { next(IDENTIFIER, "Import local renaming requires a identifier") }
                } else {
                    null
                }
                if (!isEOL) {
                    throw ParseError("Import statement not ends in a line.")
                }
                ImportNode(start, names, asToken)
            } else {
                null
            }
        }
    }

    private fun topLevelObjects(parseContext: ParseContext): TopLevelNode? {
        return functionDeclaration(parseContext)
    }

    private fun functionDeclaration(parseContext: ParseContext, parent: ParentNode? = null): FunctionNode? {
        // todo: generic function
        // todo: modifier
        // todo: argument
        return parseContext.sandbox {
            if (current(FN)) {
                val token = current
                val name = next(IDENTIFIER, "Function name must be a identifier").data
                val arguments: List<Node> = if (current(LEFT_BRACKET)) {
                    // argument
                    emptyList()
                } else emptyList()
                val childrens: List<Node> = if (current(ASSIGN)) {
                    emptyList()
                } else {
                    block(this)
                }
                if (childrens.isEmpty()) {
                    throw ParseError("Function must have a function body")
                }
                FunctionNode(token, parent, name, arguments, childrens)
            } else {
                null
            }
        }
    }

    private fun block(parseContext: ParseContext): List<StatementNode> {
        return parseContext.sandbox {
            if (current(LEFT_CURLY_BRACE)) {
                cursorNext()
                val statements = statements(this)
                if (current(RIGHT_CURLY_BRACE)) {
                    cursorNext()
                    statements
                } else null
            } else null
        } ?: emptyList()
    }

    private fun statements(parseContext: ParseContext): List<StatementNode> = greed(parseContext, this::statement)

    private fun statement(parseContext: ParseContext): StatementNode? {
        return expression(parseContext)
    }

    private fun expression(parseContext: ParseContext): ExpressionNode? {

    }

    private fun functionCall(parseContext: ParseContext)

    private fun disjunction(parseContext: ParseContext): ExpressionNode? {

    }
    // literal

    private fun int(token: Token): IntNode? = if (token.type === INTEGER) IntNode(token) else null
    private fun decimal(token: Token): DecimalNode? = if (token.type === DECIMAL) DecimalNode(token) else null
    private fun string(token: Token): StringNode? = if (token.type === STRING) StringNode(token) else null
    private fun boolean(token: Token): BooleanNode? = if (token.type in listOf(TRUE,
            FALSE)) BooleanNode(token) else null

    private fun literal(token: Token): LiteralNode? = int(token) ?: decimal(token) ?: string(token) ?: boolean(token)

    // types
    private fun type(parseContext: ParseContext): TypeNode? {
        return typeReference(parseContext)
    }

    private fun typeReference(parseContext: ParseContext): TypeReferenceNode? {
        return parseContext.sandbox {
            if (current(LEFT_BRACKET)) {
                cursorNext()
                val ref = typeReference(this)
                if (current(RIGHT_BRACKET)) {
                    ref
                } else {
                    null
                }
            } else {
                null
            }
        } ?: functionType(parseContext)
    }

    private fun functionType(parseContext: ParseContext): FunctionTypeNode? {
        return null
    }

    // Utilities

    private fun resolveIdentifiers(parseContext: ParseContext, separator: TokenType): List<Token> {
        val result = mutableListOf<Token>()
        loop@ while (parseContext.safe() && parseContext.current(IDENTIFIER)) {
            result += parseContext.current
            if (parseContext.hasNext) {
                val curr = parseContext.next
                when (curr.type) {
                    SEMICOLON, LINEFEED -> break@loop
                    separator -> {
                        result += parseContext[parseContext.cursorNext()]
                    }
                    else -> {
                        // loop will break
                    }
                }
            }
            parseContext.cursorNext()
        }
        if (parseContext.current(IDENTIFIER)) {
            parseContext.cursorNext()
        }
        return result
    }

    private fun <T : Node> greed(parseContext: ParseContext, function: (ParseContext) -> T?): List<T> {
        return parseContext.sandboxList {
            val results = mutableListOf<T>()
            var result = function(parseContext)
            while (result !== null) {
                results += result
                skipWhitespace()
                result = function(this)
            }
            results
        }
    }

    private fun accessModifier(parseContext: ParseContext): Token? = when (parseContext.current.type) {
        PUBLIC, PROTECTED, PRIVATE -> {
            parseContext.cursorNext { current }
        }
        else -> {
            null
        }
    }


    fun parse(tokens: List<Token>): Node? {
        val context = ParseContext(tokens, 0)
        context.skipWhitespace()
        // Preamble
        val pkg = packageDeclaration(context)
        context.skipWhitespace()
        val imports = greed(context, this::importDeclaration)
        val topLevelNodes = greed(context, this::topLevelObjects)

        // Top-level objects
        // class
        // object
        // package-level function
        // package-level property
        // package-level typealias

        if (context.current(EOF)) {
            // throw ParseError("Unused tokens found")
        }

        return LatteFileNode(tokens.last(), pkg, imports, topLevelNodes)
    }
}

