package com.hewgill.android.nzsldict;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WordActivity extends BaseActivity {

    private TextView gloss;
    private TextView minor;
    private TextView maori;
    private ViewPager viewPager;
    private Dictionary.DictItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word);
        setupAppToolbar();
        Intent intent = getIntent();
        item = (Dictionary.DictItem) intent.getSerializableExtra("item");

        gloss = (TextView) findViewById(R.id.gloss);
        minor = (TextView) findViewById(R.id.minor);
        maori = (TextView) findViewById(R.id.maori);
        viewPager = (ViewPager) findViewById(R.id.sign_media_pager);
        setupSignMediaPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sign_tabs);
        tabLayout.setupWithViewPager(viewPager);

        gloss.setText(item.gloss);
        minor.setText(item.minor);
        maori.setText(item.maori);
    }

    private void setupSignMediaPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SignIllustrationFragment.newInstance(item), "Illustration");
        adapter.addFragment(SignVideoFragment.newInstance(item), "Video");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
