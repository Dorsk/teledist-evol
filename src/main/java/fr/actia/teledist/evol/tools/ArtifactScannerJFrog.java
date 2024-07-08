package fr.actia.teledist.evol.tools;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArtifactScannerJFrog {

    private String baseUrl;
    private String apiKey;
    private OkHttpClient client;
    private Gson gson;

    public ArtifactScannerJFrog(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    // Méthode pour scanner les artefacts dans un repository spécifique
    public List<ArtifactItem> scanAndGetArtifacts(String repo) {
        List<ArtifactItem> artifacts = new ArrayList<>();
        try {
            String urlString = baseUrl + "/api/storage/" + repo;
            Request request = new Request.Builder()
                    .url(urlString)
                    .header("Authorization", "Bearer " + apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                    JsonArray children = jsonObject.getAsJsonArray("children");

                    for (JsonElement childElement : children) {
                        JsonObject artifactObject = childElement.getAsJsonObject();
                        String uri = artifactObject.get("uri").getAsString();
                        boolean isFolder = artifactObject.get("folder").getAsBoolean();
                        if (!isFolder) {
                            String artifactUrl = baseUrl + "/" + repo + uri;
                            artifacts.add(getArtifactDetails(artifactUrl));
                        }
                    }
                } else {
                    System.err.println("Request to " + urlString + " failed: " + response.message());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return artifacts;
    }

    // Méthode pour obtenir les détails d'un artefact
    private ArtifactItem getArtifactDetails(String artifactUrl) {
        try {
            Request request = new Request.Builder()
                    .url(artifactUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                    String name = jsonObject.get("name").getAsString();
                    String created = jsonObject.get("created").getAsString();
                    String downloadUrl = jsonObject.get("downloadUri").getAsString();
                    return new ArtifactItem(name, created, downloadUrl);
                } else {
                    System.err.println("Request to " + artifactUrl + " failed: " + response.message());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String baseUrl = "https://your.jfrog.io/artifactory";
        String apiKey = "your-api-key";
        ArtifactScannerJFrog scanner = new ArtifactScannerJFrog(baseUrl, apiKey);
        List<ArtifactItem> artifacts = scanner.scanAndGetArtifacts("your-repo");

        for (ArtifactItem artifact : artifacts) {
            System.out.println(artifact);
        }
    }
}

// Classe ArtifactItem pour stocker les données des artefacts
class ArtifactItem {
    private String name;
    private String createdAt;
    private String urlString;

    public ArtifactItem(String name, String createdAt, String urlString) {
        this.name = name;
        this.createdAt = createdAt;
        this.urlString = urlString;
    }

    @Override
    public String toString() {
        return "ArtifactItem{" +
                "name='" + name + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", urlString='" + urlString + '\'' +
                '}';
    }
}
