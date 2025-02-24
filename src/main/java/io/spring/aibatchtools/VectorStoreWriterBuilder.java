package io.spring.aibatchtools;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.util.Assert;

public class VectorStoreWriterBuilder {

    private String name;
    private VectorStore vectorStore;

    private String contentFieldName;

    private String metaDataFieldName;

    public VectorStoreWriterBuilder name(String name) {
        this.name = name;
        return this;
    }

    public VectorStoreWriterBuilder vectorStore(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        return this;
    }

    public VectorStoreWriterBuilder contentFieldName(String contentFieldName) {
        this.contentFieldName = contentFieldName;
        return this;
    }

    public VectorStoreWriterBuilder metaDataFieldName(String metaDataFieldName) {
        this.metaDataFieldName = metaDataFieldName;
        return this;
    }

    public VectorStoreWriter build() {
        VectorStoreWriter vectorStoreWriter =  new VectorStoreWriter<>();
        Assert.notNull(this.vectorStore, "VectorStore must not be null");
        vectorStoreWriter.setVectorStore(this.vectorStore);
        vectorStoreWriter.setName(this.name);
        if (contentFieldName.isEmpty()) {
            throw new IllegalArgumentException("contentFieldName must not be null.");
        }
        vectorStoreWriter.setContentFieldName(contentFieldName);
        vectorStoreWriter.setMetadataFieldName(metaDataFieldName);
        if (metaDataFieldName.isEmpty()) {
            throw new IllegalArgumentException("metdataFieldName must not be null.");
        }
        return vectorStoreWriter;
    }
}
