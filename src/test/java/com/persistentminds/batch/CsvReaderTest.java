package com.persistentminds.batch;


import com.persistentminds.batch.config.CSVToCSVConfig;
import org.junit.Test;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.FileSystemResource;

import static org.junit.Assert.assertEquals;

public class CsvReaderTest {

    private FlatFileItemReader<FieldSet> reader = new FlatFileItemReader<>();
    private static final String INPUT_FILE = "src/test/resources/input.csv";
    private static final int EXPECTED_COUNT = 4;

    @Test
    public void testing() throws Exception {
        CSVToCSVConfig csvToCSVConfig = new CSVToCSVConfig();
        csvToCSVConfig.csvUserReader();
        csvToCSVConfig.csvUserProcessor();
        csvToCSVConfig.csvUserWriter();


    }

    @Test
    public void testSuccessfulReading() throws Exception {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[]{"User ID", "Name", "Email", "User Name"});
        DefaultLineMapper<FieldSet> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new PassThroughFieldSetMapper());
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper);
        reader.setResource(new FileSystemResource(INPUT_FILE));
        reader.open(MetaDataInstanceFactory.createStepExecution().getExecutionContext());
        try {
            int count = 0;
            FieldSet line;
            while ((line = reader.read()) != null) {
                assertEquals("Id", line.getNames()[0]);
                assertEquals("Name", line.getNames()[1]);
                assertEquals("Email", line.getNames()[2]);
                assertEquals("Phone", line.getNames()[3]);
                count++;
            }
            assertEquals(EXPECTED_COUNT, count);
        } catch (Exception e) {
            throw e;
        } finally {
            reader.close();
        }
    }
}