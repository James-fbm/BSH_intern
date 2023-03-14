package com.example.parseconfig.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    private String name;
    private List<Button> buttonMember;
    private List<Led> ledMember;
    private List<Segment> segmentMember;
}
