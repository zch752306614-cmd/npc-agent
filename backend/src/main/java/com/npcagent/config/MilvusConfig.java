package com.npcagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Milvus向量数据库配置类
 *
 * 用于配置Milvus连接参数：
 * - host: Milvus服务器地址
 * - port: Milvus服务器端口
 * - database: 数据库名称
 * - collectionName: 集合名称
 * - embeddingDimension: 向量维度
 */
@Configuration
@ConfigurationProperties(prefix = "milvus")
public class MilvusConfig {

    private String host = "localhost";
    private int port = 19530;
    private String database = "default";
    private String collectionName = "npc_agent_vectors";
    private int embeddingDimension = 768;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public int getEmbeddingDimension() {
        return embeddingDimension;
    }

    public void setEmbeddingDimension(int embeddingDimension) {
        this.embeddingDimension = embeddingDimension;
    }
}
