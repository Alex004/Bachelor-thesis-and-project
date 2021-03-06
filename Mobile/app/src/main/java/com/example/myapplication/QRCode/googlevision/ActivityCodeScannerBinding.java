package com.example.myapplication.QRCode.googlevision;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;

import com.example.myapplication.R;


public final class ActivityCodeScannerBinding implements ViewBinding {
    @NonNull
    private final ConstraintLayout rootView;

    @NonNull
    public final TextView qrcodeRawValue;

    @NonNull
    public final GraphicOverlay graphicOverlay;

    @NonNull
    public final TextView label;

    @NonNull
    public final PreviewView previewView;

    @NonNull
    public final LinearLayout resultContainer;

    @NonNull
    public final ConstraintLayout scannerTopLayout;

    @NonNull
    public final TextView text;

    @NonNull
    public Button flashBtn;

    private ActivityCodeScannerBinding(@NonNull ConstraintLayout rootView,
                                       @NonNull TextView qrcodeRawValue, @NonNull GraphicOverlay graphicOverlay,
                                       @NonNull TextView label, @NonNull PreviewView previewView,
                                       @NonNull LinearLayout resultContainer, @NonNull ConstraintLayout scannerTopLayout,
                                       @NonNull TextView text,
                                       @NonNull Button flashBtn) {
        this.rootView = rootView;
        this.qrcodeRawValue = qrcodeRawValue;
        this.graphicOverlay = graphicOverlay;
        this.label = label;
        this.previewView = previewView;
        this.resultContainer = resultContainer;
        this.scannerTopLayout = scannerTopLayout;
        this.text = text;
        this.flashBtn = flashBtn;
    }

    private ActivityCodeScannerBinding(@NonNull ConstraintLayout rootView,
                                       @NonNull TextView qrcodeRawValue, @NonNull GraphicOverlay graphicOverlay,
                                       @NonNull TextView label, @NonNull PreviewView previewView,
                                       @NonNull LinearLayout resultContainer, @NonNull ConstraintLayout scannerTopLayout,
                                       @NonNull TextView text) {
        this.rootView = rootView;
        this.qrcodeRawValue = qrcodeRawValue;
        this.graphicOverlay = graphicOverlay;
        this.label = label;
        this.previewView = previewView;
        this.resultContainer = resultContainer;
        this.scannerTopLayout = scannerTopLayout;
        this.text = text;
        this.flashBtn = null;
    }

    @Override
    @NonNull
    public ConstraintLayout getRoot() {
        return rootView;
    }

    @NonNull
    public static ActivityCodeScannerBinding inflate(@NonNull LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    @NonNull
    public static ActivityCodeScannerBinding inflate(@NonNull LayoutInflater inflater,
                                                     @Nullable ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_barcode_scanner, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    @NonNull
    public static ActivityCodeScannerBinding bind(@NonNull View rootView) {
        // The body of this method is generated in a way you would not otherwise write.
        // This is done to optimize the compiled bytecode for size and performance.
        int id;
        missingId:
        {
            id = R.id.barcodeRawValue;
            TextView barcodeRawValue = rootView.findViewById(id);
            if (barcodeRawValue == null) {
                break missingId;
            }

            id = R.id.graphic_overlay;
            GraphicOverlay graphicOverlay = rootView.findViewById(id);
            if (graphicOverlay == null) {
                break missingId;
            }

            id = R.id.label;
            TextView label = rootView.findViewById(id);
            if (label == null) {
                break missingId;
            }

            id = R.id.preview_view;
            PreviewView previewView = rootView.findViewById(id);
            if (previewView == null) {
                break missingId;
            }

            id = R.id.resultContainer;
            LinearLayout resultContainer = rootView.findViewById(id);
            if (resultContainer == null) {
                break missingId;
            }

            ConstraintLayout scannerTopLayout = (ConstraintLayout) rootView;

            id = R.id.text;
            TextView text = rootView.findViewById(id);
            if (text == null) {
                break missingId;
            }

//            id = R.id.flashBtn;
//            Button flashBtn = rootView.findViewById(id);
//            if(flashBtn == null) {
//                break missingId;
//            }

//            return new ActivityCodeScannerBinding((ConstraintLayout) rootView, barcodeRawValue,
//                    graphicOverlay, label, previewView, resultContainer, scannerTopLayout, text, flashBtn);
            return new ActivityCodeScannerBinding((ConstraintLayout) rootView, barcodeRawValue,
                    graphicOverlay, label, previewView, resultContainer, scannerTopLayout, text);
        }
        String missingId = rootView.getResources().getResourceName(id);
        throw new NullPointerException("Missing required view with ID: ".concat(missingId));
    }
}
