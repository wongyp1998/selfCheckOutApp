package tarc.edu.selfcheckoutapp.ui.Home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tarc.edu.selfcheckoutapp.LiveBarcodeScanningActivity;
import tarc.edu.selfcheckoutapp.eWalletActivity;

import tarc.edu.selfcheckoutapp.Model.SliderModel;
import tarc.edu.selfcheckoutapp.R;
import tarc.edu.selfcheckoutapp.Model.Product;
import tarc.edu.selfcheckoutapp.UtlityClass.SliderAdapter;
import tarc.edu.selfcheckoutapp.ViewHolder.PromoViewHolder;

import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference();
    FirebaseRecyclerAdapter<Product, PromoViewHolder> adapter;
    Double new_price = 0.0;
    private TextView welcomeTxt;
    private Button more_btn, scanNow_btn;
    private ImageView wallet_btn;

    private ViewPager bannerSliderViewPager;
    private List<SliderModel> sliderModelList;
    private int currentPage = 0;
    private Timer timer;
    final private long DELAY_TIME = 250;
    final private long PERIOD_TIME = 2000;

    private LinearLayout dotsLayout;
    private int custom_position = 0;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        dotsLayout = root.findViewById(R.id.dots_container);
        scanNow_btn = root.findViewById(R.id.home_scan_now);
//        welcomeTxt = root.findViewById(R.id.user_welcome);
        more_btn = root.findViewById(R.id.see_all_btn);
        wallet_btn = root.findViewById(R.id.toolbar_wallet_btn);


//        cartListRef.child("User").child(LoginPreferenceUtils.getPhone(getActivity())).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                welcomeTxt.setText("Hi, " + dataSnapshot.child("user_name").getValue(String.class));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        recyclerView = root.findViewById(R.id.promo_scroll_recycleview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                ProductFragment frag = new ProductFragment();
                frag.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(),frag,"thisFrag")
                        .addToBackStack(null)
                        .commit();
            }
        });

        scanNow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), LiveBarcodeScanningActivity.class);
                startActivity(i);
            }
        });

        wallet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), eWalletActivity.class);
                startActivity(i);

            }
        });

        prepareDots(custom_position++);
        bannerSliderViewPager = root.findViewById(R.id.banner_view_pager);
        sliderModelList = new ArrayList<SliderModel>();
        SliderAdapter sliderAdapter = new SliderAdapter(sliderModelList);
        bannerSliderViewPager.setAdapter(sliderAdapter);


        cartListRef.child("Banners").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    SliderModel sliderModel = snapshot.getValue(SliderModel.class);
                    String bannerRef = sliderModel.getBanner();
                    String color = sliderModel.getBackgroundColor();
                    sliderModelList.add(new SliderModel(bannerRef,color));
                }
                sliderAdapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bannerSliderViewPager.setClipToPadding(false);
        bannerSliderViewPager.setPageMargin(20);

        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                custom_position = position;
                if(custom_position>sliderModelList.size())
                    custom_position = 0;
                prepareDots(custom_position++);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE)
                {
                    pageLooper();
                }

            }
        };
        bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);

        startBannerSlideshow();

        bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                pageLooper();
                stopBannerSlideShow();
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    startBannerSlideshow();
                }
                return false;
            }
        });





        return root;
    }

    private void pageLooper()
    {
        if (currentPage == sliderModelList.size())
        {
            currentPage = 0;
            bannerSliderViewPager.setCurrentItem(currentPage,false);
        }


    }

    private void startBannerSlideshow()
    {
        Handler handler = new Handler();
        Runnable update = new Runnable() {
            @Override
            public void run() {
                if(currentPage >= sliderModelList.size())
                {
                    currentPage = 0;
                }
                bannerSliderViewPager.setCurrentItem(currentPage++,true);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        },DELAY_TIME,PERIOD_TIME);
    }

    private void stopBannerSlideShow()
    {
        timer.cancel();
    }

    private void prepareDots(int currentSlidePosition)
    {
        if (dotsLayout.getChildCount()>0)
            dotsLayout.removeAllViews();

        ImageView dots[] = new ImageView[5];

        for(int i =0; i <5;i++)
        {
            if(getActivity()!= null) {
                dots[i] = new ImageView(getActivity());
                if (i == currentSlidePosition)
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot));
                else
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.inactive_dot));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(4, 0, 4, 0);
                dotsLayout.addView(dots[i], layoutParams);
            }
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        displayPromoView();
    }


    public void displayPromoView(){
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(cartListRef.child("Products").orderByChild("Discount").startAt(0.01),Product.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<Product, PromoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PromoViewHolder holder, int position, @NonNull Product model) {


                Picasso.get().load(model.getImage()).fit().centerCrop().into(holder.circleImageView);
                holder.txtPromoDesc.setText(model.getName());
                holder.txtOldPrice.setText("RM" + String.valueOf(model.getPrice()));
                holder.txtOldPrice.setPaintFlags(holder.txtOldPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                new_price = model.getPrice() * (1-model.getDiscount());
                Double dscValue = model.getDiscount()*100;
                holder.txtPromoTag.setText("-"+String.format("%.0f",dscValue)+"%");

                holder.txtNewPrice.setText("RM"+ String.format("%.2f",new_price));



            }

            @NonNull
            @Override
            public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promotion_scroll_item_layout,parent,false);
                PromoViewHolder holder = new PromoViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}