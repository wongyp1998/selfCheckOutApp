package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiahuan.svgmapview.SVGMapView;
import com.jiahuan.svgmapview.overlay.SVGMapLocationOverlay;
import com.jiahuan.svgmapview.core.data.SVGPicture;
import com.jiahuan.svgmapview.core.helper.ImageHelper;
import com.jiahuan.svgmapview.core.helper.map.SVGBuilder;
import java.io.IOException;
import java.util.Random;
import tarc.edu.selfcheckoutapp.AssetsHelper;

public class ViewMapActivity extends AppCompatActivity {

    SVGMapView mapView;
    String zoneID,shelfID;
    final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference();
    String productID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);

        Intent ii = getIntent();
        Bundle b = ii.getExtras();

//        if (b != null) {
//            zoneID = String.valueOf(b.get("zoneNo"));
//            shelfID = String.valueOf(b.get("shelfNo"));
//        }


        if (b != null) {
            productID = String.valueOf(b.get("productID"));
        }

        mapView = (SVGMapView) findViewById(R.id.mapView);

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("storemap.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapView.loadMap(bitmap);

        productRef.child("Products").child(productID).child("location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String zoneNo = dataSnapshot.child("zone").getValue(String.class);
                String shelfNo = dataSnapshot.child("shelf").getValue(String.class);

                productRef.child("ShelfLocation").child("Zone_"+zoneNo).child("Shelf_"+shelfNo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String x = dataSnapshot.child("x").getValue(String.class);
                        String y = dataSnapshot.child("y").getValue(String.class);

                        mapView.getController().sparkAtPoint(new PointF(Float.parseFloat(x),Float.parseFloat(y)), 20, R.color.red, 99);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





//        mapView.getController().sparkAtPoint(new PointF(350, 300), 20, R.color.red, 99);

//        SVGMapLocationOverlay locationOverlay = new SVGMapLocationOverlay(mapView);
//        locationOverlay.setIndicatorArrowBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.indicator_arrow));
//        locationOverlay.setPosition(new PointF(750, 880));
//        locationOverlay.setIndicatorCircleRotateDegree(90);
//        locationOverlay.setMode(SVGMapLocationOverlay.MODE_COMPASS);
//        locationOverlay.setIndicatorArrowRotateDegree(-45);
//        mapView.getOverLays().add(locationOverlay);
//        mapView.refresh();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
    }
}
