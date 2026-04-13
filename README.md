# 修仙题材 RPG 游戏（Spring Boot + AI 智能体）

本项目是一个文字修仙 RPG，采用「固定剧情选项 + AI 自由对话」双模式交互，目标是提供沉浸式修仙成长体验，并形成完整玩法闭环：对话 -> 探索 -> 战斗 -> 寻宝 -> 成长 -> 背包养成。

## 1. 项目目标

- 构建可扩展的修仙 RPG 后端服务
- 实现剧情分支与 AI 自由输入并行的 NPC 对话系统
- 接入向量检索，实现语义匹配与剧情智能推进
- 支持探索、战斗、寻宝、背包、任务、成就等核心玩法
- 提供规范接口，便于前端（Web/小程序/客户端）接入

## 2. 当前技术栈（按仓库现状）

### 后端

- Java 17
- Spring Boot 3.2.4
- MyBatis-Plus 3.5.5
- MySQL（业务数据）
- Redis（缓存）
- Milvus 2.4.10（向量检索）
- Ollama（本地大模型服务，聊天 + 向量）
- Maven

### 前端

- Vue 3
- Vite 8

## 3. 目录结构

```text
npc-agent/
├── backend/                  # Spring Boot 后端
├── frontend/                 # Vue 前端
├── doc/
│   └── db/                   # 数据库脚本与说明
│       ├── README.md
│       ├── V1__init.sql
│       ├── V2__add_skills_and_combat.sql
│       └── V3__add_quests_and_achievements.sql
└── README.md                 # 项目总说明（当前文件）
```

## 4. 核心业务设计

### 4.1 双模式对话机制

- 固定选项模式：按预设剧情分支推进
- 自由输入模式：玩家输入任意文本，系统做语义匹配

### 4.2 AI 匹配闭环

1. 玩家输入文本
2. 向量库检索相似语义/意图
3. 匹配成功：解锁 NPC 回复并推进剧情节点
4. 匹配失败：停留当前节点，给出引导回复，继续输入

### 4.3 玩法系统

- 探索：场景切换、交互、随机事件
- 战斗：修为/功法/灵力/法宝等属性参与结算
- 寻宝：按概率产出装备、丹药、灵石、材料
- 背包：查看、使用、分类、堆叠、丢弃、存放
- 成长：境界路线（凡人 -> 炼气 -> 筑基 -> 金丹...）

## 5. 本地运行（建议）

## 5.1 准备依赖服务

- MySQL（默认：`localhost:3306`，库名：`npc_agent`）
- Redis（默认：`localhost:6379`）
- Milvus（默认：`localhost:19530`）
- Ollama（默认：`http://localhost:11434`）

## 5.2 初始化数据库

请参考 `doc/db/README.md`，按顺序执行：

1. `V1__init.sql`
2. `V2__add_skills_and_combat.sql`
3. `V3__add_quests_and_achievements.sql`

## 5.3 启动后端

在 `backend` 目录执行：

```bash
mvn spring-boot:run
```

默认端口：`59999`

## 5.4 启动前端

在 `frontend` 目录执行：

```bash
npm install
npm run dev
```

## 6. 数据与配置说明

- 后端配置文件：`backend/src/main/resources/application.yml`
- 数据库脚本与说明：`doc/db/README.md`
- 向量检索配置：`milvus.*`
- 本地模型配置：`ollama.*`

## 7. 后续扩展建议

- 增加 AI 对话细化数据表（人设、意图规则、embedding 引用）
- 增加掉落池/资源点配置表（寻宝系统更可控）
- 增加剧情条件与效果表（更复杂的分支触发）
- 引入 Flyway 统一管理数据库迁移版本

## 8. 注意事项

- `backend/target` 为编译产物，建议加入忽略并避免提交
- 当前默认配置偏本地开发，生产环境需替换账号密码与服务地址
- 如需多人协作，建议补充 API 文档与环境变量模板
