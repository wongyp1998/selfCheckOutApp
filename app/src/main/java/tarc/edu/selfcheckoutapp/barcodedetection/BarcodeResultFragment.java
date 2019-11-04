/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tarc.edu.selfcheckoutapp.barcodedetection;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import tarc.edu.selfcheckoutapp.R;
import tarc.edu.selfcheckoutapp.LiveBarcodeScanningActivity;
import tarc.edu.selfcheckoutapp.Product;
import tarc.edu.selfcheckoutapp.camera.WorkflowModel;
import tarc.edu.selfcheckoutapp.camera.WorkflowModel.WorkflowState;

import com.luseen.spacenavigation.SpaceNavigationView;
import com.squareup.picasso.Picasso;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/** Displays the bottom sheet to present barcode fields contained in the detected barcode. */
public class BarcodeResultFragment extends BottomSheetDialogFragment implements ValueChangedListener {

  private static final String TAG = "BarcodeResultFragment";
  private static final String ARG_BARCODE_FIELD_LIST = "arg_barcode_field_list";
  private Button addToCartButton;
  private NumberPicker numberPicker;
  public static final String SHARED_PREFS = "sharedPrefs";
  public static CircleImageView circleImageView;
  SpaceNavigationView navigationView;
  final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
  private String pname;
  private String pbarcode;
  private String pprice;
  private String pimage;
  private String pweight;

  private ValueChangedListener valueChangedListener;
  private String quantity;


  public static void show(
      FragmentManager fragmentManager, ArrayList<BarcodeField> barcodeFieldArrayList) {
    BarcodeResultFragment barcodeResultFragment = new BarcodeResultFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelableArrayList(ARG_BARCODE_FIELD_LIST, barcodeFieldArrayList);
    barcodeResultFragment.setArguments(bundle);
    barcodeResultFragment.show(fragmentManager, TAG);
  }

  public static void dismiss(FragmentManager fragmentManager) {
    BarcodeResultFragment barcodeResultFragment =
        (BarcodeResultFragment) fragmentManager.findFragmentByTag(TAG);
    if (barcodeResultFragment != null) {
      barcodeResultFragment.dismiss();
    }
  }

//  public static void showImage(String image){
//
//  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater layoutInflater,
      @Nullable ViewGroup viewGroup,
      @Nullable Bundle bundle) {
    View view = layoutInflater.inflate(R.layout.barcode_bottom_sheet, viewGroup);
    ArrayList<BarcodeField> barcodeFieldList;
    Bundle arguments = getArguments();

    circleImageView = (CircleImageView) view.findViewById(R.id.product_image);
    navigationView = (SpaceNavigationView)((Activity)getActivity()).findViewById(R.id.space);

    pimage =((LiveBarcodeScanningActivity)getActivity()).productimage;
    Picasso.get().load(pimage).into(circleImageView);

    if (arguments != null && arguments.containsKey(ARG_BARCODE_FIELD_LIST)) {
      barcodeFieldList = arguments.getParcelableArrayList(ARG_BARCODE_FIELD_LIST);
    } else {
      Log.e(TAG, "No barcode field list passed in!");
      barcodeFieldList = new ArrayList<>();
    }

    RecyclerView fieldRecyclerView = view.findViewById(R.id.barcode_field_recycler_view);
    fieldRecyclerView.setHasFixedSize(true);
    fieldRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    fieldRecyclerView.setAdapter(new BarcodeFieldAdapter(barcodeFieldList));
    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS,0);
    pname = sharedPreferences.getString("pname","");
    pbarcode = sharedPreferences.getString("pbarcode","");
    pprice = sharedPreferences.getString("pprice","");
    pweight = sharedPreferences.getString("pweight","");

    addToCartButton = view.findViewById(R.id.addCartButton);
    numberPicker = view.findViewById(R.id.number_picker);

    quantity = String.valueOf(numberPicker.getValue());
    numberPicker.setValueChangedListener(new ValueChangedListener() {
     @Override
     public void valueChanged(int value, ActionEnum action) {

       quantity = String.valueOf(value);
     }
   });


    addToCartButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        addingToCartList();


      }
    });



    return view;
  }

  @Override
  public void onDismiss(@NonNull DialogInterface dialogInterface) {
    if (getActivity() != null) {
      // Back to working state after the bottom sheet is dismissed.
      ViewModelProviders.of(getActivity())
          .get(WorkflowModel.class)
          .setWorkflowState(WorkflowState.DETECTING);
    }
    super.onDismiss(dialogInterface);
  }

  public void valueChanged(int value, ActionEnum action){
    String actionText = action == ActionEnum.MANUAL ? "manually set" : (action == ActionEnum.INCREMENT ? "incremented" : "decremented");
    String message = String.format("NumberPicker is %s to %d", actionText, value);
    Log.v(this.getClass().getSimpleName(), message);
  }

  public void addingToCartList(){
    String saveCurrentTime, saveCurrentDate;

    Calendar calForDate = Calendar.getInstance();
    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
    saveCurrentDate = currentDate.format(calForDate.getTime());

    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
    saveCurrentTime = currentTime.format(calForDate.getTime());


    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

    final HashMap<String, Object> cartMap = new HashMap<>();
    cartMap.put("pid",pbarcode);
    cartMap.put("pname",pname);
    cartMap.put("price",pprice);
    cartMap.put("weight",pweight);
    cartMap.put("imageRef",pimage);
    cartMap.put("date",saveCurrentDate);
    cartMap.put("time",saveCurrentTime);
    cartMap.put("quantity",quantity);

    cartListRef.child("User View").child("013-6067208")
            .child("Products").child(pbarcode)
            .updateChildren(cartMap)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
//                  Toast.makeText(getActivity(), "Added to Cart", Toast.LENGTH_SHORT).show();
//                  dismiss();
                  cartListRef.child("Admin View").child("013-6067208")
                          .child("Products").child(pbarcode)
                          .updateChildren(cartMap)
                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful())
                              {
                                Toast.makeText(getActivity(), "Added to Cart", Toast.LENGTH_SHORT).show();
                                dismiss();
                              }
                            }
                          });

                }
              }
            });
  }

}
