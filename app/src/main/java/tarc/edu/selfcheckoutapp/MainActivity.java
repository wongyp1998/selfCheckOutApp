package tarc.edu.selfcheckoutapp;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import tarc.edu.selfcheckoutapp.ui.Home.HomeFragment;
import tarc.edu.selfcheckoutapp.ui.Home.LoginFragment;
import tarc.edu.selfcheckoutapp.ui.Home.ProductFragment;


/** Entry activity to select the detection mode. */
public class MainActivity extends AppCompatActivity {

  SpaceNavigationView navigationView;
  final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
  long itemCount = 0;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);



    navigationView = findViewById(R.id.space);


    navigationView.initWithSaveInstanceState(savedInstanceState);
    navigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_home_black_24dp));
    navigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_list_24px));
    navigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_person_24px));
    navigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_shopping_cart_black_24dp));

    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
    getSupportFragmentManager().beginTransaction().addToBackStack(null);



    cartListRef.child("User View")
            .child("013-6067208").addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        itemCount = dataSnapshot.getChildrenCount();
        showBadgeCount(dataSnapshot.getChildrenCount());
      }

      @Override
      public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        itemCount = dataSnapshot.getChildrenCount();
        showBadgeCount(dataSnapshot.getChildrenCount());


      }

      @Override
      public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

//        itemCount = (dataSnapshot.getChildrenCount());
//        showBadgeCount(dataSnapshot.getChildrenCount());



      }

      @Override
      public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        itemCount = dataSnapshot.getChildrenCount();
        showBadgeCount(dataSnapshot.getChildrenCount());

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });







    navigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
      @Override
      public void onCentreButtonClick() {
        Intent i = new Intent(MainActivity.this, LiveBarcodeScanningActivity.class);
        startActivity(i);
        navigationView.setCentreButtonSelectable(true);
      }


      @Override
      public void onItemClick(int itemIndex, String itemName) {
        switch (itemIndex){
          case 0:
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            break;
          case 1:
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProductFragment()).commit();
            break;
          case 2:
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            break;
          case 3:

              getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CartFragment()).commit();
            break;


        }
      }

      @Override
      public void onItemReselected(int itemIndex, String itemName) {
        switch (itemIndex){
          case 0:
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            break;
          case 1:
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProductFragment()).commit();
            break;
          case 2:
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            break;
          case 3:
              getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CartFragment()).commit();

            break;


        }
      }
    });


  }


  public void showBadgeCount(long count){
    final Handler handler = new Handler();
    handler.postDelayed(new Runnable(){
      @Override
      public void run() {
        if(count !=0){
          navigationView.shouldShowFullBadgeText(true);
          navigationView.showBadgeAtIndex(3, (int)count, Color.RED);
        }else{
          navigationView.hideAllBadges();
        }
      }
    }, 200);
  }

}
