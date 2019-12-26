package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

import tarc.edu.selfcheckoutapp.Model.Transaction;
import tarc.edu.selfcheckoutapp.Model.Voucher;

public class VoucherDetailsActivity extends AppCompatActivity {

    private TextView txtVCode,txtVExpiryDate,txtVHeader,txtVDesc;
    private Button copy_btn;
    private String voucherCode;
    final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    ClipboardManager clipboardManager;
    LinearLayout linearLayout;
    boolean expired = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.voucher_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Voucher Details");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        txtVCode = findViewById(R.id.voucher_detail_code);
        txtVDesc = findViewById(R.id.voucher_detail_desc);
        txtVExpiryDate = findViewById(R.id.voucher_detail_expiryDate);
        txtVHeader = findViewById(R.id.voucher_detail_header);
        copy_btn = findViewById(R.id.copyToClip_btn);
        linearLayout = findViewById(R.id.tc_content_layout);


        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);


        copy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = txtVCode.getText().toString();
                if(!text.equals(""))
                {
                    ClipData clipData = ClipData.newPlainText("text",text);
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(VoucherDetailsActivity.this,"Copied",Toast.LENGTH_SHORT).show();
                }
            }
        });


        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            voucherCode = extras.getString("vCode");

        }

        if(getIntent().hasExtra("expKey"))
        {
            expired = true;
        }


            dbRef.child("Discount").child(voucherCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Voucher voucher = dataSnapshot.getValue(Voucher.class);

                Double rate = Double.parseDouble(voucher.getRate())*100;

                txtVCode.setText(voucher.getCode());
                txtVDesc.setText(voucher.getDesc());
                long expDate = voucher.getExpiryDate();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
                String date = currentDate.format(expDate);
                if(expired == false) {
                    txtVExpiryDate.setText(date);
                }else {
                    txtVExpiryDate.setText("Expired");

                }
                txtVHeader.setText(voucher.getHeader());

                TextView tcText = new TextView(VoucherDetailsActivity.this);
                TextView tcText2 = new TextView(VoucherDetailsActivity.this);
                TextView tcText3 = new TextView(VoucherDetailsActivity.this);
                TextView tcText4 = new TextView(VoucherDetailsActivity.this);
                TextView tcText5 = new TextView(VoucherDetailsActivity.this);
                TextView tcText6 = new TextView(VoucherDetailsActivity.this);



                tcText.setText("\u2022"+"You've earned " + String.format("%.0f",rate) + "% OFF for your next in-store transaction" );
                tcText2.setText("\u2022"+"Offer is valid on orders of RM" + voucher.getMinPurchase() + " and above" );
                tcText3.setText("\u2022"+"Maximum Discount is RM"+ voucher.getMaxDiscount() );
                tcText4.setText("\u2022"+"Limited to " + voucher.getLimitPerUser() + " redemptions per customer" );
                tcText5.setText("\u2022"+"Limited to " + voucher.getTotalRedemption() + " redemptions while stocks last" );
                tcText6.setText("\u2022"+"Not Valid for use in conjunction with other promotions" );


                tcText.setTextColor(getResources().getColor(R.color.grey));
                tcText2.setTextColor(getResources().getColor(R.color.grey));
                tcText3.setTextColor(getResources().getColor(R.color.grey));
                tcText4.setTextColor(getResources().getColor(R.color.grey));
                tcText5.setTextColor(getResources().getColor(R.color.grey));
                tcText6.setTextColor(getResources().getColor(R.color.grey));

                linearLayout.addView(tcText);
                linearLayout.addView(tcText2);
                linearLayout.addView(tcText3);
                linearLayout.addView(tcText4);
                linearLayout.addView(tcText5);
                linearLayout.addView(tcText6);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
}
