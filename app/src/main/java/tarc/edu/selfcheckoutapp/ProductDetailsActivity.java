package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
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
    private TextView txtPdprice, txtPdweight, txtPdname, txtPlocation;
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

                String pName = dataSnapshot.child("prodName").getValue(String.class);
                String pPrice = dataSnapshot.child("price").getValue(String.class);
                String pWeight = dataSnapshot.child("weight").getValue(String.class);
                String pImage = dataSnapshot.child("image").getValue(String.class);
                String zoneNo = dataSnapshot.child("location").child("zone").getValue(String.class);
                String shelfNo = dataSnapshot.child("location").child("shelf").getValue(String.class);


                txtPdname.setText(pName);
                txtPdprice.setText("RM " + pPrice);
                txtPdweight.setText(pWeight);
                txtPlocation.setText("Zone " + zoneNo + ", " + "Shelf " + shelfNo);
                Picasso.get().load(pImage).into(productImage);


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

//        locationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ProductDetailsActivity.this, ViewMapActivity.class);
//                startActivity(intent);
//
//            }
//        });


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
