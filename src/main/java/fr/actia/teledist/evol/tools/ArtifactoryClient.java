package fr.actia.teledist.evol.tools;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ArtifactoryClient {
    
    private OkHttpClient client;
    private final String GITHUB_TOKEN = "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"; // Replace with your GitHub personal access token
    

    public ArtifactoryClient() {
        this.client = new OkHttpClient();
    }

    public String getArtifacts(String GITHUB_URL) throws IOException {
        Request request = new Request.Builder().url(GITHUB_URL)
            .header("Authorization", "Bearer " + GITHUB_TOKEN)
            .header("content-type", "application/json")
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                return response.body().string();
        }
    }

    public void downloadArtifact(String url, String filePath) throws IOException {
        Request request = new Request.Builder().url(url)
            .header("Authorization", "Bearer " + GITHUB_TOKEN)
            .header("content-type", "application/json")
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            ResponseBody body = response.body();
            if (body != null) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(body.string(), JsonObject.class);
                JsonObject blobContent = jsonObject.getAsJsonObject();
                InputStream inputStream = new ByteArrayInputStream(blobContent.get("content").toString().getBytes(StandardCharsets.UTF_8));
                if (!new File(filePath).exists())
                    new File(filePath).createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileOutputStream.close();
                inputStream.close();
            }
        }
    }
}