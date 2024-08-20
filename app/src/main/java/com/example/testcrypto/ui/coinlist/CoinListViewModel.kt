package com.example.testcrypto.ui.coinlist
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testcrypto.data.datasource.CoinData
import com.example.testcrypto.data.repository.RetrofitRepository
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

    private val _currency = MutableStateFlow("usd")
    val currency: StateFlow<String> = _currency

    private val _state = MutableLiveData<CoinListState>()
    val state: LiveData<CoinListState> get() = _state

    init {
        fetchCoins("usd")
    }

    fun setCurrency(newCurrency: String) {
        _currency.value = newCurrency
        fetchCoins(newCurrency)
    }

    fun fetchCoins(currency: String) {
        viewModelScope.launch {
            _state.value = CoinListState.Loading
            try {
                val coins = repository.getCoins(currency)
                _state.value = CoinListState.Success(coins)
            } catch (e: Exception) {
                _state.value = CoinListState.Error(e.message ?: "Произошла ошибка при загрузке")
            }
        }
    }
}