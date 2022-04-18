/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer;

import com.mytiki.bouncer.features.latest.sync.*;
import com.mytiki.bouncer.main.BouncerApp;
import com.mytiki.common.exception.ApiException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.*;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {BouncerApp.class}
)
@ActiveProfiles(profiles = {"test", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SyncTest {

    @Autowired
    private SyncService syncService;

    @Autowired
    private SyncRepository syncRepository;

    @Test
    public void Test_Register_Success() throws Exception {
        BCRSAPublicKey pubKey = (BCRSAPublicKey) (generateKeyPair().getPublic());
        String address = deriveAddress(pubKey);
        SyncAORegisterReq req = new SyncAORegisterReq(address, encode(pubKey));
        syncService.register(req);
        Optional<SyncDO> saved = syncRepository.findByAddress(address);
        assertTrue(saved.isPresent());
    }

    @Test
    public void Test_Register_Fail_Duplicate() throws Exception {
        BCRSAPublicKey pubKey = (BCRSAPublicKey) (generateKeyPair().getPublic());
        String address = deriveAddress(pubKey);
        SyncAORegisterReq req = new SyncAORegisterReq(address, encode(pubKey));
        syncService.register(req);
        assertThrows(ApiException.class, () -> {
            syncService.register(req);
        });
    }

    @Test
    public void Test_Register_Fail_BadKey() throws Exception {
        BCRSAPublicKey pubKey = (BCRSAPublicKey) (generateKeyPair().getPublic());
        String address = deriveAddress(pubKey);
        SyncAORegisterReq req = new SyncAORegisterReq(address, "dummy");
        assertThrows(ApiException.class, () -> {
            syncService.register(req);
        });
    }

    @Test
    public void Test_Register_Fail_Mismatch() throws Exception {
        BCRSAPublicKey pubKey = (BCRSAPublicKey) (generateKeyPair().getPublic());
        SyncAORegisterReq req = new SyncAORegisterReq("dummy", encode(pubKey));
        assertThrows(ApiException.class, () -> {
            syncService.register(req);
        });
    }

    @Test
    public void Test_Policy_Success() throws Exception {
        KeyPair keyPair =  generateKeyPair();
        BCRSAPublicKey pubKey = (BCRSAPublicKey) keyPair.getPublic();
        BCRSAPrivateKey privKey = (BCRSAPrivateKey) keyPair.getPrivate();

        String address = deriveAddress(pubKey);
        SyncAORegisterReq req = new SyncAORegisterReq(address, encode(pubKey));
        syncService.register(req);

        String dummy = "dummy";
        String signature = sign(privKey, dummy);

        SyncAOPolicyReq policyReq = new SyncAOPolicyReq(address, dummy, signature);
        SyncAOPolicyRsp policyRsp = syncService.policy(policyReq);

        assertNotNull(policyRsp);
        assertNotNull(policyRsp.getPolicy());
        assertNotNull(policyRsp.getAccountId());
        assertNotNull(policyRsp.getDate());
        assertNotNull(policyRsp.getSignature());
    }

    @Test
    public void Test_Policy_Fail_BadSignature() throws Exception {
        KeyPair keyPair =  generateKeyPair();
        BCRSAPublicKey pubKey = (BCRSAPublicKey) keyPair.getPublic();

        String address = deriveAddress(pubKey);
        SyncAORegisterReq req = new SyncAORegisterReq(address, encode(pubKey));
        syncService.register(req);

        String dummy = "dummy";
        String signature = "garbage";

        SyncAOPolicyReq policyReq = new SyncAOPolicyReq(address, dummy, signature);
        assertThrows(ApiException.class, () -> {
            SyncAOPolicyRsp policyRsp = syncService.policy(policyReq);
        });
    }

    @Test
    public void Test_Policy_Fail_NotRegistered() throws Exception {
        KeyPair keyPair =  generateKeyPair();
        BCRSAPublicKey pubKey = (BCRSAPublicKey) keyPair.getPublic();
        BCRSAPrivateKey privKey = (BCRSAPrivateKey) keyPair.getPrivate();

        String address = deriveAddress(pubKey);

        String dummy = "dummy";
        String signature = sign(privKey, dummy);

        SyncAOPolicyReq policyReq = new SyncAOPolicyReq(address, dummy, signature);
        assertThrows(ApiException.class, () -> {
            SyncAOPolicyRsp policyRsp = syncService.policy(policyReq);
        });
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(2048, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    private String encode(BCRSAPublicKey publicKey) throws IOException {
        byte[] paramsBytes = {0x5, 0x0};
        ASN1Object paramsAsn1Obj = ASN1Primitive.fromByteArray(paramsBytes);
        ASN1ObjectIdentifier objectIdentifier =
                ASN1ObjectIdentifier.fromContents("1.2.840.113549.1.1.1".getBytes(StandardCharsets.UTF_8));
        ASN1EncodableVector algVector = new ASN1EncodableVector();
        algVector.add(paramsAsn1Obj);
        algVector.add(objectIdentifier);
        ASN1Sequence algorithm = new DERSequence(algVector);

        System.out.println("mod: " + publicKey.getModulus().toString());

        ASN1Integer modulus = new ASN1Integer(publicKey.getModulus());
        ASN1Integer exponent = new ASN1Integer(publicKey.getPublicExponent());
        ASN1EncodableVector publicKeyVector = new ASN1EncodableVector();
        publicKeyVector.add(modulus);
        publicKeyVector.add(exponent);
        ASN1Sequence publicKeySequence = new DERSequence(publicKeyVector);

        ASN1BitString publicKeyBitString = new DERBitString(publicKeySequence);

        ASN1EncodableVector sequenceVector = new ASN1EncodableVector();
        sequenceVector.add(algorithm);
        sequenceVector.add(publicKeyBitString);
        ASN1Sequence sequence = new DERSequence(sequenceVector);

        return Base64.getEncoder().encodeToString(sequence.getEncoded());
    }

    private String deriveAddress(BCRSAPublicKey publicKey) throws NoSuchAlgorithmException, DecoderException {
        String raw = publicKey.getModulus().toString(16);
        raw += publicKey.getPublicExponent().toString(16);
        if (raw.length() % 2 > 0) raw = "0" + raw;
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        byte[] addressBytes = md.digest(Hex.decodeHex(raw));
        return Base64.getEncoder().encodeToString(addressBytes);
    }

    private String sign(BCRSAPrivateKey privateKey, String plaintext) throws CryptoException {
        byte[] messageBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        RSADigestSigner signer = new RSADigestSigner(new SHA256Digest());
        RSAKeyParameters keyParameters =
                new RSAKeyParameters(true, privateKey.getModulus(), privateKey.getPrivateExponent());
        signer.init(true, keyParameters);
        signer.update(messageBytes, 0, messageBytes.length);
        return Base64.getEncoder().encodeToString(signer.generateSignature());
    }
}
