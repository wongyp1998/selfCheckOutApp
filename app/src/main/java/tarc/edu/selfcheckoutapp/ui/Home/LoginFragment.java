package tarc.edu.selfcheckoutapp.ui.Home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tarc.edu.selfcheckoutapp.ForgotPasswordActivity;
import tarc.edu.selfcheckoutapp.HomeActivity;
import tarc.edu.selfcheckoutapp.Model.Transaction;
import tarc.edu.selfcheckoutapp.PinVerificationActivity;
import tarc.edu.selfcheckoutapp.SetTransactionPinActivity;
import tarc.edu.selfcheckoutapp.UtlityClass.LoginPreferenceUtils;
import tarc.edu.selfcheckoutapp.R;
import tarc.edu.selfcheckoutapp.Model.User;

public class LoginFragment extends Fragment {

    private TextInputEditText loginIdentityTxt,loginPasswordTxt;
    private TextInputLayout field1,field2;
    private static final String TAG = "EmailPassword";
    private Button signInBtn;
    private TextView signupTxt,forgotPasswordBtn;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        loginIdentityTxt = root.findViewById(R.id.loginIdentity);
        loginPasswordTxt = root.findViewById(R.id.loginPassword);
        signInBtn = root.findViewById(R.id.signIn_btn);
        signupTxt = root.findViewById(R.id.signupText);
        loadingBar = new ProgressDialog(getActivity());
        field1 = root.findViewById(R.id.identity_field);
        field2 = root.findViewById(R.id.password_field);
        forgotPasswordBtn = root.findViewById(R.id.forgot_password_btn);
        mAuth = FirebaseAuth.getInstance();

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegisterFragment();
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ForgotPasswordActivity.class);
                startActivity(i);

            }
        });

        return root;
    }

    private void userLogin()
    {
        String identity = loginIdentityTxt.getText().toString();
        String password = loginPasswordTxt.getText().toString();

        loginIdentityTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                field1.setError(null);

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        loginPasswordTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                field2.setError(null);
            }
        });

        if(TextUtils.isEmpty(identity)&& TextUtils.isEmpty(password))
        {

            field1.setError("Field cannot be empty");
            field2.setError("Field cannot be empty");

        }
        else if (TextUtils.isEmpty(identity))
        {
            field1.setError("Field cannot be empty");


        }
        else if(TextUtils.isEmpty(password))
        {
            field2.setError("Field cannot be empty");

        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Authenticating...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            loginAccount(identity,password);

        }

    }

    private void loginAccount(String idty, String pw)
    {
        mAuth.signInWithEmailAndPassword(idty,pw)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Log.d(TAG, "signInWithEmail:success");

                            userRef.child("User").orderByChild("user_email").equalTo(idty).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    User user = dataSnapshot.getValue(User.class);
                                    String pin = user.getTscPin();
                                    String phone = user.getPhone();
                                    LoginPreferenceUtils.savePhone(phone,getActivity());
                                    LoginPreferenceUtils.setLoggedInStatus(getActivity(),true);
                                    if(pin.equals("null"))
                                    {
                                        Intent i = new Intent(getActivity(), SetTransactionPinActivity.class);
                                        i.putExtra("newUser","new");
                                        startActivity(i);
                                        Toast.makeText(getActivity(), LoginPreferenceUtils.getPhone(getActivity()),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Intent i = new Intent(getActivity(), HomeActivity.class);
                                        startActivity(i);
                                    }
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                        else {
                            loadingBar.dismiss();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });


    }

    private void startRegisterFragment()
    {

        RegisterFragment registerFragment = new RegisterFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), registerFragment,"findThisFragment")
                .addToBackStack(null)
                .commit();

    }



}
