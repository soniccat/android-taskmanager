package com.example.alexeyglushkov.quizletservice.entities

import java.io.Serializable

/**
 * Created by alexeyglushkov on 27.03.16.
 */
class QuizletUser : Serializable {
    var id: Long = 0
    var name: String? = null
    var type: String? = null
    var imageUrl: String? = null

    companion object {
        private const val serialVersionUID = 4254004416559222553L
    }
}