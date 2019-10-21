package tarc.edu.selfcheckoutapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tarc.edu.selfcheckoutapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PromoViewHolder extends RecyclerView.ViewHolder     {

    public TextView txtPromoDesc, txtOldPrice, txtNewPrice;
    public CircleImageView circleImageView;

    public PromoViewHolder(@NonNull View itemView) {
        super(itemView);

        txtPromoDesc = itemView.findViewById(R.id.promo_item_desc);
        txtNewPrice = itemView.findViewById(R.id.promo_item_new_price);
        txtOldPrice = itemView.findViewById(R.id.promo_item_old_price);

        circleImageView = itemView.findViewById(R.id.promo_item_image);

    }
}
