package extensions;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLoggingExtension implements BeforeAllCallback, AfterAllCallback,
        BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger log = LoggerFactory.getLogger(TestLoggingExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        log.info("▶ [{}] @BeforeAll (shared setup)", context.getRequiredTestClass().getSimpleName());
    }

    @Override
    public void afterAll(ExtensionContext context) {
        log.info("■ [{}] @AfterAll (shared teardown)", context.getRequiredTestClass().getSimpleName());
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        log.info("▶ [{}] started", context.getDisplayName());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        boolean failed = context.getExecutionException().isPresent();
        log.info("■ [{}] {}", context.getDisplayName(), failed ? "FAILED" : "PASSED");
    }
}
