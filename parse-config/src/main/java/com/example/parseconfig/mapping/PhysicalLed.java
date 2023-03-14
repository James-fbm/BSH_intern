package com.example.parseconfig.mapping;

import com.example.parseconfig.entity.Led;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PhysicalLed {
    private List<Led> psLed;
    private List<Led> osLed;
}
