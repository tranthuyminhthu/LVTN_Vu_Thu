import{B as D,s as S,a as x,b as y,F as E,c as R,u as I,d as A,f as K,Z as m,e as $,r as v,g as O,h,o as s,w as u,i as l,j as d,m as a,k as w,T as V,l as T,n as c,p as Z,q as f,t as q,v as z,x as P,y as j,z as b,A as k,C as F}from"./index-BOhHs8XI.js";var M=function(n){var t=n.dt;return`
.p-drawer {
    display: flex;
    flex-direction: column;
    transform: translate3d(0px, 0px, 0px);
    position: relative;
    transition: transform 0.3s;
    background: `.concat(t("drawer.background"),`;
    color: `).concat(t("drawer.color"),`;
    border: 1px solid `).concat(t("drawer.border.color"),`;
    box-shadow: `).concat(t("drawer.shadow"),`;
}

.p-drawer-content {
    overflow-y: auto;
    flex-grow: 1;
    padding: `).concat(t("drawer.content.padding"),`;
}

.p-drawer-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-shrink: 0;
    padding: `).concat(t("drawer.header.padding"),`;
}

.p-drawer-footer {
    padding: `).concat(t("drawer.footer.padding"),`;
}

.p-drawer-title {
    font-weight: `).concat(t("drawer.title.font.weight"),`;
    font-size: `).concat(t("drawer.title.font.size"),`;
}

.p-drawer-full .p-drawer {
    transition: none;
    transform: none;
    width: 100vw !important;
    height: 100vh !important;
    max-height: 100%;
    top: 0px !important;
    left: 0px !important;
    border-width: 1px;
}

.p-drawer-left .p-drawer-enter-from,
.p-drawer-left .p-drawer-leave-to {
    transform: translateX(-100%);
}

.p-drawer-right .p-drawer-enter-from,
.p-drawer-right .p-drawer-leave-to {
    transform: translateX(100%);
}

.p-drawer-top .p-drawer-enter-from,
.p-drawer-top .p-drawer-leave-to {
    transform: translateY(-100%);
}

.p-drawer-bottom .p-drawer-enter-from,
.p-drawer-bottom .p-drawer-leave-to {
    transform: translateY(100%);
}

.p-drawer-full .p-drawer-enter-from,
.p-drawer-full .p-drawer-leave-to {
    opacity: 0;
}

.p-drawer-full .p-drawer-enter-active,
.p-drawer-full .p-drawer-leave-active {
    transition: opacity 400ms cubic-bezier(0.25, 0.8, 0.25, 1);
}

.p-drawer-left .p-drawer {
    width: 20rem;
    height: 100%;
    border-inline-end-width: 1px;
}

.p-drawer-right .p-drawer {
    width: 20rem;
    height: 100%;
    border-inline-start-width: 1px;
}

.p-drawer-top .p-drawer {
    height: 10rem;
    width: 100%;
    border-block-end-width: 1px;
}

.p-drawer-bottom .p-drawer {
    height: 10rem;
    width: 100%;
    border-block-start-width: 1px;
}

.p-drawer-left .p-drawer-content,
.p-drawer-right .p-drawer-content,
.p-drawer-top .p-drawer-content,
.p-drawer-bottom .p-drawer-content {
    width: 100%;
    height: 100%;
}

.p-drawer-open {
    display: flex;
}

.p-drawer-mask:dir(rtl) {
    flex-direction: row-reverse;
}
`)},N={mask:function(n){var t=n.position,o=n.modal;return{position:"fixed",height:"100%",width:"100%",left:0,top:0,display:"flex",justifyContent:t==="left"?"flex-start":t==="right"?"flex-end":"center",alignItems:t==="top"?"flex-start":t==="bottom"?"flex-end":"center",pointerEvents:o?"auto":"none"}},root:{pointerEvents:"auto"}},U={mask:function(n){var t=n.instance,o=n.props,i=["left","right","top","bottom"],r=i.find(function(p){return p===o.position});return["p-drawer-mask",{"p-overlay-mask p-overlay-mask-enter":o.modal,"p-drawer-open":t.containerVisible,"p-drawer-full":t.fullScreen},r?"p-drawer-".concat(r):""]},root:function(n){var t=n.instance;return["p-drawer p-component",{"p-drawer-full":t.fullScreen}]},header:"p-drawer-header",title:"p-drawer-title",pcCloseButton:"p-drawer-close-button",content:"p-drawer-content",footer:"p-drawer-footer"},X=D.extend({name:"drawer",theme:M,classes:U,inlineStyles:N}),Y={name:"BaseDrawer",extends:R,props:{visible:{type:Boolean,default:!1},position:{type:String,default:"left"},header:{type:null,default:null},baseZIndex:{type:Number,default:0},autoZIndex:{type:Boolean,default:!0},dismissable:{type:Boolean,default:!0},showCloseIcon:{type:Boolean,default:!0},closeButtonProps:{type:Object,default:function(){return{severity:"secondary",text:!0,rounded:!0}}},closeIcon:{type:String,default:void 0},modal:{type:Boolean,default:!0},blockScroll:{type:Boolean,default:!1}},style:X,provide:function(){return{$pcDrawer:this,$parentInstance:this}}},g={name:"Drawer",extends:Y,inheritAttrs:!1,emits:["update:visible","show","after-show","hide","after-hide"],data:function(){return{containerVisible:this.visible}},container:null,mask:null,content:null,headerContainer:null,footerContainer:null,closeButton:null,outsideClickListener:null,documentKeydownListener:null,watch:{dismissable:function(n){n?this.enableDocumentSettings():this.disableDocumentSettings()}},updated:function(){this.visible&&(this.containerVisible=this.visible)},beforeUnmount:function(){this.disableDocumentSettings(),this.mask&&this.autoZIndex&&m.clear(this.mask),this.container=null,this.mask=null},methods:{hide:function(){this.$emit("update:visible",!1)},onEnter:function(){this.$emit("show"),this.focus(),this.bindDocumentKeyDownListener(),this.autoZIndex&&m.set("modal",this.mask,this.baseZIndex||this.$primevue.config.zIndex.modal)},onAfterEnter:function(){this.enableDocumentSettings(),this.$emit("after-show")},onBeforeLeave:function(){this.modal&&!this.isUnstyled&&$(this.mask,"p-overlay-mask-leave")},onLeave:function(){this.$emit("hide")},onAfterLeave:function(){this.autoZIndex&&m.clear(this.mask),this.unbindDocumentKeyDownListener(),this.containerVisible=!1,this.disableDocumentSettings(),this.$emit("after-hide")},onMaskClick:function(n){this.dismissable&&this.modal&&this.mask===n.target&&this.hide()},focus:function(){var n=function(i){return i&&i.querySelector("[autofocus]")},t=this.$slots.header&&n(this.headerContainer);t||(t=this.$slots.default&&n(this.container),t||(t=this.$slots.footer&&n(this.footerContainer),t||(t=this.closeButton))),t&&K(t)},enableDocumentSettings:function(){this.dismissable&&!this.modal&&this.bindOutsideClickListener(),this.blockScroll&&A()},disableDocumentSettings:function(){this.unbindOutsideClickListener(),this.blockScroll&&I()},onKeydown:function(n){n.code==="Escape"&&this.hide()},containerRef:function(n){this.container=n},maskRef:function(n){this.mask=n},contentRef:function(n){this.content=n},headerContainerRef:function(n){this.headerContainer=n},footerContainerRef:function(n){this.footerContainer=n},closeButtonRef:function(n){this.closeButton=n?n.$el:void 0},bindDocumentKeyDownListener:function(){this.documentKeydownListener||(this.documentKeydownListener=this.onKeydown,document.addEventListener("keydown",this.documentKeydownListener))},unbindDocumentKeyDownListener:function(){this.documentKeydownListener&&(document.removeEventListener("keydown",this.documentKeydownListener),this.documentKeydownListener=null)},bindOutsideClickListener:function(){var n=this;this.outsideClickListener||(this.outsideClickListener=function(t){n.isOutsideClicked(t)&&n.hide()},document.addEventListener("click",this.outsideClickListener))},unbindOutsideClickListener:function(){this.outsideClickListener&&(document.removeEventListener("click",this.outsideClickListener),this.outsideClickListener=null)},isOutsideClicked:function(n){return this.container&&!this.container.contains(n.target)}},computed:{fullScreen:function(){return this.position==="full"},closeAriaLabel:function(){return this.$primevue.config.locale.aria?this.$primevue.config.locale.aria.close:void 0}},directives:{focustrap:E},components:{Button:y,Portal:x,TimesIcon:S}},G=["aria-modal"];function H(e,n,t,o,i,r){var p=v("Button"),C=v("Portal"),L=O("focustrap");return s(),h(C,null,{default:u(function(){return[i.containerVisible?(s(),l("div",a({key:0,ref:r.maskRef,onMousedown:n[0]||(n[0]=function(){return r.onMaskClick&&r.onMaskClick.apply(r,arguments)}),class:e.cx("mask"),style:e.sx("mask",!0,{position:e.position,modal:e.modal})},e.ptm("mask")),[w(V,a({name:"p-drawer",onEnter:r.onEnter,onAfterEnter:r.onAfterEnter,onBeforeLeave:r.onBeforeLeave,onLeave:r.onLeave,onAfterLeave:r.onAfterLeave,appear:""},e.ptm("transition")),{default:u(function(){return[e.visible?T((s(),l("div",a({key:0,ref:r.containerRef,class:e.cx("root"),style:e.sx("root"),role:"complementary","aria-modal":e.modal},e.ptmi("root")),[e.$slots.container?c(e.$slots,"container",{key:0,closeCallback:r.hide}):(s(),l(Z,{key:1},[f("div",a({ref:r.headerContainerRef,class:e.cx("header")},e.ptm("header")),[c(e.$slots,"header",{class:q(e.cx("title"))},function(){return[e.header?(s(),l("div",a({key:0,class:e.cx("title")},e.ptm("title")),z(e.header),17)):d("",!0)]}),e.showCloseIcon?(s(),h(p,a({key:0,ref:r.closeButtonRef,type:"button",class:e.cx("pcCloseButton"),"aria-label":r.closeAriaLabel,unstyled:e.unstyled,onClick:r.hide},e.closeButtonProps,{pt:e.ptm("pcCloseButton"),"data-pc-group-section":"iconcontainer"}),{icon:u(function(B){return[c(e.$slots,"closeicon",{},function(){return[(s(),h(P(e.closeIcon?"span":"TimesIcon"),a({class:[e.closeIcon,B.class]},e.ptm("pcCloseButton").icon),null,16,["class"]))]})]}),_:3},16,["class","aria-label","unstyled","onClick","pt"])):d("",!0)],16),f("div",a({ref:r.contentRef,class:e.cx("content")},e.ptm("content")),[c(e.$slots,"default")],16),e.$slots.footer?(s(),l("div",a({key:0,ref:r.footerContainerRef,class:e.cx("footer")},e.ptm("footer")),[c(e.$slots,"footer")],16)):d("",!0)],64))],16,G)),[[L]]):d("",!0)]}),_:3},16,["onEnter","onAfterEnter","onBeforeLeave","onLeave","onAfterLeave"])],16)):d("",!0)]}),_:3})}g.render=H;const J={class:"about bg-about-banner bg-pattern opacity-30 rotate-3 skew-x-12"},Q={class:"card flex justify-center"},_=j({__name:"AboutView",setup(e){b(12);const n=b(!1);return(t,o)=>(s(),l("div",J,[o[4]||(o[4]=f("h1",null,"This is an about page",-1)),f("div",Q,[w(k(g),{visible:n.value,"onUpdate:visible":o[0]||(o[0]=i=>n.value=i),header:"Drawer"},{default:u(()=>o[2]||(o[2]=[f("p",null,"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",-1)])),_:1},8,["visible"]),w(k(y),{icon:"pi pi-arrow-right",onClick:o[1]||(o[1]=i=>n.value=!0)},{default:u(()=>o[3]||(o[3]=[F("Click")])),_:1})])]))}});export{_ as default};
