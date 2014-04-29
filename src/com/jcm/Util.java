package com.jcm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.ztools.conf.Environment;

public class Util {

    public static Properties RSA_KEY = new Properties();
    
    static {
        try {
            InputStream inputStream = Environment.findInputStreamByResource("conf/RSA_KEY.properties", Util.class);
            RSA_KEY.load(inputStream);
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
}
