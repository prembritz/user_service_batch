package com.persistentminds.batch.service;

import com.persistentminds.batch.model.ReadUser;
import com.persistentminds.batch.model.User;
import com.persistentminds.batch.model.WriteUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class UserProcessor implements ItemProcessor<ReadUser, WriteUser> {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    RestTemplate restTemplate = new RestTemplate();
    @Value("${endpoint-url}")
    private String restUrl;
    ResponseEntity<User[]> apiUserResponse = null;
    List<User> results = null;

    @Override
    public WriteUser process(ReadUser readUser) throws Exception {
        WriteUser writeUser = new WriteUser();
        apiUserResponse = restTemplate.getForEntity(restUrl, User[].class);
        results = Arrays.asList(apiUserResponse.getBody());

        LOGGER.info("Get result:" + results.size());
        results.forEach((result) -> {
            if (readUser.getId().equals(String.valueOf(result.getId()))) {
                writeUser.setId(readUser.getId());
                writeUser.setName(result.getName());
                writeUser.setPhone(result.getPhone());
                writeUser.setWebsite(result.getWebsite());
                writeUser.setUsername(result.getUsername());
                LOGGER.info("account id=:" + result.getId() + " Name=: " + readUser.getName() +
                        " Phone=:" + result.getPhone() + " website=: " + result.getWebsite());
            }
        });


        return writeUser;
    }

}
