package com.example.testcrypto

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testcrypto.ui.coinlist.CoinListScreen
import com.example.testcrypto.ui.theme.CryptocurrencyAppTheme
import com.example.testcrypto.ui.coindetail.CoinDetailScreen
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptocurrencyAppTheme {
                val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "coin_list" // Начальный экран
                    ) {
                        // Экран со списком криптовалют
                        composable("coin_list") {
                            CoinListScreen(navController = navController, onCoinSelected = { coin ->
                                // Переход на экран с подробной информацией о монете
                                navController.navigate("coin_detail/${coin.id}")
                            })
                        }
                        // Экран с подробной информацией о монете
                        composable("coin_detail/{coinId}") { backStackEntry ->
                            val coinId = backStackEntry.arguments?.getString("coinId")
                            coinId?.let {
                                CoinDetailScreen(
                                    coinId = it,
                                    navController = navController,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
            }
        }
    }
}