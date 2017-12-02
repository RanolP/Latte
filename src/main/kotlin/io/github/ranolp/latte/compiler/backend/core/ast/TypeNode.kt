package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.core.Token

sealed class TypeNode(name: String, token: Token) : Node(name, token)
sealed class TypeReferenceNode(name: String, token: Token) : TypeNode(name, token)

class FunctionTypeNode(token: Token) : TypeReferenceNode("FunctionType", token)