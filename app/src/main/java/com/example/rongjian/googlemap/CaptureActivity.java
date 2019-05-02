/**
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 *Powered by Google Vision API, Google Vision Samples from github and theoreticalb.
 *https://developers.google.com/vision/
 */

/**
 *Licensed under the Creative Commons Attribution 3.0 License, and code samples are licensed under the Apache 2.0 License.
 *For details, see our Site Policies. Java is a registered trademark of Oracle and/or its affiliates.
 *Google official API
 */

/**
 *
 * Based on the official sample, instruction, and tutorial of Google Vision API, we re-build the API and modify the setting
 *
 */
package com.example.rongjian.googlemap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.example.rongjian.googlemap.R;
import com.example.rongjian.googlemap.ui.camera.CameraSource;
import com.example.rongjian.googlemap.ui.camera.CameraSourcePreview;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.example.rongjian.googlemap.ui.camera.CameraSource;
import com.example.rongjian.googlemap.ui.camera.CameraSourcePreview;
import com.example.rongjian.googlemap.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import android.util.SparseArray;

import com.example.rongjian.googlemap.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.io.IOException;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.rongjian.googlemap.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import android.util.SparseArray;

import com.example.rongjian.googlemap.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;

public final class CaptureActivity extends AppCompatActivity {

    private static final String TAG = "CapturedActivity";

    public static final String SwitchFlash = "SwitchFlash";
    public static final String TextClass = "String";

    private CameraSource configCamera;
    private CameraSourcePreview cameraFrame;
    private GraphicOverlay<GShell> Layout;

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    public String writingValue = null;


    @Override
    public void onCreate(Bundle camcap) {

        super.onCreate(camcap);

        setContentView(R.layout.activity_capture);

        cameraFrame = (CameraSourcePreview) findViewById(R.id.preview);
        Layout = (GraphicOverlay<GShell>) findViewById(R.id.graphicOverlay);
        int permissionoc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        boolean switchFlash = getIntent().getBooleanExtra(SwitchFlash, false);

        if (permissionoc != PackageManager.PERMISSION_GRANTED) {
            gainPermission();

        } else {
            creatView(switchFlash);

        }

        gestureDetector = new GestureDetector(this, new Capturer());
        scaleGestureDetector = new ScaleGestureDetector(this, new ShellI());

        Snackbar.make(Layout, "Click to capture",
                Snackbar.LENGTH_LONG)
                .show();
    }

    @SuppressLint("InlinedApi")
    private void creatView(boolean switchFlash) {

        Context context = getApplicationContext();

        TextRecognizer get = new TextRecognizer.Builder(context).build();
        get.setProcessor(new DetectorProcessor(Layout));

        boolean Focus = true;
        configCamera =
                new CameraSource.Builder(getApplicationContext(), get)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1024, 1024)
                        .setRequestedFps(30.0f)
                        .setFlashMode(switchFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(Focus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .build();

        if (get.isOperational() != true) {

            Log.w(TAG, "Detector is not exist");

            IntentFilter filter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean ifexist = registerReceiver(null, filter) != null;

            if (ifexist) {
                Toast.makeText(this, "low_storage_error", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createCamera() throws SecurityException {

        int PC = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());


        if (configCamera != null) {
            try {
                cameraFrame.start(configCamera, Layout);
            } catch (IOException e) {
                Log.e(TAG, "No camera source.", e);
                configCamera.release();
                configCamera = null;
            }
        }


        if (PC != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, PC, 9001);
            dlg.show();
        }
    }

    private void gainPermission() {

        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, 2);
            return;
        }

        final Activity now = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(now, permissions,
                        2);
            }
        };

        Snackbar.make(Layout, "permission_camera_rationale",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("Finish", listener)
                .show();
    }

    private class DetectorProcessor implements Detector.Processor<TextBlock> {

        private GraphicOverlay<GShell> Layout;

        DetectorProcessor(GraphicOverlay<GShell> TGraphicOverlay) {
            Layout = TGraphicOverlay;
        }

        @Override
        public void receiveDetections(Detector.Detections<TextBlock> detections) {
            Layout.clear();
            SparseArray<TextBlock> texts = detections.getDetectedItems();
            for (int i = 0; i < texts.size(); ++i) {
                TextBlock text = texts.valueAt(i);
                GShell Shell = new GShell(Layout, text);
                Layout.add(Shell);
            }
        }

        @Override
        public void release() {
            Layout.clear();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode != 2) {
            Log.d(TAG, "No Permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera start");
            boolean switchFlash = getIntent().getBooleanExtra(SwitchFlash, false);
            creatView(switchFlash);
            return;
        }

        Log.e(TAG, "Not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder sou = new AlertDialog.Builder(this);
        sou.setTitle("Multitracker sample")
                .setMessage("no_camera_permission")
                .setPositiveButton("ok", listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean scale = scaleGestureDetector.onTouchEvent(event);

        boolean ges = gestureDetector.onTouchEvent(event);

        return ges || scale || super.onTouchEvent(event);
    }

    private boolean userinput(float rawX, float rawY) {
        GShell Shell = Layout.getGraphicAtLocation(rawX, rawY);
        TextBlock text = null;
        if (Shell != null) {
            text = Shell.getTextBlock();
            if ((text != null) && (text.getValue() != null)) {
                Intent texts = new Intent();
                texts.putExtra("String", text.getValue());
                setResult(CommonStatusCodes.SUCCESS, texts);
                finish();
            }
            else {
                Log.d(TAG, "data null");
            }
        }
        else {
            Log.d(TAG,"Null");
        }
        return text != null;
    }
    private class Capturer extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            return userinput(event.getRawX(), event.getRawY()) || super.onSingleTapConfirmed(event);
        }
    }

    private class ShellI implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            configCamera.doZoom(detector.getScaleFactor());
        }
    }

    public class GShell extends GraphicOverlay.Graphic {



        private Paint recShell;
        private Paint texting;
        private final TextBlock WritingValue;
        private int currentId;

        GShell(GraphicOverlay overlay, TextBlock text) {
            super(overlay);

            WritingValue = text;

            if (recShell == null) {
                recShell = new Paint();
                recShell.setColor(Color.BLACK);
                recShell.setStyle(Paint.Style.STROKE);
                recShell.setStrokeWidth(4.0f);
            }

            if (texting == null) {
                texting = new Paint();
                texting.setColor(Color.BLACK);
                texting.setTextSize(70.0f);
            }
            postInvalidate();
        }

        public int getId() {
            return currentId;
        }

        public void setId(int id) {
            this.currentId = id;
        }

        public TextBlock getTextBlock() {
            return WritingValue;
        }

        public boolean contains(float x, float y) {
            TextBlock data = WritingValue;
            if (data == null) {
                return false;
            }
            RectF r = new RectF(data.getBoundingBox());
            r.left = translateX(r.left);
            r.right = translateX(r.right);
            r.bottom = translateY(r.bottom);
            r.top = translateY(r.top);
            return (r.left < x && r.right > x && r.top < y && r.bottom > y);
        }

        @Override
        public void draw(Canvas canvas) {
            TextBlock data = WritingValue;
            if (data != null) {

                RectF r = new RectF(data.getBoundingBox());
                r.left = translateX(r.left);
                r.top = translateY(r.top);
                r.right = translateX(r.right);
                r.bottom = translateY(r.bottom);
                canvas.drawRect(r, recShell);

                List<? extends Text> textComponents = data.getComponents();
                for (Text currentText : textComponents) {
                    float left = translateX(currentText.getBoundingBox().left);
                    float bottom = translateY(currentText.getBoundingBox().bottom);
                    canvas.drawText(currentText.getValue(), left, bottom, texting);
                }
            }
            else return;


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraFrame != null) {
            cameraFrame.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraFrame != null) {
            cameraFrame.release();
        }
    }

}


