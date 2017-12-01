package io.github.ranolp.latte.compiler.backend.core.ast

import io.github.ranolp.latte.compiler.core.Token

sealed class StatementNode(name: String, token: Token) : Node(name, token)