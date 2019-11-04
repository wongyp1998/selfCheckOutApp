package tarc.edu.selfcheckoutapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import tarc.edu.selfcheckoutapp.R;
import tarc.edu.selfcheckoutapp.Cart;
import tarc.edu.selfcheckoutapp.CartViewHolder;
import tarc.edu.selfcheckoutapp.MainActivity;

import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.squareup.picasso.Picasso;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button checkoutBtn;
    private TextView txtTotal;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
    private float overTotalPrice = 0;
    RelativeLayout layout1,layout2;
    private FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_cart, container, false);




        recyclerView  = root.findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        layout1 = root.findViewById(R.id.cart_layout);
        layout2 = root.findViewById(R.id.empty_layout);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        checkoutBtn = (Button)root.findViewById(R.id.checkOutButton);
        txtTotal = (TextView) root.findViewById(R.id.cart_total_price);

        cartListRef.child("User View")
                .child("013-6067208")
                .child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    layout1.setVisibility(View.VISIBLE);
                }
                else{
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cartListRef.child("User View")
                .child("013-6067208")
                .child("Products").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Float totalPrice = overTotalPrice;
                Intent i = new Intent(getActivity(), SummaryReviewActivity.class);
                i.putExtra("totalprice", totalPrice);
                startActivity(i);
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                        .child("013-6067208")
                        .child("Products"),Cart.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model)
            {
                Picasso.get().load(model.getImageRef()).into(holder.circleImageView);
                holder.txtPName.setText(model.getPname());
                holder.txtPWeight.setText(model.getWeight());
                holder.txtPPrice.setText("RM " + model.getPrice());
                holder.numberPicker.setValue(Integer.parseInt(model.getQuantity()));




                holder.numberPicker.setValueChangedListener(new ValueChangedListener() {
                    @Override
                    public void valueChanged(int value, ActionEnum action) {

                        if(action == ActionEnum.INCREMENT) {
                            updateQuantity(model.getPid(),value);
                            getTotalPrice();




                        }else if(action == ActionEnum.DECREMENT) {

                            updateQuantity(model.getPid(),value);
                            getTotalPrice();

                        }

                    }
                });


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();

        getTotalPrice();



    }


    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            Cart cart = adapter.getItem(position);

            if(position==0){
                txtTotal.setText("0.00");
                ((MainActivity)getActivity()).itemCount = 0;
                ((MainActivity)getActivity()).showBadgeCount(0);
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
            }


            cartListRef.child("User View")
                    .child("013-6067208")
                    .child("Products")
                    .child(cart.getPid())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "Item Removed Successfully",Toast.LENGTH_SHORT).show();
                        getTotalPrice();



                    }
                }
            });




        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(),R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    public void updateQuantity(String pid, int value){
        cartListRef.child("User View")
                .child("013-6067208")
                .child("Products")
                .child(pid)
                .child("quantity")
                .setValue(String.valueOf(value));
    }

    public void getTotalPrice(){
        overTotalPrice = 0;
        cartListRef.child("User View")
                .child("013-6067208")
                .child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                        Cart cart = snapshot.getValue(Cart.class);
                        overTotalPrice += Float.parseFloat(cart.getPrice()) * Float.parseFloat(cart.getQuantity());
                        txtTotal.setText("RM " + String.format("%.2f", overTotalPrice));

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onPause() {
        super.onPause();

        overTotalPrice =0;
    }

}