package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button locationBtn;
    private TextView txtPdprice, txtPdweight, txtPdname, txtPlocation,txtPDesc,txtPStatus,txtPnewPrice;
    final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference();
    private CircleImageView productImage;
    private String prodID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        locationBtn = findViewById(R.id.location_btn);
        txtPdname = findViewById(R.id.detail_name);
        txtPdprice = findViewById(R.id.detail_price);
        txtPdweight = findViewById(R.id.weight_txt);
        productImage = findViewById(R.id.detail_image);
        txtPlocation = findViewById(R.id.location_txt);
        txtPDesc = findViewById(R.id.description_txt);
        txtPStatus = findViewById(R.id.product_stock_status);
        txtPnewPrice = findViewById(R.id.detail_newPrice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.item_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        Intent ii = getIntent();
        Bundle b = ii.getExtras();

        if (b != null) {
            prodID = String.valueOf(b.get("pID"));
        }



        productRef.child("Products").child(prodID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String pName = dataSnapshot.child("Name").getValue(String.class);
                Double pPrice = dataSnapshot.child("Price").getValue(Double.class);
                String pWeight = dataSnapshot.child("Weight").getValue(String.class);
                String pImage = dataSnapshot.child("Image").getValue(String.class);
                String zoneNo = dataSnapshot.child("Location").child("Zone").getValue(String.class);
                String shelfNo = dataSnapshot.child("Location").child("Shelf").getValue(String.class);
                String desc = dataSnapshot.child("Desc").getValue(String.class);
                Integer currentStock = dataSnapshot.child("CurrentStock").getValue(Integer.class);


                txtPStatus.setBackgroundResource(R.drawable.stock_status_background);
                GradientDrawable drawable = (GradientDrawable) txtPStatus.getBackground();

                if(currentStock == 0)
                {
                    txtPStatus.setText("Out of Stock");
//                    txtPStatus.setBackgroundColor(Color.parseColor("#00cc00"));
                    drawable.setColor(Color.parseColor("#FF0000"));



                }else if(currentStock>0 && currentStock<=50)
                {
                    txtPStatus.setText("Low Stock");
//                    txtPStatus.setBackgroundColor(Color.parseColor("#FFA500"));
                    drawable.setColor(Color.parseColor("#FF8C00"));

                }else
                {
                    txtPStatus.setText("In Stock");
//                    txtPStatus.setBackgroundColor(Color.parseColor("#00FF00"));
                    drawable.setColor(Color.parseColor("#00e500"));


                }




                txtPdname.setText(pName);
                txtPdprice.setText("RM " + String.format("%.2f",pPrice));
                txtPdweight.setText(pWeight);
                txtPlocation.setText("Zone " + zoneNo + ", " + "Shelf " + shelfNo);
                txtPDesc.setText(desc);
                Picasso.get().load(pImage).into(productImage);

                Double value = dataSnapshot.child("Discount").getValue(Double.class);
                Double newPrice = pPrice * (1-value);


                if(value>0) {
                    txtPnewPrice.setVisibility(View.VISIBLE);
                    txtPdprice.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    txtPdprice.setTextSize(14);
                    txtPdprice.setPaintFlags(txtPdprice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                    txtPnewPrice.setText("RM"+String.format("%.2f",newPrice));

                }


                locationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(ProductDetailsActivity.this, ViewMapActivity.class);
                        i.putExtra("productID",prodID);
                        startActivity(i);

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
