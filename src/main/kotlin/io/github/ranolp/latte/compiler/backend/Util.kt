package io.github.ranolp.latte.compiler.backend

operator fun String.times(long: Long): String {
    val builder = StringBuilder()
    var count = long
    while (count-- > 0) {
        builder.append(this)
    }
    return builder.toString()
}

operator fun String.times(int: Int) = times(int.toLong())
operator fun String.times(short: Short) = times(short.toLong())