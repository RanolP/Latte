package io.github.ranolp.latte.compiler.backend.core.objects

import io.github.ranolp.latte.compiler.backend.core.ast.FunctionNode
import io.github.ranolp.latte.compiler.backend.core.ast.Node
import io.github.ranolp.latte.compiler.backend.core.ast.ParentNode
import io.github.ranolp.latte.compiler.backend.core.ast.StatementNode
import io.github.ranolp.latte.compiler.backend.interpreter.LatteVM

interface LFunctionStructure : LScope {

    // name, argument
    data class Signature(val name: String, val argument: List<StatementNode>)

    val signature: Signature
    val parent: ParentNode?
    val isMethod: Boolean
        get() = parent !== null

    operator fun invoke(latteVM: LatteVM, arguments: List<Node>? = null)
}

class LFunction(functionNode: FunctionNode) : LFunctionStructure {
    override val imports = emptySet<LImport>()
    override val signature = LFunctionStructure.Signature(functionNode.functionName, functionNode.arguments)
    private val statements = functionNode.children.map { LStatement(it) }
    override val parent = functionNode.parent

    override operator fun invoke(latteVM: LatteVM, arguments: List<Node>?) {
        latteVM.inscope(this)
        statements.forEach { it.invoke(latteVM) }
        latteVM.outscope()
    }
    override fun toString(): String = signature.toString()
}

abstract class LExternFunction(name: String, override val parent: ParentNode? = null) : LFunctionStructure {
    override val imports = emptySet<LImport>()
    override val signature = LFunctionStructure.Signature(name, emptyList())

    override operator fun invoke(latteVM: LatteVM, arguments: List<Node>?) {
        latteVM.inscope(this)
        run(latteVM, arguments)
        latteVM.outscope()
    }

    abstract fun run(latteVM: LatteVM, arguments: List<Node>?)

    override fun toString(): String = signature.toString()
}
