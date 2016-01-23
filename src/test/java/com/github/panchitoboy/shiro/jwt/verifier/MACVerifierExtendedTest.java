package com.github.panchitoboy.shiro.jwt.verifier;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MACVerifierExtendedTest {

    static byte[] sharedKey;

    @BeforeClass
    public static void testing() throws IOException {
        SecureRandom random = new SecureRandom();
        sharedKey = new byte[32];
        random.nextBytes(sharedKey);
    }

    @Test
    public void validToken() throws JOSEException, ParseException {
        JWTClaimsSet jwtClaims = getJWTClaimsSet("issuer", "subject", new Date(), new Date(), new Date(new Date().getTime() + 100000));

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        Payload payload = new Payload(jwtClaims.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        JWSSigner signer = new MACSigner(sharedKey);
        jwsObject.sign(signer);
        String token = jwsObject.serialize();

        SignedJWT signed = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifierExtended(sharedKey, signed.getJWTClaimsSet());
        signed.verify(verifier);

        Assert.assertTrue("Must be valid", signed.verify(verifier));
    }

    @Test
    public void invalidTokenNotBeforeTime() throws JOSEException, ParseException {
        JWTClaimsSet jwtClaims = getJWTClaimsSet("issuer", "subject", new Date(), new Date(new Date().getTime() + 100000), new Date(new Date().getTime() + 200000));

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        Payload payload = new Payload(jwtClaims.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        JWSSigner signer = new MACSigner(sharedKey);
        jwsObject.sign(signer);
        String token = jwsObject.serialize();

        SignedJWT signed = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifierExtended(sharedKey, signed.getJWTClaimsSet());
        signed.verify(verifier);

        Assert.assertFalse("Must be invalid", signed.verify(verifier));
    }

    @Test
    public void invalidTokenExpirationTime() throws JOSEException, ParseException {
        JWTClaimsSet jwtClaims = getJWTClaimsSet("issuer", "subject", new Date(), new Date(), new Date());

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        Payload payload = new Payload(jwtClaims.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        JWSSigner signer = new MACSigner(sharedKey);
        jwsObject.sign(signer);
        String token = jwsObject.serialize();

        SignedJWT signed = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifierExtended(sharedKey, signed.getJWTClaimsSet());
        signed.verify(verifier);

        Assert.assertFalse("Must be invalid", signed.verify(verifier));
    }

    private JWTClaimsSet getJWTClaimsSet(String issuer, String subject, Date issueTime, Date notBeforeTime, Date expirationTime) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        builder.issuer(issuer);
        builder.subject(subject);
        builder.issueTime(issueTime);
        builder.notBeforeTime(notBeforeTime);
        builder.expirationTime(expirationTime);
        builder.jwtID(UUID.randomUUID().toString());
        return builder.build();
    }

}
