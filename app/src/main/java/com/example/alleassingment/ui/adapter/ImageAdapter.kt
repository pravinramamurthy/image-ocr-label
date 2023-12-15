package com.example.alleassingment.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.alleassingment.R
import com.example.alleassingment.model.ImageModel

/**
 * Created by Praveen on 14,December,2023
 */
class ImageAdapter(private val context: Context, private val imageList: List<ImageModel>) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    var onItemClickListener: ((ImageModel) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(imageList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageModel = imageList[position]
        Glide.with(context)
            .load(imageModel.imagePath)
            .override(50, 70)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imageView)

    }

    override fun getItemCount(): Int {
        return imageList.size

    }
}