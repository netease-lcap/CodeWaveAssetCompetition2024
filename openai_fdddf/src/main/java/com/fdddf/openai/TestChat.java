package com.fdddf.openai;

import org.jetbrains.annotations.TestOnly;
import org.junit.Test;

@TestOnly
public class TestChat {

    private static final String OPENAI_API_KEY = "sk-xxxxxxxxxxxxxxxx";

    @Test
    public void testProxy() {
        OpenAiChat openAiChat = new OpenAiChat(
                OPENAI_API_KEY,
                null,
                "192.168.1.50",
                1089,
                null,
                null,
                0
        );
        openAiChat.listModelsName().forEach(System.out::println);
    }

    @Test
    public void testCompletions() {
        OpenAiChat openAiChat = new OpenAiChat(
                OPENAI_API_KEY,
                "https://api.apiyi.com/",
                null, //null, "127.0.0.1"
                0,
                null,
                null,
                0
        );
        ChatResponse response = openAiChat.getChatCompletionsWithPrompt("How to say thank you in French", "gpt-3.5-turbo");
        System.out.println(response);
        response.messages.forEach(System.out::println);
    }

    @Test
    public void testCompletionsWithCallback() {
        OpenAiChat openAiChat = new OpenAiChat(
                OPENAI_API_KEY,
                "https://api.apiyi.com/",
                null, //null, "127.0.0.1"
                0,
                null,
                null,
                0
        );
        openAiChat.getStreamChatCompletions("How to say thank you in French", "gpt-3.5-turbo",
                (String message) -> {
                    System.out.println(message);
                    return null;
                }, null, null);
    }

    @Test
    public void testConnection() {
        OpenAIConnector connector = new OpenAIConnector();
        Boolean result = connector.test(OPENAI_API_KEY, "https://api.apiyi.com/", "null", 1080, "null", "null", 0);
        System.out.println(result);
    }
}


