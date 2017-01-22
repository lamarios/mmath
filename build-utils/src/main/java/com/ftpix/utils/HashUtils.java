package com.ftpix.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by gz on 24-Sep-16.
 */
public class HashUtils {

    /**
     * Hashes a string
     */
    public static String hash(String s) {
        return DigestUtils.sha256Hex(s);
    }
}
