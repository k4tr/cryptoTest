package com.example.testcrypto.model.datasource

import com.google.gson.annotations.SerializedName

data class CoinData(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("current_price") val currentPrice: Double,
    @SerializedName("image") val image: String,
    @SerializedName("price_change_percentage_24h") val priceChangePercentage24h: Double
)
