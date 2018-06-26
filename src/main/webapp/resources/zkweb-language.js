var i18nCookiename='zkwebLanguage';
var i18nSettings={    
        name:'strings',    //属性文件名     命名格式： 文件名_国家代号.properties  
        path:'resources/locale/',   //注意这里路径是你属性文件的所在文件夹  
        mode:'map',    
        language:$.i18n.normaliseLanguageCode({language:""}), //$.i18n.browserLang(),     //这就是国家代号 name+language刚好组成属性文件名：strings+zh -> strings_zh.properties  
        callback:function(){
       	 setLayoutTitle('north');
       	 setLayoutTitle('west');
       	 setLayoutTitle('east');
       	 setHtmlText("[data-locale-html]","locale-html"); //[data-locale]：获取所有带属性data-locale的文档节点,通过data('locale')获取对应的值
       	 setAttrText("[data-locale-attr]","locale-attr",'title');
       	 setWindowTitle('.easyui-window')
       	 setTabsTitle('.easyui-tabs')
        }
    };
var writeObj=function(obj){ 
    var description = ""; 
    for(var i in obj){   
        var property=obj[i];   
        description+=i+" = "+property+"\n";  
    }   
    return description; 
} 
var getNavLanguage = function(){
	//console.log('navigator.appName='+window.navigator.appName+",navigator.language="+window.navigator.language)
	if(navigator.appName == "Netscape"){
        return $.i18n.normaliseLanguageCode({language:""});
    }
    return "en";
}

var getCookie = function(name, value, options) {
    if (typeof value != 'undefined') { // name and value given, set cookie
        options = options || {};
        if (value === null) {
            value = '';
            options.expires = -1;
        }
        var expires = '';
        if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
            var date;
            if (typeof options.expires == 'number') {
                date = new Date();
                date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
            } else {
                date = options.expires;
            }
            expires = '; expires=' + date.toUTCString(); // use expires attribute, max-age is not supported by IE
        }
        var path = options.path ? '; path=' + options.path : '';
        var domain = options.domain ? '; domain=' + options.domain : '';
        var s = [cookie, expires, path, domain, secure].join('');
        var secure = options.secure ? '; secure' : '';
        var c = [name, '=', encodeURIComponent(value)].join('');
        var cookie = [c, expires, path, domain, secure].join('')
        document.cookie = cookie;
    } else { // only name given, get cookie
        var cookieValue = null;
        if (document.cookie && document.cookie != '') {
            var cookies = document.cookie.split(';');
            for (var i = 0; i < cookies.length; i++) {
                var cookie = jQuery.trim(cookies[i]);
                // Does this cookie string begin with the name we want?
                if (cookie.substring(0, name.length + 1) == (name + '=')) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue;
    }
};
var getLanguageFromCookie=function(language){
	/**
	 * 设置语言类型： 默认为中文
	 */
	var i18nLanguage = "zh_CN";

	/*
	设置一下网站支持的语言种类
	 */
	var webLanguage = ['zh_CN', 'zh_TW', 'en'];
	if (getCookie(i18nCookiename)) {
        i18nLanguage = getCookie(i18nCookiename);
    } else {
    	if(!language)
    		language=i18nSettings.language;
        // 获取浏览器语言
        var navLanguage = language;
        if (navLanguage) {
            // 判断是否在网站支持语言数组里
            var charSize = $.inArray(navLanguage, webLanguage);
            if (charSize > -1) {
                i18nLanguage = navLanguage;
                // 存到缓存中
                getCookie(i18nCookiename,navLanguage);
            };
        } else{
            console.log("not navigator,default is "+i18nLanguage);
            
        }
    }
	return i18nLanguage;
}
var getLanguage=function(){
 var language=getLanguageFromCookie(getNavLanguage());//$.i18n.normaliseLanguageCode({language:""});//window.navigator.language;
 //暂不支持会话信息获取和存放
 //var userLanguage=$(sessionScope["org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE"]);

 //   if(null != userLanguage&&userLanguage!=""){//not login
 //       language = userLanguage;
  //  }
 	console.log('language='+language);
    return language;
}
var setLanguage=function(node){
	language=node.value
	cookielanguage=getCookie(i18nCookiename);
	if (cookielanguage&&cookielanguage!=language) {
		console.log('cookielanguage='+cookielanguage+',newlanguage='+language)
		getCookie(i18nCookiename,language);
    }else if (cookielanguage&&cookielanguage==language){
    	console.log('cookielanguage='+cookielanguage+',newlanguage='+language)
    }else{
    	console.log('cookie['+i18nCookiename+']='+language)
    	getCookie(i18nCookiename,language);
    }
	console.log('active cookie['+i18nCookiename+']='+getCookie(i18nCookiename))
	loadScript(language);
	loadProperties(language);
}

function loadScript(language){
	//resources/easyui/locale/easyui-lang-zh_CN.js
    var src = 'resources/easyui/locale' + '/easyui-lang-'+language.replace("-","_")+'.js';// when login in China the language=zh-CN     
    $.getScript(src,function(response,status){
    	console.log("Script loaded and executed.src="+src+",status="+status);
    });
}
function setLayoutTitle(title){
	try{
    	if($.i18n.prop(title+'_title')){
    		console.log(title+'_title='+$.i18n.prop(title+'_title'))
    		$('#zkweb_body').layout('panel',title).panel('setTitle',$.i18n.prop(title+'_title'));	
    	}else{
    		console.log(title+'_title'+" not found in "+i18nSettings.name+"_"+i18nSettings.language+".propertities");  
    	}       
	}catch(e){
		console.log(title+'_title'+" not found in "+i18nSettings.name+"_"+i18nSettings.language+".propertities");  
		console.log(e.stack);
		
	}
}
function setWindowTitle(attrNameList){
	$(attrNameList).each(function(){  
		//console.log($(this).window('options').title+":"+$(this).attr("id"));
		var varname=$(this).attr("id")+'-win';
		try{
        	if($.i18n.prop(varname)){
        		console.log(varname+"-var="+varname+":"+$.i18n.prop(varname)+",old="+$(this).window('options').title);  
        		$(this).window('setTitle',$.i18n.prop(varname));	
        	}else{
        		console.log(varname+"-var="+varname+" not found in "+i18nSettings.name+"_"+i18nSettings.language+".propertities");  
        	}
    	}catch(e){
    		console.log(varname+"-var="+varname+" not found in "+i18nSettings.name+"_"+i18nSettings.language+".propertities");  
    		console.log(e.stack);
    	}
	});
}
function setTabsTitle(attrNameList){
	$(attrNameList).each(function(){  
		for(var i=0;i<$(this).tabs('tabs').length;i++) {
            var title=$(this).tabs('getTab',i).panel('options').title;
            var tabid=i;
            var varname=$(this).attr("id")+'-'+tabid+'-tab';
            //console.log(title+":"+varname);
            try{
	        	if($.i18n.prop(varname)){
	        		console.log(varname+"-var="+varname+":"+$.i18n.prop(varname)+",old="+title);  
	        		var tab=$(this).tabs('getTab',i);//.panel('setTitle',$.i18n.prop(varname));
	        		$(this).tabs('update', {
	        	        tab: tab,
	        	        options: {
	        	           title: $.i18n.prop(varname)
	        	        }
	        		});
//	        		var titles = $(this).find('.tabs-header:first').find('.tabs-title');
//	        		   titles.eq(0).text($.i18n.prop(varname));
//	        		   titles.eq(1).text('标题1');
//	        		   titles.eq(2).text('标题2');	        		 
	        	}else{
	        		console.log(varname+"-var="+varname+" not found in "+i18nSettings.name+"_"+i18nSettings.language+".propertities");  
	        	}
	    	}catch(e){
	    		console.log(varname+"-var="+varname+" not found in "+i18nSettings.name+"_"+i18nSettings.language+".propertities");  
	    		console.log(e.stack);
	    	}
		}
	});
}
function setHtmlText(attrNameList,attrTrueName){
	$(attrNameList).each(function(){    
    	var locale=$(this).data(attrTrueName);
        if(locale){
        	try{
	        	if($.i18n.prop(locale)){
	        		console.log(attrTrueName+"-var="+locale+":"+$.i18n.prop(locale)+",old(html)="+$(this).html());  
	        		$(this).html($.i18n.prop(locale)); 
	        	}else{
	        		console.log(attrTrueName+"-var="+locale+" not found in "+i18nSettings.name+"_"+i18nSettings.language+".propertities");  
	        	}       
        	}catch(e){
        		console.log(attrTrueName+"-var="+locale+" not found in "+i18nSettings.name+"_"+i18nSettings.language+".propertities"); 
        		console.log(e.stack);
        		
        	}
        }
    }); 
}
function setAttrText(attrNameList,attrTrueName,valueName){
	$(attrNameList).each(function(){    
    	var locale=$(this).data(attrTrueName);
        if(locale){
        	try{
	        	if($.i18n.prop(locale)){
	        		console.log(attrTrueName+"-var="+locale+":"+$.i18n.prop(locale)+","+valueName+"(old,attr)="+$(this).attr(valueName));  
	        		$(this).attr(valueName,$.i18n.prop(locale)); 
	        	}
        	}catch(e){
        		console.log(attrTrueName+"-var="+locale+" not found in "+i18nSettings.name+"_"+i18nSettings.language+".propertities");  
        		console.log(e.name + ": " + e.message);
        		
        	}
        }
    }); 
}
function localeMessager(type,titleKey,titleDefault,msgKey,msgDefault,textKey,textDefault,arg1,arg2){
	var ok=$.i18n.prop('ok')
	if(!ok)ok='Ok'
	var cancel=$.i18n.prop('cancel')
	if(!cancel)cancel='Cancel'
	$.extend($.messager.defaults,{  
		ok:ok,  
		cancel:cancel
	});
	var title=$.i18n.prop(titleKey)
	if(!title)
		title=titleDefault
	var msg=$.i18n.prop(msgKey)
	if(!msg)
		msg=msgDefault
	if(type=='alert'){
		$.messager.alert(title,msg,textKey,textDefault);
	}else if(type=='show'){
		arg1.title=title
		arg1.msg=msg
		$.messager.show(textKey);
	}else if(type=='confirm'){
		$.messager.confirm(title,msg,textKey);
	}else if(type=='prompt'){
		$.messager.prompt(title,msg,textKey);
	}else if(type=='progress'){
		if($.type(arg1)=='object'){
			arg1.title=title
			arg1.msg=msg
			var text=$.i18n.prop(textKey)
			if(!text)
				text=textDefault
			arg1.text=text
		}		
		$.messager.progress(arg1);
	}
}
function loadProperties(language) {   
	i18nSettings.language=language
	$.i18n.properties(i18nSettings);    
 }
$(function(){
	 var language=getLanguage();
	 loadScript(language);
	 loadProperties(language);
	 //alert(writeObj(i18nSettings))
	});



