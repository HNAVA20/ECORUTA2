package com.example.wear.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WearViewModel : ViewModel() {

    // Datos del destino enviados desde el celular
    private val _destinoLat = MutableLiveData<Double>()
    val destinoLat: LiveData<Double> = _destinoLat

    private val _destinoLng = MutableLiveData<Double>()
    val destinoLng: LiveData<Double> = _destinoLng

    private val _destinoName = MutableLiveData<String>()
    val destinoName: LiveData<String> = _destinoName

    fun setDestino(lat: Double, lng: Double, name: String) {
        _destinoLat.value = lat
        _destinoLng.value = lng
        _destinoName.value = name
    }
}
