package com.vereinsverwaltung.frontend;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vereinsverwaltung.frontend.model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

public class ApiService {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public static List<Verein> alleVereine() throws Exception {
        return getList("/vereine", new TypeToken<List<Verein>>(){}.getType());
    }

    public static Verein vereinById(Long id) throws Exception {
        return get("/vereine/" + id, Verein.class);
    }

    public static List<Mitglied> alleMitglieder() throws Exception {
        return getList("/mitglieder", new TypeToken<List<Mitglied>>(){}.getType());
    }

    public static List<Mitglied> mitgliederVonVerein(Long vereinId) throws Exception {
        return getList("/mitglieder/verein/" + vereinId, new TypeToken<List<Mitglied>>(){}.getType());
    }

    public static Mitglied mitgliedById(Long id) throws Exception {
        return get("/mitglieder/" + id, Mitglied.class);
    }

    public static List<Gruppe> alleGruppen() throws Exception {
        return getList("/gruppen", new TypeToken<List<Gruppe>>(){}.getType());
    }

    public static List<Gruppe> gruppenVonVerein(Long vereinId) throws Exception {
        return getList("/gruppen/verein/" + vereinId, new TypeToken<List<Gruppe>>(){}.getType());
    }

    public static List<Rolle> alleRollen() throws Exception {
        return getList("/rollen", new TypeToken<List<Rolle>>(){}.getType());
    }

    public static List<Rolle> rollenVonVerein(Long vereinId) throws Exception {
        return getList("/rollen/verein/" + vereinId, new TypeToken<List<Rolle>>(){}.getType());
    }

    public static List<Veranstaltung> alleVeranstaltungen() throws Exception {
        return getList("/veranstaltungen", new TypeToken<List<Veranstaltung>>(){}.getType());
    }

    public static List<Veranstaltung> veranstaltungenVonVerein(Long vereinId) throws Exception {
        return getList("/veranstaltungen/verein/" + vereinId, new TypeToken<List<Veranstaltung>>(){}.getType());
    }

    public static List<Mitgliedsbeitrag> alleBeitraege() throws Exception {
        return getList("/beitraege", new TypeToken<List<Mitgliedsbeitrag>>(){}.getType());
    }

    public static List<Mitgliedsbeitrag> beitraegeVonMitglied(Long mitgliedId) throws Exception {
        return getList("/beitraege/mitglied/" + mitgliedId, new TypeToken<List<Mitgliedsbeitrag>>(){}.getType());
    }

    public static List<Mitgliedsbeitrag> beitraegeNachStatus(String status) throws Exception {
        return getList("/beitraege/status/" + status, new TypeToken<List<Mitgliedsbeitrag>>(){}.getType());
    }

    private static <T> T get(String path, Class<T> type) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), type);
    }

    private static <T> T getList(String path, java.lang.reflect.Type type) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), type);
    }
}