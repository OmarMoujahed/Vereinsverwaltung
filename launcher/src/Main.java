public static void main(String[] args) throws Exception {
    new ProcessBuilder("bash", "-c", "lsof -ti :8080 | xargs kill -9")
            .start()
            .waitFor();

    Thread.sleep(1000);

    String basePath = System.getProperty("user.home") +
            "/IdeaProjects/backend/Vereinsverwaltung-Back/Vereinsverwaltung";

    ProcessBuilder backend = new ProcessBuilder("./mvnw", "spring-boot:run");
    backend.directory(new File(basePath + "/backend"));
    backend.inheritIO();
    Process backendProcess = backend.start();

    System.out.println("Backend wird gestartet...");
    Thread.sleep(10000);
    ProcessBuilder frontend = new ProcessBuilder("./mvnw", "javafx:run");
    frontend.directory(new File(basePath + "/frontend"));
    frontend.inheritIO();
    Process frontendProcess = frontend.start();

    frontendProcess.waitFor();
    backendProcess.destroy();
}