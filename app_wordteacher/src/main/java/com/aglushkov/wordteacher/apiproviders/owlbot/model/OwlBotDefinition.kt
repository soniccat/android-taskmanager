package com.aglushkov.wordteacher.apiproviders.owlbot.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class OwlBotDefinition(
    @SerializedName("definition") val definition: String?,
    @SerializedName("emoji") val emoji: String?,
    @SerializedName("example") val example: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("type") val type: String?
) : Parcelable