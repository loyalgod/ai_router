/**
 * 简单示例
 *
 * @author ai-router
 */
package com.airouter.sdk.example;

import com.airouter.sdk.AiRouterClient;
import com.airouter.sdk.model.ChatResponse;

/**
 * 最简单的使用示例
 */
public class SimpleExample {

    public static void main(String[] args) {
        // 创建客户端
        AiRouterClient client = AiRouterClient.builder()
                .apiKey("sk-d8483dd850cf44729f41cb59f7bc1e1d")  // 替换为你的 API Key
                .baseUrl("http://localhost:8123/api")
                .build();

        try {
            // 同步调用
            ChatResponse response = client.chat("你好，请介绍一下自己");
            System.out.println("响应: " + response.getContent());

            // Token 使用统计
            System.out.println("\nToken 统计:");
            System.out.println("输入: " + response.getUsage().getPromptTokens());
            System.out.println("输出: " + response.getUsage().getCompletionTokens());
            System.out.println("总计: " + response.getUsage().getTotalTokens());

        } finally {
            // 关闭客户端
            client.close();
        }
    }
}
