package tarc.edu.selfcheckoutapp.ViewHolder;

import android.view.View;
import android.widget.TextView;
import com.travijuu.numberpicker.library.NumberPicker;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.ml.md.java.Interface.ItemClickListener;
import tarc.edu.selfcheckoutapp.R;
import de.hdodenhof.circleimageview.CircleImageView;



public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtPName, txtPPrice, txtPWeight,txtDiscountPrice;
    public CircleImageView circleImageView;
    public NumberPicker numberPicker;

    private ItemClickListener itemClickListener;


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtPName = itemView.findViewById(R.id.cart_list_pname);
        txtPPrice = itemView.findViewById(R.id.cart_item_oldPrice);
        txtDiscountPrice = itemView.findViewById(R.id.cart_item_newPrice);
        txtPWeight = itemView.findViewById(R.id.cart_list_weight);
        circleImageView = itemView.findViewById(R.id.cart_item_image);
        numberPicker = itemView.findViewById(R.id.cart_number_picker);



    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

    public void setItemClickListener(ItemClickListener itemClickListener){

        this.itemClickListener = itemClickListener;
    }





}
