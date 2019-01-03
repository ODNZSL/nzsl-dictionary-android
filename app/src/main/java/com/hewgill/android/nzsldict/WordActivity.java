package com.hewgill.android.nzsldict;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class WordActivity extends BaseActivity {

    private TextView gloss;
    private TextView minor;
    private TextView maori;
    private FloatingActionButton vocabSheetToggle;
    private SignVideoFragment mVideoFragment;
    private ViewPager viewPager;
    private DictItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word);
        setupAppToolbar();
        Intent intent = getIntent();
        item = (DictItem) intent.getSerializableExtra("item");

        gloss = (TextView) findViewById(R.id.gloss);
        minor = (TextView) findViewById(R.id.minor);
        maori = (TextView) findViewById(R.id.maori);
        vocabSheetToggle = (FloatingActionButton) findViewById(R.id.add_to_vocab_sheet);
        setupVocabSheetToggle(vocabSheetToggle);
        viewPager = (ViewPager) findViewById(R.id.sign_media_pager);
        setupSignMediaPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sign_tabs);
        tabLayout.setupWithViewPager(viewPager);

        gloss.setText(item.gloss);
        minor.setText(item.minor);
        maori.setText(item.maori);
    }

    private void setupVocabSheetToggle(final FloatingActionButton vocabSheetToggle) {
        final VocabSheetRepository repo = new VocabSheetRepository(this);

        if (repo.contains(item)) {
            vocabSheetToggle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_white_48dp));
        } else {
            vocabSheetToggle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_border_white_48dp));
        }

        vocabSheetToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = null;

                if (repo.contains(item)) {
                    repo.remove(item);
                    msg = "Removed from your vocab sheet";
                } else {
                    repo.add(item);
                    msg = "Added to your vocab sheet";
                }

                Snackbar
                        .make(view, msg, Snackbar.LENGTH_LONG)
                        .setAction("View", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent vocabSheetActivity = new Intent(WordActivity.this, VocabSheetActivity.class);
                                startActivity(vocabSheetActivity);
                            }
                        }).show();
                setupVocabSheetToggle(vocabSheetToggle);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void setupSignMediaPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SignIllustrationFragment.newInstance(item), "Illustration");
        mVideoFragment = SignVideoFragment.newInstance(item);
        adapter.addFragment(mVideoFragment, "Video");
        viewPager.addOnPageChangeListener(new WordPageChangeListener(viewPager));
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

    private class WordPageChangeListener implements ViewPager.OnPageChangeListener {
        private final ViewPager mPager;

        private WordPageChangeListener(ViewPager pager) {
            mPager = pager;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            ViewPagerAdapter vpa = (ViewPagerAdapter) mPager.getAdapter();
            if (vpa.getPageTitle(position).equals("Video")) {
                mVideoFragment.showControls();
            } else {
                mVideoFragment.stop();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
