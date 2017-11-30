package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.backend.times
import io.github.ranolp.latte.compiler.core.Token

class LatteFileNode(token: Token, skips: Int, val packageNode: PackageNode?, val importNodes: List<ImportNode>) : Node("LatteFile",
        token,
        skips) {

    override fun debug(depth: Int): String {
        return listOf(" " * depth + "LatteFile",
                packageNode?.debug(depth + 1) ?: " " * (depth + 1) + "(default package)",
                importNodes.joinToString("\n") {
                    it.debug(depth + 1)
                }).joinToString("\n")
    }
}