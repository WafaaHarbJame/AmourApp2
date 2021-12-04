package com.ramez.shopp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ramez.shopp.R
import com.ramez.shopp.Utils.LocaleUtils


open class ActivityBase : AppCompatActivity() {
    private var isMainActivity = false
    protected var gone = View.GONE
    protected var visible = View.VISIBLE
    private var mainLogo: ImageView? = null
    private var mainTitle: TextView? = null
    private var home: ImageView? = null
    protected var toolbar: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window


// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
// finally change the color
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_color)
//        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
    }

    override fun setTitle(title: CharSequence) {
        toolbar = findViewById(R.id.tool_bar)
        home = toolbar?.findViewById(R.id.backBtn)
        mainLogo = toolbar?.findViewById(R.id.logoImg)
        mainTitle = toolbar?.findViewById(R.id.mainTitleTv)
        mainTitle?.text = title
        if (!isMainActivity) {
            home?.visibility = View.VISIBLE
        } else {
            home?.visibility = View.GONE
        }
        home?.setOnClickListener(View.OnClickListener { view: View? -> onBackPressed() })
        super.setTitle(title)
    }

    protected val activity: Activity
        get() = this

    @SuppressLint("ClickableViewAccessibility")
    fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { _, event ->
                //                    System.out.println("Log event Action " + event.getAction());
                if (event.action != MotionEvent.ACTION_SCROLL) {
                    hideSoftKeyboard(activity)
                }
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    fun Toast(msg: String?) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    fun Toast(resId: Int) {
        Toast.makeText(activity, getString(resId), Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun hideSoftKeyboard(activity: Activity) {
            try {
                val inputMethodManager = activity.getSystemService(
                    INPUT_METHOD_SERVICE
                ) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken, 0
                )
            } catch (e: Exception) {
//            e.printStackTrace();
            }
        }

        fun showSoftKeyboard(activity: Activity) {
            try {
                val inputMethodManager = activity.getSystemService(
                    INPUT_METHOD_SERVICE
                ) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken, 0
                )
                //            inputMethodManager.showSoftInput(_searchText, InputMethodManager.SHOW_FORCED);
            } catch (e: Exception) {
//            e.printStackTrace();
            }
        }
    }

    init {
        LocaleUtils.updateConfig(this)
    }
}
