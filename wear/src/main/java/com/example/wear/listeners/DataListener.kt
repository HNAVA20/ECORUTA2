package com.example.wear.listeners

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.*
import com.example.wear.viewmodel.WearViewModel
import kotlinx.coroutines.launch

class DataListener(
    private val viewModel: WearViewModel
) : DataClient.OnDataChangedListener {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED &&
                event.dataItem.uri.path == "/destination") {

                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val lat = dataMap.getDouble("lat")
                val lng = dataMap.getDouble("lng")
                val name = dataMap.getString("name") ?: ""

                viewModel.setDestino(lat, lng, name)
            }
        }
    }
}