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

public class OtpTemplate {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String EMAIL_SUBJECT_RESOURCE_FILENAME = "email-subject.mustache";
    private static final String EMAIL_BODY_TEXT_RESOURCE_FILENAME = "email-body-text.mustache";
    private static final String EMAIL_BODY_HTML_RESOURCE_FILENAME = "email-body-html.mustache";
    private static final String PUSH_BODY_TEXT_RESOURCE_FILENAME = "push-body-text.mustache";
    private static final String PUSH_TITLE_TEXT_RESOURCE_FILENAME = "push-title-text.mustache";
    private static final String RESOURCE_PATH = Constants.PACKAGE_FEATURES_LATEST_SLASH_PATH + "/otp/";

    private final Mustache mustacheEmailSubject;
    private final Mustache mustacheEmailBodyText;
    private final Mustache mustacheEmailBodyHtml;
    private final Mustache mustachePushBodyText;
    private final Mustache mustachePushTitleText;

    public OtpTemplate() {
        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        mustacheEmailSubject = mustacheFactory.compile(RESOURCE_PATH + EMAIL_SUBJECT_RESOURCE_FILENAME);
        mustacheEmailBodyText = mustacheFactory.compile(RESOURCE_PATH + EMAIL_BODY_TEXT_RESOURCE_FILENAME);
        mustacheEmailBodyHtml = mustacheFactory.compile(RESOURCE_PATH + EMAIL_BODY_HTML_RESOURCE_FILENAME);
        mustachePushBodyText = mustacheFactory.compile(RESOURCE_PATH + PUSH_BODY_TEXT_RESOURCE_FILENAME);
        mustachePushTitleText = mustacheFactory.compile(RESOURCE_PATH + PUSH_TITLE_TEXT_RESOURCE_FILENAME);
    }

    public String renderEmailSubject(Map<String, String> data){
        return render(mustacheEmailSubject, data);
    }

    public String renderEmailBodyText(Map<String, String> data){
        return render(mustacheEmailBodyText, data);
    }

    public String renderEmailBodyHtml(Map<String, String> data){
        return render(mustacheEmailBodyHtml, data);
    }

    public String renderPushBodyText(Map<String, String> data){
        return render(mustachePushBodyText, data);
    }

    public String renderPushTitleText(Map<String, String> data){
        return render(mustachePushTitleText, data);
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
