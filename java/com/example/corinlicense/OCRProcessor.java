package com.example.corinlicense;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OCRProcessor {

    public OCRProcessor() {
    }

    public interface TextExtractedListener {
        void onTextExtracted(String extractedText);
        void onError(Exception e);
    }

    public void extractText(Bitmap bitmap, TextExtractedListener listener) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Text.TextBlock block : visionText.getTextBlocks()) {
                        String blockText = block.getText();
                        stringBuilder.append(blockText);
                    }
                    listener.onTextExtracted(stringBuilder.toString());
                })
                .addOnFailureListener(listener::onError);
    }

    public String extractCashValue(String text) {
        Pattern pattern = Pattern.compile("SUBTOTAL\\s+(\\d+[.,]d{2})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            Log.d("OCRProcessor", "Valoare extrasă: " + matcher.group(1));
            System.out.println("matcher.group" + matcher.group(1));
            return matcher.group(1);
        } else {
            Log.d("OCRProcessor", "Valoare nu a fost găsită în text.");
            return null;
        }
    }


    public void processImageAndExtractCashValue(Bitmap bitmap, Context context, TextExtractedListener listener) {
        extractText(bitmap, new TextExtractedListener() {
            @Override
            public void onTextExtracted(String extractedText) {
                Log.d("OCRProcessor", "Text extras: " + extractedText);
                String cashValueStr = extractCashValue(extractedText);
                if (cashValueStr != null) {
                    try {
                        BigDecimal cashValue = new BigDecimal(cashValueStr);
                        listener.onTextExtracted(cashValue.toPlainString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Eroare: Formatul numărului nu este valid.", Toast.LENGTH_LONG).show();
                        listener.onError(e);
                    }
                } else {
                    Toast.makeText(context, "Eroare: Valoarea de bani nu a fost găsită în text.", Toast.LENGTH_LONG).show();
                    listener.onTextExtracted(null);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Eroare la procesarea OCR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onError(e);
            }
        });
    }
}
