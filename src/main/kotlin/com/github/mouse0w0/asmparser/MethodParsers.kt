package com.github.mouse0w0.asmparser

class MethodDeclareParser : ParserBase("((?:public |private |protected |final |static |synchronized |abstract |synthetic |mandated )*)" +
        "([a-zA-Z0-9\$_<>]+)\\(([a-zA-Z0-9\$_, ]*)\\) : ([a-zA-Z0-9\$_]+)") {
    override fun parse(classParser: ClassParser, matchResult: MatchResult, src: String) {

    }

}
