package com.example.myapplication.QRCode.googlevision;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

/**
 * QR codes Detector.
 */
public class QRCodeScannerProcessor extends VisionProcessorBase<List<Barcode>> {

    private static final String TAG = "QRCodeProcessor";

    private static final String MANUAL_TESTING_LOG = "QRCodeProcessor_LOG";

    private final BarcodeScanner barcodeScanner;

    private IExchangeScannedData IExchangeScannedData;

    public QRCodeScannerProcessor(Context context, IExchangeScannedData IExchangeScannedData) {
        super(context);

        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        barcodeScanner = BarcodeScanning.getClient(options);

        this.IExchangeScannedData = IExchangeScannedData;
    }

    @Override
    public void stop() {
        super.stop();
        barcodeScanner.close();
    }

    @Override
    protected Task<List<Barcode>> detectInImage(InputImage image) {
        return barcodeScanner.process(image);
    }

    @Override
    protected void onSuccess(
            @NonNull List<Barcode> barcodes, @NonNull GraphicOverlay graphicOverlay) {
        if (barcodes.isEmpty()) {
            Log.v(MANUAL_TESTING_LOG, "No barcode has been detected");
        }
        for (int i = 0; i < barcodes.size(); ++i) {
            Barcode barcode = barcodes.get(i);
            graphicOverlay.add(new BarcodeGraphic(graphicOverlay, barcode));

            if (barcode != null && barcode.getRawValue() != null && !barcode.getRawValue().isEmpty()) {
                IExchangeScannedData.sendScannedCode(barcode.getRawValue());
            }
        }
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Barcode detection failed " + e);
    }
}