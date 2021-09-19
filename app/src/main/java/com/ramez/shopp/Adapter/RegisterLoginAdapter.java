package com.ramez.shopp.Adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ramez.shopp.Fragments.LoginFragment;
import com.ramez.shopp.Fragments.RegisterFragment;
import com.ramez.shopp.R;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class RegisterLoginAdapter extends FragmentStatePagerAdapter {

    private SparseArray<Fragment> fragments;
    private Fragment mCurrentFragment;


    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.create_account, R.string.text_login_login};
    private final Context mContext;

    public RegisterLoginAdapter(Context context, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        fragments = new SparseArray<>();
    }


    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if (position == 0) {
            fragment = new RegisterFragment();

        }
        if (position == 1)
            fragment = new LoginFragment();

        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    public Fragment getCurrentFragment(int index) {
        if (fragments.size() > 0)
            return fragments.get(index);
        else
            return null;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        fragments.remove(position);
        super.destroyItem(container, position, object);
    }
}

