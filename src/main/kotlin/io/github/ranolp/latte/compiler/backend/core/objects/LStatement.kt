package io.github.ranolp.latte.compiler.backend.core.objects

import io.github.ranolp.latte.compiler.backend.core.ast.*
import io.github.ranolp.latte.compiler.backend.interpreter.LatteVM
import io.github.ranolp.latte.compiler.backend.interpreter.VMError

sealed class LStatement<out N : StatementNode>(val node: N) : LValue {
    companion object {
        operator fun invoke(statementNode: StatementNode): LStatement<*> {
            return when (statementNode) {
                is IntNode -> LInt(statementNode)
                is DecimalNode -> LDecimal(statementNode)
                is BooleanNode -> LBoolean(statementNode)
                is StringNode -> LString(statementNode)
                is FunctionCallNode -> LFunctionCall(statementNode)
                else -> throw VMError("Undefined statement found: \'${statementNode.debug()}\'")
            }
        }
    }

    abstract operator fun invoke(latteVM: LatteVM): LValue?
}

class LFunctionCall(node: FunctionCallNode) : LStatement<FunctionCallNode>(node) {
    val arguments = node.arguments
    override fun invoke(latteVM: LatteVM): LValue? {
        val scope = latteVM.scope()
        var invoked = false
        when (scope) {
            is LFunction -> {
                if (node.thisRef === null) {
                    if (scope.isMethod) {
                        // todo: implicit this
                    }
                    if (!invoked) {
                        latteVM.imports.flatMap { it.toFunctions(latteVM) }.firstOrNull { it.signature.name == node.functionName }?.let {
                            invoked = true
                            it(latteVM, arguments)
                        }
                    }
                }
            }
        }
        if (!invoked) {
            throw VMError("Function not found : ${node.functionName}")
        }
        return null
    }
}

sealed class LLiteral<out T, out N : LiteralNode>(node: N) : LStatement<N>(node) {
    override fun invoke(latteVM: LatteVM): LValue = this

    abstract fun toJavaObject(): T?
}

sealed class LNumber<out T : Number, out N : NumberNode>(node: N) : LLiteral<T, N>(node)

class LInt(node: IntNode) : LNumber<Int, IntNode>(node) {
    override fun toJavaObject(): Int? = node.value
}

class LDecimal(node: DecimalNode) : LNumber<Double, DecimalNode>(node) {
    override fun toJavaObject(): Double? = node.value
}

class LBoolean(node: BooleanNode) : LLiteral<Boolean, BooleanNode>(node) {
    override fun toJavaObject(): Boolean? = node.value
}

class LString(node: StringNode) : LLiteral<String, StringNode>(node) {
    override fun toJavaObject(): String? = node.value
}
