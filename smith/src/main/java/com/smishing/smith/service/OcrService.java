package com.smishing.smith.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OcrService {

    // 여러 이미지에서 텍스트 추출 후 하나로 병합
    public String extractTextFromImages(MultipartFile[] files) throws IOException {
        StringBuilder combinedText = new StringBuilder();

        for (MultipartFile file : files) {
            String text = extractTextFromImage(file);
            combinedText.append(text).append("\n");
        }

        return combinedText.toString();
    }

    // 단일 이미지에서 텍스트 추출
    private String extractTextFromImage(MultipartFile file) throws IOException {
        ByteString imgBytes = ByteString.copyFrom(file.getBytes());

        Image image = Image.newBuilder().setContent(imgBytes).build();
        Feature feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feature)
                .setImage(image)
                .build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = response.getResponses(0);

            if (res.hasError()) {
                throw new IOException("OCR 실패: " + res.getError().getMessage());
            }

            return res.getFullTextAnnotation().getText();
        }
    }
}
