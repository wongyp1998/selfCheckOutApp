package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tarc.edu.selfcheckoutapp.Model.Transaction;

public class TransactionDetailsActivity extends AppCompatActivity {

    private TextView txtTscDetailStatus,txtTscDetailID,txtTscDetailHpNo,txtTscDetailMethod,txtTscDetailDate,txtTscDetailTime,txtTscDetailAmt,txtUnverifiedMsg;
    private String tID;
    final DatabaseReference tscListRef = FirebaseDatabase.getInstance().getReference();
    private Button viewReceipt_btn;
    private LinearLayout detail_status_layout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        txtTscDetailStatus = findViewById(R.id.tscDetail_status);
        txtTscDetailID = findViewById(R.id.tscDetail_ID);
        txtTscDetailMethod = findViewById(R.id.tscDetail_payMethod);
        txtTscDetailDate = findViewById(R.id.tscDetail_Date);
        txtTscDetailTime = findViewById(R.id.tscDetail_Time);
        txtTscDetailHpNo = findViewById(R.id.tscDetail_hpNo);
        viewReceipt_btn = findViewById(R.id.viewReceipt_btn);
        txtTscDetailAmt = findViewById(R.id.tscDetail_amount);
        detail_status_layout = findViewById(R.id.tscDetail_status_layout);
        txtUnverifiedMsg = findViewById(R.id.unverified_msg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Transaction Details");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            tID = extras.getString("tscID");

        }

        tscListRef.child("Transaction").child(tID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Transaction transaction = dataSnapshot.getValue(Transaction.class);

                txtTscDetailAmt.setText(String.format("%.2f",transaction.getTotal()));

                if(transaction.getVerifyStatus() == 0)
                {
                    txtUnverifiedMsg.setVisibility(View.VISIBLE);
//                    txtUnverifiedMsg.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_error_outline_24px,0,0,0);
                    detail_status_layout.setBackgroundResource(R.drawable.unverified_background);
                    txtTscDetailStatus.setText("UNVERIFIED");
                    txtTscDetailStatus.setTextColor(getResources().getColor(R.color.unverified));
                }else
                {
                    txtUnverifiedMsg.setVisibility(View.GONE);
                    detail_status_layout.setBackgroundResource(R.drawable.verified_background);
                    txtTscDetailStatus.setText("VERIFIED");
                    txtTscDetailStatus.setTextColor(getResources().getColor(R.color.verified));


                }



                txtTscDetailHpNo.setText(transaction.getCustomerPhone());
                txtTscDetailID.setText(transaction.getTscID());
                txtTscDetailMethod.setText(transaction.getPaymentMethod());
                txtTscDetailDate.setText(transaction.getTscDate());
                txtTscDetailTime.setText(transaction.getTscTime());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewReceipt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransactionDetailsActivity.this, ViewReceiptActivity.class);
                intent.putExtra("uniqueID2",tID);
                startActivity(intent);
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
