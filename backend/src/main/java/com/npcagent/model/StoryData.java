package com.npcagent.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoryData {
    private Scenes scenes;
    private Npcs npcs;
    private List<StoryArc> storyArcs;

    @Data
    public static class Scenes {
        private Scene village;
        private Scene mountain;
        private Scene cave;
    }

    @Data
    public static class Scene {
        private String name;
        private String description;
        private List<String> npcs;
    }

    @Data
    public static class Npcs {
        private Npc 老者;
        private Npc 村民甲;
        private Npc 药铺老板;
        private Npc 掌门;
        private Npc 师兄;
        private Npc 师姐;
        private Npc 神秘老人;
    }

    @Data
    public static class Npc {
        private String name;
        private String personality;
        private String backstory;
        private List<Dialogue> dialogues;
    }

    @Data
    public static class Dialogue {
        private int id;
        private String text;
        private List<String> options;
    }

    @Data
    public static class StoryArc {
        private int id;
        private String name;
        private String description;
        private List<String> scenes;
        private List<String> tasks;
    }
}