package com.example.testcrypto.ui.coindetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.testcrypto.ui.coinlist.ErrorScreen
import com.example.testcrypto.ui.coindetail.CoinDetailState
import com.example.testcrypto.ui.theme.robotoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    coinId: String,
    navController: NavController,
    onBack: () -> Unit,
    viewModel: CoinDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(coinId) {
        viewModel.fetchCoinDetail(coinId)
    }

    Scaffold(
        topBar = {
            if (state is CoinDetailState.Success) {
                val coinDetail = (state as CoinDetailState.Success).coinDetail

                TopAppBar(
                    title = {
                        Text(
                            text = coinDetail.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Medium

                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onBack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text(text = "Детали монеты") },
                    navigationIcon = {
                        IconButton(onClick = { onBack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
        ) {
            when (state) {
                is CoinDetailState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is CoinDetailState.Error -> {
                    ErrorScreen(
                        errorMessage = (state as CoinDetailState.Error).message,
                        onRetry = { viewModel.fetchCoinDetail(coinId) }
                    )
                }
                is CoinDetailState.Success -> {
                    val coinDetail = (state as CoinDetailState.Success).coinDetail
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
                        // Колонка для изображения крипты
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = coinDetail.image.large,
                                contentDescription = coinDetail.name,
                                modifier = Modifier.size(100.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Текстовые элементы
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                text = "Описание",
                                fontFamily = robotoFontFamily,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = coinDetail.description.en,
                                fontFamily = robotoFontFamily,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Категории",
                                fontFamily = robotoFontFamily,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            coinDetail.categories.forEach { category ->
                                Text(
                                    text = category,
                                    fontFamily = robotoFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}