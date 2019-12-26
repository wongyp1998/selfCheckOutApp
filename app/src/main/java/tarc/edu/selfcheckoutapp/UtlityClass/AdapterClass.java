package tarc.edu.selfcheckoutapp.UtlityClass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import tarc.edu.selfcheckoutapp.Model.Product;
import tarc.edu.selfcheckoutapp.ProductDetailsActivity;
import tarc.edu.selfcheckoutapp.R;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.MyViewHolder>{

    ArrayList <Product> list;
    private Context context;
    public AdapterClass(ArrayList<Product> list){

        this.list = list;

    }

    public AdapterClass(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Picasso.get().load(list.get(position).getImage()).fit().centerCrop().into(holder.circleImageView);
        holder.txtItemName.setText(list.get(position).getName());
        Double oldPrice = list.get(position).getPrice();
        holder.txtItemPrice.setText("RM"+String.format("%.2f",oldPrice));
        Double value = list.get(position).getDiscount();
        Double newPrice = oldPrice * (1-value);


        if(value>0) {
            holder.dscLayout.setVisibility(View.VISIBLE);
            holder.txtDscTag.setText("-"+String.format("%.0f",value*100)+"%");
            holder.txtItemNewPrice.setVisibility(View.VISIBLE);
            holder.txtItemPrice.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.txtItemPrice.setPaintFlags(holder.txtItemPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtItemNewPrice.setText("RM"+String.format("%.2f",newPrice));

        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pid = list.get(position).getBarcode();
                Intent i = new Intent(view.getContext(), ProductDetailsActivity.class);
                i.putExtra("pID",pid);
                view.getContext().startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtItemName, txtItemPrice,txtDscTag,txtItemNewPrice;
        CircleImageView circleImageView;
        LinearLayout dscLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtItemName = itemView.findViewById(R.id.product_name);
            txtItemPrice = itemView.findViewById(R.id.product_price);
            txtDscTag = itemView.findViewById(R.id.dsc_percentage);
            dscLayout = itemView.findViewById(R.id.discount_tag);
            txtItemNewPrice = itemView.findViewById(R.id.product_newPrice);

            circleImageView = itemView.findViewById(R.id.product_image);
        }
    }
}
