package com.anticiai.backend.service;

import com.anticiai.backend.dto.ai.AIChatResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class AIService {

    private final HttpClient httpClient;

    public AIService() {

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    public AIChatResponse chat(String message) {

        if (message == null || message.isBlank()) {

            throw new IllegalArgumentException(
                    "AI message cannot be empty"
            );
        }

        String escapedMessage = message
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        String jsonBody =
                "{\"question\":\"" + escapedMessage + "\"}";

        System.out.println(
                "===== ANTICI AI REQUEST ====="
        );

        System.out.println(
                "Message: " + message
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                "http://127.0.0.1:8000/api/chat"
                        )
                )
                .version(HttpClient.Version.HTTP_1_1)
                .header(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                )
                .header(
                        "Accept",
                        "application/json"
                )
                .POST(
                        HttpRequest.BodyPublishers.ofByteArray(
                                jsonBody.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        )
                )
                .build();

        try {

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString(
                                    StandardCharsets.UTF_8
                            )
                    );

            System.out.println(
                    "===== ANTICI AI RESPONSE ====="
            );

            System.out.println(
                    "Status: "
                            + response.statusCode()
            );

            if (response.statusCode() != 200) {

                System.err.println(
                        "Antici AI error: "
                                + response.body()
                );

                throw new RuntimeException(
                        "Antici AI is currently unavailable"
                );
            }

            String answer =
                    extractJsonValue(
                            response.body(),
                            "answer"
                    );

            if (
                    answer == null
                            || answer.isBlank()
            ) {

                throw new RuntimeException(
                        "Antici AI returned an empty answer"
                );
            }

            return new AIChatResponse(answer);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Antici AI request was interrupted",
                    e
            );

        } catch (IOException e) {

            System.err.println(
                    "Could not connect to Antici AI: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Antici AI is currently unavailable"
            );
        }
    }

    private String extractJsonValue(
            String json,
            String key
    ) {

        String search =
                "\"" + key + "\":\"";

        int start =
                json.indexOf(search);

        if (start == -1) {
            return null;
        }

        start += search.length();

        StringBuilder value =
                new StringBuilder();

        boolean escaped = false;

        for (
                int i = start;
                i < json.length();
                i++
        ) {

            char c = json.charAt(i);

            if (escaped) {

                switch (c) {

                    case 'n' ->
                            value.append('\n');

                    case 'r' ->
                            value.append('\r');

                    case 't' ->
                            value.append('\t');

                    case '"' ->
                            value.append('"');

                    case '\\' ->
                            value.append('\\');

                    case '/' ->
                            value.append('/');

                    default ->
                            value.append(c);
                }

                escaped = false;

            } else if (c == '\\') {

                escaped = true;

            } else if (c == '"') {

                return value.toString();

            } else {

                value.append(c);
            }
        }

        return null;
    }
}