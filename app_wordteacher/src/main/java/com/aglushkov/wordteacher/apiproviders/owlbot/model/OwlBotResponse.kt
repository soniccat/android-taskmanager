package com.aglushkov.wordteacher.apiproviders.owlbot.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class OwlBotResponse(
    @SerializedName("definitions") val definitions: List<OwlBotDefinition>,
    @SerializedName("pronunciation") val pronunciation: String?,
    @SerializedName("word") val word: String?
) : Parcelable