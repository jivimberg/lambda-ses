package com.budilov.lambda.ses;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import static com.budilov.lambda.ses.Main.AWS_CREDENTIALS;

class FormService {

    private static final String BUCKET_NAME = "gforms";
    private final LambdaLogger logger;
    private AmazonS3 s3Client;

    FormService(@NotNull LambdaLogger logger) {
        this.logger = logger;
        // Using the same user to send mail and process incoming mails
        this.s3Client = new AmazonS3Client(AWS_CREDENTIALS);
    }

    FormService(@NotNull LambdaLogger logger, @NotNull AmazonS3 s3Client) {
        this.logger = logger;
        this.s3Client = s3Client;
    }

    void processEmail(@NotNull String from, @NotNull String messageId)
            throws IOException, DecoderException {
        logger.log("Processing email with message Id: " + messageId + " from: " + from);
        String messageContent = readFromS3(s3Client, messageId);
        String html = getHTML(messageContent);
        String decodedHtml = decodeHTML(html);
        persistHTML(s3Client, from, messageId, decodedHtml);
        removeS3(s3Client, messageId);
    }

    String decodeHTML(@NotNull String html) throws DecoderException {
        String htmlWithoutLineBreaks = html.replaceAll("=[\n\r]", "");
        return new QuotedPrintableCodec().decode(htmlWithoutLineBreaks);
    }

    @NotNull
    String readFromS3(@NotNull AmazonS3 s3Client, @NotNull String key) throws IOException {
        logger.log("Reading from S3 key: " + key);
        S3Object object = s3Client.getObject(
                new GetObjectRequest(BUCKET_NAME, key));
        try(InputStream objectData = object.getObjectContent()){
            return IOUtils.toString(objectData);
        }
    }

    @NotNull
    String getHTML(@NotNull String messageContent) {
        logger.log("Getting HTML from content: " + messageContent);
        //Matching both beginning and end tags
        String[] parts = messageContent.split("<\\/?html\\>", 3);
        String actual = parts[1];
        return "<html>" + actual + "</html>";
    }

    private void persistHTML(@NotNull AmazonS3 s3Client,
                             @NotNull String from,
                             @NotNull String messageId,
                             @NotNull String html)
            throws IOException
    {
        String key = from + '-' + messageId;
        logger.log("Creating temp file for: " + key);
        File tempFile = File.createTempFile(key, ".tmp");
        logger.log("Writing content to tempFile: " + tempFile.getName());
        try(PrintWriter out = new PrintWriter(tempFile)){
            out.print(html);
        }

        logger.log("Persisting HTML with key: " + key + " and content: " + html);
        s3Client.putObject(new PutObjectRequest(BUCKET_NAME, key, tempFile));
    }

    private void removeS3(@NotNull AmazonS3 s3Client, @NotNull String key){
        logger.log("Removing object with key: " + key);
        s3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, key));
    }
}
