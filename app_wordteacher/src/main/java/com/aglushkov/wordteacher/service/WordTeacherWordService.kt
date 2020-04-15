package com.aglushkov.wordteacher.service

import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.repository.Config
import com.aglushkov.wordteacher.repository.ServiceMethodParams

interface WordTeacherWordService {
    val id: Int
        get() {
            return 31 * baseUrl.hashCode() + 31 * key.hashCode()
        }

    var type: Config.Type
    var baseUrl: String
    var key: String
    var methodParams: ServiceMethodParams

    suspend fun define(word: String): List<WordTeacherWord>
}