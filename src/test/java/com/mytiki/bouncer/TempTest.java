/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.UUID;

public class TempTest {

    @Test
    public void temp() throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, SignatureException, JOSEException, ParseException, KeyStoreException, CertificateException {
        ECKey ecJWK = new ECKeyGenerator(Curve.P_256)
                .keyID(UUID.randomUUID().toString())
                .generate();
        ECKey ecPublicJWK = ecJWK.toPublicJWK();
        JWKSet set = new JWKSet(ecJWK);

        String outFilePub = "pub";
        Writer outPub = new FileWriter(outFilePub + ".key");
        outPub.write(set.toJSONObject(true).toJSONString());
        outPub.close();

        String outFilePriv = "private";
        Writer outPriv = new FileWriter(outFilePriv + ".key");
        outPriv.write(set.toJSONObject(false).toJSONString());
        outPriv.close();

        JWKSet localKeys = JWKSet.load(new File("private.key"));
        //ECKey ecJWKLoad = localKeys.getKeyByKeyId(UUID.randomUUID().toString()).toECKey();
        //ECKey ecPublicJWKLoad = ecJWK.toPublicJWK();


        /*JWSSigner signer = new ECDSASigner(ecJWKLoad);
        JWSObject jwsObject = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(ecJWKLoad.getKeyID()).build(),
                new Payload(
                        new JWTClaimsSet.Builder()
                                .expirationTime()
                                .build()
                                .toJSONObject()
                ));
        jwsObject.sign(signer);
        String s = jwsObject.serialize();

        JWSVerifier verifier = new ECDSAVerifier(ecPublicJWKLoad);
        assertTrue(jwsObject.verify(verifier));

        String jwt = new JwtBuilder(new ObjectMapper())
                .typeJwt()
                .algorithmES256()
                .issuedAtNow()
                .expirationTimeAsDuration(Duration.ofMinutes(5))
                .encodeHeader()
                .encodePayload()
                .signES256(pair.getPublic(), pair.getPrivate())
                .build();

        assertNotNull(jwt);*/
    }
}
