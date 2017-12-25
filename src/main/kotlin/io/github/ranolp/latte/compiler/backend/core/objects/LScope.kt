package io.github.ranolp.latte.compiler.backend.core.objects

interface LScope : LValue {
    val imports: Set<LImport>
}
