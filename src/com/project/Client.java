package com.project;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.*;

public class Client {

    public void signRequest(HttpRequestBase requestBase, String key, String password) {
        String basic_auth = new String(Base64.encodeBase64((key + ":" + password).getBytes()));
        requestBase.addHeader("Authorization", "Basic " + basic_auth);
        System.out.println("Add Authorization header : " + "Basic " + basic_auth);
    }
}
