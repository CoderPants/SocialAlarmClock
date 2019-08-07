package com.donteco.alarmClock;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.donteco.alarmClock.components.PageFragment;
import com.donteco.alarmClock.help.ActivityHelper;
import com.donteco.alarmClock.help.AlarmClocksStorage;
import com.donteco.alarmClock.help.ConstantsForApp;

public class MainActivity extends FragmentActivity {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityHelper activityHelper = new ActivityHelper(this);
        activityHelper.getRidOfTopBar();

        viewPager = findViewById(R.id.vp_for_app);
        pagerAdapter = new PagerAdapterImpl(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);


        AlarmClocksStorage alarmClocksStorage = AlarmClocksStorage.getInstance(getApplicationContext());

        //Get into it!
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(ConstantsForApp.LOG_TAG, "onPageSelected, position " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Opacity for animation
        viewPager.setPageTransformer(false, (v, pos) -> {
            final float opacity = Math.abs(Math.abs(pos) - 1);
            v.setAlpha(opacity);
        });

    }

    //Pause thread before quiting
    @Override
    protected void onStop() {
        super.onStop();
    }

    //Cos we get only 3 fragments
    private class PagerAdapterImpl extends FragmentPagerAdapter
    {
        public PagerAdapterImpl(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return ConstantsForApp.PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.getInstance(position);
        }

        //Need to change to
        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
            //Cheat!!!!!!!!!!!!!!!
            SpannableStringBuilder ssb = new SpannableStringBuilder("    ");
            Drawable drawable;

            int iconId = Integer.MAX_VALUE;

            switch (position)
            {
                case 0:
                    iconId = R.drawable.clock_icon;
                    break;
                case 1:
                    iconId = R.drawable.alarm_clock_icon;
                    break;
                case 2:
                    iconId = R.drawable.social_network_icon;
                    break;
            }

            if(iconId == Integer.MAX_VALUE)
            {
                Log.wtf(ConstantsForApp.LOG_TAG, "In main Activity in getPageTitle method error - didn't find correct id");
                return "Title " + position;
            }

            drawable = ResourcesCompat.getDrawable(MainActivity.this.getResources(),
                    iconId, null);

            //Get into it!!!!
            drawable.setBounds(0, 0, ConstantsForApp.IMAGE_SIZE_IN_TABS, ConstantsForApp.IMAGE_SIZE_IN_TABS);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            ssb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return ssb;
        }
    }
}
