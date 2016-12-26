package com.budilov.lambda.ses;

import com.amazonaws.util.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class ReceiveLambdaTests {

    @Test
    public void parseMailContent() throws IOException {
        //expected
        InputStream expectedIS = ReceiveLambdaTests.class.getResourceAsStream("/mailContentOnlyHTML");
        String expected = IOUtils.toString(expectedIS);

        //actual
        InputStream inputStream = ReceiveLambdaTests.class.getResourceAsStream("/mailContent");
        String mailContent = IOUtils.toString(inputStream);

        String actual = FormService.getHTML(mailContent);

        assertEquals(expected, actual);

    }
}
