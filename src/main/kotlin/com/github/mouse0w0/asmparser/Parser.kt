package com.github.mouse0w0.asmparser

import org.objectweb.asm.Opcodes

val PARSERS: List<Parser> = listOf(ClassVersionParser(), DeprecatedParser(), DeclarationParser(), ClassDeclareParser(), CompliedFromParser())

fun main(args: Array<String>) {
//    val regex = Regex("((?:public |private |protected |final |static |synchronized |volatile |transient |abstract |strictfp |synthetic |mandated |enum )*)"+
//            "([a-zA-Z0-9\$_<>]+)\\(([a-zA-Z0-9\$_, ]*)\\) : ([a-zA-Z0-9\$_]+)")
//    val matchResult = regex.find("private public println(String, int) : String")
//            ?: return
    val regex = Regex("((?:public |private |protected |final |static |abstract |synthetic |enum )+)" +
            "(class|@interface|interface) " +
            "([0-9|a-z|A-Z|_\$/]+)" +
            "(?: extends ([0-9|a-z|A-Z|_\$/]+))?" +
            "(?: implements ([([0-9|a-z|A-Z|_\$/]+ )]+))?")
    val matchResult = regex.find("public class HelloWorld extends String implement abc {")
            ?: return
    val values = matchResult.groupValues
    for (i in 0 until values.size) {
        println("$i. ${values[i]}")
    }
}

interface Parser {

    fun regex(): Regex

    fun parse(classParser: ClassParser, matchResult: MatchResult, src: String)
}

abstract class ParserBase(regex: String) : Parser {

    val regex = Regex(regex)

    override fun regex(): Regex = regex
}

class ClassDeclareParser : ParserBase("((?:public |private |protected |final |static |abstract |synthetic |enum )+)" +
        "(class|@interface|interface) " +
        "([0-9|a-z|A-Z|_\$/]+)" +
        "(?: extends ([0-9|a-z|A-Z|_\$/]+))?" +
        "(?: implements ([([0-9|a-z|A-Z|_\$/]+ )]+))?") {
    override fun parse(classParser: ClassParser, matchResult: MatchResult, src: String) {
        val deprecated = isDeprecated(classParser.properties)
        val declaration = getDeclaration(classParser.properties)

        classParser.classWriter.visit(classParser.properties["classVersion"] as Int,
                getAccesses(matchResult.groupValues[1].substringBeforeLast(' '), matchResult.groupValues[2], deprecated),
                matchResult.groupValues[3],
                declaration,
                matchResult.groupValues[4],
                matchResult.groupValues[5].split(Regex("[ ,]+")).toTypedArray())
    }

    fun getAccesses(accesses: String, classType: String, deprecated: Boolean): Int =
            when (classType) {
                "class" -> Opcodes.ACC_SUPER
                "@interface" -> Opcodes.ACC_ANNOTATION + Opcodes.ACC_ABSTRACT
                "interface" -> Opcodes.ACC_INTERFACE + Opcodes.ACC_ABSTRACT
                else -> 0
            } or getAccesses(accesses, deprecated)
}

fun isDeprecated(properties: MutableMap<String, Any?>): Boolean {
    val deprecated = properties["deprecated"] as Boolean
    if (deprecated) {
        properties["deprecated"] = false
    }
    return deprecated
}

fun getDeclaration(properties: MutableMap<String, Any?>): String? {
    val declaration = properties["declaration"] as String?
    if (declaration != null) {
        properties["declaration"] = null
    }
    return declaration
}

fun getAccesses(accesses: String, deprecated: Boolean): Int {
    var access = 0
    for (s in accesses.split(' ')) {
        access = access or getAccesses(s)
    }

    if (deprecated) {
        access = access or Opcodes.ACC_DEPRECATED
    }

    return access
}

fun getAccesses(access: String): Int = when (access) {
//"((?:public |private |protected |final |static |synchronized |volatile |transient |abstract |strictfp |synthetic |mandated |enum )+)"
    "public" -> Opcodes.ACC_PUBLIC
    "private" -> Opcodes.ACC_PRIVATE
    "protected" -> Opcodes.ACC_PROTECTED
    "final" -> Opcodes.ACC_FINAL
    "static" -> Opcodes.ACC_STATIC
    "synchronized" -> Opcodes.ACC_SYNCHRONIZED
    "volatile" -> Opcodes.ACC_VOLATILE
    "transient" -> Opcodes.ACC_TRANSIENT
    "abstract" -> Opcodes.ACC_ABSTRACT
    "strictfp" -> Opcodes.ACC_STRICT
    "synthetic" -> Opcodes.ACC_SYNTHETIC
    "mandated" -> Opcodes.ACC_MANDATED
    "enum" -> Opcodes.ACC_ENUM
    "varargs" -> Opcodes.ACC_VARARGS
//"deprecated" -> Opcodes.ACC_DEPRECATED
    else -> 0
}