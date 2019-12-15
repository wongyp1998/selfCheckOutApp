package tarc.edu.selfcheckoutapp.ui.Home;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import tarc.edu.selfcheckoutapp.PinVerificationActivity;
import tarc.edu.selfcheckoutapp.R;


public class RegisterFragment extends Fragment {

    private TextInputEditText nameTxt,emailTxt,phoneTxt,passwordTxt;
    private static final String TAG = "EmailPassword";
    private ProgressDialog loadingbar;
    final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
    private Button registerBtn;
    private TextView logintxt;
    private FirebaseAuth mAuth;
    private TextInputLayout field1,field2,field3,field4;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);

        nameTxt = root.findViewById(R.id.userName);
        emailTxt = root.findViewById(R.id.userEmail);
        phoneTxt = root.findViewById(R.id.userHpNo);
        passwordTxt = root.findViewById(R.id.userPassword);
        loadingbar = new ProgressDialog(getActivity());
        registerBtn = root.findViewById(R.id.registerButton);
        logintxt = root.findViewById(R.id.loginText);
        mAuth = FirebaseAuth.getInstance();
        field1 = root.findViewById(R.id.name_field);
        field2 = root.findViewById(R.id.email_field);
        field3 = root.findViewById(R.id.hpNo_field);
        field4 = root.findViewById(R.id.password_field);
        mAuth = FirebaseAuth.getInstance();



        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAcc();
            }
        });

        logintxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginFragment();
            }
        });

        return root;
    }

    private void createAcc()
    {
        String name = nameTxt.getText().toString();
        String email = emailTxt.getText().toString();
        String phone = phoneTxt.getText().toString();
        String password = passwordTxt.getText().toString();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable == nameTxt.getEditableText())
                {
                    field1.setError(null);
                }
                else if(editable == emailTxt.getEditableText())
                {
                    field2.setError(null);
                }
                else if(editable == phoneTxt.getEditableText())
                {
                    field3.setError(null);
                }
                else if(editable == passwordTxt.getEditableText())
                {
                    field4.setError(null);
                }

            }
        };

        nameTxt.addTextChangedListener(textWatcher);
        emailTxt.addTextChangedListener(textWatcher);
        phoneTxt.addTextChangedListener(textWatcher);
        passwordTxt.addTextChangedListener(textWatcher);


        boolean allValid = true;

        if(TextUtils.isEmpty(name))
        {
            field1.setError("Field cannot be empty.");
            allValid = false;
        }
        if (TextUtils.isEmpty(email))
        {
            field2.setError("Field cannot be empty.");
            allValid = false;

        }
        if(TextUtils.isEmpty(phone))
        {
            field3.setError("Field cannot be empty.");
            allValid=false;

        }
        if(TextUtils.isEmpty(password))
        {
            field4.setError("Field cannot be empty.");
            allValid = false;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            field2.setError("Please enter a valid email.");
            allValid = false;

        }
        if(password.length()<6 && !TextUtils.isEmpty(password))
        {
            field4.setError("Password is too short. A minimum of 6 characters is required.");
            allValid = false;
        }
        if(allValid)
        {
            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("Verifying your credentials...");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            validateCredentials(name,email,phone,password);
        }else
        {

        }

    }

    private void validateCredentials(String name,String email,String hpNo, String password)
    {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("User").child(hpNo).exists()))
                {
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                Log.d(TAG,"Account created successfully!");
//                                FirebaseUser user = mAuth.getCurrentUser();

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("phone",hpNo);
                                userMap.put("user_email",email);
                                userMap.put("user_name",name);
//                    userMap.put("password",password);
                                userMap.put("tscPin","null");
                                userMap.put("balance",0);

                                userRef.child("User").child(hpNo).updateChildren(userMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(getActivity(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                                                    loadingbar.dismiss();
                                                    startLoginFragment();



                                                }
                                                else
                                                {
                                                    loadingbar.dismiss();
                                                    Toast.makeText(getActivity(), "Failed to create account!", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                            }
                            else {
                                loadingbar.dismiss();
                                Toast.makeText(getActivity(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();


                            }

                        }
                    });






                }
                else
                {
                    loadingbar.dismiss();
                    Toast.makeText(getActivity(), "Your phone number is already registered.", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void startLoginFragment()
    {

        LoginFragment loginFragment = new LoginFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), loginFragment,"findThisFragment")
                .addToBackStack(null)
                .commit();

    }
}
