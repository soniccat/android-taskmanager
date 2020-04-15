package com.aglushkov.wordteacher.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WordTeacherDefinition(
    val definitions: List<String>,
    val examples: List<String>,
    val synonyms: List<String>,
    val imageUrl: String?,

    val originalSources: List<Parcelable>
): Parcelable