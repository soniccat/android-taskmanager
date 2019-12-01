package com.example.alexeyglushkov.quizletservice.entities

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by alexeyglushkov on 27.03.16.
 */
@Parcelize
data class QuizletSet(
    var id: Long = 0,
    var title: String? = null,
    var createDate: Long = 0,
    var modifiedDate: Long = 0,
    var publishedDate: Long = 0,
    var isHasImages: Boolean = false,
    var isCanEdit: Boolean = false,
    var isHasAccess: Boolean = false,
    var description: String? = null,
    var langTerms: String? = null,
    var langDefs: String? = null,
    var creator: QuizletUser? = null,
    var terms: List<QuizletTerm> = emptyList()): Parcelable, Serializable {

    companion object {
        private const val serialVersionUID = -1874535916609130435L
    }
}