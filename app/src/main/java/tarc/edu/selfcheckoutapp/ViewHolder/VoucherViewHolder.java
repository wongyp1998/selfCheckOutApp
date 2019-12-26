package tarc.edu.selfcheckoutapp.ViewHolder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import tarc.edu.selfcheckoutapp.R;

public class VoucherViewHolder extends RecyclerView.ViewHolder{

    public TextView txtVoucherRate,txtVoucherHeader,txtVoucherExpiryDate;
    public LinearLayout expired_view;

    public VoucherViewHolder(@NonNull View itemView) {
        super(itemView);

        txtVoucherExpiryDate = itemView.findViewById(R.id.voucher_expiryDate);
        txtVoucherHeader = itemView.findViewById(R.id.voucher_header);
        txtVoucherRate = itemView.findViewById(R.id.voucher_rate);
        expired_view = itemView.findViewById(R.id.voucher_view);


    }
}