package com.amour.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.amour.shop.Models.CategoryModel
import com.amour.shop.R
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData.GlideImg
import com.amour.shop.classes.UtilityApp


class MainCategoryAdapter(
    private val context: Context, private val mainCategoryDMS: List<CategoryModel>?,
    private val onMainCategoryItemClicked: OnMainCategoryItemClicked, private var selectedPosition: Int
) :
        RecyclerView.Adapter<MainCategoryAdapter.Holder>() {
    private var selectedImageView: RoundedImageView? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_cat_page, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val mainMainCategoryDM = mainCategoryDMS?.get(position)
        if (mainMainCategoryDM?.id ?: 0 == 0) {
            if (UtilityApp.getLanguage() == Constants.Arabic) {
                holder.catImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.all_ar))
            } else {
                holder.catImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.all_en))
            }
        } else {
            try {
                GlideImg(
                    context, mainMainCategoryDM?.catImage, R.drawable.holder_image, holder.catImage
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (mainCategoryDMS?.get(position)?.id ?: 0 == selectedPosition) {
            holder.catImage.borderWidth = context.resources.getDimension(R.dimen._2sdp)
            holder.catImage.borderColor = ContextCompat.getColor(context, R.color.green)
            selectedImageView = holder.catImage
        } else {
            holder.catImage.borderWidth = context.resources.getDimension(R.dimen._2sdp)
            holder.catImage.borderColor = ContextCompat.getColor(context, R.color.transparent)
        }
    }

    override fun getItemCount(): Int {
        return mainCategoryDMS?.size ?: 0
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        var catImage: RoundedImageView = view.findViewById(R.id.catImage)

        init {
            view.setOnClickListener { v: View? ->
                val position = bindingAdapterPosition
                val mainMainCategoryDM = mainCategoryDMS?.get(position)
                if (mainMainCategoryDM?.id ?: 0 != selectedPosition) {
                    if (selectedImageView != null) {
                        selectedImageView!!.borderWidth = context.resources.getDimension(R.dimen._2sdp)
                        selectedImageView!!.borderColor = ContextCompat.getColor(
                            context,
                            R.color.transparent
                        )
                    }
                    selectedPosition = mainMainCategoryDM?.id ?: 0
                    selectedImageView = catImage
                    catImage.borderWidth = context.resources.getDimension(R.dimen._2sdp)
                    catImage.borderColor = ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                }
                //
//                notifyDataSetChanged();
                onMainCategoryItemClicked.OnMainCategoryItemClicked(mainMainCategoryDM, position)
            }
        }
    }

    interface OnMainCategoryItemClicked {
        fun OnMainCategoryItemClicked(mainCategoryDM: CategoryModel?, position: Int)
    }
}
