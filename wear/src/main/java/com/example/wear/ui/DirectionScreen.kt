package com.example.wear.ui

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.example.wear.viewmodel.WearViewModel

@Composable
fun DirectionScreen(
    viewModel: WearViewModel,
    currentLocation: Location?,
    azimuth: Float
) {
    val lat = viewModel.destinoLat.value ?: return
    val lng = viewModel.destinoLng.value ?: return
    val name = viewModel.destinoName.value ?: ""

    val destinoLocation = Location("").apply {
        latitude = lat
        longitude = lng
    }

    val bearing = if (currentLocation != null) {
        currentLocation.bearingTo(destinoLocation)
    } else 0f

    val arrowRotation = (bearing - azimuth + 360) % 360

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowUpward,
            contentDescription = "Dirección",
            modifier = Modifier
                .size(64.dp)
                .rotate(arrowRotation)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Ir a: $name")
    }
}
