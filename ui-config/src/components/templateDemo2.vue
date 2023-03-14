<template>
    <div>
        <el-col :span="2" style="background-color:#F2F6FC;height:800px;text-align: center;">
            <div style="text-align: center;margin-bottom: 20px;font-size: 20px;">Elements</div>
            <div draggable="true" id="button" @dragstart="dragElementType=1" @dragend="dragElementType=0">
            </div>
            <p style="margin-bottom:20px">Button</p>
            <div style="color:blue;margin:10px;font-size: 35px;">
                <i class="el-icon-s-opportunity" circle draggable="true" id="Led" @dragstart="dragElementType=2" @dragend="dragElementType=0"></i>
            </div>
            <p style="margin-bottom:20px">Led</p>
            <div draggable="true" id="segment" @dragstart="dragElementType=3" @dragend="dragElementType=0">
                <img src="../assets/1.png" style="width:50px;height:100px">
                Segment
            </div>
        </el-col>
        
        <el-col :span="22" style="overflow-x:auto" v-loading="loading">
            <div :style="radioClass">
                <el-radio v-model="boardType" label="1" @change="loadBoard()">PS</el-radio>
                <el-radio v-model="boardType" label="2" @change="loadBoard()">OS</el-radio>
                <el-button @click="loadGroup();">Edit Mapping Info</el-button>  
                <el-button @click="loadVariable();">Add Variable</el-button> 
                <el-button @click="loadSegment();">Edit Segment Info</el-button>
            </div>
            <div id="MainArea" :style="workspaceDivClass">
                
                <div :style="iconDivClass">
                    <span>
                        <i class="el-icon-zoom-in" @click="zoomIn"></i>
                    </span>
                    <el-divider direction="vertical"></el-divider>
                    <span>
                        <i class="el-icon-zoom-out" @click="zoomOut"></i>
                    </span>
                </div>
                
                <!--生成所有网格-->
                <div v-for="index in rowGridNum*rowGridNum" :key="index">
                    <el-tooltip class="item" :disabled="gridElementOverlap[index-1]==null" :content="gridHover(index-1)" placement="right" :open-delay="200">
                        <div :style="gridStyle[index-1]" :id="index-1"
                            @dragover="ondragover($event)" @drop="ondrop($event, index-1)"
                            @click="ifGridDialogFormVisible(index-1)">
                        </div>
                    </el-tooltip>
                </div>
                
                <br />
                <el-button type="primary" @click="ifDialogSaveVisible"> Save </el-button>
                <el-button type="primary" @click="dialogExportVisible=true; exportRelativePath=''"> Export </el-button>

                <!-- 更改某一网格所有组件属性的表单 -->
                <el-dialog title="Set Attribute" :visible.sync="dialogFormVisible">
                    <!-- 这里需要接受参数gridDialogIndex(点击的网格的index) -->
                    <el-form :inline="true">

                        <!-- 用键值对的方式遍历targetGridElement中的一个对象 -->
                        <!-- 根据targetGridElement的结构，第一个v-for遍历了一个网格中所有组件的类型 -->
                        <!-- elementList为某一类型的组件列表，elementName为组件名 -->
                        <div v-for="(elementList, elementName, index) in targetGridElement" :key="index">

                            <!-- v-if根据每个组件的类型（elementName的值）生成对应表单项。 -->
                            <div v-if="elementName==='button'">
                                <el-divider><h4>BUTTON</h4></el-divider>
                                <!-- 第二个v-for遍历了某一类型组件的组件列表（elementList） -->
                                <div v-for="(buttonElement, buttonId, index) in elementList" :key="index">
                                    <div v-if="buttonElement.toDelete==0">
                                        <el-row>
                                            <el-col :span="18">
                                                <p>Id:{{buttonId}}</p>
                                            </el-col>
                                            <el-col :span="6">
                                                <el-button type="warning" icon="el-icon-delete" @click="selectButtonId=buttonId; 
                                                     dialogDeleteButtonVisible=true;" size="medium">Delete</el-button>
                                            </el-col>
                                        </el-row>
                                        <el-row>
                                            <el-col :span="18">
                                                <p v-if="targetGridElement.button[buttonId].parentId!=0">
                                                    Mapping: {{group[buttonElement.parentId].name}}
                                                </p>
                                                <p v-else>Mapping: none</p>
                                            </el-col>
                                            <el-col :span="6">
                                                <el-button type="info" icon="el-icon-folder-opened" @click="selectButtonId=buttonId;
                                                dialogSelectButtonGroupVisible=true" size="medium">Change</el-button>
                                            </el-col>
                                        </el-row>
                                        <el-form-item label="name">
                                            <el-input v-model="buttonElement.name"></el-input>
                                        </el-form-item>
                                        <el-form-item label="hwId">
                                            <el-input v-model="buttonElement.hwId"></el-input>
                                        </el-form-item>
                                        
                                    </div> 
                                </div>
                            </div>

                            <!-- 同Button -->
                            <div v-if="elementName==='led'">
                                <el-divider><h4>Led</h4></el-divider>
                                <div v-for="(ledElement, ledId, index) in elementList" :key="index">
                                    <div v-if="ledElement.toDelete==0">
                                        <el-row>
                                            <el-col :span="8">
                                                <p>Id:{{ledId}}</p>
                                            </el-col>
                                            <el-col :span="6" :offset="10">
                                                <el-button type="warning" icon="el-icon-delete" @click="selectLedId=ledId; 
                                                dialogDeleteLedVisible=true;" size="medium">Delete</el-button>
                                            </el-col>
                                        </el-row>
                                        <el-row>
                                            <el-col :span="18">
                                                <p v-if="targetGridElement.led[ledId].parentId!=0">
                                                    Mapping: {{group[ledElement.parentId].name}}
                                                </p>
                                                <p v-else>Mapping: none</p>
                                            </el-col>
                                            <el-col :span="6" v-if="targetGridElement.led[ledId].segmentId==0">
                                                <el-button type="info" icon="el-icon-folder-opened" @click="selectLedId=ledId;
                                                dialogSelectLedGroupVisible=true" size="medium">Change</el-button>
                                            </el-col>
                                        </el-row>
                                        <el-row>
                                            <el-col :span="18">
                                                <p v-if="segment[ledElement.segmentId]">Segment: {{segment[ledElement.segmentId].name}}</p>
                                                <p v-else>Segment: none</p>
                                            </el-col>
                                            <el-col :span="6" v-if="targetGridElement.led[ledId].parentId==0">
                                                <el-button type="primary" icon="el-icon-collection" @click="selectLedId=ledId; 
                                                    dialogSelectSegmentVisible=true;" size="medium">Change</el-button>
                                            </el-col>
                                        </el-row>
                                        <el-form-item label="name">
                                            <el-input v-model="ledElement.name"></el-input> 
                                        </el-form-item>
                                        <el-form-item label="hwId">
                                            <el-input v-model="ledElement.hwId"></el-input>
                                        </el-form-item>
                                        
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                    <el-button type="primary" @click="submitGridDialogForm">Confirm</el-button>
                    <el-button @click="targetGridElement={button:{},led:{}};dialogFormVisible=false">Cancel</el-button>
                        
                    </el-form>
                </el-dialog>

                <!-- button删除提示 -->
                <el-dialog
                    title="Tips"
                    :visible.sync="dialogDeleteButtonVisible">
                    <span>Are you sure?</span>
                    <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="targetGridElement.button[selectButtonId].toDelete=1; dialogDeleteButtonVisible=false;">Confirm</el-button>
                        <el-button @click="dialogDeleteButtonVisible=false">Cancel</el-button>
                    </span>
                </el-dialog>

                <!-- led删除提示 -->
                <el-dialog
                    title="Tips"
                    :visible.sync="dialogDeleteLedVisible">
                    <span>Are you sure?</span>
                    <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="targetGridElement.led[selectLedId].toDelete=1; dialogDeleteLedVisible=false;">Confirm</el-button>
                        <el-button @click="dialogDeleteLedVisible=false">Cancel</el-button>
                    </span>
                </el-dialog>

                <!-- 下拉菜单选择led所在segment分组 -->
                <el-dialog title="Select Segment" :visible.sync="dialogSelectSegmentVisible">
                    <el-select v-model="selectSegmentId" placeholder="Please Select">
                        <el-option value=0 label="none">none</el-option>
                        <el-option
                        v-for="(seg, segId) in segment"
                        :key="segId"
                        :label="seg.name"
                        :value="segId">
                        </el-option>
                    </el-select>
                    <el-button type="primary" @click="targetGridElement.led[selectLedId].segmentId=selectSegmentId;dialogSelectSegmentVisible=false;">Confirm</el-button>
                    <el-button @click="dialogSelectSegmentVisible=false">Cancel</el-button>
                </el-dialog>

                <!-- 下拉菜单选择button所在group分组 -->
                <el-dialog title="Select Mapping" :visible.sync="dialogSelectLedGroupVisible">
                    <el-select v-model="selectGroupId" placeholder="Please Select">
                        <el-option value=0 label="none">none</el-option>
                        <el-option
                        v-for="(group, groupId) in group"
                        :key="groupId"
                        :label="group.name"
                        :value="groupId">
                        </el-option>
                    </el-select>
                    <el-button type="primary" @click="targetGridElement.led[selectLedId].parentId=selectGroupId;
                        targetGridElement.led[selectLedId].toAlterGroup=1; dialogSelectLedGroupVisible=false;">Confirm</el-button>
                    <el-button @click="dialogSelectLedGroupVisible=false">Cancel</el-button>
                </el-dialog>

                <!-- 下拉菜单选择led所在group分组 -->
                <el-dialog title="Select Mapping" :visible.sync="dialogSelectButtonGroupVisible">
                    <el-select v-model="selectGroupId" placeholder="Please Select">
                        <el-option value=0 label="none">none</el-option>
                        <el-option
                        v-for="(group, groupId) in group"
                        :key="groupId"
                        :label="group.name"
                        :value="groupId">
                        </el-option>
                    </el-select>
                    <el-button type="primary" @click="targetGridElement.button[selectButtonId].parentId=selectGroupId;
                        targetGridElement.button[selectButtonId].toAlterGroup=1; dialogSelectButtonGroupVisible=false;">Confirm</el-button>
                    <el-button @click="dialogSelectButtonGroupVisible=false">Cancel</el-button>
                </el-dialog>
                
                <!-- 保存按钮弹出窗口 -->
                <el-dialog title="Please input the design name" :visible.sync="dialogSaveVisible">
                    <el-form :inline="true">
                        <el-form-item label="name">
                            <el-input v-model="templateName"></el-input>
                        </el-form-item>
                        <el-button type="primary" @click="submitTotalForm">Confirm</el-button>
                        <el-button @click="dialogSaveVisible=false">Cancel</el-button>
                    </el-form>
                </el-dialog>

                <el-dialog title="Please input export path" :visible.sync="dialogExportVisible">
                    <el-form>
                        <el-form-item label="System path">
                            <el-input v-model="exportRelativePath" :disabled="exportFixPath!=0"></el-input>
                            <el-radio-group v-model="exportFixPath">
                                <el-radio :label="3">Documents</el-radio>
                                <el-radio :label="2">Downloads</el-radio>
                                <el-radio :label="1">Desktop</el-radio>
                                <el-radio :label="0">Other</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="File Name">
                            <el-input v-model="exportFileName"></el-input>
                        </el-form-item>
                        <el-button type="primary" @click="dialogExportVisible=false; testAxios();">Confirm</el-button>
                        <el-button @click="dialogExportVisible=false">Cancel</el-button>
                    </el-form>
                </el-dialog>

                <!-- Segment管理弹窗 -->
                <el-dialog title="Segment List" :visible.sync="dialogSegmentManagementVisible">
                    <el-table
                    :data="segmentList"
                    @cell-dblclick="alterSegmentInfo"
                    stripe
                    style="width: 100%">
                        <el-table-column
                            prop="id"
                            label="id">
                        </el-table-column>
                        <el-table-column
                            prop="name"
                            label="name">
                        </el-table-column>
                        <el-table-column label="Operations">
                            <template slot-scope="scope">
                                <el-button size="mini" @click="manageSegmentId=scope.row.id;segmentChildEdit();">Edit</el-button>
                                <el-button size="mini" type="danger" @click="deleteSegmentComponent=false; dialogDeleteSegmentVisible=true; manageSegmentId=scope.row.id;">Delete</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                   
                        <el-button type="primary" icon="el-icon-circle-plus-outline" @click="newSegmentName=''; dialogSegmentNameVisible=true">Add Segment</el-button>
                    <el-button type="text" @click="dialogSegmentManagementVisible=false">Cancel</el-button>
                </el-dialog>

                <!-- 新建Segment -->
                <el-dialog title="Add Segment" :visible.sync="dialogSegmentNameVisible">
                    <el-input v-model="newSegmentName" placeholder="Please input segment name"></el-input>
                    <div>
                        <el-button type="primary" size="mini" @click="putSegment(segmentCount, newSegmentName, '','',1, defaultSegmentId, 0);
                            dialogSegmentNameVisible=false; segmentList.push({id: segmentCount, name: newSegmentName}); segmentCount++;">Confirm</el-button>
                        <el-button size="mini" type="text" @click="dialogSegmentNameVisible=false;">Cancel</el-button>
                    </div>
                </el-dialog>

                <!-- 重命名Segment -->
                <el-dialog title="Basic Segment Attribute" :visible.sync="dialogSegmentRenameVisible">
                    <el-input v-model="segmentRename" placeholder="Please input new segment name"></el-input>
                    <div>
                        <el-button type="primary" size="mini" @click="submitAlterSegmentInfo">Confirm</el-button>
                        <el-button size="mini" type="text" @click="dialogSegmentRenameVisible=false;">Cancel</el-button>
                    </div>
                </el-dialog>

                <!-- Segment删除提示 -->
                <el-dialog
                    title="Delete Segment"
                    :visible.sync="dialogDeleteSegmentVisible">
                    <span>Do you confirm to delete this segment(id={{manageSegmentId}})?</span><br/><br/>
                    <el-checkbox v-model="deleteSegmentComponent">Delete all the included components also</el-checkbox>
                    <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="submitSegmentDelete">Confirm</el-button>
                        <el-button @click="dialogDeleteSegmentVisible=false">Cancel</el-button>
                    </span>
                </el-dialog>

                <!-- 管理Segment子组件的穿梭框 -->
                <el-dialog title="Edit Segment Component" :visible.sync="dialogSegmentChildVisible">
                    <el-transfer :data="availableSegmentChild" v-model="segmentChildMember" :titles="['Available', 'Segment']"></el-transfer>
                    <el-button type="primary" @click="submitSegmentChildEdit(); dialogSegmentChildVisible=false">Confirm</el-button>
                    <el-button type="text" @click="dialogSegmentChildVisible=false">Cancel</el-button>
                </el-dialog>

                <!-- 管理group信息表格 -->
                <el-dialog title="Mapping List" :visible.sync="dialogGroupVisible">
                    <el-table :data="groupList" @cell-dblclick="alterGroupInfo" style="width: 100%">
                        <el-table-column property="id" label="id"></el-table-column>
                        <el-table-column property="name" label="name"></el-table-column>
                        <el-table-column property="isVariable" label="isVariable"></el-table-column>
                        <el-table-column label="Operation" fixed="right">
                            <template slot-scope="scope">
                                <el-button size="small" @click="groupId=scope.row.id; groupComponentEdit();">Edit</el-button>
                                <el-button size="small" @click="groupId=scope.row.id; groupMappingEdit();">Attribute</el-button>
                                <el-button size="small" type="danger" @click="groupId = scope.row.id; dialogDeleteGroupVisible = true;">Delete</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-button size="mini" type="primary" icon="el-icon-circle-plus-outline" @click="dialogAddGroupVisible = true">Add Mapping</el-button>
                    <el-button size="mini" type="text" @click="dialogGroupVisible=false">Cancel</el-button>
                </el-dialog>

                <!-- 新增group信息 -->
                <el-dialog title="Add mapping" :visible.sync="dialogAddGroupVisible">
                    <el-form>
                        <el-form-item label="name">
                            <el-input v-model="newGroupName" placeholder="Please input mapping name"></el-input>
                        </el-form-item>
                        <el-button type="primary" @click="putGroup(groupCount, newGroupName, 0, 1); dialogAddGroupVisible=false;
                            groupList.push({id:groupCount, name:newGroupName, isVariable:0}); groupCount++">Confirm</el-button>
                        <el-button @click="dialogAddGroupVisible=false">Cancel</el-button>
                    </el-form>
                </el-dialog>

                <!-- 更改group基本信息 -->
                <el-dialog title="Basic Mapping Attribute" :visible.sync="dialogGroupInfoVisible">
                    <el-form>
                        <el-form-item label="name">
                            <el-input v-model="groupInfo.name">name</el-input>
                            <el-checkbox v-model="groupInfo.isVariable">isVariable</el-checkbox>
                        </el-form-item>
                        <el-button type="primary" @click="submitAlterGroupInfo">Confirm</el-button>
                        <el-button @click="dialogGroupInfoVisible=false">Cancel</el-button>
                    </el-form>
                </el-dialog>

                <!-- group双向穿梭框 -->
                <el-dialog title="Edit Mapping Component" :visible.sync="dialogEditGroupVisible">
                    <el-form>
                        <el-transfer v-model="groupContent" :data="availableGroupElement" :titles="['Available', 'Mapping']"></el-transfer>
                        <el-button type="primary" @click="submitGroupComponentEdit()">Confirm</el-button>
                        <el-button @click="dialogEditGroupVisible=false">Cancel</el-button>
                    </el-form>
                </el-dialog>

                <!--设置group mapping中各个组件的attribute-->
                <el-dialog title="Mapping Information" :visible.sync="dialogGroupAttributeVisible">
                    <el-form :inline="true">
                        <div v-for="(elementList, elementName, index) in groupAttribute" :key="index">
                            <div v-if="elementName=='button'">
                                <el-divider><h4>BUTTON</h4></el-divider>
                                <div v-for="(buttonAttribute, buttonId, index) in elementList" :key="index">
                                    <p>id: {{buttonId}}</p>
                                    <p>name: {{button[buttonId].name}}</p>
                                    <el-form-item label="buttonMode">
                                        <el-input v-model="elementList[buttonId]"></el-input>
                                    </el-form-item>
                                </div>
                            </div>

                            <div v-if="elementName=='led'">
                                <el-divider><h4>Led</h4></el-divider>
                                <div v-for="(ledAttribute, ledId, index) in elementList" :key="index">
                                    <p>id: {{ledId}}</p>
                                    <p>name: {{led[ledId].name}}</p>
                                    <!-- 遍历mapping数组 -->
                                    <div v-for="(ledMappingInfo, indexItem) in ledAttribute" :key="indexItem">
                                        <el-form-item label="mode">
                                            <el-input v-model="ledMappingInfo.mode"></el-input>
                                        </el-form-item>
                                        <el-form-item label="mappingName">
                                            <el-input v-model="ledMappingInfo.mappingName"></el-input>
                                        </el-form-item>
                                        <el-button type="primary" icon="el-icon-delete" size="small"
                                            @click="alterGroupComponentMapping(ledId, indexItem, 'led', 'delete')">Delete</el-button>
                                        <el-button icon="el-icon-plus" v-if="indexItem==ledAttribute.length-1" size="small"
                                            @click="alterGroupComponentMapping(ledId, indexItem, 'led', 'add')">Add</el-button>
                                    </div>
                                    <el-button icon="el-icon-plus" v-if="ledAttribute.length == 0" size="small"
                                            @click="alterGroupComponentMapping(ledId, 0, 'led', 'add')">Delete</el-button>
                                </div>
                                
                            </div>

                            <div v-if="elementName=='segment'">
                                <el-divider><h4>SEGMENT</h4></el-divider>
                                <div v-for="(segmentAttribute, segmentId, index) in elementList" :key="index">
                                    <p>id: {{segmentId}}</p>
                                    <p>name: {{segment[segmentId].name}}</p>
                                    <!-- 遍历mapping数组 -->
                                    <div v-for="(segmentMappingInfo, indexItem) in segmentAttribute" :key="indexItem">
                                        <el-form-item label="mode">
                                            <el-input v-model="segmentMappingInfo.mode"></el-input>
                                        </el-form-item>
                                        <el-form-item label="mappingName">
                                            <el-input v-model="segmentMappingInfo.mappingName"></el-input>
                                        </el-form-item>
                                        <el-button type="primary" icon="el-icon-delete" size="small"
                                            @click="alterGroupComponentMapping(segmentId, indexItem, 'segment', 'delete')">Delete</el-button>
                                        <el-button icon="el-icon-plus" v-if="indexItem==segmentAttribute.length-1" size="small"
                                            @click="alterGroupComponentMapping(segmentId, indexItem, 'segment', 'add')">Add</el-button>
                                    </div>
                                    <el-button icon="el-icon-plus" v-if="segmentAttribute.length == 0" size="small"
                                            @click="alterGroupComponentMapping(segmentId, 0, 'segment', 'add')">Add</el-button>
                                </div>
                            </div>
                            

                        </div>
                        <el-button type="primary" @click="submitGroupMappingEdit">Confirm</el-button>
                        <el-button @click="groupAttribute={button:{},led:{},segment:{}};dialogGroupAttributeVisible=false">Cancel</el-button>
                    </el-form>
                </el-dialog>

                <!-- group删除提示 -->
                <el-dialog
                    title="Tips"
                    :visible.sync="dialogDeleteGroupVisible">
                    <span>Are you sure?</span>
                    <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="submitGroupDelete">Confirm</el-button>
                        <el-button @click="dialogDeleteGroupVisible=false">Cancel</el-button>
                    </span>
                </el-dialog>

                <!-- 管理variable信息表格 -->
                <el-dialog title="Variable Mapping" :visible.sync="dialogVariableVisible">
                    <el-table :data="variableList" @cell-dblclick="alterVariableInfo" style="width: 100%">
                        <el-table-column property="id" label="id"></el-table-column>
                        <el-table-column property="name" label="name"></el-table-column>
                        <el-table-column label="Operation" fixed="right">
                            <template slot-scope="scope">
                                <el-button size="small" @click="variableId=scope.row.id; variableIncludeEdit();">Edit</el-button>
                                <el-button size="small" type="danger" @click="variableId = scope.row.id; dialogDeleteVariableVisible = true;">Delete</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-button size="mini" type="primary" icon="el-icon-circle-plus-outline" @click="dialogAddVariableVisible = true">Add Mapping</el-button>
                    <el-button size="mini" type="text" @click="dialogVariableVisible=false">Cancel</el-button>
                </el-dialog>

                <el-dialog title="Add Variable" :visible.sync="dialogAddVariableVisible">
                    <el-form>
                        <el-form-item label="name">
                            <el-input v-model="newVariableName"></el-input>
                        </el-form-item>
                        <el-button type="primary" @click="putVariable(variableCount, newVariableName, 0, 1); dialogAddVariableVisible=false;
                            variableList.push({id:variableCount, name:newVariableName}); variableCount++">Confirm</el-button>
                        <el-button @click="dialogAddVariableVisible=false">Cancel</el-button>
                    </el-form>
                </el-dialog>

                <el-dialog title="Basic Variable Attribute" :visible.sync="dialogVariableRenameVisible">
                    <el-input v-model="variableRename" placeholder="Please input new variable name"></el-input>
                    <div>
                        <el-button type="primary" size="mini" @click="submitAlterVariableInfo">Confirm</el-button>
                        <el-button size="mini" type="text" @click="dialogVariableRenameVisible=false;">Cancel</el-button>
                    </div>
                </el-dialog>

                <!-- variable双向穿梭框 -->
                <el-dialog title="Edit Variable Member" :visible.sync="dialogEditVariableVisible">
                    <el-form>
                        <el-transfer v-model="variableContent" :data="availableVariableInclude" :titles="['Available', 'Mapping']"></el-transfer>
                        <el-button type="primary" @click="submitVariableIncludeEdit">Confirm</el-button>
                        <el-button @click="dialogEditVariableVisible=false">Cancel</el-button>
                    </el-form>
                </el-dialog>

                <!-- variable删除提示 -->
                <el-dialog
                    title="Tips"
                    :visible.sync="dialogDeleteVariableVisible">
                    <span>Are you sure?</span>
                    <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="submitVariableDelete">Confirm</el-button>
                        <el-button @click="dialogDeleteVariableVisible=false">Cancel</el-button>
                    </span>
                </el-dialog>
            </div>
        </el-col>
    </div>
</template>

<script>
    import axios from 'axios';

    var db = openDatabase('BSHdb', '1.0', 'Test DB', 2 * 1024 * 1024);
    
    export default {
        name: 'templateDemo2',
        data() {
            return {
                shit: '',
                buttonCount: 1,
                ledCount: 1,
                segmentCount: 1,
                groupCount: 1,
                variableCount: 1,
                loading: false,
                exportFixPath: 1,
                exportRelativePath:'',
                exportFileName: '',
                templateName:'',
                boardType: "1",
                //templateId：默认为0，保存后根据保存的id改变
                templateId:this.$route.params.templateId,
                //未被设置Segment分组的led的默认segmentId
                defaultSegmentId: 0,
                dragElementType: 1,
                dialogExportVisible:false,
                //保存弹窗默认关闭
                dialogSaveVisible:false,
                // 确认删除弹窗
                dialogDeleteButtonVisible: false,
                dialogDeleteLedVisible: false,
                dialogDeleteSegmentVisible: false,
                // 表单弹窗默认关闭
                dialogFormVisible: false,
                dialogEditGroupVisible:false,
                dialogAddGroupVisible:false,
                dialogDeleteGroupVisible:false,
                dialogGroupInfoVisible: false,
                dialogGroupAttributeVisible: false,
                //编辑group弹窗，默认关闭
                dialogGroupVisible:false,
                
                dialogVariableVisible: false,
                dialogAddVariableVisible: false,
                dialogVariableRenameVisible: false,
                dialogDeleteVariableVisible: false,
                dialogEditVariableVisible: false,

                // 选择segment组别弹窗
                dialogSelectSegmentVisible: false,
                dialogSelectButtonGroupVisible: false,
                dialogSelectLedGroupVisible: false,
                // 管理segment弹窗
                dialogSegmentManagementVisible: false,
                dialogSegmentNameVisible: false,
                dialogSegmentRenameVisible: false,
                dialogSegmentChildVisible: false,
                totalWidth: 800,
                // 每行网格个数
                rowGridNum: 35,
                // 每个网格的边长，mounted时将被重新计算
                gridWidth: 30,
                // 网格绑定的样式，在mounted中初始化
                gridStyle: [],
                /*
                    描述： gridIndex指网格的index; button列表存放所有button组件的id值; led列表同理。
                    构造：只要一个组件的ui图标填充了一个网格，那么就将该组件的id添加至该网格index所对应的组件列表中。
                    举例：组件Button的id为1，ui图标大小为2*2，覆盖了index分别为1, 2, 36, 37的网格，
                        那么程序便会在gridElementOverlap中这四个index下的button列表中分别添加Button的id值1
                    用法：用户点击一个网格，直接对gridElementOverlap索引网格index找到该网格上overlap的所有组件，修改对应参数值
                    注：gridIndex=0代表第1个格子
                    gridElementOverlap {
                        gridIndex1: {
                            button: [
                                buttonId1, buttonId2, ...
                            ],
                            led: [
                                ledId1, ledId2, ...
                            ]
                        }
                        ...
                    }
                */
                gridElementOverlap: {},

                radioClass:{
                    // display:'inline',
                    textAlign: 'left',
                    marginRight: '2%',
                    marginBottom: '1%',
                    marginTop: '1%',
                },

                // 用户点击的网格所对应的index值。生成表单时需要通过这个值找到某一网格下的所有组件
                gridDialogIndex: 0,

                /*
                    经过尝试，发现把button以及led声明为对象形式，比列表形式更方便（主要是在表单读取网格组件信息时方便在此处索引组件id获取数据）
                    button: {
                        buttonId: {
                            name: 
                            hwId: 
                            parentId:
                            mappingName:
                            mode:
                        }
                    }
                    led: {
                        ledId: {
                            name: 
                            hwId: 
                            segmentId: 
                            parentId: 
                            mappingInfo: []
                        }
                    }
                    segment {
                        segmentId1: {
                            name: 
                            parentId: 
                            groupId: 
                            mappingInfo: []
                            ledMember: [ledId1, ledId2, ...]
                            segmentMember: [segmentId1, segmentId2]
                            isTemp:
                        }
                    }
                    group {
                        groupId1: {
                            name:
                            isVariable:
                            buttonMember: []
                            ledMember: []
                            segmentMember: []
                            isTemp:
                        }
                    }
                    variable {
                        variableId1: {
                            name:
                            groupMember: []
                        }
                    }
                */
                button: {},
                led: {},
                segment: {},
                group:{},
                variable: {},

                //存储group信息的数组
                groupList: [],
                /*
                groupAttribute: {
                    buttonId: {
                            mappingName: '',
                            mode: ''
                        }
                    }
                    ledId: {
                            mappingName: '',
                            mode: ''
                        }
                    }
                    segmentId: {
                            mappingName: '',
                            mode: ''
                        }
                    }
                },
                */
                // 与设置mapping信息的表单做双向绑定
                groupAttribute: {
                    button: {},
                    led: {},
                    segment: {}
                },
                groupContent: [],
                availableGroupElement: [],
                newGroupName: '',
                // 绑定设置group基本信息的表单
                groupInfo: {
                    id: '',
                    name: '',
                    isVariable: ''
                },
                groupId: 0,

                variableList: [],
                newVariableName: '',
                variableRenameId: '',
                variableRename: '',
                variableId: '',
                // 绑定穿梭框
                availableVariableInclude: [],
                variableContent: [],

                // 用于绑定Segment管理中的表格数据
                segmentList: [], 
                // 在表单中对某一Button进行单独操作时更新该值
                selectButtonId: '',
                // 在表单中对某一led进行单独操作时更新该值
                selectLedId: '',
                // 和segment的下拉菜单选择项做双向绑定
                selectSegmentId: '',
                selectGroupId: '',
                // 绑定输入的新增segment的名称
                newSegmentName: '',
                segmentRename: '',
                segmentRenameId: '',
                // 在SegmentManagement弹窗中操作的待编辑/删除Segment的id值
                manageSegmentId: '',
                // 删除segment时同时删除所有子组件（迭代删除）
                deleteSegmentComponent: false,
                // 所有可选的子组件选项
                availableSegmentChild: [],
                segmentChildMember: [],

                /*
                    描述：与gridElementOverlap不同，这里存了特定一个网格中的所有组件的信息
                    用法：与表单数据做双向绑定
                    targetGridElement {
                        button: {
                            buttonId1: {
                                name:
                                hwId:
                                parentId:
                            },
                            ...
                        }
                        led: {
                            ledId1: {
                                name: 
                                hwId: 
                                segmentId:
                                parentId:
                            }
                            ...
                        }
                    }
                */
                targetGridElement: {
                    button: {},
                    led: {}
                },

                // 分别定义：背景颜色，单button颜色，单led颜色，组件叠加颜色
                backgroundColor: '#FFFFFF',
                buttonColor: "#8CFE90",
                ledColor: "#00EFEF",
                mixColor: "#000080",

                // 微调放大/缩小图标的布局
                iconDivClass: {
                    textAlign: 'right',
                    marginRight: '2%',
                    marginBottom: '1%',
                    marginTop: '1%'
                },
                // 微调网格的布局
                // offsetWidth会随此处的width改变
                workspaceDivClass: {
                    width: '96%',
                    height: '730px',
                }

            }
        }, 
        mounted: function() {
            this.loadBoard()
        },
        methods: {
            testAxios() {
                // 向后端传递组件参数
                
                let psButtonList = []
                let osButtonList = []
                let psLedList = []
                let osLedList = []
                let psSegmentList = []
                let osSegmentList = []
                let groupList = []
                 let variableList = []
                this.loading = true
                if (this.exportFixPath == 3) {
                    this.exportRelativePath = 'document'
                } else if (this.exportFixPath == 2) {
                    this.exportRelativePath = 'download'
                } else if(this.exportFixPath == 1) {
                    this.exportRelativePath = 'desktop'
                } else {
                    1 + 1
                }
                
                let _console = console
                let _this = this
                db.transaction(function (context) {        
                    
                    context.executeSql('SELECT name, hwId, boardType FROM Button WHERE templateId = ?', 
                        [_this.templateId], function (context, results) {
                            for (let i = 0 ; i < results.rows.length ; ++i) {
                                if (results.rows[i].boardType == 1) {
                                    psButtonList.push({
                                        name: results.rows[i].name,
                                        hwId: results.rows[i].hwId
                                    })
                                } else {
                                    osButtonList.push({
                                        name: results.rows[i].name,
                                        hwId: results.rows[i].hwId
                                    })
                                }
                            }
                            axios ({
                                method: 'post',
                                url: '/save/touchButton',
                                data: {
                                    psButton: psButtonList,
                                    osButton: osButtonList
                                }
                                }).then(function (response) {
                                    console.log(response.data)
                            });
                            axios ({
                                method: 'get',
                                url: '/save/exportFile',
                                params: {
                                    relativePath: _this.exportRelativePath,
                                    fileName: _this.exportFileName
                                }
                                }).then(function (response) {
                                    if (response.data == "SAVE SUCCESSFULLY") {
                                        _this.loading = false
                                        _this.$message({
                                            message: 'Success',
                                            type: 'success',
                                            duration: 1500
                                        });
                                    }
                            });

                    });

                    context.executeSql('SELECT name, hwId, boardType FROM Led WHERE templateId = ?', 
                        [_this.templateId], function (context, results) {
                            for (let i = 0 ; i < results.rows.length ; ++i) {
                                if (results.rows[i].boardType == 1) {
                                    psLedList.push({
                                        name: results.rows[i].name,
                                        hwId: results.rows[i].hwId
                                    })
                                } else {
                                    osLedList.push({
                                        name: results.rows[i].name,
                                        hwId: results.rows[i].hwId
                                    })
                                }
                            }
                            axios ({
                                method: 'post',
                                url: '/save/physicalLed',
                                data: {
                                    psLed: psLedList,
                                    osLed: osLedList
                                }
                                }).then(function (response) {
                                    console.log(response.data)
                            });
                            axios ({
                                method: 'get',
                                url: '/save/exportFile',
                                params: {
                                    relativePath: _this.exportRelativePath,
                                    fileName: _this.exportFileName
                                }
                                }).then(function (response) {
                                    if (response.data == "SAVE SUCCESSFULLY") {
                                        _this.loading = false
                                        _this.$message({
                                            message: 'Success',
                                            type: 'success',
                                            duration: 1500
                                        });
                                    }
                            });
                    });

                    // 提取PS以及OS板的segment所包含的所有子组件，且按segmentId排序
                    context.executeSql('SELECT * FROM (SELECT Led.name AS memberName, Segment.id AS segParentId, Segment.name as segParentName, Segment.parentId AS upperId, \
                    Led.boardType AS boardType, \'led\' AS elType FROM Led INNER JOIN Segment ON Led.segmentId = Segment.id WHERE Led.templateId = ? UNION SELECT SegChild.name AS memberName,\
                     SegParent.id AS segParentId, SegParent.name AS segParentName, SegParent.parentId AS upperId, SegChild.boardType AS boardType, \'segment\' AS elType \
                     FROM Segment AS SegChild INNER JOIN Segment AS SegParent ON SegChild.parentId = SegParent.id WHERE SegChild.templateId = ?)\
                      ORDER BY segParentId DESC', [_this.templateId, _this.templateId], function(context, results) {

                            let lastSegmentId = ''
                            for (let i = 0 ; i < results.rows.length ; ++i) {
                                // 当一条数据的segmentId改变时，新增一个psSegmentList项
                                // 由于results.rows所获取的数据已按segmentId排序，所以可以保证新增psSegmentList项时不重复
                                if (results.rows[i].boardType == 1) {
                                    if (results.rows[i].segParentId != lastSegmentId) {
                                        psSegmentList.push({
                                            name: results.rows[i].segParentName,
                                            ledMember: [],
                                            segmentMember: [],
                                            upperId: results.rows[i].upperId
                                        })
                                       
                                        lastSegmentId = results.rows[i].segParentId
                                    }
                                    if (results.rows[i].elType == 'led') {
                                        psSegmentList[psSegmentList.length-1].ledMember.push(results.rows[i].memberName)
                                    } else {
                                        psSegmentList[psSegmentList.length-1].segmentMember.push(results.rows[i].memberName)
                                    }
                                } else {
                                    if (results.rows[i].segParentId != lastSegmentId) {
                                        osSegmentList.push({
                                            name: results.rows[i].segParentName,
                                            ledMember: [],
                                            segmentMember: [],
                                            upperId: results.rows[i].upperId
                                        })
                                       
                                        lastSegmentId = results.rows[i].segParentId
                                    }
                                    if (results.rows[i].elType == 'led') {
                                        osSegmentList[osSegmentList.length-1].ledMember.push(results.rows[i].memberName)
                                    } else {
                                        osSegmentList[osSegmentList.length-1].segmentMember.push(results.rows[i].memberName)
                                    }
                                }
                            }
                            axios ({
                                method: 'post',
                                url: '/save/virtualSegment',
                                data: {
                                    psSegment: psSegmentList,
                                    osSegment: osSegmentList
                                }
                                }).then(function (response) {
                                    console.log(response.data)
                            });
                            axios ({
                                method: 'get',
                                url: '/save/exportFile',
                                params: {
                                    relativePath: _this.exportRelativePath,
                                    fileName: _this.exportFileName
                                }
                                }).then(function (response) {
                                    if (response.data == "SAVE SUCCESSFULLY") {
                                        _this.loading = false
                                        _this.$message({
                                            message: 'Success',
                                            type: 'success',
                                            duration: 1500
                                        });
                                    }
                            });
                    })

                    // 查询一个Group表关联的所有Button, Led和Segment, 以及这些组件的mapping信息
                    context.executeSql(`SELECT * FROM (
                        SELECT Button.id AS elId, Button.name AS name, '' AS mappingName, Button.mode AS mode, 'button' AS elType, Groups.id AS groupId, Button.templateId AS templateId,
                        Groups.name AS groupName, Groups.boardType as boardType, Groups.isVariable AS isVariable FROM Button INNER JOIN Groups ON Button.parentId = Groups.id UNION 
                        SELECT Led.id AS elId, Led.name AS name, LedMapping.mappingName AS mappingName, LedMapping.mode AS mode, 'led' AS elType, Groups.id AS groupId, LED.templateId AS templateId, Groups.name AS groupName, 
                        Groups.boardType as boardType, Groups.isVariable AS isVariable FROM Led INNER JOIN Groups ON Led.parentId = Groups.id LEFT JOIN LedMapping ON LED.id = ledMapping.id UNION 
                        SELECT Segment.id AS elId, Segment.name AS name, SegmentMapping.mappingName AS mappingName, SegmentMapping.mode AS mode, 'segment' AS elType, Groups.id AS groupId, Segment.templateId AS templateId,
                        Groups.name AS groupName, Groups.boardType as boardType, Groups.isVariable AS isVariable FROM Segment INNER JOIN Groups ON Segment.groupId = Groups.id LEFT JOIN SegmentMapping ON Segment.id = SegmentMapping.id
                        ) WHERE templateId=? AND isVariable=0 ORDER BY groupId ASC, elId ASC`,
                            [_this.templateId], function(context, results) {
                                let lastGroupId = ''
                                let lastelId = ''
                                let lastelType = ''
                                for (let i = 0 ; i < results.rows.length ; ++i) {
                                    if (lastGroupId != results.rows[i].groupId) {
                                        groupList.push( {
                                            name: results.rows[i].groupName,
                                            buttonMember: [],
                                            ledMember: [],
                                            segmentMember: [],
                                        })
                                        lastGroupId = results.rows[i].groupId
                                    }
                                    switch (results.rows[i].elType) {
                                        case 'button':
                                            groupList[groupList.length-1].buttonMember.push({
                                                name: results.rows[i].name,
                                                mode: results.rows[i].mode
                                            })
                                            lastelType = 'button'
                                            break;
                                        case 'led':
                                            if (lastelId != results.rows[i].elId || lastelType != 'led') {
                                                // 添加一个新的Led
                                                groupList[groupList.length-1].ledMember.push({
                                                    name: results.rows[i].name,
                                                    mappingInfo: [
                                                        {
                                                            mappingName: results.rows[i].mappingName,
                                                            mode: results.rows[i].mode
                                                        }
                                                    ]
                                                })
                                                lastelId = results.rows[i].elId
                                                lastelType = 'led'
                                            } else {
                                                // 重复添加同一个led的mappingInfo
                                                let ledMember = groupList[groupList.length-1].ledMember
                                                ledMember[ledMember.length - 1].mappingInfo.push(
                                                    {
                                                        mappingName: results.rows[i].mappingName,
                                                        mode: results.rows[i].mode
                                                    }
                                                )
                                            }
                                            break;
                                        case 'segment':
                                            // 添加一个新的segment
                                            if (lastelId != results.rows[i].elId || lastelType != 'segment') {
                                                groupList[groupList.length-1].segmentMember.push({
                                                    name: results.rows[i].name,
                                                    mappingInfo: [
                                                        {
                                                            mappingName: results.rows[i].mappingName,
                                                            mode: results.rows[i].mode
                                                        }
                                                    ]
                                                })
                                                lastelId = results.rows[i].elId
                                                lastelType = 'segment'
                                            } else {
                                                // 重复添加同一个segment的mappingInfo
                                                let segmentMember = groupList[groupList.length-1].segmentMember
                                                segmentMember[segmentMember.length - 1].mappingInfo.push(
                                                    {
                                                        mappingName: results.rows[i].mappingName,
                                                        mode: results.rows[i].mode
                                                    }
                                                )
                                            }
                                            break;
                                        default:
                                            break;
                                    }   
                                }
                               
                                axios ({
                                    method: 'post',
                                    url: '/save/functionMapping',
                                    data: {
                                        group: groupList
                                    }
                                    }).then(function (response) {
                                        console.log(response.data)
                                });
                                axios ({
                                    method: 'get',
                                    url: '/save/exportFile',
                                    params: {
                                        relativePath: _this.exportRelativePath,
                                        fileName: _this.exportFileName
                                    }
                                    }).then(function (response) {
                                        if (response.data == "SAVE SUCCESSFULLY") {
                                            _this.loading = false
                                            _this.$message({
                                                message: 'Success',
                                                type: 'success',
                                                duration: 1500
                                            });
                                        }
                                });
                                
                    })


                    context.executeSql(`SELECT * FROM (
                        SELECT Led.id AS elId, Led.name AS elName, LedMapping.mappingName AS mappingName, LedMapping.mode AS mode, 'led' AS elType,
                        Led.templateId AS templateId, Groups.id AS groupId, Groups.name AS groupName, Variable.id AS variableId, Variable.name AS variableName
                        FROM Led LEFT JOIN LedMapping ON Led.id = LedMapping.id INNER JOIN Groups ON Led.parentId = Groups.id 
                        INNER JOIN VariableMapping ON Groups.id = VariableMapping.groupId INNER JOIN Variable ON VariableMapping.variableId = Variable.id UNION
                        SELECT Segment.id AS elId, Segment.name AS elName, SegmentMapping.mappingName AS mappingName, SegmentMapping.mode AS mode, 'segment' AS elType, 
                        Segment.templateId AS templateId, Groups.id AS groupId, Groups.name AS groupName, Variable.id AS variableId, Variable.name AS variableName
                        FROM Segment LEFT JOIN SegmentMapping ON Segment.id = SegmentMapping.id INNER JOIN Groups ON Segment.groupId = Groups.id
                        INNER JOIN VariableMapping ON Groups.id = VariableMapping.groupId INNER JOIN Variable ON VariableMapping.variableId = Variable.id) 
                        WHERE templateId = ? ORDER BY variableId ASC, groupId ASC, elId ASC`, 
                        [_this.templateId], function (context, results) {
                            // mapping/mode -- element -- group -- variable
                        let lastelId = ''
                        let lastelType = ''
                        let lastGroupId = ''
                        let lastVarId = ''
                        
                        let curVariable = {}
                        let curGroup = {}
                        let curElement = {}
                        /*
                            [
                                {
                                    name:
                                    groupMember:
                                    [
                                        {
                                            name: 
                                            ledMember: 
                                            [
                                                {
                                                    name:
                                                    mappingInfo:
                                                    [
                                                        {
                                                            mappingName:
                                                            mode:
                                                        }
                                                    ]
                                                }
                                            ]
                                            segmentMember: 
                                            [
                                                {
                                                    name:
                                                    mappingInfo:
                                                    [
                                                        {
                                                            mappingName:
                                                            mode:
                                                        }
                                                    ]
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        */
                       _console.log(results.rows)
                        for (let i = 0 ; i < results.rows.length ; ++i) {
                            // 遍历到一个新的element
                            if (lastelId != results.rows[i].elId || lastelType != results.rows[i].elType) {
                                if (lastelId != '') {
                                    switch (lastelType) {
                                        case 'led':
                                            curGroup.ledMember.push(curElement)
                                            break
                                        case 'segment':
                                            curGroup.segmentMember.push(curElement)
                                            break
                                        default:
                                            break
                                    }
                                }
                                curElement = {
                                    name: results.rows[i].elName,
                                    mappingInfo: []
                                }
                                
                                lastelId = results.rows[i].elId
                                lastelType = results.rows[i].elType
                            }

                            // 遍历到一个新的group
                            if (lastGroupId != results.rows[i].groupId) {
                                if (lastGroupId != '') {
                                    curVariable.groupMember.push(curGroup)
                                }
                                curGroup = {
                                    name: results.rows[i].groupName,
                                    ledMember: [],
                                    segmentMember: []
                                }
                                lastGroupId = results.rows[i].groupId
                            }

                            // 遍历到一个新的variable, 就将之前的variable存入varibleList中, 并更新curVariable
                            if (lastVarId != results.rows[i].variableId) {
                                if (lastVarId != '') {
                                    variableList.push(curVariable)
                                }
                                curVariable = {
                                    name: results.rows[i].variableName,
                                    groupMember: []
                                }
                                lastVarId = results.rows[i].variableId
                            }

                            // 给当前element添加一个mappingInfo
                            curElement.mappingInfo.push({
                                mappingName: results.rows[i].mappingName,
                                mode: results.rows[i].mode
                            })

                            
                            // 已到了最后一个element, 将当前信息存入variableList
                            if (i == results.rows.length - 1) {
                                switch (results.rows[i].elType) {
                                    case 'led':
                                        curGroup.ledMember.push(curElement)
                                        break
                                    case 'segment':
                                        curGroup.segmentMember.push(curElement)
                                        break
                                    default:
                                        break
                                }
                                curVariable.groupMember.push(curGroup)
                                variableList.push(curVariable)
                            }
                        }

                        _console.log(variableList)
                        axios ({
                                method: 'post',
                                url: '/save/variableMapping',
                                data: {
                                    variable: variableList
                                }
                                }).then(function (response) {
                                    console.log(response.data)
                            });
                        axios ({
                                method: 'get',
                                url: '/save/exportFile',
                                params: {
                                    relativePath: _this.exportRelativePath,
                                    fileName: _this.exportFileName
                                }
                                }).then(function (response) {
                                    if (response.data == "SAVE SUCCESSFULLY") {
                                        _this.loading = false
                                        _this.$message({
                                            message: 'Success',
                                            type: 'success',
                                            duration: 1500
                                        });
                                    }
                            });
                    });
                })
               
            },
            pageInit(){
                this.button = {};
                this.led = {};
                this.segment={}
                this.group = {}
                this.variable = {}
                this.gridElementOverlap = {};
                // 获取中间工作区的页面宽度
                this.totalWidth = document.getElementById("MainArea").offsetWidth;
                // 计算一个网格的边长, 减去0.01防止四舍五入后总宽度不够，在浏览器允许的缩放范围内这个误差够了
                // 1+this.rowGridNum用于抵消滚动条和一些玄学误差
                this.gridWidth = this.totalWidth / (1 + this.rowGridNum) - 0.01
                this.gridStyle = [];
                // 初始化所有网格的样式
                for (let i = 0 ; i < this.rowGridNum * this.rowGridNum ; ++i) {
                    document.getElementById(i).innerHTML = "";
                    this.gridStyle.push(
                        {
                            width: this.gridWidth + 'px',
                            height: this.gridWidth + 'px',
                            display: 'inline-block',
                            border: '0.2px dashed gray',
                            'box-sizing': 'border-box',
                            'font-size': '1px',
                            float: 'left',
                            background: this.backgroundColor,
                            cursor: ''
                        }
                    )
                }
                // 监听改变页面大小的事件
                let _this = this
                window.onresize = function() {
                    _this.alterGridWidth()
                }
                // 利用elemet-resize-detector, 监听放大/缩小事件
                var elementResizeDetectorMaker = require("element-resize-detector");
                let erd = elementResizeDetectorMaker()
                erd.listenTo(document.getElementById("MainArea"), function() {
                    _this.alterGridWidth()
                });
            },
            loadBoard(){
                this.pageInit()
                let that = this;
                //使用that来接受this的内容，此时可以通过that访问data中的数据
                db.transaction(function (context) { 
                        
                        // 读取Button数据
                        context.executeSql('SELECT * FROM Button WHERE templateId = ? and boardType = ?',[that.templateId,that.boardType],function(context,results){
                            let len = results.rows.length;
                            for(let i = 0;i<len;i++){
                                let index = results.rows.item(i).areaId
                                let elementId = results.rows.item(i).id
                                let name = results.rows.item(i).name
                                let hwId = results.rows.item(i).hwId;
                                let parentId = results.rows.item(i).parentId;
                                let mode = results.rows.item(i).mode
                                that.putButton(index, elementId, name, hwId, mode, 0,parentId);
                            }
                        });
                        // 读取led数据
                        context.executeSql('SELECT Led.id, Led.areaId, Led.name, Led.hwId, Led.segmentId, Led.parentId, LedMapping.mappingName, \
                            LedMapping.mode FROM Led LEFT JOIN LedMapping ON Led.id == LedMapping.id WHERE templateId = ? and boardType = ?',
                            [that.templateId,that.boardType],function(context,results){
                            let len = results.rows.length;
                            for(let i = 0;i<len;i++){
                                let index = results.rows.item(i).areaId;
                                let elementId = results.rows.item(i).id;
                                let name = results.rows.item(i).name
                                let hwId = results.rows.item(i).hwId
                                let segmentId = results.rows.item(i).segmentId
                                let parentId = results.rows.item(i).parentId;
                                let mappingName = results.rows.item(i).mappingName;
                                let mode = results.rows.item(i).mode
                                that.putLed(index, elementId, name, hwId, mappingName, mode, segmentId, 0,parentId);
                            }
                        });

                        // 读取group数据
                        context.executeSql('SELECT * FROM Groups WHERE templateId = ? and boardType = ?',[that.templateId,that.boardType],function(context,results){
                            let len = results.rows.length;
                            if(len>0)
                            {
                                for(let i = 0;i<len;i++){
                                    let groupId = results.rows.item(i).id
                                    let name = results.rows.item(i).name;
                                    let isVariable = results.rows.item(i).isVariable
                                    that.putGroup(groupId, name, isVariable,0);
                                }
                            }
                        });
                        
                        // 读取Segment数据
                        context.executeSql('SELECT Segment.id, Segment.name, Segment.parentId, Segment.groupId, SegmentMapping.mappingName, SegmentMapping.mode\
                             FROM Segment LEFT JOIN SegmentMapping ON Segment.id = SegmentMapping.id WHERE templateId = ? and boardType = ?',
                            [that.templateId,that.boardType],function(context,results){
                            let len = results.rows.length;
                            if(len>0)
                            {
                                for(let i = 0;i<len;i++){
                                    let segmentId = results.rows.item(i).id
                                    let name = results.rows.item(i).name;
                                    let parentId = results.rows.item(i).parentId;
                                    let groupId = results.rows.item(i).groupId
                                    let mappingName = results.rows.item(i).mappingName;
                                    let mode = results.rows.item(i).mode
                                    that.putSegment(segmentId, name, mappingName, mode, 0,parentId, groupId);
                                }
                            }
                        });

                        // 读取Variable数据
                        context.executeSql(`SELECT Variable.id, Variable.name, VariableMapping.groupId FROM Variable LEFT JOIN VariableMapping 
                            ON Variable.id = VariableMapping.variableId WHERE templateId = ? and boardType = ?`,[that.templateId,that.boardType],function(context,results){
                            let len = results.rows.length;
                            if(len>0)
                            {
                                for(let i = 0;i<len;i++){
                                    let variableId = results.rows.item(i).id
                                    let variableName = results.rows.item(i).name
                                    let groupId = results.rows.item(i).groupId;
                                    that.putVariable(variableId, variableName, groupId, 0);
                                }
                            }
                        });

                        context.executeSql('SELECT MAX(bt.id) AS max_result FROM Button as bt', [], function (context, results) {
                            if (results.rows.length > 0) {
                                that.buttonCount = results.rows.item(0).max_result+1;
                            }
                        });
                        context.executeSql('SELECT MAX(led.id) AS max_result FROM Led as led', [], function (context, results) {
                            if (results.rows.length > 0) {
                                that.ledCount = results.rows.item(0).max_result+1;
                            }
                        });
                        context.executeSql('SELECT MAX(seg.id) AS max_result FROM Segment as seg', [], function (context, results) {
                            if (results.rows.length > 0) {
                                that.segmentCount = results.rows.item(0).max_result+1;
                            }
                        });
                        context.executeSql('SELECT MAX(grp.id) AS max_result FROM Groups as grp', [], function (context, results) {
                            if (results.rows.length > 0) {
                                that.groupCount = results.rows.item(0).max_result+1;
                            }
                        });

                        context.executeSql('SELECT MAX(vr.id) AS max_result FROM Variable as vr', [], function (context, results) {
                            if (results.rows.length > 0) {
                                that.variableCount = results.rows.item(0).max_result+1;
                            }
                        });
                });
            },
            ondragover(event) {
                event.preventDefault()
            },
            ondrop(event, index)  {
                event.preventDefault()
                let rangeIndex
                let elementName
                switch (this.dragElementType) {
                    // 新建一个button
                    case 1:
                        elementName = (this.boardType == 1 ? 'PS_Btn_' : 'OS_Btn_') + this.buttonCount
                        this.putButton(index, this.buttonCount, elementName, '','',1,0)
                        this.buttonCount++
                        break
                    // 新建一个led
                    case 2:
                        elementName = (this.boardType == 1 ? 'PS_Led_' : 'OS_Led_') + this.ledCount
                        this.putLed(index, this.ledCount, elementName, '', '','', this.defaultSegmentId, 1,0)
                        this.ledCount++
                        break
                    // 新建一个segment并生成7个相应的led
                    case 3:
                        this.putSegment(this.segmentCount, "SegmentGroup" + this.segmentCount, '', '',1,0,0);
                        rangeIndex = [index, index-1+this.rowGridNum, index+1+this.rowGridNum, index+2*this.rowGridNum,
                            index-1+3*this.rowGridNum, index+1+3*this.rowGridNum, index+4*this.rowGridNum]
                        for (let i = 0 ; i < rangeIndex.length ; ++i) {
                            elementName = (this.boardType == 1 ? 'PS_Led_' : 'OS_Led_') + this.ledCount
                            this.putLed(rangeIndex[i], this.ledCount, elementName, '', '', '',this.segmentCount, 1,0)
                            this.ledCount++
                        }
                        this.segmentCount++
                        break
                    default:
                        console.log('other situations')
                        break
                }
                this.dragElementType = 0
            },
            gridHover(index) {
                let hoverString = ' / '
                let existGroup = []

                if (this.gridElementOverlap[index] != null) {
                    for (let i = 0 ; i < this.gridElementOverlap[index].button.length ; ++i) {
                        let buttonId = this.gridElementOverlap[index].button[i]
                        let groupId = this.button[buttonId].parentId
                        if (existGroup.indexOf(groupId) == -1 && this.group[groupId] != null) {
                            hoverString += this.group[groupId].name + ' / '
                            existGroup.push(groupId)
                        }
                    }
                    for (let i = 0 ; i < this.gridElementOverlap[index].led.length ; ++i) {
                        let ledId = this.gridElementOverlap[index].led[i]
                        let groupId = this.led[ledId].parentId
                        if (existGroup.indexOf(groupId) == -1 && this.group[groupId] != null) {
                            hoverString += this.group[groupId].name + ' / '
                            existGroup.push(groupId)
                        }

                        let segmentId = this.led[this.gridElementOverlap[index].led[i]].segmentId
                        if (this.segment[segmentId] != null) {
                            while (this.segment[segmentId].parentId != 0) {
                                segmentId = this.segment[segmentId].parentId
                            }
                            let segmentGroupId = this.segment[segmentId].groupId
                            if (existGroup.indexOf(segmentGroupId) == -1 && this.group[segmentGroupId] != null) {
                                hoverString += this.group[segmentGroupId].name + ' / '
                                existGroup.push(segmentGroupId)
                            }
                        }
                    }
                    
                }
                
                return hoverString
            },
            putButton(index, elementId, _name, _hwId, _mode, isCreate,_parentId) {
                let templateId = this.templateId
                let boardType = this.boardType
                // button覆盖的index
                let rangeIndex = [index, index+1, index+this.rowGridNum, index+this.rowGridNum+1]
                for(let i = 0 ; i < rangeIndex.length ; ++i) {
                    // 如果覆盖位置上已存在其它组件，就将背景颜色设为mixColor，否则设为buttonColor
                    let curBackground = this.gridStyle[rangeIndex[i]].background
                    if (curBackground == this.backgroundColor) {
                        this.gridStyle[rangeIndex[i]].background = this.buttonColor
                    } else {
                        this.gridStyle[rangeIndex[i]].background = this.mixColor
                    }

                    //已有元素在上面的格子变成可点击的手型 
                    this.gridStyle[rangeIndex[i]].cursor="pointer"

                    // 必须用this.$set形式赋值, 以便拖入新元素时页面做响应式更新
                    if (this.gridElementOverlap[rangeIndex[i]] == null) {
                        this.$set(this.gridElementOverlap, rangeIndex[i], {})
                        this.$set(this.gridElementOverlap[rangeIndex[i]], 'button', [])
                        this.$set(this.gridElementOverlap[rangeIndex[i]], 'led', [])
                    }
                    this.gridElementOverlap[rangeIndex[i]]['button'].push(
                        elementId
                    )
                }

                this.button[elementId] = {
                    name: _name,
                    hwId: _hwId,
                    parentId:_parentId,
                    mode: _mode
                }
                if (_parentId != 0) {
                    // 若button在group之前被加载，就初始化一个临时group
                    if (this.group[_parentId] == null) {
                        this.group[_parentId] = {
                            name: '',
                            isVariable: 0,
                            buttonMember: [],
                            ledMember: [],
                            segmentMember: [],
                            isTemp: true
                        }
                    }
                    this.group[_parentId].buttonMember.push(elementId)
                }

                if (isCreate == 1) {
                    db.transaction(function (context) {  
                        context.executeSql('INSERT INTO Button (id,name,hwId,mode,areaId,templateId,boardType,parentId)\
                            VALUES (?,?,?,?,?,?,?,?)',[elementId,_name,_hwId,_mode,index,templateId,boardType,_parentId]);
                    })
                }
            },
            putLed(index, elementId, _name, _hwId, _mappingName, _mode, _segmentId, isCreate,_parentId) {
                // 规定：若重复put同一个led，那么mappingName和mode将被添加，其余信息不做变动
                let templateId = this.templateId
                let boardType = this.boardType

                // 是仅需要添加mappingName和mode，还是需要创建一个新的led
                let isRedundant = false
                if (this.led[elementId] != null) {
                    this.led[elementId].mappingInfo.push ({
                        mappingName: _mappingName,
                        mode: _mode
                    })
                    isRedundant = true
                }
               
                this.gridStyle[index].cursor = "pointer";

                //拖动按钮松开后蓝色代表Led
                document.getElementById(index).innerHTML = "<div style = 'background-color:#409EFF;width:50%;height:50%;margin:auto'></div>";
                
                // 必须用this.$set形式赋值，否则拖入一个新led后页面可能无法及时做响应式更新
                if (this.gridElementOverlap[index] == null) {
                    this.$set(this.gridElementOverlap, index, {})
                    this.$set(this.gridElementOverlap[index], 'button', [])
                    this.$set(this.gridElementOverlap[index], 'led', [])
                }

                if (isRedundant == false) {
                    this.led[elementId] = {
                        name: _name,
                        hwId: _hwId,
                        segmentId: _segmentId,
                        parentId:_parentId,
                        mappingInfo: [{
                            mappingName: _mappingName,
                            mode: _mode
                        }]
                    }

                    this.gridElementOverlap[index]['led'].push(
                        elementId
                    )
                    // 把新建led添加至给定的segment组别中
                    if(_segmentId!=0){
                        // 若led在segment之前被加载，就初始化一个临时segment
                        // 由于不存在临时led, 所以这里可直接用isRedundant判断是否已经添加过了
                        if (this.segment[_segmentId] == null) {
                            this.segment[_segmentId] = {
                                name: '',
                                parentId: '',
                                groupId: '',
                                mappingInfo: [],
                                ledMember: [],
                                segmentMember: [],
                                isTemp: true
                            }
                        }
                        this.segment[_segmentId].ledMember.push(elementId);
                    }
                    if (_parentId != 0) {
                        // 若led在group之前被加载，就初始化一个临时group
                        if (this.group[_parentId] == null) {
                            this.group[_parentId] = {
                                name: '',
                                isVariable: 0,
                                buttonMember: [],
                                ledMember: [],
                                segmentMember: [],
                                isTemp: true
                            }
                        }
                        this.group[_parentId].ledMember.push(elementId)
                    }
                }
                
                //将Led插入数据库
                if (isCreate == 1) {
                    db.transaction(function (context) {  
                        if (isRedundant == false) {
                            context.executeSql('INSERT INTO Led (id,name,hwId,areaId,segmentId,templateId,boardType,parentId)\
                                VALUES (?,?,?,?,?,?,?,?)',[elementId,_name,_hwId,index,_segmentId,templateId,boardType,_parentId]);
                        }
                        if ((_mappingName != '' && _mappingName != null) || (_mode != '' && _mappingName != null)) {
                            context.executeSql('INSERT INTO LedMapping (id,mappingName, mode) VALUES (?,?,?)',[elementId,_mappingName, _mode]);
                        }
                    });
                    
                }
            },
            putSegment(segmentId, _name, _mappingName, _mode, isCreate,_parentId, _groupId) {
                // 规定：若重复put同一个segment，那么mappingName和mode将被添加，其余信息不做变动
                let templateId = this.templateId
                let boardType = this.boardType

                let isRedundant = false

                if (this.segment[segmentId] != null && this.segment[segmentId].isTemp == false) {
                    // 重复添加mappingInfo时的冗余情况
                    this.segment[segmentId].mappingInfo.push({
                        mappingName: _mappingName,
                        mode: _mode
                    })
                    isRedundant = true
                } else {
                    // 若存在一个临时的segment，则提取必要信息
                    let _ledMember = this.segment[segmentId] != null ? this.segment[segmentId].ledMember : []
                    let _segmentMember = this.segment[segmentId] != null ? this.segment[segmentId].segmentMember : []
                        this.segment[segmentId] = {
                            name: _name,
                            ledMember: _ledMember,
                            segmentMember: _segmentMember,
                            parentId:_parentId,
                            groupId: _groupId,
                            mappingInfo: [{
                                mappingName: _mappingName,
                                mode: _mode
                            }],
                            isTemp: false
                        }
                    
                    if (_groupId != 0) {
                        // 若segment在group之前被加载，就初始化一个临时group
                        if (this.group[_groupId] == null) {
                            this.group[_groupId] = {
                                name: '',
                                isVariable: 0,
                                buttonMember: [],
                                ledMember: [],
                                segmentMember: [],
                                isTemp: true
                            }
                            
                        }
                        this.group[_groupId].segmentMember.push(segmentId)
                    } 
                    if (_parentId != 0) {
                        // 若segment在parent_segment之前被加载，就初始化一个临时parent_segment
                        if (this.segment[_parentId] == null) {
                            this.segment[_parentId] = {
                                name: '',
                                parentId: '',
                                groupId: '',
                                ledMember: [],
                                segmentMember: [],
                                mappingInfo: [],
                                isTemp: true
                            }
                            
                        }
                        this.segment[_parentId].segmentMember.push(segmentId)
                    } 
                }
                if (isCreate == 1) {
                    db.transaction(function (context) {  
                        if (isRedundant == false) {
                            context.executeSql('INSERT INTO Segment (id,name,templateId,boardType,parentId, groupId)\
                                VALUES (?,?,?,?,?,?)',[segmentId,_name,templateId,boardType,_parentId,_groupId]);
                        }
                        if ((_mappingName != '' && _mappingName != null) || (_mode != '' && _mappingName != null)) {
                            context.executeSql('INSERT INTO SegmentMapping (id,mappingName, mode) VALUES (?,?,?)',[segmentId,_mappingName, _mode]);
                        }
                    });
                }
                
            },
            putGroup(groupId, _name, _isVariable, isCreate){
                let _buttonMember = []
                let _ledMember = []
                let _segmentMember = []

                // 若存在临时Group, 则读取其必要内容
                if (this.group[groupId] != null && this.group[groupId].isTemp == true) {
                    _buttonMember = this.group[groupId].buttonMember
                    _ledMember = this.group[groupId].ledMember
                    _segmentMember = this.group[groupId].segmentMember
                }

                let templateId = this.templateId
                let boardType = this.boardType

                this.group[groupId] = {
                    name: _name,
                    isVariable: _isVariable,
                    buttonMember: _buttonMember,
                    ledMember: _ledMember,
                    segmentMember: _segmentMember,
                    isTemp: false
                }
                
                if (isCreate == 1) {
                    db.transaction(function (context) { 
                        context.executeSql('INSERT INTO Groups (id,name,isVariable,templateId,boardType) VALUES (?,?,?,?,?)',[groupId,_name,_isVariable,templateId,boardType]);
                    });
                }
                this.dialogAddGroupVisible = false;
            },
            putVariable(variableId, variableName, groupId, isCreate) {
                let isRedundant = false;
                if (this.variable[variableId] == null) {
                    this.variable[variableId] = {
                        name: variableName,
                        groupMember: []
                    }
                } else {
                    isRedundant = true
                }
                if (groupId != 0) {
                    this.variable[variableId].groupMember.push(groupId)
                }
                
                let _this = this
                if (isCreate == 1) {
                    db.transaction(function (context) { 
                        if (isRedundant == false) {
                            context.executeSql('INSERT INTO Variable (id,name,templateId,boardType) VALUES (?,?,?,?)',[variableId,variableName,_this.templateId,_this.boardType]);
                        }
                        if (groupId != 0 && groupId != null) {
                            context.executeSql('INSERT INTO VariableMapping(groupId, variableId) VALUES (?,?)', [groupId, variableId])
                        }
                    });
                        
                }
            },
            //读取group列表, 存入groupList
            loadGroup(){
                this.groupList = []
                for (let groupId in this.group) {
                    this.groupList.push({
                        id: groupId,
                        name: this.group[groupId].name,
                        isVariable: this.group[groupId].isVariable
                    })
                }
                this.dialogGroupVisible = true
            },    
            //绑定编辑group成员的穿梭框数据
            groupComponentEdit() {
                let groupIdTmp = this.groupId;
                this.availableGroupElement =[];
                this.groupContent = [];
                
                // isVariable=1时不接受button分组
                if (this.group[groupIdTmp].isVariable == 0) {
                    for (let buttonId in this.button) {
                        let name = this.button[buttonId].name
                        let parentId = this.button[buttonId].parentId
                        if (parentId == 0 || parentId == groupIdTmp) {
                            this.availableGroupElement.push({
                                key: 'Button:'+buttonId,
                                label: 'Button:'+name,
                            })
                        }
                        if (parentId == groupIdTmp) {
                            this.groupContent.push("Button:"+buttonId)
                        }
                    }
                }

                // 同一个led只能被分为group或是segment
                for (let ledId in this.led) {
                    let name = this.led[ledId].name
                    let parentId = this.led[ledId].parentId
                    let segmentId = this.led[ledId].segmentId
                    if (segmentId == 0 && (parentId == 0 || parentId == groupIdTmp)) {
                        this.availableGroupElement.push({
                            key: "Led:"+ledId,
                            label: "Led:"+name,
                        })
                    }
                    if (parentId == groupIdTmp) {
                        this.groupContent.push("Led:"+ledId)
                    }
                }

                // 只允许对游离的segment进行group分组
                for (let segmentId in this.segment) {
                    let name = this.segment[segmentId].name
                    let groupId = this.segment[segmentId].groupId
                    let parentId = this.segment[segmentId].parentId
                    if ((groupId == 0 || groupId == groupIdTmp) && parentId == 0) {
                        this.availableGroupElement.push({
                            key: 'Segment:'+segmentId,
                            label: 'Segment:'+name,
                        })
                    }
                    if (groupId == groupIdTmp) {
                        this.groupContent.push("Segment:"+segmentId)
                    }
                }
                

                this.dialogEditGroupVisible = true;
            },
            submitGroupComponentEdit(){
                let groupIdTmp = this.groupId;
                let targetGroup = this.group[groupIdTmp]

                // 重置所有原始子组件的parentId
                for (let i in this.group[this.groupId].buttonMember) {
                    this.button[targetGroup.buttonMember[i]].parentId = 0
                    this.button[targetGroup.buttonMember[i]].mode = ''
                }

                for (let i in this.group[this.groupId].ledMember) {
                    this.led[targetGroup.ledMember[i]].parentId = 0
                    this.led[targetGroup.ledMember[i]].mappingInfo = [{
                        mappingName: '',
                        mode: ''
                    }]
                }

                for (let i in this.group[this.groupId].segmentMember) {
                    this.segment[targetGroup.segmentMember[i]].groupId = 0
                    this.segment[targetGroup.segmentMember[i]].mappingInfo = [{
                        mappingName: '',
                        mode: ''
                    }]
                }


                targetGroup.buttonMember = []
                targetGroup.ledMember = []
                targetGroup.segmentMember = []

                // 将编辑后的所有子组件的parentId置为当前groupId
                for (let i in this.groupContent) {
                    let element = this.groupContent[i]
                    let elementName = element.split(':')[0]
                    let elementId = element.split(':')[1]
                    if (elementName == 'Button') {
                        this.button[elementId].parentId = groupIdTmp
                        this.button[elementId].mode = ''
                        targetGroup.buttonMember.push(elementId)
                    } else if (elementName == 'Led') {
                        this.led[elementId].parentId = groupIdTmp
                        this.led[elementId].mappingInfo = [{
                        mappingName: '',
                        mode: ''
                    }]
                        targetGroup.ledMember.push(elementId)
                    } else {
                        this.segment[elementId].groupId = groupIdTmp
                        this.segment[elementId].mappingInfo = [{
                        mappingName: '',
                        mode: ''
                    }]
                        targetGroup.segmentMember.push(elementId)
                    }
                }
                
                let buttonChild = targetGroup.buttonMember
                let ledChild = targetGroup.ledMember
                let segmentChild = targetGroup.segmentMember

                db.transaction(function (context) {
                    context.executeSql('UPDATE Button SET parentId = 0, mode=NULL WHERE parentId = ?', [groupIdTmp])
                    context.executeSql('UPDATE Led SET parentId = 0 WHERE parentId = ?', [groupIdTmp])
                    context.executeSql('UPDATE Segment SET groupId = 0 WHERE groupId = ?', [groupIdTmp])

                    for (let i in buttonChild) {
                        context.executeSql('UPDATE Button SET parentId=?, mode=NULL WHERE id =?',[groupIdTmp,buttonChild[i]]);
                    }
                    for (let i in ledChild) {
                        context.executeSql('UPDATE Led SET parentId=? WHERE id =?',[groupIdTmp,ledChild[i]]);
                        context.executeSql('DELETE FROM LedMapping WHERE id =?',[ledChild[i]]);
                    }      
                    for (let i in segmentChild) {
                        context.executeSql('UPDATE Segment SET groupId=? WHERE id =?',[groupIdTmp,segmentChild[i]]);
                        context.executeSql('DELETE FROM SegmentMapping WHERE id=?',[segmentChild[i]])
                    }
                })
                
                this.$message({
                    message: 'Success!',
                    type: 'success',
                    duration: 1500
                });

                this.dialogEditGroupVisible = false;
            },
            // 设置group mapping中组件的相关属性
            groupMappingEdit() {
                /*
                    button: {
                        buttonId: mode
                    }
                    led: {
                        ledId: [{
                            mappingName:
                            mode:
                        }
                            ...
                        ]
                    }
                    segment: [{
                        segmentId: [{
                            mappingName:
                            mode:
                        }
                            ...
                        ]
                    }]
                 */
                this.groupAttribute = {
                    button: {},
                    led: {},
                    segment: {}
                }
                let buttonMember = this.group[this.groupId].buttonMember
                let ledMember = this.group[this.groupId].ledMember
                let segmentMember = this.group[this.groupId].segmentMember

              for (let i = 0 ; i < buttonMember.length ; ++i) {
                this.$set(this.groupAttribute['button'], buttonMember[i], this.button[buttonMember[i]].mode)
              }
              for (let i = 0 ; i < ledMember.length ; ++i) {
                let ledMapping = this.led[ledMember[i]].mappingInfo
                this.$set(this.groupAttribute['led'], ledMember[i], ledMapping)
              }
              for (let i = 0 ; i < segmentMember.length ; ++i) {
                let segmentMapping = this.segment[segmentMember[i]].mappingInfo
                this.$set(this.groupAttribute['segment'], segmentMember[i], segmentMapping)
              }
              this.dialogGroupAttributeVisible=true
            },
            alterGroupComponentMapping(id, mappingIndex, component, option) {
                if (component == 'led') {
                    if (option == 'add') {
                        this.groupAttribute['led'][id].push({
                            mappingName: '',
                            mode: ''
                        })
                    } else {
                        this.groupAttribute['led'][id].splice(mappingIndex, 1)
                    }
                } else {
                    if (option == 'add') {
                        this.groupAttribute['segment'][id].push({
                            mappingName: '',
                            mode: ''
                        })
                    } else {
                        this.groupAttribute['segment'][id].splice(mappingIndex, 1)
                    }
                }
            },
            submitGroupMappingEdit() {
                let buttonMember = []
                let ledMember = []
                let segmentMember = []

                //  更改所有组件的mappingName和mode
                for (let buttonId in this.groupAttribute.button) {
                    this.button[buttonId].mode = this.groupAttribute.button[buttonId]
                    buttonMember.push(buttonId)
                }
                for (let ledId in this.groupAttribute.led) {
                    if (this.groupAttribute.led[ledId].length == 0) {
                        this.groupAttribute.led[ledId].push({
                            mappingName: '',
                            mode: ''
                        })
                    }
                    this.led[ledId].mappingInfo = this.groupAttribute.led[ledId]
                    ledMember.push(ledId)
                }
                for (let segmentId in this.groupAttribute.segment) {
                    if (this.groupAttribute.segment[segmentId].length == 0) {
                        this.groupAttribute.segment[segmentId].push({
                            mappingName: '',
                            mode: ''
                        })
                    }
                    this.segment[segmentId].mappingInfo = this.groupAttribute.segment[segmentId]
                    segmentMember.push(segmentId)
                }

                let _this = this
                db.transaction(function (context) {
                    for (let i in buttonMember) {
                        context.executeSql('UPDATE Button SET mode=? WHERE id =?',
                            [_this.button[buttonMember[i]].mode, buttonMember[i]]);
                    }
                    for (let i in ledMember) {
                        // 先删除所有的mappingInfo, 再逐个插入新值
                        context.executeSql('DELETE FROM LedMapping WHERE id = ?', [ledMember[i]])
                        for (let j in _this.led[ledMember[i]].mappingInfo) {
                            let ledMapping = _this.led[ledMember[i]].mappingInfo[j]
                            if ((ledMapping.mappingName != null && ledMapping.mappingName != '') || (ledMapping.mode != null && ledMapping.mode != '')) {
                                context.executeSql('INSERT INTO LedMapping(id, mappingName, mode) VALUES(?, ?, ?)',
                                    [ledMember[i], ledMapping.mappingName, ledMapping.mode]);
                            }
                        }
                    }      
                    for (let i in segmentMember) {
                        // 同led
                        context.executeSql('DELETE FROM SegmentMapping WHERE id = ?', [segmentMember[i]])
                        for (let j in _this.segment[segmentMember[i]].mappingInfo) {
                            let segmentMapping = _this.segment[segmentMember[i]].mappingInfo[j]
                            if ((segmentMapping.mappingName != null && segmentMapping.mappingName != '') || (segmentMapping.mode != null && segmentMapping.mode != '')) {
                                context.executeSql('INSERT INTO SegmentMapping(id, mappingName, mode) VALUES(?, ?, ?)',
                                    [segmentMember[i], segmentMapping.mappingName, segmentMapping.mode])
                            }
                        }
                    }
                })

                this.groupAttribute = {
                    button: {},
                    led: {},
                    segment: {}
                }

                this.$message({
                    message: 'Success!',
                    type: 'success',
                    duration: 1500
                });

                this.dialogGroupAttributeVisible=false;
            },
            // 打开设置group基本信息的弹窗
            alterGroupInfo(row, column) {
                if (column.label=="name" || column.label == "isVariable") {
                    this.groupInfo.id = row.id
                    this.groupInfo.name = this.group[row.id].name
                    // el-checkbox需要绑定一个bool值
                    this.groupInfo.isVariable = this.group[row.id].isVariable == 1 ? true : false;
                    this.dialogGroupInfoVisible = true
                }
            },
            // 提交重置Group基本信息
            submitAlterGroupInfo() { 
                for (let i = 0 ; i < this.groupList.length ; ++i) {
                    // el-checkbox绑定的bool值转换为整型
                    this.groupInfo.isVariable = this.groupInfo.isVariable == true ? 1 : 0;
                    if (this.groupList[i].id == this.groupInfo.id) {
                        this.groupList[i].name = this.groupInfo.name
                        this.groupList[i].isVariable = this.groupInfo.isVariable
                    }
                }
                
                if (this.group[this.groupInfo.id].isVariable == this.groupInfo.isVariable) {
                    let _this = this
                    db.transaction(function (context) {
                        context.executeSql('UPDATE Groups SET name = ? WHERE id = ?', [_this.groupInfo.name, _this.groupInfo.id])
                    })
                } else {
                    // 与submitGroupDelete大部分相同
                    // 重置group的所有信息
                    let buttonMember = this.group[this.groupInfo.id].buttonMember
                    let ledMember = this.group[this.groupInfo.id].ledMember
                    let segmentMember = this.group[this.groupInfo.id].segmentMember

                    for (let i in buttonMember) {
                        this.button[buttonMember[i]].parentId = 0
                        this.button[buttonMember[i]].mode = ''
                    }
                    for (let i in ledMember) {
                        this.led[ledMember[i]].parentId = 0
                        this.led[ledMember[i]].mappingInfo = [{
                            mappingName: '',
                            mode: ''
                        }]
                    }
                    for (let i in segmentMember) {
                        this.segment[segmentMember[i]].groupId = 0
                        this.segment[segmentMember[i]].mappingInfo = [{
                            mappingName: '',
                            mode: ''
                        }]
                    }
                    this.group[this.groupInfo.id].buttonMember = []
                    this.group[this.groupInfo.id].ledMember = []
                    this.group[this.groupInfo.id].segmentMember = []

                    for (let variableId in this.variable) {
                        let groupMember = this.variable[variableId].groupMember
                        for (let i = 0 ; i < groupMember.length ; ++i) {
                            let groupIndex = groupMember.indexOf(this.groupInfo.id)
                            if (groupIndex != -1) {
                                this.variable[variableId].groupMember.splice(groupIndex, 1)
                            }
                        }
                    }

                    let _this = this
                    db.transaction(function (context) { 
                        context.executeSql('DELETE FROM LedMapping WHERE id in (SELECT id FROM Led WHERE parentId = ?)', [_this.groupInfo.id])
                        context.executeSql('DELETE FROM SegmentMapping WHERE id in (SELECT id FROM Segment WHERE groupId = ?)', [_this.groupInfo.id])
                        context.executeSql('UPDATE Groups SET name = ?, isVariable = ? WHERE id = ?', [_this.groupInfo.name, _this.groupInfo.isVariable, _this.groupInfo.id])
                        context.executeSql('UPDATE Button SET parentId=0,mode=NULL WHERE parentId =?',[_this.groupInfo.id]);
                        context.executeSql('UPDATE Led SET parentId=0 WHERE parentId =?',[_this.groupInfo.id]);
                        context.executeSql('UPDATE Segment SET groupId=0 WHERE groupId=?', [_this.groupInfo.id])
                        context.executeSql('DELETE FROM VariableMapping WHERE groupId = ?', [_this.groupInfo.id])
                    });
                }
                
                this.$message({
                    message: 'Success',
                    type: 'success',
                    duration: 1500
                });

                this.group[this.groupInfo.id].name = this.groupInfo.name
                this.group[this.groupInfo.id].isVariable = this.groupInfo.isVariable
                this.dialogGroupInfoVisible=false
            }, 
            //点击删除group后确认
            submitGroupDelete(){
                let groupIdTmp = this.groupId;
                let buttonMember = this.group[groupIdTmp].buttonMember
                let ledMember = this.group[groupIdTmp].ledMember
                let segmentMember = this.group[groupIdTmp].segmentMember

                // 重置所有子组件的parentId
                for (let i in buttonMember) {
                    this.button[buttonMember[i]].parentId = 0
                    this.button[buttonMember[i]].mode = ''
                }
                for (let i in ledMember) {
                    this.led[ledMember[i]].parentId = 0
                    this.led[ledMember[i]].mappingInfo = [{
                        mappingName: '',
                        mode: ''
                    }]
                }
                for (let i in segmentMember) {
                    this.segment[segmentMember[i]].groupId = 0
                    this.segment[segmentMember[i]].mappingInfo = [{
                        mappingName: '',
                        mode: ''
                    }]
                }

                // 删除group和groupList中的对应项
                for (let i = 0 ; i < this.groupList.length ; ++i) {
                    if (this.groupList[i].id == groupIdTmp) {
                        this.groupList.splice(i, 1)
                        break
                    }
                }

                // 删除所有variable中相应的group
                for (let variableId in this.variable) {
                    let groupMember = this.variable[variableId].groupMember
                    for (let i = 0 ; i < groupMember.length ; ++i) {
                        let groupIndex = groupMember.indexOf(groupIdTmp)
                        if (groupIndex != -1) {
                            this.variable[variableId].groupMember.splice(groupIndex, 1)
                        }
                    }
                }
                delete this.group[groupIdTmp]

                db.transaction(function (context) { 
                    context.executeSql('DELETE FROM LedMapping WHERE id in (SELECT id FROM Led WHERE parentId = ?)', [groupIdTmp])
                    context.executeSql('DELETE FROM SegmentMapping WHERE id in (SELECT id FROM Segment WHERE groupId = ?)', [groupIdTmp])
                    context.executeSql('DELETE FROM Groups WHERE id = ?',[groupIdTmp]);
                    context.executeSql('UPDATE Button SET parentId=0,mode=NULL WHERE parentId =?',[groupIdTmp]);
                    context.executeSql('UPDATE Led SET parentId=0 WHERE parentId =?',[groupIdTmp]);
                    context.executeSql('UPDATE Segment SET groupId=0 WHERE groupId=?', [groupIdTmp])
                    context.executeSql('DELETE FROM VariableMapping WHERE groupId = ?', [groupIdTmp])
                    
                });
                this.dialogDeleteGroupVisible = false;

                this.$message({
                    message: 'Success!',
                    type: 'success',
                    duration: 1500
                });

            },
            loadVariable() {
                this.variableList = []
                for (let variableId in this.variable) {
                    this.variableList.push({
                        id: variableId,
                        name: this.variable[variableId].name,
                    })
                }
                this.dialogVariableVisible = true
            },
            alterVariableInfo(row, column) {
                if (column.label=="name") {
                    this.variableRenameId = row.id
                    this.dialogVariableRenameVisible = true
                }
            },
            submitAlterVariableInfo() {
                for (let i = 0 ; i < this.variableList.length ; ++i) {
                    if (this.variableList[i].id == this.variableRenameId) {
                        this.variableList[i].name = this.variableRename
                    }
                }
                this.variable[this.variableRenameId].name = this.variableRename
                let _this = this
                db.transaction(function (context) {
                    context.executeSql('UPDATE Variable SET name = ? WHERE id = ?', [_this.variableRename, _this.variableRenameId])
                })
                this.$message({
                    message: 'Success',
                    type: 'success',
                    duration: 1500
                });
                this.dialogVariableRenameVisible=false
            },
            variableIncludeEdit() {
                this.availableVariableInclude =[];
                this.variableContent = [];
                for (let groupId in this.group) {
                    if (this.group[groupId].isVariable == 1) {
                        this.availableVariableInclude.push({
                            key: groupId,
                            label: 'Mapping:'+this.group[groupId].name,
                        })
                    }
                }
                for (let i = 0 ; i < this.variable[this.variableId].groupMember.length ; ++i) {
                    this.variableContent.push(this.variable[this.variableId].groupMember[i].toString())
                }
                this.dialogEditVariableVisible = true
            },
            submitVariableIncludeEdit() {
                this.variable[this.variableId].groupMember = this.variableContent

                let _this = this
                db.transaction(function (context) {
                    context.executeSql('DELETE FROM VariableMapping WHERE variableId = ?', [_this.variableId])
                    for (let i = 0 ; i < _this.variableContent.length ; ++i) {
                        context.executeSql("INSERT INTO VariableMapping (groupId, variableId) VALUES (?, ?)", [_this.variableContent[i], _this.variableId])
                    }
                })
                this.dialogEditVariableVisible = false

                this.$message({
                    message: 'Success',
                    type: 'success',
                    duration: 1500
                });
            },
            submitVariableDelete() {
                console.log(this.variableId)
                delete this.variable[this.variableId]
                for (let i = 0 ; i < this.variableList.length ; ++i) {
                    if (this.variableList[i].id == this.variableId) {
                        this.variableList.splice(i, 1)
                    }
                }
                
                let _this = this
                db.transaction(function (context) {
                    context.executeSql('DELETE FROM Variable WHERE id = ?', [_this.variableId])
                    context.executeSql('DELETE FROM VariableMapping WHERE variableId = ?', [_this.variableId])
                })

                this.$message({
                    message: 'Success',
                    type: 'success',
                    duration: 1500
                });
                this.dialogDeleteVariableVisible = false

            },
            // 打开管理Segment弹窗前进行的初始化工作
            loadSegment() {
                this.segmentList = []
                for (let segmentId in this.segment) {
                    this.segmentList.push({
                        id: segmentId,
                        name: this.segment[segmentId].name
                    })
                }
                this.dialogSegmentManagementVisible = true
            },
            // 打开重命名Segment弹窗
            alterSegmentInfo(row, column) {
                if (column.label=="name") {
                    this.segmentRenameId = row.id
                    this.dialogSegmentRenameVisible = true
                }
            },
            // 提交重命名Segment
            submitAlterSegmentInfo() {
                for (let i = 0 ; i < this.segmentList.length ; ++i) {
                    if (this.segmentList[i].id == this.segmentRenameId) {
                        this.segmentList[i].name = this.segmentRename
                    }
                }
                this.segment[this.segmentRenameId].name = this.segmentRename
                let _this = this
                db.transaction(function (context) {
                    context.executeSql('UPDATE Segment SET name = ? WHERE id = ?', [_this.segmentRename, _this.segmentRenameId])
                })
                this.$message({
                    message: 'Success',
                    type: 'success',
                    duration: 1500
                });
                this.dialogSegmentRenameVisible=false
            },
            // 打开编辑Segment子组件弹窗并进行初始化工作
            segmentChildEdit() {
                this.segmentChildMember = []
                this.availableSegmentChild = []

                // 判断两个segment之间是否存在父子关系
                let _this = this
                function isOfSegmentChild(childId, parentId) {
                    let childSegment = _this.segment[childId]
                    while (childSegment.parentId != 0) {
                        if (childSegment.parentId == parentId) {
                            return true;
                        }
                        childSegment = _this.segment[childSegment.parentId]
                    }
                    return false;
                }

                // 游离的led或是parent为选中segment的所有led
                // 同时除去所有已选择group分组的led
                for (let i in this.led) {
                    if ((this.led[i].segmentId == 0 || this.led[i].segmentId==this.manageSegmentId) && this.led[i].parentId == 0) {
                        this.availableSegmentChild.push(
                            {
                                key: 'led:'+i,
                                label: this.led[i].name,
                            }
                        )
                    }
                    // 初始化已成为子组件的led
                    if (this.led[i].segmentId == this.manageSegmentId) {
                        this.segmentChildMember.push('led:'+i)
                    }
                }

                // 游离的segment或是parent为选中segment的所有segment, 并且要保证添加的segment不是选中的segment的parent以及选中的segment其本身
                // 同时，不考虑所有已被group分组的segment
                for (let i in this.segment) {
                    if ((this.segment[i].parentId == 0 || this.segment[i].parentId == this.manageSegmentId) 
                        && isOfSegmentChild(this.manageSegmentId, i) == false && i != this.manageSegmentId && this.segment[i].groupId == 0) {
                        this.availableSegmentChild.push(
                            {
                                key: 'segment:'+i,
                                label: this.segment[i].name,
                            }
                        )
                    }
                    if (this.segment[i].parentId == this.manageSegmentId) {
                        this.segmentChildMember.push('segment:'+i)
                    }
                }
                this.dialogSegmentChildVisible = true
            },
            // 根据manageSegmentId和segmentChildMember, 更改segment相关组件之间的父子关系
            submitSegmentChildEdit() {
                let targetSegment = this.segment[this.manageSegmentId]

                // 重置相关led和segment的父id
                for (let i = 0 ; i < targetSegment.ledMember.length ; ++i) {
                    this.led[targetSegment.ledMember[i]].segmentId = 0
                }
                for (let i = 0 ; i < targetSegment.segmentMember.length ; ++i) {
                    this.segment[targetSegment.segmentMember[i]].parentId = 0
                }
                
                targetSegment.ledMember = []
                targetSegment.segmentMember = []

                // 遍历segmentChildMember, 即待处理的所有子组件
                for (let i = 0 ; i < this.segmentChildMember.length ; ++i) {
                    let element = this.segmentChildMember[i].split(':')     // element: [elementName, elementId]
                    if (element[0] == 'led') {
                        this.led[element[1]].segmentId = this.manageSegmentId
                        targetSegment.ledMember.push(element[1])
                    } else {
                        this.segment[element[1]].parentId = this.manageSegmentId
                        targetSegment.segmentMember.push(element[1])
                    }
                }

                let childLed = targetSegment.ledMember
                let childSegment = targetSegment.segmentMember
                let _this = this
                db.transaction(function (context) { 
                    context.executeSql('UPDATE Led SET segmentId = 0 WHERE segmentId = ?',[_this.manageSegmentId]);
                    context.executeSql('UPDATE Segment SET parentId = 0 WHERE parentId = ?', [_this.manageSegmentId])
                    for (let i = 0 ; i < childLed.length; ++i) {
                        context.executeSql('UPDATE Led SET segmentId = ? WHERE id = ?', [_this.manageSegmentId, childLed[i]])
                    }
                    for (let i = 0 ; i < childSegment.length; ++i) {
                        context.executeSql('UPDATE Segment SET parentId = ? WHERE id = ?', [_this.manageSegmentId, childSegment[i]])
                    }
                })

                this.$message({
                    message: 'Success!',
                    type: 'success',
                    duration: 1500
                });
            },
            // 在segment管理弹窗中的删除功能
            submitSegmentDelete() {
                // 列出一个segment的所有待删除组件（包括其自身），只在this.deleteSegmentComponent=true时使用。在这里声明是为了防止报错：Move function declaration to function body root
                let segmentComponent = [this.manageSegmentId]
                let _this = this
                // 迭代查找所有组件，目前找到所有segment即可）
                function findAllSegmentComponent(id) {
                    if (_this.segment[id].segmentMember.length == 0) {
                        return;
                    } else {
                        for (let i = 0 ; i < _this.segment[id].segmentMember.length ; ++i) {
                            segmentComponent.push(_this.segment[id].segmentMember[i])
                            findAllSegmentComponent(_this.segment[id].segmentMember[i])
                        }
                    }
                }
                // 判断是否要删除Segment中的所有组件
                if (this.deleteSegmentComponent == true) {
                    findAllSegmentComponent(this.manageSegmentId)
                    db.transaction(function (context) {
                        for (let i = 0 ; i < segmentComponent.length ; ++i) {
                            context.executeSql('DELETE FROM Led WHERE segmentId = ?', [segmentComponent[i]]);
                            context.executeSql('DELETE FROM Segment WHERE id = ?', [segmentComponent[i]])
                            context.executeSql('DELETE FROM SegmentMapping WHERE id = ?', [segmentComponent[i]])
                        }
                    });
                    this.loadBoard()
                    this.dialogDeleteSegmentVisible = false
                    this.dialogSegmentManagementVisible = false
                    this.$message({
                        message: 'Success!',
                        type: 'success',
                        duration: 1500
                    });
                } else {
                    let id = this.manageSegmentId
                    // 将孩子元素(led以及segment)的segmentParentId都置为默认值
                    for (let i = 0 ; i < this.segment[id].ledMember.length ; ++i) {
                        let childLedId = this.segment[id].ledMember[i]
                        this.led[childLedId].segmentId = this.defaultSegmentId
                    }
                    
                    for (let i = 0 ; i < this.segment[id].segmentMember.length ; ++i) {
                        let childSegmentId = this.segment[id].segmentMember[i]
                        this.segment[childSegmentId].parentId = this.defaultSegmentId
                    }
                    // 删除对应group中的segmentMember
                    let groupId = this.segment[id].groupId
                    if (groupId != 0) {
                        for (let i = 0 ; i < this.group[groupId].segmentMember.length ; ++i) {
                            if (this.group[groupId].segmentMember[i] == id) {
                                this.group[groupId].segmentMember.splice(i, 1)
                                break;
                            }
                        }
                    }
                    // 删除segment和segmentList中的对应项
                    delete this.segment[id]
                    for (let i = 0 ; i < this.segmentList.length ; ++i) {
                        if (this.segmentList[i].id == id) {
                            this.segmentList.splice(i, 1)
                            break
                        }
                    }
                    
                    let _this = this
                    // 同步数据库数据
                    db.transaction(function (context) {
                        context.executeSql('UPDATE Led SET segmentId = ? WHERE segmentId = ?', [_this.defaultSegmentId, _this.manageSegmentId])
                        context.executeSql('UPDATE Segment SET parentId = ? WHERE parentId = ?', [_this.defaultSegmentId, _this.manageSegmentId])
                        context.executeSql('DELETE FROM Segment Where id = ?', [_this.manageSegmentId]);
                        context.executeSql('DELETE FROM SegmentMapping WHERE id = ?', [_this.manageSegmentId])
                    });
                    this.dialogDeleteSegmentVisible = false
                    this.$message({
                        message: 'Success!',
                        type: 'success',
                        duration: 1500
                    });
                }
            },
            // 判断是否显示表单，并初始化表单绑定的数据
            ifGridDialogFormVisible(index) {
                // targetGrid读取gridElementOverlap对象，获得了选中网格中的所有组件。
                let targetGrid = this.gridElementOverlap[index]
                if (targetGrid == null) {
                    this.dialogFormVisible = false
                } else {
                    this.gridDialogIndex = index
                    // 初始化
                    this.targetGridElement = {
                        button: {},
                        led: {}
                    }
                    for (let i = 0 ; i < targetGrid['button'].length ; ++i) {
                        let targetButtonId = targetGrid['button'][i]
                        // 新增的对象默认是非响应式的，要用 this.$set() 其声明为响应式。
                        // 详见 https://cn.vuejs.org/v2/guide/reactivity.html
                        this.$set(this.targetGridElement['button'], targetButtonId, {})
                        let targetButton = this.targetGridElement['button'][targetButtonId]
                        this.$set(targetButton, 'name', this.button[targetButtonId].name)
                        this.$set(targetButton, 'hwId', this.button[targetButtonId].hwId)
                        this.$set(targetButton, 'parentId', this.button[targetButtonId].parentId)
                        this.$set(targetButton, 'toDelete', 0)
                        this.$set(targetButton, 'toAlterGroup', 0)
                    }
                    for (let i = 0 ; i < targetGrid['led'].length ; ++i) {
                        let targetLedId = targetGrid['led'][i]
                        // 与 button 同理
                        this.$set(this.targetGridElement['led'], targetLedId, {})
                        let targetLed = this.targetGridElement['led'][targetLedId]
                        this.$set(targetLed, 'name', this.led[targetLedId].name)
                        this.$set(targetLed, 'hwId', this.led[targetLedId].hwId)
                        this.$set(targetLed, 'segmentId', this.led[targetLedId].segmentId)
                        this.$set(targetLed, 'parentId', this.led[targetLedId].parentId)
                        this.$set(targetLed, 'toDelete', 0)
                        this.$set(targetLed, 'toAlterGroup', 0)
                    }
                    this.dialogFormVisible = true
                }
            }, 
            ifDialogSaveVisible() {
                if (this.templateId == 0) {
                    this.dialogSaveVisible = true
                } else {
                    this.$message({
                        message: 'Success!',
                        type: 'success',
                        duration: 1500
                    });
                }
            },
            // 提交表单触发事件
            submitGridDialogForm() {
                // 处理表单数据
                let targetButton = this.targetGridElement['button']
                db.transaction(function (context) { 
                    for (let buttonId in targetButton) {
                        if (targetButton[buttonId].toDelete==1) {
                            // 删除指定的button
                            context.executeSql('DELETE FROM Button WHERE id =?',[buttonId]);
                        } else {
                            if (targetButton[buttonId].toAlterGroup == 1) {
                                context.executeSql('UPDATE Button SET parentId=?,mode=? WHERE id=?',[targetButton[buttonId].parentId,'',buttonId]);
                            }
                            context.executeSql('UPDATE Button SET name=?,hwId=? WHERE id =?',[targetButton[buttonId].name,targetButton[buttonId].hwId,buttonId]);
                        }
                        
                    }
                })

                let targetLed = this.targetGridElement['led']
                db.transaction(function (context) {
                    for (let ledId in targetLed) {
                        if (targetLed[ledId].toDelete==1) {
                            // 删除指定的led
                            context.executeSql('DELETE FROM Led WHERE id =?',[ledId]);
                            context.executeSql('DELETE FROM LedMapping WHERE id = ?', [ledId])
                        } else {
                            // 修改led的group信息
                            if (targetLed[ledId].toAlterGroup == 1) {
                                context.executeSql('DELETE FROM LedMapping WHERE id=?',[ledId]);
                                context.executeSql('UPDATE Led SET parentId = ? WHERE id = ?', [targetLed[ledId].parentId, ledId])
                            }
                            context.executeSql('UPDATE Led SET name=?,hwId=?,segmentId=? WHERE id =?',[targetLed[ledId].name,targetLed[ledId].hwId,targetLed[ledId].segmentId,ledId]);
                        }
                    }
                })
                this.$message({
                    message: 'Success!',
                    type: 'success',
                    duration: 1500
                });
                this.targetGridElement = {
                    button: {},
                    led: {}
                }
                this.loadBoard()
                this.dialogFormVisible = false
            },
            //点保存取名后确定
            submitTotalForm(){
                let name = this.templateName;
                let templateId = this.templateId;
                db.transaction(function (context) { 
                    //若是新设计，插入TemplateList后将templateId暂时等于0的元素均设置成当前插入templateId
                    context.executeSql('INSERT INTO TemplateList (name) VALUES (?)',[name]);
                    context.executeSql('SELECT MAX(tp.id) AS max_result FROM TemplateList as tp', [], function (context, results) {
                        if (results.rows.length > 0) {
                            templateId = results.rows.item(0).max_result;
                            context.executeSql('UPDATE Button SET templateId=? WHERE templateId =0',[templateId]);
                            context.executeSql('UPDATE Led SET templateId=? WHERE templateId =0',[templateId]);
                            context.executeSql('UPDATE Segment SET templateId=? WHERE templateId =0',[templateId]);
                            context.executeSql('UPDATE Groups SET templateId=? WHERE templateId =0',[templateId]);
                            context.executeSql('UPDATE Variable SET templateId=? WHERE templateId =0',[templateId]);
                            location.reload();
                        }
                    });
                })
            },
            // 当页面状态改变时调用该函数重新计算网格边长
            alterGridWidth() {
                this.totalWidth = document.getElementById("MainArea").offsetWidth;
                // 重新计算一个网格的边长
                this.gridWidth = this.totalWidth / (1+this.rowGridNum) - 0.01
                
                // 改变网格宽度
                for (let i = 0 ; i < this.rowGridNum * this.rowGridNum ; ++i) {
                    this.gridStyle[i].width = this.gridWidth + 'px'
                    this.gridStyle[i].height = this.gridWidth + 'px'
                }
            },
            // 放大页面
            zoomIn() {
                // 解析百分制的width参数
                let width = parseInt(this.workspaceDivClass.width.replace('%', ''))
                if (width >= 300) {
                    this.$message({
                        type: 'error',
                        duration: 1500,
                        message: 'Error!'
                    });
                } else {
                    width += 15
                    this.workspaceDivClass.width = width + '%'
                    // 在这里读取offsetWidth仍会得到旧值
                    // 所以选择在mount里定义监听事件而非显式调用alterGridWidth()函数
                }
            },
            // 缩小页面
            zoomOut() {
                // 解析百分制的width参数
                let width = parseInt(this.workspaceDivClass.width.replace('%', ''))
                if (width <= 30) {
                    this.$message({
                        type: 'error',
                        duration: 1500,
                        message: 'Error!'
                    });
                } else {
                    this.workspaceDivClass.width = width - 15 + '%'
                }
            },
        }
    }
</script>

<style scoped>
    #MainArea {
        height:730px;
        border:1px solid #aaaaaa;
        overflow-y:auto
    }
    .el-icon-zoom-in {
        font-size: 30px;
        cursor: pointer
    }
    .el-icon-zoom-out {
        font-size: 30px;
        cursor: pointer
    }
    #button {
        width: 88px;
        height:88px;
        /*border:1px solid #aaaaaa;*/
        background-color: #8CFE90;
        margin: 17px;
    }

    #Led {
        width: 38px;
        height:38px;
        /*border:1px solid #aaaaaa;*/
    }

    #segment {
        margin: 15px;
        width: 88px;
        height:150px;
        /*border:1px solid #aaaaaa;*/
    }

</style>
