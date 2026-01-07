package com.example.homework2.ui.screens.map

import com.example.homework2.model.ExchangeRate

data class MapScreenData(
    var restaurants: List<ExchangeRate> = mutableListOf()
)