package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import tarc.edu.selfcheckoutapp.Model.WalletTransaction;
import tarc.edu.selfcheckoutapp.UtlityClass.LoginPreferenceUtils;
import tarc.edu.selfcheckoutapp.ViewHolder.WalletActivityViewHolder;

public class WalletHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseRecyclerAdapter<WalletTransaction, WalletActivityViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_history);

        recyclerView  = findViewById(R.id.wallet_history_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.wallet_history_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Wallet Transaction");

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

        FirebaseRecyclerOptions<WalletTransaction> options = new FirebaseRecyclerOptions.Builder<WalletTransaction>()
                .setQuery(cartListRef.child("WalletTransaction")
                        .child(LoginPreferenceUtils.getPhone(WalletHistoryActivity.this)).orderByChild("wTscDateTime"),WalletTransaction.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<WalletTransaction, WalletActivityViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull WalletActivityViewHolder holder, int position, @NonNull WalletTransaction model)
            {
                holder.wTscDesc.setText(model.getwTscDesc());

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
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
}
