package io.github.ranolp.latte.compiler.backend.core.objects

import io.github.ranolp.latte.compiler.backend.core.ast.PackageNode

class LPackage private constructor(val packageName: String?) : LValue {
    companion object {
        val DEFAULT = LPackage(null)
        private val cachedPackages = mutableMapOf<String, LPackage>()

        operator fun invoke(packageNode: PackageNode?): LPackage {
            return invoke(packageNode?.packageName)
        }

        operator fun invoke(packageName: String?): LPackage {
            return if (packageName === null) DEFAULT
            else cachedPackages.getOrPut(packageName, { LPackage(packageName) })
        }
    }
}
