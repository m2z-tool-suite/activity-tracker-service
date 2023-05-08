package com.m2z.tools.security.service;

import com.m2z.tools.security.exception.BadTokenException;
import com.m2z.tools.security.exception.InternalTokenProcessingException;
import com.m2z.tools.security.model.PrincipleUser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.net.URL;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class JwkServiceNimbusImpl implements JwkService {

    private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    public JwkServiceNimbusImpl(final String url, Set<String> requiredClaims, String algorithm, String type) {
        try {
            log.info("Configuring nimbus token processor");
            jwtProcessor = new DefaultJWTProcessor<>();
            // send null to allow missing type verifier cognito tokens are missing them
            jwtProcessor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>((type == null ? (JOSEObjectType) null : new JOSEObjectType(type))));
            JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(url));
            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.parse(algorithm), keySource);

            jwtProcessor.setJWSKeySelector(keySelector);
            jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                    new JWTClaimsSet.Builder().build(),
                    requiredClaims));
            log.info("Successfully configured nimbus token processor");
        } catch (Exception e) {
            throw new RuntimeException("Could not configure nimbus token processor");
        }
    }


    @Override
    public PrincipleUser process(String idToken) throws BadTokenException, InternalTokenProcessingException {
        try {
            SecurityContext ctx = null; // optional context parameter, not required here
            Set<GrantedAuthority> authorities;
            JWTClaimsSet claimsSet = jwtProcessor.process(idToken, ctx);
            Object claim = claimsSet.getClaim("cognito:groups");
            if (claim != null) {
                authorities = ((Collection<String>) claim).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
            } else {
                authorities = new HashSet<>();
            }
            UUID userId = UUID.fromString(claimsSet.getSubject());

            // TODO don't pass "null like this" either create a new impl without null or find a workaround
            return new PrincipleUser("null", authorities, userId, (String) claimsSet.getClaim("email"));
        } catch (ParseException | BadJOSEException e) {
            e.printStackTrace();
            throw new BadTokenException();
        } catch (JOSEException e) {
            log.error("Nimbus failed configured incorrectly", e);
            throw new InternalTokenProcessingException();
        }
    }
}
