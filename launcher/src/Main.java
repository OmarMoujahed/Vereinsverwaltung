public static void main(String[] args) throws Exception {
    final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

    killPort8080(isWindows);
    Thread.sleep(1000);

    File workDir = new File(System.getProperty("user.dir"));
    File backendDir = new File(workDir, "backend");
    if (!backendDir.exists()) {
        backendDir = new File(workDir.getParent(), "backend");
    }
    File frontendDir = new File(workDir, "frontend");
    if (!frontendDir.exists()) {
        frontendDir = new File(workDir.getParent(), "frontend");
    }

    ProcessBuilder backend = isWindows
            ? new ProcessBuilder("cmd", "/c", "mvnw.cmd", "spring-boot:run")
            : new ProcessBuilder("./mvnw", "spring-boot:run");
    backend.directory(backendDir);
    backend.environment().put("JAVA_HOME", System.getProperty("java.home"));
    backend.inheritIO();
    Process backendProcess = backend.start();

    final File finalBackendDir = backendDir;
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        try {
            killPort8080(isWindows);
            backendProcess.destroyForcibly();
        } catch (Exception ignored) {}
    }));

    System.out.println("Backend wird gestartet...");
    Thread.sleep(10000);

    ProcessBuilder frontend = isWindows
            ? new ProcessBuilder("cmd", "/c", "mvnw.cmd", "javafx:run")
            : new ProcessBuilder("./mvnw", "javafx:run");
    frontend.directory(frontendDir);
    frontend.environment().put("JAVA_HOME", System.getProperty("java.home"));
    frontend.inheritIO();
    Process frontendProcess = frontend.start();

    frontendProcess.waitFor();
    killPort8080(isWindows);
    backendProcess.destroyForcibly();
}

private static void killPort8080(boolean isWindows) throws Exception {
    if (isWindows) {
        new ProcessBuilder("cmd", "/c",
                "for /f \"tokens=5\" %a in ('netstat -aon ^| find \":8080\"') do taskkill /PID %a /F /T")
                .start()
                .waitFor();
    } else {
        new ProcessBuilder("bash", "-c", "lsof -ti :8080 | xargs kill -9 2>/dev/null || true")
                .start()
                .waitFor();
    }
}