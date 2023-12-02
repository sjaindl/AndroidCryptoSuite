package com.sjaindl.cryptosuite

import kotlin.experimental.and

fun ByteArray.print(algorithm: String) {
    val sb = StringBuilder()
    sb.append("$algorithm: ")

    for (element in asList()) {
        sb.append(((element and 0xff.toByte()) + 0x100).toString(16).substring(1))
    }

    println(sb.toString())
}
