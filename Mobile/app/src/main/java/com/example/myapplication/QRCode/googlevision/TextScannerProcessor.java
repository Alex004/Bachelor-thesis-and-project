package com.example.myapplication.QRCode.googlevision;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

public class TextScannerProcessor extends VisionProcessorBase<Text> {
    private static final String TAG = "OCR_Detection";
    private final TextRecognizer textRecognition;
    private IExchangeScannedData IExchangeScannedData;

    public TextScannerProcessor(Context context, IExchangeScannedData IExchangeScannedData) {
        super(context);

        this.IExchangeScannedData = IExchangeScannedData;

        textRecognition = TextRecognition.getClient();
    }

    @Override
    public void stop() {
        super.stop();
        textRecognition.close();
    }

    @Override
    protected Task<Text> detectInImage(InputImage image) {
        return textRecognition.process(image);
    }

    @Override
    protected void onSuccess(@NonNull Text results, @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.add(new TextGraphic(graphicOverlay, results));

        if (textRecognition != null) {
            IExchangeScannedData.sendScannedCode(results.getText());
        }
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "OCR detection failed " + e);
    }
}
