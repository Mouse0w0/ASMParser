package com.github.mouse0w0.asmparser

import joptsimple.OptionParser
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths

val LOGGER = LoggerFactory.getLogger("ASMParser")

fun main(args: Array<String>) {
    val optionParser = OptionParser()
    val target = optionParser.accepts("target").withRequiredArg().defaultsTo("src")
    val output = optionParser.accepts("output").withOptionalArg().defaultsTo("build")
    val optionSet = optionParser.parse(*args)

    val targetPath = Paths.get(target.value(optionSet))
    val outputPath = Paths.get(output.value(optionSet))

    if(!Files.exists(targetPath)) {
        LOGGER.error("Target path isn't exists. {}", targetPath)
        return
    }

    val asmParser = ASMParser(targetPath, outputPath)
}