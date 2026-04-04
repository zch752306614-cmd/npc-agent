package com.npcagent.service;

import com.npcagent.config.MilvusConfig;
import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.SearchResults;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.SearchResultsWrapper;
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
import java.util.concurrent.TimeUnit;

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
 * - 使用Milvus Java SDK v1 API进行向量操作
 * - 支持自动创建集合和索引
 * - 提供向量CRUD操作
 * - 支持连接失败时的降级处理
 */
@Service
public class MilvusService {

    private static final Logger logger = LoggerFactory.getLogger(MilvusService.class);

    private final MilvusConfig milvusConfig;
    private MilvusClient milvusClient;
    private boolean connected = false;

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
            connected = true;
            logger.info("Milvus service initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Milvus service: {}", e.getMessage());
            connected = false;
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
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost(milvusConfig.getHost())
                .withPort(milvusConfig.getPort())
                .withDatabaseName(milvusConfig.getDatabase())
                .withConnectTimeout(5000, TimeUnit.MILLISECONDS)
                .build();

        milvusClient = new MilvusServiceClient(connectParam);
        logger.info("Connected to Milvus at {}:{}", milvusConfig.getHost(), milvusConfig.getPort());
    }

    private void createCollectionIfNotExists() {
        String collectionName = milvusConfig.getCollectionName();

        HasCollectionParam hasCollectionParam = HasCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        R<Boolean> hasCollectionResponse = milvusClient.hasCollection(hasCollectionParam);

        if (hasCollectionResponse.getData() == null || !hasCollectionResponse.getData()) {
            List<FieldType> fields = new ArrayList<>();

            FieldType nodeIdField = FieldType.newBuilder()
                    .withName(FIELD_NODE_ID)
                    .withDataType(io.milvus.grpc.DataType.VarChar)
                    .withMaxLength(256)
                    .build();

            FieldType contentField = FieldType.newBuilder()
                    .withName(FIELD_CONTENT)
                    .withDataType(io.milvus.grpc.DataType.VarChar)
                    .withMaxLength(1024)
                    .build();

            FieldType vectorField = FieldType.newBuilder()
                    .withName(FIELD_VECTOR)
                    .withDataType(io.milvus.grpc.DataType.FloatVector)
                    .withDimension(milvusConfig.getEmbeddingDimension())
                    .build();

            fields.add(nodeIdField);
            fields.add(contentField);
            fields.add(vectorField);

            CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withDescription("NPC Agent vector storage")
                    .withFieldTypes(fields)
                    .build();

            R<RpcStatus> createResponse = milvusClient.createCollection(createCollectionParam);
            if (createResponse.getStatus() != R.Status.Success.getCode()) {
                logger.error("Failed to create collection: {}", createResponse.getMessage());
                return;
            }

            CreateIndexParam indexParam = CreateIndexParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withFieldName(FIELD_VECTOR)
                    .withIndexType(IndexType.IVF_FLAT)
                    .withMetricType(MetricType.COSINE)
                    .withExtraParam("{\"nlist\":1024}")
                    .build();

            R<RpcStatus> indexResponse = milvusClient.createIndex(indexParam);
            if (indexResponse.getStatus() != R.Status.Success.getCode()) {
                logger.error("Failed to create index: {}", indexResponse.getMessage());
                return;
            }

            R<RpcStatus> loadResponse = milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build());

            if (loadResponse.getStatus() != R.Status.Success.getCode()) {
                logger.error("Failed to load collection: {}", loadResponse.getMessage());
                return;
            }

            logger.info("Created collection: {}", collectionName);
        } else {
            logger.info("Collection already exists: {}", collectionName);
        }
    }

    public void insertVector(String nodeId, String content, List<Float> vector) {
        if (!connected) {
            logger.warn("Milvus service is not connected, cannot insert vector");
            return;
        }

        try {
            List<InsertParam.Field> fields = new ArrayList<>();
            fields.add(new InsertParam.Field(FIELD_NODE_ID, Collections.singletonList(nodeId)));
            fields.add(new InsertParam.Field(FIELD_CONTENT, Collections.singletonList(content)));
            fields.add(new InsertParam.Field(FIELD_VECTOR, Collections.singletonList(vector)));

            InsertParam insertParam = InsertParam.newBuilder()
                    .withCollectionName(milvusConfig.getCollectionName())
                    .withFields(fields)
                    .build();

            R<io.milvus.grpc.MutationResult> insertResponse = milvusClient.insert(insertParam);
            if (insertResponse.getStatus() == R.Status.Success.getCode()) {
                logger.debug("Inserted vector for node: {}", nodeId);
            } else {
                logger.error("Failed to insert vector: {}", insertResponse.getMessage());
            }
        } catch (Exception e) {
            logger.error("Failed to insert vector: {}", e.getMessage());
        }
    }

    public List<Map<String, Object>> searchSimilarVectors(List<Float> queryVector, int topK) {
        if (!connected) {
            logger.warn("Milvus service is not connected, returning empty result");
            return Collections.emptyList();
        }

        try {
            List<List<Float>> vectors = new ArrayList<>();
            vectors.add(queryVector);

            SearchParam searchParam = SearchParam.newBuilder()
                    .withCollectionName(milvusConfig.getCollectionName())
                    .withMetricType(MetricType.COSINE)
                    .withTopK(topK)
                    .withVectors(vectors)
                    .withVectorFieldName(FIELD_VECTOR)
                    .withOutFields(List.of(FIELD_NODE_ID, FIELD_CONTENT))
                    .build();

            R<SearchResults> searchResponse = milvusClient.search(searchParam);
            if (searchResponse.getStatus() != R.Status.Success.getCode()) {
                logger.error("Failed to search vectors: {}", searchResponse.getMessage());
                return Collections.emptyList();
            }

            SearchResultsWrapper wrapper = new SearchResultsWrapper(searchResponse.getData().getResults());
            List<SearchResultsWrapper.IDScore> results = wrapper.getIDScore(0);

            List<Map<String, Object>> searchResults = new ArrayList<>();
            for (SearchResultsWrapper.IDScore result : results) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("id", result.getLongID());
                resultMap.put("score", result.getScore());
                resultMap.put("nodeId", result.get(FIELD_NODE_ID));
                resultMap.put("content", result.get(FIELD_CONTENT));
                searchResults.add(resultMap);
            }

            logger.debug("Found {} similar vectors", searchResults.size());
            return searchResults;
        } catch (Exception e) {
            logger.error("Failed to search vectors: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void dropCollection() {
        if (!connected) {
            logger.warn("Milvus service is not connected, cannot drop collection");
            return;
        }

        try {
            DropCollectionParam dropCollectionParam = DropCollectionParam.newBuilder()
                    .withCollectionName(milvusConfig.getCollectionName())
                    .build();

            R<RpcStatus> response = milvusClient.dropCollection(dropCollectionParam);
            if (response.getStatus() == R.Status.Success.getCode()) {
                logger.info("Dropped collection: {}", milvusConfig.getCollectionName());
            } else {
                logger.error("Failed to drop collection: {}", response.getMessage());
            }
        } catch (Exception e) {
            logger.error("Failed to drop collection: {}", e.getMessage());
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
