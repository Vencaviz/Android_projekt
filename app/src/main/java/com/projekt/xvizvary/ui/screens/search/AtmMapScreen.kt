package com.projekt.xvizvary.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.projekt.xvizvary.R

@Composable
fun AtmMapScreen() {
    val (query, setQuery) = remember { mutableStateOf("") }

    val brno = LatLng(49.1951, 16.6068)
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(brno, 12f)
    }

    val results = listOf(
        "${stringResource(R.string.label_atm)}, 1656 Union Street",
        "${stringResource(R.string.label_atm)}, Secaucus",
        "${stringResource(R.string.label_atm)}, 1657 Riverside Drive"
    )

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = setQuery,
            label = { Text(text = stringResource(R.string.hint_search_place)) },
            modifier = Modifier.fillMaxWidth()
        )

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            cameraPositionState = cameraPositionState
        ) {
            Marker(position = brno, title = stringResource(R.string.label_atm))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(results) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(text = item, modifier = Modifier.padding(14.dp))
                }
            }
        }
    }
}

