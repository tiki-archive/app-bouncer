/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.sync;

import com.mytiki.common.exception.ApiExceptionBuilder;
import com.mytiki.common.exception.ApiExceptionFactory;
import com.mytiki.common.reply.ApiReplyAOMessageBuilder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class SyncService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String s3AccountKey;
    private final String s3SecretKey;
    private final SyncRepository repository;

    public SyncService(String s3AccountKey, String s3SecretKey, SyncRepository repository) {
        this.s3AccountKey = s3AccountKey;
        this.s3SecretKey = s3SecretKey;
        this.repository = repository;
    }

    public void register(SyncAORegisterReq req){
        try {
            RSAPublicKey publicKey = decodeKey(req.getPublicKey());
            String address = address(publicKey);
            if(!address.equals(req.getAddress()))
                throw new ApiExceptionBuilder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .messages(new ApiReplyAOMessageBuilder()
                                .message("key - address mismatch")
                                .properties("publicKey", req.getPublicKey(), "address", req.getAddress())
                                .build())
                        .build();
            SyncDO syncDO = new SyncDO();
            syncDO.setAddress(req.getAddress());
            syncDO.setPublicKey(req.getPublicKey());
            syncDO.setCreated(ZonedDateTime.now());
            repository.save(syncDO);
        } catch (IOException | IllegalArgumentException e) {
            logger.error("Failed to decode public key");
            throw new ApiExceptionBuilder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .messages(new ApiReplyAOMessageBuilder()
                            .message("invalid public key")
                            .properties("publicKey", req.getPublicKey())
                            .build())
                    .build();
        } catch (NoSuchAlgorithmException | DecoderException e) {
            logger.error("Failed to derive address");
            throw new ApiExceptionBuilder()
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .messages(new ApiReplyAOMessageBuilder()
                            .message("unable to confirm address")
                            .properties("address", req.getAddress())
                            .build())
                    .build();
        } catch (DataIntegrityViolationException e){
            throw new ApiExceptionBuilder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .messages(new ApiReplyAOMessageBuilder()
                            .message("address already registered")
                            .properties("address", req.getAddress())
                            .build())
                    .build();
        }
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
            logger.error("Unable to sign policy", e);
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

    private RSAPublicKey decodeKey(String publicKey) throws IOException {
        ASN1InputStream inputStream = new ASN1InputStream(Base64.getDecoder().decode(publicKey));
        ASN1Sequence topLevelSeq = ASN1Sequence.getInstance(inputStream.readObject());
        ASN1Sequence algorithmSeq = ASN1Sequence.getInstance(topLevelSeq.getObjectAt(0));
        ASN1BitString publicKeyBitString = ASN1BitString.getInstance(topLevelSeq.getObjectAt(1));
        ASN1Sequence publicKeySeq = ASN1Sequence.getInstance(
                ASN1Primitive.fromByteArray(publicKeyBitString.getBytes()));
        ASN1Integer modulus = ASN1Integer.getInstance(publicKeySeq.getObjectAt(0));
        ASN1Integer exponent = ASN1Integer.getInstance(publicKeySeq.getObjectAt(1));
        return new RSAPublicKey(modulus.getValue(), exponent.getValue());
    }

    private boolean verify(RSAPublicKey publicKey, String plaintext, String signature){
        byte[] messageBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        RSADigestSigner signer = new RSADigestSigner(new SHA256Digest());
        RSAKeyParameters keyParameters =
                new RSAKeyParameters(false, publicKey.getModulus(), publicKey.getPublicExponent());
        signer.init(false, keyParameters);
        signer.update(messageBytes, 0, messageBytes.length);
        return signer.verifySignature(Base64.getDecoder().decode(signature));
    }

    private String address(RSAPublicKey publicKey) throws NoSuchAlgorithmException, DecoderException {
        String raw = publicKey.getModulus().toString(16);
        raw += publicKey.getPublicExponent().toString(16);
        if (raw.length() % 2 > 0) raw = "0" + raw;
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        byte[] addressBytes = md.digest(Hex.decodeHex(raw));
        return Base64.getEncoder().encodeToString(addressBytes);
    }
}
