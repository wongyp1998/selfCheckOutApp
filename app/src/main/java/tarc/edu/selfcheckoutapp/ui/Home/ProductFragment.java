package tarc.edu.selfcheckoutapp.ui.Home;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import tarc.edu.selfcheckoutapp.R;
import tarc.edu.selfcheckoutapp.UtlityClass.LoginPreferenceUtils;
import tarc.edu.selfcheckoutapp.UtlityClass.ViewPagerAdapter;


public class ProductFragment extends Fragment {

    private SearchView searchView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int currentTab;
    private int tab_no=0;

    public ProductFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_product, container, false);

        Bundle bundle = this.getArguments();

        tabLayout = root.findViewById(R.id.category_tab);
        viewPager = root.findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.AddFragment(new AllProductFragment(), "All");
        adapter.AddFragment(new PromoProductFragment(), "Sale");


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        searchView = root.findViewById(R.id.search_view);
        ImageView searchbtn = (ImageView) searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchbtn.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

//        if(bundle!=null)
//        {
//
//            tab_no = bundle.getInt("tab_no");
////            currentTab = tab_no;
//            TabLayout.Tab tab = tabLayout.getTabAt(tab_no);
//            tab.select();
//        }

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_basket);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                currentTab = position;
                if(position==0)
                {
                    tabLayout.getTabAt(position).setIcon(R.drawable.ic_basket);


                }else if(position ==1){
                    tabLayout.getTabAt(position).setIcon(R.drawable.ic_tag);



                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                searchView.setQuery("",false);
                int position = tab.getPosition();
                currentTab = position;
                if(position==0)
                {

                    searchView.setQuery("",false);
                    searchView.setIconified(true);


                }else if(position ==1){

                    searchView.setQuery("",false);
                    searchView.setIconified(true);


                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                        if (currentTab == 0) {
                            FragmentManager fm = getChildFragmentManager();
                            AllProductFragment frag = (AllProductFragment) fm.getFragments().get(currentTab);
                            frag.searchProduct(s);
                        } else if (currentTab == 1) {
                            FragmentManager fm = getChildFragmentManager();
                            PromoProductFragment frag = (PromoProductFragment) fm.getFragments().get(currentTab);
                            frag.searchProduct(s);
                        }


                    return false;
                }
            });
        }


        return root;
    }

}
