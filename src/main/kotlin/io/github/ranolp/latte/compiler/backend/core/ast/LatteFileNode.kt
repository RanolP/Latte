package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.backend.times
import io.github.ranolp.latte.compiler.core.Token

class LatteFileNode(token: Token, val packageNode: PackageNode?, val importNodes: List<ImportNode>, val topLevelObjects: List<TopLevelNode>) : Node(
        "LatteFile",
        token) {

    override fun debug(depth: Int): String {
        return listOf(" " * depth + "LatteFile",
                " " * depth + " Preamble",
                packageNode?.debug(depth + 2) ?: " " * depth + "  (default package)",
                importNodes.joinToString("\n") {
                    it.debug(depth + 2)
                },
                " " * depth + " Top-level object",
                if (topLevelObjects.isEmpty()) " " * depth + "  (No top-level objects)"
                else topLevelObjects.joinToString("\n") {
                    it.debug(depth + 2)
                }).joinToString("\n")
    }
}