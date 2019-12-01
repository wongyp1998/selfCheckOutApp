package tarc.edu.selfcheckoutapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class TransactionViewHolder extends RecyclerView.ViewHolder     {

    public TextView txtTscDate, txtTscTime, txtTscID,txtTscAmt;

    public TransactionViewHolder(@NonNull View itemView) {
        super(itemView);

        txtTscDate = itemView.findViewById(R.id.tsc_date);
        txtTscTime = itemView.findViewById(R.id.tsc_time);
        txtTscAmt = itemView.findViewById(R.id.tsc_total);
        txtTscID = itemView.findViewById(R.id.tsc_id);

    }
}
