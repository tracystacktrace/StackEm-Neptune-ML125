package net.tracystacktrace.stackem.tools;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public final class SystemIOTools {
    public static void log(String message) {
        Calendar cal = Calendar.getInstance();
        System.out.printf(
                "[%02d:%02d:%02d] [Stack 'Em] %s%n",
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                message
        );
    }

    public static void log(String message, String arg0) {
        log(String.format(message, arg0));
    }

    public static boolean classExists(String s) {
        try {
            Class.forName(s);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void setClipboardText(String text) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
    }

    public static String computeSHA256(File file) throws IOException {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");

            final FileInputStream fis = new FileInputStream(file);
            final BufferedInputStream bis = new BufferedInputStream(fis);

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            bis.close();
            fis.close();

            final StringBuilder hexString = new StringBuilder();

            for (byte b : digest.digest()) {
                final String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
