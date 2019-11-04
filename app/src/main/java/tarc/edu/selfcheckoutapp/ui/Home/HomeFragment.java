package tarc.edu.selfcheckoutapp.ui.Home;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import tarc.edu.selfcheckoutapp.R;
import tarc.edu.selfcheckoutapp.Product;
import tarc.edu.selfcheckoutapp.PromoViewHolder;
import com.squareup.picasso.Picasso;


public class HomeFragment extends Fragment {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference();
    FirebaseRecyclerAdapter<Product, PromoViewHolder> adapter;
    float new_price = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        recyclerView = root.findViewById(R.id.promo_scroll_recycleview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,true);
        recyclerView.setLayoutManager(layoutManager);

//        displayPromoView();



        return root;
    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        displayPromoView();
//    }
//
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayPromoView();

    }

    @Override
    public void onResume() {
        super.onResume();
        displayPromoView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        displayPromoView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        displayPromoView();
    }

    public void displayPromoView(){
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(cartListRef.child("Products").orderByChild("discount").startAt(0.01),Product.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<Product, PromoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PromoViewHolder holder, int position, @NonNull Product model) {


                Picasso.get().load(model.getImage()).into(holder.circleImageView);
                holder.txtPromoDesc.setText(model.getProdName());
                holder.txtOldPrice.setText("RM" + String.valueOf(model.getPrice()));
                holder.txtOldPrice.setPaintFlags(holder.txtOldPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                new_price = Float.parseFloat(model.getPrice()) * (1-Float.parseFloat(model.getDiscount()));

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