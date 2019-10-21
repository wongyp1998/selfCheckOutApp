package tarc.edu.selfcheckoutapp.ui.Home;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import tarc.edu.selfcheckoutapp.R;
import tarc.edu.selfcheckoutapp.CartActivity;
import tarc.edu.selfcheckoutapp.LiveBarcodeScanningActivity;
import tarc.edu.selfcheckoutapp.MainActivity;
import tarc.edu.selfcheckoutapp.Product;
import tarc.edu.selfcheckoutapp.PromoViewHolder;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    SpaceNavigationView navigationView;
    private RecyclerView recyclerView;
    private static String LIST_STATE_KEY = "list_state";
    private Parcelable mListState;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbRef = db.collection("products");
    FirestoreRecyclerAdapter<Product, PromoViewHolder> adapter;
    float new_price = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        recyclerView = root.findViewById(R.id.promo_scroll_recyleview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);



//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
//            @Override
//            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
//                // force height of viewHolder here, this will override layout_height from xml
//                lp.height = getHeight() / 3;
//                return true;
//            }
//        });




        navigationView = root.findViewById(R.id.space);

        navigationView.initWithSaveInstanceState(savedInstanceState);
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_home_black_24dp));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_list_24px));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_person_24px));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_shopping_cart_black_24dp));


        navigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent i = new Intent(getActivity(), LiveBarcodeScanningActivity.class);
                startActivity(i);
                navigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex){
                    case 0:
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);
                        break;
                    case 1:
                        Toast.makeText(getActivity(), itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getActivity(), itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Intent j = new Intent(getActivity(), CartActivity.class);
                        startActivity(j);


                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                switch (itemIndex){
                    case 0:
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);
                        break;
                    case 1:
                        Toast.makeText(getActivity(), itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getActivity(), itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Intent j = new Intent(getActivity(), CartActivity.class);
                        startActivity(j);


                }
            }
        });


        return root;
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save list state
        mListState = layoutManager.onSaveInstanceState();
        state.putParcelable(LIST_STATE_KEY, mListState);
    }

//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//
//        if(savedInstanceState != null)
//            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null)
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
    }



        @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Query query = dbRef.whereGreaterThan("discount",0);
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query,Product.class)
                .build();



        adapter = new FirestoreRecyclerAdapter<Product, PromoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PromoViewHolder holder, int position, @NonNull Product model) {


                Picasso.get().load(model.getImage()).into(holder.circleImageView);
                holder.txtPromoDesc.setText(model.getProdName());
                holder.txtOldPrice.setText("RM" + String.valueOf(model.getPrice()));
                holder.txtOldPrice.setPaintFlags(holder.txtOldPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

                new_price = model.getPrice() * (1-model.getDiscount());

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