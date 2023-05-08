package com.m2z.tools.security.util;

import com.m2z.tools.security.model.PrincipleUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

    private static <T extends UserDetails> T transformPrinciple(Object auth, Class<T> cls) {
        if (cls.isInstance(auth)) {
            return cls.cast(auth);
        }
        throw new RuntimeException(String.format("Could not cast auth to %s class either wrong instance type or null", cls.getName()));
    }

    public static PrincipleUser getSecurityContextUser() {
        return transformPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal(), PrincipleUser.class);
    }
}
