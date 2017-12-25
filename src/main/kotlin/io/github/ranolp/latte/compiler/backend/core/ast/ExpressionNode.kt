package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.backend.times
import io.github.ranolp.latte.compiler.core.Token

open class ExpressionNode(name: String, token: Token, val left: DisjunctionNode, val middle: Token?, val right: DisjunctionNode?) : StatementNode(
        name,
        token)

class DisjunctionNode()

// TODO: named arguments
class FunctionCallNode(val thisRef: Node?, token: Token, val arguments: List<Node>) : StatementNode("FunctionCall",
        token) {
    val functionName = token.data
    override fun debug(depth: Int): String = " " * depth + "FunctionCall(${thisRef?.toString().map { "$it::" }}$functionName)\n" + arguments.joinToString(
            "\n") { it.debug(depth + 1) }
}

// || &&
// == != === !== > >= < <=
// in is (named infix)
// ?:
// infix fun
// A..B A..
// + -
// * / %
// as as? :
// -x +x ++x --x !x @Annot x label@x
// A?::B(c,d)
// (x) literal {lambda -> good} this@asdf super@asdf if when try object throw return continue break for while dowhile name
