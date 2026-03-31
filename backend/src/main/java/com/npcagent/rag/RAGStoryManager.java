package com.npcagent.rag;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RAGStoryManager {
    private final Map<String, StoryNode> storyNodes;
    private final Map<String, CharacterSetting> characterSettings;

    public RAGStoryManager() {
        this.storyNodes = loadStoryNodes();
        this.characterSettings = loadCharacterSettings();
    }

    private Map<String, StoryNode> loadStoryNodes() {
        Map<String, StoryNode> nodes = new HashMap<>();

        List<StoryNode> nodeList = Arrays.asList(
                new StoryNode("village_001", "关键节点", Collections.emptyList(),
                        Arrays.asList("拜师", "入门", "修仙", "求道", "学艺"),
                        "老者引导玩家了解修仙基础",
                        "（慈祥地看着你）年轻人有志向，修仙之路虽艰险，但若心诚，必有所成。老朽当年也是从青云门外门弟子做起，虽资质平平，但也略知一二。",
                        Arrays.asList("village_002"),
                        Collections.singletonList("获得：修仙入门知识"),
                        Collections.singletonList("无法进入青云门")),

                new StoryNode("village_002", "关键节点", Collections.singletonList("village_001"),
                        Arrays.asList("聚灵台", "灵根", "测试", "测灵"),
                        "玩家在聚灵台测试灵根",
                        "（指向镇东方向）镇东有一座废弃的聚灵台，虽已荒废，但仍能感应灵根。你若有心，可去一试。记住，修仙之路，九死一生，需有决心。",
                        Arrays.asList("village_003", "village_004"),
                        Collections.singletonList("获得：灵根测试结果"),
                        null),

                new StoryNode("village_003", "引导节点", Collections.singletonList("village_002"),
                        Arrays.asList("功法", "修炼", "法术", "技能"),
                        "传授基础功法",
                        "（欣慰地点头）既然你有此天赋，老夫便传你一套《基础吐纳诀》，助你推开这第一扇仙门。",
                        Collections.singletonList("village_006"),
                        Collections.singletonList("获得：基础吐纳诀"),
                        null),

                new StoryNode("village_004", "引导节点", Collections.singletonList("village_002"),
                        Arrays.asList("努力", "坚持", "勤奋"),
                        "鼓励玩家坚持修炼",
                        "（拍拍你的肩膀）修仙一途，资质固然重要，但更重要的是恒心。古往今来，多少资质平庸者凭毅力登临绝顶。你若肯努力，未必不能成事。",
                        Collections.singletonList("village_006"),
                        Collections.singletonList("获得：修炼建议"),
                        null),

                new StoryNode("village_005", "关键节点", Collections.singletonList("village_002"),
                        Arrays.asList("放弃", "离开", "回去"),
                        "玩家选择放弃修仙",
                        "（叹了口气）修仙之路，本就非人人可走。你若心意已决，老夫也不强求。不过，这青牛镇虽小，却也安宁，你若愿意，可在此安身立命。",
                        Collections.singletonList("village_end"),
                        Collections.emptyList(),
                        Collections.singletonList("剧情结束")),

                new StoryNode("village_006", "关键节点", Arrays.asList("village_003", "village_004"),
                        Arrays.asList("青云门", "上山", "拜见掌门"),
                        "前往青云门",
                        "（指向青云山方向）青云门就在那云雾缭绕的山巅。你既已入门，便可前往一试。记住，入门之后，便是真正的修仙之路，切莫懈怠。",
                        Collections.singletonList("mountain_001"),
                        Collections.singletonList("获得：青云门通行令"),
                        Collections.singletonList("无法进入青云门")),

                new StoryNode("mountain_001", "关键节点", Collections.singletonList("village_006"),
                        Arrays.asList("掌门", "拜见", "入门"),
                        "拜见青云门掌门",
                        "（青云子端坐于大殿之上，目光如电）年轻人，既已至此，便让我看看你的资质。",
                        Collections.singletonList("mountain_002"),
                        Collections.singletonList("获得：入门测试资格"),
                        null),

                new StoryNode("mountain_002", "引导节点", Collections.singletonList("mountain_001"),
                        Arrays.asList("师兄", "师姐", "请教"),
                        "与师兄师姐交流",
                        "（师兄微笑着）师弟，既已入门，便要勤加修炼。若有不懂之处，尽管来问我。",
                        Collections.singletonList("mountain_003"),
                        Collections.singletonList("获得：修炼指导"),
                        null),

                new StoryNode("mountain_003", "关键节点", Collections.singletonList("mountain_002"),
                        Arrays.asList("任务", "试炼", "挑战"),
                        "接受门派任务",
                        "（掌门沉吟片刻）既如此，便给你一个试炼任务。去后山秘境，取回一枚灵石。此行虽不凶险，却也能磨炼心性。",
                        Collections.singletonList("cave_001"),
                        Collections.singletonList("获得：试炼任务"),
                        null),

                new StoryNode("cave_001", "关键节点", Collections.singletonList("mountain_003"),
                        Arrays.asList("秘境", "洞穴", "探索"),
                        "探索神秘洞穴",
                        "（神秘老人从阴影中走出）年轻人，既已至此，便是有缘。这洞穴中藏有上古传承，你可愿一试？",
                        Collections.singletonList("cave_002"),
                        Collections.singletonList("获得：上古传承线索"),
                        null),

                new StoryNode("cave_002", "引导节点", Collections.singletonList("cave_001"),
                        Arrays.asList("传承", "功法", "宝物"),
                        "获得传承",
                        "（神秘老人点头）既如此，便传你这套《青云诀》。此乃上古传承，你需勤加修炼，莫要辜负了这份机缘。",
                        Collections.singletonList("end_001"),
                        Collections.singletonList("获得：青云诀"),
                        null),

                new StoryNode("end_001", "关键节点", Collections.singletonList("cave_002"),
                        Arrays.asList("完成", "结束", "成就"),
                        "剧情完成",
                        "（老者欣慰地看着你）年轻人，恭喜你踏上了真正的修仙之路。但这只是开始，前路漫漫，还需你自行探索。老夫能做的，只有这些了。",
                        Collections.emptyList(),
                        Collections.singletonList("获得：修仙之路开启"),
                        null)
        );

        for (StoryNode node : nodeList) {
            nodes.put(node.getNodeId(), node);
        }

        return nodes;
    }

    private Map<String, CharacterSetting> loadCharacterSettings() {
        Map<String, CharacterSetting> settings = new HashMap<>();

        List<CharacterSetting> settingList = Arrays.asList(
                new CharacterSetting("老者", Arrays.asList("慈祥", "见多识广", "乐于助人"),
                        "古风、温和、带点仙侠气息",
                        Arrays.asList("老朽", "年轻人", "修仙一途", "道友"),
                        Arrays.asList("现代科技", "其他门派机密")),

                new CharacterSetting("青云子", Arrays.asList("威严", "公正", "看重资质"),
                        "庄重、简洁、不怒自威",
                        Arrays.asList("本座", "弟子", "修为", "境界"),
                        Arrays.asList("门派秘辛", "过往恩怨")),

                new CharacterSetting("师兄", Arrays.asList("热情", "耐心", "乐于助人"),
                        "亲切、随和、像邻家大哥",
                        Arrays.asList("师弟", "师妹", "修炼", "功法"),
                        Collections.emptyList()),

                new CharacterSetting("神秘老人", Arrays.asList("神秘", "高深", "看透世事"),
                        "玄奥、深邃、似有深意",
                        Arrays.asList("机缘", "传承", "因果", "道"),
                        Arrays.asList("身份", "来历"))
        );

        for (CharacterSetting setting : settingList) {
            settings.put(setting.getCharacterName(), setting);
        }

        return settings;
    }

    public RAGResult processDialogue(String npcName, String playerInput, List<DialogueHistory> dialogueHistory, PlayerState playerState) {
        for (StoryNode node : storyNodes.values()) {
            boolean hasAllPreconditions = true;
            for (String precondition : node.getPreconditions()) {
                if (!playerState.getCompletedNodes().contains(precondition)) {
                    hasAllPreconditions = false;
                    break;
                }
            }

            if (!hasAllPreconditions) {
                continue;
            }

            for (String keyword : node.getTriggerKeywords()) {
                if (playerInput.contains(keyword)) {
                    RAGResult result = new RAGResult();
                    result.setResponseType("关键剧情");
                    result.setResponseContent(node.getNpcReplyTemplate());
                    result.setNodeId(node.getNodeId());
                    result.setRewards(node.getRewards());

                    return result;
                }
            }
        }

        RAGResult result = new RAGResult();
        result.setResponseType("自由对话");
        result.setCharacterSetting(characterSettings.get(npcName));

        return result;
    }

    public void updatePlayerState(PlayerState playerState, String nodeId) {
        if (!playerState.getCompletedNodes().contains(nodeId)) {
            playerState.getCompletedNodes().add(nodeId);
        }
    }

    public static class StoryNode {
        private final String nodeId;
        private final String nodeType;
        private final List<String> preconditions;
        private final List<String> triggerKeywords;
        private final String storyDescription;
        private final String npcReplyTemplate;
        private final List<String> nextStoryNodes;
        private final List<String> rewards;
        private final List<String> failureConsequences;

        public StoryNode(String nodeId, String nodeType, List<String> preconditions, List<String> triggerKeywords, 
                        String storyDescription, String npcReplyTemplate, List<String> nextStoryNodes, 
                        List<String> rewards, List<String> failureConsequences) {
            this.nodeId = nodeId;
            this.nodeType = nodeType;
            this.preconditions = preconditions;
            this.triggerKeywords = triggerKeywords;
            this.storyDescription = storyDescription;
            this.npcReplyTemplate = npcReplyTemplate;
            this.nextStoryNodes = nextStoryNodes;
            this.rewards = rewards;
            this.failureConsequences = failureConsequences;
        }

        public String getNodeId() { return nodeId; }
        public String getNodeType() { return nodeType; }
        public List<String> getPreconditions() { return preconditions; }
        public List<String> getTriggerKeywords() { return triggerKeywords; }
        public String getStoryDescription() { return storyDescription; }
        public String getNpcReplyTemplate() { return npcReplyTemplate; }
        public List<String> getNextStoryNodes() { return nextStoryNodes; }
        public List<String> getRewards() { return rewards; }
        public List<String> getFailureConsequences() { return failureConsequences; }
    }

    public static class CharacterSetting {
        private final String characterName;
        private final List<String> personality;
        private final String speakingStyle;
        private final List<String> commonVocabulary;
        private final List<String> tabooTopics;

        public CharacterSetting(String characterName, List<String> personality, String speakingStyle, 
                              List<String> commonVocabulary, List<String> tabooTopics) {
            this.characterName = characterName;
            this.personality = personality;
            this.speakingStyle = speakingStyle;
            this.commonVocabulary = commonVocabulary;
            this.tabooTopics = tabooTopics;
        }

        public String getCharacterName() { return characterName; }
        public List<String> getPersonality() { return personality; }
        public String getSpeakingStyle() { return speakingStyle; }
        public List<String> getCommonVocabulary() { return commonVocabulary; }
        public List<String> getTabooTopics() { return tabooTopics; }
    }

    public static class RAGResult {
        private String responseType;
        private String responseContent;
        private String nodeId;
        private List<String> rewards;
        private CharacterSetting characterSetting;

        public String getResponseType() { return responseType; }
        public void setResponseType(String responseType) { this.responseType = responseType; }
        public String getResponseContent() { return responseContent; }
        public void setResponseContent(String responseContent) { this.responseContent = responseContent; }
        public String getNodeId() { return nodeId; }
        public void setNodeId(String nodeId) { this.nodeId = nodeId; }
        public List<String> getRewards() { return rewards; }
        public void setRewards(List<String> rewards) { this.rewards = rewards; }
        public CharacterSetting getCharacterSetting() { return characterSetting; }
        public void setCharacterSetting(CharacterSetting characterSetting) { this.characterSetting = characterSetting; }
    }

    public static class DialogueHistory {
        private String speaker;
        private String text;

        public String getSpeaker() { return speaker; }
        public void setSpeaker(String speaker) { this.speaker = speaker; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public static class PlayerState {
        private List<String> completedNodes;
        private List<String> inventory;
        private String currentScene;

        public PlayerState() {
            this.completedNodes = new ArrayList<>();
            this.inventory = new ArrayList<>();
            this.currentScene = "village";
        }

        public List<String> getCompletedNodes() { return completedNodes; }
        public void setCompletedNodes(List<String> completedNodes) { this.completedNodes = completedNodes; }
        public List<String> getInventory() { return inventory; }
        public void setInventory(List<String> inventory) { this.inventory = inventory; }
        public String getCurrentScene() { return currentScene; }
        public void setCurrentScene(String currentScene) { this.currentScene = currentScene; }
    }
}