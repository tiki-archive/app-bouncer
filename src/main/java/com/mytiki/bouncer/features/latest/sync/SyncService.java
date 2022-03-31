/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.sync;

import com.mytiki.common.exception.ApiExceptionFactory;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpStatus;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class SyncService {

    private final String s3AccountKey;
    private final String s3SecretKey;

    public SyncService(String s3AccountKey, String s3SecretKey) {
        this.s3AccountKey = s3AccountKey;
        this.s3SecretKey = s3SecretKey;
    }

    public SyncAOPolicyRsp policy(SyncAOPolicyReq req) {
        String date = ZonedDateTime.now().toLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE);
        String policy = base64Policy(req.getAddress(), date);
        try {
            String signature = signature(policy, date);
            SyncAOPolicyRsp rsp = new SyncAOPolicyRsp();
            rsp.setAccountId(s3AccountKey);
            rsp.setDate(date);
            rsp.setPolicy(base64Policy(req.getAddress(), date));
            rsp.setSignature(signature);
            return rsp;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw ApiExceptionFactory.exception(HttpStatus.EXPECTATION_FAILED, "Signing failed");
        }
    }

    private String base64Policy(String address, String date){
        String policy = "{\n" +
                "  \"expiration\": \"2022-04-01T12:00:00.000Z\",\n" +
                "  \"conditions\": [\n" +
                "    {\"bucket\": \"tiki-sync-chain\"},\n" +
                "    [\"starts-with\", \"$key\", \"" + address + "/\"], \n" +
                "    {\"x-amz-credential\": \"" + s3AccountKey + "/" + date + "/us-east-1/s3/aws4_request\"},\n" +
                "    {\"x-amz-algorithm\": \"AWS4-HMAC-SHA256\"},\n" +
                "    {\"x-amz-date\": \"" + date + "T000000Z\" },\n" +
                "    {\"content-type\": \"application/json\" },\n" +
                "    [\"starts-with\", \"$x-amz-object-lock-mode\", \"\"], \n" +
                "    [\"starts-with\", \"$x-amz-object-lock-retain-until-date\", \"\"], \n" +
                "    [\"starts-with\", \"$Content-MD5\", \"\"] \n" +
                "  ]\n" +
                "}";
        return Base64.getEncoder().encodeToString(policy.getBytes(StandardCharsets.UTF_8));
    }

    private String signature(String policy, String date) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] kSecret = ("AWS4" + s3SecretKey).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = HmacSHA256(date, kSecret);
        byte[] kRegion = HmacSHA256("us-east-1", kDate);
        byte[] kService = HmacSHA256("s3", kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);
        return Hex.encodeHexString(HmacSHA256(policy, kSigning));
    }

    private byte[] HmacSHA256(String data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }
}
