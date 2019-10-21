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


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import tarc.edu.selfcheckoutapp.R;
import tarc.edu.selfcheckoutapp.ui.Home.HomeFragment;

/** Entry activity to select the detection mode. */
public class MainActivity extends AppCompatActivity {



  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);


    FragmentManager manager = getSupportFragmentManager();
    HomeFragment fragment = new HomeFragment();
    FragmentTransaction transaction = manager.beginTransaction();
    transaction.add(R.id.container,fragment);
//    transaction.addToBackStack(null);
    transaction.commit();



  }

  public void onBackPressed() {
    if (getFragmentManager().getBackStackEntryCount() == 1) {
      finish();
    } else {
      super.onBackPressed();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!Utils.allPermissionsGranted(this)) {
      Utils.requestRuntimePermissions(this);
    }
  }

}
