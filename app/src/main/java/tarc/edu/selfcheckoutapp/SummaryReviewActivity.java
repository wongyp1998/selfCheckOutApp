package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class SummaryReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Float TPrice,GrandTotal;
    private TextView txtTotal,txtSubtotal,txtGrandTotal,txtDiscount,txtDiscPrice;
    private EditText inputDiscount;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference();
    private LinearLayout discountlayout, discPriceLayout;
    private Button btnApplyDiscount, btnCancel, btnPay;
    private Float newPrice;
    FirebaseRecyclerAdapter<Cart, ReviewViewHolder> adapter;
    Dialog successfulDialog;
    String discountRate;
    Float discountedPrice ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_review);

        recyclerView  = findViewById(R.id.review_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        txtTotal = findViewById(R.id.review_total_price);
        txtSubtotal = findViewById(R.id.sub_total_value);
        txtDiscount = findViewById(R.id.discount_value);
        txtDiscPrice = findViewById(R.id.discount_price);
        txtGrandTotal = findViewById(R.id.final_total_value);
        discPriceLayout = findViewById(R.id.linear_2);
        discountlayout = findViewById(R.id.linear_3);
        btnApplyDiscount = findViewById(R.id.apply_btn);
        inputDiscount = findViewById(R.id.promo_edit);
        btnCancel = findViewById(R.id.cancel_btn);
        btnPay = findViewById(R.id.payButton);
        successfulDialog = new Dialog(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.review_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Summary Review");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent ii = getIntent();
        Bundle b = ii.getExtras();

        if(b!=null)
        {
            TPrice = (Float) b.get("totalprice");
        }

        txtTotal.setText(String.format("%.2f",TPrice));

        inputDiscount.setSelection(0);
        txtDiscPrice.setText("-(0.00)");
        txtSubtotal.setText("RM " + String.format("%.2f",TPrice));
        txtGrandTotal.setText("RM " + String.format("%.2f",TPrice));

        applyDiscount();



        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SummaryReviewActivity.this, SelectPaymentActivity.class);
                intent.putExtra("total_amount", txtTotal.getText().toString());
                startActivity(intent);

                SharedPreferences pref = getSharedPreferences("tscDataPref",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                String dbDiscountCode,dbDiscountRate,dbDiscountValue,dbTscID;
                String dbSubtotal, dbTotal;

                dbDiscountRate = discountRate;
                dbDiscountCode = inputDiscount.getText().toString();
                dbSubtotal = new Float(TPrice).toString();
                dbTotal = txtTotal.getText().toString();
                dbDiscountValue = txtDiscPrice.getText().toString();

                editor.putString("keyCode",dbDiscountCode);
                editor.putString("keyRate",dbDiscountRate);
                editor.putString("keyValue",dbDiscountValue);
                editor.putString("keyTotal",dbTotal);
                editor.putString("keySubtotal",dbSubtotal);

                editor.apply();




            }
        });





    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("Cart List").child("User View")
                        .child("013-6067208")
                        .child("Products"),Cart.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Cart, ReviewViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReviewViewHolder holder, int position, @NonNull Cart model)
            {


                Picasso.get().load(model.getImageRef()).into(holder.circleImageView);
                holder.txtPName.setText(model.getPname());
                holder.txtPWeight.setText(model.getWeight());
                holder.txtPPrice.setText("RM " + model.getPrice());
                holder.txtPQuantity.setText("x"+model.getQuantity());







            }

            @NonNull
            @Override
            public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item_layout,parent,false);
                ReviewViewHolder holder = new ReviewViewHolder(view);
                return holder;
            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }


    //Apply discount actions
    public void applyDiscount(){
        btnApplyDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputCode = inputDiscount.getText().toString();

                if(inputDiscount.getText().toString().isEmpty()){
                    Toast.makeText(SummaryReviewActivity.this,
                            "Please enter a promo code!",
                            Toast.LENGTH_SHORT).show();
                }else {

                    cartListRef.child("discount").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(inputCode)) {
                                discountRate = dataSnapshot.child(inputCode).child("rate").getValue(String.class);
                                Float discountValue = Float.parseFloat(discountRate) * 100;
                                discountlayout.setVisibility(View.VISIBLE);
                                txtDiscount.setText(String.format("%.0f", discountValue) + " %");
                                discPriceLayout.setVisibility(View.VISIBLE);
                                discountedPrice = TPrice * Float.parseFloat(discountRate);
                                txtDiscPrice.setText("(-RM " + String.format("%.2f", discountedPrice) + ")");
                                GrandTotal = TPrice - discountedPrice;
                                txtGrandTotal.setText("RM " + String.format("%.2f", GrandTotal));
                                newPrice = GrandTotal;
                                txtTotal.setText(String.format("%.2f",newPrice));
                                inputDiscount.setEnabled(false);
                                btnApplyDiscount.setVisibility(View.GONE);
                                btnCancel.setVisibility(View.VISIBLE);
                                Toast.makeText(SummaryReviewActivity.this,
                                        "Apply Successfully",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SummaryReviewActivity.this,
                                        "Invalid Code!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDiscount.getText().clear();
                inputDiscount.setEnabled(true);
                btnApplyDiscount.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                txtGrandTotal.setText("RM " + String.format("%.2f", TPrice));
                txtTotal.setText(String.format("%.2f",TPrice));
                discountlayout.setVisibility(View.GONE);
                discPriceLayout.setVisibility(View.GONE);



            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
