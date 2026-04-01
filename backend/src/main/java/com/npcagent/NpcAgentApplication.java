package com.npcagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NPC Agent 应用程序入口
 *
 * 本项目是一个基于Spring Boot的修仙游戏NPC对话系统，核心功能包括：
 * 1. 剧情管理：通过JSON文件加载游戏场景、NPC、对话等数据
 * 2. RAG系统：基于规则的剧情节点匹配 + AI自由对话生成
 * 3. AI集成：使用Ollama API（本地大语言模型）生成NPC回复
 * 4. 角色扮演：每个NPC都有独特的性格、说话风格和背景故事
 *
 * 技术栈：
 * - Spring Boot 2.7.x：Web框架
 * - Jackson：JSON数据处理
 * - Lombok：代码简化
 * - Ollama API：AI对话生成（需要本地部署）
 *
 * AI说明：
 * - 使用通义千问3.5（qwen3.5）模型
 * - 通过Prompt Engineering技术实现角色扮演
 * - 支持上下文保持（最近10条对话历史）
 *
 * 启动前准备：
 * 1. 确保已安装Ollama并运行（默认端口11434）
 * 2. 确保已下载qwen3.5模型：ollama pull qwen3.5
 * 3. 确保story-data.json文件位于resources目录
 */
@SpringBootApplication
public class NpcAgentApplication {

    /**
     * 应用程序入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(NpcAgentApplication.class, args);
    }
}
