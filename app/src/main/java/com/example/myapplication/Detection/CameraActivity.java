/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myapplication.Detection;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.Data.Local.FlowData;
import com.example.myapplication.Detection.env.ImageUtils;
import com.example.myapplication.Detection.env.Logger;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.CodeForRoutingDTO;
import com.example.myapplication.Models.CurrentRouteDTO;
import com.example.myapplication.Models.ElementDTO;
import com.example.myapplication.Models.Enums.ActionEventMain;
import com.example.myapplication.Models.Enums.IconItemState;
import com.example.myapplication.Models.GraphAdjacenciesDTO;
import com.example.myapplication.QRCode.QRVisionScannerActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ConstantValue;
import com.example.myapplication.Utils.Pair;
import com.google.android.material.bottomsheet.BottomSheetBehavior;



import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class CameraActivity extends AppCompatActivity
    implements OnImageAvailableListener,
        Camera.PreviewCallback,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {
  private static final Logger LOGGER = new Logger();

  private static final int PERMISSIONS_REQUEST = 1;

  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  protected int previewWidth = 0;
  protected int previewHeight = 0;
  private boolean debug = false;
  private Handler handler;
  private HandlerThread handlerThread;
  private boolean useCamera2API;
  private boolean isProcessingFrame = false;
  private byte[][] yuvBytes = new byte[3][];
  private int[] rgbBytes = null;
  private int yRowStride;
  protected ElementDTO detectionElement ;
  private Runnable postInferenceCallback;
  private Runnable imageConverter;

//  private LinearLayout bottomSheetLayout;
//  private LinearLayout gestureLayout;
//  private BottomSheetBehavior<LinearLayout> sheetBehavior;
//
  protected TextView nextItem, currentItem;
  protected ImageView findBtn, notFindBtn;
  protected List<String> computeObjString = new ArrayList<>();
  protected List<IconItemState> computeObjIconItemState = new ArrayList<>();
//  protected ImageView bottomSheetArrowImageView;
//  private ImageView plusImageView, minusImageView;
//  private SwitchCompat apiSwitchCompat;
//  private TextView threadsTextView;
  protected CodeForRoutingDTO codeEntities;
  protected GraphAdjacenciesDTO graphAdjacencies;
  protected Pair<List<IconItemState>,List<String>> searchingItem;
  protected Pair<List<IconItemState>,List<String>> notFondItem;
  protected Pair<List<IconItemState>,List<String>> foundItem;
  protected Integer constantValue;
  protected CurrentRouteDTO currentRouteDTO;
  protected CurrentRouteDTO currentImportantRoutePoints;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    LOGGER.d("onCreate " + this);
    super.onCreate(null);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    setContentView(R.layout.tfe_od_activity_camera);
//    Toolbar toolbar = findViewById(R.id.toolbar);

    nextItem = findViewById(R.id.nextItem);
    currentItem = findViewById(R.id.currentItem);
    findBtn = findViewById(R.id.foundIcon);
    notFindBtn = findViewById(R.id.notFoundIcon);


    constantValue = (Integer) getIntent().getExtras().get("ConstantValue");
    GraphAdjacenciesDTO graphAdjacenciesDTO = (GraphAdjacenciesDTO) getIntent().getExtras().get("Routing");
    if (graphAdjacenciesDTO != null) {
      graphAdjacencies = graphAdjacenciesDTO;

    } else {
      graphAdjacencies = null;
    }

    CodeForRoutingDTO codeForRoutingDTO = (CodeForRoutingDTO) getIntent().getExtras().get("Code");

    if (codeForRoutingDTO != null) {
      codeEntities = codeForRoutingDTO;
    } else {
      codeEntities = null;
    }



//        ElementDTO element = (ElementDTO) getIntent().getExtras().get("Elements");
//        if (element != null) {
//            elementDTO = element;
//        } else {
//            elementDTO = new ElementDTO();
//        }
//        intent.putExtra("SearchingItem",searchingItem);
//        intent.putExtra("FoundItem", foundItem);
//        intent.putExtra("NotFoundItem", notFondItem);

    Pair<List<IconItemState>,List<String>> search = (Pair<List<IconItemState>,List<String>>)
            getIntent().getExtras().get("SearchingItem");
    if(search != null)
    {
      searchingItem = search;
    }
    else
    {
      searchingItem = null;
    }

    Pair<List<IconItemState>,List<String>> found = (Pair<List<IconItemState>,List<String>>)
            getIntent().getExtras().get("FoundItem");
    if(found != null)
    {
      foundItem = found;
    }
    else
    {
      foundItem = null;
    }

    Pair<List<IconItemState>,List<String>> notFound = (Pair<List<IconItemState>,List<String>>)
            getIntent().getExtras().get("NotFoundItem");
    if(search != null)
    {
      notFondItem = notFound;
    }
    else
    {
      notFondItem = null;
    }

    CurrentRouteDTO route = (CurrentRouteDTO) getIntent().getExtras().get("RemainingRoute");
    if (route != null) {
      currentRouteDTO = route;
    } else {
      currentRouteDTO = null;
    }

    CurrentRouteDTO importantPoints = (CurrentRouteDTO) getIntent().getExtras().get("RemainingImportantPoint");
    if (importantPoints != null) {
      currentImportantRoutePoints = importantPoints;
    } else {
      currentImportantRoutePoints = new CurrentRouteDTO();
    }

    ElementDTO element = (ElementDTO) getIntent().getExtras().get("ItemsForDetection");
    if (element != null) {
      detectionElement = element;
      if(detectionElement.getSelectItems().size() == 0)
      {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Pair<List<IconItemState>, List<String>> pairElementsState = null;
        intent.putExtra("PairInfoDetectedElements", pairElementsState);
        intent.putExtra("Action", ActionEventMain.END_GROUP_DETECTION);
        startActivity(intent);
        finish();
      }
      else
      {
        if(detectionElement.getSelectItems().size() == 1)
        {
          currentItem.setText(detectionElement.getSelectItems().get(0));
          nextItem.setText("-");

        }
        else{
          currentItem.setText(detectionElement.getSelectItems().get(0));
          nextItem.setText(detectionElement.getSelectItems().get(1));
        }


      }

    } else {
      detectionElement = new ElementDTO();
    }




    findObj();
    notFindObj();

    if (hasPermission()) {
      setFragment();
    } else {
      requestPermission();
    }
//
//    threadsTextView = findViewById(R.id.threads);
//    plusImageView = findViewById(R.id.plus);
//    minusImageView = findViewById(R.id.minus);
//    apiSwitchCompat = findViewById(R.id.api_info_switch);
//    bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
//    gestureLayout = findViewById(R.id.gesture_layout);
//    sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
//    bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow);
//
//    ViewTreeObserver vto = gestureLayout.getViewTreeObserver();
//    vto.addOnGlobalLayoutListener(
//        new ViewTreeObserver.OnGlobalLayoutListener() {
//          @Override
//          public void onGlobalLayout() {
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//              gestureLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//            } else {
//              gestureLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//            //                int width = bottomSheetLayout.getMeasuredWidth();
//            int height = gestureLayout.getMeasuredHeight();
//
//            sheetBehavior.setPeekHeight(height);
//          }
//        });
//    sheetBehavior.setHideable(false);
//
//    sheetBehavior.setBottomSheetCallback(
//        new BottomSheetBehavior.BottomSheetCallback() {
//          @Override
//          public void onStateChanged(@NonNull View bottomSheet, int newState) {
//            switch (newState) {
//              case BottomSheetBehavior.STATE_HIDDEN:
//                break;
//              case BottomSheetBehavior.STATE_EXPANDED:
//                {
//                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_down);
//                }
//                break;
//              case BottomSheetBehavior.STATE_COLLAPSED:
//                {
//                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
//                }
//                break;
//              case BottomSheetBehavior.STATE_DRAGGING:
//                break;
//              case BottomSheetBehavior.STATE_SETTLING:
//                bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
//                break;
//            }
//          }
//
//          @Override
//          public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
//        });
//
//    frameValueTextView = findViewById(R.id.frame_info);
//    cropValueTextView = findViewById(R.id.crop_info);
//    inferenceTimeTextView = findViewById(R.id.inference_info);
//
//    apiSwitchCompat.setOnCheckedChangeListener(this);
//
//    plusImageView.setOnClickListener(this);
//    minusImageView.setOnClickListener(this);
  }

  protected int[] getRgbBytes() {
    imageConverter.run();
    return rgbBytes;
  }

  protected int getLuminanceStride() {
    return yRowStride;
  }

  protected byte[] getLuminance() {
    return yuvBytes[0];
  }

  /** Callback for android.hardware.Camera API */
  @Override
  public void onPreviewFrame(final byte[] bytes, final Camera camera) {
    if (isProcessingFrame) {
      LOGGER.w("Dropping frame!");
      return;
    }

    try {
      // Initialize the storage bitmaps once when the resolution is known.
      if (rgbBytes == null) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
      }
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      return;
    }

    isProcessingFrame = true;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;

    imageConverter =
        new Runnable() {
          @Override
          public void run() {
            ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
          }
        };

    postInferenceCallback =
        new Runnable() {
          @Override
          public void run() {
            camera.addCallbackBuffer(bytes);
            isProcessingFrame = false;
          }
        };
    processImage();
  }

  /** Callback for Camera2 API */
  @Override
  public void onImageAvailable(final ImageReader reader) {
    // We need wait until we have some size from onPreviewSizeChosen
    if (previewWidth == 0 || previewHeight == 0) {
      return;
    }
    if (rgbBytes == null) {
      rgbBytes = new int[previewWidth * previewHeight];
    }
    try {
      final Image image = reader.acquireLatestImage();

      if (image == null) {
        return;
      }

      if (isProcessingFrame) {
        image.close();
        return;
      }
      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();

      imageConverter =
          new Runnable() {
            @Override
            public void run() {
              ImageUtils.convertYUV420ToARGB8888(
                  yuvBytes[0],
                  yuvBytes[1],
                  yuvBytes[2],
                  previewWidth,
                  previewHeight,
                  yRowStride,
                  uvRowStride,
                  uvPixelStride,
                  rgbBytes);
            }
          };

      postInferenceCallback =
          new Runnable() {
            @Override
            public void run() {
              image.close();
              isProcessingFrame = false;
            }
          };

      processImage();
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      Trace.endSection();
      return;
    }
    Trace.endSection();
  }

  @Override
  public synchronized void onStart() {
    LOGGER.d("onStart " + this);
    super.onStart();
  }

  @Override
  public synchronized void onResume() {
    LOGGER.d("onResume " + this);
    super.onResume();

    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  @Override
  public synchronized void onPause() {
    LOGGER.d("onPause " + this);

    handlerThread.quitSafely();
    try {
      handlerThread.join();
      handlerThread = null;
      handler = null;
    } catch (final InterruptedException e) {
      LOGGER.e(e, "Exception!");
    }

    super.onPause();
  }

  @Override
  public synchronized void onStop() {
    LOGGER.d("onStop " + this);
    super.onStop();
  }

  @Override
  public synchronized void onDestroy() {
    LOGGER.d("onDestroy " + this);
    super.onDestroy();
  }

  protected synchronized void runInBackground(final Runnable r) {
    if (handler != null) {
      handler.post(r);
    }
  }

  @Override
  public void onRequestPermissionsResult(
          final int requestCode, final String[] permissions, final int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST) {
      if (allPermissionsGranted(grantResults)) {
        setFragment();
      } else {
        requestPermission();
      }
    }
  }

  private static boolean allPermissionsGranted(final int[] grantResults) {
    for (int result : grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
        Toast.makeText(
                CameraActivity.this,
                "Camera permission is required for this demo",
                Toast.LENGTH_LONG)
            .show();
      }
      requestPermissions(new String[] {PERMISSION_CAMERA}, PERMISSIONS_REQUEST);
    }
  }

  // Returns true if the device supports the required hardware level, or better.
  private boolean isHardwareLevelSupported(
          CameraCharacteristics characteristics, int requiredLevel) {
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }

  private String chooseCamera() {
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      for (final String cameraId : manager.getCameraIdList()) {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        // We don't use a front facing camera in this sample.
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }

        final StreamConfigurationMap map =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (map == null) {
          continue;
        }

        // Fallback to camera1 API for internal cameras that don't have full support.
        // This should help with legacy situations where using the camera2 API causes
        // distorted or otherwise broken previews.
        useCamera2API =
            (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                || isHardwareLevelSupported(
                    characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        LOGGER.i("Camera API lv2?: %s", useCamera2API);
        return cameraId;
      }
    } catch (CameraAccessException e) {
      LOGGER.e(e, "Not allowed to access camera");
    }

    return null;
  }

  protected void findObj()
  {
    findBtn.setOnClickListener(v->{
      List<String> elements = detectionElement.getSelectItems();
      String element = elements.get(0);
      elements.remove(0);
      detectionElement.setSelectItems(elements);

      List<String> allElementsForSearching = searchingItem.second;
      List<IconItemState> allIconElementedForSearching = searchingItem.first;
      allIconElementedForSearching.remove(0);
      allElementsForSearching.remove(allElementsForSearching.indexOf(element));
      searchingItem = new Pair<>(allIconElementedForSearching,allElementsForSearching);

      List<String> allElementFound = foundItem.second;
      List<IconItemState> allIconElementedFound = foundItem.first;
      allElementFound.add(element);
      allIconElementedFound.add(IconItemState.FOUND);
      foundItem = new Pair<>(allIconElementedFound,allElementFound);

//      computeObjString.add(element);
//      computeObjIconItemState.add(IconItemState.FOUND);

      if(detectionElement.getSelectItems().size() == 0)
      {

        if(currentRouteDTO.getCurrentRoute().size() == 1 && searchingItem.second.size() == 0)
        {
          Intent resultIntent = new Intent(this, MainActivity.class);
          resultIntent.putExtra("SearchingItem",searchingItem);
          resultIntent.putExtra("FoundItem", foundItem);
          resultIntent.putExtra("NotFoundItem", notFondItem);
          resultIntent.putExtra("Action", ActionEventMain.END_ROUTE_ACTIVITY);
          startActivity(resultIntent);
          finish();
        }
        else {
          Intent intent = new Intent(getApplicationContext(), QRVisionScannerActivity.class);
          intent.putExtra("RemainingRoute", currentRouteDTO);
          intent.putExtra("RemainingImportantPoint", currentImportantRoutePoints);
          intent.putExtra("ConstantValue", ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_ROUTING);
          intent.putExtra("Routing", graphAdjacencies);
          intent.putExtra("Code", codeEntities);
          intent.putExtra("SearchingItem",searchingItem);
          intent.putExtra("FoundItem", foundItem);
          intent.putExtra("NotFoundItem", notFondItem);
          intent.putExtra("Action", ActionEventMain.END_ROUTE_ACTIVITY);
          startActivity(intent);
          finish();
        }

      }
      else
      {
        if(detectionElement.getSelectItems().size() == 1)
        {
          currentItem.setText(nextItem.getText().toString());
          nextItem.setText("-");

        }
        else{
          currentItem.setText(nextItem.getText().toString());
          nextItem.setText(elements.get(1));
        }
      }
    });
  }

  protected void notFindObj()
  {
    notFindBtn.setOnClickListener(v->{
      List<String> elements = detectionElement.getSelectItems();
      String element = elements.get(0);
      elements.remove(0);
      detectionElement.setSelectItems(elements);


      List<String> allElementsForSearching = searchingItem.second;
      List<IconItemState> allIconElementedForSearching = searchingItem.first;
      allIconElementedForSearching.remove(0);
      allElementsForSearching.remove(allElementsForSearching.indexOf(element));
      searchingItem = new Pair<>(allIconElementedForSearching,allElementsForSearching);

      List<String> allElementNotFound = notFondItem.second;
      List<IconItemState> allIconElementeNotFound = notFondItem.first;
      allElementNotFound.add(element);
      allIconElementeNotFound.add(IconItemState.NOT_FOUND);
      notFondItem = new Pair<>(allIconElementeNotFound,allElementNotFound);

      if(detectionElement.getSelectItems().size() == 0)
      {

        if(currentRouteDTO.getCurrentRoute().size() == 1 && searchingItem.second.size() == 0)
        {
          Intent resultIntent = new Intent(this, MainActivity.class);
          resultIntent.putExtra("SearchingItem",searchingItem);
          resultIntent.putExtra("FoundItem", foundItem);
          resultIntent.putExtra("NotFoundItem", notFondItem);
          resultIntent.putExtra("Action", ActionEventMain.END_ROUTE_ACTIVITY);
          startActivity(resultIntent);
          finish();
        }
        else {
          Intent intent = new Intent(getApplicationContext(), QRVisionScannerActivity.class);
          intent.putExtra("RemainingRoute", currentRouteDTO);
          intent.putExtra("RemainingImportantPoint", currentImportantRoutePoints);
          intent.putExtra("ConstantValue", ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_ROUTING);
          intent.putExtra("Routing", graphAdjacencies);
          intent.putExtra("Code", codeEntities);
          intent.putExtra("SearchingItem",searchingItem);
          intent.putExtra("FoundItem", foundItem);
          intent.putExtra("NotFoundItem", notFondItem);
          intent.putExtra("Action", ActionEventMain.END_ROUTE_ACTIVITY);
          startActivity(intent);
          finish();
        }


      }
      else
        {
          if(detectionElement.getSelectItems().size() == 1)
          {
            currentItem.setText(nextItem.getText().toString());
            nextItem.setText("-");

          }
          else{
            currentItem.setText(nextItem.getText().toString());
            nextItem.setText(elements.get(1));
          }
        }
    });
  }

  protected void setFragment() {
    String cameraId = chooseCamera();

    Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
          CameraConnectionFragment.newInstance(
              new CameraConnectionFragment.ConnectionCallback() {
                @Override
                public void onPreviewSizeChosen(final Size size, final int rotation) {
                  previewHeight = size.getHeight();
                  previewWidth = size.getWidth();
                  CameraActivity.this.onPreviewSizeChosen(size, rotation);
                }
              },
              this,
              getLayoutId(),
              getDesiredPreviewFrameSize());

      camera2Fragment.setCamera(cameraId);
      fragment = camera2Fragment;
    } else {
      fragment =
          new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }

    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
  }

  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (int i = 0; i < planes.length; ++i) {
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null) {
        LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
        yuvBytes[i] = new byte[buffer.capacity()];
      }
      buffer.get(yuvBytes[i]);
    }
  }

  public boolean isDebug() {
    return debug;
  }

  protected void readyForNextImage() {
    if (postInferenceCallback != null) {
      postInferenceCallback.run();
    }
  }

  protected int getScreenOrientation() {
    switch (getWindowManager().getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;
    }
  }

//  @Override
//  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//    setUseNNAPI(isChecked);
//    if (isChecked) apiSwitchCompat.setText("NNAPI");
//    else apiSwitchCompat.setText("TFLITE");
//  }

//  @Override
//  public void onClick(View v) {
//    if (v.getId() == R.id.plus) {
//      String threads = threadsTextView.getText().toString().trim();
//      int numThreads = Integer.parseInt(threads);
//      if (numThreads >= 9) return;
//      numThreads++;
//      threadsTextView.setText(String.valueOf(numThreads));
//      setNumThreads(numThreads);
//    } else if (v.getId() == R.id.minus) {
//      String threads = threadsTextView.getText().toString().trim();
//      int numThreads = Integer.parseInt(threads);
//      if (numThreads == 1) {
//        return;
//      }
//      numThreads--;
//      threadsTextView.setText(String.valueOf(numThreads));
//      setNumThreads(numThreads);
//    }
//  }

  protected void showFrameInfo(String frameInfo) {
//    frameValueTextView.setText(frameInfo);
  }

  protected void showCropInfo(String cropInfo) {
//    cropValueTextView.setText(cropInfo);
  }

  protected void showInference(String inferenceTime) {
//    inferenceTimeTextView.setText(inferenceTime);
  }

  protected abstract void processImage();

  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

  protected abstract int getLayoutId();

  protected abstract Size getDesiredPreviewFrameSize();

  protected abstract void setNumThreads(int numThreads);

  protected abstract void setUseNNAPI(boolean isChecked);
}
