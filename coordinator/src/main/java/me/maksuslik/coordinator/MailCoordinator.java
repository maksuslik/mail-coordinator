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
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;
import me.maksuslik.coordinator.message.MessageSender;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class MailCoordinator {
    private static final MailCoordinator MAIL_COORDINATOR = new MailCoordinator();

    private static final String APPLICATION_NAME = "GmailBot";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = List.of(GmailScopes.GMAIL_LABELS, GmailScopes.MAIL_GOOGLE_COM, GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_SEND);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Создаёт объект учётных даннных пользователя
     *
     * @return Объект учётных данных пользователя
     * @throws IOException Если файл credentials.json не найден
     */
    public Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, boolean isNewUser) throws IOException {
        // Загружаем данные из credentials.json
        InputStream in = MailCoordinator.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Отправляем запрос на авторизацию пользователя и возвращаем данные
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        String userId = isNewUser ? UUID.randomUUID().toString() : "ac6755b1-6ad5-4377-a6a2-acca41a92ddf";
        System.out.println("userId: " + userId);
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize(userId);
        return credential;
    }

    public Gmail getService(boolean isNewUser) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, isNewUser))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}