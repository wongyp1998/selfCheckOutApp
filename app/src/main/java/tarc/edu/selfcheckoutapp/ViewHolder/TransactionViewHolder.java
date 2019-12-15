package tarc.edu.selfcheckoutapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import tarc.edu.selfcheckoutapp.R;


public class TransactionViewHolder extends RecyclerView.ViewHolder     {

    public TextView txtTscDate, txtTscTime, txtTscID,txtTscAmt,txtTscStatus;
    public LinearLayout status_layout;

    public TransactionViewHolder(@NonNull View itemView) {
        super(itemView);

        txtTscDate = itemView.findViewById(R.id.tsc_date);
        txtTscTime = itemView.findViewById(R.id.tsc_time);
        txtTscAmt = itemView.findViewById(R.id.tsc_total);
        txtTscID = itemView.findViewById(R.id.tsc_id);
        txtTscStatus = itemView.findViewById(R.id.tsc_status);
        status_layout = itemView.findViewById(R.id.status_layout);


    }
}
