package com.persistentminds.batch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WriteUser {
    private String id;
    private String name;
    private String phone;
    private String website;
    private String username;
}
