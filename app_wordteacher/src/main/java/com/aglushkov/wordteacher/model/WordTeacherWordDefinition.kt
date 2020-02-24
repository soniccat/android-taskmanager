package com.aglushkov.wordteacher.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WordTeacherWordDefinition(
    val definition: String,
    val examples: List<String>,
    val imageUrl: String?,

    val originalSources: List<Parcelable>
): Parcelable