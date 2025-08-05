package com.example.wear.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.wear.ui.DirectionScreen
import com.example.wear.viewmodel.WearViewModel
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun WearMainScreen(
    viewModel: WearViewModel,
    fusedLocationClient: FusedLocationProviderClient,
    azimuth: State<Float>
) {
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                currentLocation = it
            }
        }
    }

    DirectionScreen(viewModel, currentLocation, azimuth.value)
}
