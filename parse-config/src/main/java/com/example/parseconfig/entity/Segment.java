package com.example.parseconfig.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Segment {
    private String name;
    private List<String> ledMember;
    private List<String> segmentMember;
    private Integer upperId;
    private List<Mapping> mappingInfo;
}
