package com.softwareverde.http.cookie;

import com.softwareverde.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CookieParser {
    protected Cookie _parseSingleCookieFromHeader(final String headerValue) {
        final String trimmedHeaderValue = headerValue.trim();
        final Integer trimmedHeaderLength = trimmedHeaderValue.length();

        final Integer endOfKeyIndex = trimmedHeaderValue.indexOf("=");
        if (endOfKeyIndex < 0) { return null; }

        final String key = trimmedHeaderValue.substring(0, endOfKeyIndex);

        final String value;
        final Integer endOfValueIndex;
        {
            Integer startOfValueIndex = endOfKeyIndex + 1;
            if (startOfValueIndex >= trimmedHeaderLength) {
                endOfValueIndex = startOfValueIndex;
                value = trimmedHeaderValue.substring(startOfValueIndex, endOfValueIndex);
            }
            else {
                final Integer indexOfNextSemicolon = trimmedHeaderValue.indexOf(';', startOfValueIndex);
                final Integer indexOfLastQuotation = trimmedHeaderValue.lastIndexOf('"');
                final Boolean hasSemicolon = (indexOfNextSemicolon >= 0);
                final Boolean isEmpty = (indexOfNextSemicolon.equals(startOfValueIndex));

                final Boolean isQuoted;
                {
                    final Boolean nextCharIsQuotation = (trimmedHeaderValue.charAt(startOfValueIndex) == '"');
                    final Boolean hasTwoQuotationMarks = (! indexOfLastQuotation.equals(startOfValueIndex));
                    isQuoted = (nextCharIsQuotation && hasTwoQuotationMarks);
                }

                if (isEmpty) {
                    endOfValueIndex = startOfValueIndex;
                }
                else if (isQuoted) {
                    endOfValueIndex = indexOfLastQuotation;
                    startOfValueIndex += 1;
                }
                else {
                    if (hasSemicolon) {
                        endOfValueIndex = indexOfNextSemicolon;
                    }
                    else {
                        endOfValueIndex = trimmedHeaderLength;
                    }
                }

                final String unTrimmedValue = trimmedHeaderValue.substring(startOfValueIndex, endOfValueIndex);
                if (isQuoted) {
                    value = unTrimmedValue;
                }
                else {
                    value = unTrimmedValue.trim();
                }
            }
        }

        final Cookie cookie = new Cookie();
        cookie._key = key.trim();
        cookie._value = value;
        cookie._isSecure = false;
        cookie._isHttpOnly = false;
        cookie._isSameSiteStrict = false;

        final Integer indexOfNextSemicolon = trimmedHeaderValue.indexOf(';', endOfValueIndex);
        final Boolean hasSemicolon = (indexOfNextSemicolon >= 0);
        if (hasSemicolon) {
            final String[] segments = trimmedHeaderValue.substring(indexOfNextSemicolon).split(";");
            for (final String segment : segments) {
                final String trimmedSegment = segment.trim();
                final Integer indexOfTrimmedSegmentEqualSign = trimmedSegment.indexOf('=');
                final Boolean segmentHasEqualSign = (indexOfTrimmedSegmentEqualSign >= 0);
                if (! segmentHasEqualSign) {
                    if (trimmedSegment.equalsIgnoreCase("secure")) {
                        cookie._isSecure = true;
                    }
                    else if (trimmedSegment.equalsIgnoreCase("httponly")) {
                        cookie._isHttpOnly = true;
                    }
                }
                else {
                    final String trimmedSegmentKey = trimmedSegment.substring(0, indexOfTrimmedSegmentEqualSign).trim();
                    final String trimmedSegmentValue = trimmedSegment.substring(indexOfTrimmedSegmentEqualSign+1);

                    switch (trimmedSegmentKey.toLowerCase()) {
                        case "expires":
                            cookie._expirationDate = trimmedSegmentValue;
                            break;
                        case "max-age":
                            cookie._maxAge = Util.parseInt(trimmedSegmentValue);
                            break;
                        case "domain":
                            cookie._domain = trimmedSegmentValue;
                            break;
                        case "path":
                            cookie._path = trimmedSegmentValue;
                            break;
                        case "samesite":
                            cookie._isSameSiteStrict = (trimmedSegmentValue.equalsIgnoreCase("strict"));
                            break;
                    }
                }
            }
        }

        return cookie;
    }

    /**
     * Parses the value of a Set-Cookie header.
     *  Accounts for Set-Cookie headers squashed into a single header delimited by commas.
     */
    public List<Cookie> parseFromHeader(final String setCookieHeaderValue) {
        final List<Cookie> cookies = new ArrayList<Cookie>();

        String previousCookiePart = "";
        for (final String headerValue : setCookieHeaderValue.split(",")) {
            final Integer headerValueLength = headerValue.length();
            if (headerValueLength > 3) {
                final String expiresMatchString = "expires=" + headerValue.substring(headerValueLength-3, headerValueLength).toLowerCase();
                final Integer matchIndex = headerValue.toLowerCase().lastIndexOf(expiresMatchString);
                if (matchIndex.equals(headerValueLength - expiresMatchString.length())) {
                    previousCookiePart = headerValue + ",";
                    continue;
                }
            }

            final Cookie cookie = _parseSingleCookieFromHeader(previousCookiePart + headerValue);
            if (cookie != null) {
                cookies.add(cookie);
            }

            previousCookiePart = "";
        }

        return cookies;
    }
}
