package com.ramez.shopp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.androidnetworking.BuildConfig
import com.bumptech.glide.manager.SupportRequestManagerFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.onesignal.OneSignal
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.classes.*
import com.ramez.shopp.Dialogs.ConfirmDialog
import com.ramez.shopp.Dialogs.FilterDialog
import com.ramez.shopp.Utils.ActivityHandler
import com.ramez.shopp.activities.ActivityBase
import com.ramez.shopp.databinding.ActivityMainBinding
import com.ramez.shopp.fragments.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import com.ramez.shopp.classes.MessageEvent
import com.ramez.shopp.Models.*
import com.ramez.shopp.activities.AllBookleteActivity
import com.ramez.shopp.activities.ExtraRequestActivity
import mobi.foo.benefitinapp.Application
import mobi.foo.benefitinapp.utils.CurrencyUtil


class MainActivity : ActivityBase() {

    private var fragmentManager: FragmentManager? = null
    private var ft: FragmentTransaction? = null
    var oldFragment: Fragment? = null
    var currFragment: Fragment? = null
    private var lastBottomFragmentId = R.id.homeButton
    private var selectedFragmentId = 0

    var cartCount = 0
    var storeId = 7263
    var localModel: LocalModel? = null
    private var toggleButton = false
    private var toggleSortButton = false
    var country_name = Constants.default_short_name
    var categoryModelList: ArrayList<CategoryModel>? = null

    private lateinit var tabNavArr: Array<BottomNavModel>

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currencyKey = Application.ujco("ᨸ⠜繇")
        println("Log currencyKey $currencyKey")



        println("Log Currency currencyCode ${getString(CurrencyUtil.getCurrencyFromNumericCode("048").currencyCode)}")
        println("Log Currency currencyName ${getString(CurrencyUtil.getCurrencyFromNumericCode("048").currencyName)}")
        println("Log Currency numericCurrencyCode ${CurrencyUtil.getCurrencyFromNumericCode("048").numericCurrencyCode}")


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Log Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("TAG", "Log token firebase manin  $token")
        })
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )

        fragmentManager = supportFragmentManager
        val UUID = OneSignal.getDeviceState()?.userId
        val UUID1 = OneSignal.getDeviceState()?.pushToken

        if (UUID != null) {
            UtilityApp.setFCMToken(UUID)
        }
        Log.d("debug", "Log token one signal main: $UUID")
        Log.d("debug", "Log token one signal2  main: $UUID1")
        tabNavArr = arrayOf(
            BottomNavModel(
                R.id.homeButton,
                binding.tab1Tv,
                binding.tab1Icon,
                R.string.string_menu_home,
                R.drawable.home_clicked,
                R.drawable.home_icon,
            ),
            BottomNavModel(
                R.id.categoryButton,
                binding.tab2Tv,
                binding.tab2Icon,
                R.string.category,
                R.drawable.category_click,
                R.drawable.category_icon,
            ),
            BottomNavModel(
                R.id.cartButton,
                binding.tab3Tv,
                binding.tab3Icon,
                R.string.cart,
                R.drawable.cart_icon_bottom,
                R.drawable.cart_icon_before,
            ),
            BottomNavModel(
                R.id.offerButton,
                binding.tab4Tv,
                binding.tab4Icon,
                R.string.offer_text,
                R.drawable.offer_clicked,
                R.drawable.offer_icon,
            ),
            BottomNavModel(
                R.id.myAccountButton,
                binding.tab5Tv,
                binding.tab5Icon,
                R.string.myaccount,
                R.drawable.my_account_clciked,
                R.drawable.myaccount_icon,
            ),
        )

        selectBottomTab(R.id.homeButton, null)

        categoryModelList = ArrayList()
        validation
        initListeners()
        if (localModel?.cityId != null) {
            storeId = localModel?.cityId?.toInt() ?: 0
            country_name = localModel?.shortname ?: Constants.default_short_name
            OneSignal.sendTag(Constants.COUNTRY, country_name)
        }
        if (UtilityApp.isLogin()) {
            cartsCount
        }
        AnalyticsHandler.APP_OPEN()

        initIntentExtra(intent?.extras)

        println("Log main eventBus onCreate ${EventBus.getDefault().isRegistered(this)}")

    }

    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(newIntent)
        println("Log main onNewIntent")
        selectedFragmentId = 0

        initIntentExtra(newIntent?.extras)
    }

    private fun initListeners() {
        fragmentManager?.addOnBackStackChangedListener {

            binding.toolBar.backBtn.visibility = gone
            binding.toolBar.sortBut.visibility = gone
            binding.toolBar.view2But.visibility = gone
            binding.toolBar.sort2But.visibility = gone
            binding.toolBar.filterBut.visibility = gone
            binding.toolBar.mainSearchBtn.visibility = gone
            binding.toolBar.SearchBtn.visibility = gone
            binding.toolBar.addExtra.visibility = gone

            println("Log main backStack fragments ${fragmentManager?.fragments}")
            if (oldFragment is SearchFragment) {
                hideSoftKeyboard(activity)
            }
            currFragment = fragmentManager?.fragments?.last()
            when (currFragment) {
                is HomeFragment, is SupportRequestManagerFragment -> {
                    println("Log main homeButton ${R.id.homeButton}")
                    selectedFragmentId = R.id.homeButton
                    lastBottomFragmentId = selectedFragmentId

                    binding.toolBar.mainSearchBtn.visibility = visible
                }
                is CategoryFragment -> {
                    println("Log main categoryButton ${R.id.categoryButton}")
                    selectedFragmentId = R.id.categoryButton
                    lastBottomFragmentId = selectedFragmentId
                }
                is CartFragment -> {
                    selectedFragmentId = R.id.cartButton
                    lastBottomFragmentId = selectedFragmentId

                    if (UtilityApp.isLogin()) {
                        binding.toolBar.addExtra.visibility = visible
                    }
                }
                is OfferFragment -> {
                    selectedFragmentId = R.id.offerButton
                    lastBottomFragmentId = selectedFragmentId
                    binding.toolBar.view2But.visibility = visible

                }
                is MyAccountFragment -> {
                    selectedFragmentId = R.id.myAccountButton
                    lastBottomFragmentId = selectedFragmentId
                }
                is SearchFragment -> {
                    selectedFragmentId = R.id.searchFragment
                    binding.toolBar.backBtn.visibility = visible
                    binding.toolBar.view2But.visibility = visible
                    binding.toolBar.sortBut.visibility = visible

                }
                is CategoryProductsFragment -> {
                    println("Log main categoryProductsFragment ${R.id.categoryProductsFragment}")
                    selectedFragmentId = R.id.categoryProductsFragment

                    binding.toolBar.backBtn.visibility = visible
                    binding.toolBar.view2But.visibility = visible
                    binding.toolBar.sort2But.visibility = visible
                    binding.toolBar.filterBut.visibility = visible
                }
                is SpecialOfferFragment -> {
                    selectedFragmentId = R.id.specialOffersFragment

                    binding.toolBar.backBtn.visibility = visible
                }
                is InvoiceFragment -> {
                    selectedFragmentId = R.id.invoiceFragment

                    binding.toolBar.backBtn.visibility = visible
                }

            }
            oldFragment = currFragment
            changeColor(selectedFragmentId)
        }

        binding.homeButton.setOnClickListener {

            selectBottomTab(R.id.homeButton, null)
            if (UtilityApp.isLogin()) {
                cartsCount
            }
        }

        binding.categoryButton.setOnClickListener {

            selectBottomTab(R.id.categoryButton, null)
            if (UtilityApp.isLogin()) {
                cartsCount
            }
        }

        binding.cartButton.setOnClickListener {

            selectBottomTab(R.id.cartButton, null)

        }

        binding.offerButton.setOnClickListener {
            selectBottomTab(R.id.offerButton, null)
            if (UtilityApp.isLogin()) {
                cartsCount
            }
        }

        binding.myAccountButton.setOnClickListener {

            selectBottomTab(R.id.myAccountButton, null)
            if (UtilityApp.isLogin()) {
                cartsCount
            }
        }

        binding.toolBar.view2But.setOnClickListener {
            toggleButton = !toggleButton
            if (toggleButton) {
                EventBus.getDefault()
                    .post(MessageEvent(MessageEvent.TYPE_view, 1))
                binding.toolBar.view2But.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.filter_view2
                    )
                )
            } else {
                EventBus.getDefault()
                    .post(MessageEvent(MessageEvent.TYPE_view, 2))
                binding.toolBar.view2But.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.filter_view_white
                    )
                )
            }
        }

        binding.toolBar.mainSearchBtn.setOnClickListener {

            selectBottomTab(R.id.searchFragment, null)
        }

        binding.toolBar.SearchBtn.setOnClickListener {
            selectBottomTab(R.id.searchFragment, null)

        }

        binding.toolBar.sortBut.setOnClickListener {
            toggleSortButton = !toggleSortButton
            if (toggleSortButton) {
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_SORT))

            } else {
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_SORT_OLD))

            }


        }


        binding.toolBar.addExtra.setOnClickListener {
            val intent = Intent(activity, ExtraRequestActivity::class.java)
            startActivity(intent)
        }

        binding.toolBar.backBtn.setOnClickListener {
            onBackPressed()
        }

//        val deviceState: OSDeviceState = OneSignal.getDeviceState()
//        val userId: String? = if (deviceState != null) deviceState.getUserId() else null
//
//        OneSignal.idsAvailable { userId: String, registrationId: String? ->
//            Log.d("debug", "Log User:$userId")
//            if (registrationId != null) Log.d(
//                "debug",
//                "Log token one signal first :" + OneSignal.getPermissionSubscriptionState()
//                    .subscriptionStatus.userId
//            )
//            Log.d("debug", "Log token one signal second  :$registrationId")
//            Log.d("debug", "Log token firebase:" + UtilityApp.getFCMToken())
//        }


        binding.toolBar.filterBut.setOnClickListener {
            val filterDialog =
                FilterDialog(
                    activity,object:DataFetcherCallBack{
                        override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                            val sortType:Int= obj as Int
                            EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_SORT2, sortType))

                        }
                    })
            filterDialog.show()
        }


        binding.toolBar.sort2But.setOnClickListener {
            EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_SORT2, 5))

        }
    }

    private fun selectBottomTab(resId: Int, bundle: Bundle?) {
        var newFragment: Fragment? = null
        when (resId) {
            R.id.homeButton -> {
                newFragment = HomeFragment()
                lastBottomFragmentId = R.id.homeButton
            }
            R.id.cartButton -> {
                newFragment = CartFragment()
                lastBottomFragmentId = R.id.cartButton
            }
            R.id.categoryButton -> {
                newFragment = CategoryFragment()
                lastBottomFragmentId = R.id.categoryButton
            }
            R.id.offerButton -> {
                newFragment = OfferFragment()
                lastBottomFragmentId = R.id.offerButton
            }
            R.id.myAccountButton -> {
                newFragment = MyAccountFragment()
                lastBottomFragmentId = R.id.myAccountButton
            }
            R.id.searchFragment -> {
                newFragment = SearchFragment()
            }
            R.id.specialOffersFragment -> {
                newFragment = SpecialOfferFragment()
            }
            R.id.categoryProductsFragment -> {
                newFragment = CategoryProductsFragment()
            }
            R.id.invoiceFragment -> {
                newFragment = InvoiceFragment()
            }

        }

        println("Log main resId $resId")
        println("Log main selectedFragmentId $selectedFragmentId")
        if (resId != selectedFragmentId && newFragment != null) {
            println("Log main newFragment $newFragment")
            newFragment.arguments = bundle
            ft = fragmentManager?.beginTransaction()
            ft?.addToBackStack(newFragment.javaClass.simpleName)
            ft?.add(R.id.mainContainer, newFragment)?.commit()
            println("Log main fragments ${fragmentManager?.fragments}")
        }

    }

    private fun changeColor(id: Int) {
        var hasSelectTab = false
        for (tab in tabNavArr) {
            if (tab.id == id) {
                tab.tabIcon?.setImageDrawable(
                    ContextCompat.getDrawable(activity, tab.tabActiveRes!!)
                )
                tab.tabText?.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorPrimary
                    )
                )
                hasSelectTab = true
            } else {
                tab.tabIcon?.setImageDrawable(
                    ContextCompat.getDrawable(activity, tab.tabInActiveRes!!)
                )
                tab.tabText?.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.font_gray
                    )
                )
            }
        }

        if (!hasSelectTab)
            changeColor(lastBottomFragmentId)
    }

    private val cartsCount: Unit
        get() {
            cartCount = UtilityApp.getCartCount()
            putBadge(cartCount)
        }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
        println("Log main eventBus onResume ${EventBus.getDefault().isRegistered(this)}")
        if (UtilityApp.isLogin()) {
            cartsCount
        }
    }

    public override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
        println("Log main eventBus onStart ${EventBus.getDefault().isRegistered(this)}")
        initIntentExtra(intent?.extras)
    }

    public override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        println("Log main eventBus onStop ${EventBus.getDefault().isRegistered(activity)}")
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        println("Log main event " + event.type)
        when (event.type) {
            MessageEvent.TYPE_FRAGMENT -> {
                val bundle = event.data as Bundle
                val fragmentId = bundle.getInt(Constants.KEY_FRAGMENT_ID)

                selectBottomTab(fragmentId, bundle)

            }
            MessageEvent.TYPE_SHOW_SEARCH -> {
                val showSearchBtn = event.data as Boolean
                binding.toolBar.SearchBtn.visibility =
                    if (showSearchBtn) visible else gone
            }
            MessageEvent.TYPE_UPDATE_CART -> cartsCount


        }
    }


    private fun initIntentExtra(bundle: Bundle?) {
        var searchTEXT = ""
        var fromInsideApp = false
        if (bundle != null) {
            println("Log main has bundle")
            val toCart = bundle.getBoolean(Constants.CART, false)
            val fragmentType = bundle.getString(Constants.KEY_OPEN_FRAGMENT, "")
            val subCatId = intent.extras?.getInt(Constants.SUB_CAT_ID)
            val bookletsModel = bundle.getSerializable(Constants.bookletsModel) as BookletsModel?
            searchTEXT = bundle.getString(Constants.inputType_text) ?: ""
            fromInsideApp = bundle.getBoolean(Constants.Inside_app)
            val toFragHome = bundle.getBoolean(Constants.TO_FRAG_HOME)
            if (toCart) {
                EventBus.getDefault()
                    .post(MessageEvent(MessageEvent.TYPE_REFRESH))
                binding.cartButton.performClick()
            } else if (toFragHome) {
                binding.homeButton.performClick()
            } else if (fragmentType == Constants.FRAG_CATEGORY_DETAILS) {
                if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size > 0) {
                    categoryModelList = UtilityApp.getCategories()
                }

                val categoryBundle = Bundle()
                categoryBundle.putSerializable(
                    Constants.CAT_LIST,
                    categoryModelList
                )
                categoryBundle.putInt(Constants.SUB_CAT_ID, subCatId ?: 0)
                categoryBundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, categoryBundle))

            } else if (fragmentType == Constants.FRAG_CATEGORIES) {
                binding.categoryButton.performClick()
            } else if (fragmentType == Constants.FRAG_OFFERS) {
                binding.offerButton.performClick()
            } else if (fragmentType == Constants.FRAG_HOME) {
                binding.homeButton.performClick()
            } else if (fragmentType == Constants.FRAG_SEARCH) {
                val searchBundle = Bundle()
                searchBundle.putString(Constants.inputType_text, searchTEXT)
                searchBundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.searchFragment)
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, searchBundle))
            } else if (fragmentType == Constants.FRAG_BROSHORE) {
                if (bookletsModel != null && bookletsModel.id != 0) {
                    val specialOfferBundle = Bundle()
                    specialOfferBundle.putSerializable(
                        Constants.bookletsModel,
                        bookletsModel
                    )
                    specialOfferBundle.putBoolean(Constants.TO_BROSHER, true)
                    specialOfferBundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.specialOffersFragment)
                    EventBus.getDefault()
                        .post(MessageEvent(MessageEvent.TYPE_FRAGMENT, specialOfferBundle))
                    binding.toolBar.backBtn.setOnClickListener {
                        if (fromInsideApp) {
                            val intent = Intent(activity, AllBookleteActivity::class.java)
                            intent.putExtra(Constants.Activity_type, Constants.BOOKLETS)
                            startActivity(intent)
                        } else {
                            onBackPressed()
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (selectedFragmentId == R.id.homeButton || fragmentManager?.backStackEntryCount == 1) {
            finish()
        } else
            super.onBackPressed()

    }

    private val validation: Unit
        get() {
            DataFeacher(
                false, object : DataFetcherCallBack {
                    override fun Result(
                        obj: Any?,
                        func: String?,
                        IsSuccess: Boolean
                    ) {
                        if (IsSuccess) {
                            val result = obj as GeneralModel?
                            if (result != null && result.message != null) {
                                if (result.status == Constants.OK_STATUS) {
                                    Log.i(
                                        ContentValues.TAG,
                                        "Log getValidation" + result.message
                                    )
                                } else {
                                    val click: ConfirmDialog.Click =
                                        object : ConfirmDialog.Click() {
                                            override fun click() {
                                                ActivityHandler.OpenGooglePlay(
                                                    activity
                                                )
                                            }
                                        }
                                    val cancel: ConfirmDialog.Click =
                                        object : ConfirmDialog.Click() {
                                            override fun click() {
                                                finish()
                                            }
                                        }
                                    ConfirmDialog(
                                        activity,
                                        getString(R.string.updateMessage),
                                        R.string.ok,
                                        R.string.cancel_label,
                                        click,
                                        cancel,
                                        false
                                    )
                                }
                                Log.i(
                                    ContentValues.TAG,
                                    "Log getValidation" + result.message
                                )
                            }
                        }
                    }
                }
            ).getValidate(
                Constants.deviceType,
                UtilityApp.getAppVersionStr(),
                BuildConfig.VERSION_CODE
            )
        }

    @SuppressLint("UnsafeExperimentalUsageError")
    fun putBadge(cartCount: Int) {
        if (cartCount == 0) {
            binding.cartCountTv.visibility = View.GONE
        } else {
            binding.cartCountTv.visibility = View.VISIBLE
            binding.cartCountTv.text = cartCount.toString()
        }

    }





}
