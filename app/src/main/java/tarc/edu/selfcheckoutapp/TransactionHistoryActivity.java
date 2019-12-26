package tarc.edu.selfcheckoutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;

import tarc.edu.selfcheckoutapp.Model.Transaction;
import tarc.edu.selfcheckoutapp.UtlityClass.LoginPreferenceUtils;
import tarc.edu.selfcheckoutapp.ViewHolder.TransactionViewHolder;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseRecyclerAdapter<Transaction, TransactionViewHolder> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_history_layout);

        recyclerView  = findViewById(R.id.tsc_history_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);



        Toolbar toolbar = (Toolbar) findViewById(R.id.tsc_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Transaction History");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



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

        FirebaseRecyclerOptions<Transaction> options = new FirebaseRecyclerOptions.Builder<Transaction>()
                .setQuery(cartListRef.child("Transaction")
                        .orderByChild("customerPhone").equalTo(LoginPreferenceUtils.getPhone(this)),Transaction.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Transaction, TransactionViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TransactionViewHolder holder, int position, @NonNull Transaction model)
            {
                holder.txtTscID.setText(model.getTscID());
                holder.txtTscDate.setText(model.getTscDate());
                holder.txtTscTime.setText(model.getTscTime());
                holder.txtTscAmt.setText("RM" + model.getTotal());

                if(model.getVerifyStatus() == 0)
                {
                    holder.status_layout.setBackgroundResource(R.drawable.unverified_background);
                    holder.txtTscStatus.setText("UNVERIFIED");
                    holder.txtTscStatus.setTextColor(getResources().getColor(R.color.unverified));
                }else
                {
                    holder.status_layout.setBackgroundResource(R.drawable.verified_background);
                    holder.txtTscStatus.setText("VERIFIED");
                    holder.txtTscStatus.setTextColor(getResources().getColor(R.color.verified));


                }


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TransactionHistoryActivity.this, TransactionDetailsActivity.class);
                        intent.putExtra("tscID",model.getTscID());
                        startActivity(intent);

                    }
                });


            }

            @NonNull
            @Override
            public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item_layout,parent,false);
                TransactionViewHolder holder = new TransactionViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }
}
