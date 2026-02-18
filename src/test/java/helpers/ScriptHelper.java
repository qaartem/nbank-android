package helpers;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.qameta.allure.Allure.step;

public class ScriptHelper {
    private static final Map<String, String> envVariables = new ConcurrentHashMap<>();

    public static void execute(String scriptName) {
        step("Run script with name: " + scriptName, ()-> {
            ProcessBuilder processBuilder = new ProcessBuilder("./" + scriptName);
            processBuilder.directory(new File(System.getProperty("user.dir")));
            processBuilder.environment().putAll(envVariables);

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Failed to execute script with name: " + scriptName
                        + ", exit code: " + exitCode);
            } else {
                System.out.println("✅ Script with name " + scriptName + " executed successfully!");
            }
        });
    }

    public static void putEnvVariable(String name, String value) {
        step("Set env variable: " + name + "=" + value, () -> {
            envVariables.put(name, value);
        });
    }
}