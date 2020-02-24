package com.aglushkov.wordteacher.model

interface WordTeacherWordDefinition {
    val definition: String
    val imageUrl: String?
    val examples: List<String>

    val originalSources: List<Any>
}