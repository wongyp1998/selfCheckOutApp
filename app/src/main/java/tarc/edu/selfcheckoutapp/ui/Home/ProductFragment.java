package tarc.edu.selfcheckoutapp.ui.Home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tarc.edu.selfcheckoutapp.AdapterClass;
import tarc.edu.selfcheckoutapp.Product;
import tarc.edu.selfcheckoutapp.R;



public class ProductFragment extends Fragment {

    private RecyclerView recyclerView;
    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Products");
    private SearchView searchView;
    private ArrayList<Product> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_product, container, false);

        recyclerView  = root.findViewById(R.id.product_list);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        searchView = root.findViewById(R.id.search_view);




        return root;
    }

    public void onStart() {
        super.onStart();

        if(cartListRef != null)
        {
            cartListRef.addValueEventListener(new ValueEventListener() {
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

        if(searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    searchProduct(s);
                    return false;
                }
            });
        }

    }

    private void searchProduct(String str){

        ArrayList<Product> myList = new ArrayList<>();
        for(Product object : list)
        {
            if(object.getProdName().toLowerCase().contains(str.toLowerCase()))
            {
                myList.add(object);
            }
        }
        AdapterClass adapterClass = new AdapterClass(myList);
        recyclerView.setAdapter(adapterClass);



    }



}
