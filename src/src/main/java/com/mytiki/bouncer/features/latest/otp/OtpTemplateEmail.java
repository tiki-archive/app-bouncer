/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.otp;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.mytiki.bouncer.utilities.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.util.Map;

public class OtpTemplateEmail {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String SUBJECT_RESOURCE_FILENAME = "email-subject.mustache";
    private static final String BODY_TEXT_RESOURCE_FILENAME = "email-body-text.mustache";
    private static final String BODY_HTML_RESOURCE_FILENAME = "email-body-html.mustache";
    private static final String RESOURCE_PATH = Constants.PACKAGE_FEATURES_LATEST_SLASH_PATH + "/otp/";

    private final Mustache mustacheSubject;
    private final Mustache mustacheBodyText;
    private final Mustache mustacheBodyHtml;

    public OtpTemplateEmail() {
        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        mustacheSubject = mustacheFactory.compile(RESOURCE_PATH + SUBJECT_RESOURCE_FILENAME);
        mustacheBodyText = mustacheFactory.compile(RESOURCE_PATH + BODY_TEXT_RESOURCE_FILENAME);
        mustacheBodyHtml = mustacheFactory.compile(RESOURCE_PATH + BODY_HTML_RESOURCE_FILENAME);
    }

    public String renderSubject(Map<String, String> data){
        return render(mustacheSubject, data);
    }

    public String renderBodyText(Map<String, String> data){
        return render(mustacheBodyText, data);
    }

    public String renderBodyHtml(Map<String, String> data){
        return render(mustacheBodyHtml, data);
    }

    private String render(Mustache mustache, Map<String, String> data){
        try {
            StringWriter writer = new StringWriter();
            mustache.execute(writer, data).flush();
            return writer.toString();
        } catch (Exception e) {
            logger.error("Failed to render mustache", e);
            return null;
        }
    }
}
