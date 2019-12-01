package tarc.edu.selfcheckoutapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tarc.edu.selfcheckoutapp.ui.Home.HomeFragment;

public class SetTransactionPinActivity extends AppCompatActivity {

    final DatabaseReference userListRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_transaction_pin);

        PFLockScreenFragment fragment = new PFLockScreenFragment();
        PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration.Builder(SetTransactionPinActivity.this)
                .setMode(PFFLockScreenConfiguration.MODE_CREATE);
        fragment.setConfiguration(builder.build());
        fragment.setCodeCreateListener(new PFLockScreenFragment.OnPFLockScreenCodeCreateListener() {
            @Override
            public void onCodeCreated(String encodedCode) {
                //TODO: save somewhere;
                userListRef.child("User").child("0136067208").child("tscPin").setValue(encodedCode);
                Toast.makeText(SetTransactionPinActivity.this, "Transaction Pin Created Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.pin_fragment,fragment).commit();
    }
}
