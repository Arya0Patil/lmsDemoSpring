package com.learnnest.lms.infra.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ApiLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(ApiLoggingFilter.class);
    private static final int MAX_BODY_LENGTH = 2000;
    private static final Pattern SENSITIVE_FIELDS = Pattern.compile(
            "(?i)\"(password|secret|token|accessToken|refreshToken)\"\\s*:\\s*\".*?\""
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri == null || !uri.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (Exception ex) {
            long durationMs = System.currentTimeMillis() - start;
            log.error(
                    "API {} {} failed in {}ms from {}: {}",
                    request.getMethod(),
                    buildPathWithQuery(request),
                    durationMs,
                    request.getRemoteAddr(),
                    ex.getMessage(),
                    ex
            );
            throw ex;
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            int status = responseWrapper.getStatus();
            String requestBody = extractBody(requestWrapper.getContentAsByteArray(), requestWrapper.getContentType(), requestWrapper.getCharacterEncoding(), request.getRequestURI());
            String responseBody = extractBody(responseWrapper.getContentAsByteArray(), responseWrapper.getContentType(), responseWrapper.getCharacterEncoding(), request.getRequestURI());

            String message = String.format(
                    "API %s %s -> %d in %dms from %s | reqBody=%s | resBody=%s",
                    request.getMethod(),
                    buildPathWithQuery(request),
                    status,
                    durationMs,
                    request.getRemoteAddr(),
                    requestBody,
                    responseBody
            );

            if (status >= 500) {
                log.error(message);
            } else if (status >= 400) {
                log.warn(message);
            } else {
                log.info(message);
            }

            responseWrapper.copyBodyToResponse();
        }
    }

    private String buildPathWithQuery(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        return (query == null || query.isBlank()) ? path : path + "?" + query;
    }

    private String extractBody(byte[] content, String contentType, String encoding, String uri) {
        if (content == null || content.length == 0) {
            return "<empty>";
        }
        if (uri != null && uri.toLowerCase(Locale.ROOT).contains("/auth")) {
            return "<omitted>";
        }
        if (!isTextualContent(contentType)) {
            return "<non-text content-type>";
        }

        Charset charset = (encoding == null || encoding.isBlank())
                ? StandardCharsets.UTF_8
                : Charset.forName(encoding);
        String body = new String(content, charset);
        body = SENSITIVE_FIELDS.matcher(body).replaceAll("\"$1\":\"****\"");

        if (body.length() > MAX_BODY_LENGTH) {
            return body.substring(0, MAX_BODY_LENGTH) + "...(truncated)";
        }
        return body;
    }

    private boolean isTextualContent(String contentType) {
        if (contentType == null) {
            return false;
        }
        try {
            MediaType mediaType = MediaType.parseMediaType(contentType);
            return MediaType.APPLICATION_JSON.includes(mediaType)
                    || MediaType.APPLICATION_XML.includes(mediaType)
                    || "text".equalsIgnoreCase(mediaType.getType());
        } catch (Exception ex) {
            return false;
        }
    }
}
