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

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.common.base.Objects;
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
import com.travijuu.numberpicker.library.NumberPicker;


import java.io.IOException;
import java.util.ArrayList;

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

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

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
                              String price = "RM " + String.valueOf(product.getPrice());
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

//                              View view = layoutInflater.inflate(R.layout.barcode_bottom_sheet, null);
//
//                                  addToCartButton = view.findViewById(R.id.addCartButton);
//
//                                  addToCartButton.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                      Toast.makeText(getApplicationContext(),"Hello World",Toast.LENGTH_SHORT).show();
//                                    }
//                                  });

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

}
