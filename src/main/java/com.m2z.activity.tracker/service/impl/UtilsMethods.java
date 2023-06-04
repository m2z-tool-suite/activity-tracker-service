package com.m2z.activity.tracker.service.impl;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.m2z.activity.tracker.config.CryptUtils;
import com.m2z.activity.tracker.dto.ExternalTrackerDto;

import java.nio.charset.StandardCharsets;

public class UtilsMethods {

    public static String getBasicAuth(ExternalTrackerDto externalTracker) {

        String emailPrivateKey = String.format("%s:%s", externalTracker.getEmail(), CryptUtils.decrypt(externalTracker.getPrivateKey()));

        return Base64.encodeBase64String(emailPrivateKey.getBytes(StandardCharsets.UTF_8));
    }
}
