//package com.ramez.shopp
//
//import android.annotation.SuppressLint
//import android.content.ContentValues
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentManager
//import androidx.fragment.app.FragmentTransaction
//import com.androidnetworking.BuildConfig
//import com.onesignal.OneSignal
//import com.ramez.shopp.Activities.ActivityBase
//import com.ramez.shopp.Activities.AllBookleteActivity
//import com.ramez.shopp.Activities.ExtraRequestActivity
//import com.ramez.shopp.ApiHandler.DataFeacher
//import com.ramez.shopp.Classes.*
//import com.ramez.shopp.Dialogs.ConfirmDialog
//import com.ramez.shopp.Fragments.*
//import com.ramez.shopp.Models.BookletsModel
//import com.ramez.shopp.Models.BottomNavModel
//import com.ramez.shopp.Models.GeneralModel
//import com.ramez.shopp.Models.LocalModel
//import com.ramez.shopp.Utils.ActivityHandler
//import com.ramez.shopp.databinding.ActivityMainBinding
//import org.greenrobot.eventbus.EventBus
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
//import java.util.*
//
//
//class MainActivity2 : ActivityBase() {
//
//    private var fragmentManager: FragmentManager? = null
//    private var ft: FragmentTransaction? = null
//    var oldFragment: Fragment? = null
//    var currFragment: Fragment? = null
//    private var lastBottomFragmentId = R.id.homeButton
//    private var selectedFragmentId = 0
//
//    var cartCount = 0
//    var storeId = 7263
//    var localModel: LocalModel? = null
//    private var toggleButton = false
//    var country_name = "BH"
//    var categoryModelList: ArrayList<CategoryModel>? = null
//
//    private lateinit var tabNavArr: Array<BottomNavModel>
//
//    lateinit var binding: ActivityMainBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        localModel =
//            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
//                activiy
//            )
//
//        fragmentManager = supportFragmentManager
//
//        tabNavArr = arrayOf(
//            BottomNavModel(
//                R.id.homeButton,
//                binding.tab1Tv,
//                binding.tab1Icon,
//                R.string.string_menu_home,
//                R.drawable.home_clicked,
//                R.drawable.home_icon,
//            ),
//            BottomNavModel(
//                R.id.categoryButton,
//                binding.tab2Tv,
//                binding.tab2Icon,
//                R.string.category,
//                R.drawable.category_click,
//                R.drawable.category_icon,
//            ),
//            BottomNavModel(
//                R.id.cartButton,
//                binding.tab3Tv,
//                binding.tab3Icon,
//                R.string.cart,
//                R.drawable.cart_icon_bottom,
//                R.drawable.cart_icon_before,
//            ),
//            BottomNavModel(
//                R.id.offerButton,
//                binding.tab4Tv,
//                binding.tab4Icon,
//                R.string.offer_text,
//                R.drawable.offer_clicked,
//                R.drawable.offer_icon,
//            ),
//            BottomNavModel(
//                R.id.myAccountButton,
//                binding.tab5Tv,
//                binding.tab5Icon,
//                R.string.myaccount,
//                R.drawable.my_account_clciked,
//                R.drawable.myaccount_icon,
//            ),
//        )
//
////        supportFragmentManager.beginTransaction()
////            .replace(R.id.mainContainer, HomeFragment(), "HomeFragment").commit()
////        binding.toolBar.backBtn.visibility = View.GONE
////        binding.toolBar.view2But.visibility = View.GONE
////        binding.homeButn.setImageDrawable(
////            ContextCompat.getDrawable(
////                activiy,
////                R.drawable.home_clicked
////            )
////        )
//
//
//        categoryModelList = ArrayList()
//        validation
//        initListeners()
//        intentExtra
//        if (localModel?.cityId != null) {
//            storeId = localModel?.cityId?.toInt() ?: 0
//            country_name = localModel?.shortname ?: Constants.default_short_name
//            OneSignal.sendTag(Constants.COUNTRY, country_name)
//        }
//        if (UtilityApp.isLogin()) {
//            cartsCount
//        }
//        AnalyticsHandler.APP_OPEN()
//    }
//
//    private fun initListeners() {
//        binding.homeButton.setOnClickListener { view1 ->
//            binding.toolBar.backBtn.visibility = View.GONE
//            binding.toolBar.sortBut.visibility = View.GONE
//            binding.toolBar.view2But.visibility = View.GONE
//            binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//            binding.toolBar.SearchBtn.visibility = View.GONE
//            try {
//                initBottomNav(0)
//            } catch (ex: NumberFormatException) { // handle your exception
//            }
//            binding.toolBar.addExtra.visibility = View.GONE
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.mainContainer, HomeFragment(), "HomeFragment").commit()
//            if (UtilityApp.isLogin()) {
//                cartsCount
//            }
//        }
//        binding.categoryButton.setOnClickListener { view1 ->
//            binding.toolBar.backBtn.visibility = View.GONE
//            binding.toolBar.sortBut.visibility = View.GONE
//            binding.toolBar.view2But.visibility = View.GONE
//            binding.toolBar.mainSearchBtn.visibility = View.GONE
//            binding.toolBar.SearchBtn.visibility = View.GONE
//            initBottomNav(1)
//            binding.toolBar.addExtra.visibility = View.GONE
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.mainContainer, CategoryFragment(), "CategoryFragment").commit()
//            if (UtilityApp.isLogin()) {
//                cartsCount
//            }
//        }
//        binding.cartButton.setOnClickListener { view1 ->
//            binding.cartCountTv.visibility = View.GONE
//            binding.toolBar.backBtn.visibility = View.GONE
//            binding.toolBar.sortBut.visibility = View.GONE
//            binding.toolBar.view2But.visibility = View.GONE
//            binding.toolBar.mainSearchBtn.visibility = View.GONE
//            initBottomNav(2)
//            if (UtilityApp.isLogin()) {
//                binding.toolBar.addExtra.visibility = View.VISIBLE
//            } else {
//                binding.toolBar.addExtra.visibility = View.GONE
//            }
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.mainContainer, CartFragment(), "CartFragment").commit()
//        }
//        binding.offerButton.setOnClickListener { view1 ->
//            EventBus.getDefault()
//                .post(MessageEvent(MessageEvent.Type_offer))
//            binding.toolBar.mainSearchBtn.visibility = View.GONE
//            binding.toolBar.backBtn.visibility = View.GONE
//            binding.toolBar.addExtra.visibility = View.GONE
//            binding.toolBar.SearchBtn.visibility = View.GONE
//            initBottomNav(3)
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.mainContainer, OfferFragment(), "OfferFragment").commit()
//            if (UtilityApp.isLogin()) {
//                cartsCount
//            }
//        }
//        binding.myAccountButton.setOnClickListener { view1 ->
//            binding.toolBar.sortBut.visibility = View.GONE
//            binding.toolBar.view2But.visibility = View.GONE
//            binding.toolBar.backBtn.visibility = View.GONE
//            binding.toolBar.mainSearchBtn.visibility = View.GONE
//            binding.toolBar.SearchBtn.visibility = View.GONE
//            initBottomNav(4)
//            binding.toolBar.addExtra.visibility = View.GONE
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.mainContainer, MyAccountFragment(), "MyAccountFragment").commit()
//            if (UtilityApp.isLogin()) {
//                cartsCount
//            }
//        }
//        binding.toolBar.view2But.setOnClickListener { view11 ->
//            toggleButton = !toggleButton
//            if (toggleButton) {
//                EventBus.getDefault()
//                    .post(MessageEvent(MessageEvent.TYPE_view, 1))
//                binding.toolBar.view2But.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        activiy,
//                        R.drawable.filter_view2
//                    )
//                )
//            } else {
//                EventBus.getDefault()
//                    .post(MessageEvent(MessageEvent.TYPE_view, 2))
//                binding.toolBar.view2But.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        activiy,
//                        R.drawable.filter_view_white
//                    )
//                )
//            }
//        }
//        binding.toolBar.mainSearchBtn.setOnClickListener { view ->
//            EventBus.getDefault()
//                .post(MessageEvent(MessageEvent.TYPE_search, false))
//        }
//        binding.toolBar.SearchBtn.setOnClickListener { view ->
//            EventBus.getDefault()
//                .post(MessageEvent(MessageEvent.TYPE_search, false))
//        }
//        binding.toolBar.sortBut.setOnClickListener { view11 ->
//            EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_SORT))
//        }
//        binding.toolBar.addExtra.setOnClickListener { view1 ->
//            val intent = Intent(activiy, ExtraRequestActivity::class.java)
//            startActivity(intent)
//        }
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
//    }
//
//    private fun selectBottomTab(resId: Int, bundle: Bundle?) {
////        var navId = 0
//        var newFragment: Fragment? = null
////        isMyProfileTab = false
//        when (resId) {
//            R.id.mainBtn -> {
//                //                        Toast("lastNewsBtn");
//                newFragment = MainScreenFragment()
//                lastBottomFragmentId = R.id.mainBtn
//                bundle?.putInt(Constants.KEY_FRAGMENT_ID, R.id.mainBtn)
////                gui_position = 0
////                mTitle = getString(R.string.home)
////                navId = R.id.mainScreenFragment
//            }
//            R.id.timeLineBtn -> {
//                lastBottomFragmentId = R.id.timeLineBtn
//                bundle?.putInt(Constants.KEY_FRAGMENT_ID, R.id.timeLineBtn)
////                val bundle = Bundle()
////                bundle.putInt(Constants.KEY_SEASON_ID, selectedSeasonModel?.id ?: 0)
////                bundle.putString(Constants.KEY_TITLE, searchTitle)
////                bundle.putString(Constants.KEY_PAGE_TYPE, selectedPageType)
//
//                if (BuildConfig.FLAVOR == Constants.FLAVOUR_SOCIAL) {
//                    binding.navBarLY.tabTimeLineTV.text = getString(R.string.time_line)
////                    newFragment = if (UtilityApp.isLogin) TimelineFragment() else LoginRegisterFragment()
////                    newFragment = TimelineFragment()
//                    newFragment = TimelineFragment2()
//                } else {
//                    binding.navBarLY.tabTimeLineTV.text = getString(R.string.activities)
//                    newFragment = if (UtilityApp.isLogin) MainActivitiesFragment() else LoginRegisterFragment()
//                }
////                newFragment?.arguments = bundle
////                gui_position = 1
////                mTitle = getString(R.string.search)
////                navId = R.id.timeLineFragment
//            }
////            R.id.newPostBtn -> {
////
////                val intent = Intent(activiy, NewPostActivity::class.java)
////                startActivity(intent)
////            }
////            R.id.notificationBtn -> {
////                newFragment = MainActivitiesFragment()
//////                    if (UtilityApp.isLogin) MainOrdersFragment() else LoginRegisterFragment()
//////                gui_position = 3
//////                mTitle = getString(R.string.orders)
//////                navId = R.id.notificationsFragment
////            }
//            R.id.profileBtn -> {
////                if (UtilityApp.isLogin) {
//                lastBottomFragmentId = R.id.profileBtn
//                bundle?.putInt(Constants.KEY_FRAGMENT_ID, R.id.profileBtn)
//                if (BuildConfig.FLAVOR == Constants.FLAVOUR_SOCIAL) {
//                    newFragment = if (UtilityApp.isLogin) {
//                        ProfileDetailsFragment()
//                    } else {
//                        AccountSettingFragment2()
//                    }
////                    newFragment.arguments = bundle
////                    newFragment = AccountSettingFragment2()
////                    isMyProfileTab = true
////                        selectedFragmentId = R.id.profileBtn
////                        res = R.id.profileDetailsFragment
//                } else {
//                    newFragment = if (UtilityApp.isLogin) {
//                        AccountSettingFragment()
//                    } else {
//                        SettingFragment()
//                    }
//                }
//
//            }
//            R.id.searchFragment -> {
//                val searchType = bundle?.getString(Constants.KEY_SEARCH_TYPE)
//                newFragment =
//                    if (searchType == Constants.SEARCH_SOCIAL) SearchSocialFragment() else SearchFragment()
////                newFragment.arguments = bundle
////                    if (UtilityApp.isLogin) AccountSettingFragment() else SettingFragment()
////                gui_position = 4
////                mTitle = getString(R.string.profile)
////                navId = R.id.settingsFragment
//            }
//
//            R.id.mainSearchBtn -> {
////                bundle.putInt(Constants.KEY_SEASON_ID, selectedSeasonModel?.id ?: 0)
////                bundle.putString(Constants.KEY_TITLE, searchTitle)
////                bundle.putString(Constants.KEY_PAGE_TYPE, selectedPageType)
//                newFragment = SearchFragment()
////                newFragment.arguments = bundle
////                gui_position = 1
//                mTitle = getString(R.string.search)
//            }
//            R.id.profileDetailsFragment -> {
////                bundle.putInt(Constants.KEY_SEASON_ID, selectedSeasonModel?.id ?: 0)
////                bundle.putString(Constants.KEY_TITLE, searchTitle)
////                bundle.putString(Constants.KEY_PAGE_TYPE, selectedPageType)
//                newFragment = ProfileDetailsFragment()
////                newFragment.arguments = bundle
////                gui_position = 1
////                mTitle = getString(R.string.profile_details)
//            }
//            R.id.accountSettingsFragment -> {
////                bundle.putInt(Constants.KEY_SEASON_ID, selectedSeasonModel?.id ?: 0)
////                bundle.putString(Constants.KEY_TITLE, searchTitle)
////                bundle.putString(Constants.KEY_PAGE_TYPE, selectedPageType)
//                newFragment = AccountSettingFragment2()
////                newFragment.arguments = bundle
////                gui_position = 1
////                mTitle = getString(R.string.profile_details)
//            }
//            R.id.postsViewerFragment -> {
//                newFragment = PostsViewerFragment()
//            }
//            R.id.followersFragment -> {
//                newFragment = FollowersFollowingsFragment()
//            }
//
//        }
//    private fun changeColor(id: Int) {
//        var hasSelectTab = false
//        for (tab in tabNavArr) {
//            if (tab.id == id) {
//                tab.tabIcon?.setImageDrawable(
//                    ContextCompat.getDrawable(activiy, tab.tabActiveRes!!)
//                )
//                tab.tabText?.setTextColor(
//                    ContextCompat.getColor(
//                        activiy,
//                        R.color.colorPrimary
//                    )
//                )
//            } else {
//                tab.tabIcon?.setImageDrawable(
//                    ContextCompat.getDrawable(activiy, tab.tabInActiveRes!!)
//                )
//                tab.tabText?.setTextColor(
//                    ContextCompat.getColor(
//                        activiy,
//                        R.color.font_gray
//                    )
//                )
//            }
//        }
//
//        if (!hasSelectTab)
//            changeColor(lastBottomFragmentId)
//    }
//
//    //        System.out.println("Log cart count " + cartCount);
//    private val cartsCount: Unit
//        private get() {
//            cartCount = UtilityApp.getCartCount()
//            //        System.out.println("Log cart count " + cartCount);
//            putBadge(cartCount)
//        }
//
//    override fun onResume() {
//        super.onResume()
//
//        // binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);
//        if (UtilityApp.isLogin()) {
//            cartsCount
//        }
//    }
//
//    public override fun onStart() {
//        super.onStart()
//        EventBus.getDefault().register(this)
//    }
//
//    public override fun onStop() {
//        super.onStop()
//        EventBus.getDefault().unregister(this)
//    }
//
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    fun onMessageEvent(event: MessageEvent) {
////        println("Log event " + event.type)
////        binding.toolBar.mainSearchBtn.visibility = View.GONE
////        binding.toolBar.SearchBtn.visibility = View.GONE
////        when (event.type) {
////            MessageEvent.TYPE_invoice -> {
////                binding.toolBar.backBtn.visibility = View.VISIBLE
////                binding.toolBar.view2But.visibility = View.GONE
////                binding.toolBar.sortBut.visibility = View.GONE
////                binding.toolBar.addExtra.visibility = View.GONE
////                binding.toolBar.backBtn.setOnClickListener { view ->
////                    supportFragmentManager.beginTransaction()
////                        .replace(R.id.mainContainer, CartFragment(), "CartFragment").commit()
////                    binding.toolBar.backBtn.visibility = View.GONE
////                }
////            }
////            MessageEvent.TYPE_BROUSHERS -> {
////                //   boolean is_Home = (boolean) event.data;
////                binding.toolBar.backBtn.visibility = View.VISIBLE
////                binding.toolBar.view2But.visibility = View.GONE
////                binding.toolBar.sortBut.visibility = View.GONE
////                binding.toolBar.addExtra.visibility = View.GONE
////                binding.toolBar.backBtn.setOnClickListener { view ->
////                    supportFragmentManager.beginTransaction()
////                        .replace(R.id.mainContainer, HomeFragment(), "HomeFragment").commit()
////                    binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
////                    binding.toolBar.backBtn.visibility = View.GONE
////                    binding.toolBar.SearchBtn.visibility = View.GONE
////                }
////            }
////            MessageEvent.TYPE_POSITION -> {
////                binding.toolBar.backBtn.visibility = View.GONE
////                binding.toolBar.view2But.visibility = View.GONE
////                binding.toolBar.sortBut.visibility = View.GONE
////                binding.tab1Txt.setTextColor(
////                    ContextCompat.getColor(
////                        activiy,
////                        R.color.colorPrimaryDark
////                    )
////                )
////                val position = event.data as Int
////                val selectedFragment: Fragment
////                if (position == 0) {
////                    binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
////                    binding.toolBar.SearchBtn.visibility = View.GONE
////                    selectedFragment = HomeFragment()
////                    EventBus.getDefault().post(MessageEvent(MessageEvent.REFRESH_CART))
////                } else if (position == 1) {
////                    selectedFragment = CategoryFragment()
////                } else if (position == 2) {
////                    selectedFragment = CartFragment()
////                } else if (position == 3) {
////                    selectedFragment = OfferFragment()
////                } else if (position == 4) {
////                    selectedFragment = MyAccountFragment()
////                } else {
////                    binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
////                    binding.toolBar.SearchBtn.visibility = View.GONE
////                    selectedFragment = HomeFragment()
////                }
////                initBottomNav(position)
////                supportFragmentManager.beginTransaction()
////                    .replace(R.id.mainContainer, selectedFragment, "MyAccountFragment").commit()
////            }
////            MessageEvent.TYPE_CATEGORY_PRODUCT -> {
////                binding.toolBar.backBtn.visibility = View.VISIBLE
////                binding.toolBar.view2But.visibility = View.VISIBLE
////                initBottomNav(0)
////                binding.toolBar.backBtn.setOnClickListener { view ->
////                    supportFragmentManager.beginTransaction()
////                        .replace(R.id.mainContainer, HomeFragment(), "HomeFragment").commit()
////                    binding.toolBar.backBtn.visibility = View.GONE
////                    binding.toolBar.view2But.visibility = View.GONE
////                    binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
////                    binding.toolBar.SearchBtn.visibility = View.GONE
////                }
////            }
////            MessageEvent.TYPE_SHOW_SEARCH -> {
////                val showSearchBtn = event.data as Boolean
////                binding.toolBar.SearchBtn.visibility =
////                    if (showSearchBtn) View.VISIBLE else View.GONE
////            }
////            MessageEvent.Type_offer -> {
////                binding.toolBar.view2But.visibility = View.VISIBLE
////                binding.toolBar.SearchBtn.visibility = View.GONE
////            }
////            MessageEvent.TYPE_main -> {
////                binding.toolBar.backBtn.visibility = View.GONE
////                binding.toolBar.view2But.visibility = View.GONE
////                binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
////                binding.toolBar.SearchBtn.visibility = View.GONE
////                initBottomNav(0)
////                supportFragmentManager.beginTransaction()
////                    .replace(R.id.mainContainer, HomeFragment(), "HomeFragment").commit()
////            }
////            MessageEvent.TYPE_UPDATE_CART -> cartsCount
////            MessageEvent.TYPE_view -> {
////                binding.toolBar.backBtn.visibility = View.VISIBLE
////                binding.toolBar.view2But.visibility = View.VISIBLE
////                binding.toolBar.SearchBtn.visibility = View.GONE
////            }
////            MessageEvent.TYPE_search -> {
////                val toCat = booleanArrayOf(event.data as Boolean)
////                println("Log toCat " + toCat[0])
////                binding.toolBar.backBtn.visibility = View.VISIBLE
////                binding.toolBar.view2But.visibility = View.VISIBLE
////                binding.toolBar.sortBut.visibility = View.VISIBLE
////                binding.toolBar.backBtn.setOnClickListener { view ->
////                    if (toCat[0]) {
////                        println("Log toCat " + toCat[0])
////                        hideSoftKeyboard(activiy)
////                        onBackPressed()
////                        binding.toolBar.mainSearchBtn.visibility = View.GONE
////                        binding.toolBar.backBtn.visibility = View.VISIBLE
////                        binding.toolBar.view2But.visibility = View.VISIBLE
////                        binding.toolBar.sortBut.visibility = View.GONE
////                        binding.toolBar.SearchBtn.visibility = View.GONE
////                        toCat[0] = false
////                    } else {
////                        supportFragmentManager.beginTransaction()
////                            .add(R.id.mainContainer, HomeFragment(), "HomeFragment")
////                            .addToBackStack(null).commit()
////                        binding.toolBar.backBtn.visibility = View.GONE
////                        binding.toolBar.view2But.visibility = View.GONE
////                        binding.toolBar.sortBut.visibility = View.GONE
////                        binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
////                        binding.toolBar.SearchBtn.visibility = View.GONE
////                        hideSoftKeyboard(activiy)
////                    }
////                }
////                supportFragmentManager.beginTransaction().add(
////                    R.id.mainContainer, SearchFragment(),
////                    "searchFragment"
////                ).addToBackStack(null).commit()
////            }
////            MessageEvent.TYPE_SORT -> {
////                binding.toolBar.backBtn.visibility = View.GONE
////                binding.toolBar.view2But.visibility = View.VISIBLE
////                binding.toolBar.sortBut.visibility = View.VISIBLE
////            }
////            else -> {
////                println("Log default event")
////                binding.toolBar.backBtn.visibility = View.GONE
////                binding.toolBar.sortBut.visibility = View.GONE
////                binding.toolBar.view2But.visibility = View.GONE
////                binding.toolBar.addExtra.visibility = View.GONE
////            }
////        }
////    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMessageEvent(event: MessageEvent) {
//        println("Log event " + event.type)
//        binding.toolBar.mainSearchBtn.visibility = View.GONE
//        binding.toolBar.SearchBtn.visibility = View.GONE
//        when (event.type) {
//            MessageEvent.TYPE_invoice -> {
//                binding.toolBar.backBtn.visibility = View.VISIBLE
//                binding.toolBar.view2But.visibility = View.GONE
//                binding.toolBar.sortBut.visibility = View.GONE
//                binding.toolBar.addExtra.visibility = View.GONE
//                binding.toolBar.backBtn.setOnClickListener { view ->
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.mainContainer, CartFragment(), "CartFragment").commit()
//                    binding.toolBar.backBtn.visibility = View.GONE
//                }
//            }
//            MessageEvent.TYPE_BROUSHERS -> {
//                //   boolean is_Home = (boolean) event.data;
//                binding.toolBar.backBtn.visibility = View.VISIBLE
//                binding.toolBar.view2But.visibility = View.GONE
//                binding.toolBar.sortBut.visibility = View.GONE
//                binding.toolBar.addExtra.visibility = View.GONE
//                binding.toolBar.backBtn.setOnClickListener { view ->
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.mainContainer, HomeFragment(), "HomeFragment").commit()
//                    binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                    binding.toolBar.backBtn.visibility = View.GONE
//                    binding.toolBar.SearchBtn.visibility = View.GONE
//                }
//            }
//            MessageEvent.TYPE_POSITION -> {
//                binding.toolBar.backBtn.visibility = View.GONE
//                binding.toolBar.view2But.visibility = View.GONE
//                binding.toolBar.sortBut.visibility = View.GONE
//                binding.tab1Txt.setTextColor(
//                    ContextCompat.getColor(
//                        activiy,
//                        R.color.colorPrimaryDark
//                    )
//                )
//                val position = event.data as Int
//                val selectedFragment: Fragment
//                if (position == 0) {
//                    binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                    binding.toolBar.SearchBtn.visibility = View.GONE
//                    selectedFragment = HomeFragment()
//                    EventBus.getDefault().post(MessageEvent(MessageEvent.REFRESH_CART))
//                } else if (position == 1) {
//                    selectedFragment = CategoryFragment()
//                } else if (position == 2) {
//                    selectedFragment = CartFragment()
//                } else if (position == 3) {
//                    selectedFragment = OfferFragment()
//                } else if (position == 4) {
//                    selectedFragment = MyAccountFragment()
//                } else {
//                    binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                    binding.toolBar.SearchBtn.visibility = View.GONE
//                    selectedFragment = HomeFragment()
//                }
//                initBottomNav(position)
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.mainContainer, selectedFragment, "MyAccountFragment").commit()
//            }
//            MessageEvent.TYPE_CATEGORY_PRODUCT -> {
//                binding.toolBar.backBtn.visibility = View.VISIBLE
//                binding.toolBar.view2But.visibility = View.VISIBLE
//                initBottomNav(0)
//                binding.toolBar.backBtn.setOnClickListener { view ->
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.mainContainer, HomeFragment(), "HomeFragment").commit()
//                    binding.toolBar.backBtn.visibility = View.GONE
//                    binding.toolBar.view2But.visibility = View.GONE
//                    binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                    binding.toolBar.SearchBtn.visibility = View.GONE
//                }
//            }
//            MessageEvent.TYPE_SHOW_SEARCH -> {
//                val showSearchBtn = event.data as Boolean
//                binding.toolBar.SearchBtn.visibility =
//                    if (showSearchBtn) View.VISIBLE else View.GONE
//            }
//            MessageEvent.Type_offer -> {
//                binding.toolBar.view2But.visibility = View.VISIBLE
//                binding.toolBar.SearchBtn.visibility = View.GONE
//            }
//            MessageEvent.TYPE_main -> {
//                binding.toolBar.backBtn.visibility = View.GONE
//                binding.toolBar.view2But.visibility = View.GONE
//                binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                binding.toolBar.SearchBtn.visibility = View.GONE
//                initBottomNav(0)
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.mainContainer, HomeFragment(), "HomeFragment").commit()
//            }
//            MessageEvent.TYPE_UPDATE_CART -> cartsCount
//            MessageEvent.TYPE_view -> {
//                binding.toolBar.backBtn.visibility = View.VISIBLE
//                binding.toolBar.view2But.visibility = View.VISIBLE
//                binding.toolBar.SearchBtn.visibility = View.GONE
//            }
//            MessageEvent.TYPE_search -> {
//                val toCat = booleanArrayOf(event.data as Boolean)
//                println("Log toCat " + toCat[0])
//                binding.toolBar.backBtn.visibility = View.VISIBLE
//                binding.toolBar.view2But.visibility = View.VISIBLE
//                binding.toolBar.sortBut.visibility = View.VISIBLE
//                binding.toolBar.backBtn.setOnClickListener { view ->
//                    if (toCat[0]) {
//                        println("Log toCat " + toCat[0])
//                        hideSoftKeyboard(activiy)
//                        onBackPressed()
//                        binding.toolBar.mainSearchBtn.visibility = View.GONE
//                        binding.toolBar.backBtn.visibility = View.VISIBLE
//                        binding.toolBar.view2But.visibility = View.VISIBLE
//                        binding.toolBar.sortBut.visibility = View.GONE
//                        binding.toolBar.SearchBtn.visibility = View.GONE
//                        toCat[0] = false
//                    } else {
//                        supportFragmentManager.beginTransaction()
//                            .add(R.id.mainContainer, HomeFragment(), "HomeFragment")
//                            .addToBackStack(null).commit()
//                        binding.toolBar.backBtn.visibility = View.GONE
//                        binding.toolBar.view2But.visibility = View.GONE
//                        binding.toolBar.sortBut.visibility = View.GONE
//                        binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                        binding.toolBar.SearchBtn.visibility = View.GONE
//                        hideSoftKeyboard(activiy)
//                    }
//                }
//                supportFragmentManager.beginTransaction().add(
//                    R.id.mainContainer, SearchFragment(),
//                    "searchFragment"
//                ).addToBackStack(null).commit()
//            }
//            MessageEvent.TYPE_SORT -> {
//                binding.toolBar.backBtn.visibility = View.GONE
//                binding.toolBar.view2But.visibility = View.VISIBLE
//                binding.toolBar.sortBut.visibility = View.VISIBLE
//            }
//            else -> {
//                println("Log default event")
//                binding.toolBar.backBtn.visibility = View.GONE
//                binding.toolBar.sortBut.visibility = View.GONE
//                binding.toolBar.view2But.visibility = View.GONE
//                binding.toolBar.addExtra.visibility = View.GONE
//            }
//        }
//    }
//
//    private val intentExtra: Unit
//        private get() {
//            var searchTEXT = ""
//            val bundle = intent.extras
//            var from_inside_app = false
//            if (bundle != null) {
//                println("Log main has bundle")
//                val TO_CART = bundle.getBoolean(Constants.CART, false)
//                val fragmentType = bundle.getString(Constants.KEY_OPEN_FRAGMENT, "")
//                val subCatId = intent.extras!!.getInt(Constants.SUB_CAT_ID)
//                val bookletsModel =
//                    bundle.getSerializable(Constants.bookletsModel) as BookletsModel?
//                searchTEXT = bundle.getString(Constants.inputType_text) ?: ""
//                from_inside_app = bundle.getBoolean(Constants.Inside_app)
//                val TO_FRAG_HOME = bundle.getBoolean(Constants.TO_FRAG_HOME)
//                if (TO_CART) {
//                    EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_REFRESH))
//                    binding.cartButton.performClick()
//                } else if (TO_FRAG_HOME) {
//                    binding.toolBar.backBtn.visibility = View.GONE
//                    binding.toolBar.view2But.visibility = View.GONE
//                    binding.toolBar.sortBut.visibility = View.GONE
//                    binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                    binding.toolBar.SearchBtn.visibility = View.GONE
//                    binding.homeButton.performClick()
//                } else if (fragmentType == Constants.FRAG_CATEGORY_DETAILS) {
//                    if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size > 0) {
//                        categoryModelList = UtilityApp.getCategories()
//                    }
//                    val fragmentManager =
//                        supportFragmentManager
//                    val categoryProductsFragment = CategoryProductsFragment()
//                    val bundle1 = Bundle()
//                    bundle1.putSerializable(Constants.CAT_LIST, categoryModelList)
//                    bundle1.putInt(Constants.SUB_CAT_ID, subCatId)
//                    categoryProductsFragment.arguments = bundle
//                    fragmentManager.beginTransaction().replace(
//                        R.id.mainContainer,
//                        categoryProductsFragment,
//                        "categoryProductsFragment"
//                    ).commit()
//                    binding.toolBar.backBtn.visibility = View.VISIBLE
//                    binding.toolBar.view2But.visibility = View.VISIBLE
//                    binding.toolBar.backBtn.setOnClickListener { view ->
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.mainContainer, HomeFragment(), "HomeFragment").commit()
//                        binding.toolBar.backBtn.visibility = View.GONE
//                        binding.toolBar.view2But.visibility = View.GONE
//                        binding.toolBar.sortBut.visibility = View.GONE
//                        binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                        binding.toolBar.SearchBtn.visibility = View.GONE
//                    }
//                } else if (fragmentType == Constants.FRAG_CATEGORIES) {
//                    binding.categoryButton.performClick()
//                } else if (fragmentType == Constants.FRAG_OFFERS) {
//                    binding.offerButton.performClick()
//                } else if (fragmentType == Constants.FRAG_HOME) {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.mainContainer, HomeFragment(), "HomeFragment").commit()
//                    binding.toolBar.backBtn.visibility = View.GONE
//                    binding.toolBar.view2But.visibility = View.GONE
//                    binding.toolBar.sortBut.visibility = View.GONE
//                    binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                    binding.toolBar.SearchBtn.visibility = View.GONE
//                    binding.homeButton.performClick()
//                } else if (fragmentType == Constants.FRAG_SEARCH) {
//                    val bundle2 = Bundle()
//                    bundle2.putString(Constants.inputType_text, searchTEXT)
//                    val fragmentManager =
//                        supportFragmentManager
//                    val searchFragment = SearchFragment()
//                    searchFragment.arguments = bundle2
//                    fragmentManager.beginTransaction()
//                        .replace(R.id.mainContainer, searchFragment, "searchFragment").commit()
//                    binding.toolBar.backBtn.visibility = View.VISIBLE
//                    binding.toolBar.view2But.visibility = View.VISIBLE
//                    binding.toolBar.sortBut.visibility = View.VISIBLE
//                    binding.toolBar.backBtn.setOnClickListener { view ->
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.mainContainer, HomeFragment(), "HomeFragment").commit()
//                        binding.toolBar.backBtn.visibility = View.GONE
//                        binding.toolBar.view2But.visibility = View.GONE
//                        binding.toolBar.sortBut.visibility = View.GONE
//                        binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                        binding.toolBar.SearchBtn.visibility = View.GONE
//                    }
//                } else if (fragmentType == Constants.FRAG_BROSHORE) {
//                    if (bookletsModel != null && bookletsModel.id != 0) {
//                        val fragmentManager =
//                            supportFragmentManager
//                        val specialOfferFragment = SpecialOfferFragment()
//                        val bundle2 = Bundle()
//                        bundle2.putSerializable(Constants.bookletsModel, bookletsModel)
//                        bundle2.putBoolean(Constants.TO_BROSHER, true)
//                        specialOfferFragment.arguments = bundle2
//                        fragmentManager.beginTransaction().replace(
//                            R.id.mainContainer,
//                            specialOfferFragment,
//                            "specialOfferFragment"
//                        ).commit()
//                        binding.toolBar.backBtn.visibility = View.VISIBLE
//                        val finalFrom_inside_app = from_inside_app
//                        binding.toolBar.backBtn.setOnClickListener { view ->
//                            if (finalFrom_inside_app) {
//                                val intent =
//                                    Intent(activiy, AllBookleteActivity::class.java)
//                                intent.putExtra(
//                                    Constants.Activity_type,
//                                    Constants.BOOKLETS
//                                )
//                                startActivity(intent)
//                            } else {
//                                supportFragmentManager.beginTransaction()
//                                    .replace(R.id.mainContainer, HomeFragment(), "HomeFragment")
//                                    .commit()
//                                binding.toolBar.backBtn.visibility = View.GONE
//                                binding.toolBar.view2But.visibility = View.GONE
//                                binding.toolBar.sortBut.visibility = View.GONE
//                                binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                                binding.toolBar.SearchBtn.visibility = View.GONE
//                            }
//                        }
//                    }
//                } else {
//                    binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                    binding.toolBar.SearchBtn.visibility = View.GONE
//                }
//            } else {
//                println("Log main not has bundle")
//                binding.toolBar.mainSearchBtn.visibility = View.VISIBLE
//                binding.toolBar.SearchBtn.visibility = View.GONE
//            }
//        }
//
//    //finish();
//    val validation: Unit
//        get() {
//            DataFeacher(
//                false
//            ) { obj: Any?, func: String?, IsSuccess: Boolean ->
//                if (IsSuccess) {
//                    val result = obj as GeneralModel?
//                    if (result != null && result.message != null) {
//                        if (result.status == Constants.OK_STATUS) {
//                            Log.i(
//                                ContentValues.TAG,
//                                "Log getValidation" + result.message
//                            )
//                        } else {
//                            val click: ConfirmDialog.Click = object : ConfirmDialog.Click() {
//                                override fun click() {
//                                    ActivityHandler.OpenGooglePlay(activiy)
//                                    //finish();
//                                }
//                            }
//                            val cancel: ConfirmDialog.Click = object : ConfirmDialog.Click() {
//                                override fun click() {
//                                    finish()
//                                }
//                            }
//                            ConfirmDialog(
//                                activiy,
//                                getString(R.string.updateMessage),
//                                R.string.ok,
//                                R.string.cancel_label,
//                                click,
//                                cancel,
//                                false
//                            )
//                        }
//                        Log.i(
//                            ContentValues.TAG,
//                            "Log getValidation" + result.message
//                        )
//                    }
//                }
//            }.getValidate(
//                Constants.deviceType,
//                UtilityApp.getAppVersionStr(),
//                BuildConfig.VERSION_CODE
//            )
//        }
//
//    @SuppressLint("UnsafeExperimentalUsageError")
//    fun putBadge(cartCount: Int) {
//        if (cartCount == 0) {
//            binding.cartCountTv.visibility = View.GONE
//        } else {
//            binding.cartCountTv.visibility = View.VISIBLE
//            binding.cartCountTv.text = cartCount.toString()
//        }
//    }
//}
