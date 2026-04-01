package com.npcagent.rag;

import com.npcagent.model.DialogueOption;
import com.npcagent.model.DialogueResult;
import com.npcagent.model.SemanticMatchResult;
import com.npcagent.service.VectorStorageService;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * RAG (Retrieval-Augmented Generation) 故事管理系统
 * 
 * 本类实现了基于检索增强生成的游戏剧情管理系统，核心功能包括：
 * 1. 剧情节点管理：维护游戏中的所有剧情节点和触发条件
 * 2. 角色设定管理：管理每个NPC的性格、说话风格等特征
 * 3. 对话处理：根据玩家输入匹配剧情节点或生成自由对话
 * 4. 语义匹配：使用向量数据库进行语义相似度匹配
 * 
 * AI相关说明：
 * - 本系统采用"规则+AI"的混合模式
 * - 关键剧情节点通过规则匹配触发（关键词匹配）
 * - 自由对话通过AI大语言模型生成（Ollama API）
 * - 角色设定（CharacterSetting）作为Prompt Engineering的一部分传递给AI
 * - 语义匹配通过向量数据库实现，提高自由对话的准确性
 */
@Component
public class RAGStoryManager {
    
    /**
     * 剧情节点映射表
     * Key: 节点ID (如 "village_001")
     * Value: 剧情节点对象
     */
    private final Map<String, StoryNode> storyNodes;
    
    /**
     * 角色设定映射表
     * Key: 角色名称 (如 "老者", "青云子")
     * Value: 角色设定对象，用于AI生成对话时的角色扮演
     */
    private final Map<String, CharacterSetting> characterSettings;

    /**
     * 向量存储服务
     * 用于语义匹配和相似度计算
     */
    private final VectorStorageService vectorStorageService;

    /**
     * 对话历史存储
     * Key: playerId_npcCode
     * Value: 对话历史列表
     */
    private final Map<String, List<DialogueHistory>> dialogueHistoryStore;

    /**
     * 构造函数
     * 初始化时加载所有剧情节点和角色设定
     */
    public RAGStoryManager(VectorStorageService vectorStorageService) {
        this.vectorStorageService = vectorStorageService;
        this.storyNodes = loadStoryNodes();
        this.characterSettings = loadCharacterSettings();
        this.dialogueHistoryStore = new HashMap<>();
    }

    /**
     * 加载所有剧情节点
     * 
     * 剧情节点设计说明：
     * - 每个节点代表游戏中的一个关键剧情点
     * - 节点之间通过前置条件(preconditions)形成依赖关系
     * - 玩家输入通过关键词匹配(triggerKeywords)触发节点
     * - 节点类型分为：关键节点、引导节点、结束节点
     * 
     * @return 剧情节点映射表
     */
    private Map<String, StoryNode> loadStoryNodes() {
        Map<String, StoryNode> nodes = new HashMap<>();

        // 剧情节点列表 - 定义游戏的主线剧情流程
        List<StoryNode> nodeList = Arrays.asList(
                // 村庄起始节点 - 玩家初次与老者的对话
                new StoryNode("village_001", "关键节点", Collections.emptyList(),
                        Arrays.asList("拜师", "入门", "修仙", "求道", "学艺"),
                        "老者引导玩家了解修仙基础",
                        "（慈祥地看着你）年轻人有志向，修仙之路虽艰险，但若心诚，必有所成。老朽当年也是从青云门外门弟子做起，虽资质平平，但也略知一二。",
                        Arrays.asList("village_002"),
                        Collections.singletonList("获得：修仙入门知识"),
                        Collections.singletonList("无法进入青云门")),

                // 灵根测试节点 - 引导玩家前往聚灵台
                new StoryNode("village_002", "关键节点", Collections.singletonList("village_001"),
                        Arrays.asList("聚灵台", "灵根", "测试", "测灵"),
                        "玩家在聚灵台测试灵根",
                        "（指向镇东方向）镇东有一座废弃的聚灵台，虽已荒废，但仍能感应灵根。你若有心，可去一试。记住，修仙之路，九死一生，需有决心。",
                        Arrays.asList("village_003", "village_004"),
                        Collections.singletonList("获得：灵根测试结果"),
                        null),

                // 功法传授节点 - 正向引导
                new StoryNode("village_003", "引导节点", Collections.singletonList("village_002"),
                        Arrays.asList("功法", "修炼", "法术", "技能"),
                        "传授基础功法",
                        "（欣慰地点头）既然你有此天赋，老夫便传你一套《基础吐纳诀》，助你推开这第一扇仙门。",
                        Collections.singletonList("village_006"),
                        Collections.singletonList("获得：基础吐纳诀"),
                        null),

                // 鼓励节点 - 另一种正向引导
                new StoryNode("village_004", "引导节点", Collections.singletonList("village_002"),
                        Arrays.asList("努力", "坚持", "勤奋"),
                        "鼓励玩家坚持修炼",
                        "（拍拍你的肩膀）修仙一途，资质固然重要，但更重要的是恒心。古往今来，多少资质平庸者凭毅力登临绝顶。你若肯努力，未必不能成事。",
                        Collections.singletonList("village_006"),
                        Collections.singletonList("获得：修炼建议"),
                        null),

                // 放弃节点 - 负面结局分支
                new StoryNode("village_005", "关键节点", Collections.singletonList("village_002"),
                        Arrays.asList("放弃", "离开", "回去"),
                        "玩家选择放弃修仙",
                        "（叹了口气）修仙之路，本就非人人可走。你若心意已决，老夫也不强求。不过，这青牛镇虽小，却也安宁，你若愿意，可在此安身立命。",
                        Collections.singletonList("village_end"),
                        Collections.emptyList(),
                        Collections.singletonList("剧情结束")),

                // 前往青云门节点 - 进入下一阶段
                new StoryNode("village_006", "关键节点", Arrays.asList("village_003", "village_004"),
                        Arrays.asList("青云门", "上山", "拜见掌门"),
                        "前往青云门",
                        "（指向青云山方向）青云门就在那云雾缭绕的山巅。你既已入门，便可前往一试。记住，入门之后，便是真正的修仙之路，切莫懈怠。",
                        Collections.singletonList("mountain_001"),
                        Collections.singletonList("获得：青云门通行令"),
                        Collections.singletonList("无法进入青云门")),

                // 拜见掌门节点
                new StoryNode("mountain_001", "关键节点", Collections.singletonList("village_006"),
                        Arrays.asList("掌门", "拜见", "入门"),
                        "拜见青云门掌门",
                        "（青云子端坐于大殿之上，目光如电）年轻人，既已至此，便让我看看你的资质。",
                        Collections.singletonList("mountain_002"),
                        Collections.singletonList("获得：入门测试资格"),
                        null),

                // 师兄交流节点
                new StoryNode("mountain_002", "引导节点", Collections.singletonList("mountain_001"),
                        Arrays.asList("师兄", "师姐", "请教"),
                        "与师兄师姐交流",
                        "（师兄微笑着）师弟，既已入门，便要勤加修炼。若有不懂之处，尽管来问我。",
                        Collections.singletonList("mountain_003"),
                        Collections.singletonList("获得：修炼指导"),
                        null),

                // 门派任务节点
                new StoryNode("mountain_003", "关键节点", Collections.singletonList("mountain_002"),
                        Arrays.asList("任务", "试炼", "挑战"),
                        "接受门派任务",
                        "（掌门沉吟片刻）既如此，便给你一个试炼任务。去后山秘境，取回一枚灵石。此行虽不凶险，却也能磨炼心性。",
                        Collections.singletonList("cave_001"),
                        Collections.singletonList("获得：试炼任务"),
                        null),

                // 秘境探索节点
                new StoryNode("cave_001", "关键节点", Collections.singletonList("mountain_003"),
                        Arrays.asList("秘境", "洞穴", "探索"),
                        "探索神秘洞穴",
                        "（神秘老人从阴影中走出）年轻人，既已至此，便是有缘。这洞穴中藏有上古传承，你可愿一试？",
                        Collections.singletonList("cave_002"),
                        Collections.singletonList("获得：上古传承线索"),
                        null),

                // 传承获得节点
                new StoryNode("cave_002", "引导节点", Collections.singletonList("cave_001"),
                        Arrays.asList("传承", "功法", "宝物"),
                        "获得传承",
                        "（神秘老人点头）既如此，便传你这套《青云诀》。此乃上古传承，你需勤加修炼，莫要辜负了这份机缘。",
                        Collections.singletonList("end_001"),
                        Collections.singletonList("获得：青云诀"),
                        null),

                // 剧情完成节点
                new StoryNode("end_001", "关键节点", Collections.singletonList("cave_002"),
                        Arrays.asList("完成", "结束", "成就"),
                        "剧情完成",
                        "（老者欣慰地看着你）年轻人，恭喜你踏上了真正的修仙之路。但这只是开始，前路漫漫，还需你自行探索。老夫能做的，只有这些了。",
                        Collections.emptyList(),
                        Collections.singletonList("获得：修仙之路开启"),
                        null)
        );

        // 将节点列表转换为映射表，便于快速查找
        for (StoryNode node : nodeList) {
            nodes.put(node.getNodeId(), node);
        }

        return nodes;
    }

    /**
     * 加载所有角色设定
     * 
     * 角色设定说明：
     * - 每个NPC都有独特的性格、说话风格和常用词汇
     * - 这些设定用于构建AI Prompt，确保AI生成的对话符合角色特征
     * - 禁忌话题列表用于限制AI不要生成不符合角色设定的内容
     * 
     * AI提示工程说明：
     * - personality: 转化为"你是一个[性格特点]的人"
     * - speakingStyle: 转化为"你的说话风格是[风格描述]"
     * - commonVocabulary: 转化为"你经常使用以下词汇：[词汇列表]"
     * - tabooTopics: 转化为"你从不谈论以下话题：[话题列表]"
     * 
     * @return 角色设定映射表
     */
    private Map<String, CharacterSetting> loadCharacterSettings() {
        Map<String, CharacterSetting> settings = new HashMap<>();

        List<CharacterSetting> settingList = Arrays.asList(
                // 老者 - 引导型NPC，负责新手教学
                new CharacterSetting("elder", "老者", Arrays.asList("慈祥", "见多识广", "乐于助人"),
                        "古风、温和、带点仙侠气息，说话慢条斯理，喜欢用典故",
                        Arrays.asList("老朽", "年轻人", "修仙一途", "道友", "机缘"),
                        Arrays.asList("现代科技", "其他门派机密", "个人隐私")),

                // 青云子 - 权威型NPC，门派掌门
                new CharacterSetting("qingyunMaster", "青云子", Arrays.asList("威严", "公正", "看重资质"),
                        "庄重、简洁、不怒自威，说话有分量，不轻易表态",
                        Arrays.asList("本座", "弟子", "修为", "境界", "资质"),
                        Arrays.asList("门派秘辛", "过往恩怨", "私人情感")),

                // 师兄 - 友好型NPC，同辈交流
                new CharacterSetting("seniorBrother", "师兄", Arrays.asList("热情", "耐心", "乐于助人"),
                        "亲切、随和、像邻家大哥，说话直接，不拐弯抹角",
                        Arrays.asList("师弟", "师妹", "修炼", "功法", "师兄"),
                        Collections.emptyList()),

                // 神秘老人 - 神秘型NPC，隐藏剧情关键
                new CharacterSetting("mysteriousElder", "神秘老人", Arrays.asList("神秘", "高深", "看透世事"),
                        "玄奥、深邃、似有深意，说话喜欢打哑谜，让人捉摸不透",
                        Arrays.asList("机缘", "传承", "因果", "道", "天命"),
                        Arrays.asList("身份", "来历", "过去"))
        );

        for (CharacterSetting setting : settingList) {
            settings.put(setting.getCharacterCode(), setting);
        }

        return settings;
    }

    /**
     * 处理玩家对话输入
     * 
     * 核心逻辑：
     * 1. 首先检查是否匹配任何剧情节点的触发条件
     *    - 检查前置条件：玩家必须已完成所有前置节点
     *    - 检查触发关键词：玩家输入中是否包含节点的触发词
     * 2. 如果匹配到剧情节点，返回预设的剧情回复
     * 3. 如果没有匹配到，返回自由对话模式，由AI生成回复
     * 
     * AI决策流程：
     * - 规则优先：先尝试规则匹配，确保关键剧情不被AI随意改变
     * - AI补充：规则未命中时，使用AI生成符合角色设定的回复
     * - 上下文保持：通过dialogueHistory保持对话连贯性
     * 
     * @param npcCode 当前对话的NPC代码
     * @param playerInput 玩家输入的文本
     * @param dialogueHistory 对话历史记录，用于保持上下文
     * @param playerState 玩家当前状态（已完成节点、背包等）
     * @return RAGResult 包含回复类型、内容、奖励等信息
     */
    public RAGResult processDialogue(String npcCode, String playerInput, List<DialogueHistory> dialogueHistory, PlayerState playerState) {
        // 遍历所有剧情节点，检查是否满足触发条件
        for (StoryNode node : storyNodes.values()) {
            // 检查前置条件：玩家必须已完成所有前置节点
            boolean hasAllPreconditions = true;
            for (String precondition : node.getPreconditions()) {
                if (!playerState.getCompletedNodes().contains(precondition)) {
                    hasAllPreconditions = false;
                    break;
                }
            }

            // 前置条件不满足，跳过此节点
            if (!hasAllPreconditions) {
                continue;
            }

            // 检查触发关键词：玩家输入中是否包含节点的触发词
            for (String keyword : node.getTriggerKeywords()) {
                if (playerInput.contains(keyword)) {
                    // 匹配成功，返回剧情节点回复
                    RAGResult result = new RAGResult();
                    result.setResponseType("关键剧情");
                    result.setResponseContent(node.getNpcReplyTemplate());
                    result.setNodeId(node.getNodeId());
                    result.setRewards(node.getRewards());

                    return result;
                }
            }
        }

        // 未匹配到任何剧情节点，进入自由对话模式
        // 此时将使用AI生成回复，角色设定作为Prompt的一部分传递给AI
        RAGResult result = new RAGResult();
        result.setResponseType("自由对话");
        result.setCharacterSetting(characterSettings.get(npcCode));

        return result;
    }

    /**
     * 更新玩家状态
     * 
     * 当玩家完成一个剧情节点时，将该节点标记为已完成
     * 这会解锁后续依赖此节点的剧情
     * 
     * @param playerState 玩家状态对象
     * @param nodeId 已完成的节点ID
     */
    public void updatePlayerState(PlayerState playerState, String nodeId) {
        if (!playerState.getCompletedNodes().contains(nodeId)) {
            playerState.getCompletedNodes().add(nodeId);
        }
    }

    /**
     * 获取NPC的对话选项
     *
     * @param npcCode NPC代码
     * @param playerId 玩家ID
     * @return 对话选项列表
     */
    public List<DialogueOption> getDialogueOptions(String npcCode, String playerId) {
        List<DialogueOption> options = new ArrayList<>();
        
        // 根据NPC代码和玩家状态生成对话选项
        // 这里使用模拟数据
        if ("elder".equals(npcCode)) {
            DialogueOption option1 = new DialogueOption();
            option1.setOptionId("option1");
            option1.setText("我想拜师修仙");
            option1.setType("normal");
            option1.setNextNodeId("village_001");
            option1.setAvailable(true);
            options.add(option1);

            DialogueOption option2 = new DialogueOption();
            option2.setOptionId("option2");
            option2.setText("请问灵根测试在哪里？");
            option2.setType("normal");
            option2.setNextNodeId("village_002");
            option2.setAvailable(true);
            options.add(option2);
        }
        
        return options;
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
        
        // 根据选项ID和NPC代码生成对话结果
        // 这里使用模拟数据
        if ("elder".equals(npcCode)) {
            if ("option1".equals(optionId)) {
                result.setNpcResponse("（慈祥地看着你）年轻人有志向，修仙之路虽艰险，但若心诚，必有所成。老朽当年也是从青云门外门弟子做起，虽资质平平，但也略知一二。");
                result.setTriggeredNodeId("village_001");
                result.setRewards(Collections.singletonList("获得：修仙入门知识"));
                result.setDialogueType("fixed");
                result.setStoryAdvance(true);
            } else if ("option2".equals(optionId)) {
                result.setNpcResponse("（指向镇东方向）镇东有一座废弃的聚灵台，虽已荒废，但仍能感应灵根。你若有心，可去一试。记住，修仙之路，九死一生，需有决心。");
                result.setTriggeredNodeId("village_002");
                result.setRewards(Collections.singletonList("获得：灵根测试结果"));
                result.setDialogueType("fixed");
                result.setStoryAdvance(true);
            }
        }
        
        // 添加后续对话选项
        result.setNextOptions(getDialogueOptions(npcCode, playerId));
        
        return result;
    }

    /**
     * 处理语义匹配结果
     *
     * @param matchResult 语义匹配结果
     * @param playerId 玩家ID
     * @param dialogueHistory 对话历史
     * @return 对话结果
     */
    public DialogueResult processSemanticMatch(SemanticMatchResult matchResult, String playerId, List<DialogueHistory> dialogueHistory) {
        DialogueResult result = new DialogueResult();
        
        // 根据匹配的节点ID生成对话结果
        String nodeId = matchResult.getMatchedNodeId();
        StoryNode node = storyNodes.get(nodeId);
        
        if (node != null) {
            result.setNpcResponse(node.getNpcReplyTemplate());
            result.setTriggeredNodeId(nodeId);
            result.setRewards(node.getRewards());
            result.setDialogueType("semantic");
            result.setStoryAdvance(true);
        }
        
        return result;
    }

    /**
     * 生成通用回复
     *
     * @param npcCode NPC代码
     * @param playerInput 玩家输入
     * @param dialogueHistory 对话历史
     * @return 对话结果
     */
    public DialogueResult generateGenericResponse(String npcCode, String playerInput, List<DialogueHistory> dialogueHistory) {
        DialogueResult result = new DialogueResult();
        
        // 根据NPC代码生成通用回复
        // 这里使用模拟数据
        if ("elder".equals(npcCode)) {
            result.setNpcResponse("（微笑着）年轻人，你的话我不太明白。修仙之路，需要一步一个脚印。你可以问我关于修仙的基础问题，或者直接告诉我你想做什么。");
        } else if ("sectMaster".equals(npcCode)) {
            result.setNpcResponse("（威严地）有话直说，莫要拐弯抹角。");
        }
        
        result.setDialogueType("free");
        result.setStoryAdvance(false);
        
        return result;
    }

    /**
     * 获取对话历史
     *
     * @param playerId 玩家ID
     * @param npcCode NPC代码
     * @return 对话历史
     */
    public List<DialogueHistory> getDialogueHistory(String playerId, String npcCode) {
        String key = playerId + "_" + npcCode;
        return dialogueHistoryStore.getOrDefault(key, new ArrayList<>());
    }

    /**
     * 保存对话历史
     *
     * @param playerId 玩家ID
     * @param npcCode NPC代码
     * @param dialogueHistory 对话历史
     */
    public void saveDialogueHistory(String playerId, String npcCode, List<DialogueHistory> dialogueHistory) {
        String key = playerId + "_" + npcCode;
        dialogueHistoryStore.put(key, dialogueHistory);
    }
}
