package edu.fz.cs411.weatherapp

// ListRecyclerAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListRecyclerAdapter(private val items: List<YourTableModel>) : RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.cityNameTextView.text = item.cityName

        holder.minTempTextView.text = item.minTemperature.toString()
        holder.maxTempTextView.text = item.maxTemperature.toString()


    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityNameTextView: TextView = itemView.findViewById(R.id.cityNameTextView)

        val minTempTextView: TextView = itemView.findViewById(R.id.minTempTextView)

        val maxTempTextView: TextView=itemView.findViewById(R.id.maxTempTextView)

    }
}
