package com.budilov.lambda.ses;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

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
}
