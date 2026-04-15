package com.npcagent.controller;

import com.npcagent.common.Result;
import com.npcagent.service.StoryVectorInitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 剧情初始化控制器
 *
 * 用于触发剧情相关的初始化操作
 */
@RestController
@RequestMapping("/api/story/init")
public class StoryInitController {

    private static final Logger logger = LoggerFactory.getLogger(StoryInitController.class);

    private final StoryVectorInitService storyVectorInitService;

    public StoryInitController(StoryVectorInitService storyVectorInitService) {
        this.storyVectorInitService = storyVectorInitService;
    }

    /**
     * 初始化剧情向量库
     *
     * @return 初始化结果
     */
    @PostMapping("/vectors")
    public Result<String> initStoryVectors() {
        logger.info("Receive init story vectors request");
        storyVectorInitService.initStoryVectors();
        return Result.success("剧情向量库初始化成功");
    }

    /**
     * 重新初始化剧情向量库
     *
     * @return 初始化结果
     */
    @PostMapping("/vectors/reinit")
    public Result<String> reinitStoryVectors() {
        logger.info("Receive reinit story vectors request");
        storyVectorInitService.reinitStoryVectors();
        return Result.success("剧情向量库重新初始化成功");
    }
}
