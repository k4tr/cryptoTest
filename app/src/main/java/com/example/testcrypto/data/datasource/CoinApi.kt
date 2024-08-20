package com.example.testcrypto.data.datasource
import com.example.testcrypto.data.datasource.CoinData
import com.example.testcrypto.data.datasource.CoinDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
interface CoinApi {
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") vsCurrency: String,
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false,
        @Query("price_change_percentage") priceChangePercentage: String = "24h"
    ): List<CoinData>
    @GET("coins/{id}")
    suspend fun getCoinDetail(@Path("id") id: String): CoinDetail
}