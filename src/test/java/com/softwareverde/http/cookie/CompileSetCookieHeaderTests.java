package com.softwareverde.http.cookie;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class CompileSetCookieHeaderTests {
    @Test
    public void should_compile_full_cookie() {
        // Setup
        final String expectedHeaderValue = "id=a3fWa; Expires=Wed, 21 Oct 2015 07:28:00 GMT; Max-Age=60; Domain=softwareverde.com; Path=/; Secure; HttpOnly; SameSite=Strict";

        final Cookie cookie = new Cookie();
        cookie.setKey("id");
        cookie.setValue("a3fWa");
        cookie.setExpirationDate("Wed, 21 Oct 2015 07:28:00 GMT");
        cookie.setMaxAge(60);
        cookie.setDomain("softwareverde.com");
        cookie.setPath("/");
        cookie.setIsSecure(true);
        cookie.setIsHttpOnly(true);
        cookie.setIsSameSiteStrict(true);

        final CookieParser cookieParser = new CookieParser();

        // Action
        final List<String> receivedValues = cookieParser.compileCookiesIntoSetCookieHeaderValues(Collections.singletonList(cookie));

        // Assert
        Assert.assertEquals(1, receivedValues.size());

        Assert.assertEquals(expectedHeaderValue, receivedValues.get(0));
    }
}
