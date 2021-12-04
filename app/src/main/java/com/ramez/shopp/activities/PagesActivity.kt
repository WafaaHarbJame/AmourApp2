package com.ramez.shopp.activities

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.jaeger.library.StatusBarUtil
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.R
import com.ramez.shopp.databinding.ActivityPagesBinding
import com.ramez.shopp.fragments.FastqHistoryFragment
import com.ramez.shopp.fragments.FastqSettingsFragment

class PagesActivity : ActivityBase() {

    var fragmentManager: FragmentManager? = null
    var ft: FragmentTransaction? = null
    var newFragment: Fragment? = null

    //    var container: FrameLayout? = null
    var bundle: Bundle? = null
    var type: String? = null

    lateinit var binding: ActivityPagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeToolBarColor()
        StatusBarUtil.setColor(this, ContextCompat.getColor(activity, R.color.fastq_color), 0)

        bundle = intent.extras
        if (bundle != null) {
            type = bundle?.getString(Constants.KEY_FRAGMENT_TYPE)
        }


        getFragmentType(type)

        if (newFragment != null) {
//            newFragment?.retainInstance = true
            fragmentManager = supportFragmentManager
            ft = fragmentManager?.beginTransaction()
            ft?.replace(R.id.container, newFragment!!)?.commit()
        }

    }

    private fun getFragmentType(type: String?) {


        when (type) {

            Constants.FRAG_FASTQ_SETTINGS -> {
                newFragment = FastqSettingsFragment()
                title = getString(R.string.setting)
            }
            Constants.FRAG_FASTQ_HISTORY -> {
                newFragment = FastqHistoryFragment()
                title = getString(R.string.History)
            }
            else -> {
            }
        }
    }

    private fun changeToolBarColor() {
        binding.toolBar.toolbarBack.setBackgroundColor(ContextCompat.getColor(activity, R.color.fastq_color))
        binding.toolBar.logoImg.visibility = gone
        binding.toolBar.mainTitleTv.visibility = visible

    }


}
