package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;

import tarc.edu.selfcheckoutapp.Model.Cart;
import tarc.edu.selfcheckoutapp.UtlityClass.LoginPreferenceUtils;

public class SelectPaymentActivity extends AppCompatActivity {

    MaterialCardView cardView1, cardView2;
    private TextView payAmount;
    private String transactionID;
    Dialog successfulDialog;
    String saveCurrentTime, saveCurrentDate;
    private TextView txtTransactionDate, txtTransactionTime, txtTransactionAmt,walletBalance,errorMsg;
    private Button btnViewReceipt,btnPay;

    private static final int REQUEST_CODE = 1234;
    String API_GET_TOKEN = "http://192.168.0.120:8080/braintree/main.php";
    String API_CHECK_OUT = "http://192.168.0.120:8080/braintree/checkout.php";


    static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    final private Random rng = new SecureRandom();


    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "SelectPaymentActivity";
    String method;

    String token,amount;
    HashMap<String,String> paramsHash;
    private Handler handler = new Handler();

    private Executor executor = new Executor() {
        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payment);

        cardView1 = findViewById(R.id.wallet_option);
        cardView2 = findViewById(R.id.other_option);
        walletBalance = findViewById(R.id.wallet_balance);
        payAmount = findViewById(R.id.pay_amt);
        btnPay = findViewById(R.id.pay_btn);
        errorMsg = findViewById(R.id.wallet_error);
        successfulDialog = new Dialog(this);

        btnPay.setEnabled(false);

        cartListRef.child("User").child(LoginPreferenceUtils.getPhone(SelectPaymentActivity.this)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double balance = dataSnapshot.child("balance").getValue(Double.class);
                walletBalance.setText("RM" + String.format("%.2f",balance));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        Double amount = Double.parseDouble(getIntent().getStringExtra("total_amount"));

        payAmount.setText(String.format("%.2f",amount));

        new getToken().execute();

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView1.toggle();
                cardView2.setChecked(false);
//                btnPay.setBackgroundTintList(getResources().getColorStateList(R.color.red));
//                btnPay.setEnabled(true);
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView2.toggle();
                cardView1.setChecked(false);
//                btnPay.setBackgroundTintList(getResources().getColorStateList(R.color.red));
//                btnPay.setEnabled(true);
            }
        });




        cardView1.setOnCheckedChangeListener(new MaterialCardView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialCardView card, boolean isChecked) {
                if(isChecked == true){
                    btnPay.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                    btnPay.setEnabled(true);
                }else if(isChecked==false){
                    if(!cardView2.isChecked()==true) {
                        btnPay.setBackgroundTintList(getResources().getColorStateList(R.color.lighter_grey));
                        btnPay.setEnabled(false);
                    }
                    else{
                        btnPay.setEnabled(true);
                    }
                }
            }
        });

        cardView2.setOnCheckedChangeListener(new MaterialCardView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialCardView card, boolean isChecked) {
                if(isChecked == true){
                    btnPay.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                    btnPay.setEnabled(true);
                }else if(isChecked == false){
                    if(!cardView1.isChecked()==true) {
                        btnPay.setBackgroundTintList(getResources().getColorStateList(R.color.lighter_grey));
                        btnPay.setEnabled(false);

                    }
                    else{
                        btnPay.setEnabled(true);
                    }

                }
            }
        });


        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cardView1.isChecked()==false && cardView2.isChecked()==false)
                {
                    Toast.makeText(SelectPaymentActivity.this, "Please Select a Method to Pay! !", Toast.LENGTH_SHORT).show();
                }else if(cardView1.isChecked() == true)
                {
                    method = "E-Wallet";
                    Intent ii = new Intent(SelectPaymentActivity.this, PinVerificationActivity.class);
////                    ii.putExtra("tscID",transactionID);
                    ii.putExtra("verifyKey","key2");
                    startActivityForResult(ii,1);
                }else if(cardView2.isChecked() == true)
                {
                    submitPayment();
                }
            }
        });
    }





    private void submitPayment(){

        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token);
        enableGooglePay(dropInRequest);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    private void enableGooglePay(DropInRequest dropInRequest) {
        GooglePaymentRequest googlePaymentRequest = new GooglePaymentRequest()
                .transactionInfo(TransactionInfo.newBuilder()
                        .setTotalPrice("10.00")
                        .setTotalPriceStatus(WalletConstants.ENVIRONMENT_TEST)
                        .setCurrencyCode("MYR")
                        .build())
                // We recommend collecting and passing billing address information
                // with all Google Pay transactions as a best practice.
                .billingAddressRequired(true);
        // Optional in sandbox; if set in sandbox, this value must be
        // a valid production Google Merchant ID.
//      .googleMerchantId("merchant-id-from-google");

        dropInRequest.googlePaymentRequest(googlePaymentRequest);
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
                method = nonce.getTypeLabel();
                String strNonce = nonce.getNonce();

                if(!payAmount.getText().toString().isEmpty()){
                    amount = payAmount.getText().toString();
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

        }else if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                walletPayment();
            }
        }


    }

    private void sendPayment() {

        RequestQueue queue = Volley.newRequestQueue(SelectPaymentActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_CHECK_OUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.toString().contains("Successful")){
                    addingTransactiontoDB();
                    updateStock();
                    showPopup();

                }

                else
                {
                    Toast.makeText(SelectPaymentActivity.this, "Transaction failed !", Toast.LENGTH_SHORT).show();

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
            mDialog = new ProgressDialog(SelectPaymentActivity.this,android.R.style.Theme_DeviceDefault_Dialog);
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

                            //Set token

                            token = responseBody.trim();

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

    public void walletPayment(){

        cartListRef.child("User").child(LoginPreferenceUtils.getPhone(SelectPaymentActivity.this)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double balance = dataSnapshot.child("balance").getValue(Double.class);
                Double totalAmt = Double.parseDouble(payAmount.getText().toString());
                if(balance >= totalAmt){
                    Double newBalance = balance - totalAmt;
                    cartListRef.child("User").child(LoginPreferenceUtils.getPhone(SelectPaymentActivity.this)).child("balance").setValue(newBalance);
                    addingTransactiontoDB();
                    updateWalletActivity();
                    updateStock();
                    showPopup();
                }else{
                    errorMsg.setText("Insufficient wallet balance for transaction.");
                    errorMsg.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //    Display Success Screen
    public void showPopup(){
        successfulDialog.setContentView(R.layout.transaction_success_dialog);
        txtTransactionDate = (TextView)successfulDialog.findViewById(R.id.transaction_date);
        txtTransactionTime = (TextView)successfulDialog.findViewById(R.id.transaction_time);
        txtTransactionAmt= (TextView)successfulDialog.findViewById(R.id.transaction_amount);
        btnViewReceipt = (Button)successfulDialog.findViewById(R.id.view_receipt_btn);



        txtTransactionDate.setText(saveCurrentDate);
        txtTransactionTime.setText(saveCurrentTime);
        txtTransactionAmt.setText("RM " + payAmount.getText().toString());



        successfulDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successfulDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        successfulDialog.setCanceledOnTouchOutside(false);
        successfulDialog.show();

        btnViewReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SelectPaymentActivity.this, ViewReceiptActivity.class);
                i.putExtra("uniqueID",transactionID);
                startActivity(i);
            }
        });
    }

    private void updateWalletActivity(){

        String topUpTscID = randomUUID(12,3,'-');
        String topupAmt = payAmount.getText().toString();
        String status = "0";
        String desc = "Payment";

        final HashMap<String, Object> TopUpMap = new HashMap<>();

        TopUpMap.put("wTscDesc",desc);
        TopUpMap.put("wTscID",topUpTscID);
        TopUpMap.put("wTscDateTime", ServerValue.TIMESTAMP);
        TopUpMap.put("wTscAmount",topupAmt);
        TopUpMap.put("wTscStatus",status);

        cartListRef.child("WalletTransaction").child(LoginPreferenceUtils.getPhone(SelectPaymentActivity.this)).child(topUpTscID).updateChildren(TopUpMap);



    }

    public void addingTransactiontoDB(){

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        SharedPreferences pref = getSharedPreferences("tscDataPref",MODE_PRIVATE);
        transactionID = randomUUID(16,4,'-');


        final HashMap<String, Object> transactionMap = new HashMap<>();
        final DatabaseReference fromRef = cartListRef.child("Cart List").child("User View").child(LoginPreferenceUtils.getPhone(SelectPaymentActivity.this)).child("Products");
        final DatabaseReference toRef = cartListRef.child("Transaction").child(transactionID).child("product");

        String dbDate,dbTime;
        dbTime = saveCurrentTime;
        dbDate = saveCurrentDate;



        transactionMap.put("discountRate", pref.getString("keyRate",null));
        transactionMap.put("discountCode",pref.getString("keyCode",null));
        transactionMap.put("subtotal",Double.parseDouble(pref.getString("keySubtotal",null)));
        transactionMap.put("total",Double.parseDouble(pref.getString("keyTotal",null)));
        transactionMap.put("tscDate", dbDate);
        transactionMap.put("tscTime", dbTime);
        transactionMap.put("timeStamp",ServerValue.TIMESTAMP);
        transactionMap.put("tscID", transactionID);
        transactionMap.put("discountValue", pref.getString("keyValue",null));
        transactionMap.put("paymentMethod",method);
        transactionMap.put("customerPhone",LoginPreferenceUtils.getPhone(SelectPaymentActivity.this));
        transactionMap.put("verifyStatus",0);

        cartListRef.child("Transaction").child(transactionID)
                .updateChildren(transactionMap);

        moveRecord(fromRef,toRef);

        cartListRef.child("Cart List").child("User View").child(LoginPreferenceUtils.getPhone(SelectPaymentActivity.this)).child("Products").removeValue();





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

    private void updateStock()
    {
        cartListRef.child("Transaction").child(transactionID).child("product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Cart cart = snapshot.getValue(Cart.class);
                    String productID = cart.getPid();
                    int qty = Integer.parseInt(cart.getQuantity());
                    cartListRef.child("Products").child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int sold = dataSnapshot.child("Sold").getValue(Integer.class);
                            int currentStock = dataSnapshot.child("CurrentStock").getValue(Integer.class);
                            int newSold = sold + qty;
                            int newStock = currentStock - qty;
                            cartListRef.child("Products").child(productID).child("Sold").setValue(newSold);
                            cartListRef.child("Products").child(productID).child("CurrentStock").setValue(newStock);



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
}
