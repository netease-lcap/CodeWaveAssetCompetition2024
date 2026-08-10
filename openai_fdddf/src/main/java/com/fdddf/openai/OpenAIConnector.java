package com.fdddf.openai;

import com.netease.lowcode.core.annotation.NaslConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@NaslConnector(connectorKind = "openAIConnector")
public class OpenAIConnector {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    /**
     * 密钥
     */
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
    private Integer proxyPort = 80;

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

    /**
     * 初始化
     * @param secretKey String OpenAI密钥
     * @param customEndpoint String 自定义endpoint，空填写为null
     * @param proxyHost String 代理地址，空填写为null
     * @param proxyPort Integer 代理端口，空填写为0
     * @param proxyUsername String 代理用户名，空填写为null
     * @param proxyPassword String 代理密码，空填写为null
     * @param proxyType Integer 代理类型 0:http 1:socks
     * @return OpenAIConnector
     */
    @NaslConnector.Creator
    public OpenAIConnector initBean(String secretKey, String customEndpoint,
                                    String proxyHost, Integer proxyPort,
                                    String proxyUsername, String proxyPassword, Integer proxyType) {
        OpenAIConnector openAIConnector = new OpenAIConnector();
        openAIConnector.secretKey = secretKey;
        openAIConnector.customEndpoint = customEndpoint.equals("null") ? null : customEndpoint;
        openAIConnector.proxyHost = proxyHost.equals("null") ? null : proxyHost;
        openAIConnector.proxyPort = proxyPort;
        openAIConnector.proxyUsername = proxyUsername.equals("null") ? null : proxyUsername;
        openAIConnector.proxyPassword = proxyPassword.equals("null") ? null : proxyPassword;
        openAIConnector.proxyType = proxyType;
        return openAIConnector;
    }

    private OpenAiChat getOpenAiChat() {
        return new OpenAiChat(secretKey, customEndpoint, proxyHost, proxyPort, proxyUsername, proxyPassword, proxyType);
    }

    /**
     * 获取聊天 completions
     * @param chatMessages List<ChatMessage>
     * @param modelId String
     * @return ChatResponse
     */
    @NaslConnector.Logic
    public ChatResponse getChatCompletions(List<ChatMessage> chatMessages, String modelId) {
        OpenAiChat openAiChat = getOpenAiChat();
        return openAiChat.getChatCompletions(chatMessages, modelId);
    }

    /**
     * 获取模型列表
     * @return List<String>
     */
    @NaslConnector.Logic
    public List<String> listModels() {
        try {
            OpenAiChat openAiChat = getOpenAiChat();
            return openAiChat.listModelsName();
        } catch (Exception e) {
            LCAP_LOGGER.error("listModels error:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 completions
     * @param prompt String
     * @param modelId String
     * @return ChatResponse
     */
    @NaslConnector.Logic
    public ChatResponse getChatCompletionsWithPrompt(String prompt, String modelId) {
        OpenAiChat openAiChat = getOpenAiChat();
        return openAiChat.getChatCompletionsWithPrompt(prompt, modelId);
    }

    /**
     * 流式获取 completions
     * @param prompt String
     * @param modelId String
     * @param onMessageHandler Function<String,Boolean>
     * @param onFailureHandler Function<String,Boolean>
     * @param onCompleteHandler Function<String,Boolean>
     * @return Boolean
     */
    public Boolean getStreamChatCompletions(String prompt, String modelId,
                                            Function<String,Boolean> onMessageHandler,
                                            Function<String,Boolean> onFailureHandler,
                                            Function<String,Boolean> onCompleteHandler) {
        OpenAiChat openAiChat = getOpenAiChat();
        openAiChat.getStreamChatCompletions(prompt, modelId, onMessageHandler, onFailureHandler, onCompleteHandler);
        return true;
    }

    @NaslConnector.Tester
    public Boolean test(String secretKey, String customEndpoint,
                        String proxyHost, Integer proxyPort,
                        String proxyUsername, String proxyPassword, Integer proxyType) {
//        OpenAIConnector openAIConnector = new OpenAIConnector();
//        openAIConnector = openAIConnector.initBean(secretKey, customEndpoint, proxyHost, proxyPort, proxyUsername, proxyPassword, proxyType);
//        List<String> models = openAIConnector.listModels();
//        return !models.isEmpty();

        return Objects.nonNull(secretKey) && !secretKey.isEmpty();
    }

}
