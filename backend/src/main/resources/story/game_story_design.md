# 修仙游戏剧情设计

## 游戏概述
- **游戏类型**：文字冒险 + 修仙养成
- **游戏时长**：约60分钟
- **核心玩法**：对话选择 + 场景探索 + 简单战斗 + 寻宝
- **游戏背景**：一个平凡少年踏上修仙之路的故事

## 剧情流程设计

### 第一章：平凡少年（10分钟）
**场景**：青牛镇
**NPC**：老者（村长）

1. **剧情节点1-1**：初入青牛镇
   - 玩家作为一个平凡少年，来到青牛镇寻求修仙机缘
   - 老者（村长）告知镇上即将举行灵根测试
   - 对话选项：询问灵根测试详情、了解修仙世界、询问镇上情况

2. **剧情节点1-2**：灵根测试
   - 老者带领玩家参加灵根测试
   - 玩家被测出具有特殊灵根（五行灵根）
   - 对话选项：表达兴奋、询问灵根等级、请教修仙基础

3. **剧情节点1-3**：修仙启蒙
   - 老者作为引路人，教导玩家修仙基础知识
   - 赠送玩家第一本修仙功法《基础吐纳法》
   - 对话选项：感谢老者、询问修炼方法、请求更多指导

### 第二章：踏上仙途（15分钟）
**场景**：青云山
**NPC**：掌门（青云门）

4. **剧情节点2-1**：前往青云山
   - 玩家根据老者指引，前往青云山拜师
   - 途中遇到小妖兽，触发第一次战斗
   - 战斗胜利后获得经验和基础装备

5. **剧情节点2-2**：拜见掌门
   - 掌门考验玩家的资质和决心
   - 玩家展示灵根资质，获得入门资格
   - 对话选项：表达决心、询问门派规矩、请求修行指导

6. **剧情节点2-3**：入门任务
   - 掌门指派第一个任务：采集灵药
   - 玩家前往指定地点采集，过程中触发探索事件
   - 完成任务后获得门派贡献和奖励

### 第三章：修行之路（15分钟）
**场景**：青云山弟子院
**NPC**：师兄、师姐

7. **剧情节点3-1**：日常修行
   - 玩家开始日常修行，提升境界
   - 与师兄师姐交流，学习修行心得
   - 对话选项：请教修行技巧、了解门派历史、询问进阶方法

8. **剧情节点3-2**：宗门比试
   - 门派举行入门弟子比试
   - 玩家参与比试，展示实力
   - 比试胜利后获得宗门认可和奖励

9. **剧情节点3-3**：神秘洞穴
   - 师兄告知后山有神秘洞穴
   - 玩家前往探索，发现古代修士遗迹
   - 获得传承和宝物，提升实力

### 第四章：危机降临（15分钟）
**场景**：青云山
**NPC**：掌门、神秘老者

10. **剧情节点4-1**：魔修入侵
    - 魔修势力入侵青云山
    - 玩家参与保卫宗门的战斗
    - 战斗中展现勇气和实力

11. **剧情节点4-2**：掌门嘱托
    - 掌门受伤，将重要任务托付给玩家
    - 玩家需要前往寻找上古神器
    - 对话选项：接受任务、询问详情、表达担忧

12. **剧情节点4-3**：神秘指引
    - 神秘老者出现，给予玩家重要指引
    - 告知神器所在地和获取方法
    - 对话选项：询问老者身份、请教应对之策、表达决心

### 第五章：最终挑战（5分钟）
**场景**：上古遗迹
**NPC**：魔修首领

13. **剧情节点5-1**：遗迹探险
    - 玩家进入上古遗迹，解开机关
    - 克服重重困难，接近神器

14. **剧情节点5-2**：最终对决
    - 魔修首领出现，抢夺神器
    - 玩家与魔修首领展开最终战斗
    - 战斗胜利后获得神器

15. **剧情节点5-3**：回归宗门
    - 玩家带着神器返回宗门
    - 掌门康复，表彰玩家的功绩
    - 玩家成为宗门核心弟子，开启新的修仙之路

## 剧情存储方案

### 1. 数据库存储
**表结构设计**：

#### `npc_character` 表
| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| `code` | VARCHAR(50) | NPC代码 |
| `name` | VARCHAR(100) | NPC名称 |
| `personality` | TEXT | 性格特点 |
| `speaking_style` | TEXT | 说话风格 |
| `scene_code` | VARCHAR(50) | 所属场景 |
| `description` | TEXT | 角色描述 |
| `initial_node_id` | VARCHAR(50) | 初始剧情节点ID |
| `enabled` | BOOLEAN | 是否启用 |

#### `story_node` 表
| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| `node_id` | VARCHAR(50) | 节点ID |
| `title` | VARCHAR(200) | 节点标题 |
| `description` | TEXT | 节点描述 |
| `content` | TEXT | 剧情内容 |
| `npc_code` | VARCHAR(50) | 所属NPC代码 |
| `prerequisite_node_id` | VARCHAR(50) | 前置节点ID |
| `next_node_id` | VARCHAR(50) | 后续节点ID |
| `is_initial` | BOOLEAN | 是否为初始节点 |
| `enabled` | BOOLEAN | 是否启用 |

#### `dialogue_option` 表
| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| `option_id` | VARCHAR(50) | 选项ID |
| `text` | TEXT | 选项文本 |
| `npc_code` | VARCHAR(50) | 所属NPC代码 |
| `node_id` | VARCHAR(50) | 所属剧情节点ID |
| `next_node_id` | VARCHAR(50) | 后续节点ID |
| `response_template` | TEXT | 回复模板 |
| `order_index` | INTEGER | 选项顺序 |
| `enabled` | BOOLEAN | 是否启用 |

### 2. 向量库存储
**用途**：存储剧情节点的向量表示，用于语义检索

**存储内容**：
- 剧情节点ID
- 剧情内容向量
- 相关关键词
- 场景信息

**使用方式**：
- 玩家自由输入时，通过向量检索找到最相关的剧情节点
- 增强RAG系统的语义理解能力

### 3. Redis存储
**用途**：缓存热点数据和玩家状态

**存储内容**：
- 玩家当前剧情进度
- 最近访问的剧情节点
- 对话历史
- 临时游戏状态

**使用方式**：
- 提高系统响应速度
- 减少数据库访问压力
- 保持玩家会话状态

## 数据初始化脚本

### 1. 数据库初始化脚本
```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS npc_agent;

-- 使用数据库
USE npc_agent;

-- 创建NPC表
CREATE TABLE IF NOT EXISTS npc_character (
    code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    personality TEXT,
    speaking_style TEXT,
    scene_code VARCHAR(50),
    description TEXT,
    initial_node_id VARCHAR(50),
    enabled BOOLEAN DEFAULT TRUE
);

-- 创建剧情节点表
CREATE TABLE IF NOT EXISTS story_node (
    node_id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    content TEXT NOT NULL,
    npc_code VARCHAR(50),
    prerequisite_node_id VARCHAR(50),
    next_node_id VARCHAR(50),
    is_initial BOOLEAN DEFAULT FALSE,
    enabled BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (npc_code) REFERENCES npc_character(code)
);

-- 创建对话选项表
CREATE TABLE IF NOT EXISTS dialogue_option (
    option_id VARCHAR(50) PRIMARY KEY,
    text TEXT NOT NULL,
    npc_code VARCHAR(50),
    node_id VARCHAR(50),
    next_node_id VARCHAR(50),
    response_template TEXT,
    order_index INTEGER DEFAULT 0,
    enabled BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (npc_code) REFERENCES npc_character(code),
    FOREIGN KEY (node_id) REFERENCES story_node(node_id)
);

-- 插入NPC数据
INSERT INTO npc_character (code, name, personality, speaking_style, scene_code, description, initial_node_id) VALUES
('elder', '村长', '慈祥、智慧、乐于助人', '温和稳重，喜欢用比喻教导年轻人', 'village', '青牛镇的村长，曾经是一位修仙者，退隐后回到家乡', 'node_1_1'),
('sect_master', '掌门', '威严、公正、深谋远虑', '语气严肃，条理清晰，注重规矩', 'mountain', '青云门掌门，修为高深，德高望重', 'node_2_2'),
('senior_brother', '师兄', '热情、直率、经验丰富', '说话直接，喜欢开玩笑，乐于分享', 'mountain', '青云门的资深弟子，对新人很照顾', 'node_3_1'),
('mysterious_elder', '神秘老者', '神秘、深不可测、古道热肠', '语气高深，喜欢说禅语，点到为止', 'cave', '隐居在神秘洞穴的高人，对主角有特殊缘分', 'node_4_3'),
('demon_leader', '魔修首领', '邪恶、残忍、野心勃勃', '语气嚣张，充满恶意，喜欢嘲讽', 'ruins', '魔修势力的首领，试图抢夺上古神器', 'node_5_2');

-- 插入剧情节点数据
INSERT INTO story_node (node_id, title, description, content, npc_code, prerequisite_node_id, next_node_id, is_initial) VALUES
('node_1_1', '初入青牛镇', '玩家第一次来到青牛镇，遇到村长', '你是一个平凡的少年，听说青牛镇即将举行灵根测试，于是不远万里来到这里。刚进入镇子，你就看到一位慈眉善目的老者站在镇口。', 'elder', NULL, 'node_1_2', TRUE),
('node_1_2', '灵根测试', '村长带领玩家进行灵根测试', '老者见你风尘仆仆，便主动上前询问。当他得知你是来参加灵根测试的，眼中闪过一丝惊讶。\n\n"年轻人，灵根测试可不是闹着玩的，只有真正有资质的人才能踏上修仙之路。"\n\n在他的带领下，你来到了测试场地。经过测试，你被测出具有五行灵根，这是非常罕见的资质。', 'elder', 'node_1_1', 'node_1_3', FALSE),
('node_1_3', '修仙启蒙', '村长教导玩家修仙基础知识', '老者对你的资质非常满意，决定收你为徒，教导你修仙的基础知识。\n\n"五行灵根虽然罕见，但也意味着你需要付出更多的努力。我这里有一本《基础吐纳法》，你先好好修炼。"\n\n他还告诉你，青云山的掌门即将来镇上挑选弟子，这是你进入大门派的好机会。', 'elder', 'node_1_2', 'node_2_1', FALSE),
('node_2_1', '前往青云山', '玩家前往青云山拜师', '在村长的鼓励下，你决定前往青云山拜师。途中，你遇到了一只小妖兽的袭击。虽然是第一次战斗，但凭借着村长教你的基础功法，你勉强战胜了它。\n\n战斗结束后，你感到自己的修为有所提升，同时也更加坚定了修仙的决心。', 'elder', 'node_1_3', 'node_2_2', FALSE),
('node_2_2', '拜见掌门', '玩家拜见青云门掌门', '经过长途跋涉，你终于来到了青云山。掌门亲自接见了你，对你的五行灵根非常感兴趣。\n\n"五行灵根，万中无一。但修仙之路充满艰辛，你可做好了准备？"\n\n在通过掌门的考验后，你正式成为了青云门的入门弟子。', 'sect_master', 'node_2_1', 'node_2_3', FALSE),
('node_2_3', '入门任务', '掌门指派第一个任务', '作为入门弟子，你接到了第一个任务：前往后山采集灵药。\n\n在采集过程中，你遇到了各种奇妙的植物和小动物，也学到了许多关于灵药的知识。完成任务后，你获得了门派贡献和一些基础修炼资源。', 'sect_master', 'node_2_2', 'node_3_1', FALSE),
('node_3_1', '日常修行', '玩家开始日常修行', '在青云山的日子里，你开始了规律的修行生活。师兄师姐们对你很照顾，经常分享修行心得。\n\n"修行如逆水行舟，不进则退。每天的基础修炼虽然枯燥，但却是提升实力的关键。"\n\n在他们的指导下，你的修为稳步提升。', 'senior_brother', 'node_2_3', 'node_3_2', FALSE),
('node_3_2', '宗门比试', '门派举行入门弟子比试', '门派举行了入门弟子比试，你报名参加了。在比试中，你凭借着五行灵根的优势和平时的努力，取得了不错的成绩。\n\n掌门对你的表现非常满意，奖励了你一本中级功法和一些修炼资源。', 'senior_brother', 'node_3_1', 'node_3_3', FALSE),
('node_3_3', '神秘洞穴', '玩家探索神秘洞穴', '师兄告诉你后山有一个神秘洞穴，据说里面有古代修士的遗迹。好奇之下，你决定前往探索。\n\n在洞穴中，你遇到了一些机关和小怪物，但都被你一一克服。最终，你找到了古代修士的传承，获得了一件宝物和一些高级功法。', 'senior_brother', 'node_3_2', 'node_4_1', FALSE),
('node_4_1', '魔修入侵', '魔修势力入侵青云山', '就在你沉浸在修行中的时候，魔修势力突然入侵青云山。作为门派弟子，你义不容辞地加入了保卫宗门的战斗。\n\n在战斗中，你展现出了惊人的实力，帮助门派击退了魔修的进攻。但掌门在战斗中受了重伤。', 'sect_master', 'node_3_3', 'node_4_2', FALSE),
('node_4_2', '掌门嘱托', '掌门将重要任务托付给玩家', '掌门伤势严重，他将一个重要任务托付给了你：前往上古遗迹寻找一件神器，只有这件神器才能彻底击败魔修。\n\n"这件任务危险重重，但我相信你有能力完成。记住，神器不仅是力量的象征，更是责任的象征。"', 'sect_master', 'node_4_1', 'node_4_3', FALSE),
('node_4_3', '神秘指引', '神秘老者给予玩家指引', '在前往上古遗迹的路上，你遇到了一位神秘老者。他似乎早就知道你的到来，主动给予你指引。\n\n"年轻人，你的命运与这件神器紧密相连。记住，真正的力量来自于内心的坚定，而不是外物。"\n\n他还给了你一张地图，标记了神器的具体位置。', 'mysterious_elder', 'node_4_2', 'node_5_1', FALSE),
('node_5_1', '遗迹探险', '玩家进入上古遗迹', '根据神秘老者的指引，你来到了上古遗迹。遗迹中机关重重，你需要解开各种谜题才能前进。\n\n在探索过程中，你不仅提升了自己的智慧，也更加了解了古代修士的文明。最终，你来到了神器所在的核心区域。', 'mysterious_elder', 'node_4_3', 'node_5_2', FALSE),
('node_5_2', '最终对决', '玩家与魔修首领战斗', '就在你即将拿到神器的时候，魔修首领突然出现。他早就跟踪你来到了这里，想要抢夺神器。\n\n"哈哈哈，小子，你以为你能阻止我吗？神器是属于我的！"\n\n一场激烈的战斗展开了。凭借着你这些天的修行和古代修士的传承，你最终战胜了魔修首领。', 'demon_leader', 'node_5_1', 'node_5_3', FALSE),
('node_5_3', '回归宗门', '玩家带着神器返回宗门', '你带着神器返回了青云山。掌门看到你平安归来，非常欣慰。\n\n"你不仅完成了任务，更重要的是你展现了一个修仙者应有的品质。从今天起，你就是我青云门的核心弟子。"\n\n在掌门的主持下，你正式成为了青云门的核心弟子，开启了新的修仙之路。', 'sect_master', 'node_5_2', NULL, FALSE);

-- 插入对话选项数据
INSERT INTO dialogue_option (option_id, text, npc_code, node_id, next_node_id, response_template, order_index) VALUES
-- 节点1-1的选项
('option_1_1_1', '请问灵根测试什么时候开始？', 'elder', 'node_1_1', 'node_1_2', '灵根测试将在三天后举行，到时候镇上会热闹起来。', 1),
('option_1_1_2', '修仙到底是什么样的？', 'elder', 'node_1_1', 'node_1_2', '修仙是一条漫长而艰辛的道路，需要天赋、努力和机缘。但一旦踏上这条路，你将获得常人无法想象的力量。', 2),
('option_1_1_3', '镇上最近有什么新鲜事吗？', 'elder', 'node_1_1', 'node_1_2', '除了即将举行的灵根测试，最近镇上来了一位青云门的修士，说是要挑选有资质的弟子。', 3),

-- 节点1-2的选项
('option_1_2_1', '我的灵根怎么样？', 'elder', 'node_1_2', 'node_1_3', '你的灵根非常特殊，是五行灵根。这种灵根万中无一，潜力无穷。', 1),
('option_1_2_2', '灵根有等级之分吗？', 'elder', 'node_1_2', 'node_1_3', '当然有。灵根分为金、木、水、火、土五行，单一灵根最为纯粹，而五行灵根则最为罕见。', 2),
('option_1_2_3', '我该如何开始修炼？', 'elder', 'node_1_2', 'node_1_3', '我这里有一本《基础吐纳法》，你先从基础开始，循序渐进。', 3),

-- 节点1-3的选项
('option_1_3_1', '谢谢前辈的指导！', 'elder', 'node_1_3', 'node_2_1', '不必客气，能够遇到你这样有资质的年轻人，是我的荣幸。', 1),
('option_1_3_2', '青云山的掌门什么时候来？', 'elder', 'node_1_3', 'node_2_1', '掌门会在灵根测试后到来，到时候你一定要好好表现。', 2),
('option_1_3_3', '我需要做什么准备吗？', 'elder', 'node_1_3', 'node_2_1', '好好休息，保持最佳状态。修仙之路，心态比实力更重要。', 3),

-- 节点2-2的选项
('option_2_2_1', '我一定会努力修炼的！', 'sect_master', 'node_2_2', 'node_2_3', '很好，有这份决心，你已经成功了一半。', 1),
('option_2_2_2', '门派有什么规矩吗？', 'sect_master', 'node_2_2', 'node_2_3', '我青云门以正义为本，要求弟子尊师重道，团结互助，不得为非作歹。', 2),
('option_2_2_3', '我什么时候能开始正式修炼？', 'sect_master', 'node_2_2', 'node_2_3', '从明天开始，你将跟随师兄师姐们一起修行。今天先好好休息。', 3),

-- 节点2-3的选项
('option_2_3_1', '我一定完成任务！', 'sect_master', 'node_2_3', 'node_3_1', '好，我相信你。后山的灵药虽然常见，但也要小心不要迷失方向。', 1),
('option_2_3_2', '需要采集多少灵药？', 'sect_master', 'node_2_3', 'node_3_1', '采集十株百年灵芝即可。记住，只取所需，不可过度采摘。', 2),
('option_2_3_3', '后山危险吗？', 'sect_master', 'node_2_3', 'node_3_1', '后山有一些小妖兽，但对你来说应该不成问题。如果遇到危险，及时返回。', 3),

-- 节点3-1的选项
('option_3_1_1', '师兄，有什么修行技巧吗？', 'senior_brother', 'node_3_1', 'node_3_2', '修行没有捷径，只有持之以恒。每天的吐纳练习是基础，千万不能偷懒。', 1),
('option_3_1_2', '门派有多少年历史了？', 'senior_brother', 'node_3_1', 'node_3_2', '青云门已经有千年历史了，是这一带最古老的修仙门派之一。', 2),
('option_3_1_3', '我什么时候能进阶？', 'senior_brother', 'node_3_1', 'node_3_2', '以你的资质，只要努力，三个月内应该可以突破到练气一层。', 3),

-- 节点3-2的选项
('option_3_2_1', '谢谢掌门的奖励！', 'senior_brother', 'node_3_2', 'node_3_3', '不用谢，这是你应得的。继续努力，你会成为门派的骄傲。', 1),
('option_3_2_2', '中级功法该如何修炼？', 'senior_brother', 'node_3_2', 'node_3_3', '中级功法需要更强的灵力基础，你可以先从基础招式开始，逐渐掌握。', 2),
('option_3_2_3', '师兄，你有什么故事吗？', 'senior_brother', 'node_3_2', 'node_3_3', '哈哈，我的故事可多了。等你有时间，我慢慢讲给你听。', 3),

-- 节点3-3的选项
('option_3_3_1', '这个洞穴真神秘！', 'senior_brother', 'node_3_3', 'node_4_1', '对吧？我第一次来的时候也被吓到了。不过里面的宝藏确实值得冒险。', 1),
('option_3_3_2', '古代修士真厉害！', 'senior_brother', 'node_3_3', 'node_4_1', '那是自然，古代修士的智慧和实力远在我们之上。我们要学习的东西还很多。', 2),
('option_3_3_3', '我感觉自己变强了！', 'senior_brother', 'node_3_3', 'node_4_1', '这是好事，但不要骄傲。修仙之路，一山还有一山高。', 3),

-- 节点4-1的选项
('option_4_1_1', '掌门，您没事吧？', 'sect_master', 'node_4_1', 'node_4_2', '我没事，只是受了点伤。魔修的实力比我们想象的要强大。', 1),
('option_4_1_2', '我们能战胜魔修吗？', 'sect_master', 'node_4_1', 'node_4_2', '只要我们团结一心，一定能战胜魔修。但我们需要一件神器的帮助。', 2),
('option_4_1_3', '我该怎么做？', 'sect_master', 'node_4_1', 'node_4_2', '我需要你前往上古遗迹寻找一件神器，只有它才能彻底击败魔修。', 3),

-- 节点4-2的选项
('option_4_2_1', '我接受这个任务！', 'sect_master', 'node_4_2', 'node_4_3', '很好，我相信你。神器就在上古遗迹的核心区域，你一定要小心。', 1),
('option_4_2_2', '神器有什么作用？', 'sect_master', 'node_4_2', 'node_4_3', '这件神器是上古修士留下的，具有净化魔气的力量。有了它，我们就能彻底清除魔修的威胁。', 2),
('option_4_2_3', '我需要带什么东西？', 'sect_master', 'node_4_2', 'node_4_3', '带上你的武器和丹药，遗迹中危险重重。记住，安全第一。', 3),

-- 节点4-3的选项
('option_4_3_1', '前辈，您是谁？', 'mysterious_elder', 'node_4_3', 'node_5_1', '我只是一个普通的修士，偶然路过这里。我们有缘，所以我才会帮你。', 1),
('option_4_3_2', '遗迹中有什么危险？', 'mysterious_elder', 'node_4_3', 'node_5_1', '遗迹中有各种机关和守护者，你需要用智慧和勇气去克服它们。', 2),
('option_4_3_3', '我一定不会让您失望！', 'mysterious_elder', 'node_4_3', 'node_5_1', '很好，年轻人。记住，真正的力量来自于内心，而不是外物。', 3),

-- 节点5-2的选项
('option_5_2_1', '休想拿到神器！', 'demon_leader', 'node_5_2', 'node_5_3', '哈哈哈，就凭你？看我怎么收拾你！', 1),
('option_5_2_2', '你为什么要抢夺神器？', 'demon_leader', 'node_5_2', 'node_5_3', '神器的力量可以让我统治整个修仙界，你这种小角色怎么会明白？', 2),
('option_5_2_3', '魔修只会带来破坏！', 'demon_leader', 'node_5_2', 'node_5_3', '破坏？不，这是新生！等我拿到神器，整个世界都会在我的脚下颤抖！', 3),

-- 节点5-3的选项
('option_5_3_1', '掌门，我完成任务了！', 'sect_master', 'node_5_3', NULL, '太好了！有了这件神器，我们就能彻底击败魔修了。你是我青云门的英雄。', 1),
('option_5_3_2', '神器的力量真强大！', 'sect_master', 'node_5_3', NULL, '是的，但更强大的是你的勇气和决心。神器只是工具，真正的力量在于使用者。', 2),
('option_5_3_3', '我会继续努力的！', 'sect_master', 'node_5_3', NULL, '我相信你。从今天起，你就是我青云门的核心弟子。未来的修仙之路，我们一起前行。', 3);
```

### 2. 向量库初始化脚本
```java
package com.npcagent.service;

import com.npcagent.model.StoryNode;
import com.npcagent.service.VectorStorageService;
import com.npcagent.service.StoryNodeService;
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
}
```

### 3. 执行脚本说明

#### 步骤1：创建数据库
1. 启动MySQL服务
2. 运行数据库初始化脚本创建数据库和表结构
3. 插入初始数据

#### 步骤2：启动Milvus向量数据库
1. 下载并安装Milvus（参考官方文档）
2. 启动Milvus服务
3. 创建默认集合

#### 步骤3：初始化向量库
1. 启动Spring Boot应用
2. 调用`StoryVectorInitService.initStoryVectors()`方法
3. 等待初始化完成

#### 步骤4：启动前端
1. 进入frontend目录
2. 运行 `npm install` 安装依赖
3. 运行 `npm run dev` 启动开发服务器
4. 访问 http://localhost:5174/ 开始游戏

## 游戏特色

1. **沉浸式剧情**：丰富的对话选项和分支剧情
2. **多样化玩法**：对话、探索、战斗、寻宝
3. **成长系统**：玩家通过修行提升境界
4. **智能对话**：基于RAG系统的自由输入理解
5. **视觉体验**：精美的场景切换和UI设计

## 时间控制

- **对话部分**：约25分钟
- **探索部分**：约15分钟
- **战斗部分**：约10分钟
- **寻宝部分**：约5分钟
- **其他部分**：约5分钟

总计：约60分钟

## 后续扩展

1. **更多剧情分支**：增加玩家选择对剧情的影响
2. **更多NPC**：增加更多有特色的NPC角色
3. **更多场景**：增加更多可探索的场景
4. **更多玩法**：增加炼丹、炼器等修仙特色玩法
5. **多人互动**：增加玩家之间的互动功能
