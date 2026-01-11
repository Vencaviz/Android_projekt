package com.projekt.xvizvary.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.projekt.xvizvary.R

@Composable
fun SearchScreen(
    onAtmMap: () -> Unit,
    onExchangeRate: () -> Unit,
    onInterestRate: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.title_what_to_find),
            style = MaterialTheme.typography.titleLarge
        )

        // ATM Card
        SearchCard(
            title = stringResource(R.string.search_for_atms),
            subtitle = stringResource(R.string.hint_search_atm),
            onClick = onAtmMap,
            backgroundColor = Color(0xFF1E3A5F),
            content = { AtmCardContent() }
        )

        // Interest Rate Card
        SearchCard(
            title = stringResource(R.string.search_for_interest_rate),
            subtitle = stringResource(R.string.screen_interest_rate),
            onClick = onInterestRate,
            backgroundColor = Color(0xFF2D2D2D),
            content = { InterestRateCardContent() }
        )

        // Exchange Rate Card
        SearchCard(
            title = stringResource(R.string.search_for_exchange_rate),
            subtitle = stringResource(R.string.screen_exchange_rate),
            onClick = onExchangeRate,
            backgroundColor = Color(0xFF1A1A2E),
            content = { ExchangeRateCardContent() }
        )
    }
}

@Composable
private fun SearchCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

@Composable
private fun AtmCardContent() {
    Box(contentAlignment = Alignment.Center) {
        // Background piggy bank
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50).copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Savings,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(36.dp)
            )
        }
        
        // Chart overlay
        Icon(
            imageVector = Icons.Default.ShowChart,
            contentDescription = null,
            tint = Color(0xFFFF9800),
            modifier = Modifier
                .size(28.dp)
                .offset(x = 30.dp, y = (-20).dp)
        )
        
        // Location pin
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color(0xFFE53935),
            modifier = Modifier
                .size(24.dp)
                .offset(x = (-30).dp, y = 25.dp)
        )
    }
}

@Composable
private fun InterestRateCardContent() {
    Box(contentAlignment = Alignment.Center) {
        // Calculator/chart background
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF42A5F5),
                            Color(0xFF1E88E5)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PieChart,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        
        // Money icons around
        Icon(
            imageVector = Icons.Default.Payments,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier
                .size(24.dp)
                .offset(x = 35.dp, y = (-25).dp)
        )
        
        Icon(
            imageVector = Icons.Default.AttachMoney,
            contentDescription = null,
            tint = Color(0xFFFFD700),
            modifier = Modifier
                .size(20.dp)
                .offset(x = (-35).dp, y = 20.dp)
        )
        
        Icon(
            imageVector = Icons.Default.TrendingUp,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier
                .size(22.dp)
                .offset(x = 30.dp, y = 28.dp)
        )
    }
}

@Composable
private fun ExchangeRateCardContent() {
    Box(contentAlignment = Alignment.Center) {
        // Phone/app background
        Box(
            modifier = Modifier
                .size(50.dp, 70.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CurrencyExchange,
                contentDescription = null,
                tint = Color(0xFF1A1A2E),
                modifier = Modifier.size(30.dp)
            )
        }
        
        // Euro
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = 10.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF2196F3)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Euro,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Dollar
        Box(
            modifier = Modifier
                .offset(x = 40.dp, y = (-15).dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD700)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Pound
        Box(
            modifier = Modifier
                .offset(x = 35.dp, y = 25.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFE91E63)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "£",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        // Coins
        Box(
            modifier = Modifier
                .offset(x = (-30).dp, y = (-25).dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF9800)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
