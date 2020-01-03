package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import tarc.edu.selfcheckoutapp.Model.WalletTransaction;
import tarc.edu.selfcheckoutapp.UtlityClass.LoginPreferenceUtils;

public class WalletTopUpActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1234;
    String API_GET_TOKEN = "http://192.168.0.120:8080/braintree/main.php";
    String API_CHECK_OUT = "http://192.168.0.120:8080/braintree/checkout.php";

    static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    final private Random rng = new SecureRandom();

    final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    String token,amount,topUpMethod,topUpTscID;
    HashMap<String,String> paramsHash;
    private EditText topUpEdt;
    private Button continueBtn,dialogCtnBtn;
    private TextView topUpValue,errorMsg, txtWalletDateTime,txtWalletTopUpAmt;
    Dialog successfulDialog;
    private Map<String,String> date;
    private ProgressDialog loadingBar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_top_up);

        topUpEdt = findViewById(R.id.amount_txt);
        continueBtn = findViewById(R.id.ctnButton);
        topUpValue = findViewById(R.id.topup_amt);
        errorMsg = findViewById(R.id.error);
        successfulDialog = new Dialog(this);
        loadingBar = new ProgressDialog(this);



        Toolbar toolbar = (Toolbar) findViewById(R.id.topup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Top Up Wallet");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        topUpValue.setText("0");
        continueBtn.setEnabled(false);


        new getToken().execute();

        topUpEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!topUpEdt.getText().toString().isEmpty()) {
                    errorMsg.setVisibility(View.GONE);
                    Double input = Double.parseDouble(topUpEdt.getText().toString());
                    if (input >= 10 && input <= 999) {
                        continueBtn.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                        continueBtn.setEnabled(true);

                    } else {
                        errorMsg.setText("Please enter a number between RM10.00 and RM999.00");
                        errorMsg.setVisibility(View.VISIBLE);
                        continueBtn.setEnabled(false);
                        continueBtn.setBackgroundTintList(getResources().getColorStateList(R.color.lighter_grey));
                    }
                }
                else {
                    topUpValue.setText("0");
                }

                topUpValue.setText(topUpEdt.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!topUpEdt.getText().toString().isEmpty()) {
                    submitPayment();
                }else
                {

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
                topUpMethod = nonce.getTypeLabel();
                String strNonce = nonce.getNonce();

                if(!topUpEdt.getText().toString().isEmpty()){
                    amount = topUpEdt.getText().toString();
                    paramsHash = new HashMap<>();
                    paramsHash.put("amount",amount);
                    paramsHash.put("nonce",strNonce);

                    startLoadingDialog();
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

        RequestQueue queue = Volley.newRequestQueue(WalletTopUpActivity.this);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_CHECK_OUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.toString().contains("Successful")){
                    addTopUpActivityToDb();
                    updateBalance();
                    loadingBar.dismiss();
                    showPopup();

                }

                else
                {
                    Toast.makeText(WalletTopUpActivity.this, "Topped Up failed !", Toast.LENGTH_SHORT).show();

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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);


    }

    private class getToken extends AsyncTask {

        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(WalletTopUpActivity.this,android.R.style.Theme_DeviceDefault_Dialog);
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

                            //Set

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

    private void updateBalance(){

        dbRef.child("User").child(LoginPreferenceUtils.getPhone(WalletTopUpActivity.this)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double balance = dataSnapshot.child("balance").getValue(Double.class);
                Double newBalance = balance + Double.parseDouble(amount);
                dataSnapshot.getRef().child("balance").setValue(newBalance);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void addTopUpActivityToDb(){

        topUpTscID = randomUUID(12,3,'-');
        String topupAmt = amount;
        String status = "1";
        String desc = "Top Up";

        final HashMap<String, Object> TopUpMap = new HashMap<>();

        date = ServerValue.TIMESTAMP;



        TopUpMap.put("wTscDesc",desc);
        TopUpMap.put("wTscID",topUpTscID);
        TopUpMap.put("wTscDateTime",date);
        TopUpMap.put("wTscAmount",topupAmt);
        TopUpMap.put("wTscStatus",status);
        TopUpMap.put("wPaymentMethod",topUpMethod);

        dbRef.child("WalletTransaction").child(LoginPreferenceUtils.getPhone(WalletTopUpActivity.this)).child(topUpTscID).updateChildren(TopUpMap);



    }

    public void startLoadingDialog()
    {
        loadingBar.setTitle("Payment Transaction");
        loadingBar.setMessage("Processing...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
    }

    public void showPopup(){
        successfulDialog.setContentView(R.layout.topup_success_dialog);
        txtWalletDateTime = (TextView)successfulDialog.findViewById(R.id.topup_date_time);
        txtWalletTopUpAmt = (TextView)successfulDialog.findViewById(R.id.topup_amount);
        dialogCtnBtn = (Button)successfulDialog.findViewById(R.id.dialog_continue);

        dbRef.child("WalletTransaction").child(LoginPreferenceUtils.getPhone(this)).child(topUpTscID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WalletTransaction tscWallet = dataSnapshot.getValue(WalletTransaction.class);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
                String date = dateFormat.format(new Date(tscWallet.getwTscDateTime()));
                txtWalletDateTime.setText(date);
                Double amount = Double.parseDouble(tscWallet.getwTscAmount());
                txtWalletTopUpAmt.setText("RM" + String.format("%.2f",amount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        dialogCtnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });



        successfulDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successfulDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        successfulDialog.setCanceledOnTouchOutside(false);
        successfulDialog.show();


    }
}
