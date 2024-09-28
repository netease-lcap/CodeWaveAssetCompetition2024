package com.fdddf.openai;

import lombok.SneakyThrows;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSourceListener;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class ConsoleEventSourceListener extends EventSourceListener {

    public Function<String, Boolean> onMessageHandler;
    public Function<String, Boolean> onFailureHandler;
    public Function<String, Boolean> onCompleteHandler;

    public CountDownLatch countDownLatch;

    @Override
    public void onOpen(okhttp3.sse.EventSource eventSource, Response response) {
        System.out.println("EventSource opened!");
    }

    @Override
    public void onClosed(okhttp3.sse.EventSource eventSource) {
        System.out.println("EventSource closed!");
        countDownLatch.countDown();
        if (onCompleteHandler != null) {
            onCompleteHandler.apply("");
        }
    }

    @Override
    public void onEvent(okhttp3.sse.EventSource eventSource, String id, String type, String data) {
//        System.out.println("EventSource received: " + data);
        if (data.equals("[DONE]")) {
            countDownLatch.countDown();
            return;
        }
        if (onMessageHandler != null) {
            onMessageHandler.apply(data);
        }
    }

    @SneakyThrows
    @Override
    public void onFailure(okhttp3.sse.EventSource eventSource, Throwable t, Response response) {
        System.out.println("EventSource failed!");
        if (onFailureHandler != null) {
            onFailureHandler.apply(t.toString());
        }
        if (Objects.isNull(response)) {
            countDownLatch.countDown();
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            System.out.println("Response none null: " + body.string());
            countDownLatch.countDown();
            return;
        } else {
            System.out.println("Response error: " + t.toString());
        }
        eventSource.cancel();
        countDownLatch.countDown();
   }
}
