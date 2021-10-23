(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-78d011ba"],{"617b":function(a,t,e){},"8aba":function(a,t,e){"use strict";var l=e("617b"),i=e.n(l);i.a},b7c2:function(a,t,e){"use strict";e.r(t);var l=function(){var a=this,t=a.$createElement,e=a._self._c||t;return e("div",{staticClass:"app-container"},[e("div",{directives:[{name:"loading",rawName:"v-loading",value:a.loadingTables,expression:"loadingTables"}],staticClass:"div-box-down"},[e("form",{staticClass:"form-inline"},[e("div",{staticClass:"form-group"},[e("label",[a._v("database")]),e("el-select",{attrs:{clearable:"",filterable:"","allow-create":"","default-first-option":"",placeholder:"请选择","no-match-text":"新建"},model:{value:a.database,callback:function(t){a.database=t},expression:"database"}},a._l(a.queryDatabase,(function(a){return e("el-option",{key:a,attrs:{label:a,value:a}})})),1),e("el-button-group",[e("el-button",{staticClass:"btn btn-primary",on:{click:function(t){return a.getTables()}}},[a._v("查询")])],1)],1),e("div",{staticClass:"form-group"},[e("label",[a._v("table")]),e("el-select",{attrs:{clearable:"",filterable:"","allow-create":"","default-first-option":"",placeholder:"请选择","no-match-text":"新建"},model:{value:a.table,callback:function(t){a.table=t},expression:"table"}},a._l(a.queryTable,(function(a){return e("el-option",{key:a,attrs:{label:a,value:a}})})),1),e("el-button-group",[e("el-button",{staticClass:"btn btn-primary",on:{click:function(t){return a.getColumns()}}},[a._v("查询")])],1)],1)]),e("br"),e("div",{directives:[{name:"loading",rawName:"v-loading",value:a.loadingSql,expression:"loadingSql"}]},[e("el-tabs",{attrs:{type:"card",editable:""},on:{edit:a.handleTabsEdit},model:{value:a.listTabValue,callback:function(t){a.listTabValue=t},expression:"listTabValue"}},a._l(a.listTab,(function(t,l){return e("el-tab-pane",{key:t.name,attrs:{label:t.title,name:t.name}},[e("el-input",{attrs:{type:"textarea",rows:4,placeholder:"输入需要执行的sql"},model:{value:t.content,callback:function(e){a.$set(t,"content",e)},expression:"item.content"}})],1)})),1),e("el-button-group",[e("el-button",{staticClass:"btn btn-warning",on:{click:function(t){return a.getListPage()}}},[a._v("执行sql")]),e("el-button",{staticClass:"btn btn-danger",on:{click:function(t){return a.initTab()}}},[a._v("清空所有sql")])],1)],1)]),e("div",{directives:[{name:"show",rawName:"v-show",value:null!=a.obj[a.listTabValue].list,expression:"obj[listTabValue].list!=null"}]},[e("el-table",{directives:[{name:"loading",rawName:"v-loading",value:a.loadingList,expression:"loadingList"}],ref:"multipleTable",attrs:{data:a.obj[a.listTabValue].list,"element-loading-text":"Loading",border:"",fit:"",stripe:"","show-summary":"","sum-text":"S","highlight-current-row":"","max-height":"86%"},on:{"sort-change":a.handlerSortChange}},[e("el-table-column",{attrs:{fixed:"left",align:"center",type:"index","min-width":"12px"}}),a._l(a.obj[a.listTabValue].colMap,(function(t,l){return e("el-table-column",{key:l,attrs:{property:l,label:""==t?l:t,sortable:"","show-overflow-tooltip":"","min-width":"100px"},scopedSlots:a._u([{key:"default",fn:function(t){return[a._v(" "+a._s(t.row[t.column.property])+" ")]}}],null,!0)})}))],2),e("el-pagination",{staticStyle:{float:"right",margin:"10px 20px 0 0"},attrs:{"current-page":a.obj[a.listTabValue].page.nowpage,"page-size":a.obj[a.listTabValue].page.shownum,"page-sizes":[2,4,8,16,32,64,128],"pager-count":9,layout:"total, sizes, prev, pager, next, jumper",total:a.obj[a.listTabValue].page.num,background:""},on:{"current-change":a.handlerCurrentChange,"size-change":a.handlerSizeChange}})],1),e("el-input",{directives:[{name:"show",rawName:"v-show",value:a.obj[a.listTabValue].info,expression:"obj[listTabValue].info "}],attrs:{type:"textarea",rows:10,placeholder:"执行sql结果"},model:{value:a.obj[a.listTabValue].info,callback:function(t){a.$set(a.obj[a.listTabValue],"info",t)},expression:"obj[listTabValue].info"}})],1)},i=[],n=(e("ac6a"),e("f559"),e("28a5"),e("7f7f"),e("5c96")),s={filters:{statusFilter:function(a){var t={published:"success",draft:"gray",deleted:"danger"};return t[a]}},data:function(){return{obj:[],list:[],colMap:{},colKey:"",rowSearch:{},rowUpdate:{},rowUpdateFrom:{},rowSelect:[],info:"",queryTable:[],table:"",page:{nowpage:1,num:0,order:"",pagenum:0,shownum:8},loadingList:!1,loadingTables:!1,loadingSql:!1,tabPre:"T",defaultSql:"select t.* from walker.W_USER t where 1=1 ",listTab:[],tabIndex:0,listTabValue:"0",queryDatabase:[],database:""}},created:function(){this.initTab(),this.getDatabases()},methods:{initTab:function(){if(!this.obj||0==this.obj.length){this.obj=[],this.listTab=[];for(var a=0;a<3;a++)this.addTab();this.listTabValue=this.listTab[0].name}},addTab:function(){var a=this.handleTabsEdit("","add");return a},initTabVal:function(a){this.obj[a.name]={},this.obj[a.name].page={nowpage:1,num:0,order:"",pagenum:0,shownum:8},this.obj[a.name].list=null,this.obj[a.name].colKey="",this.obj[a.name].colMap=null},getDatabases:function(){var a=this;this.loadingTables=!0;var t=this.assign({},{});this.get("/common/getDatabasesOrUsers.do",t).then((function(t){a.queryDatabase=t.data,a.database=null!=t.data&&t.data.length>0?t.data[0]:"walker",a.database="walker",a.loadingTables=!1,a.getTables()})).catch((function(){a.loadingTables=!1}))},getTables:function(){var a=this;this.loadingTables=!0;var t=this.assign({_TABLE_NAME_:this.table,_DATABASE_:this.database},{});this.get("/common/getTables.do",t).then((function(t){a.queryTable=t.data,a.table=null!=t.data&&t.data.length>0?t.data[0]:"W_USER",a.loadingTables=!1})).catch((function(){a.loadingTables=!1}))},getColumns:function(){var a=this.addTab();a.content="select t.* from "+this.database+"."+this.table+" t where 1=1 "},getListPage:function(){var a=this;this.loadingSql=!0;for(var t=null,e=0;e<this.listTab.length;e++)this.listTab[e].name==this.listTabValue&&(t=this.listTab[e]);if(null!=t){this.obj[this.listTabValue].list=null,this.obj[this.listTabValue].info="",this.obj[this.listTabValue].colKey="",this.obj[this.listTabValue].colMap=null;var l=t.content,i=this.assign({nowPage:this.obj[this.listTabValue].page.nowpage,showNum:this.obj[this.listTabValue].page.shownum,order:this.obj[this.listTabValue].page.order},{}),s=this.assign({_SQL_:l},i);this.get("/common/exeSql.do",s).then((function(e){null!=e.data.data?(a.obj[a.listTabValue].list=e.data.data,a.obj[a.listTabValue].page=e.data.page,a.obj[a.listTabValue].colMap=e.data.colMap,a.obj[a.listTabValue].colKey=e.data.colKey):a.obj[a.listTabValue].info=e.data.info,t.title=t.title.split(" ")[0]+" "+e.costTime,a.loadingSql=!1})).catch((function(){a.loadingSql=!1}))}else n["Message"].error("tab 页异常?")},handlerSortChange:function(a){console.info("handlerSortChange "+JSON.stringify(a)),this.obj[this.listTabValue].page.order=a.prop+(a.order.startsWith("desc")?" desc":"")},handlerCurrentChange:function(a){console.info("handlerCurrentChange"),console.info(a),this.obj[this.listTabValue].page.nowpage=a,this.getListPage()},handlerSizeChange:function(a){this.obj[this.listTabValue].page.shownum=a,this.getListPage()},handleTabsEdit:function(a,t){if("add"===t){var e={title:this.tabPre+this.tabIndex,name:""+this.tabIndex,content:this.defaultSql};return this.listTab.push(e),this.listTabValue=e.name,this.tabIndex++,this.initTabVal(e),e}if("remove"===t){var l=this.listTab;this.list[this.listTabValue]=null;var i=this.listTabValue;i===a&&l.forEach((function(t,e){if(t.name===a){var n=l[e+1]||l[e-1];n&&(i=n.name)}})),this.listTabValue=i,this.listTab=l.filter((function(t){return t.name!==a}))}}}},o=s,r=(e("8aba"),e("2877")),b=Object(r["a"])(o,l,i,!1,null,"0f09f366",null);t["default"]=b.exports}}]);