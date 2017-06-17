package com.softwareverde.http.cookie;

import com.softwareverde.util.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Cookie Class as defined by: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie
 */
public class Cookie {
    protected static final DateFormat _dateFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz");
    static {
        _dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static Long parseExpirationDate(final String expirationDateString) {
        try {
            return _dateFormat.parse(expirationDateString).getTime();
        }
        catch (final ParseException exception) {
            return null;
        }
    }

    public static String formatExpirationDate(final Long expirationTime) {
        return _dateFormat.format(new Date(expirationTime));
    }

    protected String _key;
    protected String _value;
    protected String _expirationDate;
    protected Integer _maxAge;
    protected String _domain;
    protected String _path;
    protected Boolean _isSecure = true;
    protected Boolean _isHttpOnly = true;
    protected Boolean _isSameSiteStrict = true;

    public Cookie() { }
    public Cookie(final String key, final String value) {
        _key = key;
        _value = value;
    }

    public String getKey() { return _key; }
    public String getValue() { return _value; }
    public String getExpirationDate() { return _expirationDate; }
    public Integer getMaxAge() { return _maxAge; }
    public String getDomain() { return _domain; }
    public String getPath() { return _path; }
    public Boolean isSecure() { return _isSecure; }
    public Boolean isHttpOnly() { return _isHttpOnly; }
    public Boolean isSameSiteStrict() { return _isSameSiteStrict; }

    public void setKey(final String key) { _key = key; }
    public void setValue(final String value) { _value = value; }
    public void setExpirationDate(final Long expirationDate) {
        _expirationDate = formatExpirationDate(expirationDate);
    }
    public void setExpirationDate(final String expirationDate) {
        _expirationDate = expirationDate;
    }
    public void setMaxAge(final Integer maxAge) { _maxAge = maxAge; }
    public void setDomain(final String domain) { _domain = domain; }
    public void setPath(final String path) { _path = path; }
    public void setIsSecure(final Boolean isSecure) { _isSecure = isSecure; }
    public void setIsHttpOnly(final Boolean isHttpOnly) { _isHttpOnly = isHttpOnly; }
    public void setIsSameSiteStrict(final Boolean isSameSiteStrict) { _isSameSiteStrict = isSameSiteStrict; }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(_key);
        stringBuilder.append("=");
        stringBuilder.append(_value);
        stringBuilder.append("; ");

        if (_expirationDate != null) {
            stringBuilder.append("Expires=");
            stringBuilder.append(_expirationDate);
            stringBuilder.append("; ");
        }

        if (_maxAge != null) {
            stringBuilder.append("Max-Age=");
            stringBuilder.append(_maxAge);
            stringBuilder.append("; ");
        }

        if (_domain != null) {
            stringBuilder.append("Domain=");
            stringBuilder.append(_domain);
            stringBuilder.append("; ");
        }

        if (_path != null) {
            stringBuilder.append("Path=");
            stringBuilder.append(_path);
            stringBuilder.append("; ");
        }

        if (Util.coalesce(_isSecure)) {
            stringBuilder.append("Secure");
            stringBuilder.append("; ");
        }

        if (Util.coalesce(_isHttpOnly)) {
            stringBuilder.append("HttpOnly");
            stringBuilder.append("; ");
        }

        if (_isSameSiteStrict != null) {
            stringBuilder.append("SameSite=");
            stringBuilder.append(_isSameSiteStrict ? "Strict" : "Lax");
            stringBuilder.append("; ");
        }

        return stringBuilder.toString();
    }
}
