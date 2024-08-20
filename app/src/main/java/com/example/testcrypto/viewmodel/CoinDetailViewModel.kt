package com.example.testcrypto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testcrypto.model.repository.RetrofitRepository
import com.example.testcrypto.viewmodel.CoinDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(private val repository: RetrofitRepository) : ViewModel() {

    private val _state = MutableStateFlow<CoinDetailState>(CoinDetailState.Loading)
    val state: StateFlow<CoinDetailState> get() = _state

    // Загружает данные о выбранной монете
    fun fetchCoinDetail(coinId: String) {
        viewModelScope.launch {
            // Устанавливаем состояние loading
            _state.value = CoinDetailState.Loading
            try {
                val coinDetail = repository.getCoinDetail(coinId)
                // Устанавливаем состояние success с полученными данынми
                _state.value = CoinDetailState.Success(coinDetail)
            } catch (e: Exception) {
                _state.value = CoinDetailState.Error(e.message ?: "Произошла ошибка при загрузке")
            }
        }
    }
}