package com.example.testcrypto.ui.coindetail

import com.example.testcrypto.data.datasource.CoinDetail

sealed class CoinDetailState {
    object Loading : CoinDetailState()
    data class Success(val coinDetail: CoinDetail) : CoinDetailState()
    data class Error(val message: String) : CoinDetailState()
}