package com.example.testcrypto.model.datasource

import com.google.gson.annotations.SerializedName

data class CoinDetail(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: CoinImage,
    @SerializedName("description") val description: CoinDescription,
    @SerializedName("categories") val categories: List<String>
)

data class CoinImage(
    @SerializedName("large") val large: String
)

data class CoinDescription(
    @SerializedName("en") val en: String
)