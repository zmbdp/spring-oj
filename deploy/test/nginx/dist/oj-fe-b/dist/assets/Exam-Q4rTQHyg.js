import"./request-CT5bBdcx.js";/* empty css                     *//* empty css                     */import{g as F,d as L,p as O,c as R}from"./exam-CFzY93NG.js";import{e as h,r as k,c as g,b as a,w as n,W as A,U as W,X,Y,o as s,i,u as Z,Z as q,n as r,g as x,d as b,$ as G,S as H,E as J,T as K,_ as M,a0 as Q}from"./index-4IXDAXJx.js";const ee={key:0},te={key:1},ae={key:0},ne={key:1},de={__name:"Exam",setup(le){function f(o){const e=new Date;return new Date(o.startTime)>e}const l=h({pageNum:1,pageSize:10,startTime:"",endTime:"",title:""}),v=k([]),E=k(0),m=k([]);async function u(){m.value[0]instanceof Date&&(l.startTime=m.value[0].toISOString()),m.value[1]instanceof Date&&(l.endTime=m.value[1].toISOString());const o=await F(l);v.value=o.rows,E.value=o.total}u();function C(o){l.pageNum=1,u()}function S(o){u()}function T(){l.pageNum=1,u()}function z(){l.pageNum=1,l.pageSize=10,l.title="",l.startTime="",l.endTime="",m.value.length=0,u()}function N(){b.push("/oj/system/updateExam?type=add")}async function I(o){b.push(`/oj/system/updateExam?type=edit&examId=${o}`)}async function V(o){await L(o),l.pageNum=1,u()}async function w(o){await O(o),u()}async function D(o){await R(o),u()}return(o,e)=>{const P=G,c=H,$=J,p=K,U=W,d=M,_=Q,B=X,j=Y;return s(),g(A,null,[a(U,{inline:"true"},{default:n(()=>[a(c,{label:"创建日期"},{default:n(()=>[a(P,{modelValue:m.value,"onUpdate:modelValue":e[0]||(e[0]=t=>m.value=t),style:{width:"240px"},type:"datetimerange","range-separator":"至","start-placeholder":"开始日期","end-placeholder":"结束日期"},null,8,["modelValue"])]),_:1}),a(c,{label:"竞赛名称"},{default:n(()=>[a($,{modelValue:l.title,"onUpdate:modelValue":e[1]||(e[1]=t=>l.title=t),placeholder:"请输入竞赛名称"},null,8,["modelValue"])]),_:1}),a(c,null,{default:n(()=>[a(p,{onClick:T,plain:""},{default:n(()=>e[4]||(e[4]=[i("搜索")])),_:1}),a(p,{onClick:z,plain:"",type:"info"},{default:n(()=>e[5]||(e[5]=[i("重置")])),_:1}),a(p,{type:"primary",icon:Z(q),plain:"",onClick:N},{default:n(()=>e[6]||(e[6]=[i("添加竞赛")])),_:1},8,["icon"])]),_:1})]),_:1}),a(B,{height:"526px",data:v.value},{default:n(()=>[a(d,{prop:"title",label:"竞赛标题"}),a(d,{prop:"startTime",width:"180px",label:"竞赛开始时间"}),a(d,{prop:"endTime",width:"180px",label:"竞赛结束时间"}),a(d,{label:"是否开赛",width:"100px"},{default:n(({row:t})=>[f(t)?(s(),g("div",te,[a(_,{type:"info"},{default:n(()=>e[8]||(e[8]=[i("未开赛")])),_:1})])):(s(),g("div",ee,[a(_,{type:"warning"},{default:n(()=>e[7]||(e[7]=[i("已开赛")])),_:1})]))]),_:1}),a(d,{prop:"status",width:"100px",label:"是否发布"},{default:n(({row:t})=>[t.status==0?(s(),g("div",ae,[a(_,{type:"danger"},{default:n(()=>e[9]||(e[9]=[i("未发布")])),_:1})])):r("",!0),t.status==1?(s(),g("div",ne,[a(_,{type:"success"},{default:n(()=>e[10]||(e[10]=[i("已发布")])),_:1})])):r("",!0)]),_:1}),a(d,{prop:"createName",width:"100px",label:"创建用户"}),a(d,{prop:"createTime",width:"180px",label:"创建时间"}),a(d,{label:"操作",width:"160px"},{default:n(({row:t})=>[f(t)&&t.status==0?(s(),x(p,{key:0,type:"text",onClick:y=>I(t.examId)},{default:n(()=>e[11]||(e[11]=[i("编辑 ")])),_:2},1032,["onClick"])):r("",!0),f(t)&&t.status==0?(s(),x(p,{key:1,type:"text",onClick:y=>V(t.examId),class:"red"},{default:n(()=>e[12]||(e[12]=[i("删除 ")])),_:2},1032,["onClick"])):r("",!0),t.status==1&&f(t)?(s(),x(p,{key:2,type:"text",onClick:y=>D(t.examId)},{default:n(()=>e[13]||(e[13]=[i("撤销发布")])),_:2},1032,["onClick"])):r("",!0),t.status==0&&f(t)?(s(),x(p,{key:3,type:"text",onClick:y=>w(t.examId)},{default:n(()=>e[14]||(e[14]=[i("发布")])),_:2},1032,["onClick"])):r("",!0),f(t)?r("",!0):(s(),x(p,{key:4,type:"text"},{default:n(()=>e[15]||(e[15]=[i("已开赛，不允许操作")])),_:1}))]),_:1})]),_:1},8,["data"]),a(j,{background:"",size:"small",layout:"total, sizes, prev, pager, next, jumper",total:E.value,"current-page":l.pageNum,"onUpdate:currentPage":e[2]||(e[2]=t=>l.pageNum=t),"page-size":l.pageSize,"onUpdate:pageSize":e[3]||(e[3]=t=>l.pageSize=t),"page-sizes":[5,10,30,50],onSizeChange:C,onCurrentChange:S},null,8,["total","current-page","page-size"])],64)}}};export{de as default};
