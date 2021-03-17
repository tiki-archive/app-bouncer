/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;

public class JwtBuilder {

    public static final String HEADER_TYP = "typ";
    public static final String HEADER_TYP_JWT = "JWT";
    public static final String HEADER_ALG = "alg";
    public static final String HEADER_ALG_HS256 = "HS256";
    public static final String HEADER_ALG_ES256 = "ES256";

    public static final String CLAIM_IAT = "iat";
    public static final String CLAIM_EXP = "exp";
    public static final String CLAIM_NBF = "nbf";
    public static final String CLAIM_ISS = "iss";
    public static final String CLAIM_SUB = "sub";
    public static final String CLAIM_AUD = "aud";
    public static final String CLAIM_JTI = "jti";

    private final ObjectMapper objectMapper;

    private final HashMap<String, Object> headerMap;
    private final HashMap<String, Object> payloadMap;
    private String secret;

    private String encodedHeader;
    private String encodedPayload;
    private String encodedSignature;

    public JwtBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.headerMap = new HashMap<>();
        this.payloadMap = new HashMap<>();
    }

    public JwtBuilder audience(String aud){
        this.payloadMap.put(CLAIM_AUD, aud);
        return this;
    }

    public JwtBuilder expirationTime(ZonedDateTime zdt){
        this.payloadMap.put(CLAIM_EXP, zdt.toEpochSecond());
        return this;
    }

    public JwtBuilder expirationTime(long exp){
        this.payloadMap.put(CLAIM_EXP, exp);
        return this;
    }

    public JwtBuilder expirationTimeAsDuration(Duration dur){
        if(!this.payloadMap.containsKey(CLAIM_IAT))
            throw new RuntimeException("IAT must be set before EXP when using duration", new Throwable());
        long exp = ((long) this.payloadMap.get(CLAIM_IAT)) + dur.toSeconds();
        this.payloadMap.put(CLAIM_EXP, exp);
        return this;
    }

    public JwtBuilder issuer(String iss){
        this.payloadMap.put(CLAIM_ISS, iss);
        return this;
    }

    public JwtBuilder subject(String sub){
        this.payloadMap.put(CLAIM_SUB, sub);
        return this;
    }

    public JwtBuilder issuedAtTime(ZonedDateTime zdt){
        this.payloadMap.put(CLAIM_IAT, zdt.toEpochSecond());
        return this;
    }

    public JwtBuilder issuedAtTime(long iat){
        this.payloadMap.put(CLAIM_IAT, iat);
        return this;
    }

    public JwtBuilder issuedAtNow(){
        this.payloadMap.put(CLAIM_IAT, ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond());
        return this;
    }

    public JwtBuilder notBeforeTime(ZonedDateTime zdt){
        this.payloadMap.put(CLAIM_NBF, zdt.toEpochSecond());
        return this;
    }

    public JwtBuilder notBeforeTime(long nbf){
        this.payloadMap.put(CLAIM_NBF, nbf);
        return this;
    }

    public JwtBuilder jwtId(String jti){
        this.payloadMap.put(CLAIM_JTI, jti);
        return this;
    }

    public JwtBuilder jwtId32BSecureRandom() throws NoSuchAlgorithmException {
        byte[] jtiArray = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(jtiArray);
        java.util.Base64.Encoder encoder = java.util.Base64.getUrlEncoder().withoutPadding();
        this.payloadMap.put(CLAIM_JTI, encoder.encodeToString(jtiArray));
        return this;
    }

    public JwtBuilder customClaim(String claim, String value){
        this.payloadMap.put(claim, value);
        return this;
    }

    public JwtBuilder customClaim(String claim, long value){
        this.payloadMap.put(claim, value);
        return this;
    }

    public JwtBuilder type(String typ){
        this.headerMap.put(HEADER_TYP, typ);
        return this;
    }

    public JwtBuilder typeJwt(){
        this.headerMap.put(HEADER_TYP, HEADER_TYP_JWT);
        return this;
    }

    public JwtBuilder algorithm(String alg){
        this.headerMap.put(HEADER_ALG, alg);
        return this;
    }

    public JwtBuilder algorithmHS256(){
        this.headerMap.put(HEADER_ALG, HEADER_ALG_HS256);
        return this;
    }

    public JwtBuilder algorithmES256(){
        this.headerMap.put(HEADER_ALG, HEADER_ALG_ES256);
        return this;
    }

    public JwtBuilder encodeHeader() throws JsonProcessingException {
        this.encodedHeader = Base64.encodeBase64URLSafeString(objectMapper.writeValueAsBytes(this.headerMap));
        return this;
    }

    public JwtBuilder encodePayload() throws JsonProcessingException {
        this.encodedPayload = Base64.encodeBase64URLSafeString(objectMapper.writeValueAsBytes(this.payloadMap));
        return this;
    }

    public JwtBuilder signHS256(String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        String algoName = "HmacSHA256";
        Mac hmacsha256 = Mac.getInstance(algoName);
        hmacsha256.init(new SecretKeySpec(secret.getBytes(), algoName));
        String input = this.encodedHeader + "." + this.encodedPayload;
        this.encodedSignature = Base64.encodeBase64URLSafeString(hmacsha256.doFinal(input.getBytes()));
        return this;
    }

    public JwtBuilder signES256(PublicKey publicKey, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature ecdsa = Signature.getInstance("SHA256withECDSA");
        ecdsa.initSign(privateKey);
        String input = this.encodedHeader + "." + this.encodedPayload;
        ecdsa.update(input.getBytes(StandardCharsets.UTF_8));
        this.encodedSignature = Base64.encodeBase64URLSafeString(ecdsa.sign());
        return this;
    }

    public String build(){
        return this.encodedHeader + "." + this.encodedPayload + "." + this.encodedSignature;
    }
}
