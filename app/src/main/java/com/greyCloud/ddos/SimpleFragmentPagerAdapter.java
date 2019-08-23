package com.greyCloud.ddos;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    //选项卡元素
    private static final String[] mTitles = {"欢迎", "DDoS"};
    public static Fragment wel=new fragment_welcome();
    public static Fragment ddos=new PageFragment();
    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        //return PageFragment.newInstance(position + 1);
        if(position==0){
            return wel;
        }else{
            return ddos;
        }
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
