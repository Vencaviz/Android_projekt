package com.example.homework2.ui.screens.map

import java.io.Serializable

data class MapScreenUIState(
    var Loading : Boolean = true,
    var error: MapScreenError? = null,
    var data: MapScreenData? = null,
)