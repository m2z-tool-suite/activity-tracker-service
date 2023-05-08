package com.m2z.tools.security.service;

import com.m2z.tools.security.exception.BadTokenException;
import com.m2z.tools.security.exception.InternalTokenProcessingException;
import com.m2z.tools.security.model.PrincipleUser;

public interface JwkService {

    PrincipleUser process(final String idToken) throws BadTokenException, InternalTokenProcessingException;
}
