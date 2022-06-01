package com.amour.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.BuildConfig
import com.amour.shop.Models.LocalModel
import com.amour.shop.Models.ProductModel
import com.amour.shop.Models.Recipe
import com.amour.shop.Models.ResultAPIModel
import com.amour.shop.R
import com.amour.shop.classes.Constants
import com.amour.shop.classes.UtilityApp
import com.amour.shop.classes.UtilityApp.getLocalData
import com.amour.shop.classes.UtilityApp.getUserData
import com.amour.shop.databinding.RowRecipeBinding


class RecipeAdapter(
    private val activity: Context,
    private val list: List<Recipe>?,
    selectedPosition: Int,
    private val dataFetcherCallBack: DataFetcherCallBack
) :
        RecyclerView.Adapter<RecipeAdapter.ViewHolder>(),SuggestedProductAdapter.OnItemClick {
    var adapter: SuggestedProductAdapter? = null
    var countryId: Int
    var cityId: Int
    var userId = 0
    var localModel: LocalModel? = getLocalData()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = RowRecipeBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        localModel= getLocalData()
        val recipe = list?.get(position)
        holder.binding.nameTxt.text = recipe?.description
        holder.binding.selectTxt.text = activity.getString(R.string.fa_check)
        holder.binding.selectTxt.setTextColor(ContextCompat.getColor(activity, R.color.green))
        if (recipe?.isOpen == true) {
            // to open and change header icon
            holder.binding.toggleBut.text = activity.getString(R.string.fa_angle_up)
            holder.binding.selectTxt.visibility = View.VISIBLE
            if (recipe.isLoaded) {
                // to check if products loaded before
                adapter = SuggestedProductAdapter(activity, recipe.productsList, this, 0)
                holder.binding.rv.adapter = adapter
                holder.binding.progressBar1.visibility = View.GONE
                holder.binding.noProductsTv.visibility = View.GONE
                holder.binding.rv.visibility = View.VISIBLE
            } else {
                // if products not loaded
                getProductRecipeList(holder.binding, recipe, countryId, cityId, userId.toString())
            }
        } else {
            holder.binding.toggleBut.text = activity.getString(R.string.fa_angle_down)
            holder.binding.selectTxt.visibility = View.GONE
            holder.binding.rv.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list?.size?:0
    }

    override fun onItemClicked(position: Int, productModel: ProductModel?) {}
    inner class ViewHolder(var binding: RowRecipeBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        init {
            val llm = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            binding.rv.layoutManager = llm
            binding.rv.setHasFixedSize(true)
            binding.rv.itemAnimator = null
            itemView.setOnClickListener { v: View? ->
                val recipe = list?.get(bindingAdapterPosition)
                recipe?.isOpen = !recipe?.isOpen!!
                notifyItemChanged(bindingAdapterPosition)
            }
        }
    }

    private fun getProductRecipeList(
        binding: RowRecipeBinding,
        recipe: Recipe,
        country_id: Int,
        city_id: Int,
        user_id: String
    ) {
//        productList.clear();
        binding.progressBar1.visibility = View.VISIBLE
        binding.rv.visibility = View.GONE
        binding.noProductsTv.visibility = View.GONE
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                binding.progressBar1.visibility = View.GONE
                val result =
                    obj as ResultAPIModel<ArrayList<ProductModel>?>
                if (IsSuccess) {
                    if (result.data != null && result.data!!.size > 0) {
                        recipe.productsList = result.data
                        recipe.isLoaded = true
                        val adapter =
                            SuggestedProductAdapter(activity, recipe.productsList,this@RecipeAdapter, 0)
                        binding.rv.adapter = adapter
                        binding.rv.visibility = View.VISIBLE
                        binding.noProductsTv.visibility = View.GONE
                    } else {
                        binding.rv.visibility = View.GONE
                        binding.noProductsTv.visibility = View.VISIBLE
                    }
                }            }

        }).getProductRecipeList(recipe.id, country_id, city_id, user_id, 0, 10)
    }

    init {
        countryId = localModel!!.countryId
        cityId = localModel!!.cityId.toInt()
        if (getUserData() != null) {
            userId = getUserData()!!.id
        }
    }
}