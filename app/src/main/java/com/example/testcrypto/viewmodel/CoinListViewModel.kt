package com.example.testcrypto.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testcrypto.model.datasource.CoinData
import com.example.testcrypto.model.repository.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CoinListState {
    object Loading : CoinListState()
    data class Success(val coins: List<CoinData>) : CoinListState()
    data class Error(val message: String) : CoinListState()
}
@HiltViewModel
class CoinListViewModel  @Inject constructor(private val repository: RetrofitRepository) : ViewModel() {

    // Хранит текущую выбранную валюту.По умолчанию usd
    private val _currency = MutableStateFlow("usd")
    val currency: StateFlow<String> = _currency

    // Хранит состояние экрана со списком монет (loading, success или error)
    private val _state = MutableLiveData<CoinListState>()
    val state: LiveData<CoinListState> get() = _state

    // Загружаем список монет с валютой по умолчанию usd
    init {
        fetchCoins("usd")
    }

    // Устанавливает выбранную валюту и загружает данные
    fun setCurrency(newCurrency: String) {
        _currency.value = newCurrency
        fetchCoins(newCurrency)
    }

    // Загружает список монет для указанной валюты
    fun fetchCoins(currency: String) {
        viewModelScope.launch {
            // Устанавливаем состояние loading
            _state.value = CoinListState.Loading
            try {
                val coins = repository.getCoins(currency)
                // Устанавливаем состояние success с полученным списком
                _state.value = CoinListState.Success(coins)
            } catch (e: Exception) {
                _state.value = CoinListState.Error(e.message ?: "Произошла ошибка при загрузке")
            }
        }
    }
}