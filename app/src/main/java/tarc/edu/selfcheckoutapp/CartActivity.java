package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import tarc.edu.selfcheckoutapp.R;
import com.squareup.picasso.Picasso;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button checkoutBtn;
    private TextView txtTotal;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
    private float overTotalPrice = 0;
    FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;
    private TextView emptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView  = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        emptyView = (TextView)findViewById(R.id.empty_view);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        Toolbar toolbar = findViewById(R.id.cart_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shopping Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        checkoutBtn = (Button)findViewById(R.id.checkOutButton);
        txtTotal = (TextView) findViewById(R.id.cart_total_price);



    }

    @Override
    protected void onStart() {
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

//                int count = 0;
//                if(adapter.getItemCount() == 0){
//                    recyclerView.setVisibility(View.GONE);
//                    emptyView.setVisibility(View.VISIBLE);
//                    txtTotal.setText(adapter.getItemCount());
//
//                    count = adapter.getItemCount();
//                }

                Picasso.get().load(model.getImageRef()).into(holder.circleImageView);
                holder.txtPName.setText(model.getPname());
                holder.txtPWeight.setText(model.getWeight());
                holder.txtPPrice.setText("RM " + model.getPrice());
                holder.numberPicker.setValue(Integer.parseInt(model.getQuantity()));

                float singleItemTotal = Float.parseFloat(model.getPrice()) * Float.parseFloat(model.getQuantity());

                overTotalPrice = overTotalPrice + singleItemTotal;

                txtTotal.setText("RM " + String.format("%.2f",overTotalPrice));


                holder.numberPicker.setValueChangedListener(new ValueChangedListener() {
                    @Override
                    public void valueChanged(int value, ActionEnum action) {

                        if(action == ActionEnum.INCREMENT) {
//                          overTotalPrice = overTotalPrice + Float.parseFloat(model.getPrice());
                            updateQuantity(model.getPid(),value);
                            refresh();
//                          txtTotal.setText("RM " + String.format("%.2f",overTotalPrice));


                        }else if(action == ActionEnum.DECREMENT) {

                            updateQuantity(model.getPid(),value);
                            refresh();

//                            overTotalPrice = overTotalPrice - Float.parseFloat(model.getPrice());
//                            txtTotal.setText("RM " + String.format("%.2f",overTotalPrice));


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


            cartListRef.child("User View")
                    .child("013-6067208")
                    .child("Products")
                    .child(cart.getPid())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(CartActivity.this, "Item Removed Successfully",Toast.LENGTH_SHORT).show();
                        refresh();
                    }
                }
            });




        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(CartActivity.this,R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public void refresh(){
        Intent i = new Intent(CartActivity.this, CartActivity.class);
        finish();
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

    public void updateQuantity(String pid, int value){
        cartListRef.child("User View")
                .child("013-6067208")
                .child("Products")
                .child(pid)
                .child("quantity")
                .setValue(String.valueOf(value));
    }



    @Override
    protected void onPause() {
        super.onPause();

        overTotalPrice =0;
    }
}
