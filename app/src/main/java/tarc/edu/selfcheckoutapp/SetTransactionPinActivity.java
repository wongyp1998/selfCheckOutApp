package tarc.edu.selfcheckoutapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tarc.edu.selfcheckoutapp.UtlityClass.LoginPreferenceUtils;

public class SetTransactionPinActivity extends AppCompatActivity {

    final DatabaseReference userListRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_transaction_pin);

        PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration.Builder(SetTransactionPinActivity.this)
                .setMode(PFFLockScreenConfiguration.MODE_CREATE)
                .setTitle("Enter Your New Pin")
                .setCodeLength(6);

        PFFLockScreenConfiguration.Builder builder2 = new PFFLockScreenConfiguration.Builder(SetTransactionPinActivity.this)
                .setMode(PFFLockScreenConfiguration.MODE_CREATE)
                .setTitle("Create Your 6-digit Pin to proceed")
                .setCodeLength(6);


        if(getIntent().hasExtra("newUser"))
        {
            setUpNewPin(builder2);
        }
        else if(getIntent().hasExtra("existingUser"))
        {
            setUpNewPin(builder);

        }


    }


    public void setUpNewPin(PFFLockScreenConfiguration.Builder myBuilder)
    {
        PFLockScreenFragment fragment = new PFLockScreenFragment();
        fragment.setConfiguration(myBuilder.build());
        fragment.setCodeCreateListener(new PFLockScreenFragment.OnPFLockScreenCodeCreateListener() {
            @Override
            public void onCodeCreated(String encodedCode) {
                //TODO: save somewhere;
                if(getIntent().hasExtra("newUser"))
                {
                    userListRef.child("User").child(LoginPreferenceUtils.getPhone(SetTransactionPinActivity.this)).child("tscPin").setValue(encodedCode);
                    Toast.makeText(SetTransactionPinActivity.this, "Transaction Pin Created Succesfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SetTransactionPinActivity.this, HomeActivity.class);
                    startActivity(i);

                }
                else if(getIntent().hasExtra("existingUser"))
                {
                    userListRef.child("User").child(LoginPreferenceUtils.getPhone(SetTransactionPinActivity.this)).child("tscPin").setValue(encodedCode);
                    Toast.makeText(SetTransactionPinActivity.this, "Transaction Pin Updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SetTransactionPinActivity.this, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);

                }

            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.pin_fragment,fragment).commit();
    }


}

