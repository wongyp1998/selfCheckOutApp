package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ViewReceiptActivity extends AppCompatActivity {

    private ListView lv;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference();
    FirebaseListAdapter adapter;
    private LinearLayout llScroll;
    private TextView txtReceiptSub, txtReceiptDisc, txtReceiptTotal;
    private Bitmap bitmap, bitmap2;
    private Button btn,btn1;
    private String transactionID;
    private String TAG ="GenerateQrCode";
    private ImageView qrImg;
    QRGEncoder qrgEncoder;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.digital_receipt_layout);

        lv = (ListView)findViewById(R.id.receipt_list);
        llScroll = findViewById(R.id.llScroll);


        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }





            if(getIntent().hasExtra("uniqueID")) {
                transactionID = getIntent().getStringExtra("uniqueID");
            }
            else if(getIntent().hasExtra("uniqueID2")){
                transactionID = getIntent().getStringExtra("uniqueID2");
            }






        cartListRef.child("Transaction").child("013-6067208").child(transactionID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Double Subtotal = dataSnapshot.child("subtotal").getValue(Double.class);
                Double Total = dataSnapshot.child("total").getValue(Double.class);
                String Discount = dataSnapshot.child("discountValue").getValue(String.class);


                txtReceiptSub.setText(String.format("%.2f",Subtotal));
                txtReceiptDisc.setText(Discount);
                txtReceiptTotal.setText(String.format("%.2f", Total));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        btn = findViewById(R.id.btn_view_pdf);
        btn1 = findViewById(R.id.btn_continue);
        txtReceiptSub = findViewById(R.id.receipt_subtotal);
        txtReceiptDisc = findViewById(R.id.receipt_discount);
        txtReceiptTotal = findViewById(R.id.receipt_total);
        qrImg = findViewById(R.id.qrImage);


        Query query = cartListRef.child("Transaction").child("013-6067208").child(transactionID).child("product");
        FirebaseListOptions<Cart> options = new FirebaseListOptions.Builder<Cart>()
                .setLayout(R.layout.receipt_adapter_view_layout)
                .setQuery(query,Cart.class)
                .build();

        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Object model, int position) {

                TextView pname = v.findViewById(R.id.pname);
                TextView quantity = v.findViewById(R.id.quantity);
                TextView pprice = v.findViewById(R.id.pprice);
                TextView ptotal = v.findViewById(R.id.ptotal);
                Cart cart = (Cart)model;

                pname.setText(cart.getPname().toString());
                quantity.setText(cart.getQuantity().toString());
                pprice.setText(cart.getPrice().toString());
                float total = Float.parseFloat(cart.getPrice()) * Float.parseFloat(cart.getQuantity());
                ptotal.setText(String.format("%.2f", total));


            }
        };

        lv.setAdapter(adapter);



        generateQR();




        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("size"," "+llScroll.getWidth() +"  "+llScroll.getWidth());
                bitmap = loadBitmapFromView(llScroll, llScroll.getWidth(), llScroll.getHeight());
                createPdf();

            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewReceiptActivity.this, MainActivity.class);
                startActivity(i);
            }
        });


    }

    public void generateQR(){
        if(transactionID.length()>0){
            WindowManager manager = (WindowManager)getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int dimens = width<height ? width:height;
            dimens = dimens*3/4;
            qrgEncoder = new QRGEncoder(transactionID,null, QRGContents.Type.TEXT,dimens);

            try{
                bitmap2 = qrgEncoder.encodeAsBitmap();
                qrImg.setImageBitmap(bitmap2);

            }
            catch (WriterException e){
                Log.v(TAG,e.toString());
            }
        }else {
            Toast.makeText(ViewReceiptActivity.this,
                    "Empty!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private void createPdf(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHeight = (int) height, convertWidth = (int) width;


        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // write the document content
        String targetPdf = "Receipt.pdf";
        File filePath;
        filePath = new File(getExternalFilesDir(null).getAbsolutePath() + File.separator + targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();

        openGeneratedPDF();

    }

    private void openGeneratedPDF(){
        File file = new File(getExternalFilesDir(null).getAbsolutePath() + File.separator + "Receipt.pdf");
        if (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try
            {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(ViewReceiptActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ViewReceiptActivity.this, MainActivity.class);
        startActivity(i);
    }
}
