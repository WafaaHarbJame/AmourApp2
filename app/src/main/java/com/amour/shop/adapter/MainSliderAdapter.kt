package com.amour.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.amour.shop.Models.Slider
import com.amour.shop.R
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData.GlideImgWeb
import com.amour.shop.classes.UtilityApp
import java.util.ArrayList


class MainSliderAdapter(
    private val context: Context,
    var sliderList: MutableList<Slider?>?,
    private val onSliderClick: OnSliderClick
) :
        PagerAdapter() {
    override fun getCount(): Int {
        return sliderList?.size ?: 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.fragment_slider, container, false)
        val imageUrl: String
        try {
            val productImg = view.findViewById<ImageView>(R.id.slideImg)
            val slider = sliderList?.get(position)
            imageUrl = if (UtilityApp.getLanguage() == Constants.English) {
                slider?.image ?: ""
            } else {
                slider?.image2 ?: ""
            }
            try {
                GlideImgWeb(context, imageUrl, R.drawable.holder_image, productImg)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            container.addView(view)
            productImg.setOnClickListener { view1: View? ->
                onSliderClick.onSliderClicked(
                    position,
                    slider
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }

    interface OnSliderClick {
        fun onSliderClicked(position: Int, slider: Slider?)
    }
}