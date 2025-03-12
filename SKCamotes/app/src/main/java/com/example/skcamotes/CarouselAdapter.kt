package com.example.skcamotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class CarouselAdapter(
    private val images: List<Int>,
    private val onItemClick: (Int) -> Unit // Callback for item clicks

) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carouselImage: ImageView = itemView.findViewById(R.id.carouselImage)

        fun bind(imageResId: Int) {
            carouselImage.setImageResource(imageResId)
            itemView.setOnClickListener {
                onItemClick(imageResId) // Call callback when clicked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carousel_item, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.carouselImage.setImageResource(images[position])
        holder.bind(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }
}
