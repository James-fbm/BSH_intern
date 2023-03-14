package com.example.parseconfig.parser;

import com.example.parseconfig.entity.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class Parser {
    private Document document;

    public Parser() {
        this.reset();
    }

    public void parseTouchButton(List<Button> listButton, Integer boardType) {
        Element root = this.document.getRootElement();
        String touchButtonPath;
        if (boardType == 1) {
            touchButtonPath = "/IABO_Config/UIM-Domain_IABO_PS/InputUnits/Buttons/TouchButtons";
        } else if (boardType == 2) {
            touchButtonPath = "/IABO_Config/UIM-Domain_IABO_OS/InputUnits/Buttons/TouchButtons";
        } else {
            return;
        }
        Element touchButton = (Element) root.selectNodes(touchButtonPath).get(0);
        for (Button button: listButton) {
            Element newTouchButton = touchButton.addElement("Unit");
            String buttonName = button.getName();
            String buttonHwId = button.getHwId();
            newTouchButton.addAttribute("name", buttonName == null ? "" : buttonName);
            newTouchButton.addAttribute("hwId", buttonHwId == null ? "" : buttonHwId);
        }
    }

    public void parsePhysicalLed(List<Led> listLed, Integer boardType) {
        Element root = this.document.getRootElement();
        String physicalLedPath;

        if (boardType == 1)
            physicalLedPath = "/IABO_Config/UIM-Domain_IABO_PS/OutputUnits/LEDs/PhysicalLEDs";
        else if (boardType == 2) {
            physicalLedPath = "/IABO_Config/UIM-Domain_IABO_OS/OutputUnits/LEDs/PhysicalLEDs";
        } else {
            return;
        }
        Element physicalLed = (Element) root.selectNodes(physicalLedPath).get(0);
        for (Led led: listLed) {
            Element newLed = physicalLed.addElement("Unit");
            String ledName = led.getName();
            String ledHwId = led.getHwId();
            newLed.addAttribute("name", ledName == null ? "" : ledName);
            newLed.addAttribute("hwId", ledHwId == null ? "" : ledHwId);
        }
    }

    public void parseVirtualSegment(List<Segment> listSegment, Integer boardType) {
        Element root = this.document.getRootElement();
        String virtualSegmentPath;
        if (boardType == 1)
            virtualSegmentPath = "/IABO_Config/UIM-Domain_IABO_PS/OutputUnits/LEDs/VirtualSegments";
        else if (boardType == 2) {
            virtualSegmentPath = "/IABO_Config/UIM-Domain_IABO_OS/OutputUnits/LEDs/VirtualSegments";
        } else {
            return;
        }
        Element virtualSegment = (Element) root.selectNodes(virtualSegmentPath).get(0);
        for (Segment segment: listSegment) {
            // 新建一个Segment
            Element newSegment;
            String segmentName = segment.getName();
            // 如果一个Segment的parentId为0，则将其视为VirtualSegmentString
            if (segment.getUpperId() == 0) {
                newSegment = virtualSegment.addElement("VirtualSegmentString");
            } else {
                newSegment = virtualSegment.addElement("VirtualSegment");
            }
            newSegment.addAttribute("name", segmentName == null ? "" : segmentName);
            // 遍历Segment的所有子组件
            Element newMember;
            for (String ledElement: segment.getLedMember()) {
                newMember = newSegment.addElement("PhysicalUnit");
                newMember.addAttribute("name", ledElement == null ? "" : ledElement);
            }
            for (String segElement: segment.getSegmentMember()) {
                newMember = newSegment.addElement("VirtualUnit");
                newMember.addAttribute("name", segElement == null ? "" : segElement);
            }
        }
    }

    public void parseFunctionMapping(List<Group> listGroup) {
        Element root = this.document.getRootElement();
        String simpleMappingPath = "/IABO_Config/UIM-Domain_only/OutputUnits/LEDs/LEDFunctionMappingSimple";
        String extendMappingPath = "/IABO_Config/UIM-Domain_only/OutputUnits/LEDs/LEDFunctionMappingExtended";
        String buttonMappingPath = "/IABO_Config/UIM-Domain_only/InputUnits/Buttons/ButtonFunctions";
        for (Group group: listGroup) {
            if (group.getButtonMember().size() == 0 &&
                    (group.getSegmentMember().size() == 0 && group.getLedMember().size() == 1 && group.getLedMember().get(0).getMappingInfo().size() == 1 ||
                    group.getLedMember().size() == 0 && group.getSegmentMember().size() == 1 && group.getSegmentMember().get(0).getMappingInfo().size() == 1 ||
                    group.getLedMember().size() == 0 && group.getSegmentMember().size() == 0)) {
                // simple mapping
                if (group.getLedMember().size() == 1) {
                    Led led = group.getLedMember().get(0);
                    Element simpleMapping = (Element) root.selectNodes(simpleMappingPath).get(0);
                    Element simpleElement = simpleMapping.addElement("LEDFunction");
                    simpleElement.addAttribute("name", group.getName() == null ? "" : group.getName());
                    String unitName = led.getMappingInfo().get(0).getMappingName();
                    String ledMode = led.getMappingInfo().get(0).getMode();
                    simpleElement.addAttribute("unitname", unitName == null ? "" : unitName);
                    simpleElement.addAttribute("ledMode", ledMode == null ? "" : ledMode);
                }
                if (group.getSegmentMember().size() == 1) {
                    Segment segment = group.getSegmentMember().get(0);
                    Element simpleMapping = (Element) root.selectNodes(simpleMappingPath).get(0);
                    Element simpleElement = simpleMapping.addElement("LEDFunction");
                    simpleElement.addAttribute("name", group.getName() == null ? "" : group.getName());
                    String unitName = segment.getMappingInfo().get(0).getMappingName();
                    String segmentMode = segment.getMappingInfo().get(0).getMode();
                    simpleElement.addAttribute("unitname", unitName == null ? "" : unitName);
                    simpleElement.addAttribute("ledMode", segmentMode == null ? "" : segmentMode);
                }
            } else {
                for (Button button: group.getButtonMember()) {
                    Element buttonMapping = (Element) root.selectNodes(buttonMappingPath).get(0);
                    Element buttonElement = buttonMapping.addElement("ButtonMapping");
                    buttonElement.addAttribute("name", group.getName() == null ? "" : group.getName());
                    buttonElement.addAttribute("unitname", button.getName() == null ? "" : button.getName());
                    buttonElement.addAttribute("buttonMode", button.getMode() == null ? "" : button.getMode());
                }
                Element extendMapping = (Element) root.selectNodes(extendMappingPath).get(0);
                Element extendElement = extendMapping.addElement("LEDFunctionExtended");
                extendElement.addAttribute("name", group.getName());
                for (Led led: group.getLedMember()) {
                    for (Mapping mapping: led.getMappingInfo()) {
                        Element ledElement = extendElement.addElement("LED");
                        ledElement.addAttribute("name", mapping.getMappingName() == null ? "" : mapping.getMappingName());
                        ledElement.addAttribute("unitname", led.getName() == null ? "" : led.getName());
                        ledElement.addAttribute("ledMode", mapping.getMode() == null ? "" : mapping.getMode());
                    }
                }
                for (Segment segment: group.getSegmentMember()) {
                    for (Mapping mapping: segment.getMappingInfo()) {
                        Element segmentElement = extendElement.addElement("LED");
                        segmentElement.addAttribute("name", mapping.getMappingName() == null ? "" : mapping.getMappingName());
                        segmentElement.addAttribute("unitname", segment.getName() == null ? "" : segment.getName());
                        segmentElement.addAttribute("ledMode", mapping.getMode() == null ? "" : mapping.getMode());
                    }
                }
            }
        }
    }

    public void parseVariableMapping(List<Variable> listVariable) {
        Element root = this.document.getRootElement();
        String variableMappingPath = "/IABO_Config/UIM-Domain_only/OutputUnits/LEDs/LEDFunctionMappingVariable";
        for (Variable variable: listVariable) {
            String variableName = variable.getName();
            Element variableMapping = (Element) root.selectNodes(variableMappingPath).get(0);
            Element variableElement = variableMapping.addElement("LEDFunctionVariable");
            variableElement.addAttribute("name", variableName == null ? "" : variableName);
            List<Group> groupMember = variable.getGroupMember();

            for (Group group : groupMember) {
                String groupName = group.getName();
                Element groupElement = variableElement.addElement("LEDFunctionExtended");
                groupElement.addAttribute("name", groupName == null ? "" : groupName);
                List<Led> ledMember = group.getLedMember();
                List<Segment> segmentMember = group.getSegmentMember();

                // 遍历group中所存的led和segment
                for (Led led: ledMember) {
                    String unitName = led.getName();
                    List<Mapping> ledMapping = led.getMappingInfo();
                    for (Mapping mapping: ledMapping) {
                        String ledMappingName = mapping.getMappingName();
                        String ledMode = mapping.getMode();
                        Element ledElement = groupElement.addElement("LED");
                        ledElement.addAttribute("name", ledMappingName == null ? "" : ledMappingName);
                        ledElement.addAttribute("unitName", unitName == null ? "" : unitName);
                        ledElement.addAttribute("ledMode", ledMode == null ? "" : ledMode);
                    }
                }
                for (Segment segment: segmentMember) {
                    String unitName = segment.getName();
                    List<Mapping> ledMapping = segment.getMappingInfo();
                    for (Mapping mapping: ledMapping) {
                        String segmentMappingName = mapping.getMappingName();
                        String segmentMode = mapping.getMode();
                        Element ledElement = groupElement.addElement("LED");
                        ledElement.addAttribute("name", segmentMappingName == null ? "" : segmentMappingName);
                        ledElement.addAttribute("unitName", unitName == null ? "" : unitName);
                        ledElement.addAttribute("ledMode", segmentMode == null ? "" : segmentMode);
                    }
                }

            }
        }
    }

    public void saveToFile(String relativePath, String filename) {
        try {
            if (Objects.equals(relativePath, "desktop")) {
                relativePath = System.getProperty("user.home") + "/Desktop";
            } else if (Objects.equals(relativePath, "download")) {
                relativePath = System.getProperty("user.home") + "/Downloads";
            } else if (Objects.equals(relativePath, "document")) {
                relativePath = System.getProperty("user.home") + "/Documents";
            } else {
                relativePath = relativePath.replace('\\', '/');
            }
            if (!filename.endsWith(".xml")) {
                filename += ".xml";
            }
            OutputFormat prettyFormat = OutputFormat.createPrettyPrint();
            prettyFormat.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(relativePath + "/" + filename))), prettyFormat);
            writer.write(this.document);
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void reset() {
        try {
            SAXReader saxReader = new SAXReader();
            this.document = saxReader.read("src/main/resources/static/basic_2.xml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
