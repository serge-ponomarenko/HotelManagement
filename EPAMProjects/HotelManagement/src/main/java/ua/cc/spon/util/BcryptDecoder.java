package ua.cc.spon.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * @author Sergiy Ponomarenko
 */
public class BcryptDecoder {

    private BcryptDecoder() {
    }

    public static String generateHash(String value) {
        return BCrypt.withDefaults().hashToString(8, value.toCharArray());
    }

    public static boolean verify(String password, String hash) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }

}
