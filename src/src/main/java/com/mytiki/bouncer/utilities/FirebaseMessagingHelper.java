/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.utilities;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseMessagingHelper {

    private final FirebaseMessaging firebaseMessaging;

    public FirebaseMessagingHelper(String keyPath) throws IOException {
        FirebaseOptions options = FirebaseOptions
                .builder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(keyPath)))
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(options, "TIKI");
        firebaseMessaging = FirebaseMessaging.getInstance(app);
    }

    public void send(Message message) throws FirebaseMessagingException {
        firebaseMessaging.send(message);
    }
}
