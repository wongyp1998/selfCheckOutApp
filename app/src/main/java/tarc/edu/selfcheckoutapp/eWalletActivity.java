package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class eWalletActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseRecyclerAdapter<WalletTransaction, WalletActivityViewHolder> adapter;
    private Button topup_btn;
    private TextView balancetxt;
    final DatabaseReference userdbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_wallet);

        recyclerView  = findViewById(R.id.wallet_activity_recycleview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.wallet_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Wallet");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        topup_btn = findViewById(R.id.btn_topup);
        balancetxt = findViewById(R.id.blc_amount);

        userdbRef.child("User").child("0136067208").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                showBalance();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                showBalance();
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




        topup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(eWalletActivity.this, WalletTopUpActivity.class);
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


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<WalletTransaction> options = new FirebaseRecyclerOptions.Builder<WalletTransaction>()
                .setQuery(cartListRef.child("WalletTransaction")
                        .child("0136067208").orderByChild("wTscDateTime").limitToLast(4),WalletTransaction.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<WalletTransaction, WalletActivityViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull WalletActivityViewHolder holder, int position, @NonNull WalletTransaction model)
            {
                holder.wTscDesc.setText(model.getwTscDesc());

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String date = dateFormat.format(new Date(model.getwTscDateTime()));
                holder.wTscDate.setText(date);
                Double amount = Double.parseDouble(model.getwTscAmount());
                holder.wTscAmt.setText("RM" + String.format("%.2f",amount));

                if(model.getwTscStatus().equals("1"))
                {
                    holder.debit.setVisibility(View.VISIBLE);
                    holder.credit.setVisibility(View.INVISIBLE);
                }else if(model.getwTscStatus().equals("0"))
                {
                    holder.debit.setVisibility(View.INVISIBLE);
                    holder.credit.setVisibility(View.VISIBLE);
                }




            }

            @NonNull
            @Override
            public WalletActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transaction_item,parent,false);
                WalletActivityViewHolder holder = new WalletActivityViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }



    private void showBalance(){
        userdbRef.child("User").child("0136067208").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double balance = dataSnapshot.child("balance").getValue(Double.class);
                balancetxt.setText(String.format("%.2f",balance));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
