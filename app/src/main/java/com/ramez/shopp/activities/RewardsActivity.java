//package com.ramez.shopp.Activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentStatePagerAdapter;
//
//import com.ramez.shopp.Classes.MessageEvent;
//import com.ramez.shopp.Fragments.CardFragment;
//import com.ramez.shopp.Fragments.CouponsFragment;
//import com.ramez.shopp.MainActivity;
//import com.ramez.shopp.R;
//import com.ramez.shopp.databinding.ActivityRewardsBinding;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//import org.jetbrains.annotations.NotNull;
//
//public class RewardsActivity extends ActivityBase {
//    ActivityRewardsBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityRewardsBinding.inflate(getLayoutInflater());
//        View view = binding.getRoot();
//        setContentView(view);
//
//        setTitle("");
//
//        binding.viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
//        binding.tabs.setupWithViewPager(binding.viewPager);
//        binding.tabs.setTabTextColors(ContextCompat.getColor(getActiviy(), R.color.black),
//                ContextCompat.getColor(getActiviy(), R.color.black));
//
//
//    }
//
//    class MyViewPagerAdapter extends FragmentStatePagerAdapter {
//
//        public MyViewPagerAdapter(FragmentManager fm) {
//            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//        }
//
//        @NotNull
//        @Override
//        public Fragment getItem(int position) {
//
//            switch (position) {
//
//                case 0:
//                    return new CardFragment();
//                case 1:
//                    return new CouponsFragment();
//
//                default:
//                    return new CardFragment();
//            }
//        }
//
//        @Override
//        public int getCount() {
//            return 2;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            if (position == 0) return getString(R.string.Card);
//            else if (position == 1) return getString(R.string.Coupons);
//            else return "";
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(@NotNull MessageEvent event) {
//
//        if (event.type.equals(MessageEvent.TYPE_main)) {
//
//            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_POSITION, 0));
//            Intent intent = new Intent(getActiviy(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//
//        }
//
//
//    }
//
//
//}