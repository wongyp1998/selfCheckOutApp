package tarc.edu.selfcheckoutapp;

import android.view.View;
import android.widget.TextView;

import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.ml.md.java.Interface.ItemClickListener;
import tarc.edu.selfcheckoutapp.R;
import de.hdodenhof.circleimageview.CircleImageView;



public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtPName, txtPPrice, txtPWeight,txtPQuantity;
    public CircleImageView circleImageView;

    private ItemClickListener itemClickListener;


    public ReviewViewHolder(@NonNull View itemView) {
        super(itemView);

        txtPName = itemView.findViewById(R.id.review_pname);
        txtPPrice = itemView.findViewById(R.id.review_pprice);
        txtPWeight = itemView.findViewById(R.id.review_pweight);
        circleImageView = itemView.findViewById(R.id.review_image);
        txtPQuantity = itemView.findViewById(R.id.review_quantity);



    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);

    }

    public void setItemClickListener(ItemClickListener itemClickListener){

        this.itemClickListener = itemClickListener;
    }





}
