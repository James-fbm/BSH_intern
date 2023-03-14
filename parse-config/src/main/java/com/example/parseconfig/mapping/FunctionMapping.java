package com.example.parseconfig.mapping;

import com.example.parseconfig.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FunctionMapping {
    private List<Group> group;
}
