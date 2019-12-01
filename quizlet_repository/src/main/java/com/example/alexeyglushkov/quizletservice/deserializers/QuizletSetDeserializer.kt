package com.example.alexeyglushkov.quizletservice.deserializers

import com.example.alexeyglushkov.jacksonlib.CustomDeserializer
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm
import com.example.alexeyglushkov.quizletservice.entities.QuizletUser
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by alexeyglushkov on 27.03.16.
 */
class QuizletSetDeserializer(vc: Class<QuizletSet>) : CustomDeserializer<QuizletSet>(vc) {
    override fun createObject(): QuizletSet {
        return QuizletSet()
    }

    @Throws(IOException::class)
    protected override fun handle(p: JsonParser, ctxt: DeserializationContext, set: QuizletSet): Boolean {
        val mapper = p.codec as ObjectMapper
        val name = p.currentName
        var isHandled = false
        if (name == "id") {
            set.id = _parseLongPrimitive(p, ctxt)
            isHandled = true
        } else if (name == "title") {
            set.title = _parseString(p, ctxt)
            isHandled = true
        } else if (name == "created_date") {
            set.createDate = _parseLong(p, ctxt)
            isHandled = true
        } else if (name == "modified_date") {
            set.modifiedDate = _parseLong(p, ctxt)
            isHandled = true
        } else if (name == "published_date") {
            set.publishedDate = _parseLong(p, ctxt)
            isHandled = true
        } else if (name == "has_images") {
            set.isHasImages = _parseBooleanPrimitive(p, ctxt)
            isHandled = true
        } else if (name == "can_edit") {
            set.isCanEdit = _parseBooleanPrimitive(p, ctxt)
            isHandled = true
        } else if (name == "has_access") {
            set.isHasAccess = _parseBooleanPrimitive(p, ctxt)
            isHandled = true
        } else if (name == "description") {
            set.description = _parseString(p, ctxt)
            isHandled = true
        } else if (name == "lang_terms") {
            set.langTerms = _parseString(p, ctxt)
            isHandled = true
        } else if (name == "lang_definitions") {
            set.langDefs = _parseString(p, ctxt)
            isHandled = true
        } else if (name == "creator") {
            val creator = mapper.readValue(p, QuizletUser::class.java)
            set.creator = creator
            isHandled = true
        } else if (name == "terms") {
            val terms = mapper.readValue(p, Array<QuizletTerm>::class.java)
            set.terms = ArrayList(Arrays.asList(*terms))
            for (term in terms) {
                term.setId = set.id
            }
            isHandled = true
        }
        return isHandled
    }

    companion object {
        private const val serialVersionUID = 1600472684113690561L
    }
}