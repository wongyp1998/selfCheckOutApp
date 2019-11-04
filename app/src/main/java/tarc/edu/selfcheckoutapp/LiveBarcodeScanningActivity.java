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

package tarc.edu.selfcheckoutapp;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.common.base.Objects;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import tarc.edu.selfcheckoutapp.R;
import tarc.edu.selfcheckoutapp.camera.GraphicOverlay;
import tarc.edu.selfcheckoutapp.camera.WorkflowModel;
import tarc.edu.selfcheckoutapp.camera.WorkflowModel.WorkflowState;
import tarc.edu.selfcheckoutapp.barcodedetection.BarcodeField;
import tarc.edu.selfcheckoutapp.barcodedetection.BarcodeProcessor;
import tarc.edu.selfcheckoutapp.barcodedetection.BarcodeResultFragment;
import tarc.edu.selfcheckoutapp.camera.CameraSource;
import tarc.edu.selfcheckoutapp.camera.CameraSourcePreview;
import tarc.edu.selfcheckoutapp.settings.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.travijuu.numberpicker.library.NumberPicker;
import tarc.edu.selfcheckoutapp.MainActivity;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/** Demonstrates the barcode scanning workflow using camera preview. */
public class LiveBarcodeScanningActivity extends AppCompatActivity implements OnClickListener {

  private static final String TAG = "LiveBarcodeActivity";

  private CameraSource cameraSource;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private View settingsButton;
  private View flashButton;
  private Chip promptChip;
  private AnimatorSet promptChipAnimator;
  private WorkflowModel workflowModel;
  private WorkflowState currentWorkflowState;
  private FirebaseFirestore mFireStore;
  private LayoutInflater layoutInflater;
  private Button addToCartButton;
  public static final String SHARED_PREFS = "sharedPrefs";
  public static final String TEXT = "text";
  public static final String SWITCH = "switch1";
  public static String productimage;
  public static final int REQUEST_ID_MULTIPLE_PERMISSIONS= 7;

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    checkAndroidVersion();

    setContentView(R.layout.activity_live_barcode);
    mFireStore = FirebaseFirestore.getInstance();
    preview = findViewById(R.id.camera_preview);
    graphicOverlay = findViewById(R.id.camera_preview_graphic_overlay);
    graphicOverlay.setOnClickListener(this);
    cameraSource = new CameraSource(graphicOverlay);

    promptChip = findViewById(R.id.bottom_prompt_chip);
    promptChipAnimator =
        (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter);
    promptChipAnimator.setTarget(promptChip);

    findViewById(R.id.close_button).setOnClickListener(this);
    flashButton = findViewById(R.id.flash_button);
    flashButton.setOnClickListener(this);
    settingsButton = findViewById(R.id.settings_button);
    settingsButton.setOnClickListener(this);


    setUpWorkflowModel();

  }

  @Override
  protected void onResume() {
    super.onResume();

    workflowModel.markCameraFrozen();
    settingsButton.setEnabled(true);
    currentWorkflowState = WorkflowState.NOT_STARTED;
    cameraSource.setFrameProcessor(new BarcodeProcessor(graphicOverlay, workflowModel));
    workflowModel.setWorkflowState(WorkflowState.DETECTING);
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    BarcodeResultFragment.dismiss(getSupportFragmentManager());
  }

  @Override
  protected void onPause() {
    super.onPause();
    currentWorkflowState = WorkflowState.NOT_STARTED;
    stopCameraPreview();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
      cameraSource = null;
    }
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    if (id == R.id.close_button) {
      onBackPressed();

    } else if (id == R.id.flash_button) {
      if (flashButton.isSelected()) {
        flashButton.setSelected(false);
        cameraSource.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF);
      } else {
        flashButton.setSelected(true);
        cameraSource.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
      }

    } else if (id == R.id.settings_button) {
      // Sets as disabled to prevent the user from clicking on it too fast.
      settingsButton.setEnabled(false);
      startActivity(new Intent(this, SettingsActivity.class));
    }
  }

  private void startCameraPreview() {
    if (!workflowModel.isCameraLive() && cameraSource != null) {
      try {
        workflowModel.markCameraLive();
        preview.start(cameraSource);
      } catch (IOException e) {
        Log.e(TAG, "Failed to start camera preview!", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  private void stopCameraPreview() {
    if (workflowModel.isCameraLive()) {
      workflowModel.markCameraFrozen();
      flashButton.setSelected(false);
      preview.stop();
    }
  }

  private void beep(){
//    ToneGenerator generator = new ToneGenerator((AudioManager.STREAM_NOTIFICATION),100);
//    generator.startTone(ToneGenerator.TONE_PROP_BEEP);
//    SystemClock.sleep(100);
//    generator.release();

    MediaPlayer beepSound = MediaPlayer.create(this,R.raw.beep);

    beepSound.start();
  }

  private void setUpWorkflowModel() {
    workflowModel = ViewModelProviders.of(this).get(WorkflowModel.class);

    // Observes the workflow state changes, if happens, update the overlay view indicators and
    // camera preview state.
    workflowModel.workflowState.observe(
        this,
        workflowState -> {
          if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
            return;
          }

          currentWorkflowState = workflowState;
          Log.d(TAG, "Current workflow state: " + currentWorkflowState.name());

          boolean wasPromptChipGone = (promptChip.getVisibility() == View.GONE);

          switch (workflowState) {
            case DETECTING:
              promptChip.setVisibility(View.VISIBLE);
              promptChip.setText(R.string.prompt_point_at_a_barcode);
              startCameraPreview();
              break;
            case CONFIRMING:
              promptChip.setVisibility(View.VISIBLE);
              promptChip.setText(R.string.prompt_move_camera_closer);
              startCameraPreview();
              break;
            case SEARCHING:
              beep();
              promptChip.setVisibility(View.VISIBLE);
              promptChip.setText(R.string.prompt_searching);
              stopCameraPreview();
              break;
            case DETECTED:
//              beep();
            case SEARCHED:
              promptChip.setVisibility(View.GONE);
              stopCameraPreview();
              break;
            default:
              promptChip.setVisibility(View.GONE);
              break;
          }

          boolean shouldPlayPromptChipEnteringAnimation =
              wasPromptChipGone && (promptChip.getVisibility() == View.VISIBLE);
          if (shouldPlayPromptChipEnteringAnimation && !promptChipAnimator.isRunning()) {
            promptChipAnimator.start();
          }
        });

    workflowModel.detectedBarcode.observe(
        this,
        barcode -> {
          if (barcode != null) {
                    DocumentReference docRef = mFireStore.collection("products").document(barcode.getRawValue());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {
                              Product product = document.toObject(Product.class);
                              SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                              SharedPreferences.Editor editor = sharedPreferences.edit();
                              ArrayList<BarcodeField> barcodeFieldList = new ArrayList<>();
                              productimage = product.getImage();
                              String price = "RM " + String.valueOf(Float.parseFloat(product.getPrice()));
                              barcodeFieldList.add(new BarcodeField("Barcode Value", barcode.getRawValue()));
                              barcodeFieldList.add(new BarcodeField("Item Name", product.getProdName()));
                              barcodeFieldList.add(new BarcodeField("Weight", product.getWeight()));
                              barcodeFieldList.add(new BarcodeField("Unit Price", price));
                              editor.putString("pbarcode", barcode.getRawValue());
                              editor.putString("pname",product.getProdName());
                              editor.putString("pprice",String.valueOf(product.getPrice()));
                              editor.putString("pweight",product.getWeight());
                              editor.commit();
                              BarcodeResultFragment.show(getSupportFragmentManager(), barcodeFieldList);





                            }
                            else {
                              Toast.makeText(LiveBarcodeScanningActivity.this,
                                      "Barcode does not exists, please try again.",
                                      Toast.LENGTH_SHORT).show();
                                      startCameraPreview();

                            }

                        } else {
                          Toast.makeText(LiveBarcodeScanningActivity.this,
                                  "Failed to get product data, please try again.",
                                  Toast.LENGTH_SHORT).show();
                          Log.d("get product",
                                  "Error getting documents: ", task.getException());
                        }
                      }
                    });
          }
        });
  }

  private void checkAndroidVersion() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      checkAndRequestPermissions();

    } else {
      // code for lollipop and pre-lollipop devices
    }

  }


  private boolean checkAndRequestPermissions() {
    int camera = ContextCompat.checkSelfPermission(LiveBarcodeScanningActivity.this,
            Manifest.permission.CAMERA);
    int wtite = ContextCompat.checkSelfPermission(LiveBarcodeScanningActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    int read = ContextCompat.checkSelfPermission(LiveBarcodeScanningActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
    List<String> listPermissionsNeeded = new ArrayList<>();
    if (wtite != PackageManager.PERMISSION_GRANTED) {
      listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    if (camera != PackageManager.PERMISSION_GRANTED) {
      listPermissionsNeeded.add(Manifest.permission.CAMERA);
    }
    if (read != PackageManager.PERMISSION_GRANTED) {
      listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    if (!listPermissionsNeeded.isEmpty()) {
      ActivityCompat.requestPermissions(LiveBarcodeScanningActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
      return false;
    }
    return true;
  }


  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    Log.d("in fragment on request", "Permission callback called-------");
    switch (requestCode) {
      case REQUEST_ID_MULTIPLE_PERMISSIONS: {

        Map<String, Integer> perms = new HashMap<>();
        // Initialize the map with both permissions
        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        // Fill with actual results from user
        if (grantResults.length > 0) {
          for (int i = 0; i < permissions.length; i++)
            perms.put(permissions[i], grantResults[i]);
          // Check for both permissions
          if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                  && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("in fragment on request", "CAMERA & WRITE_EXTERNAL_STORAGE READ_EXTERNAL_STORAGE permission granted");
            // process the normal flow
            //else any one or both the permissions are not granted
          } else {
            Log.d("in fragment on request", "Some permissions are not granted ask again ");
            //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
            //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
            if (ActivityCompat.shouldShowRequestPermissionRationale(LiveBarcodeScanningActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(LiveBarcodeScanningActivity.this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(LiveBarcodeScanningActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
              showDialogOK("Camera and Storage Permission required for this app",
                      new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                              checkAndRequestPermissions();
                              break;
                            case DialogInterface.BUTTON_NEGATIVE:
                              // proceed with logic by disabling the related features or quit the app.
                              break;
                          }
                        }
                      });
            }
            //permission is denied (and never ask again is  checked)
            //shouldShowRequestPermissionRationale will return false
            else {
              Toast.makeText(LiveBarcodeScanningActivity.this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                      .show();
              //                            //proceed with logic by disabling the related features or quit the app.
            }
          }
        }
      }
    }

  }

  private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
    new AlertDialog.Builder(LiveBarcodeScanningActivity.this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show();
  }


}
