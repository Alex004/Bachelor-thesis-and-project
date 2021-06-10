package com.example.myapplication.QRCode.googlevision;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.mlkit.vision.text.Text;

public class TextGraphic extends GraphicOverlay.Graphic {

    private static final float STROKE_WIDTH = 4.0f;

    private static final int BOX_COLOR = Color.GREEN;

    private final Paint rectPaint;
    //private final Paint barcodePaint;
    private final Text text;
    //private final Paint labelPaint;

    public TextGraphic(GraphicOverlay overlay, Text text) {
        super(overlay);

        this.text = text;

        rectPaint = new Paint();
        rectPaint.setColor(BOX_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    public void draw(Canvas canvas) {
        if (text == null) {
            throw new IllegalStateException("Attempting to draw a null text block.");
        }

        // Draws the bounding box around the text.
        for (Text.TextBlock textBlock : text.getTextBlocks()) {
            RectF rect = new RectF(textBlock.getBoundingBox());
            rect.left = translateX(rect.left);
            rect.top = translateY(rect.top);
            rect.right = translateX(rect.right);
            rect.bottom = translateY(rect.bottom);
            canvas.drawRect(rect, rectPaint);
        }
    }
}
