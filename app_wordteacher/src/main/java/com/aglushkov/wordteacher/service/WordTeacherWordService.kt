package com.aglushkov.wordteacher.service

import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.repository.ServiceMethodParams

interface WordTeacherWordService {
    var key: String
    var options: ServiceMethodParams

    suspend fun define(word: String): List<WordTeacherWord>
}