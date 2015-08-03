package com.github.panchitoboy.shiro.jwt.repository;

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
import com.github.panchitoboy.shiro.jwt.verifier.MACVerifierExtended;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

public interface UserRepository {

    public UserDefault findByUserId(Object userId);

    public UserDefault findById(Object id);

    default byte[] generateSharedKey() {
        SecureRandom random = new SecureRandom();
        byte[] sharedKey = new byte[32];
        random.nextBytes(sharedKey);
        return sharedKey;
    }

    default long getExpirationDate() {
        return 1000 * 60 * 60 * 24 * 5;
    }

    public String getIssuer();

    public byte[] getSharedKey();

    default TokenResponse createToken(UserDefault user) {
        TokenResponse response = new TokenResponse(user, createToken(user.getPrincipal()));
        return response;
    }

    default String createToken(Object userId) {
        try {
            JWTClaimsSet jwtClaims = new JWTClaimsSet();
            jwtClaims.setIssuer(getIssuer());
            jwtClaims.setSubject(userId.toString());
            jwtClaims.setIssueTime(new Date());
            jwtClaims.setNotBeforeTime(new Date());
            jwtClaims.setExpirationTime(new Date(new Date().getTime() + getExpirationDate()));
            jwtClaims.setJWTID(UUID.randomUUID().toString());

            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            Payload payload = new Payload(jwtClaims.toJSONObject());

            JWSObject jwsObject = new JWSObject(header, payload);

            JWSSigner signer = new MACSigner(getSharedKey());
            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (JOSEException ex) {
            return null;
        }
    }

    default boolean validateToken(String token) {
        try {
            SignedJWT signed = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifierExtended(getSharedKey(), signed.getJWTClaimsSet());
            return signed.verify(verifier);
        } catch (JOSEException | ParseException ex) {
            return false;
        }
    }

}
