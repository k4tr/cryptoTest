package com.example.testcrypto.model.repository
import com.example.testcrypto.model.datasource.CoinApi
import com.example.testcrypto.model.datasource.CoinData
import com.example.testcrypto.model.datasource.CoinDetail
import javax.inject.Inject

class RetrofitRepository @Inject constructor(
    private var api: CoinApi
) {
    /**
     * Получение списка криптовалют по заданной валюте.
     *
     * @param currency Код валюты для запроса (usd или rub)
     * @return Список объектов CoinData, содержащий информацию о криптовалютах
     */
    suspend fun getCoins(currency: String): List<CoinData> {
        return api.getCoins(vsCurrency = currency)
    }
    /**
     * Получение подробной информации о криптовалюте по её id
     * @return Объект CoinDetail, содержащий подробную информацию о криптовалюте
     */
    suspend fun getCoinDetail(id: String): CoinDetail {
        return api.getCoinDetail(id)
    }
}