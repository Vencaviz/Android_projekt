package com.example.homework2.ui.screens.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.homework2.model.ExchangeRate
import com.example.homework2.navigation.INavigationRouter
import com.example.homework2.ui.components.BaseScreen
import com.example.homework2.ui.components.PlaceholderScreenContent

@Composable
fun MapScreen(navigation: INavigationRouter) {

    val viewModel = hiltViewModel<MapScreenViewModel>()

    val data = remember {
        mutableStateOf<MapScreenData>(MapScreenData())
    }

    val state = viewModel.uiState.collectAsStateWithLifecycle()



    BaseScreen(
        topBarText = "Exchange Rates",
        onBackClick = null,
        showLoading = state.value.Loading,
        placeholderScreenContent = if (state.value.error != null) {
            PlaceholderScreenContent(
                image = null,
                title = null,
                text = stringResource(state.value.error!!.communicationError)
            )
        } else null,
    ) {
        MapScreenContent(
            paddingValues = it,
            actions = viewModel,
            mapScreenData = state.value.data,
            navigation = navigation,

        )
    }
}

@Composable
fun MapScreenContent(
    paddingValues: PaddingValues,
    navigation: INavigationRouter,
    actions: MapScreenActions,
    mapScreenData: MapScreenData?,
    ){

    when{
        mapScreenData == null -> {
        }
        else -> {
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            items(items = mapScreenData.restaurants){ rate ->
                RateRow(
                    rate = rate,
                    onClick = null
                )
            }

        }
        }
    }
}

@Composable
fun RateRow(
    rate: ExchangeRate,
    onClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(rate.country!!)

                Spacer(modifier = Modifier.width(16.dp))
                Text(rate.currency!!)
                Spacer(modifier = Modifier.width(16.dp))
                Text(rate.exchange_rate!!.toString())
            }
        }
    }
}
