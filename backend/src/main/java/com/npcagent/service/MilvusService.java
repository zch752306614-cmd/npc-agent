package com.npcagent.service;

import com.npcagent.config.MilvusConfig;
import io.milvus.v2.client.MilvusServiceClient;
import io.milvus.v2.client.ConnectParam;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.HasCollectionReq;
import io.milvus.v2.service.collection.request.DropCollectionReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.SearchResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Milvus向量数据库服务
 *
 * 提供向量存储和检索功能：
 * 1. 连接Milvus服务器
 * 2. 创建和管理集合
 * 3. 插入和检索向量
 * 4. 执行向量相似度搜索
 *
 * 实现说明：
 * - 使用Milvus Java SDK进行向量操作
 * - 支持自动创建集合和索引
 * - 提供向量CRUD操作
 */
@Service
public class MilvusService {

    private static final Logger logger = LoggerFactory.getLogger(MilvusService.class);

    private final MilvusConfig milvusConfig;
    private MilvusServiceClient milvusClient;

    private static final String FIELD_ID = "id";
    private static final String FIELD_VECTOR = "vector";
    private static final String FIELD_NODE_ID = "node_id";
    private static final String FIELD_CONTENT = "content";

    public MilvusService(MilvusConfig milvusConfig) {
        this.milvusConfig = milvusConfig;
    }

    @PostConstruct
    public void init() {
        try {
            connect();
            createCollectionIfNotExists();
            logger.info("Milvus service initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Milvus service: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        if (milvusClient != null) {
            try {
                milvusClient.close();
                logger.info("Milvus client closed successfully");
            } catch (Exception e) {
                logger.error("Failed to close Milvus client: {}", e.getMessage());
            }
        }
    }

    private void connect() {
        ConnectParam connectParam = ConnectParam.builder()
                .host(milvusConfig.getHost())
                .port(milvusConfig.getPort())
                .databaseName(milvusConfig.getDatabase())
                .build();

        milvusClient = new MilvusServiceClient(connectParam);
        logger.info("Connected to Milvus at {}:{}", milvusConfig.getHost(), milvusConfig.getPort());
    }

    private void createCollectionIfNotExists() {
        String collectionName = milvusConfig.getCollectionName();

        HasCollectionReq hasCollectionReq = HasCollectionReq.builder()
                .collectionName(collectionName)
                .build();

        Boolean hasCollection = milvusClient.hasCollection(hasCollectionReq);

        if (!hasCollection) {
            CreateCollectionReq.FieldSchema idField = CreateCollectionReq.FieldSchema.builder()
                    .name(FIELD_ID)
                    .dataType(DataType.Int64)
                    .isPrimaryKey(true)
                    .autoID(true)
                    .build();

            CreateCollectionReq.FieldSchema nodeIdField = CreateCollectionReq.FieldSchema.builder()
                    .name(FIELD_NODE_ID)
                    .dataType(DataType.VarChar)
                    .maxLength(256)
                    .build();

            CreateCollectionReq.FieldSchema contentField = CreateCollectionReq.FieldSchema.builder()
                    .name(FIELD_CONTENT)
                    .dataType(DataType.VarChar)
                    .maxLength(1024)
                    .build();

            CreateCollectionReq.FieldSchema vectorField = CreateCollectionReq.FieldSchema.builder()
                    .name(FIELD_VECTOR)
                    .dataType(DataType.FloatVector)
                    .dimension(milvusConfig.getEmbeddingDimension())
                    .build();

            CreateCollectionReq createCollectionReq = CreateCollectionReq.builder()
                    .collectionName(collectionName)
                    .description("NPC Agent vector storage")
                    .fieldSchemaList(List.of(idField, nodeIdField, contentField, vectorField))
                    .build();

            milvusClient.createCollection(createCollectionReq);

            IndexParam indexParam = IndexParam.builder()
                    .fieldName(FIELD_VECTOR)
                    .indexType(IndexParam.IndexType.IVF_FLAT)
                    .metricType(IndexParam.MetricType.COSINE)
                    .extraParams(Map.of("nlist", 1024))
                    .build();

            milvusClient.createIndex(IndexParam.builder()
                    .collectionName(collectionName)
                    .indexParams(List.of(indexParam))
                    .build());

            milvusClient.loadCollection(io.milvus.v2.service.collection.request.LoadCollectionReq.builder()
                    .collectionName(collectionName)
                    .build());

            logger.info("Created collection: {}", collectionName);
        } else {
            logger.info("Collection already exists: {}", collectionName);
        }
    }

    public void insertVector(String nodeId, String content, List<Float> vector) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put(FIELD_NODE_ID, nodeId);
            data.put(FIELD_CONTENT, content);
            data.put(FIELD_VECTOR, vector);

            InsertReq insertReq = InsertReq.builder()
                    .collectionName(milvusConfig.getCollectionName())
                    .data(List.of(data))
                    .build();

            milvusClient.insert(insertReq);
            logger.debug("Inserted vector for node: {}", nodeId);
        } catch (Exception e) {
            logger.error("Failed to insert vector: {}", e.getMessage());
        }
    }

    public List<Map<String, Object>> searchSimilarVectors(List<Float> queryVector, int topK) {
        try {
            FloatVec queryData = new FloatVec(queryVector);

            SearchReq searchReq = SearchReq.builder()
                    .collectionName(milvusConfig.getCollectionName())
                    .data(Collections.singletonList(queryData))
                    .annsField(FIELD_VECTOR)
                    .topK(topK)
                    .metricType(IndexParam.MetricType.COSINE)
                    .build();

            SearchResp searchResp = milvusClient.search(searchReq);

            List<Map<String, Object>> results = new ArrayList<>();
            for (List<SearchResp.SearchResult> searchResults : searchResp.getSearchResults()) {
                for (SearchResp.SearchResult result : searchResults) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("id", result.getId());
                    resultMap.put("score", result.getScore());
                    resultMap.put("nodeId", result.getEntity().get(FIELD_NODE_ID));
                    resultMap.put("content", result.getEntity().get(FIELD_CONTENT));
                    results.add(resultMap);
                }
            }

            logger.debug("Found {} similar vectors", results.size());
            return results;
        } catch (Exception e) {
            logger.error("Failed to search vectors: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void dropCollection() {
        try {
            DropCollectionReq dropCollectionReq = DropCollectionReq.builder()
                    .collectionName(milvusConfig.getCollectionName())
                    .build();

            milvusClient.dropCollection(dropCollectionReq);
            logger.info("Dropped collection: {}", milvusConfig.getCollectionName());
        } catch (Exception e) {
            logger.error("Failed to drop collection: {}", e.getMessage());
        }
    }

    public boolean isConnected() {
        return milvusClient != null;
    }
}
