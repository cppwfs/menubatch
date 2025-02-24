package io.spring.aibatchtools;

import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class TikaItemReaderBuilder {

    private String name;

    private TextSplitter textSplitter = new TokenTextSplitter();

    private Resource resource;


    public TikaItemReaderBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TikaItemReaderBuilder textSplitter(TextSplitter textSplitter) {
        this.textSplitter = textSplitter;
        return this;
    }

    public TikaItemReaderBuilder resource(Resource resource) {
        this.resource = resource;
        return this;
    }


    public TikaItemReader build() {
        TikaItemReader tikaItemReader =  new TikaItemReader();
        Assert.notNull(this.resource, "resource must not be null");
        tikaItemReader.setTextSplitter(this.textSplitter);
        tikaItemReader.setName(this.name);
        tikaItemReader.setResource(this.resource);

        return tikaItemReader;
    }
}
