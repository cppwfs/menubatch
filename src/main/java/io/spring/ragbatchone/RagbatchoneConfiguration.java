package io.spring.ragbatchone;

import javax.sql.DataSource;

import io.spring.aibatchtools.TikaItemReaderBuilder;
import io.spring.aibatchtools.VectorStoreWriterBuilder;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableTask
public class RagbatchoneConfiguration {

    @Value("classpath:/docs/Menu.pdf")
    private Resource textResource;

    @Bean
    public ItemReader reader() {
        return new TikaItemReaderBuilder().name("PDFReader").
                textSplitter(new TokenTextSplitter()).
                resource(textResource).
                build();
    }

    @Bean
    public ItemWriter writer(VectorStore vectorStore) {
        return new VectorStoreWriterBuilder().
                contentFieldName("message").
                metaDataFieldName("keyData").
                vectorStore(vectorStore).build();
    }


    @Bean
    public Job processDocumentsJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importDocJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, VectorStore vectorStore) {
        return new StepBuilder("step1", jobRepository)
                .chunk(10, transactionManager)
                .reader(reader())
                .writer(writer(vectorStore))
                .build();
    }

    @Bean(name = "springDataSourceProperties")
    @ConfigurationProperties("spring.datasource")
    @Primary
    public DataSourceProperties springDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "vectorDataSourceProperties")
    @ConfigurationProperties("vector.datasource")
    public DataSourceProperties vectorSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "springDataSource")
    @Primary
    public DataSource dataSource(
            @Qualifier("springDataSourceProperties") DataSourceProperties springDataSourceProperties) {
        return DataSourceBuilder.create()
                .driverClassName(springDataSourceProperties.getDriverClassName())
                .url(springDataSourceProperties.getUrl())
                .password(springDataSourceProperties.getPassword())
                .username(springDataSourceProperties.getUsername())
                .build();
    }

    @Bean(name = "vectorDataSource")
    public DataSource vectorDataSource(
            @Qualifier("vectorDataSourceProperties") DataSourceProperties vectorDataSourceProperties) {
        return DataSourceBuilder.create()
                .driverClassName(vectorDataSourceProperties.getDriverClassName())
                .url(vectorDataSourceProperties.getUrl())
                .password(vectorDataSourceProperties.getPassword())
                .username(vectorDataSourceProperties.getUsername())
                .build();
    }

    @Bean
    @Primary
    JdbcTemplate jdbcTemplate(@Qualifier("vectorDataSource") DataSource vectorDataSource, JdbcProperties properties) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(vectorDataSource);
        JdbcProperties.Template template = properties.getTemplate();
        jdbcTemplate.setFetchSize(template.getFetchSize());
        jdbcTemplate.setMaxRows(template.getMaxRows());
        if (template.getQueryTimeout() != null) {
            jdbcTemplate.setQueryTimeout((int)template.getQueryTimeout().getSeconds());
        }

        return jdbcTemplate;
    }
}
