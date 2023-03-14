package com.example.parseconfig.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Led {
    private String name;
    private String hwId;
    private List<Mapping> mappingInfo;
}
