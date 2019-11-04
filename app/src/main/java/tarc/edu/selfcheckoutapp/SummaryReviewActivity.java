package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.google.android.gms.wallet.TransactionInfo;
//import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SummaryReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Float TPrice,GrandTotal;
    private String transactionID;
    private TextView txtTotal,txtSubtotal,txtGrandTotal,txtDiscount,txtDiscPrice, txtTransactionDate, txtTransactionTime, txtTransactionAmt;
    private EditText inputDiscount;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference();
    private LinearLayout discountlayout, discPriceLayout;
    private RelativeLayout dialogLayout;
    private Button btnApplyDiscount, btnCancel, btnPay, btnViewReceipt;
    private Float newPrice;
    private static final String TAG = "SummaryReviewActivity";
    FirebaseRecyclerAdapter<Cart, ReviewViewHolder> adapter;
    Dialog successfulDialog;
    String saveCurrentTime, saveCurrentDate;
    String discountRate;

    static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    final private Random rng = new SecureRandom();



    private static final int REQUEST_CODE = 1234;
    String API_GET_TOKEN = "http://172.16.101.237/braintree/main.php";
    String API_CHECK_OUT = "http://172.16.101.237/braintree/checkout.php";

    String token,amount,paymentPrice;
    Float discountedPrice ;
    HashMap<String,String> paramsHash;


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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                getSupportFragmentManager().popBackStackImmediate();
//            }
//        });

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


        new getToken().execute();



        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPayment();
            }
        });

        transactionID = randomUUID(16,4,'-');





    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    char randomChar(){
        return ALPHABET.charAt(rng.nextInt(ALPHABET.length()));
    }

    //Generate unique transaction ID
    String randomUUID(int length, int spacing, char spacerChar){
        StringBuilder sb = new StringBuilder();
        int spacer = 0;
        while(length > 0){
            if(spacer == spacing){
                sb.append(spacerChar);
                spacer = 0;
            }
            length--;
            spacer++;
            sb.append(randomChar());
        }
        return sb.toString();
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


    private void submitPayment(){

        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token.trim());
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNonce = nonce.getNonce();

                if(!txtTotal.getText().toString().isEmpty()){
                    amount = txtTotal.getText().toString();
                    paramsHash = new HashMap<>();
                    paramsHash.put("amount",amount);
                    paramsHash.put("nonce",strNonce);


                    sendPayment();

                }
                else
                {

                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }





            }
            else if(resultCode == RESULT_CANCELED)
                Toast.makeText(this, "Transaction cancelled", Toast.LENGTH_SHORT).show();
            else
            {
                Exception error = (Exception)data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("ERROR", error.toString());
            }

        }
    }

    private void sendPayment() {

        RequestQueue queue = Volley.newRequestQueue(SummaryReviewActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_CHECK_OUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.toString().contains("Successful")){
                    addingTransactiontoDB();
                    showPopup();

                }

                else
                {
                    Toast.makeText(SummaryReviewActivity.this, "Transaction failed !", Toast.LENGTH_SHORT).show();

                }
                Log.d("ERROR", response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.toString());




            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if(paramsHash == null)
                    return null;
                Map<String,String> params = new HashMap<>();
                for(String key:paramsHash.keySet())
                {
                    params.put(key,paramsHash.get(key));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);


    }

    private class getToken extends AsyncTask {

        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(SummaryReviewActivity.this,android.R.style.Theme_DeviceDefault_Dialog);
            mDialog.setCancelable(false);
            mDialog.setMessage("Please Wait");
            mDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(API_GET_TOKEN, new HttpResponseCallback() {
                @Override
                public void success(String responseBody) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //Set Token
                            token = responseBody;

                        }
                    });
                }

                @Override
                public void failure(Exception exception) {
                    Log.d("ERROR", exception.toString());

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mDialog.dismiss();
        }
    }


    //Display Success Screen
    public void showPopup(){
        successfulDialog.setContentView(R.layout.transaction_success_dialog);
        txtTransactionDate = (TextView)successfulDialog.findViewById(R.id.transaction_date);
        txtTransactionTime = (TextView)successfulDialog.findViewById(R.id.transaction_time);
        txtTransactionAmt= (TextView)successfulDialog.findViewById(R.id.transaction_amount);
        btnViewReceipt = (Button)successfulDialog.findViewById(R.id.view_receipt_btn);



        txtTransactionDate.setText(saveCurrentDate);
        txtTransactionTime.setText(saveCurrentTime);
        txtTransactionAmt.setText("RM " + txtTotal.getText().toString());



        successfulDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successfulDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        successfulDialog.setCanceledOnTouchOutside(false);
        successfulDialog.show();

        btnViewReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Float subtotalValue = TPrice;
                Float discountValue = discountedPrice;
                Float totalValue = Float.parseFloat(txtTotal.getText().toString());
                String uniqueID = transactionID;
                Intent i = new Intent(SummaryReviewActivity.this, ViewReceiptActivity.class);
                i.putExtra("subvalue",subtotalValue);
                if(discountValue == null){
                    i.putExtra("discvalue",(float)0.0);
                }else {
                    i.putExtra("discvalue", discountValue);
                }
                i.putExtra("totalvalue",totalValue);
                i.putExtra("uniqueID",uniqueID);
                startActivity(i);
            }
        });
    }

    public void addingTransactiontoDB(){

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final HashMap<String, Object> transactionMap = new HashMap<>();
        final DatabaseReference fromRef = cartListRef.child("Cart List").child("User View").child("013-6067208").child("Products");
        final DatabaseReference toRef = cartListRef.child("Transaction").child("013-6067208").child(transactionID).child("product");

        String dbDiscountCode,dbDate,dbTime,dbDiscountRate,dbDiscountValue;
        Double dbSubtotal, dbTotal;

        dbDiscountRate = discountRate;
        dbDiscountCode = inputDiscount.getText().toString();
        dbSubtotal = Double.parseDouble(new Float(TPrice).toString());
        dbTotal = Double.parseDouble(txtTotal.getText().toString());
        dbTime = saveCurrentTime;
        dbDate = saveCurrentDate;



            dbDiscountValue = txtDiscPrice.getText().toString();


        transactionMap.put("discountRate", dbDiscountRate);
        transactionMap.put("discountCode",dbDiscountCode);
        transactionMap.put("subtotal",dbSubtotal);
        transactionMap.put("total",dbTotal);
        transactionMap.put("tscDate", dbDate);
        transactionMap.put("tscTime", dbTime);
        transactionMap.put("tscID", transactionID);
        transactionMap.put("discountValue", dbDiscountValue);

        cartListRef.child("Transaction").child("013-6067208").child(transactionID)
                .updateChildren(transactionMap);

        moveRecord(fromRef,toRef);

        cartListRef.child("Cart List").child("User View").child("013-6067208").child("Products").removeValue();





    }

    private void moveRecord(DatabaseReference fromPath, final DatabaseReference toPath) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Log.d(TAG, "Success!");
                        } else {
                            Log.d(TAG, "Copy failed!");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
