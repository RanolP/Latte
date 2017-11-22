package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.core.Token

class IntNode(token: Token) : Node("Int", token, 1)
class DecimalNode(token: Token) : Node("Decimal", token, 1)
class StringNode(token: Token) : Node("String", token, 1)