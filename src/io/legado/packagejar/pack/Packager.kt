//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package io.legado.packagejar.pack

import com.intellij.openapi.compiler.CompileStatusNotification

abstract class Packager : CompileStatusNotification {
    @Throws(Exception::class)
    abstract fun pack()
}