package io.spring.aibatchtools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.core.io.Resource;

import java.util.List;

public class TikaItemReader extends AbstractItemStreamItemReader<Document> {

    private final Logger logger =  LoggerFactory.getLogger(this.getClass());

    private Resource resource;

    private TikaDocumentReader tikaDocumentReader;

    private List<Document> documents;

    private int docCount = 0;

    private TextSplitter textSplitter = new TokenTextSplitter();

    public Document read() throws UnexpectedInputException, ParseException, NonTransientResourceException {
        if (documents == null) {
            initializeDocuments();
        }

        if (docCount >= documents.size()) {
            return null; // No more documents to read
        }

        return documents.get(docCount++);
    }

    private void initializeDocuments() {
        try {
            logger.info("Initializing document processing with Tika");
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
            documents = textSplitter.apply(tikaDocumentReader.read());
            logger.info("Document initialization complete. Total documents: {}", documents.size());
        } catch (Exception e) {
            logger.error("Error during document initialization", e);
            throw new IllegalStateException("Failed to initialize documents", e);
        }
    }



    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public TextSplitter getTextSplitter() {
        return textSplitter;
    }

    public void setTextSplitter(TextSplitter textSplitter) {
        this.textSplitter = textSplitter;
    }
}
