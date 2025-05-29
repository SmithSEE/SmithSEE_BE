package com.smishing.smith.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.*;
import java.io.IOException;
import java.util.Collections;

@Service
public class OcrService
{
    public String extractTextFromImage(MultipartFile file) throws IOException
    {
        ByteString imgBytes = ByteString.copyFrom(file.getBytes());

        Image image = Image.newBuilder().setContent(imgBytes).build();
        Feature feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feature)
                .setImage(image)
                .build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create())
        {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = response.getResponses(0);

            if (res.hasError())
            {
                throw new RuntimeException("OCR 실패: " + res.getError().getMessage());
            }

            return res.getFullTextAnnotation().getText();
        }
    }

    public String extractFirstUrl(String text)
    {
        Pattern pattern = Pattern.compile("(http|https)://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find())
        {
            return matcher.group();
        }
        return null;
    }
}
