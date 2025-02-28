package io.spring.aibatchtools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collections;
import java.util.function.BiConsumer;

public class VectorStoreWriter<T> implements ItemWriter<T>, InitializingBean {

    private final Logger logger =  LoggerFactory.getLogger(this.getClass());

    private VectorStore vectorStore;

    private String name;

    private String metadataFieldName = null;

    private String contentFieldName = null;

    @Override
    public void write(Chunk<? extends T> chunk) {
        for(T item : chunk.getItems()) {
            Document document = (Document) item;
            logger.info("Writing doc");
            vectorStore.accept(Collections.singletonList(document));
        }
    }

    public VectorStore getVectorStore() {
        return vectorStore;
    }

    public void setVectorStore(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetadataFieldName() {
        return metadataFieldName;
    }

    public void setMetadataFieldName(String metadataFieldName) {
        this.metadataFieldName = metadataFieldName;
    }

    public String getContentFieldName() {
        return contentFieldName;
    }

    public void setContentFieldName(String contentFieldName) {
        this.contentFieldName = contentFieldName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }


}
