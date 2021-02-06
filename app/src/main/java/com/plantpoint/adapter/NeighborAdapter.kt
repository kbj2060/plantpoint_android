package com.plantpoint.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.plantpoint.App
import com.plantpoint.R

import com.plantpoint.dto.Neighbor


class NeighborAdapter(val neighborList: ArrayList<Neighbor>) : RecyclerView.Adapter<NeighborAdapter.CustomViewHolder>() {
    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }
    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: Any) {
        this.itemClickListener = itemClickListener as ItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.neighbor_item, parent, false)
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                itemClickListener.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return neighborList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(neighborList[position].profile)
            .transform(CenterInside(), RoundedCorners(10))
            .into(holder.profile)
        holder.farmerName.text = neighborList[position].farmerName
        holder.farmerLocation.text = neighborList[position].farmerLocation
        createCropsView(holder, position)
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile:ImageView = itemView.findViewById(R.id.neighbor_title_img)
        val farmerName:TextView = itemView.findViewById(R.id.farmer_name)
        val farmerLocation:TextView = itemView.findViewById(R.id.neighbor_location)
        val cropsItemContainer: LinearLayout = itemView.findViewById(R.id.card_bottom)
    }

    @SuppressLint("ResourceType", "InflateParams", "UseCompatLoadingForDrawables")
    fun createCropsView(holder: CustomViewHolder, position: Int) {
        holder.cropsItemContainer.removeAllViews()
        val context : Context = holder.itemView.context
        val layoutInflater = LayoutInflater.from(context)
        for (cropEntry in neighborList[position].crops) {
            val cropItem : ConstraintLayout = layoutInflater.inflate(R.layout.crop_item, null) as ConstraintLayout
            val cropImageHolder: ImageView = cropItem.findViewById(R.id.crop_image)
            val cropNameHolder: TextView = cropItem.findViewById(R.id.crop_name)
            val cropPriceHolder: TextView = cropItem.findViewById(R.id.crop_price)
            val image = getImageId(App.instance, "ic_${cropEntry.key}");
            cropImageHolder.setImageResource(image)
            cropNameHolder.text = getStrings(App.instance, cropEntry.key)
            cropPriceHolder.text = cropEntry.value.toString()
            holder.cropsItemContainer.addView(cropItem)
        }
    }

    fun getImageId(c: Context, ImageName: String?): Int {
        return c.resources.getIdentifier(ImageName, "drawable", c.packageName);
    }

    fun getStrings(c: Context, stringName: String?): String {
        val resId = c.resources.getIdentifier(stringName, "string", c.packageName)
        return c.resources.getString(resId)
    }
}

