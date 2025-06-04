package com.galilikelike.Utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CalcFileSum {

    public static String getSum(byte[] data) throws IOException,NoSuchAlgorithmException {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.update(data,0,data.length);
            byte[] digestBytes = instance.digest();
            StringBuffer hashValue = new StringBuffer();
            for (int i = 0;i<digestBytes.length;i++) {
                byte cur = digestBytes[i];
                hashValue.append(String.format("%02x",cur));
            }
            log.info("calc hash:{}",hashValue.toString());
            return hashValue.toString();
    }
}
