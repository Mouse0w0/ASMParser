package com.github.mouse0w0.asmparser

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor

val SPACE_REGEX = Regex("[ ]+")

class ClassParser(val parsers: List<Parser>) {

    val properties: MutableMap<String, Any?> = hashMapOf()

    var classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)

    var currentMethod : MethodVisitor? = null
    var currentField : FieldVisitor? = null
    var currentAnnotation : AnnotationVisitor? = null

    fun parse(lines: List<String>): ByteArray {
        for (line in lines) {
            val src = line.replace(SPACE_REGEX, " ")
            for (parser in parsers) {
                val matcher = parser.regex().find(src)
                if (matcher != null) {
                    parser.parse(this, matcher, src)
                    break
                }
            }
        }
        return classWriter.toByteArray()
    }

    fun clear() {
        properties.clear()
        classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
    }
}