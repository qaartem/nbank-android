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

public final class BackendLogging {

    public static final String BACKEND_LOGGER_NAME = "backend";

    private static final Logger log = LoggerFactory.getLogger(BACKEND_LOGGER_NAME);

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
                    log.info("{}", line.toString());
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
}
