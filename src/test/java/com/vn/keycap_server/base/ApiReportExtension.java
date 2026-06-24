package com.vn.keycap_server.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ApiReportExtension implements AfterTestExecutionCallback, AfterAllCallback {

    private static final List<TestResult> results = new ArrayList<>();

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        String className = context.getRequiredTestClass().getSimpleName();
        String methodName = context.getRequiredTestMethod().getName();
        Optional<Throwable> exception = context.getExecutionException();

        String status = exception.isPresent() ? "FAILED" : "PASSED";
        String reason = exception.isPresent() ? exception.get().getClass().getSimpleName() : "";
        String stack = exception.isPresent() ? exception.get().getMessage() : "";

        results.add(new TestResult(className, methodName, status, reason, stack));
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // Overwrite the report with accumulated results every time a test class finishes
        generateMarkdownReport();
    }

    private void generateMarkdownReport() {
        int total = results.size();
        long passed = results.stream().filter(r -> "PASSED".equals(r.status)).count();
        long failed = total - passed;

        StringBuilder sb = new StringBuilder();
        sb.append("# API Test Report\n\n");
        sb.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        sb.append("=================================\n\n");
        sb.append("Total API Tests: ").append(total).append("\n\n");
        sb.append("Passed: ").append(passed).append("\n\n");
        sb.append("Failed: ").append(failed).append("\n\n");
        sb.append("=================================\n\n");

        if (failed > 0) {
            sb.append("## Failed API Tests\n\n");
            for (TestResult r : results) {
                if ("FAILED".equals(r.status)) {
                    sb.append("**Class**: ").append(r.className).append("\n\n");
                    sb.append("**Method**: ").append(r.methodName).append("\n\n");
                    sb.append("**Reason**: ").append(r.reason).append("\n\n");
                    sb.append("**Message**: ").append(r.stack).append("\n\n");
                    sb.append("--------------------------------\n\n");
                }
            }
        }

        // Save to file
        File dir = new File("docs/reports");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(new File(dir, "api-test-report.md"))) {
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class TestResult {
        String className;
        String methodName;
        String status;
        String reason;
        String stack;

        public TestResult(String className, String methodName, String status, String reason, String stack) {
            this.className = className;
            this.methodName = methodName;
            this.status = status;
            this.reason = reason;
            this.stack = stack;
        }
    }
}
