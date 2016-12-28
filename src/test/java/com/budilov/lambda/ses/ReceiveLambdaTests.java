package com.budilov.lambda.ses;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.util.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ReceiveLambdaTests {

    @Mock
    private AmazonS3 s3Client;

    @Test
    public void parseMailContent() throws IOException {
        //expected
        InputStream expectedIS = ReceiveLambdaTests.class.getResourceAsStream("/mailContentOnlyHTML");
        String expected = IOUtils.toString(expectedIS);

        //actual
        InputStream inputStream = ReceiveLambdaTests.class.getResourceAsStream("/mailContent");
        String mailContent = IOUtils.toString(inputStream);

        FormService formService = new FormService(System.out::println, s3Client);
        String actual = formService.getHTML(mailContent);

        assertEquals(expected, actual);

    }
}
