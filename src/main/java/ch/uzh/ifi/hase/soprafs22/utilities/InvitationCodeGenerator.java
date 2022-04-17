package ch.uzh.ifi.hase.soprafs22.utilities;

import org.springframework.web.client.RestTemplate;

import java.util.Random;

/**
 *
 */
public class InvitationCodeGenerator {
    private static final String QR_API_URL = "https://api.qrserver.com/v1/create-qr-code";
    private final static String HANNIBAL_URL = "https://sopra-fs22-group-16-client.herokuapp.com?data=";

    public static byte[] getQr( String alphanumeric) {
            String data = HANNIBAL_URL + alphanumeric;
            RestTemplate restTemplate = new RestTemplate();
            String url = QR_API_URL + "/?data=" + data + "&size=100x100";
            return restTemplate.getForObject(url, byte[].class);
    }

    public static String getAlphanumeric() {
        //set limits for including only alphanumeric values
        int lowerLimit = 48;
        int upperLimit = 123;

        //set limit for the string length
        int lengthLimit = 10;

        Random random = new Random();
        return random.ints(lowerLimit, upperLimit)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(lengthLimit)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString().toUpperCase();
    }
}
