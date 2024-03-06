package noise.road.service.util;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder();
        // Generate 4 random letters
        for (int i = 0; i < 4; i++) {
            password.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
        }
        // Generate 4 random numbers
        for (int i = 0; i < 4; i++) {
            password.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        }
        // Shuffle the characters in the password
        for (int i = 0; i < password.length(); i++) {
            int randomIndex = RANDOM.nextInt(password.length());
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(randomIndex));
            password.setCharAt(randomIndex, temp);
        }
        return password.toString();
    }
}
