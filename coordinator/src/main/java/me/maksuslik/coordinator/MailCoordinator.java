package me.maksuslik.coordinator;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class MailCoordinator {
    public static final MailCoordinator INSTANCE = new MailCoordinator();

    private static final String APPLICATION_NAME = "GmailBot";

    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = List.of(GmailScopes.GMAIL_LABELS, GmailScopes.MAIL_GOOGLE_COM, GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_SEND, "https://www.googleapis.com/auth/pubsub");
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Создаёт объект учётных даннных пользователя
     *
     * @return Объект учётных данных пользователя
     * @throws IOException Если файл credentials.json не найден
     */
    public CompletableFuture<Credential> getCredentials(GoogleAuthorizationCodeFlow flow, Long userId, UUID id) throws IOException {
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize(id.toString());
        return CompletableFuture.completedFuture(credential);
    }

    public CompletableFuture<Credential> getCredentials(final NetHttpTransport httpTransport, Long userId, UUID id) throws IOException {
        GoogleAuthorizationCodeFlow flow = getAuthorizationCodeFlow(httpTransport);
        return getCredentials(flow, userId, id);
    }

    @SneakyThrows
    public GoogleAuthorizationCodeFlow getAuthorizationCodeFlow(NetHttpTransport httpTransport) {
        // Загружаем данные из credentials.json
        InputStream in = MailCoordinator.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
    }

    @SneakyThrows
    public Gmail getService(Credential credential) {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @SneakyThrows
    public Gmail getService(Long userId, UUID id) {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return getService(getCredentials(HTTP_TRANSPORT, userId, id).get(5L, TimeUnit.SECONDS));
    }
}