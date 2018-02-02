/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.base.crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class PBKDF2Encryption {

    private static final String RNG_ALGORITHM = "SHA1PRNG";
    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int HASH_ITERATIONS = 22000;

    public static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(RNG_ALGORITHM);
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return salt;
    }

    public static String generateSaltBase64() throws NoSuchAlgorithmException {
        return DatatypeConverter.printBase64Binary(generateSalt());
    }

    public static byte[] getEncryptedPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int derivedKeyLength = 160;
        int iterations = HASH_ITERATIONS;
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(HASH_ALGORITHM);
        return f.generateSecret(spec).getEncoded();
    }

    public static String getEncryptedPasswordBase64(String password, String saltBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = DatatypeConverter.parseBase64Binary(saltBase64);
        return DatatypeConverter.printBase64Binary(getEncryptedPassword(password, salt));
    }

    public static boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);
        return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
    }

    public static boolean authenticateBase64(String attemptedPassword, String passwordBase64, String saltBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encryptedPassword = DatatypeConverter.parseBase64Binary(passwordBase64);
        byte[] salt = DatatypeConverter.parseBase64Binary(saltBase64);
        return authenticate(attemptedPassword, encryptedPassword, salt);
    }
}
