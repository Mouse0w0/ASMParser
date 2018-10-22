package com.github.mouse0w0.asmparser

class ClassVersionParser : ParserBase("// class version ([0-9|\\.]+) \\(([0-9]+)\\)") {
    override fun parse(classParser: ClassParser, matchResult: MatchResult, src: String) {
        classParser.properties["classVersion"] = matchResult.groupValues[2].toInt()
    }
}

class CompliedFromParser : ParserBase("// compiled from: ([a-z|A-Z|\\.]+)") {
    override fun parse(classParser: ClassParser, matchResult: MatchResult, src: String) {
        classParser.classWriter.visitSource(matchResult.groupValues[1], null)
    }
}

class DeprecatedParser : ParserBase("// DEPRECATED") {
    override fun parse(classParser: ClassParser, matchResult: MatchResult, src: String) {
        classParser.properties["deprecated"] = true
    }
}

class DeclarationParser : ParserBase("// declaration: ([ _\$/<>\\.a-zA-Z0-9]*)") {
    override fun parse(classParser: ClassParser, matchResult: MatchResult, src: String) {
        classParser.properties["declaration"] = matchResult.groupValues[1]
    }
}