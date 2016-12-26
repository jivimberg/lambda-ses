package com.budilov.lambda.ses;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.budilov.lambda.ses.models.Email;
import com.budilov.lambda.ses.models.SESEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main {

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
            String jsonString = IOUtils.toString(input);
            logger.log(jsonString);

            //parse object
            SESEvent event = mapper.readValue(jsonString, SESEvent.class);
            SESEvent.SESMail mailObject = event.getRecords().get(0).getSES().getMail();
            String from = mailObject.getCommonHeaders().getFrom().get(0);
            logger.log("From: "+ from);
            String messageId  = mailObject.getMessageId();
            logger.log("MessageId: "+ messageId);

            //read from S3

            //get HTML

            //persist to S3

        } catch (IOException e) {
            response = "{ \"success\": \"false\"}";
            e.printStackTrace();
        }

        return String.valueOf(response);
    }

    public static void main(String[] args) throws JSONException {
        String json =  " {Records=[{eventVersion=1.0, ses={mail={commonHeaders={from=[Jane Doe <janedoe@example.com>], to=[johndoe@example.com], returnPath=janedoe@example.com, messageId=<0123456789example.com>, date=Wed, 7 Oct 2015 12:34:56 -0700, subject=Test Subject}, source=janedoe@example.com, timestamp=1970-01-01T00:00:00.000Z, destination=[johndoe@example.com], headers=[{name=Return-Path, value=<janedoe@example.com>}, {name=Received, value=from mailer.example.com (mailer.example.com [203.0.113.1]) by inbound-smtp.us-west-2.amazonaws.com with SMTP id o3vrnil0e2ic28trm7dfhrc2v0cnbeccl4nbp0g1 for johndoe@example.com; Wed, 07 Oct 2015 12:34:56 +0000 (UTC)}, {name=DKIM-Signature, value=v=1; a=rsa-sha256; c=relaxed/relaxed; d=example.com; s=example; h=mime-version:from:date:message-id:subject:to:content-type; bh=jX3F0bCAI7sIbkHyy3mLYO28ieDQz2R0P8HwQkklFj4=; b=sQwJ+LMe9RjkesGu+vqU56asvMhrLRRYrWCbVt6WJulueecwfEwRf9JVWgkBTKiL6m2hr70xDbPWDhtLdLO+jB3hzjVnXwK3pYIOHw3vxG6NtJ6o61XSUwjEsp9tdyxQjZf2HNYee873832l3K1EeSXKzxYk9Pwqcpi3dMC74ct9GukjIevf1H46hm1L2d9VYTL0LGZGHOAyMnHmEGB8ZExWbI+k6khpurTQQ4sp4PZPRlgHtnj3Zzv7nmpTo7dtPG5z5S9J+L+Ba7dixT0jn3HuhaJ9b+VThboo4YfsX9PMNhWWxGjVksSFOcGluPO7QutCPyoY4gbxtwkN9W69HA==}, {name=MIME-Version, value=1.0}, {name=From, value=Jane Doe <janedoe@example.com>}, {name=Date, value=Wed, 7 Oct 2015 12:34:56 -0700}, {name=Message-ID, value=<0123456789example.com>}, {name=Subject, value=Test Subject}, {name=To, value=johndoe@example.com}, {name=Content-Type, value=text/plain; charset=UTF-8}], headersTruncated=false, messageId=o3vrnil0e2ic28trm7dfhrc2v0clambda4nbp0g1}, receipt={recipients=[johndoe@example.com], timestamp=1970-01-01T00:00:00.000Z, spamVerdict={status=PASS}, dkimVerdict={status=PASS}, processingTimeMillis=574, action={type=Lambda, invocationType=Event, functionArn=arn:aws:lambda:us-west-2:012345678912:function:Example}, spfVerdict={status=PASS}, virusVerdict={status=PASS}}}, eventSource=aws:ses}]}/relaxed; d=example.com; s=example; h=mime-version:from:date:message-id:subject:to:content-type; bh=jX3F0bCAI7sIbkHyy3mLYO28ieDQz2R0P8HwQkklFj4=; b=sQwJ+LMe9RjkesGu+vqU56asvMhrLRRYrWCbVt6WJulueecwfEwRf9JVWgkBTKiL6m2hr70xDbPWDhtLdLO+jB3hzjVnXwK3pYIOHw3vxG6NtJ6o61XSUwjEsp9tdyxQjZf2HNYee873832l3K1EeSXKzxYk9Pwqcpi3dMC74ct9GukjIevf1H46hm1L2d9VYTL0LGZGHOAyMnHmEGB8ZExWbI+k6khpurTQQ4sp4PZPRlgHtnj3Zzv7nmpTo7dtPG5z5S9J+L+Ba7dixT0jn3HuhaJ9b+VThboo4YfsX9PMNhWWxGjVksSFOcGluPO7QutCPyoY4gbxtwkN9W69HA==}, {name=MIME-Version, value=1.0}, {name=From, value=Jane Doe <janedoe@example.com>}, {name=Date, value=Wed, 7 Oct 2015 12:34:56 -0700}, {name=Message-ID, value=<0123456789example.com>}, {name=Subject, value=Test Subject}, {name=To, value=johndoe@example.com}, {name=Content-Type, value=text/plain; charset=UTF-8}], headersTruncated=false, messageId=o3vrnil0e2ic28trm7dfhrc2v0clambda4nbp0g1}, receipt={recipients=[johndoe@example.com], timestamp=1970-01-01T00:00:00.000Z, spamVerdict={status=PASS}, dkimVerdict={status=PASS}, processingTimeMillis=574, action={type=Lambda, invocationType=Event, functionArn=arn:aws:lambda:us-west-2:012345678912:function:Example}, spfVerdict={status=PASS}, virusVerdict={status=PASS}}}, eventSource=aws:ses}]}";
        JSONObject jsonObject = new JSONObject(json);
        System.out.println(jsonObject.getJSONArray("Records"));
    }
}
