package com.example.parseconfig.controller;

import com.example.parseconfig.mapping.*;
import com.example.parseconfig.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/save")
public class SaveController {
    @Autowired
    Parser parser;
    int t;

    @PostMapping("/touchButton")
    public String saveButton(@RequestBody TouchButton touchButton) {
        parser.parseTouchButton(touchButton.getPsButton(), 1);
        parser.parseTouchButton(touchButton.getOsButton(), 2);
        this.t += 1;
        return "Hello Button";
    }

    @PostMapping("/physicalLed")
    public String saveLed(@RequestBody PhysicalLed physicalLed) {
        parser.parsePhysicalLed(physicalLed.getPsLed(), 1);
        parser.parsePhysicalLed(physicalLed.getOsLed(), 2);
        this.t += 1;
        return "Hello Led";
    }

    @PostMapping("/virtualSegment")
    public String saveSegment(@RequestBody VirtualSegment virtualSegment) {
        parser.parseVirtualSegment(virtualSegment.getPsSegment(), 1);
        parser.parseVirtualSegment(virtualSegment.getOsSegment(), 2);
        this.t += 1;
        return "Hello Segment";
    }

    @PostMapping("/functionMapping")
    public String functionMapping(@RequestBody FunctionMapping functionMapping) {
        parser.parseFunctionMapping(functionMapping.getGroup());
        this.t += 1;
        return "Hello Mapping";
    }

    @PostMapping("/variableMapping")
    public String variableMapping(@RequestBody VariableMapping variableMapping) {
        parser.parseVariableMapping(variableMapping.getVariable());
        this.t += 1;
        return "Hello Variable";
    }

    @GetMapping("/exportFile")
    public String exportFile(@RequestParam("relativePath") String relativePath, @RequestParam("fileName") String fileName) {
        if (this.t < 5) {
            return "DATA MISSING";
        } else {
            this.t = 0;
            System.out.println("export file ...");
            parser.saveToFile(relativePath, fileName);
            parser.reset();
            System.out.println("export successful ...");
            return "SAVE SUCCESSFULLY";
        }
    }
}
