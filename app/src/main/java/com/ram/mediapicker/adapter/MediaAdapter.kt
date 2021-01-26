package com.ram.mediapicker.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.fetch.VideoFrameFileFetcher
import coil.load
import coil.request.videoFrameMillis
import com.ram.mediapicker.R
import com.ram.mediapicker.model.PhotoModel
import com.ram.mediapicker.utility.AppConstants
import kotlinx.android.synthetic.main.item_media.view.*
import java.io.File

/**
 * Created by Ramashish Prajapati on 26,January,2021
 */

class MediaAdapter(onMediaSelected: OnMediaSelected) :
    ListAdapter<PhotoModel, MediaAdapter.ItemViewholder>(MediaDiffCallback()) {

    var onMediaSelected: OnMediaSelected = onMediaSelected
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_media, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MediaAdapter.ItemViewholder, position: Int) {
        holder.bind(getItem(position), onMediaSelected)
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,
        View.OnLongClickListener {

        private lateinit var onMediaSelected2: OnMediaSelected
        lateinit var photoModel: PhotoModel

        init {
            itemView.aimg_library_image.setOnClickListener(this)
            itemView.aimg_library_image.setOnLongClickListener(this)
            itemView.view_background.visibility = View.GONE
            itemView.mtv_photo_number.visibility = View.GONE
        }


        fun bind(item: PhotoModel, onMediaSelected: OnMediaSelected) = with(itemView) {
            onMediaSelected2 = onMediaSelected
            photoModel = item

            if (!item.photoPath.isNullOrEmpty()) {

                itemView.aimg_library_image.let {
                    val mediaFilePath = File(item.photoPath)
                    when (item.mediaType) {
                        AppConstants.IMAGE_MEDIA -> {
                            it.load(mediaFilePath) {
                                placeholder(R.drawable.placeholder_image)
                                error(R.drawable.error_image)
                            }
                            itemView.aimg_media_type.visibility = View.GONE
                        }
                        AppConstants.VIDEO_MEDIA -> {
                            it.load(Uri.fromFile(mediaFilePath)) {
                                videoFrameMillis(1000)
                                fetcher(VideoFrameFileFetcher(context))
                                placeholder(R.drawable.placeholder_image)
                                error(R.drawable.error_image)
                            }
                            itemView.aimg_media_type.visibility = View.VISIBLE
                        }
                        else -> {
                            it.load(R.drawable.placeholder_image) {
                                size(100)
                            }
                            Log.d("PhotoLibraryAdapter", "Media does not match")
                        }
                    }
                }
            }

            if (item.isSelected) {
                itemView.view_background.visibility = View.VISIBLE
                itemView.mtv_photo_number.visibility = View.VISIBLE
                itemView.mtv_photo_number.text = item.selectedPostion.toString()
            } else {
                itemView.view_background.visibility = View.GONE
                itemView.mtv_photo_number.visibility = View.GONE
            }
        }

        override fun onClick(v: View?) {
            if (onMediaSelected2 != null) {
                onMediaSelected2.onSingleClick(adapterPosition, photoModel)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            itemView.view_background.visibility = View.VISIBLE
            itemView.mtv_photo_number.visibility = View.VISIBLE
            if (onMediaSelected2 != null) {
                onMediaSelected2.onLongPress(adapterPosition, photoModel)
            }
            return true
        }
    }
}

class MediaDiffCallback : DiffUtil.ItemCallback<PhotoModel>() {
    override fun areItemsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
        return oldItem.photoPath == newItem.photoPath &&
                oldItem.isSelected == newItem.isSelected &&
                oldItem.selectedPostion == newItem.selectedPostion &&
                oldItem.mediaType == newItem.mediaType
    }

    override fun areContentsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
        return oldItem.photoPath == newItem.photoPath &&
                oldItem.isSelected == newItem.isSelected &&
                oldItem.selectedPostion == newItem.selectedPostion &&
                oldItem.mediaType == newItem.mediaType
    }

}

interface OnMediaSelected {
    fun onSingleClick(position: Int, imagePath: PhotoModel)
    fun onLongPress(position: Int, imagePath: PhotoModel)
}