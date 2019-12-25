package com.example.alexeyglushkov.quizletservice.tasks

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import com.example.alexeyglushkov.authtaskmanager.HttpServiceCommand
import com.example.alexeyglushkov.quizletservice.QuizletSetsCommand
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletSetDeserializer
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletTermDeserializer
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletUserDeserializer
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm
import com.example.alexeyglushkov.quizletservice.entities.QuizletUser
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import java.io.IOException
import java.util.*

/**
 * Created by alexeyglushkov on 03.04.16.
 */
class QuizletSetsCommand(server: String, userId: String) : HttpServiceCommand<List<QuizletSet>>(createBuilder(server, userId), createHandler()), QuizletSetsCommand {
    companion object {
        private fun createBuilder(server: String, userId: String): HttpUrlConnectionBuilder {
            val builder = HttpUrlConnectionBuilder()
            val url = "$server/users/$userId/sets"
            builder.setUrl(url)

            return builder
        }

        private fun createHandler(): ByteArrayHandler<List<QuizletSet>> {
            return ByteArrayHandler { bytes -> Arrays.asList(*parseSets(bytes)) }
        }

        private fun parseSets(bytes: ByteArray): Array<QuizletSet> {
            var result: Array<QuizletSet>? = null
            try {
                val md = SimpleModule("QuizletModule", Version(1, 0, 0, null, null, null))
                md.addDeserializer(QuizletSet::class.java, QuizletSetDeserializer(QuizletSet::class.java))
                md.addDeserializer(QuizletUser::class.java, QuizletUserDeserializer(QuizletUser::class.java))
                md.addDeserializer(QuizletTerm::class.java, QuizletTermDeserializer(QuizletTerm::class.java))

                val mapper = ObjectMapper()
                mapper.registerModule(md)

                result = mapper.readValue(bytes, Array<QuizletSet>::class.java)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return result ?: emptyArray()
        }
    }
}