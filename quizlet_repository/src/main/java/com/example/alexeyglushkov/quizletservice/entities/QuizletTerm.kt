package com.example.alexeyglushkov.quizletservice.entities

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by alexeyglushkov on 02.04.16.
 */
@Parcelize
data class QuizletTerm(
    var id: Long = 0,
    var setId: Long = 0,
    var term: String? = null,
    var definition: String? = null,
    var rank: Int = 0): Parcelable, Serializable {

    companion object {
        private const val serialVersionUID = -6211744973878309135L
    }
}