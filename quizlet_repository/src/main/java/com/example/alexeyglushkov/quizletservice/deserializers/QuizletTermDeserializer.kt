package com.example.alexeyglushkov.quizletservice.deserializers

import com.example.alexeyglushkov.jacksonlib.CustomDeserializer
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import java.io.IOException

/**
 * Created by alexeyglushkov on 02.04.16.
 */
class QuizletTermDeserializer(vc: Class<*>) : CustomDeserializer<QuizletTerm>(vc) {
    override fun createObject(): QuizletTerm {
        return QuizletTerm()
    }

    @Throws(IOException::class)
    protected override fun handle(p: JsonParser, ctxt: DeserializationContext, term: QuizletTerm): Boolean {
        var isHandled = false
        val name = p.currentName
        if (name == "id") {
            term.id = _parseLongPrimitive(p, ctxt)
            isHandled = true
        } else if (name == "term") {
            term.term = _parseString(p, ctxt)
            isHandled = true
        } else if (name == "definition") {
            term.definition = _parseString(p, ctxt)
            isHandled = true
        } else if (name == "rank") {
            term.rank = _parseIntPrimitive(p, ctxt)
            isHandled = true
        }
        return isHandled
    }

    companion object {
        private const val serialVersionUID = -6788551863193717342L
    }
}