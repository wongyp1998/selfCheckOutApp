package tarc.edu.selfcheckoutapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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

        Picasso.get().load(list.get(position).getImage()).into(holder.circleImageView);
        holder.txtItemName.setText(list.get(position).getProdName());
        holder.txtItemPrice.setText(list.get(position).getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pid = list.get(position).getProdId();
                Intent i = new Intent(view.getContext(),ProductDetailsActivity.class);
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
        TextView txtItemName, txtItemPrice;
        CircleImageView circleImageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtItemName = itemView.findViewById(R.id.product_name);
            txtItemPrice = itemView.findViewById(R.id.product_price);

            circleImageView = itemView.findViewById(R.id.product_image);
        }
    }
}
