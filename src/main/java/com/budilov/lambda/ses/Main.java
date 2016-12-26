package com.budilov.lambda.ses;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.IOUtils;
import com.budilov.lambda.ses.models.Email;
import com.budilov.lambda.ses.models.SESEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main {

    static final BasicAWSCredentials AWS_CREDENTIALS = new BasicAWSCredentials(PropUtil.get("accessKey"), PropUtil.get("secretKey"));

    @SuppressWarnings("unused") // used by aws lambda
    public String sendMail(Email email, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("received : " + email);
        String response = "{ \"success\": \"true\"}";
        try {
            EmailService.sendEmail(email);
        } catch (Exception exc) {
            response = "{ \"success\": \"false\"}";
        }

        return String.valueOf(response);
    }

    @SuppressWarnings("unused") // used by aws lambda
    public String processReceivedForm(InputStream input, OutputStream output, Context context) {
        LambdaLogger logger = context.getLogger();
        String response = "{ \"success\": \"true\"}";

        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        try {

            //parse object
            String jsonString = IOUtils.toString(input);
            logger.log(jsonString);
            SESEvent event = mapper.readValue(jsonString, SESEvent.class);
            SESEvent.SESMail mailObject = event.getRecords().get(0).getSES().getMail();
            String from = mailObject.getCommonHeaders().getFrom().get(0);
            logger.log("From: "+ from);
            String messageId  = mailObject.getMessageId();
            logger.log("MessageId: "+ messageId);

            //read from S3
            String messageContent = FormService.readFromS3(messageId);

            //get HTML
            String html = FormService.getHTML(messageContent);

            //persist to S3

            //remove full message

        } catch (IOException e) {
            response = "{ \"success\": \"false\"}";
            e.printStackTrace();
        }

        return String.valueOf(response);
    }

}
