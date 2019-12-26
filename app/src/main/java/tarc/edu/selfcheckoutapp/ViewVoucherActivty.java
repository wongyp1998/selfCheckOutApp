package tarc.edu.selfcheckoutapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import tarc.edu.selfcheckoutapp.UtlityClass.ViewPagerAdapter;
import tarc.edu.selfcheckoutapp.ui.Home.AllProductFragment;
import tarc.edu.selfcheckoutapp.ui.Home.ExpiredVoucherFragment;
import tarc.edu.selfcheckoutapp.ui.Home.PromoProductFragment;
import tarc.edu.selfcheckoutapp.ui.Home.VoucherFragment;

public class ViewVoucherActivty extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_voucher_activty);

        Toolbar toolbar = (Toolbar) findViewById(R.id.voucher_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Voucher");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tabLayout = findViewById(R.id.voucher_tab);
        viewPager = findViewById(R.id.voucher_viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new VoucherFragment(), "Ongoing");
        adapter.AddFragment(new ExpiredVoucherFragment(), "Past");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
