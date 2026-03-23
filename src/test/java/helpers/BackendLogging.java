package helpers;

import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public final class BackendLogging {

    public static final String BACKEND_LOGGER_NAME = "backend";

    private static final Logger log = LoggerFactory.getLogger(BACKEND_LOGGER_NAME);
    private static final String REDACTED = "<REDACTED>";
    private static final Pattern AUTHORIZATION_HEADER_PATTERN =
            Pattern.compile("(?i)(Authorization\\s*[:=]\\s*Basic\\s+)[A-Za-z0-9+/=._-]+");
    private static final Pattern JSON_PASSWORD_PATTERN =
            Pattern.compile("(?i)(\"password\"\\s*:\\s*\")[^\"]*(\")");
    private static final Pattern FORM_PASSWORD_PATTERN =
            Pattern.compile("(?i)(password\\s*[=:]\\s*)[^\\s&,]+");

    private BackendLogging() {
    }

    public static void install() {
        OutputStream stream = new OutputStream() {
            private final StringBuilder line = new StringBuilder();

            @Override
            public void write(int b) throws IOException {
                if (b == '\n' || b == '\r') {
                    flushLine();
                } else {
                    line.append((char) b);
                }
            }

            private void flushLine() {
                if (line.length() > 0) {
                    log.info("{}", sanitizeSensitiveData(line.toString()));
                    line.setLength(0);
                }
            }

            @Override
            public void flush() throws IOException {
                flushLine();
            }
        };
        PrintStream printStream = new PrintStream(stream, true, StandardCharsets.UTF_8);
        io.restassured.RestAssured.filters(
                new RequestLoggingFilter(LogDetail.ALL, true, printStream),
                new ResponseLoggingFilter(LogDetail.ALL, true, printStream)
        );
        log.info("Backend request/response logging enabled (logger: {})", BACKEND_LOGGER_NAME);
    }

    private static String sanitizeSensitiveData(String value) {
        String sanitized = AUTHORIZATION_HEADER_PATTERN.matcher(value).replaceAll("$1" + REDACTED);
        sanitized = JSON_PASSWORD_PATTERN.matcher(sanitized).replaceAll("$1" + REDACTED + "$2");
        sanitized = FORM_PASSWORD_PATTERN.matcher(sanitized).replaceAll("$1" + REDACTED);
        return sanitized;
    }
}
