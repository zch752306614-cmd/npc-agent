package com.npcagent.rag;

import com.npcagent.model.DialogueOption;
import com.npcagent.model.DialogueResult;
import com.npcagent.model.StoryNode;
import com.npcagent.model.NpcCharacter;
import com.npcagent.service.AiService;
import com.npcagent.service.VectorStorageService;
import com.npcagent.service.StoryNodeService;
import com.npcagent.service.NpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * RAG (Retrieval-Augmented Generation) 故事管理系统
 *
 * 基于Embedding + LLM的完整RAG实现：
 *
 * 标准RAG流程：
 * 1. 检索(Retrieval): 玩家输入 → Embedding → 向量数据库检索 → 获取相关剧情节点
 * 2. 增强(Augmentation): 将检索到的剧情节点作为上下文(Context)
 * 3. 生成(Generation): Context + 角色设定 + 对话历史 → LLM → 生成回复
 *
 * 与传统实现的区别：
 * - 传统：关键词匹配 → 硬编码回复
 * - RAG：语义检索 → 动态生成回复
 *
 * AI相关说明：
 * - Embedding模型：bge-m3 (通过Ollama本地部署)
 * - LLM模型：qwen3.5 (通过Ollama本地部署)
 * - 向量数据库：Milvus
 */
@Component
public class RAGStoryManager {

    private static final Logger logger = LoggerFactory.getLogger(RAGStoryManager.class);

    /**
     * 向量存储服务
     * 用于语义检索 - RAG的Retrieval部分
     */
    private final VectorStorageService vectorStorageService;

    /**
     * AI服务
     * 用于生成回复 - RAG的Generation部分
     */
    private final AiService aiService;

    /**
     * 剧情节点服务
     * 从数据库加载剧情节点，替代硬编码
     */
    private final StoryNodeService storyNodeService;

    /**
     * NPC服务
     * 从数据库加载角色设定，替代硬编码
     */
    private final NpcService npcService;

    /**
     * 对话历史存储
     * Key: playerId_npcCode
     * Value: 对话历史列表
     */
    private final Map<String, List<DialogueHistory>> dialogueHistoryStore;

    /**
     * 相似度阈值
     * 低于此阈值的检索结果将被忽略
     */
    private static final double SIMILARITY_THRESHOLD = 0.75;

    /**
     * 最大检索结果数
     */
    private static final int MAX_RETRIEVAL_RESULTS = 3;

    public RAGStoryManager(VectorStorageService vectorStorageService,
                          AiService aiService,
                          StoryNodeService storyNodeService,
                          NpcService npcService) {
        this.vectorStorageService = vectorStorageService;
        this.aiService = aiService;
        this.storyNodeService = storyNodeService;
        this.npcService = npcService;
        this.dialogueHistoryStore = new HashMap<>();
    }

    /**
     * 处理玩家对话输入 - 完整RAG流程
     *
     * RAG流程：
     * 1. Retrieval: 将玩家输入转换为Embedding，检索相似剧情节点
     * 2. Augmentation: 构建包含检索结果的Prompt
     * 3. Generation: 使用LLM生成符合角色设定的回复
     *
     * @param npcCode 当前对话的NPC代码
     * @param playerInput 玩家输入的文本
     * @param playerId 玩家ID
     * @return RAGResult 包含回复内容、触发的剧情节点等信息
     */
    public RAGResult processDialogue(String npcCode, String playerInput, String playerId) {
        logger.debug("Processing dialogue - NPC: {}, Input: {}", npcCode, playerInput);

        // Step 1: Retrieval - 语义检索相关剧情节点
        List<RetrievedNode> retrievedNodes = retrieveRelevantNodes(playerInput, npcCode);
        logger.debug("Retrieved {} relevant nodes", retrievedNodes.size());

        // Step 2: 获取角色设定
        NpcCharacter npcCharacter = npcService.getNpcByCode(npcCode);
        if (npcCharacter == null) {
            logger.warn("NPC not found: {}", npcCode);
            npcCharacter = createDefaultCharacter(npcCode);
        }

        // Step 3: 获取对话历史
        List<DialogueHistory> dialogueHistory = getDialogueHistory(playerId, npcCode);

        // Step 4: 判断是否应该触发剧情节点
        Optional<RetrievedNode> bestMatch = findBestMatchingNode(retrievedNodes, playerInput);

        RAGResult result = new RAGResult();

        if (bestMatch.isPresent() && bestMatch.get().getSimilarity() > SIMILARITY_THRESHOLD) {
            // 高相似度匹配 - 触发剧情节点
            RetrievedNode node = bestMatch.get();
            logger.info("Triggering story node: {} with similarity: {}",
                    node.getNodeId(), node.getSimilarity());

            result.setResponseType("关键剧情");
            result.setTriggeredNodeId(node.getNodeId());
            result.setStoryAdvance(true);

            // 使用RAG生成剧情回复（结合检索到的节点内容）
            String response = generateStoryResponse(npcCharacter, node, playerInput, dialogueHistory);
            result.setResponseContent(response);

        } else {
            // 低相似度 - 自由对话模式
            logger.debug("No high-similarity match found, entering free dialogue mode");

            result.setResponseType("自由对话");
            result.setStoryAdvance(false);

            // 使用RAG生成自由回复（可能包含低相似度的检索结果作为参考）
            String response = generateFreeResponse(npcCharacter, retrievedNodes, playerInput, dialogueHistory);
            result.setResponseContent(response);
        }

        // Step 5: 更新对话历史
        updateDialogueHistory(playerId, npcCode, playerInput, result.getResponseContent());

        return result;
    }

    /**
     * 检索相关剧情节点 - RAG的Retrieval阶段
     *
     * 流程：
     * 1. 将玩家输入转换为Embedding向量
     * 2. 在向量数据库中检索相似向量
     * 3. 获取对应的剧情节点详情
     *
     * @param playerInput 玩家输入
     * @param npcCode NPC代码（用于过滤）
     * @return 检索到的剧情节点列表
     */
    private List<RetrievedNode> retrieveRelevantNodes(String playerInput, String npcCode) {
        List<RetrievedNode> results = new ArrayList<>();

        try {
            // 使用向量存储服务检索相似节点
            List<Map<String, Object>> searchResults = vectorStorageService.searchSimilar(
                    playerInput, MAX_RETRIEVAL_RESULTS);

            for (Map<String, Object> searchResult : searchResults) {
                String nodeId = (String) searchResult.get("nodeId");
                Double similarity = (Double) searchResult.get("similarity");

                if (nodeId != null && similarity != null) {
                    // 从数据库获取完整的剧情节点信息
                    StoryNode node = storyNodeService.getStoryNodeById(nodeId);
                    if (node != null) {
                        results.add(new RetrievedNode(node, similarity));
                    }
                }
            }

            // 按相似度排序
            results.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));

        } catch (Exception e) {
            logger.error("Error retrieving relevant nodes: {}", e.getMessage());
        }

        return results;
    }

    /**
     * 查找最佳匹配的剧情节点
     *
     * 除了相似度，还考虑：
     * - 前置条件是否满足
     * - 是否已触发过
     *
     * @param retrievedNodes 检索到的节点
     * @param playerInput 玩家输入
     * @return 最佳匹配的节点
     */
    private Optional<RetrievedNode> findBestMatchingNode(List<RetrievedNode> retrievedNodes, String playerInput) {
        // 这里可以添加更多逻辑，比如检查前置条件
        // 目前简单返回相似度最高的
        return retrievedNodes.stream()
                .filter(node -> node.getSimilarity() > SIMILARITY_THRESHOLD)
                .findFirst();
    }

    /**
     * 生成剧情回复 - 基于检索到的剧情节点
     *
     * Prompt构建：
     * - 角色设定
     * - 剧情节点内容（Context）
     * - 对话历史
     * - 玩家输入
     *
     * @param npcCharacter NPC角色
     * @param retrievedNode 检索到的剧情节点
     * @param playerInput 玩家输入
     * @param dialogueHistory 对话历史
     * @return 生成的回复
     */
    private String generateStoryResponse(NpcCharacter npcCharacter, RetrievedNode retrievedNode,
                                        String playerInput, List<DialogueHistory> dialogueHistory) {
        // 构建RAG Prompt
        StringBuilder prompt = new StringBuilder();

        // 角色设定
        prompt.append("你是").append(npcCharacter.getName()).append("。\n");
        prompt.append("性格特点：").append(npcCharacter.getPersonality()).append("\n");
        prompt.append("说话风格：").append(npcCharacter.getSpeakingStyle()).append("\n\n");

        // 检索到的剧情节点作为Context
        prompt.append("【当前剧情背景】\n");
        prompt.append(retrievedNode.getNode().getDescription()).append("\n");
        prompt.append("剧情内容：").append(retrievedNode.getNode().getContent()).append("\n\n");

        // 对话历史
        if (!dialogueHistory.isEmpty()) {
            prompt.append("【对话历史】\n");
            int historySize = Math.min(dialogueHistory.size(), 10); // 最多取最近10条记录（5轮对话）
            for (int i = dialogueHistory.size() - historySize; i < dialogueHistory.size(); i++) {
                DialogueHistory history = dialogueHistory.get(i);
                if ("player".equals(history.getSpeaker()) && history.getPlayerInput() != null) {
                    prompt.append("玩家：").append(history.getPlayerInput()).append("\n");
                } else if ("npc".equals(history.getSpeaker()) && history.getNpcResponse() != null) {
                    prompt.append("你：").append(history.getNpcResponse()).append("\n");
                }
            }
            prompt.append("\n");
        }

        // 玩家输入
        prompt.append("【玩家当前说的话】\n");
        prompt.append(playerInput).append("\n\n");

        // 生成要求
        prompt.append("【回复要求】\n");
        prompt.append("1. 根据剧情背景和角色设定回复\n");
        prompt.append("2. 保持角色性格一致\n");
        prompt.append("3. 回复要自然，符合修仙世界观\n");
        prompt.append("4. 不要提及这是游戏或AI\n");

        // 调用LLM生成回复
        return aiService.generateResponse(prompt.toString());
    }

    /**
     * 生成自由回复 - 没有触发剧情节点时的回复
     *
     * @param npcCharacter NPC角色
     * @param retrievedNodes 检索到的节点（可能作为参考）
     * @param playerInput 玩家输入
     * @param dialogueHistory 对话历史
     * @return 生成的回复
     */
    private String generateFreeResponse(NpcCharacter npcCharacter, List<RetrievedNode> retrievedNodes,
                                       String playerInput, List<DialogueHistory> dialogueHistory) {
        // 构建RAG Prompt
        StringBuilder prompt = new StringBuilder();

        // 角色设定
        prompt.append("你是").append(npcCharacter.getName()).append("。\n");
        prompt.append("性格特点：").append(npcCharacter.getPersonality()).append("\n");
        prompt.append("说话风格：").append(npcCharacter.getSpeakingStyle()).append("\n\n");

        // 可选：低相似度的检索结果作为参考（可选）
        if (!retrievedNodes.isEmpty() && retrievedNodes.get(0).getSimilarity() > 0.5) {
            prompt.append("【相关背景参考】\n");
            prompt.append(retrievedNodes.get(0).getNode().getDescription()).append("\n\n");
        }

        // 对话历史
        if (!dialogueHistory.isEmpty()) {
            prompt.append("【对话历史】\n");
            int historySize = Math.min(dialogueHistory.size(), 10); // 最多取最近10条记录（5轮对话）
            for (int i = dialogueHistory.size() - historySize; i < dialogueHistory.size(); i++) {
                DialogueHistory history = dialogueHistory.get(i);
                if ("player".equals(history.getSpeaker()) && history.getPlayerInput() != null) {
                    prompt.append("玩家：").append(history.getPlayerInput()).append("\n");
                } else if ("npc".equals(history.getSpeaker()) && history.getNpcResponse() != null) {
                    prompt.append("你：").append(history.getNpcResponse()).append("\n");
                }
            }
            prompt.append("\n");
        }

        // 玩家输入
        prompt.append("【玩家说的话】\n");
        prompt.append(playerInput).append("\n\n");

        // 生成要求
        prompt.append("【回复要求】\n");
        prompt.append("1. 以角色身份自然回复\n");
        prompt.append("2. 保持角色性格一致\n");
        prompt.append("3. 回复要符合修仙世界观\n");
        prompt.append("4. 可以引导玩家探索剧情\n");

        return aiService.generateResponse(prompt.toString());
    }

    /**
     * 处理固定选项选择
     *
     * @param npcCode NPC代码
     * @param optionId 选项ID
     * @param playerId 玩家ID
     * @return 对话结果
     */
    public DialogueResult processFixedOption(String npcCode, String optionId, String playerId) {
        DialogueResult result = new DialogueResult();

        // 从数据库获取选项信息
        DialogueOption option = storyNodeService.getDialogueOptionById(optionId);
        if (option == null) {
            logger.warn("Dialogue option not found: {}", optionId);
            result.setNpcResponse("（沉默）");
            return result;
        }

        // 获取NPC角色
        NpcCharacter npcCharacter = npcService.getNpcByCode(npcCode);
        if (npcCharacter == null) {
            npcCharacter = createDefaultCharacter(npcCode);
        }

        // 获取对话历史
        List<DialogueHistory> dialogueHistory = getDialogueHistory(playerId, npcCode);

        // 使用RAG生成回复
        String response = generateOptionResponse(npcCharacter, option, dialogueHistory);

        result.setNpcResponse(response);
        result.setTriggeredNodeId(option.getNextNodeId());
        result.setDialogueType("fixed");
        result.setStoryAdvance(option.getNextNodeId() != null);

        // 更新对话历史
        updateDialogueHistory(playerId, npcCode, option.getText(), response);

        return result;
    }

    /**
     * 基于选项生成回复
     */
    private String generateOptionResponse(NpcCharacter npcCharacter, DialogueOption option,
                                         List<DialogueHistory> dialogueHistory) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("你是").append(npcCharacter.getName()).append("。\n");
        prompt.append("性格特点：").append(npcCharacter.getPersonality()).append("\n");
        prompt.append("说话风格：").append(npcCharacter.getSpeakingStyle()).append("\n\n");

        prompt.append("【玩家选择了】\n");
        prompt.append(option.getText()).append("\n\n");

        if (option.getResponseTemplate() != null) {
            prompt.append("【剧情内容参考】\n");
            prompt.append(option.getResponseTemplate()).append("\n\n");
        }

        prompt.append("【回复要求】\n");
        prompt.append("1. 根据玩家的选择回复\n");
        prompt.append("2. 保持角色性格\n");
        prompt.append("3. 自然流畅，符合修仙世界观\n");

        return aiService.generateResponse(prompt.toString());
    }

    /**
     * 获取NPC的对话选项
     *
     * @param npcCode NPC代码
     * @param playerId 玩家ID
     * @return 对话选项列表
     */
    public List<DialogueOption> getDialogueOptions(String npcCode, String playerId) {
        // 从数据库获取当前NPC的对话选项
        return storyNodeService.getAvailableDialogueOptions(npcCode, playerId);
    }

    /**
     * 获取对话历史
     */
    private List<DialogueHistory> getDialogueHistory(String playerId, String npcCode) {
        String key = playerId + "_" + npcCode;
        return dialogueHistoryStore.getOrDefault(key, new ArrayList<>());
    }

    /**
     * 更新对话历史
     */
    private void updateDialogueHistory(String playerId, String npcCode,
                                      String playerInput, String npcResponse) {
        String key = playerId + "_" + npcCode;
        List<DialogueHistory> history = dialogueHistoryStore.computeIfAbsent(key, k -> new ArrayList<>());

        // 添加玩家输入历史
        DialogueHistory playerDialogue = new DialogueHistory();
        playerDialogue.setPlayerInput(playerInput);
        playerDialogue.setSpeaker("player");
        playerDialogue.setTimestamp(System.currentTimeMillis());
        history.add(playerDialogue);

        // 添加NPC回复历史
        DialogueHistory npcDialogue = new DialogueHistory();
        npcDialogue.setNpcResponse(npcResponse);
        npcDialogue.setSpeaker("npc");
        npcDialogue.setTimestamp(System.currentTimeMillis());
        history.add(npcDialogue);

        // 限制历史记录数量，避免Prompt过长
        if (history.size() > 20) {
            history.remove(0);
        }
    }

    /**
     * 创建默认角色（当数据库中找不到时）
     */
    private NpcCharacter createDefaultCharacter(String npcCode) {
        NpcCharacter character = new NpcCharacter();
        character.setCode(npcCode);
        character.setName("神秘人");
        character.setPersonality("神秘");
        character.setSpeakingStyle("说话简洁，意味深长");
        return character;
    }

    /**
     * 检索到的节点包装类
     */
    private static class RetrievedNode {
        private final StoryNode node;
        private final double similarity;

        public RetrievedNode(StoryNode node, double similarity) {
            this.node = node;
            this.similarity = similarity;
        }

        public StoryNode getNode() {
            return node;
        }

        public double getSimilarity() {
            return similarity;
        }

        public String getNodeId() {
            return node.getNodeId();
        }
    }
}
