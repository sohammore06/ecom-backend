package com.demo.ecommerce_backend.smileone;

import com.demo.ecommerce_backend.util.SmileOneUtil;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SmileOneUtilTest {

    @Test
    void testGenerateSignature() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("uid", "1341228");
        params.put("email", "ajaybhavsar88@gmail.com");
        params.put("product", "mobilelegends");
        params.put("time", "1751915177");

        String key = "655e4bc07fcece4f3328f033763a2f3b";  // Use actual key here
        String expectedSign = "56614e17966ffabfa89bec8f9021e342";

        String generatedSign = SmileOneUtil.generateSignature(params, key);

        assertEquals(expectedSign, generatedSign);
    }
}
