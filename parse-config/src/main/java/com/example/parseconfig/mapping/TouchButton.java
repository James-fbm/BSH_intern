package com.example.parseconfig.mapping;

import com.example.parseconfig.entity.Button;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TouchButton {
    private List<Button> psButton;
    private List<Button> osButton;
}
