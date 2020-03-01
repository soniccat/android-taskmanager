package com.aglushkov.wordteacher.service

import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.repository.ServiceMethodParams

interface WordTeacherWordService {
    var baseUrl: String
    var key: String
    var methodParams: ServiceMethodParams

    suspend fun define(word: String): List<WordTeacherWord>
}