package com.ramez.shopp.fragments


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.single.PermissionListener
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.*
import com.ramez.shopp.Dialogs.CheckLoginDialog
import com.ramez.shopp.Dialogs.ConfirmDialog
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.ActivityHandler
import com.ramez.shopp.activities.*
import com.ramez.shopp.databinding.FragmentMyAccountBinding
import es.dmoral.toasty.Toasty


class MyAccountFragment : FragmentBase() {
    var isLogin = false
    var memberModel: MemberModel? = null
    var user_id = 0
    lateinit var binding: FragmentMyAccountBinding
    private val checkLoginDialog: CheckLoginDialog? = null
    private var soicalLink: SoicalLink? = null
    private var whatsLink: String? = ""
    private var facebookLink: String? = ""
    private var instagramLinks: String? = ""
    private var twitterLinks: String? = ""
    private val searchCode = 2000
    var localModel: LocalModel? = null
    private var code: String? = ""
    private var countryId = 0
    private var cityId = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyAccountBinding.inflate(inflater, container, false)
        val view: View = binding.root
        isLogin = UtilityApp.isLogin()
        binding.SupportBtn.visibility = View.GONE
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )
        countryId = localModel?.countryId ?: Constants.default_country_id
        cityId =
            if (localModel != null && localModel!!.cityId != null)
                localModel?.cityId!!.toInt() else UtilityApp.getDefaultLocalData(
                activityy
            ).cityId.toInt()
        if (UtilityApp.getLinks() != null) {
            soicalLink = UtilityApp.getLinks()
            twitterLinks = soicalLink?.twitterLink
            facebookLink = soicalLink?.facebookLink
            instagramLinks = soicalLink?.instagramLink
            whatsLink = soicalLink?.whatsappLink
        } else {
            if (localModel?.shortname != null) {
                getLinks(cityId)
            }
        }


        if (UtilityApp.isLogin()) {
            binding.viewLogin.visibility = View.GONE
            binding.logoutText.setText(R.string.logout)
            binding.editProfileBu.visibility = View.VISIBLE
            binding.fastqLyBut.visibility= View.VISIBLE
            if (UtilityApp.getUserData() != null) {
                memberModel = UtilityApp.getUserData()
                if (memberModel != null) {
                    initData(memberModel)
                }
            } else {
                if (memberModel != null && memberModel!!.id != null) {
                    getUserData(memberModel!!.id, memberModel!!.storeId)
                }
            }
        } else {
            binding.logoutText.setText(R.string.text_login_login)
            binding.viewLogin.visibility = View.VISIBLE
            binding.editProfileBu.visibility = View.GONE
            binding.addressBtn.visibility = View.GONE
            binding.changePassBtn.visibility = View.GONE
        }

        initListeners()
        return view
    }

    private fun initData(memberModel: MemberModel?) {
        if (memberModel != null && memberModel.id != null) {
            user_id = memberModel.id
            binding.usernameTV.text = memberModel.name
            binding.emailTv.text = memberModel.email
            Glide.with(activityy).asBitmap().load(memberModel.profilePicture).placeholder(R.drawable.avatar)
                .into(
                    binding.userImg
                )
        }
    }

    private fun showDialog(message: Int) {
        val checkLoginDialog = CheckLoginDialog(
            activityy,
            R.string.LoginFirst,
            message,
            R.string.ok,
            R.string.cancel,
            null,
            null
        )
        checkLoginDialog.show()
        checkLoginDialog.show()
    }

    private fun startLogin() {
        val intent = Intent(activityy, RegisterLoginActivity::class.java)
        intent.putExtra(Constants.LOGIN, true)
        startActivity(intent)
    }

    private fun startSupport() {
        val intent = Intent(activityy, ContactSupportActivity::class.java)
        startActivity(intent)
    }

    private fun startChangeLang() {
        val intent = Intent(activityy, ChangeLangCurrencyActivity::class.java)
        startActivity(intent)
    }

    private fun startChangeBranch() {
        val intent = Intent(activityy, ChangeCityBranchActivity::class.java)
        startActivity(intent)
    }

    private fun startAddressActivity() {
        val intent = Intent(activityy, AddressActivity::class.java)
        startActivity(intent)
    }

    private fun startOrderActivity() {
        val intent = Intent(activityy, MyOrderActivity::class.java)
        startActivity(intent)
    }

    private fun startRewardsActivity() {
        val intent = Intent(activityy, RewardsActivity::class.java)
        startActivity(intent)
    }

    private fun startEditProfileActivity() {
        val intent = Intent(activityy, EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun startTermsActivity() {
        val intent = Intent(activityy, TermsActivity::class.java)
        startActivity(intent)
    }

    private fun startConditionActivity() {
        val intent = Intent(activityy, ConditionActivity::class.java)
        startActivity(intent)
    }

    private fun startAboutActivity() {
        val intent = Intent(activityy, AboutActivity::class.java)
        startActivity(intent)
    }

    private fun startRateAppActivity() {
        val intent = Intent(activityy, RatingActivity::class.java)
        startActivity(intent)
    }

    private fun startChangeActivity() {
        val intent = Intent(activityy, ChangePassActivity::class.java)
        startActivity(intent)
    }

    private fun startFavProductActivity() {
        val intent = Intent(activityy, FavoriteActivity::class.java)
        startActivity(intent)
    }

    fun signOut(memberModel: MemberModel?) {
        val click: ConfirmDialog.Click = object : ConfirmDialog.Click() {
            override fun click() {
                DataFeacher(false,
                    object : DataFetcherCallBack {
                        override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                            if (isVisible) {
                                if (func == Constants.ERROR) {
                                    Toast(R.string.fail_to_sign_out)
                                } else if (func == Constants.FAIL) {
                                    Toast(R.string.fail_to_sign_out)
                                } else if (func == Constants.NO_CONNECTION) {
                                    GlobalData.Toast(activityy, R.string.no_internet_connection)
                                } else {
                                    if (IsSuccess) {
                                        UtilityApp.logOut()
                                        UtilityApp.setCartCount(0)
                                        GlobalData.Position = 0
                                        val intent =
                                            Intent(activityy, SplashScreenActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    } else {
                                        Toast(R.string.fail_to_sign_out)
                                    }
                                }
                            }
                        }

                    }).logOut(memberModel)
            }
        }
        ConfirmDialog(
            activityy,
            getString(R.string.want_to_signout),
            R.string.ok,
            R.string.cancel_label,
            click,
            null,
            false
        )
    }

    fun getUserData(user_id: Int, store_id: Int) {
        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        val result =
                            obj as ResultAPIModel<ProfileData>
                        val message = getString(R.string.fail_to_get_data)
                        if (IsSuccess) {
                            val memberModel = UtilityApp.getUserData()
                            memberModel.name = result.data.name
                            memberModel.email = result.data.email
                            memberModel.id = result.data.id
                            memberModel.loyalBarcode = result.data.loyalBarcode
                            memberModel.profilePicture = result.data.profilePicture
                            initData(memberModel)
                            UtilityApp.setUserData(memberModel)
                        }
                    }
                }
            }).getUserDetails(user_id, store_id)
    }

    override fun onResume() {
        if (UtilityApp.isLogin()) {
            memberModel = UtilityApp.getUserData()
            initData(memberModel)
        }
        super.onResume()
    }

    private fun getLinks(store_id: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as ResultAPIModel<SoicalLink?>
                if (isVisible) {
                    if (IsSuccess) {
                        if (result.data != null) {
                            soicalLink = result.data
                            UtilityApp.SetLinks(soicalLink)
                            if (soicalLink!!.twitterLink != null) {
                                twitterLinks = soicalLink!!.twitterLink
                            }
                            if (soicalLink!!.facebookLink != null) {
                                facebookLink = soicalLink!!.facebookLink
                            }
                            if (soicalLink!!.instagramLink != null) {
                                instagramLinks = soicalLink!!.instagramLink
                            }
                            if (soicalLink!!.whatsappLink != null) {
                                whatsLink = soicalLink!!.whatsappLink
                            }
                        }
                    }
                }
            }

        }).getLinks(store_id)
    }

    private fun checkCameraPermission() {
        Dexter.withContext(activityy).withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                @RequiresApi(api = Build.VERSION_CODES.M)
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    startScan()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        activityy,
                        "" + activityy.getString(R.string.permission_camera_rationale),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener { error: DexterError? ->
                Toast.makeText(
                    activityy,
                    "" + activityy.getString(R.string.error_in_data),
                    Toast.LENGTH_SHORT
                ).show()
            }.onSameThread().check()
    }

    private fun startScan() {
        if (ContextCompat.checkSelfPermission(
                    activityy,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activityy,
                arrayOf(Manifest.permission.CAMERA),
                ZBAR_CAMERA_PERMISSION
            )
        } else {
            val intent = Intent(activityy, FullScannerActivity::class.java)
            startActivityForResult(intent, searchCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == searchCode) {
            if (data != null) {
                code = data.getStringExtra(Constants.CODE)
                val intent = Intent(activityy, PriceCheckerResultActivity::class.java)
                val productModel = ProductModel()
                intent.putExtra(Constants.DB_productModel, productModel)
                startActivity(intent)
            }
        }
    }

    private fun CheckLoyal() {
        val countryDetailsModel = DBFunction.getLoyal()
        if (countryDetailsModel == null) {
            if (localModel != null && localModel!!.shortname != null) getCountryDetail(localModel!!.shortname)
        } else {
            val hasLoyal = countryDetailsModel.hasLoyal
            if (hasLoyal) {
                startRewardsActivity()
            } else Toasty.warning(activityy, getString(R.string.no_active), Toast.LENGTH_SHORT, true).show()
        }
    }

    private fun getCountryDetail(shortName: String) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as ResultAPIModel<CountryDetailsModel?>?
                if (result != null && result.isSuccessful) {
                    if (result.data != null) {
                        val countryDetailsModel = result.data
                        Log.i(
                            javaClass.simpleName,
                            "Log  getCountryDetail call hasLoyal " + countryDetailsModel!!.hasLoyal
                        )
                        DBFunction.setLoyal(countryDetailsModel)
                    }
                }
            }

        }).getCountryDetail(shortName)
    }

    private fun goToFacebook() {
        try {
            val facebookIntent = Intent(Intent.ACTION_VIEW)
            facebookIntent.data = Uri.parse(facebookLink)
            startActivity(facebookIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openFacebookPage(pageId: String, context: Context) {
        val pageUrl = "https://www.facebook.com/$pageId"
        try {
            val applicationInfo = context.packageManager.getApplicationInfo("com.facebook.katana", 0)
            if (applicationInfo.enabled) {
                val versionCode = context.packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
                val url: String
                url = if (versionCode >= 3002850) {
                    "fb://facewebmodal/f?href=$pageUrl"
                } else {
                    "fb://page/$pageId"
                }
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } else {
                throw Exception("Facebook is disabled")
            }
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pageUrl)))
        }
    }

    private fun goToFacebookNew() {
        try {
            val facebookUrl = facebookPageURL
            val facebookIntent = Intent(Intent.ACTION_VIEW)
            facebookIntent.data = Uri.parse(facebookUrl)
            startActivity(facebookIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val facebookPageURL: String?
        get() {
            val FACEBOOK_URL = facebookLink
            var facebookurl: String? = null
            try {
                val packageManager = requireActivity().packageManager
                if (packageManager != null) {
                    val activated = packageManager.getLaunchIntentForPackage("com.facebook.katana")
                    if (activated != null) {
                        val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
                        if (versionCode >= 3002850) {
                            facebookurl = "fb://page/XXXXXXpage_id"
                        }
                    } else {
                        facebookurl = FACEBOOK_URL
                    }
                } else {
                    facebookurl = FACEBOOK_URL
                }
            } catch (e: Exception) {
                facebookurl = FACEBOOK_URL
            }
            return facebookurl
        }

    companion object {
        private const val ZBAR_CAMERA_PERMISSION = 1
    }

    fun initListeners(): Unit {
        binding.facebookBut.setOnClickListener {
            println("Log facebook_link$facebookLink")
            try {
                if (facebookLink != null) {
//                    boolean installed = ActivityHandler.isPackageExist(getActivityy(), "com.facebook.katana");
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebook_link));
//
//                    if (installed) {
//                        System.out.println("App is already installed on your phone");
//                        intent.setPackage("com.whatsapp");
//                        intent.setData(Uri.parse(facebook_link));
//
//
//                    }
//                    intent.setData(Uri.parse(facebook_link));
//                    startActivity(intent);
                    goToFacebook()
                }
            } catch (e: Exception) {
                // This will catch any exception, because they are all descended from Exception
                println("Error " + e.message)
            }
        }

        binding.whatsBut.setOnClickListener {
            println("Log whats_link$whatsLink")
            Log.i("TAG", "Log whats_link $whatsLink")
            try {
                if (whatsLink != null) {
                    val installed =
                        ActivityHandler.isPackageExist(activityy, "com.whatsapp")
                    val installedBusiness =
                        ActivityHandler.isPackageExist(activityy, "com.whatsapp.w4b")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsLink))
                    if (installed) {
                        println("App is already installed on your phone")
                        intent.setPackage("com.whatsapp")
                        startActivity(intent)
                    } else if (installedBusiness) {
                        println("App is already installed on your phone")
                        intent.setPackage("com.whatsapp.w4b")
                        startActivity(intent)
                    } else {
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                // This will catch any exception, because they are all descended from Exception
                println("Error " + e.message)
            }
        }

        binding.twitterBut.setOnClickListener {
            try {
                if (twitterLinks != null) {
                    val installed =
                        ActivityHandler.isPackageExist(activityy, "com.twitter.android")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(twitterLinks))
                    if (installed) {
                        intent.setPackage("com.twitter.android")
                        println("App is already installed on your phone")
                    }
                    startActivity(intent)
                }
            } catch (e: Exception) {
                // This will catch any exception, because they are all descended from Exception
                println("Error " + e.message)
            }
        }

        binding.instBut.setOnClickListener {
            try {
                if (instagramLinks != null) {
                    val installed =
                        ActivityHandler.isPackageExist(activityy, "com.instagram.android")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramLinks))
                    if (installed) {
                        println("App is already installed on your phone")
                        intent.setPackage("com.instagram.android")
                    }
                    startActivity(intent)
                }
            } catch (e: Exception) {
                // This will catch any exception, because they are all descended from Exception
                println("Error " + e.message)
            }
        }

        binding.termsBtn.setOnClickListener { startTermsActivity() }

        binding.conditionsBtn.setOnClickListener { startConditionActivity() }

        binding.aboutUsBtn.setOnClickListener { startAboutActivity() }

        binding.rateBtn.setOnClickListener {
            if (isLogin) {
                startRateAppActivity()
            } else {
                showDialog(R.string.to_rate_app)
            }
        }

        binding.priceCheckerBtn.setOnClickListener { checkCameraPermission() }

        binding.shareBtn.setOnClickListener {
            val appPackageName = activityy.packageName
            // getPackageName() from Context or Activity object
            ActivityHandler.shareTextUrl(
                activityy,
                getString(R.string.app_share_text1),
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    .toString(),
                "https://apps.apple.com/bh/app/ramez-%D8%B1%D8%A7%D9%85%D8%B2/id1509927576",
                ""
            )
        }

        binding.changePassBtn.setOnClickListener {
            if (isLogin) {
                startChangeActivity()
            } else {
                showDialog(R.string.to_change_pass)
            }
        }

        binding.favProductBut.setOnClickListener { view1 ->
            if (isLogin) {
                startFavProductActivity()
            } else {
                showDialog(R.string.to_show_products)
            }
        }

        binding.myOrderBut.setOnClickListener {
            if (isLogin) {
                startOrderActivity()
            } else {
                showDialog(R.string.to_show_orders)
            }
        }

        binding.ramezRewardBtn.setOnClickListener { CheckLoyal() }

        binding.editProfileBu.setOnClickListener { startEditProfileActivity() }

        binding.addressBtn.setOnClickListener { view1 ->
            if (isLogin) {
                startAddressActivity()
            } else {
                showDialog(R.string.to_show_address)
            }
        }

        binding.changeCityBtn.setOnClickListener { startChangeBranch() }

        binding.changeLangBtn.setOnClickListener { startChangeLang() }

        binding.SupportBtn.setOnClickListener {
            if (isLogin) {
                startSupport()
            } else {
                showDialog(R.string.to_contact_support)
            }
        }

        binding.logoutBtn.setOnClickListener {
            if (UtilityApp.isLogin()) {
                val memberModel = UtilityApp.getUserData()
                if (memberModel != null && memberModel.id != null) {
                    signOut(memberModel)
                }
            } else {
                startLogin()
            }
        }

        binding.fastqLyBut.setOnClickListener {
            val intent = Intent(activityy, FastqActivity::class.java)
            requireActivity().startActivity(intent)

        }


    }
}