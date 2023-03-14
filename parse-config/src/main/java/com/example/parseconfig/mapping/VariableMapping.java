package com.example.parseconfig.mapping;

import com.example.parseconfig.entity.Variable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VariableMapping {
    private List<Variable> variable;
}
