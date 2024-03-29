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

package tarc.edu.selfcheckoutapp.UtlityClass;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.exifinterface.media.ExifInterface;
import tarc.edu.selfcheckoutapp.camera.CameraSizePair;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/** Utility class to provide helper methods. */
public class Utils {

  /**
   * If the absolute difference between aspect ratios is less than this tolerance, they are
   * considered to be the same aspect ratio.
   */
  public static final float ASPECT_RATIO_TOLERANCE = 0.01f;
  private static final String TAG = "Utils";

  static void requestRuntimePermissions(Activity activity) {
    List<String> allNeededPermissions = new ArrayList<>();
    for (String permission : getRequiredPermissions(activity)) {
      if (checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
        allNeededPermissions.add(permission);
      }
    }

    if (!allNeededPermissions.isEmpty()) {
      ActivityCompat.requestPermissions(
          activity, allNeededPermissions.toArray(new String[0]), /* requestCode= */ 0);
    }
  }

  static boolean allPermissionsGranted(Context context) {
    for (String permission : getRequiredPermissions(context)) {
      if (checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private static String[] getRequiredPermissions(Context context) {
    try {
      PackageInfo info =
          context
              .getPackageManager()
              .getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
      String[] ps = info.requestedPermissions;
      return (ps != null && ps.length > 0) ? ps : new String[0];
    } catch (Exception e) {
      return new String[0];
    }
  }

  public static boolean isPortraitMode(Context context) {
    return context.getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_PORTRAIT;
  }

  /**
   * Generates a list of acceptable preview sizes. Preview sizes are not acceptable if there is not
   * a corresponding picture size of the same aspect ratio. If there is a corresponding picture size
   * of the same aspect ratio, the picture size is paired up with the preview size.
   *
   * <p>This is necessary because even if we don't use still pictures, the still picture size must
   * be set to a size that is the same aspect ratio as the preview size we choose. Otherwise, the
   * preview images may be distorted on some devices.
   */
  public static List<CameraSizePair> generateValidPreviewSizeList(Camera camera) {
    Camera.Parameters parameters = camera.getParameters();
    List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
    List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
    List<CameraSizePair> validPreviewSizes = new ArrayList<>();
    for (Camera.Size previewSize : supportedPreviewSizes) {
      float previewAspectRatio = (float) previewSize.width / (float) previewSize.height;

      // By looping through the picture sizes in order, we favor the higher resolutions.
      // We choose the highest resolution in order to support taking the full resolution
      // picture later.
      for (Camera.Size pictureSize : supportedPictureSizes) {
        float pictureAspectRatio = (float) pictureSize.width / (float) pictureSize.height;
        if (Math.abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
          validPreviewSizes.add(new CameraSizePair(previewSize, pictureSize));
          break;
        }
      }
    }

    // If there are no picture sizes with the same aspect ratio as any preview sizes, allow all of
    // the preview sizes and hope that the camera can handle it.  Probably unlikely, but we still
    // account for it.
    if (validPreviewSizes.size() == 0) {
      Log.w(TAG, "No preview sizes have a corresponding same-aspect-ratio picture size.");
      for (Camera.Size previewSize : supportedPreviewSizes) {
        // The null picture size will let us know that we shouldn't set a picture size.
        validPreviewSizes.add(new CameraSizePair(previewSize, null));
      }
    }

    return validPreviewSizes;
  }


}
