package tarc.edu.selfcheckoutapp.ui.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tarc.edu.selfcheckoutapp.UtlityClass.LoginPreferenceUtils;
import tarc.edu.selfcheckoutapp.MainActivity;
import tarc.edu.selfcheckoutapp.PinVerificationActivity;
import tarc.edu.selfcheckoutapp.R;
import tarc.edu.selfcheckoutapp.TransactionHistoryActivity;
import tarc.edu.selfcheckoutapp.ViewVoucherActivty;
import tarc.edu.selfcheckoutapp.eWalletActivity;

public class ProfileFragment extends Fragment {

    private Button tsc_btn;
    private Button wallet_btn;
    private Button security_btn;
    private Button signout_btn;
    private Button voucher_btn;
    private TextView accName,accEmail,accHpNo;
    final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_profile, container, false);
        tsc_btn = root.findViewById(R.id.tsc_btn);
        wallet_btn = root.findViewById(R.id.wallet_btn);
        security_btn = root.findViewById(R.id.security_btn);
        accName = root.findViewById(R.id.profile_name);
        accEmail = root.findViewById(R.id.profile_email);
        accHpNo = root.findViewById(R.id.profile_hp);
        signout_btn = root.findViewById(R.id.signOut_btn);
        voucher_btn = root.findViewById(R.id.voucher_btn);
        mAuth = FirebaseAuth.getInstance();

        userRef.child("User").child(LoginPreferenceUtils.getPhone(getActivity())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                accName.setText(dataSnapshot.child("user_name").getValue(String.class));
                accEmail.setText(dataSnapshot.child("user_email").getValue(String.class));
                accHpNo.setText(dataSnapshot.child("phone").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        tsc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });

        wallet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), eWalletActivity.class);
                startActivity(intent);
            }
        });

        security_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PinVerificationActivity.class);
                intent.putExtra("updateKey","key1");
                startActivity(intent);



            }
        });

        signout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginPreferenceUtils.clear(getActivity());
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

            }
        });

        voucher_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), ViewVoucherActivty.class);
                startActivity(intent);

            }
        });





        return root;

    }
}
