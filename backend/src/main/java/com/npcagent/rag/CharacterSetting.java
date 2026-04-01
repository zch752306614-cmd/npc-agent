package com.npcagent.rag;

import java.util.List;

/**
 * 角色设定类
 *
 * 用于AI角色扮演（Role-Playing）的设定信息：
 * - characterCode: 角色代码（英文标识）
 * - characterName: 角色名称（中文显示名）
 * - personality: 性格特点，用于构建"你是一个...的人"的Prompt
 * - speakingStyle: 说话风格，用于构建"你的说话风格是..."的Prompt
 * - commonVocabulary: 常用词汇，用于构建"你经常使用以下词汇..."的Prompt
 * - tabooTopics: 禁忌话题，用于构建"你从不谈论以下话题..."的Prompt
 *
 * AI提示工程应用：
 * 这些字段会被DialogueManager.generateFreeResponse()方法组合成完整的Prompt，
 * 传递给Ollama API，确保AI生成的回复符合角色设定。
 */
public class CharacterSetting {
    private final String characterCode;
    private final String characterName;
    private final List<String> personality;
    private final String speakingStyle;
    private final List<String> commonVocabulary;
    private final List<String> tabooTopics;

    public CharacterSetting(String characterCode, String characterName, List<String> personality, String speakingStyle,
                            List<String> commonVocabulary, List<String> tabooTopics) {
        this.characterCode = characterCode;
        this.characterName = characterName;
        this.personality = personality;
        this.speakingStyle = speakingStyle;
        this.commonVocabulary = commonVocabulary;
        this.tabooTopics = tabooTopics;
    }

    public String getCharacterCode() {
        return characterCode;
    }

    public String getCharacterName() {
        return characterName;
    }

    public List<String> getPersonality() {
        return personality;
    }

    public String getSpeakingStyle() {
        return speakingStyle;
    }

    public List<String> getCommonVocabulary() {
        return commonVocabulary;
    }

    public List<String> getTabooTopics() {
        return tabooTopics;
    }
}
