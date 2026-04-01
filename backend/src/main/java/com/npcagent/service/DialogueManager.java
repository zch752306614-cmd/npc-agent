package com.npcagent.service;

import com.npcagent.model.StoryData;
import com.npcagent.rag.RAGStoryManager;
import com.npcagent.rag.DialogueHistory;
import com.npcagent.rag.PlayerState;
import com.npcagent.rag.RAGResult;
import com.npcagent.rag.CharacterSetting;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对话管理服务
 *
 * 本服务负责处理游戏中所有NPC对话相关的业务逻辑，包括：
 * 1. 获取NPC预设对话：从剧情数据中加载NPC的对话选项
 * 2. 生成NPC回复：根据玩家输入和对话历史，生成符合角色设定的回复
 * 3. 管理玩家状态：跟踪玩家的游戏进度和状态
 *
 * AI集成说明：
 * - 本服务使用Ollama API（本地部署的大语言模型）进行AI对话生成
 * - AI模型：qwen3.5（通义千问3.5）
 * - 通过Prompt Engineering技术，将角色设定、对话历史、游戏规则等信息
 *   组合成完整的Prompt传递给AI，确保生成的回复符合游戏世界观和角色设定
 */
@Service
public class DialogueManager {

    /**
     * 剧情数据对象，包含所有场景、NPC、对话等信息
     */
    private final StoryData storyData;

    /**
     * RAG故事管理器，负责剧情节点匹配和角色设定管理
     */
    private final RAGStoryManager ragManager;

    /**
     * 玩家状态映射表
     * Key: 玩家ID
     * Value: 玩家状态对象（包含已完成节点、背包等信息）
     *
     * 注意：当前为内存存储，生产环境应使用数据库持久化
     */
    private final Map<String, PlayerState> playerStates;

    /**
     * HTTP客户端，用于调用Ollama API
     */
    private final RestTemplate restTemplate;

    /**
     * Ollama API的基础URL
     * 默认地址：http://localhost:11434
     * 请确保本地已安装Ollama并运行
     */
    private static final String OLLAMA_BASE_URL = "http://localhost:11434";

    /**
     * 使用的AI模型名称
     * 当前使用：qwen3.5（通义千问3.5）
     * 可通过Ollama pull命令下载其他模型
     */
    private static final String AI_MODEL = "qwen3.5";

    /**
     * 构造函数
     *
     * @param storyData 剧情数据对象
     * @param ragManager RAG故事管理器
     */
    public DialogueManager(StoryData storyData, RAGStoryManager ragManager) {
        this.storyData = storyData;
        this.ragManager = ragManager;
        this.playerStates = new HashMap<>();
        this.restTemplate = new RestTemplate();
    }

    /**
     * 获取指定NPC的对话列表
     *
     * 根据NPC代码从剧情数据中获取对应的对话选项
     * 用于游戏界面显示NPC的初始对话选项
     *
     * @param npcCode NPC代码（如 "elder", "villagerA" 等）
     * @return 对话列表，包含对话ID、文本、选项等信息
     */
    public List<StoryData.Dialogue> getNpcDialogues(String npcCode) {
        switch (npcCode) {
            case "elder":
                return storyData.getNpcs().getElder().getDialogues();
            case "villagerA":
                return storyData.getNpcs().getVillagerA().getDialogues();
            case "pharmacyOwner":
                return storyData.getNpcs().getPharmacyOwner().getDialogues();
            case "sectMaster":
                return storyData.getNpcs().getSectMaster().getDialogues();
            case "seniorBrother":
                return storyData.getNpcs().getSeniorBrother().getDialogues();
            case "seniorSister":
                return storyData.getNpcs().getSeniorSister().getDialogues();
            case "mysteriousElder":
                return storyData.getNpcs().getMysteriousElder().getDialogues();
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 生成NPC回复
     *
     * 核心方法，处理玩家输入并生成NPC的回复。处理流程：
     * 1. 获取或创建玩家状态
     * 2. 转换对话历史格式
     * 3. 调用RAG管理器进行剧情节点匹配
     * 4. 如果匹配到剧情节点，返回预设回复
     * 5. 如果未匹配到，调用AI生成自由对话回复
     *
     * AI生成流程：
     * - 构建包含角色设定、对话历史、游戏规则的Prompt
     * - 调用Ollama API生成回复
     * - 返回AI生成的回复内容
     *
     * @param npcCode 当前对话的NPC代码
     * @param playerInput 玩家输入的文本
     * @param dialogueHistory 对话历史记录（原始格式）
     * @param playerId 玩家唯一标识
     * @return NPC的回复文本
     */
    public String generateResponse(String npcCode, String playerInput, List<Map<String, String>> dialogueHistory, String playerId) {
        // 获取或创建玩家状态
        // 如果玩家ID不存在，自动创建新的玩家状态对象
        PlayerState playerState = playerStates.computeIfAbsent(playerId, id -> new PlayerState());

        // 转换对话历史格式
        // 将前端传来的Map格式转换为内部使用的DialogueHistory对象列表
        List<DialogueHistory> history = new ArrayList<>();
        for (Map<String, String> msg : dialogueHistory) {
            DialogueHistory dh = new DialogueHistory();
            dh.setSpeaker(msg.get("speaker"));
            dh.setText(msg.get("text"));
            history.add(dh);
        }

        // 调用RAG管理器处理对话
        // RAG管理器会尝试匹配剧情节点，如果匹配失败则返回自由对话模式
        RAGResult ragResult = ragManager.processDialogue(npcCode, playerInput, history, playerState);

        // 处理关键剧情回复
        // 如果匹配到剧情节点，更新玩家状态并返回预设回复
        if ("关键剧情".equals(ragResult.getResponseType()) || "引导剧情".equals(ragResult.getResponseType())) {
            // 更新玩家状态，标记该节点为已完成
            if (ragResult.getNodeId() != null) {
                ragManager.updatePlayerState(playerState, ragResult.getNodeId());
            }

            // 构建回复内容
            String response = ragResult.getResponseContent();

            // 如果有奖励，追加到回复中
            if (ragResult.getRewards() != null && !ragResult.getRewards().isEmpty()) {
                response += "\n\n【系统提示】" + String.join("、", ragResult.getRewards());
            }

            return response;
        }

        // 处理自由对话回复
        // 未匹配到剧情节点，调用AI生成回复
        return generateFreeResponse(npcCode, playerInput, history, ragResult.getCharacterSetting());
    }

    /**
     * 生成自由对话回复（AI生成）
     *
     * 核心AI方法，通过Prompt Engineering技术构建完整的Prompt，
     * 调用Ollama API生成符合角色设定的回复。
     *
     * Prompt构建策略：
     * 1. 角色设定：描述NPC的身份、性格、背景故事
     * 2. 角色扮演指令：告诉AI扮演该角色
     * 3. 游戏规则：限制AI的回复范围，确保符合游戏世界观
     * 4. 对话历史：提供上下文，保持对话连贯性
     * 5. 当前输入：玩家的最新输入
     *
     * AI参数设置：
     * - temperature: 0.7（平衡创造性和一致性）
     * - num_predict: 800（最大生成token数）
     * - stream: false（非流式输出）
     * - think: false（不显示思考过程）
     *
     * @param npcCode NPC代码
     * @param playerInput 玩家输入文本
     * @param dialogueHistory 对话历史记录
     * @param characterSetting 角色设定信息
     * @return AI生成的回复文本
     */
    private String generateFreeResponse(String npcCode, String playerInput, List<DialogueHistory> dialogueHistory,
                                        CharacterSetting characterSetting) {
        // 构建Prompt
        StringBuilder prompt = new StringBuilder();

        // 根据NPC代码添加角色设定
        // 每个NPC都有独特的身份、性格、背景故事
        switch (npcCode) {
            case "elder":
                prompt.append("你是老者，慈祥、见多识广、乐于助人。青牛镇的老居民，曾经是青云门的外门弟子，因资质有限未能更进一步。\n");
                break;
            case "villagerA":
                prompt.append("你是村民甲，热情、朴实、好奇。青牛镇的普通村民，世代居住于此。\n");
                break;
            case "pharmacyOwner":
                prompt.append("你是药铺老板，精明、势利、见多识广。青牛镇药铺的老板，经常与修仙者打交道。\n");
                break;
            case "sectMaster":
                prompt.append("你是青云子，威严、公正、看重资质。青云门掌门，金丹期修士，掌管门派已有百年。\n");
                break;
            case "seniorBrother":
                prompt.append("你是林峰，开朗、热心、好胜。青云门内门弟子，筑基期修士，入门已有十年。\n");
                break;
            case "seniorSister":
                prompt.append("你是林小婉，温柔、善良、细心。青云门内门弟子，筑基期修士，擅长炼丹。\n");
                break;
            case "mysteriousElder":
                prompt.append("你是神秘老人，神秘、高深、孤傲。隐居在神秘洞穴中的大能，疑似元婴期修士。\n");
                break;
        }

        // 添加角色设定信息（从CharacterSetting对象）
        // 这些信息用于增强AI的角色扮演能力
        if (characterSetting != null) {
            prompt.append("性格特点：").append(String.join("、", characterSetting.getPersonality())).append("\n");
            prompt.append("说话风格：").append(characterSetting.getSpeakingStyle()).append("\n");
            prompt.append("常用词汇：").append(String.join("、", characterSetting.getCommonVocabulary())).append("\n");
            if (!characterSetting.getTabooTopics().isEmpty()) {
                prompt.append("禁忌话题：").append(String.join("、", characterSetting.getTabooTopics())).append("\n");
            }
        }

        // 添加游戏规则和任务指令
        // 限制AI的回复范围，确保符合游戏世界观
        prompt.append("你正在和一个想要修仙的年轻人对话。\n");
        prompt.append("当前剧情：初入仙途 - 从凡人到修仙者的第一步\n");
        prompt.append("你的任务是：\n");
        prompt.append("1. 保持符合你人格的对话风格\n");
        prompt.append("2. 自然地回答玩家的问题\n");
        prompt.append("3. 使用符合修仙世界的语言和表达方式\n");
        prompt.append("4. 不要说任何现代或不符合修仙世界的内容\n");
        prompt.append("5. 保持对话的趣味性和互动性\n");
        prompt.append("对话历史：\n");

        // 添加对话历史（最近10条）
        // 保持上下文连贯性，让AI理解对话的进展
        List<DialogueHistory> recentHistory = dialogueHistory.size() > 10 ?
                dialogueHistory.subList(dialogueHistory.size() - 10, dialogueHistory.size()) : dialogueHistory;

        for (DialogueHistory msg : recentHistory) {
            if ("player".equals(msg.getSpeaker())) {
                prompt.append("年轻人：").append(msg.getText()).append("\n");
            } else {
                prompt.append(npcCode).append("：").append(msg.getText()).append("\n");
            }
        }

        // 添加当前玩家输入
        prompt.append("年轻人：").append(playerInput).append("\n");
        prompt.append(npcCode).append("：");

        // 调用Ollama API生成回复
        try {
            String ollamaUrl = OLLAMA_BASE_URL + "/api/generate";

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", AI_MODEL);
            requestBody.put("prompt", prompt.toString());
            requestBody.put("stream", false);
            requestBody.put("think", false);

            // 设置生成参数
            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.7);  // 温度参数，控制创造性（0.0-1.0）
            options.put("num_predict", 800);  // 最大生成token数
            requestBody.put("options", options);

            // 设置HTTP请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送HTTP POST请求到Ollama API
            Map<String, Object> response = restTemplate.exchange(
                    ollamaUrl, HttpMethod.POST, entity, Map.class).getBody();

            // 解析响应，提取生成的文本
            if (response != null && response.containsKey("response")) {
                return response.get("response").toString().trim();
            }
        } catch (Exception e) {
            // 异常处理：记录错误日志
            // 生产环境应使用日志框架（如SLF4J）替代printStackTrace
            e.printStackTrace();
        }

        // 异常情况下的默认回复
        return "抱歉，我暂时无法回答你的问题。";
    }

    /**
     * 获取玩家状态
     *
     * 根据玩家ID获取对应的玩家状态对象
     * 如果玩家ID不存在，自动创建新的玩家状态
     *
     * @param playerId 玩家唯一标识
     * @return 玩家状态对象
     */
    public PlayerState getPlayerState(String playerId) {
        return playerStates.computeIfAbsent(playerId, id -> new PlayerState());
    }
}
