package com.budilov.lambda.ses;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.budilov.lambda.ses.models.Email;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static com.budilov.lambda.ses.Main.AWS_CREDENTIALS;

class EmailService {

    private static final String FROM = PropUtil.get("fromEmail");
    private static final String TO = PropUtil.get("toEmail"); // Replace with a "To" address. If your account is still in the

    static void sendEmail(Email email) throws Exception {

        Destination destination = new Destination().withToAddresses(TO);

        Content subject = new Content().withData(email.getSubject() + " (from " + email.getFrom() + ")");

        try {
            InputStream inputStream = EmailService.class.getResourceAsStream("/form.html");
            String formHtml = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));

            Content textBody = new Content().withData(formHtml);
            Body body = new Body().withHtml(textBody);

            Message message = new Message().withSubject(subject).withBody(body);

            SendEmailRequest request = new SendEmailRequest().withSource(FROM)
                .withDestination(destination)
                .withMessage(message);


            System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(AWS_CREDENTIALS);
            Region REGION = Region.getRegion(Regions.US_EAST_1);
            client.setRegion(REGION);

            client.sendEmail(request);
            System.out.println("Email sent!");
        } catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
            throw ex;
        }
    }
}
