package tarc.edu.selfcheckoutapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Context;
import com.google.android.material.textfield.TextInputEditText;
import com.msg91.sendotpandroid.library.SendOTPConfig;
import com.msg91.sendotpandroid.library.SendOtpVerification;
import com.msg91.sendotpandroid.library.Verification;
import com.msg91.sendotpandroid.library.VerificationListener;


public class OTPActivity extends AppCompatActivity {


    private static final String TAG = "OTPActivity";
    private String tID;
    private Button verfiyBtn,sendotpBtn;
    private TextInputEditText otptxt;
    private Verification mVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        verfiyBtn = findViewById(R.id.verify_btn);
        sendotpBtn = findViewById(R.id.sendotp_btn);

        tID = getIntent().getStringExtra("tscID");

        otptxt = findViewById(R.id.otp_value);

        VerificationListener verificationListener = new VerificationListener() {
            @Override
            public void onInitiated(String response) {
                Log.d(TAG, "Initialized!" + response);
                //OTP successfully resent/sent.
            }

            @Override
            public void onInitiationFailed(Exception paramException) {
                Log.e(TAG, "Verification initialization failed: " + paramException.getMessage());
                //sending otp failed.
            }

            @Override
            public void onVerified(String response) {
                Log.d(TAG, "Verified!\n" + response);
                //OTP verified successfully.
            }

            @Override
            public void onVerificationFailed(Exception paramException) {
                Log.e(TAG, "Verification failed: " + paramException.getMessage());
                //OTP  verification failed.
            }
        };

        SendOTPConfig otpConfig = SendOtpVerification
                .config("60136067208")
                .context(this)
                .httpsConnection(false)
                .autoVerification(false)
                .unicode(false)
                .expiry("5")
                .senderId("ABCDE")
                .otplength("6")
                .otp("1234")
                .message("1234 is Your verification digits")
                .build();

        mVerification = SendOtpVerification.createSmsVerification(otpConfig,verificationListener);


//        mVerification = SendOtpVerification.createSmsVerification
//                (SendOtpVerification
//                        .config("+60136067208")
//                        .context(this.getApplicationContext())
//                        .unicode(false) // set true if you want to use unicode in sms
//                        .autoVerification(false)
//                        .httpsConnection(false)//use false currently https is under maintenance
//                        .expiry("5")//value in minutes
//                        .senderId("11111") //where XXXX is any string
//                        .otplength("4") //length of your otp max length up to 9 digits
//                        //--------case 1-------------------
////                        .message("##OTP## is Your verification digits.")//##OTP## use for default generated OTP
////                        //--------case 2-------------------
//                        .otp("1234")// Custom Otp code, if want to add yours
//                        .message("1234 is Your verification digits.")//Here 1234 same as above Custom otp.
//                        //-------------------------------------
//                        //use single case at a time either 1 or 2
//                        .build(),verificationListener);


        sendotpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVerification.initiate();
            }
        });




        verfiyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }













}
