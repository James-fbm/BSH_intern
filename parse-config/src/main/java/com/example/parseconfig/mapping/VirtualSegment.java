package com.example.parseconfig.mapping;

import com.example.parseconfig.entity.Segment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VirtualSegment {
    public List<Segment> psSegment;
    public List<Segment> osSegment;
}
