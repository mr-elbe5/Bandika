/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.user;

import de.elbe5.base.crypto.PBKDF2Encryption;
import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.log.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Random;

public class UserSecurity {

    public static final String ALL_ALPHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String ALL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static final String UPPER_ALPHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String UPPER_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final String LOWER_ALPHA_CHARS = "abcdefghijklmnopqrstuvwxyz";
    public static final String LOWER_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    public static final String NUMBERS = "0123456789";
    public static final String NON_ALPHA_CHARS = "!\"$%&/()[]{}=?#";
    public static final String UPPER_SIMPLE_CONSONANTS = "BCDFGKLMNPRSTVWXZ";
    public static final String LOWER_SIMPLE_CONSONANTS = "bcdfgklmnprstvwxz";
    public static final String UPPER_VOWELS = "AEIOU";
    public static final String LOWER_VOWELS = "aeiou";

    public static String generateSimplePassword() {
        Random random = new Random();
        random.setSeed(Instant.now().toEpochMilli());
        char[] chars = new char[7];
        chars[0] = getRandomChar(UPPER_SIMPLE_CONSONANTS, random);
        chars[1] = getRandomChar(LOWER_VOWELS, random);
        chars[2] = getRandomChar(LOWER_SIMPLE_CONSONANTS, random);
        chars[3] = getRandomChar(LOWER_VOWELS, random);
        chars[4] = getRandomChar(UPPER_SIMPLE_CONSONANTS, random);
        chars[5] = getRandomChar(LOWER_VOWELS, random);
        chars[6] = getRandomChar(LOWER_SIMPLE_CONSONANTS, random);
        return new String(chars);
    }

    public static String generateCaptchaString() {
        Random random = new Random();
        random.setSeed(Instant.now().toEpochMilli());
        char[] chars = new char[5];
        chars[0] = getRandomChar(UPPER_SIMPLE_CONSONANTS, random);
        chars[1] = getRandomChar(LOWER_VOWELS, random);
        chars[2] = getRandomChar(LOWER_SIMPLE_CONSONANTS, random);
        chars[3] = getRandomChar(LOWER_VOWELS, random);
        chars[4] = getRandomChar(UPPER_SIMPLE_CONSONANTS, random);
        return new String(chars);
    }

    public static String getApprovalString() {
        return getRandomString(8, UPPER_ALPHA_CHARS);
    }

    public static String getRandomString(int count, String sourceChars) {
        Random random = new Random();
        random.setSeed(Instant.now().toEpochMilli());
        char[] chars = new char[count];
        for (int i = 0; i < count; i++) {
            chars[i] = getRandomChar(sourceChars, random);
        }
        return new String(chars);
    }

    private static char getRandomChar(String chars, Random random) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    public static BinaryFileData getCaptcha(String captcha) {
        int width = 300;
        int height = 60;
        int fontSize = 26;
        int xGap = 30;
        int yGap = 25;
        String fontName = "Arial";
        Color gradiantStartColor = new Color(60, 60, 60);
        Color gradiantEndColor = new Color(140, 140, 140);
        Color textColor = new Color(255, 153, 0);
        BinaryFileData data;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);
        GradientPaint gp = new GradientPaint(0, 0, gradiantStartColor, 0, height / 2, gradiantEndColor, true);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
        Random r = new Random();
        for (int i = 0; i < width - 10; i = i + 25) {
            int q = Math.abs(r.nextInt()) % width;
            int colorIndex = Math.abs(r.nextInt()) % 200;
            g2d.setColor(new Color(colorIndex, colorIndex, colorIndex));
            g2d.drawLine(i, q, width, height);
            g2d.drawLine(q, i, i, height);
        }
        g2d.setColor(textColor);
        int x = 0;
        int y;
        for (int i = 0; i < captcha.length(); i++) {
            Font font = new Font(fontName, Font.BOLD, fontSize);
            g2d.setFont(font);
            x += xGap + (Math.abs(r.nextInt()) % 15);
            y = yGap + Math.abs(r.nextInt()) % 20;
            g2d.drawChars(captcha.toCharArray(), i, 1, x, y);
        }
        for (int i = 0; i < width - 10; i = i + 25) {
            int p = Math.abs(r.nextInt()) % width;
            int q = Math.abs(r.nextInt()) % width;
            int colorIndex = Math.abs(r.nextInt()) % 200;
            g2d.setColor(new Color(colorIndex, colorIndex, colorIndex));
            g2d.drawLine(p, 0, i + p, q);
            g2d.drawLine(p, 0, i + 25, height);
        }
        g2d.dispose();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            data = new BinaryFileData();
            data.setContentType("image/png");
            ImageIO.write(bufferedImage, "png", out);
            out.close();
            data.setBytes(out.toByteArray());
        } catch (IOException e) {
            return null;
        }
        return data;
    }

    public static String generateKey() {
        try {
            return PBKDF2Encryption.generateSaltBase64();
        } catch (Exception e) {
            Log.error("failed to create password key", e);
            return null;
        }
    }

    public static String encryptPassword(String pwd, String key) {
        try {
            return PBKDF2Encryption.getEncryptedPasswordBase64(pwd, key);
        } catch (Exception e) {
            Log.error("failed to encrypt password", e);
            return null;
        }
    }
}
