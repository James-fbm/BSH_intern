

<template>
  <el-row>
    <el-col :span="22" style="overflow-x:auto">
      <router-view :key="$route.fullPath" />
    </el-col>
    <el-col :span="2" style="background-color:#F2F6FC;">
    <h3 style="text-align:center">DesignList</h3>
      <el-menu default-active="2" class="el-menu-vertical-demo" background-color="#F2F6FC">
      <el-submenu v-for="(t_item, index) in templates" :key="index" :index = "index+''">
        <template slot="title">
          <span>{{t_item.name}}</span>
        </template>
        <el-menu-item-group>
          <el-menu-item><router-link :to="t_item.path">Edit</router-link></el-menu-item>
          <el-menu-item v-if="t_item.id != 0" @click="renameTemplateId = t_item.id; dialogRenameTemplateVisible = true">Rename</el-menu-item>
          <el-menu-item @click="DeleteTemplateId = t_item.id; dialogDeleteTemplateVisible = true">Delete</el-menu-item>
        </el-menu-item-group>
      </el-submenu>
    </el-menu>
    <!-- /////////////////////////////// -->
      <!-- <div v-for="(t_item, index) in templates" :key="index">
        <router-link :to="t_item.path">{{t_item.name}}</router-link>
      </div> -->
      <el-dialog title="Input Name" :visible.sync="dialogRenameTemplateVisible">
          <el-input v-model="newTemplateName" placeholder="Please input template name"></el-input>
          <div>
              <el-button type="primary" size="mini" @click="renameTemplate">Confirm</el-button>
              <el-button size="mini" type="text" @click="dialogRenameTemplateVisible=false;">Cancel</el-button>
          </div>
      </el-dialog>

      <el-dialog title="Tips" :visible.sync="dialogDeleteTemplateVisible">
        <span>Are you sure?</span>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="deleteDesign()">Confirm</el-button>
          <el-button @click="dialogDeleteTemplateVisible=false">Cancel</el-button>
        </span>
      </el-dialog>
    </el-col>

  </el-row>
  
</template>

<script>
var db = openDatabase('BSHdb', '1.0', 'Test DB', 2 * 1024 * 1024);
  export default {
    data() {
      return {
        templates: [
          {
            id:0,
            name: "Default",
            path: "/templateDemo2/0"
          }
        ],
        dialogDeleteTemplateVisible:false,
        DeleteTemplateId:0,
        dialogRenameTemplateVisible: false,
        renameTemplateId: 0,
        newTemplateName: '',
      }
    },
    mounted:function(){
      this.initPage();
    },
    methods: {
      initPage(){
      //元素唯一id
        let templates = this.templates;
        db.transaction(function (context) {  
          context.executeSql('CREATE TABLE IF NOT EXISTS TemplateList ("id" INTEGER NOT NULL,"name" TEXT,PRIMARY KEY ("id"))');
          context.executeSql('CREATE TABLE IF NOT EXISTS Button ("id" INTEGER NOT NULL,"name" TEXT,"hwId" TEXT,"mode" TEXT,"areaId" INTEGER,"templateId" INTEGER,"boardType" INTEGER,"parentId" INTEGER,PRIMARY KEY ("id"))');
          context.executeSql('CREATE TABLE IF NOT EXISTS Led ("id" INTEGER NOT NULL,"name" TEXT,"hwId" TEXT,"areaId" INTEGER,"segmentId" INTEGER,"templateId" INTEGER,"boardType" INTEGER,"parentId" INTEGER,PRIMARY KEY ("id"))');
          context.executeSql('CREATE TABLE IF NOT EXISTS Segment ("id" INTEGER NOT NULL,"name" TEXT,"templateId" INTEGER,"boardType" INTEGER,"parentId" INTEGER, "groupId" INTEGER, PRIMARY KEY ("id"))');
          context.executeSql('CREATE TABLE IF NOT EXISTS LedMapping ("id" INTEGER NOT NULL,"mappingName" TEXT,"mode" TEXT)');
          context.executeSql('CREATE TABLE IF NOT EXISTS SegmentMapping ("id" INTEGER NOT NULL,"mappingName" TEXT,"mode" TEXT)');
          context.executeSql('CREATE TABLE IF NOT EXISTS Groups ("id" INTEGER NOT NULL,"name" TEXT, "isVariable" BOOLEAN, "templateId" INTEGER,"boardType" INTEGER,PRIMARY KEY ("id"))');
          context.executeSql('CREATE TABLE IF NOT EXISTS Variable("id" INTEGER NOT NULL, "name" TEXT, "templateId" INTEGER, "boardType" INTEGER, PRIMARY KEY("id"))')
          context.executeSql('CREATE TABLE IF NOT EXISTS VariableMapping("groupId" INTEGER, "variableId" INTEGER)')
          context.executeSql('SELECT * FROM TemplateList',[],function(context,results){
            let len = results.rows.length;
            for(let i = 0;i<len;i++){
              templates.push({name:results.rows.item(i).name,path:"/templateDemo2/"+results.rows.item(i).id,id:results.rows.item(i).id});
            }
          });
      })
    },
      deleteDesign(){
        let id = this.DeleteTemplateId;
        db.transaction(function(context){
          context.executeSql('DELETE FROM LedMapping WHERE id IN (SELECT id FROM Led WHERE templateId = ?)', [id])
          context.executeSql('DELETE FROM SegmentMapping WHERE id IN (SELECT id FROM Segment WHERE templateId = ?)', [id])
          context.executeSql('DELETE FROM VariableMapping WHERE groupId IN (SELECT id FROM Groups WHERE templateId = ?)', [id])
          context.executeSql('DELETE FROM TemplateList WHERE id = ?',[id]);
          context.executeSql('DELETE FROM Segment WHERE templateId = ?',[id]);
          context.executeSql('DELETE FROM LED WHERE templateId = ?',[id]);
          context.executeSql('DELETE FROM Groups WHERE templateId = ?',[id]);
          context.executeSql('DELETE FROM Button WHERE templateId = ?',[id]);
          context.executeSql('DELETE FROM Variable WHERE templateId = ?', [id])
        })
        location.reload();
      },
      renameTemplate() {
        console.log(this.renameTemplateId)
        console.log(this.newTemplateName)
        let _this = this
        db.transaction(function(context){
          context.executeSql('UPDATE TemplateList SET name = ? WHERE id = ?',[_this.newTemplateName, _this.renameTemplateId]);
          location.reload()
        })
      }
    }
  }
</script>

<style>
.el-menu {
	border-right:0!important;
}
</style>

