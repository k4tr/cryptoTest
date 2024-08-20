package com.example.testcrypto.data.repository
import com.example.testcrypto.data.datasource.CoinApi
import com.example.testcrypto.data.datasource.CoinData
import com.example.testcrypto.data.datasource.CoinDetail
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RetrofitRepository @Inject constructor(
    private var api: CoinApi
) {

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(CoinApi::class.java)
    }

    suspend fun getCoins(currency: String): List<CoinData> {
        return api.getCoins(vsCurrency = currency)
    }

    suspend fun getCoinDetail(id: String): CoinDetail {
        return api.getCoinDetail(id)
    }
}