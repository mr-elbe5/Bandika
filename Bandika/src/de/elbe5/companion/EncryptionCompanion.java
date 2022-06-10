/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.companion;

import de.elbe5.log.Log;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

public interface EncryptionCompanion {

    String ASCII_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    String RNG_ALGORITHM = "SHA1PRNG";
    String HASH_ALGORITHM = "PBKDF2WithHmacSHA1";
    int HASH_ITERATIONS = 22005;

    default byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(RNG_ALGORITHM);
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return salt;
    }

    default @NotNull String generateSaltBase64() throws NoSuchAlgorithmException {
        return Base64.getEncoder().encodeToString(generateSalt());
    }

    default byte[] getEncryptedPassword(@NotNull String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int derivedKeyLength = 160;
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(HASH_ALGORITHM);
        return f.generateSecret(spec).getEncoded();
    }

    default @NotNull String getEncryptedPasswordBase64(@NotNull String password, String saltBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = Base64.getDecoder().decode(saltBase64);
        return Base64.getEncoder().encodeToString(getEncryptedPassword(password, salt));
    }

    default @NotNull String encryptPassword(@NotNull String pwd, @NotNull String key) {
        try {
            return getEncryptedPasswordBase64(pwd, key);
        } catch (Exception e) {
            Log.error("failed to encrypt password", e);
            return pwd;
        }
    }


    default char getRandomChar(@NotNull String chars, @NotNull Random random) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    default @NotNull String generateKey() {
        try {
            return generateSaltBase64();
        } catch (Exception e) {
            Log.error("failed to create password key", e);
            return "";
        }
    }

}
