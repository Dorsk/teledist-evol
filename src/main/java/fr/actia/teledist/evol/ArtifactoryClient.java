package fr.actia.teledist.evol;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class ArtifactoryClient {
    
    private OkHttpClient client;

    public ArtifactoryClient() {
        this.client = new OkHttpClient();
    }

    public String getArtifacts(String GITHUB_URL) throws IOException {
        Request request = new Request.Builder()
            .url(GITHUB_URL)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                return response.body().string();
        }
    }
}