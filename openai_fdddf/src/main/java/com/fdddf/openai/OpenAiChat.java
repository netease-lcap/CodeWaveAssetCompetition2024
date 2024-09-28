package com.fdddf.openai;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.*;
import com.unfbx.chatgpt.entity.common.Usage;
import com.unfbx.chatgpt.entity.models.Model;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OpenAiChat {
    private String secretKey;

    /**
     * 自定义endpoint
     */
    private String customEndpoint;

    /**
     * 代理
     */
    private String proxyHost;

    /**
     * 代理端口
     */
    private Integer proxyPort;

    /**
     * 代理用户名
     */
    private String proxyUsername;

    /**
     * 代理用户密码
     */
    private String proxyPassword;

    /**
     * 代理类型 0:http 1:socks
     */
    private Integer proxyType = 0;

    public OpenAiChat(String secretKey, String customEndpoint, String proxyHost,
                       Integer proxyPort, String proxyUsername, String proxyPassword,
                       Integer proxyType ) {
        this.secretKey = secretKey;
        this.customEndpoint = customEndpoint;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyUsername = proxyUsername;
        this.proxyPassword = proxyPassword;
        this.proxyType = proxyType;
    }

    public OpenAiClient getClient(String secretKey, String customEndpoint) {
        OkHttpClient okHttpClient = getOkHttpClient();

        OpenAiClient.Builder clientBuilder = OpenAiClient.builder()
                .apiKey(Collections.singletonList(secretKey))
                .okHttpClient(okHttpClient);
        if (customEndpoint != null && !customEndpoint.isEmpty()) {
            clientBuilder.apiHost(customEndpoint);
        }
        return clientBuilder.build();
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = getBuilder();
        return builder.connectTimeout(30, TimeUnit.SECONDS)//自定义超时时间
                .writeTimeout(30, TimeUnit.SECONDS)//自定义超时时间
                .readTimeout(30, TimeUnit.SECONDS)//自定义超时时间
                .build();
    }

    public OpenAiStreamClient getStreamClient(String secretKey, String customEndpoint) {
        OkHttpClient okHttpClient = getOkHttpClient();

        OpenAiStreamClient.Builder clientBuilder = OpenAiStreamClient.builder()
                .apiKey(Collections.singletonList(secretKey))
                .okHttpClient(okHttpClient);
        if (customEndpoint != null && !customEndpoint.isEmpty()) {
            clientBuilder.apiHost(customEndpoint);
        }
        return clientBuilder.build();
    }

    @NotNull
    private OkHttpClient.Builder getBuilder() {
        Proxy proxy = getProxy();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (proxy != null) {
            builder.proxy(proxy);

            if (proxyUsername != null && !proxyUsername.isEmpty()) {
                Authenticator proxyAuthenticator = (route, response) -> {
                    String credential = Credentials.basic(proxyUsername, proxyPassword);
                    return response.request().newBuilder().header("Proxy-Authorization", credential).build();
                };
                builder.proxyAuthenticator(proxyAuthenticator);
            }


        }
        return builder;
    }

    private Proxy getProxy() {
        Proxy proxy = null;
        Proxy.Type type = Proxy.Type.DIRECT;
        if (proxyHost != null && !proxyHost.isEmpty()) {
            if (proxyType == 0) {
                type = Proxy.Type.HTTP;
            } else if (proxyType == 1) {
                type = Proxy.Type.SOCKS;
            }
            proxy = new Proxy(type, new InetSocketAddress(proxyHost, proxyPort));
        }
        return proxy;
    }

    public ChatResponse getChatCompletions(List<ChatMessage> chatMessages, String modelId) {
        OpenAiClient client = getClient(secretKey, customEndpoint);

        List<Message> messages = toChatMessages(chatMessages);
        ChatCompletion chatCompletion = ChatCompletion
                .builder()
                .messages(messages)
                .model(modelId)
                .build();

        ChatResponse response = new ChatResponse();

        try {
            ChatCompletionResponse chatCompletionResponse = client.chatCompletion(chatCompletion);
            System.out.printf("Model ID=%s is created at %s.%n", chatCompletionResponse.getId(), chatCompletionResponse.getCreated());

            response.messages = new ArrayList<>();
            for (ChatChoice choice : chatCompletionResponse.getChoices()) {
                Message message = choice.getMessage();
                response.messages.add(fromOpenAIChatResponseMessage(message));
            }

            Usage usage = chatCompletionResponse.getUsage();
            System.out.printf("Usage: number of prompt token is %d, "
                            + "number of completion token is %d, and number of total tokens in request and response is %d.%n",
                    usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());

            response.model = chatCompletionResponse.getModel();
            response.promptTokens = usage.getPromptTokens();
            response.completionTokens = usage.getCompletionTokens();
            response.totalTokens = usage.getTotalTokens();
        } catch (Exception e) {
            e.printStackTrace();
            response.errorCode = 1;
            response.errorMessage = e.getMessage();
        }

        return response;
    }

    public static Message toOpenAIChatRequestMessage(ChatMessage message)
    {
        Message.Role role = Message.Role.USER;
        if (message.roleName.equals("assistant"))
            role = Message.Role.ASSISTANT;
        if (message.roleName.equals("system"))
            role = Message.Role.SYSTEM;

        return Message.builder().role(role).content(message.content).build();
    }

    public ChatMessage fromOpenAIChatResponseMessage(Message message)
    {
        String role = message.getRole();
        return new ChatMessage(role, message.getContent());
    }

    public List<Message> toChatMessages(List<ChatMessage> chatMessages) {
        return chatMessages.stream().map(OpenAiChat::toOpenAIChatRequestMessage).collect(Collectors.toList());
    }


    public List<Model> listModels() {
        OpenAiClient openAiChat = getClient(secretKey, customEndpoint);
        return openAiChat.models();
    }

    public List<String> listModelsName() {
        return listModels().stream().map(Model::getId).collect(Collectors.toList());
    }

    public ChatResponse getChatCompletionsWithPrompt(String prompt, String modelId) {
        return getChatCompletions(Collections.singletonList(new ChatMessage("user", prompt)), modelId);
    }

    public void getStreamChatCompletions(String prompt, String modelId,
                                         Function<String,Boolean> onMessageHandler,
                                         Function<String,Boolean> onFailureHandler,
                                         Function<String,Boolean> onCompleteHandler) {
        OpenAiStreamClient openAiStreamClient = getStreamClient(secretKey, customEndpoint);

        ConsoleEventSourceListener listener = new ConsoleEventSourceListener();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        listener.onMessageHandler = onMessageHandler;
        listener.onFailureHandler = onFailureHandler;
        listener.onCompleteHandler = onCompleteHandler;
        listener.countDownLatch = countDownLatch;

        Message message = Message.builder().role(Message.Role.USER).content(prompt).build();
        ChatCompletion chatCompletion = ChatCompletion.builder().
                messages(Collections.singletonList(message))
                .model(modelId)
                .build();

        openAiStreamClient.streamChatCompletion(chatCompletion, listener);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stream chat completed.");
    }

}
