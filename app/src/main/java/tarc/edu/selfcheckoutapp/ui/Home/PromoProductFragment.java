package tarc.edu.selfcheckoutapp.ui.Home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tarc.edu.selfcheckoutapp.Model.Product;
import tarc.edu.selfcheckoutapp.R;
import tarc.edu.selfcheckoutapp.UtlityClass.AdapterClass;


public class PromoProductFragment extends Fragment {

    private RecyclerView recyclerView;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Products");
    private ArrayList<Product> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_promo_product, container, false);

        recyclerView  = root.findViewById(R.id.product_list);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);


        return root;
    }

    public void onStart() {
        super.onStart();

        if(cartListRef != null)
        {
            cartListRef.orderByChild("Discount").startAt(0.01).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        list = new ArrayList<>();
                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            list.add(ds.getValue(Product.class));

                        }
                        AdapterClass adapterClass = new AdapterClass(list);
                        recyclerView.setAdapter(adapterClass);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });

        }


    }

    public void searchProduct(String str){

        ArrayList<Product> myList = new ArrayList<>();
        for(Product object : list)
        {
            if(object.getName().toLowerCase().contains(str.toLowerCase()))
            {
                myList.add(object);
            }
        }
        AdapterClass adapterClass = new AdapterClass(myList);
        recyclerView.setAdapter(adapterClass);



    }







}
