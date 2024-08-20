package com.example.testcrypto.viewmodel

import com.example.testcrypto.model.datasource.CoinDetail

sealed class CoinDetailState {
    object Loading : CoinDetailState()
    data class Success(val coinDetail: CoinDetail) : CoinDetailState()
    data class Error(val message: String) : CoinDetailState()
}