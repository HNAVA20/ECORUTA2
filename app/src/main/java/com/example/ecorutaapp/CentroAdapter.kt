package com.example.ecorutaapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecorutaapp.databinding.ItemCentroBinding

class CentroAdapter(
    private var lista: List<CentroReciclaje>,
    private val onItemClick: (CentroReciclaje) -> Unit
) : RecyclerView.Adapter<CentroAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCentroBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCentroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.binding.txtNombre.text = item.nombre
        holder.binding.txtMateriales.text = item.materiales

        holder.itemView.setOnClickListener {
            onItemClick(item)  // ✅ Clic que enfoca en el mapa
        }
    }

    fun actualizarLista(nuevaLista: List<CentroReciclaje>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
