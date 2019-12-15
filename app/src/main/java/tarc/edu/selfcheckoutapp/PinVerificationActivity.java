package tarc.edu.selfcheckoutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;
import com.beautycoder.pflockscreen.security.PFResult;
import com.beautycoder.pflockscreen.viewmodels.PFPinCodeViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tarc.edu.selfcheckoutapp.UtlityClass.LoginPreferenceUtils;

public class PinVerificationActivity extends AppCompatActivity {

    final DatabaseReference userListRef = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_verification);

        final PFFLockScreenConfiguration.Builder builder1 = new PFFLockScreenConfiguration.Builder(this)
                .setTitle("Please enter your current 6-digit PIN")
                .setCodeLength(6)
                .setLeftButton("Can't remember")
                .setUseFingerprint(false);

        final PFFLockScreenConfiguration.Builder builder2 = new PFFLockScreenConfiguration.Builder(this)
                .setTitle("Verify yourself with pin code or fingerprint to proceed")
                .setCodeLength(6)
                .setLeftButton("Can't remember")
                .setUseFingerprint(true);

        if(getIntent().hasExtra("updateKey")) {
            showLockScreenFragment(mLoginListener2,builder1);
        }
        else if(getIntent().hasExtra("verifyKey"))
        {
            showLockScreenFragment(mLoginListener,builder2);

        }






    }

    private final PFLockScreenFragment.OnPFLockScreenLoginListener mLoginListener =
            new PFLockScreenFragment.OnPFLockScreenLoginListener() {

                @Override
                public void onCodeInputSuccessful() {
                    Toast.makeText(PinVerificationActivity.this, "Verified successfull", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK,resultIntent);
                    finish();

                }

                @Override
                public void onFingerprintSuccessful() {
                    Toast.makeText(PinVerificationActivity.this, "Verified successfully", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK,resultIntent);
                    finish();
                }

                @Override
                public void onPinLoginFailed() {
                    Toast.makeText(PinVerificationActivity.this, "Invalid PIN", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFingerprintLoginFailed() {
                    Toast.makeText(PinVerificationActivity.this, "Invalid Fingerprint", Toast.LENGTH_SHORT).show();
                }
            };

    private final PFLockScreenFragment.OnPFLockScreenLoginListener mLoginListener2 =
            new PFLockScreenFragment.OnPFLockScreenLoginListener() {

                @Override
                public void onCodeInputSuccessful() {
                    Toast.makeText(PinVerificationActivity.this, "Verified successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PinVerificationActivity.this,SetTransactionPinActivity.class);
                    intent.putExtra("existingUser","existing");
                    startActivity(intent);

                }

                @Override
                public void onFingerprintSuccessful() {
                    Toast.makeText(PinVerificationActivity.this, "Verified successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PinVerificationActivity.this,SetTransactionPinActivity.class);
                    intent.putExtra("existingUser","existing");
                    startActivity(intent);
                }

                @Override
                public void onPinLoginFailed() {
                    Toast.makeText(PinVerificationActivity.this, "Invalid PIN", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFingerprintLoginFailed() {
                    Toast.makeText(PinVerificationActivity.this, "Invalid Fingerprint", Toast.LENGTH_SHORT).show();
                }
            };

    private void showLockScreenFragment(PFLockScreenFragment.OnPFLockScreenLoginListener myListener,PFFLockScreenConfiguration.Builder mybuilder) {
        new PFPinCodeViewModel().isPinCodeEncryptionKeyExist().observe(
                this,
                new Observer<PFResult<Boolean>>() {
                    @Override
                    public void onChanged(@Nullable PFResult<Boolean> result) {
                        if (result == null) {
                            return;
                        }
                        if (result.getError() != null) {
                            Toast.makeText(PinVerificationActivity.this, "Can not get pin code info", Toast.LENGTH_SHORT).show();
                            return;
                        }

                            showLockScreenFragment(result.getResult(), myListener,mybuilder);

                    }
                }
        );
    }

    private void showLockScreenFragment(boolean isPinExist,PFLockScreenFragment.OnPFLockScreenLoginListener listener, PFFLockScreenConfiguration.Builder builder) {
        final PFLockScreenFragment fragment = new PFLockScreenFragment();

        fragment.setOnLeftButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PinVerificationActivity.this, "Left button pressed", Toast.LENGTH_LONG).show();
            }
        });

        builder.setMode(isPinExist
                ? PFFLockScreenConfiguration.MODE_AUTH
                : PFFLockScreenConfiguration.MODE_CREATE);
        if (isPinExist) {
            userListRef.child("User").child(LoginPreferenceUtils.getPhone(PinVerificationActivity.this)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String encodedPin = dataSnapshot.child("tscPin").getValue(String.class);
                    fragment.setEncodedPinCode(encodedPin);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            fragment.setLoginListener(listener);
        }

        fragment.setConfiguration(builder.build());

        getSupportFragmentManager().beginTransaction().replace(R.id.pinVerify_container,fragment).commit();


    }


}
