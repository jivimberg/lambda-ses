package com.budilov.lambda.ses;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.io.InputStream;

import static com.budilov.lambda.ses.Main.AWS_CREDENTIALS;

public class FormService {

    public static final String BUCKET_NAME = "gforms";

    @NotNull
    static String readFromS3(String messageId) throws IOException {
        AmazonS3 s3Client = new AmazonS3Client(AWS_CREDENTIALS);
        S3Object object = s3Client.getObject(
                new GetObjectRequest(BUCKET_NAME, messageId));
        try(InputStream objectData = object.getObjectContent()){
            return IOUtils.toString(objectData);
        }
    }

    @NotNull
    static String getHTML(String messageContent) {
        //Matching both beginning and end tags
        String[] parts = messageContent.split("\\<\\/?html\\>", 3);
        String actual = parts[1];
        return "<html>" + actual + "</html>";
    }
}
