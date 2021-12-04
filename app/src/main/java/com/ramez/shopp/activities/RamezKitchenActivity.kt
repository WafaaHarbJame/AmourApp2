package com.ramez.shopp.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.Constants.MAIN_ACTIVITY_CLASS
import com.ramez.shopp.Classes.MessageEvent
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.adapter.RecipeAdapter
import com.ramez.shopp.adapter.RecipeSliderAdapter
import com.ramez.shopp.adapter.SuggestedProductAdapter
import com.ramez.shopp.databinding.ActivityRamezKitchenBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class RamezKitchenActivity : ActivityBase(), SuggestedProductAdapter.OnItemClick {
    lateinit var binding: ActivityRamezKitchenBinding
    var userId = 0
    var sliderList: ArrayList<String>? = null
    var productList: ArrayList<ProductModel>? = null
    var reviewList: ArrayList<ReviewModel>? = null
    var productName = ""
    var dinnerModel: SingleDinnerModel? = null
    var currency: String? = null
    private var countryId = 0
    private var cityId = 0
    private var sliderAdapter: RecipeSliderAdapter? = null
    private var productLayoutManager: LinearLayoutManager? = null
    private var storeId = 0
    private var dinner_id = 0
    private var lang: String? = null
    var recipes: List<Recipe>? = null
    private var localModel: LocalModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRamezKitchenBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        val memberModel = UtilityApp.getUserData()
        lang = if (UtilityApp.getLanguage() != null) {
            UtilityApp.getLanguage()
        } else {
            Locale.getDefault().language
        }
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )
        countryId = localModel?.countryId ?:Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        sliderList = ArrayList()
        productList = ArrayList()
        reviewList = ArrayList()
        recipes = ArrayList()
        storeId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        currency = localModel?.currencyCode?:Constants.BHD
        productLayoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.offerRecycler.layoutManager = productLayoutManager
        binding.offerRecycler.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(activity)
        binding.recipeRecycler.layoutManager = linearLayoutManager
        binding.recipeRecycler.setHasFixedSize(true)
        binding.offerRecycler.itemAnimator = null
        binding.processCartBut.setOnClickListener { v ->
            val intent = Intent(activity, MAIN_ACTIVITY_CLASS)
            intent.putExtra(Constants.CART, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        binding.fabCart.setOnClickListener { v ->
            val intent = Intent(activity, MAIN_ACTIVITY_CLASS)
            intent.putExtra(Constants.CART, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        intentExtra
        if (UtilityApp.isLogin()) {
            binding.fabCart.visibility = View.VISIBLE
            userId = memberModel.id.toString().toInt()
            binding.tVCartSize.text = UtilityApp.getCartCount().toString()
        } else {
            binding.fabCart.visibility = View.GONE
        }
        binding.productDescTv.expandInterpolator = OvershootInterpolator()
        binding.productDescTv.collapseInterpolator = OvershootInterpolator()
        getSingleDinner(dinner_id, lang)
        initListener()
    }

    private fun initListener() {
        binding.backBtn.setOnClickListener { view1 -> onBackPressed() }
        binding.showAllBut.setOnClickListener { v ->
            binding.showAllBut.setText(if (binding.productDescTv.isExpanded) R.string.ShowAll else R.string.Show_less)
            binding.productDescTv.toggle()
        }
    }

    private val intentExtra: Unit
        private get() {
            val bundle = intent.extras
            if (bundle != null) {
                val dinnerModel = bundle.getSerializable(Constants.DB_DINNER_MODEL) as DinnerModel?
                if (dinnerModel != null) {
                    dinner_id = dinnerModel.id
                    productName = dinnerModel.description
                    binding.productNameTv.text = productName
                    binding.mainTitleTv.text = productName
                }
            }
        }

    fun initAdapter() {
        sliderAdapter = RecipeSliderAdapter(this, sliderList)
        binding.viewPager.adapter = sliderAdapter
    }

    private fun initRecipeAdapter() {
        val recipeAdapter = RecipeAdapter(this, recipes, 0,
            object :DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                }

            }
        )
        binding.recipeRecycler.adapter = recipeAdapter
    }

    override fun onItemClicked(position: Int, productModel: ProductModel?) {}
    @SuppressLint("SetTextI18n")
    fun getSingleDinner(dinner_id: Int, lan: String?) {
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        binding.processCartBut.visibility = View.GONE
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as ResultAPIModel<SingleDinnerModel?>?
                binding.processCartBut.visibility = View.VISIBLE
                var message: String? = getString(R.string.fail_to_get_data)
                binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                if (func == Constants.ERROR) {
                    if (result != null) {
                        message = result.message
                    }
                    binding.dataLY.visibility = View.GONE
                    binding.noDataLY.noDataLY.visibility = View.GONE
                    binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                    binding.failGetDataLY.failTxt.text = message
                    binding.processCartBut.visibility = View.GONE
                } else if (func == Constants.FAIL) {
                    binding.dataLY.visibility = View.GONE
                    binding.noDataLY.noDataLY.visibility = View.GONE
                    binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                    binding.failGetDataLY.failTxt.text = message
                    binding.processCartBut.visibility = View.GONE
                } else if (func == Constants.NO_CONNECTION) {
                    binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                    binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection)
                    binding.failGetDataLY.noInternetIv.visibility = View.VISIBLE
                    binding.dataLY.visibility = View.GONE
                    binding.processCartBut.visibility = View.GONE
                } else {
                    if (IsSuccess) {
                        if (result?.data != null && result.data?.recipes?.size ?: 0 > 0) {
                            binding.dataLY.visibility = View.VISIBLE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                            dinnerModel = result.data
                            recipes = dinnerModel?.recipes
                            productName = if (UtilityApp.getLanguage() == Constants.Arabic) {
                                dinnerModel?.description?:""
                            } else {
                                dinnerModel?.description?:""
                            }
                            binding.productNameTv.text = productName
                            binding.mainTitleTv.text = productName
                            if (dinnerModel?.fullDescription != null && dinnerModel?.description != null) {

//                            if (UtilityApp.getLanguage().equals(Constants.Arabic)) {
//                                binding.productDescTv.setText(Html.fromHtml(dinnerModel.getFullDescription().toString()));
//
//                            } else {
//                                binding.productDescTv.setText(Html.fromHtml(dinnerModel.getFullDescription().toString()));
//
//                            }
                                binding.productDescTv.text = Html.fromHtml(dinnerModel?.fullDescription)
                            }
                            sliderList = result.data?.images
                            initAdapter()
                            initRecipeAdapter()
                        } else {
                            binding.dataLY.visibility = View.GONE
                            binding.noDataLY.noDataLY.visibility = View.VISIBLE
                            binding.processCartBut.visibility = View.GONE
                        }
                    } else {
                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.text = message
                        binding.processCartBut.visibility = View.GONE
                    }
                }            }


        }).getSingleDinner(dinner_id, lan)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {

//        if (event.type.equals(MessageEvent.TYPE_main)) {
//            binding.backBtn.setOnClickListener(view -> {
//                Intent intent = new Intent(getActiviy(), Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
//                startActivity(intent);
//            });
//
//        } else
        if (event.type == MessageEvent.TYPE_READ_CART) {
            if (UtilityApp.isLogin()) {
                binding.tVCartSize.text = UtilityApp.getCartCount().toString()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val bundle = intent.extras
        if (bundle != null) {
            val dinnerModel = bundle.getSerializable(Constants.DB_DINNER_MODEL) as DinnerModel?
            if (dinnerModel != null) {
                dinner_id = dinnerModel.id
            }
            if (dinnerModel != null && dinnerModel.description != null) {
                productName = dinnerModel.description
            }
            binding.productNameTv.text = productName
            getSingleDinner(dinner_id, lang)
            binding.productNameTv.text = productName
            binding.mainTitleTv.text = productName
        }
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}