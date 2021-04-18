package com.persistentminds.batch.config;

import com.persistentminds.batch.model.ReadUser;
import com.persistentminds.batch.model.WriteUser;
import com.persistentminds.batch.service.StringHeaderWriter;
import com.persistentminds.batch.service.UserProcessor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Configuration
@EnableBatchProcessing
@Component
public class CSVToCSVConfig {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ApplicationArguments applicationArguments;

    @Value("${csv.file.input}")
    private String csvPathInput;
    @Value("${csv.file.output}")
    private String csvPathOutput;

    @Bean
    public FlatFileItemReader<ReadUser> csvUserReader() {
        FlatFileItemReader<ReadUser> userReader = new FlatFileItemReader<>();
        userReader.setLinesToSkip(1);
        System.out.println("path: " + csvPathInput);
        System.out.println("path array: " + getInputOutputFiles()[0]);
        userReader.setResource(new ClassPathResource(getInputOutputFiles()[0]));
        userReader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"Id","Name","Email","Phone"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(ReadUser.class);
            }});
        }});
        return userReader;
    }

    @Bean
    public FlatFileItemWriter<WriteUser> csvUserWriter() {
        FlatFileItemWriter<WriteUser> userWriter = new FlatFileItemWriter<>();
        String exportFileHeader = "Id,Name,Phone,Website,Username";
        StringHeaderWriter headerWriter = new StringHeaderWriter(exportFileHeader);
        userWriter.setHeaderCallback(headerWriter);
        userWriter.setResource(new FileSystemResource(getInputOutputFiles()[1]));
        userWriter.setLineAggregator(new DelimitedLineAggregator<>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<>() {
                    {
                        setNames(new String[]{"id","name","phone","website","username"});
                    }
                });
            }
        });
        return userWriter;
    }

    @Bean
    public ItemProcessor<ReadUser, WriteUser> csvUserProcessor() {
        return new UserProcessor();
    }

    @Bean
    public Step csvFileToCSVStep() {
        return stepBuilderFactory.get("csvFileToCSVStep")
                .<ReadUser, WriteUser>chunk(1)
                .reader(csvUserReader())
                .processor(csvUserProcessor())
                .writer(csvUserWriter())
                .build();
    }

    @Bean
    public Job csvFileToCSVJob() {
        return jobBuilderFactory
                .get("csvFileToCSVJob")
                .incrementer(new RunIdIncrementer())
                .start(csvFileToCSVStep())
                .build();
    }

    @Bean
    public String[] getInputOutputFiles() {
        String[] source = applicationArguments.getSourceArgs();
        String[] responseSource = new String[2];
        if (source == null || source.length == 0 || ArrayUtils.isEmpty(source)) {
            responseSource[0] = csvPathInput;
            responseSource[1] = csvPathOutput;
        } else {
            System.out.println("input/output: " + Arrays.toString(source));
            for (int index = 0; index < source.length; index++) {
                responseSource[index] = source[index];
            }
        }
        return responseSource;
    }
}
