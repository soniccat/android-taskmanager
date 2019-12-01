package com.example.alexeyglushkov.quizletservice.deserializers

import com.example.alexeyglushkov.jacksonlib.CustomDeserializer
import com.example.alexeyglushkov.quizletservice.entities.QuizletUser
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import java.io.IOException

/**
 * Created by alexeyglushkov on 27.03.16.
 */
class QuizletUserDeserializer(vc: Class<*>) : CustomDeserializer<QuizletUser>(vc) {
    override fun createObject(): QuizletUser {
        return QuizletUser()
    }

    @Throws(IOException::class)
    protected override fun handle(p: JsonParser, ctxt: DeserializationContext, user: QuizletUser): Boolean {
        var isHandled = false
        val name = p.currentName
        if (name == "id") {
            user.id = _parseLongPrimitive(p, ctxt)
            isHandled = true
        } else if (name == "username") {
            user.name = _parseString(p, ctxt)
            isHandled = true
        } else if (name == "profile_image") {
            user.imageUrl = _parseString(p, ctxt)
            isHandled = true
        } else if (name == "account_type") {
            user.type = _parseString(p, ctxt)
            isHandled = true
        }
        return isHandled
    }

    companion object {
        private const val serialVersionUID = 4365096740984693871L
    }
}