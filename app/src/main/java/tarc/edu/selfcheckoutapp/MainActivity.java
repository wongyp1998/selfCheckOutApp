package tarc.edu.selfcheckoutapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import tarc.edu.selfcheckoutapp.UtlityClass.LoginPreferenceUtils;
import tarc.edu.selfcheckoutapp.ui.Home.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(LoginPreferenceUtils.getLoggedStatus(getApplicationContext()) ==true)
        {
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new LoginFragment()).commit();
            getSupportFragmentManager().beginTransaction().addToBackStack(null);
        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to close this application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }
}
