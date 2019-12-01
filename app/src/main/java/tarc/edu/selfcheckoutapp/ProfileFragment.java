package tarc.edu.selfcheckoutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {

    private Button tsc_btn;
    private Button wallet_btn;
    private Button security_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_profile, container, false);

        tsc_btn = root.findViewById(R.id.tsc_btn);
        wallet_btn = root.findViewById(R.id.wallet_btn);
        security_btn = root.findViewById(R.id.security_btn);



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
                Intent intent = new Intent(getActivity(),eWalletActivity.class);
                startActivity(intent);
            }
        });

        security_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SetTransactionPinActivity.class);
                startActivity(intent);



            }
        });





        return root;

    }
}
