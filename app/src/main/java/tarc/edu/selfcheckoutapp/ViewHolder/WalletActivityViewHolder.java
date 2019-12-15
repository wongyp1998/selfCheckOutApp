package tarc.edu.selfcheckoutapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tarc.edu.selfcheckoutapp.R;


public class WalletActivityViewHolder extends RecyclerView.ViewHolder     {

    public TextView wTscDate,wTscAmt,wTscDesc;
    public ImageView debit,credit;

    public WalletActivityViewHolder(@NonNull View itemView) {
        super(itemView);

        wTscDate = itemView.findViewById(R.id.wTsc_date);
        wTscAmt = itemView.findViewById(R.id.wTsc_amt);
        wTscDesc = itemView.findViewById(R.id.wTsc_desc);
        credit = itemView.findViewById(R.id.credit_icon);
        debit = itemView.findViewById(R.id.debit_icon);
    }
}
