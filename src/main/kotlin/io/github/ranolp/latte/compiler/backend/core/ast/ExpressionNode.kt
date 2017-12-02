package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.core.Token

open class ExpressionNode(name: String, token: Token, val left: DisjunctionNode, val middle: Token?, val right: DisjunctionNode?) : StatementNode(name, token)

class DisjunctionNode()

class FunctionCallNode() {

}