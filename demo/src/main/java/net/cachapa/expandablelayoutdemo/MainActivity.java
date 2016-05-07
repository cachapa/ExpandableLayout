package net.cachapa.expandablelayoutdemo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new simpleAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private class simpleAdapter extends FragmentPagerAdapter{
        public simpleAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            return position == 0 ? new SimpleFragment()
                    : position == 1 ? new AccordionFragment()
                    : new RecyclerViewFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "Simple" : position == 1 ? "Accordion" : "RecyclerView";
        }
    }
}
