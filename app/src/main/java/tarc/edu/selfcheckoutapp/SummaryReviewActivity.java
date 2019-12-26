package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tarc.edu.selfcheckoutapp.Model.Cart;
import tarc.edu.selfcheckoutapp.UtlityClass.LoginPreferenceUtils;
import tarc.edu.selfcheckoutapp.ViewHolder.ReviewViewHolder;


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
    Float discountedPrice;
    private Double discount;
    private Double dscPrice;


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
        discountedPrice = Float.parseFloat((String.format("%.2f",0.0)));
        txtDiscPrice.setText(String.format("%.2f",discountedPrice));
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
                dbDiscountValue = String.format("%.2f",discountedPrice);

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
                        .child(LoginPreferenceUtils.getPhone(SummaryReviewActivity.this))
                        .child("Products"),Cart.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Cart, ReviewViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReviewViewHolder holder, int position, @NonNull Cart model)
            {


                Picasso.get().load(model.getImageRef()).into(holder.circleImageView);
                holder.txtPName.setText(model.getPname());
                holder.txtPWeight.setText(model.getWeight());
                Double price = Double.parseDouble(model.getPrice());
                holder.txtPPrice.setText("RM " + String.format("%.2f",price));
                holder.txtPQuantity.setText("x"+model.getQuantity());


                if(model.getDiscount()!=null) {
                    discount = model.getDiscount();
                    dscPrice = price * (1 - discount);
                }

                if(discount!=null) {
                    if (discount > 0) {
                        holder.txtDscPrice.setVisibility(View.VISIBLE);
                        holder.txtPPrice.setTextColor(getResources().getColor(R.color.grey));
                        holder.txtPPrice.setPaintFlags(holder.txtPPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.txtDscPrice.setText("RM" + String.format("%.2f", dscPrice));
                        holder.txtDscPrice.setTextSize(11);
                    }
                }







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

                    cartListRef.child("Discount").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(inputCode)) {

                                if(dataSnapshot.child(inputCode).child("totalRedemption").getValue(Integer.class) != 0)
                                {
                                    Float minPurchase = dataSnapshot.child(inputCode).child("minPurchase").getValue(Float.class);
                                    Float maxDsc = dataSnapshot.child(inputCode).child("maxDiscount").getValue(Float.class);
                                    Integer limitPerUser = dataSnapshot.child(inputCode).child("LimitPerUser").getValue(Integer.class);
                                    if(TPrice > minPurchase) {
                                        Long expiry = dataSnapshot.child(inputCode).child("expiryDate").getValue(Long.class);
                                        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                        try {
                                            Date date = (Date)formatter.parse("01-02-2020");
                                            long output = date.getTime();

                                            Calendar calForDate = Calendar.getInstance();
                                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
                                            String saveCurrentDate = currentDate.format(calForDate.getTime());
                                            Date cDate = (Date)formatter.parse(saveCurrentDate);
                                            long output2 = cDate.getTime();
//                                            String str2 = Long.toString(output2);
//                                            long todayDate = Long.parseLong(str2)*1000;

                                            if(output2 <= expiry)
                                            {
                                                discountRate = dataSnapshot.child(inputCode).child("rate").getValue(String.class);

                                                cartListRef.child("Transaction").orderByChild("dscCode_phone").equalTo(inputCode+"_"+LoginPreferenceUtils.getPhone(SummaryReviewActivity.this)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                        long usedFreq = dataSnapshot2.getChildrenCount();

                                                        if(usedFreq<limitPerUser)
                                                        {
                                                            Float discountValue = Float.parseFloat(discountRate) * 100;
                                                            discountlayout.setVisibility(View.VISIBLE);
                                                            txtDiscount.setText(String.format("%.0f", discountValue) + " %");
                                                            discPriceLayout.setVisibility(View.VISIBLE);
                                                            discountedPrice = TPrice * Float.parseFloat(discountRate);


                                                            if(discountedPrice>maxDsc)
                                                            {
                                                                discountedPrice = maxDsc;
                                                            }

                                                            txtDiscPrice.setText("(-RM " + String.format("%.2f", discountedPrice) + ")");
                                                            GrandTotal = TPrice - discountedPrice;
                                                            txtGrandTotal.setText("RM " + String.format("%.2f", GrandTotal));
                                                            newPrice = GrandTotal;
                                                            txtTotal.setText(String.format("%.2f", newPrice));
                                                            inputDiscount.setEnabled(false);
                                                            btnApplyDiscount.setVisibility(View.GONE);
                                                            btnCancel.setVisibility(View.VISIBLE);
                                                            Toast.makeText(SummaryReviewActivity.this,
                                                                    String.valueOf(output),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(SummaryReviewActivity.this,
                                                                    "Redemption limit per user has been reached",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });


                                            }
                                            else
                                            {
                                                Toast.makeText(SummaryReviewActivity.this,
                                                        "This voucher is expired",
                                                        Toast.LENGTH_SHORT).show();
                                            }



                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                    else {
                                        Toast.makeText(SummaryReviewActivity.this,
                                                "The minimum value for the voucher has not been reached",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(SummaryReviewActivity.this,
                                            "Redemption limit has been reached.",
                                            Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                Toast.makeText(SummaryReviewActivity.this,
                                        "Invalid Code!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(SummaryReviewActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

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
