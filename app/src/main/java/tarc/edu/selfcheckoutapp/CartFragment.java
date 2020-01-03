package tarc.edu.selfcheckoutapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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

import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import tarc.edu.selfcheckoutapp.Model.Cart;
import tarc.edu.selfcheckoutapp.UtlityClass.LoginPreferenceUtils;
import tarc.edu.selfcheckoutapp.ViewHolder.CartViewHolder;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button checkoutBtn;
    private TextView txtTotal;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
    private float overTotalPrice = 0;
    RelativeLayout layout1,layout2;
    private FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;
    private Double discount;
    private Double dscPrice;


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

        //To display empty cart view when there is no item in the cart
        cartListRef.child("User View")
                .child(LoginPreferenceUtils.getPhone(getActivity()))
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
                .child(LoginPreferenceUtils.getPhone(getActivity()))
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
                        .child(LoginPreferenceUtils.getPhone(getActivity()))
                        .child("Products"),Cart.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model)
            {
                Picasso.get().load(model.getImageRef()).into(holder.circleImageView);
                holder.txtPName.setText(model.getPname());
                holder.txtPWeight.setText(model.getWeight());
                holder.numberPicker.setValue(Integer.parseInt(model.getQuantity()));
                Double price = Double.parseDouble(model.getPrice());
                holder.txtPPrice.setText("RM " + String.format("%.2f",price));

                if(model.getDiscount()!=null) {
                    discount = model.getDiscount();
                    dscPrice = price * (1 - discount);
                }

                if(discount!=null) {
                    if (discount > 0) {
                        holder.txtDiscountPrice.setVisibility(View.VISIBLE);
                        holder.txtPPrice.setTextSize(12);
                        holder.txtPPrice.setTextColor(getResources().getColor(R.color.grey));
                        holder.txtPPrice.setPaintFlags(holder.txtPPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.txtDiscountPrice.setText("RM" + String.format("%.2f", dscPrice));
                    }
                }




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


            new AlertDialog.Builder(getActivity())
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int position = viewHolder.getAdapterPosition();
                            Cart cart = adapter.getItem(position);


                            if(position==0){
                                txtTotal.setText("0.00");
                                ((HomeActivity)getActivity()).itemCount = 0;
                                ((HomeActivity)getActivity()).showBadgeCount(0);

                            }




                            cartListRef.child("User View")
                                    .child(LoginPreferenceUtils.getPhone(getActivity()))
                                    .child("Products")
                                    .child(cart.getPid())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(getActivity(), "Item Removed Successfully",Toast.LENGTH_SHORT).show();
                                        getTotalPrice();

                                        if(adapter.getItemCount() == 0)
                                        {
                                            layout1.setVisibility(View.GONE);
                                            layout2.setVisibility(View.VISIBLE);

                                        }



                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        }
                    })
                    .setCancelable(false)
                    .show();







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
                .child(LoginPreferenceUtils.getPhone(getActivity()))
                .child("Products")
                .child(pid)
                .child("quantity")
                .setValue(String.valueOf(value));
    }

    //To calculate total price of items in the cart
    public void getTotalPrice(){
        overTotalPrice = 0;
        cartListRef.child("User View")
                .child(LoginPreferenceUtils.getPhone(getActivity()))
                .child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                        Cart cart = snapshot.getValue(Cart.class);
                        Float price = Float.parseFloat(cart.getPrice()) * (1-Float.valueOf(cart.getDiscount().toString()));

                        overTotalPrice += price * Float.parseFloat(cart.getQuantity());
                        txtTotal.setText("RM " + String.format("%.2f", overTotalPrice));

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


//    @Override
//    public void onPause() {
//        super.onPause();
//
//        overTotalPrice =0;
//    }

}