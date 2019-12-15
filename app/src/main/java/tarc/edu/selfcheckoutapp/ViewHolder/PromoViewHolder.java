package tarc.edu.selfcheckoutapp.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tarc.edu.selfcheckoutapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PromoViewHolder extends RecyclerView.ViewHolder     {

    public TextView txtPromoDesc, txtOldPrice, txtNewPrice,txtPromoTag;
      public CircleImageView circleImageView;

    public PromoViewHolder(@NonNull View itemView) {
        super(itemView);

        txtPromoDesc = itemView.findViewById(R.id.promo_item_desc);
        txtNewPrice = itemView.findViewById(R.id.promo_item_new_price);
        txtOldPrice = itemView.findViewById(R.id.promo_item_old_price);
        txtPromoTag = itemView.findViewById(R.id.home_promo_value);

        circleImageView = itemView.findViewById(R.id.promo_item_image);

    }
}
