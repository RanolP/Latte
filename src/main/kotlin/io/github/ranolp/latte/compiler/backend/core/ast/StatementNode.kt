package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.backend.times
import io.github.ranolp.latte.compiler.core.Token

class ImportNode(token: Token, names: List<Token>, asToken: Token?) : Node("Import", token, 1 + names.size) {
    val packageName: String? = if (names.size > 1) names.dropLast(1).joinToString("") { it.data } else null
    val destination: String = names.lastOrNull()?.data ?: throw IllegalArgumentException("Names must not be empty")
    val asName: String? = asToken?.data
    override fun debug(depth: Int): String = " " * depth + "ImportNode(${packageName.map { "$it." }}$destination${asName?.map { " as $it" }})"
}

class PackageNode(token: Token, names: List<Token>) : Node("Package", token, 1 + names.size) {
    val packageName: String = names.joinToString("") { it.data }
    override fun debug(depth: Int): String = " " * depth + "PackageNode($packageName)"
}