package tarc.edu.selfcheckoutapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseRecyclerAdapter<Transaction, TransactionViewHolder> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_history_layout);

        recyclerView  = findViewById(R.id.tsc_history_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tsc_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Transaction History");

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
                        .child("013-6067208").orderByChild("Date"),Transaction.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Transaction, TransactionViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TransactionViewHolder holder, int position, @NonNull Transaction model)
            {
                holder.txtTscID.setText(model.getTscID());
                holder.txtTscDate.setText(model.getTscDate());
                holder.txtTscTime.setText(model.getTscTime());
                holder.txtTscAmt.setText("RM" + model.getTotal());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TransactionHistoryActivity.this, ViewReceiptActivity.class);
                        intent.putExtra("uniqueID2",model.getTscID());
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