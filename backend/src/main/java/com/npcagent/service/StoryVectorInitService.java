package com.npcagent.service;

import com.npcagent.model.StoryNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 剧情向量初始化服务
 *
 * 用于将剧情节点数据初始化到向量库中
 */
@Service
public class StoryVectorInitService {

    private static final Logger logger = LoggerFactory.getLogger(StoryVectorInitService.class);

    private final StoryNodeService storyNodeService;
    private final VectorStorageService vectorStorageService;

    @Autowired
    public StoryVectorInitService(StoryNodeService storyNodeService, VectorStorageService vectorStorageService) {
        this.storyNodeService = storyNodeService;
        this.vectorStorageService = vectorStorageService;
    }

    /**
     * 初始化剧情向量库
     *
     * 从数据库加载所有剧情节点，生成向量并存储到Milvus
     */
    public void initStoryVectors() {
        try {
            logger.info("开始初始化剧情向量库...");
            
            // 从数据库加载所有剧情节点
            List<StoryNode> storyNodes = storyNodeService.getAllStoryNodes();
            logger.info("加载到 {} 个剧情节点", storyNodes.size());
            
            // 遍历剧情节点，生成向量并存储
            for (StoryNode node : storyNodes) {
                if (node.getContent() != null && !node.getContent().isEmpty()) {
                    // 生成向量并存储
                    vectorStorageService.storeVector(
                        node.getNodeId(),
                        node.getContent(),
                        node.getTitle(),
                        node.getNpcCode()
                    );
                    logger.debug("已存储剧情节点: {}", node.getTitle());
                }
            }
            
            logger.info("剧情向量库初始化完成！");
        } catch (Exception e) {
            logger.error("初始化剧情向量库失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 重新初始化剧情向量库
     *
     * 先清空现有向量，然后重新初始化
     */
    public void reinitStoryVectors() {
        try {
            logger.info("开始重新初始化剧情向量库...");
            
            // 这里可以添加清空向量库的逻辑
            // vectorStorageService.clearVectors();
            
            // 重新初始化
            initStoryVectors();
            
            logger.info("剧情向量库重新初始化完成！");
        } catch (Exception e) {
            logger.error("重新初始化剧情向量库失败: {}", e.getMessage(), e);
        }
    }
}
