package tarc.edu.selfcheckoutapp.ui.Home;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tarc.edu.selfcheckoutapp.Model.Voucher;
import tarc.edu.selfcheckoutapp.R;
import tarc.edu.selfcheckoutapp.TransactionDetailsActivity;
import tarc.edu.selfcheckoutapp.TransactionHistoryActivity;
import tarc.edu.selfcheckoutapp.ViewHolder.VoucherViewHolder;
import tarc.edu.selfcheckoutapp.VoucherDetailsActivity;


public class VoucherFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Voucher, VoucherViewHolder> adapter;
    final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_voucher, container, false);

        recyclerView  = root.findViewById(R.id.voucher_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        return root;

    }

    @Override
    public void onStart() {
        super.onStart();

        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        String saveCurrentDate = currentDate.format(calForDate.getTime());
        Date cDate = null;
        try {
            cDate = (Date)formatter.parse(saveCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        FirebaseRecyclerOptions<Voucher> options = new FirebaseRecyclerOptions.Builder<Voucher>()
                .setQuery(dbRef.child("Discount").orderByChild("expiryDate").startAt(cDate.getTime())
                        ,Voucher.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Voucher, VoucherViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VoucherViewHolder holder, int position, @NonNull Voucher model)
            {

                Double rate = Double.parseDouble(model.getRate())*100;

                holder.txtVoucherRate.setText(String.format("%.0f",rate)+"%");

                holder.txtVoucherHeader.setText(model.getHeader());

                long expDate = model.getExpiryDate();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
                String date = currentDate.format(expDate);


                holder.txtVoucherExpiryDate.setText(date);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), VoucherDetailsActivity.class);
                        intent.putExtra("vCode",model.getCode());
                        startActivity(intent);
                    }
                });




            }

            @NonNull
            @Override
            public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_item_layout,parent,false);
                VoucherViewHolder holder = new VoucherViewHolder(view);
                return holder;
            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();






    }


}
