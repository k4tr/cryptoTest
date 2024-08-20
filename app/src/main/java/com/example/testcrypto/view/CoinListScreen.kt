package com.example.testcrypto.view
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import kotlin.math.absoluteValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.testcrypto.R
import com.example.testcrypto.model.datasource.CoinData
import com.example.testcrypto.viewmodel.CoinListState
import com.example.testcrypto.viewmodel.CoinListViewModel
import java.text.NumberFormat
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinListScreen(
    navController: NavController,
    viewModel: CoinListViewModel = hiltViewModel(),
    onCoinSelected: (CoinData) -> Unit
) {
    // Состояние экрана
    val state by viewModel.state.observeAsState(CoinListState.Loading)
    val currencyState = viewModel.currency.collectAsState().value
    // Состояние для pull to refresh и snackbar
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)
    val snackbarHostState = remember { SnackbarHostState() }
    var isRefreshAction by remember { mutableStateOf(false) }

    // Переменная для отображения валюты
    val currencySymbol = when (currencyState) {
        "usd" -> "$"
        "rub" -> "₽"
        else -> "$"
    }

    // Показываем Snackbar в случае ошибки из-за pull to refresh
    LaunchedEffect(state) {
        if (state is CoinListState.Error && isRefreshAction) {
            snackbarHostState.showSnackbar("Произошла ошибка при загрузке")
            isRefreshAction = false
        }
    }

    // Обновление данных при Pull to Refresh
    fun refreshData() {
        isRefreshAction = true
        viewModel.fetchCoins(currencyState)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список криптовалют", color = Color.Black) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }, snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                }
            )
        }
    ) { innerPadding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                swipeRefreshState.isRefreshing = true
                refreshData()
                swipeRefreshState.isRefreshing = false
            }
        ) {
            Column(modifier = Modifier.padding(innerPadding)) {
                // Chips для выбора валюты
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    CurrencyChip(
                        currency = "USD",
                        isSelected = currencyState == "usd",
                        onSelected = { viewModel.setCurrency("usd") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CurrencyChip(
                        currency = "RUB",
                        isSelected = currencyState == "rub",
                        onSelected = { viewModel.setCurrency("rub") }
                    )
                }
                // Разделитель тулбара и списка монет
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                // Состояния списка монет
                when (state) {
                    is CoinListState.Loading -> {
                        // Отображение индикатора загрузки
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFFFFAD25))
                        }
                    }
                    is CoinListState.Error -> {
                        // Отображение сообщения об ошибке
                        ErrorScreen(
                            errorMessage = (state as CoinListState.Error).message,
                            onRetry = { viewModel.fetchCoins(currencyState) }
                        )
                    }
                    is CoinListState.Success -> {
                        // Отображение списка монет
                        val coins = (state as CoinListState.Success).coins
                        LazyColumn {
                            items(coins) { coin ->
                                CoinListItem(
                                    coin = coin,
                                    currencySymbol = currencySymbol
                                ) {
                                    onCoinSelected(it) // Передача выбранной монеты в обработчик клика
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun CoinListItem(
    coin: CoinData,
    currencySymbol: String,
    onClick: (CoinData) -> Unit // Функция, которая будет вызываться при нажатии на элемент списка
) {
    // Контейнер для элемента списка
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(coin) } // Обработчик клика по элементу
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Отображение иконки криптовалюты
            AsyncImage(
                model = coin.image,
                contentDescription = coin.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Отображение названия криптовалюты
            Column {
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        // Колонка для отображения цены и процента изменения цены
        Column(
            horizontalAlignment = Alignment.End
        ) {
            // Отображение текущей цены криптовалюты
            Text(
                text = "$currencySymbol${coin.currentPrice.formatWithCommas()}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            // Отображение процента изменения цены под ценой
            Text(
                text = "${coin.priceChangePercentage24h.formatPercentage()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = if (coin.priceChangePercentage24h >= 0) Color.Green else Color.Red
            )
        }
    }
}

// Формат цены с разделением на порядки
fun Double.formatWithCommas(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return formatter.format(this)
}
// Формат процента с двумя знаками после запятой
fun Double.formatPercentage(): String {
    val sign = if (this < 0) "-" else "+"
    return "$sign${"%.2f".format(this.absoluteValue)}"
}

// Дизайн chips и их поведение при клике
@Composable
fun CurrencyChip(
    currency: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFFBEFDC) else Color(0xFFE0E0E0)
    val textColor = if (isSelected) Color(0xFFFFAD25) else Color(0xFF353535)

    Surface(
        modifier = Modifier
            .size(width = 89.dp, height = 32.dp)
            .clickable(onClick = onSelected),
        shape = RoundedCornerShape(15.dp),
        color = backgroundColor
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currency,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Сообщение об ошибке
@Composable
fun ErrorScreen(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Картинка ошибки
        Image(
            painter = painterResource(id = R.drawable.ic_error),
            contentDescription = "Error",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Сообщение об ошибке
        Text(
            text = "Произошла какая-то ошибка :(\nПопробуем снова?",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Кнопка для повторной попытки
        Button(
            onClick = { onRetry() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFAD25),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(150.dp)
                .height(50.dp)
        ) {
            Text(text = "Попробовать")
        }
    }
}
