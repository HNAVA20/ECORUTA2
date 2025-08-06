package com.example.ecorutaapp.utils

import android.content.Context
import com.example.ecorutaapp.MainMenuActivity
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

object LocationSender {
    fun enviarDestinoAlReloj(context: Context, lat: Double, lng: Double, name: String) {
        val dataMap = PutDataMapRequest.create("/destination").apply {
            dataMap.putDouble("lat", lat)
            dataMap.putDouble("lng", lng)
            dataMap.putString("name", name)
        }.asPutDataRequest().setUrgent()

        Wearable.getDataClient(context).putDataItem(dataMap)
    }

    fun enviarDestinoEnTiempoReal(context: Context, lat: Double, lng: Double, name: String) {
        // Enviar datos cada vez que cambie la ubicación o el destino
        enviarDestinoAlReloj(context, lat, lng, name)
    }

    fun enviarTestSincronizacion(mainMenuActivity: MainMenuActivity, callback: (Boolean) -> Unit) {
        val dataMap = PutDataMapRequest.create("/test_sync").apply {
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }.asPutDataRequest().setUrgent()

        Wearable.getDataClient(mainMenuActivity).putDataItem(dataMap)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}
