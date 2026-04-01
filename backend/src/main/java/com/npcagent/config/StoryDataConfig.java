package com.npcagent.config;

import com.npcagent.model.StoryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

/**
 * 剧情数据配置类
 *
 * 本配置类负责加载游戏的剧情数据：
 * 1. 从story-data.json文件读取JSON数据
 * 2. 使用Jackson库将JSON反序列化为StoryData对象
 * 3. 将StoryData对象注册为Spring Bean，供其他组件注入使用
 *
 * 数据加载流程：
 * - 应用启动时，Spring容器会调用storyData()方法
 * - 从classpath加载story-data.json文件
 * - 解析JSON并映射到StoryData模型类
 * - 返回的StoryData对象可被DialogueManager等服务注入使用
 */
@Configuration
public class StoryDataConfig {

    /**
     * 创建StoryData Bean
     *
     * 该方法在Spring应用启动时执行，加载剧情数据到内存中
     *
     * @return StoryData对象，包含所有场景、NPC、对话等数据
     * @throws IOException 当读取JSON文件失败时抛出
     */
    @Bean
    public StoryData storyData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getResourceAsStream("/story-data.json")) {
            return objectMapper.readValue(inputStream, StoryData.class);
        }
    }
}
