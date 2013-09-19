/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/


//---------------------------------------------
//#############################################
//  jQuery plugins
//#############################################
//

jQuery.extend({
    postJSON: function(url, data, callback) {
        return jQuery.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify(data),
            success: callback,
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            processData: false
        });
    },
    deleteJSON: function(url, data, callback) {
        return jQuery.ajax({
            type: "DELETE",
            url: url,
            data: JSON.stringify(data),
            success: callback,
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            processData: false
        });
    }    
});

/**
 * extended click function. Provides correct callback with all arguments provided as given to this function
 * 
 * e.g. clickExt(this, myCallback, arg1, arg2, arg3) 
 * will produce a callback like myCallback(event, jqueryItem, arg1, arg2, arg3)
 * with "this" pointing on the object refered with handlerSelf
 * @param handlerSelf normaly the object owning the handlerfunction
 * @param handlerFunction callback function that will be called
 * @param further params that will be forwarded to the handlerFunction
 *
 */
jQuery.fn.extend({
    clickExt: function(handlerSelf, handlerFunction){
        if (!handlerSelf || !handlerFunction ){
            throw "ERROR: clickExt: handlerSelf and handlerFunction must be defined.";
        }
        var callArgs = Array.prototype.slice.call(arguments);
        
        var self=this;     
        this.click(function(e) {
            
            callArgs[0]=e;
            callArgs[1]=self;
            handlerFunction.apply(handlerSelf, callArgs);            
        });
        return this;
    },

    /**
 * extended keyup function. Provides correct callback with all arguments provided as given to this function
 * 
 * e.g. keyupExt(this, myCallback, arg1, arg2, arg3) 
 * will produce a callback like myCallback(event, jqueryItem, arg1, arg2, arg3)
 * with "this" pointing on the object refered with handlerSelf
 * @param handlerSelf normaly the object owning the handlerfunction
 * @param handlerFunction callback function that will be called
 * @param further params that will be forwarded to the handlerFunction
 *
 */

    keyupExt: function(handlerSelf, handlerFunction){
        if (!handlerSelf || !handlerFunction ){
            throw "ERROR: clickExt: handlerSelf and handlerFunction must be defined.";
        }
        var callArgs = Array.prototype.slice.call(arguments);
        
        var self=this;     
        this.keyup(function(e) {
            
            callArgs[0]=e;
            callArgs[1]=self;
            handlerFunction.apply(handlerSelf, callArgs);            
        });
        return this;
    },
    /**
 * extended mouseover function. Provides correct callback with all arguments provided as given to this function
 * 
 * e.g. mouseoverExt(this, myCallback, arg1, arg2, arg3) 
 * will produce a callback like myCallback(event, jqueryItem, arg1, arg2, arg3)
 * with "this" pointing on the object refered with handlerSelf
 * @param handlerSelf normaly the object owning the handlerfunction
 * @param handlerFunction callback function that will be called
 * @param further params that will be forwarded to the handlerFunction
 *
 */

    mouseoverExt: function(handlerSelf, handlerFunction){
        if (!handlerSelf || !handlerFunction ){
            throw "ERROR: clickExt: handlerSelf and handlerFunction must be defined.";
        }
        var callArgs = Array.prototype.slice.call(arguments);
        
        var self=this;     
        this.mouseover(function(e) {
            
            callArgs[0]=e;
            callArgs[1]=self;
            handlerFunction.apply(handlerSelf, callArgs);            
        });
        return this;
    },
    /**
    * extended mouseoutExt function. Provides correct callback with all arguments provided as given to this function
    * 
    * e.g. mouseoutExt(this, myCallback, arg1, arg2, arg3) 
    * will produce a callback like myCallback(event, jqueryItem, arg1, arg2, arg3)
    * with "this" pointing on the object refered with handlerSelf
    * @param handlerSelf normaly the object owning the handlerfunction
    * @param handlerFunction callback function that will be called
    * @param further params that will be forwarded to the handlerFunction
    *
    */
    mouseoutExt: function(handlerSelf, handlerFunction){
        if (!handlerSelf || !handlerFunction ){
            throw "ERROR: clickExt: handlerSelf and handlerFunction must be defined.";
        }
        var callArgs = Array.prototype.slice.call(arguments);
        
        var self=this;     
        this.mouseout(function(e) {
            
            callArgs[0]=e;
            callArgs[1]=self;
            handlerFunction.apply(handlerSelf, callArgs);            
        });
        return this;
    },
    /**
     *  writes the text into the given jQuery node(s), but preserves the children of the node
     *  @param text to be set
     */
    textOnly: function(text){
        
        var childs = this.children();
        this.text(text);
        this.append(childs);

        return this;
    },

    /**
     *  appends an 'a' element with the given parameters
     *  @param targetUrl uri for href attribute
     *  @param caption text added to the a element
     *  @param classes classes added to the a element
     */
    addHrefOpeningInNewWindow:function(targetUrl, caption, classes){
        this.append($('<a/>').addClass(classes)
            .attr('href',targetUrl).text(caption)
            .attr('target','_blank')
            );
        return this;
    }
});





//---------------------------------------------
//#############################################
//  JSTool
//#############################################
//
JSTool = {

    

    isNumber: function(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    },
    
    arrayContainsItem: function(myArray, myItem){
        if (!myArray ||myArray.length===0 || !myItem){
            return false;
        }
      
        for (var i=0;i<myArray.length;i++){
            if (myArray[i]===myItem){
                return true;
            }
        }  
        return false;
    },
    
    countDefinedMembers: function(myObject){
        var count = 0;
        for (var k in myObject){ 
            if ((myObject.hasOwnProperty(k))
                && (myObject[k]!==null)
                ) {                
                count++;
            }
        }
        return count;
    },
    
    getDefinedMembers: function(myObject){
        var result = [];
        for (var k in myObject){ 
            if ((myObject.hasOwnProperty(k))
                && (myObject[k]!==null)
                ) {                
                result.push(myObject[k]);
            }
        }
        return result;
    },
    
    ajaxError: function(x, e) {
        if (x.status === 0) {
            console.log('Check Your Network.', e, x);
        } else if (x.status === 404) {
            console.log('Requested URL not found.', e, x);
        } else if (x.status === 500) {
            console.log('Internal Server Error.', e, x);
        } else {
            console.log('Unknow Error.\n' + x.responseText);
        }
        
        var delay=10;

        //HACK? register comet call in case the last one terminated...
        if (Dime.initProcessor.functions.length===0){
            delay=15000;
            Dime.Navigation.registerCometCall();
        }
        
        
        //HACK check for remaining functions to be executed for initial loading the page
        //wait for delay in case of failed comet call 
        setTimeout(Dime.initProcessor.executeFunctions, delay);
        
        
    },
        
    /**
     * returns UUID-like random ID
     * 
     * @ignore
     * @returns {String}
     */    
    randomGUID: function(){
        
        var S4 = function (){
            return Math.floor(
                Math.random() * 0x10000 /* 65536 */
                ).toString(16);
        };

        return (
            S4() + S4() + "-" +
            S4() + "-" +
            S4() + "-" +
            S4() + "-" +
            S4() + S4() + S4()
            );
    },
    
    /**
     * returns formated Date String from UNIX time millis
     */
    millisToFormatString: function(millis){
        if (!millis){
            return '';
        }
        var d = new Date(millis);
        return d.toDateString();
    },
    
    createHTMLElementString: function(tagName, id, classes, onClick, innerHtml){
        var result = "<"+tagName+" ";
        
        if (id){
            result+='id="'+id+'" ';
        }
                
        if (classes && classes.length>0){
            var classesString="";
            for (var i=0; i<classes.length;i++){
                //skip empty classes
                if (!classes[i] || classes[i].length===0){
                    continue;
                }
                //add space for additional classes
                if (i>0){
                    classesString+=" ";
                }
                classesString+=classes[i];
            }
            result+='class="'+classesString+'" ';
        }
        
        if (onClick){
            result+='onclick="'+onClick+'" ';
        }
        result+=">";
        
        if (innerHtml){
            result+=innerHtml;            
        }
        return result+"</"+tagName+">";
    },
    
    
    createHTMLElement: function(tagName, id, classes, onClick, innerHtml){
        var result = document.createElement(tagName);
        
        if (id){
            result.setAttribute("id", id);
        }
                
        if (classes && classes.length>0){
            var classesString="";
            for (var i=0; i<classes.length;i++){
                if (i>0){
                    classesString+=" ";
                }
                classesString+=classes[i];
            }
            result.setAttribute("class", classesString);
        }
        
        if (onClick){
            var self=this;            
            result.onclick = function(e) {
                onClick.call(self, e, $(this));
            };
        }
        
        if (innerHtml){
            result.innerHTML = innerHtml;            
        }
        return result;
    },
    
    insertChildAtFront: function(parent, child){
        if (!parent.hasChildNodes()){
            parent.appendChild(child);
            return;
        }
        parent.insertBefore(child, parent.firstChild);
    },
    
    removefirstChild: function(parent){        
        parent.removeChild(parent.firstChild);        
    },
    
    removelastChild: function(parent){        
        parent.removeChild(parent.lastChild);        
    },
    
    elementExists: function(elementId){
        var element =  document.getElementById(elementId);
        if (typeof(element) != 'undefined' && element != null){
            return true;
        }
        return false;
    },
    
    updateImageWithId: function(imageId, imageUrl){
        var myImageElement = document.getElementById(imageId);        
        myImageElement.setAttribute("src", imageUrl); 
    },
    
    updateImageWithIdIfExists: function(imageId, imageUrl){
        var myImageElement =  document.getElementById(imageId);
        if (typeof(myImageElement) != 'undefined' && myImageElement != null){
            myImageElement.setAttribute("src", imageUrl); 
            return true;
        }
        return false;
    },
        
    addClassIfNotAdded: function(element, className){
        if (!$(element).hasClass(className)){
            $(element).addClass(className);
        }
    },
    removeClassIfSet: function(element, className){
        if ($(element).hasClass(className)){
            $(element).removeClass(className);
        }
    },
    newInstanceOfObject: function(myObject) {
        var F = function () {} ;
        F.prototype = myObject;
        return new F();
    },
    upCaseFirstLetter: function(str){
        if (!str || str.length<1){
            return "";
        }
        if (str.length===1){
            return str.charAt(0).toUpperCase();
        }
        return str.charAt(0).toUpperCase() + str.slice(1);
    }
};

//---------------------------------------------
//#############################################
//  BSTool
//  tools for bootstrap
//#############################################
//



BSTool={
    
    DropDownEntry: function(handlerSelf, label, callBack){
        this.handlerSelf = handlerSelf;
        this.label = label;
        this.callBack = callBack;
    },
    
    /**
     * @param buttonLabel label of the button
     * @param dropDownEntries array of BSTool.DropDownEntry
     * @return jquery element containing button group with single button and dropdown
     * 
     * <div class="btn-group">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                Action
                <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <!-- dropdown menu links -->
            </ul>
        </div>
     * 
     */
    createDropdown: function(buttonLabel, dropDownEntries, buttonClass, buttonGroupClass){
        var button=$('<a >'+buttonLabel+'</a>')
        .addClass("dropdown-toggle")
        .attr("data-toggle","dropdown")
        .attr("href","#")
        .append($("<span></span>").addClass("caret"));
        
        
        var ul = $("<ul></ul>").addClass("dropdown-menu");
        
        jQuery.each(dropDownEntries, function(){
            var entry = this;
            
            var entryElem = $('<a href="#">'+entry.label+'</a>');
            
            var myCallback=function(){
                button.empty();
                button.append(entry.label);
                
                if (entry.callBack){
                    var callArgs = Array.prototype.slice.call(arguments);        
                    entry.callBack.apply(entry.handlerSelf, callArgs);
                }
            };
            
            
            entryElem.clickExt(entry.handlerSelf, myCallback);
            
            
            var li = $("<li></li>").append(entryElem);
            ul.append(li);
        });
        
        if (buttonClass){
            button.addClass(buttonClass);
        }else{
            button.addClass("btn");
        }
        
        var result = 
        $('<div></div>').addClass("btn-group")
        .append(button)
        .append(ul);
        if (buttonGroupClass){
            result.addClass(buttonGroupClass);
        }
            
        return result;
        
    }
};



//---------------------------------------------
//#############################################
//  UITOOL
//#############################################
// --------------------------------------------
UITool = {

    showHideElement: function(divId, showHide) {
        document.getElementById(divId).style.visibility = showHide;
    },

    showElement: function(divId){
        UITool.showHideElement(divId, 'visible');
    },

    hideElement: function(divId){
        UITool.showHideElement(divId, 'hidden');
    },
    
    removeAllChildElementsForContainer: function(container){
        
        while (container.firstChild) {
            container.removeChild(container.firstChild);
        }
    },
    
    removeAllChildElements: function(containerId){
        var container = document.getElementById(containerId);
        UITool.removeAllChildElementsForContainer(container);
    }
};



//---------------------------------------------
//#############################################
//  PS INIT
//#############################################
// --------------------------------------------
/* 
 *  Description of ps_init
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 04.07.2012
 */

Dime = {};
YES=1;
NO=0;



//---------------------------------------------
//#############################################
//  Dime.Tool
//#############################################
//

Dime.Tool = {
    pipeFunction: function(firstCall, secondCall){
        return secondCall(firstCall);
    },
    /**
    * keep session alive
    * FIXME could be taken out, when sending token with every ajax call
    * FIXME should use a smaller file
    */
    keep_alive: function() {

        jQuery.get(Dime.PsConfigurationClass.uiMainSite, null, function(data, textStatus){
            if (textStatus.toLowerCase()!=='success'){
                window.location=Dime.ps_configuration.getRealBasicUrlString()+'/dime-communications/web/access/login';
            }
        });
    }

};

//---------------------------------------------
//#############################################
//  Dime.initProcessor
//#############################################
//


/*
 * when using the init processor make sure that all 
 * functions registered finally call the callback function somehow
 */
Dime.initProcessor = {
    functions:[],
    registerFunction: function(myFunction){
        Dime.initProcessor.functions.push(myFunction);
    },
    executeFunctions:function(){
 
        if (Dime.initProcessor.functions.length>0){
            var myFunction = Dime.initProcessor.functions[0];
            Dime.initProcessor.functions.splice(0,1);
            try{
                myFunction(Dime.initProcessor.executeFunctions);
            }catch(e){
                console.log("Exception:",e,"\nwhen executing: "+myFunction);
            }
        }        
    }
};

/**
 * handle AjaxErrors nicely
 * 
 * @param {function} callback callback function as part of initProcessor mechanism
 */
Dime.initProcessor.registerFunction(function(callback){
    $.ajaxSetup({
        error: JSTool.ajaxError
    });
    callback();
});


//---------------------------------------------
//#############################################
//  PS MAP
//#############################################
// --------------------------------------------


//OUTDATED INFORMATION: TODO update - check whats actually used!
//default models: these models are all handled the same when fetching
//non-default models: some models require special handling(livepost is typically @all and notification requires also callback to retrieve)

Dime.PSMap = function(itemType, caption, parentType, childType, image, path, isDefault, isGenItemModel, pluralCaption){
    this.itemType = itemType;
    this.caption = caption;
    this.parentType = parentType;
    this.childType = childType; 
    this.image = image; 
    this.path = path;
    this.isDefault = isDefault;
    this.isGenItemModel = isGenItemModel;
    this.pluralCaption = pluralCaption;

};

Dime.CallTypeMap = function(callType, callString){
    this.callType = callType;
    this.callString = callString;
};


Dime.psMap = {
    TYPE:{
        GROUP: 'group',
        PERSON: 'person',
        DATABOX: 'databox',
        RESOURCE: 'resource',        
        LIVESTREAM: 'livestream',
        LIVEPOST: 'livepost',
        NOTIFICATION: 'notification',
        PROFILE: 'profile',
        PROFILEATTRIBUTE: 'profileattribute',
        SITUATION: 'situation',
        EVENT: 'event',
        SERVICEADAPTER: 'serviceadapter',
        DEVICE: 'device',
        CONTEXT: 'context',
        PLACE: 'place',
        ACCOUNT: 'account',
        USERNOTIFICATION:'usernotification'
    },
    CALLTYPE: {
        AT_ALL_GET: 'AT_ALL_GET', //"@all", 
        AT_ITEM_GET: 'AT_ITEM_GET', //"{guid}", 
        AT_ITEM_POST_NEW: 'AT_ITEM_POST_NEW', //"", 
        AT_ITEM_POST_UPDATE: 'AT_ITEM_POST_UPDATE', // "{guid}", 
        AT_ITEM_DELETE: 'AT_ITEM_DELETE', // "{guid}", 
        COMET: 'COMET', //"@comet", 
        DUMP: 'DUMP', //"@dump", 
        AT_ALL_ALL_GET: 'AT_ALL_ALL_GET', //global "@all"
        SHARE_QUERY: 'SHARE_QUERY', //creates a query for @all shared things to a person
        ADVISORY:'ADVISORY'//path for advisory call
    }, 
    UN_TYPE: {
        MERGE_RECOMMENDATION: 'merge_recommendation',
        SITUATION_RECOMMENDATION: 'situation_recommendation',
        ADHOC_GROUP_RECOMMENDATION: 'adhoc_group_recommendation', 
        REF_TO_ITEM: 'ref_to_item',
        MESSAGE: 'message', 
        UNDEFINED: 'UNDEFINED'
    },
    map: {},
    callTypeMap: {},
    unTypeMap:{},
    
    getInfoHtmlForType: function(type){
        return "Detail view for "+Dime.psHelper.getCaptionForItemType(type)
        +"<br/><br/>In this dialog, you can see (and edit) the properties of this item...";
    }


};

/*
 * add the provided object to Dime.psMap.map
 * @param {object} typeMap initially used to construct Dime.psMap.map
 */
Dime.psMap.addToTypeMap = function(typeMap) {
    Dime.psMap.map[typeMap.itemType] = typeMap;
};


//itemType, caption, parentType, childType, image, path, isDefault, isGenItemModel, pluralCaption
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.GROUP, "group", 0, Dime.psMap.TYPE.PERSON, 'group.png', 'group', YES, YES, 'groups'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.PERSON, "person", Dime.psMap.TYPE.GROUP, 0, 'person.png', 'person', YES, YES, 'persons'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.DATABOX, "databox", 0, Dime.psMap.TYPE.RESOURCE, 'data_box.png', 'databox', YES, YES, 'databoxes'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.RESOURCE, "document", Dime.psMap.TYPE.DATABOX, 0,  'resource.png', 'resource', YES, YES, 'documents'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.LIVESTREAM, "livestream", 0, Dime.psMap.TYPE.LIVEPOST,  'resource.png', 'livestream', NO, NO, 'livestreams'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.LIVEPOST, "livepost", Dime.psMap.TYPE.LIVESTREAM, 0, 'livepost.png', 'livepost', NO, YES, 'liveposts'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.NOTIFICATION, "notification", 0, 0,  'notification.png', 'notification', NO, YES, 'notification'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.PROFILE, "profile card", 0, Dime.psMap.TYPE.PROFILEATTRIBUTE, 'profileCard.png', 'profile', YES, YES, 'profile cards'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.PROFILEATTRIBUTE, "profile detail", Dime.psMap.TYPE.PROFILE, 0, 'profileCard.png', 'profileattribute', YES, YES, 'profile details'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.SITUATION, "situation", 0, 0,  'situation_general.png', 'situation', YES, YES, 'situations'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.EVENT, "calendar entry", 0, 0,  'resource.png', 'event', YES, YES, 'calendar entries'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.SERVICEADAPTER, "service", 0, 0,  'resource.png', 'serviceadapter', YES, YES, 'services'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.DEVICE, "device", 0, 0,  'resource.png', 'device', NO, YES, 'devices'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.CONTEXT, "context", 0, 0, 'resource.png', 'context', NO, NO, 'context'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.PLACE, "place", 0, 0, 'resource.png', 'place', NO, YES, 'places'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.ACCOUNT, "account", 0, 0, 'resource.png', 'account', NO, YES, 'accounts'));
Dime.psMap.addToTypeMap(new Dime.PSMap(Dime.psMap.TYPE.USERNOTIFICATION, "notification", 0, 0, 'notification.png', 'usernotification', NO, YES, 'notifications'));

/**
 * add the provided object to Dime.psMap.callTypeMap
 * @param {object} callTypeMap initially used to construct Dime.psMap.callTypeMap
 */
Dime.psMap.addToCallTypeMap = function(callTypeMap) {
    Dime.psMap.callTypeMap[callTypeMap.callType] = callTypeMap;
};



Dime.psMap.addToCallTypeMap(new Dime.CallTypeMap(Dime.psMap.CALLTYPE.AT_ALL_GET, "@all"));
Dime.psMap.addToCallTypeMap(new Dime.CallTypeMap(Dime.psMap.CALLTYPE.AT_ITEM_GET, "{guid}"));
Dime.psMap.addToCallTypeMap(new Dime.CallTypeMap(Dime.psMap.CALLTYPE.AT_ITEM_POST_NEW, ""));
Dime.psMap.addToCallTypeMap(new Dime.CallTypeMap(Dime.psMap.CALLTYPE.AT_ITEM_POST_UPDATE, "{guid}"));
Dime.psMap.addToCallTypeMap(new Dime.CallTypeMap(Dime.psMap.CALLTYPE.AT_ITEM_DELETE, "{guid}"));
Dime.psMap.addToCallTypeMap(new Dime.CallTypeMap(Dime.psMap.CALLTYPE.COMET, "@comet"));
Dime.psMap.addToCallTypeMap(new Dime.CallTypeMap(Dime.psMap.CALLTYPE.DUMP, "@dump"));
        



Dime.psMap.length = Dime.psMap.map.length;




/**
 * Returns the first element of Dime.psMap.map that has the specified value 
 * set for the key given  (entry[key]===value)
 * 
 * E.g.: console.log('Dime.psMap.map: image==situation_general.png',
 *        Dime.psMap.getElementWithKey('image', 'situation_general.png'));
 *
 * @param {String} key 
 * @param {String} value 
 *
 */
Dime.psMap.getElementWithKey = function(key, value){
    for (var entryKey in Dime.psMap.map){
        var entryValue =  Dime.psMap.map[entryKey];
        if (entryValue[key]===value){
            return entryValue;
        }
    }
 
    return null;
};

//
//console.log('Dime.psMap.map: image==resource.png',
//    Dime.psMap.getElementWithKey('image', 'resource.png'));

/**
 * Map of Categories for ProfileAttributes
 * 
*/
Dime.PA_CATEGORY_MAP = {
   
    PERSON_NAME: {
        name: "PersonName",
        caption: "Name",
        keys: ["nameHonorificSuffix", "nameFamily", "nameHonorificPrefix", "nameAdditional", "nameGiven", "nickname", "fullname"],
        keyCaptions: ["Suffix", "Family Name", "Title", "Middle Name", "First Name", "Nickname", "Full Name"]
    },
//    NAME: { //only internally
//        name: "Name",
//        caption: "Name",
//        keys: ["nickname", "fullname"]
//    },
    BIRTH_DATE: {
        name: "BirthDate",
        caption: "Birthday",
        keys: ["birthDate"],
        keyCaptions: ["Date"]
    },
    EMAIL_ADDRESS: {
        name: "EmailAddress",
        caption: "Email",
        keys: ["emailAddress"],
        keyCaptions: ["Address"]
    },
    PHONE_NUMBER: {
        name: "PhoneNumber",
        caption: "Phone",
        keys: ["phoneNumber"],
        keyCaptions: ["Number"]
    },
    POSTAL_CODE: {
        name: "PostalAddress",
        caption: "Address",
        keys: ["region", "country", "extendedAddress", "addressLocation", "streetAddress", "postalcode", "locality", "pobox"],
        keyCaptions: ["Region", "Country", "Addition to Address", "Location", "Street", "Postal Code", "Locality", "PO Box"]
    },
    AFFILIATION: {
        name: "Affiliation",
        caption: "Affiliation",
        keys: ["department", "org", "title", "role"],
        keyCaptions: ["Department", "Organisation", "Job Description", "Role"]
    },
    INTERESTS: {
        name: "Hobby",
        caption: "Hobby",
        keys: ["hobby"],
        keyCaptions: ["Hobby"]
    }
    
};

Dime.PACategory={
    getDefaultCategory:function(){
        return Dime.PA_CATEGORY_MAP.PERSON_NAME;
    },
    
    getListOfCategories: function(){
        return JSTool.getDefinedMembers(Dime.PA_CATEGORY_MAP);
    },
    getCategoryByName: function(name){
        var categories = JSTool.getDefinedMembers(Dime.PA_CATEGORY_MAP);
        for (var i=0;i<categories.length;i++){
            if (categories[i].name===name){
                return categories[i];
            }
        }
        return null;
    },
    getCategoryByCaption: function(caption){
        var categories = JSTool.getDefinedMembers(Dime.PA_CATEGORY_MAP);
        for (var i=0;i<categories.length;i++){
            if (categories[i].caption===caption){
                return categories[i];
            }
        }
        return null;
    }
};

/*
 *
 * "nao:includes":[{
			"saidSender":"de713715-b133-494d-b159-ee3e64bd2048",
			 "groups":[],
			 "persons":[{
					"personId":"5f23dc00-33b6-4c31-a44f-a43cb24893fb",
					 "saidReceiver":null}],
			 "services":[]}],
	 "nao:excludes":[]}
 *
 **/

Dime.ACLPerson=function(){
    this.personId=""; //GUID of person
    this.saidReceiver= null;
};

Dime.ACLPackage=function(){
    this.saidSender= "";
    this.groups=[]; //guids of groups
    this.persons=[]; // Dime.ACLPerson
    this.services= []; //GUIDs of accessing services
};

Dime.ExtendedACLPackage=function(){
    this.saidSender= "",
    this.groups=[], //guids of groups
    this.persons=[], // Dime.ACLPerson
    this.services= [], //GUIDs of accessing services
    this.groupItems=[], //group objects
    this.personItems=[], //person objects
    this.serviceItems= []; // account objects ?? //TODO - recheck!
};


/* ////////////////////////////////////////////////////////
 *  PRIVACY TRUST ADVISORY
 * ////////////////////////////////////////////////////////
 */

Dime.privacyTrust={
    LEVELS: [{
        pCaption:"low",
        tCaption:"low",
        limit: 0.0,
        pClass: "privacyLevelLow",
        tClass: "trustLevelLow",
        pClassThin: "privacyLevelLowThin",
        tClassThin: "trustLevelLowThin"
    },
    {
        pCaption:"medium",
        tCaption:"medium",
        limit: 0.5,
        pClass: "privacyLevelMedium",
        tClass: "trustLevelMedium",
        pClassThin: "privacyLevelMediumThin",
        tClassThin: "trustLevelMediumThin"
    },
    {
        pCaption:"high",
        tCaption:"high",
        limit: 1.0,
        pClass: "privacyLevelHigh",
        tClass: "trustLevelHigh",
        pClassThin: "privacyLevelHighThin",
        tClassThin: "trustLevelHighThin"
    }
    ],
    
    
    hasPrivTrust: function(item){
        return (item.hasOwnProperty("nao:privacyLevel")) 
        || (item.hasOwnProperty("nao:trustLevel"));
    },
    
    isPrivacy: function(item){
        return (!Dime.psHelper.isAgentType(item.type));
    },
    
    getLevels: function(item){
        var result=[];
        var isPrivacy = this.isPrivacy(item);
        for (var i=0;i<this.LEVELS.length;i++){
            if (isPrivacy){
                result.push({
                    caption:this.LEVELS[i].pCaption,
                    limit: this.LEVELS[i].limit,
                    classString: this.LEVELS[i].pClass,
                    thinClassString: this.LEVELS[i].pClassThin,
                    isPrivacy:true
                });
            }else{
                result.push({
                    caption:this.LEVELS[i].tCaption,
                    limit: this.LEVELS[i].limit,
                    classString: this.LEVELS[i].tClass,
                    thinClassString: this.LEVELS[i].tClassThin,
                    isPrivacy:false
                });
            }
        }
        return result;
    },
    
    updatePrivacyTrust: function(item, limit){
        if (this.isPrivacy(item)){
            item["nao:privacyLevel"]=limit;            
        }else{
            item["nao:trustLevel"]=limit;
        }   
    },
    
    getClassAndCaptionForPrivacyTrust: function(level, isPrivacy){
        for (var i=0;i<this.LEVELS.length;i++){
            if (level<=this.LEVELS[i].limit){
                if (isPrivacy){
                    return {
                        caption: this.LEVELS[i].pCaption,
                        limit: this.LEVELS[i].limit,
                        classString: this.LEVELS[i].pClass,
                        thinClassString: this.LEVELS[i].pClassThin,
                        isPrivacy:true
                    };
                    
                }else{
                    return {
                        caption: this.LEVELS[i].tCaption,                        
                        limit: this.LEVELS[i].limit,
                        classString: this.LEVELS[i].tClass,
                        thinClassString: this.LEVELS[i].tClassThin,
                        isPrivacy:false
                    };
                }
            }
        }
        return null;
    },
    getClassAndCaptionForPrivacyTrustFromItem: function(item){
        if(!this.hasPrivTrust(item)){
            return {
                caption: "no trust or privacy level defined",                
                limit: -1,
                classString: "",
                thinClassString: "",
                isPrivacy:false
            };
        }
        
        if (this.isPrivacy(item)){
            return this.getClassAndCaptionForPrivacyTrust(item["nao:privacyLevel"], true);            
        }else{
            return this.getClassAndCaptionForPrivacyTrust(item["nao:trustLevel"], false);
        }        
    }
    
    
};


Dime.AdvisoryRequest=function(profileGuid, agentGuids, shareableItems){
    this.profileGuid=profileGuid;
    this.agentGuids=agentGuids;
    this.shareableItems=shareableItems;    
};


Dime.AllMyDataContainer=function(){
};
Dime.AllMyDataContainer.prototype={
    callbackCounter:7,

    loadForType: function(type){
        var handleResult=function(response){
            this[type]=response;

            this.callbackCounter--;
            if (this.callbackCounter===0){
                this.callBack.call(this.callerRef);
            }
        };
        Dime.REST.getAll(type, handleResult, "@me", this);
    },

    load:function(callBack, callerRef){
        this.callBack = callBack;
        this.callerRef = callerRef;

        this.loadForType(Dime.psMap.TYPE.GROUP);
        this.loadForType(Dime.psMap.TYPE.PERSON);
        this.loadForType(Dime.psMap.TYPE.DATABOX);
        this.loadForType(Dime.psMap.TYPE.RESOURCE);
        this.loadForType(Dime.psMap.TYPE.LIVEPOST);
        this.loadForType(Dime.psMap.TYPE.PROFILE);
        this.loadForType(Dime.psMap.TYPE.PROFILEATTRIBUTE);
    }
};

Dime.AdvisoryItem=function(item){
    if (!item){
        throw "item is required!"
    }
    
    this.item=item;//store the whole item - so no further parsing is required
    
};


Dime.AdvisoryItem.prototype={
    
    WARNING_TYPES:{
        "untrusted":{
            name: "Privacy risk!",
            getMessage:function(attributes, selectedReceivers, selectedItems, allMyData){
                return '<table><tr><td  class="warningDlgAdvHeading">'
                    + this.getPrivacyLevelText(attributes.privacyValue)
                    + " privacy:</td></tr><tr><td>"
                    + this.getFormatedNamesOfGuids(attributes.privateResources, selectedItems, [Dime.psMap.TYPE.DATABOX, Dime.psMap.TYPE.RESOURCE], allMyData)
                    + '</td></tr><tr><td class="warningDlgAdvHeading">'
                    + this.getTrustLevelText(attributes.trustValue)
                    + " trust:</td></tr><tr><td>"
                    + this.getFormatedNamesOfGuids(attributes.untrustedAgents, selectedReceivers, [Dime.psMap.TYPE.GROUP, Dime.psMap.TYPE.PERSON], allMyData)
                    + "</td></tr></table>";
            }
            
        }, 
        "disjunct_groups":{
            name: "Sharing outside usual group!",
            getMessage:function(attributes, selectedReceivers, selectedItems, allMyData){
                var personString = this.getFormatedNamesOfGuids(attributes.concernedPersons, selectedReceivers, [Dime.psMap.TYPE.PERSON], allMyData);
                var groupString = this.getFormatedNamesOfGuids(attributes.previousSharedGroups, selectedReceivers, [Dime.psMap.TYPE.GROUP], allMyData);
                return '<table><tr><td class="warningDlgAdvHeading" >items:</td></tr><tr><td>'
                + this.getFormatedNamesOfGuids(attributes.concernedResources, selectedItems, [Dime.psMap.TYPE.DATABOX, Dime.psMap.TYPE.RESOURCE], allMyData)
                + '</td></tr><tr><td class="warningDlgAdvHeading">previous recipients:</td></tr><tr><td>'
                +  groupString
                + ((groupString.length>0 && personString.length>0)?"</td></tr><tr><td>":"")
                +  personString
                + "</td></tr></table>";
            }
        }, 
        "unshared_profile":{
            name: "Revealing profile card!",
            getMessage:function(attributes, selectedReceivers, selectedItems, allMyData){
                return '<table><tr><td class="warningDlgAdvHeading">The profile card was never shared with:</td></tr><tr><td>'
                + this.getFormatedNamesOfGuids(attributes.personGuids, selectedReceivers, [Dime.psMap.TYPE.GROUP, Dime.psMap.TYPE.PERSON], allMyData)
                + "</td></tr></table>";
            }
        }, 
        "too_many_resources":{
            name: "Many items shared!",
            getMessage:function(attributes, selectedReceivers, selectedItems, allMyData){
                return "More then  "
                + attributes.numberOfResources
                + " items selected!";
            }
        }, 
        "too_many_receivers":{
            name: "Many recipients selected!",
            getMessage:function(attributes, selectedReceivers, selectedItems, allMyData){
                return "More then "
                + attributes.numberOfReceivers
                + " recipients selected!";
            }
        }
    },
    
    getWarningLevel: function(){
        return this.item.warningLevel;
    },
    
    getWarningType: function(){
        return this.item.type;
    },
    
    getAttributes: function(){
        return this.item;
    },
    
    getTypeText:function(){
        return this.WARNING_TYPES[this.getWarningType()].name;
    },
    
    getFormatedNamesOfGuids: function(guids, items, types, allMyData){

        var result = "";
        for (var i=0;i<guids.length;i++){

            var myName=null;
            var inContainer=null;
            jQuery.each(items, function(){
                if (this.guid===guids[i]){
                    myName=this.name;
                }else if (this.items && this.items.length>0){
                    for(var k=0;k<this.items.length;k++){
                        if (this.items[k]===guids[i]){
                            inContainer=this;
                        }
                    }
                }
            });
            if (!myName){//try all names and all types given
                for (var j=0;j<types.length;j++){
                    jQuery.each(allMyData[types[j]], function(){
                        if (this.guid===guids[i]){
                            myName=this.name;
                        }
                    });
                }
            }
            
            if(myName){
                if (i>0){
                    result+="<br/>";
                }
                result+=myName;
                if (inContainer){
                    result+= ' ('+inContainer.name+')';
                }
            }else{
                window.alert("Unable to find item with guid:"+guids[i]);
            }

        }
        return result;
    },
    
    getPrivacyLevelText: function(privacyValue){
        return Dime.privacyTrust.getClassAndCaptionForPrivacyTrust(privacyValue, true).caption;
    },
    
    getTrustLevelText: function(trustValue){
        return Dime.privacyTrust.getClassAndCaptionForPrivacyTrust(trustValue, false).caption;
    },

    getIconForWarning: function(){
        var result= $('<img/>');
        if (this.getWarningLevel() <= 0.5){

            result.attr('src','img/warn/share_state_severe.png');
        }else{
            result.attr('src','img/warn/share_state_critical.png');
        }
        return result;
    },
    
    getTextForWarning: function(selectedReceivers, selectedItems, allMyData) {
        var attributes = this.getAttributes();

        var message
            = this.WARNING_TYPES[this.getWarningType()].getMessage.call(this, attributes, selectedReceivers, selectedItems, allMyData);
        
       
        return message;
    }
};

Dime.evaluation={

    getViewStackItemByGroupType: function(groupType, viewType){
        //https://confluence.deri.ie:8443/display/digitalme/SET+%28Self+Evaluation+Tool%29+Specs#SET%28SelfEvaluationTool%29Specs-Viewstack
        
        if (viewType===DimeView.SETTINGS_VIEW){
            return "Settings";
        }
        if (viewType===DimeView.PERSON_VIEW){
            return "Person_Detail";
        }
        if (groupType===Dime.psMap.TYPE.GROUP){
            return "People";
        }else if (groupType===Dime.psMap.TYPE.DATABOX){
            return "Data";
        }else if (groupType===Dime.psMap.TYPE.PROFILE){
            return "Myprofile";
        }else if (groupType===Dime.psMap.TYPE.LIVESTREAM){
            return "Communication";
        }else if (groupType===Dime.psMap.TYPE.SITUATION){
            return "Situations";
        }else if (groupType===Dime.psMap.TYPE.PLACE){
            return "Place";
        }
        return 'undefined';
    },

    createEmptyInvolvedItems: function(){
          return {
            "profile":0,
            "profileattribute":0,
            "person":0,
            "group":0,
            "databox":0,
            "livepost":0,
            "account":0
        };
    },

    createInvolvedItems: function(profiles, profileattributes, persons,
        groups, databoxes, liveposts, accounts){
        return {  
            "profile":profiles,
            "profileattribute":profileattributes,
            "person":persons,
            "group":groups,
            "databox":databoxes,
            "livepost":liveposts,
            "account":accounts
        };
    },

    createViewStack: function(){
        return [];
    },


   createEvaluationItem: function(evaluationId, viewStack, action, involvedItems){
       return {
            "guid": JSTool.randomGUID(),
            "type": "evaluation",
            "created": new Date().getTime(),
            "tenantId": evaluationId, //evaluation id from user call
            "clientId":"0.01",
            "viewStack":viewStack,
            "action": action,
            "currPlace":"unknown",
            "currSituationId":"unknown",
            "involvedItems":involvedItems
        };
    },
            
    updateViewStack: function(groupType, viewType){
        if (!Dime.ps_configuration.viewStack){
            Dime.ps_configuration.viewStack=this.createViewStack();
            //store initial evaluation
            var action = "navigate_search_web_UI";
            this.createAndSendEvaluationItemForAction(action);
        }
        Dime.ps_configuration.viewStack.push(this.getViewStackItemByGroupType(groupType, viewType));
    },

    createAndSendEvaluationItemForAction: function(action){
        //if evaluation is enabled
        if (Dime.ps_configuration.userInformation.evaluationDataCapturingEnabled){
            var evaluationItem = this.createEvaluationItem(Dime.ps_configuration.userInformation.evaluationId,
                Dime.ps_configuration.viewStack, action, this.createEmptyInvolvedItems());
            Dime.REST.postEvaluation(evaluationItem);
        }
    }
};


//---------------------------------------------
//#############################################
//  USERNOTIFICATION
//#############################################
// --------------------------------------------


Dime.un={
    unOperations:{
        'shared':{
            getCaption: function(entry){
                return JSTool.upCaseFirstLetter(Dime.psHelper.getCaptionForItemType(entry.unEntry.type))+ " has been shared:";
            },
            imageUrl: 'img/metaData/sharedWith.png',
            operationName: "Shared",
            getShortCaption: function(entry){
                return "shared: "+Dime.psHelper.getCaptionForItemType(entry.unEntry.type);
            }
        },
        'unshared':{
            getCaption: function(entry){
                return JSTool.upCaseFirstLetter(Dime.psHelper.getCaptionForItemType(entry.unEntry.type))+ " has been unshared:";
            },
            imageUrl: 'img/metaData/sharedWith.png',
            operationName: "Unshared",
            getShortCaption: function(entry){
                return "unshared: "+Dime.psHelper.getCaptionForItemType(entry.unEntry.type);
            }
        },
        'inc_priv':{
            getCaption: function(entry){
                return "Please consider to increase the privacy level for";
            },
            imageUrl: 'img/metaData/sharedWith.png',
            operationName: "Increase privacy",
            getShortCaption: function(entry){
                return "privacy: "+Dime.psHelper.getCaptionForItemType(entry.unEntry.type);
            }
        },
        'dec_priv':{
            getCaption: function(entry){
                return "Please consider to deincrease the privacy level for";
            },
            imageUrl: 'img/metaData/sharedWith.png',
            operationName: "Decrease privacy",
            getShortCaption: function(entry){
                return "privacy: "+Dime.psHelper.getCaptionForItemType(entry.unEntry.type);
            }
        },
        'inc_trust':{
            getCaption: function(entry){
                return "Please consider to increase the trust level for";
            },
            imageUrl: 'img/metaData/sharedWith.png',
            operationName: "Increase trust",
            getShortCaption: function(entry){
                return "trust: "+Dime.psHelper.getCaptionForItemType(entry.unEntry.type);
            }
        },
        'dec_trust':{
            getCaption: function(entry){
                return "Please consider to decrease the trust level for";
            },
            imageUrl: 'img/metaData/sharedWith.png',
            operationName: "Decrease trust",
            getShortCaption: function(entry){
                return "trust: "+Dime.psHelper.getCaptionForItemType(entry.unEntry.type);
            }
        }
    },
            
    getCaptionImageUrl:function(entry){
        
        if (entry.unType===Dime.psMap.UN_TYPE.REF_TO_ITEM){
            var operationEntry = this.unOperations[entry.unEntry.operation];
            return {
                caption: operationEntry.getCaption(entry),
                imageUrl: operationEntry.imageUrl,
                operation: entry.unEntry.operation,
                operationName: operationEntry.operationName,
                childName: entry.unEntry.name+"("+entry.unEntry.type+")",
                shortCaption: operationEntry.getShortCaption(entry)
            };

        }else if (entry.unType===Dime.psMap.UN_TYPE.MERGE_RECOMMENDATION){
            return {
                caption: 'These contacts have similar profile informations. You may merge them to one person.',
                imageUrl: 'img/metaData/merge_person.png',
                operation: 'merge',
                operationName: 'Merge',
                childName: entry.unEntry.sourceName +' and '+entry.unEntry.targetName,
                shortCaption: "merge suggestion"
            };
        }else{
            return{
                caption: entry.unType,
                imageUrl: Dime.psHelper.getImageUrlForItemType(entry.type),
                operation: "",
                operationName: "",
                childName: "",
                shortCaption: entry.unType
            };
        }
    }

};



//---------------------------------------------
//#############################################
//  PS CONFIGURATION
//#############################################
// --------------------------------------------

Dime.ps_static_configuration = {
    JUAN_MAIN_SAID: "juan",
    ME_OWNER: "@me",
    DEFAULT_HOSTNAME: "127.0.0.1",
    DEFAULT_PORT: 8080
         
};


Dime.PsConfigurationClass = function(mainSaid, hostname, port, useHttps){
    this.mainSaid = mainSaid; //==username
    this.hostname = hostname;
    this.port = port;
    this.useHttps = useHttps;    
    this.serverPath = '/dime-communications/api/dime/rest';   
    this.deviceGuid = JSTool.randomGUID();
    this.startTime = new Date().getTime();
    this.serverInformation={}; //will be set in the initRegister call
    this.userInformation={};//will be set in the initRegister call
    
    //old config from segovia
    this.uiMainSite = '/dime-communications/static/ui/dime/index.html';
    this.uiPath = '/dime-communications/static/ui/dime';
    this.iconPath = '/dime-communications/static/ui/dime/icons';
    this.uiImagePath = '/dime-communications/static/ui/images';
    this.createNavigation = true;
    
    //    this.uiMainSite = '/net/static/dime/index.html';
    //    this.uiPath = '/net/static/dime/';
    //    this.iconPath = '/net/static/dime/icons';
    //    this.uiImagePath = '/net/static/dime/img';
    
    this.getRealBasicUrlString = function(){
        var result = this.useHttps ? "https://" : "http://";
        return result + this.hostname + ":"+this.port; 
    };
   
    this.getBasicUrlString = function(){
        var result = this.useHttps ? "https://" : "http://";
        return result + this.hostname + ":"+this.port+this.serverPath;    
    };

    this.getUserUrlString=function(){
        return this.getBasicUrlString()
        +'/'
        + encodeURIComponent(this.mainSaid);
    };

    this.getQuestionairePath=function(){
        return '/dime-communications/web/access/questionaire';
    };
    
    this.handleMainSaidRetrieve = function(response){ 
        console.log("received mainSAID:", response);
        this.mainSaid = response;
    };
    
    
    
    this.toString = function(){
        return 'mainSAID: ' + this.mainSaid
        +'\nhostname: '+ this.hostname
        +'\nport: '+ this.port
        +'\nuseHttps: '+ this.useHttps
        +'\nserverPath: '+ this.serverPath
        +'\niconPath: '+ this.iconPath;
    };
};
   

Dime.ps_configuration = new Dime.PsConfigurationClass(
    Dime.ps_static_configuration.JUAN_MAIN_SAID,
    Dime.ps_static_configuration.DEFAULT_HOSTNAME,
    Dime.ps_static_configuration.DEFAULT_PORT,
    false
    );
    
/*
 * analyse URL to find out about port and base URL etc.
 */
Dime.initProcessor.registerFunction(function(callback){
    var myUrl = document.URL;
    var splittedURL = myUrl.split(/\/+/g);
    var defaultPort=80;
         
    for (var i = 0; i < splittedURL.length; i++) {
            
        if (!splittedURL[i]){
        //do nothing
        } else if(splittedURL[i]==='http:'){
            Dime.ps_configuration.useHttps = false;
        }else if(splittedURL[i]==='https:'){
            Dime.ps_configuration.useHttps = true;
            defaultPort=443;
        }else{
            var hostNamePort = splittedURL[i].split(':');
            Dime.ps_configuration.hostname = hostNamePort[0];
            Dime.ps_configuration.port = (hostNamePort[1]?hostNamePort[1]:defaultPort);
            callback();
            return;
        }
    }
    callback();
});

/**
 * retrieve mainSAID from call
 */
Dime.initProcessor.registerFunction(function(){
    //  /dime-communications/web/access/auth/@me
    //TODO adapt to new path /dime/rest/{said}/user/...
    var path = Dime.ps_configuration.getRealBasicUrlString() + "/dime-communications/web/access/auth/@me";
    console.log(path);
    var doubleCallBack = function (response){
        Dime.ps_configuration.handleMainSaidRetrieve(response);
        Dime.initProcessor.executeFunctions(); //HACK ? call execute again to trigger next call
    };
        
    $.get(path, null , doubleCallBack, "text");
});
Dime.initProcessor.registerFunction( function(callBack){
    console.log("Dime.ps_configuration: ",Dime.ps_configuration);
    callBack();
});




//---------------------------------------------
//#############################################
//  DEMO DATA
//#############################################
// --------------------------------------------

DEMO_DATA = {
    IMAGES: [
    "/dime-communications/static/ui/dime/img_demo/items/4.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/12.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/19.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/17.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/18.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/5.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/11.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/0.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/3.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/9.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/14.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/13.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/15.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/7.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/10.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/8.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/6.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/2.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/16.jpg",
    "/dime-communications/static/ui/dime/img_demo/items/1.jpg",
    "/dime-communications/static/ui/dime/img_demo/Hochseilgarten.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen39.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen32m.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen35.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen41.jpg",
    "/dime-communications/static/ui/dime/img_demo/Messe.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen34.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen23m.jpg",
    "/dime-communications/static/ui/dime/img_demo/HotelOchsen.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen26w.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen31w.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen33m.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen37.jpg",
    "/dime-communications/static/ui/dime/img_demo/Fernsehturm.jpg",
    "/dime-communications/static/ui/dime/img_demo/Flugzeug.jpg",
    "/dime-communications/static/ui/dime/img_demo/Kantine.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen30w.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen28.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen40.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen36.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen22w.jpg",
    "/dime-communications/static/ui/dime/img_demo/Empfangsdamen.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen27.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen38.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen24m.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen42.jpg",
    "/dime-communications/static/ui/dime/img_demo/digitalSphere.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen25m.jpg",
    "/dime-communications/static/ui/dime/img_demo/Pizzeria Bologna.jpg",
    "/dime-communications/static/ui/dime/img_demo/Personen29.jpg"]
};
        




//---------------------------------------------
//#############################################
//  PS HELPER
//#############################################
// --------------------------------------------

Dime.psHelper = {
    
   
    
    getParentType: function(type){
        
        var typeMap = Dime.psMap.map[type];
        return typeMap.parentType;      
    },
    
    isParentType: function(type){
        return (Dime.psMap.map[type].childType!==0);
    },
    
    isAgentType: function(type){
        return (type===Dime.psMap.TYPE.GROUP) || (type===Dime.psMap.TYPE.PERSON) || (type===Dime.psMap.TYPE.ACCOUNT);
    },

    isSharableAgent: function(agent){
        if (!Dime.psHelper.isAgentType(agent.type)){
            return false;
        }
        return ((agent.type!==Dime.psMap.TYPE.PERSON)
            || (agent.defProfile && agent.defProfile.length >0));

    },
   
    isShareableType: function(type){
                
        return ((type===Dime.psMap.TYPE.DATABOX)
            || (type===Dime.psMap.TYPE.RESOURCE)
            || (type===Dime.psMap.TYPE.LIVESTREAM)
            || (type===Dime.psMap.TYPE.LIVEPOST)            
            || (type===Dime.psMap.TYPE.PROFILE)
            //|| (type===Dime.psMap.TYPE.PROFILEATTRIBUTE)
            );
    },


    
    getChildType: function(type){        
        var typeMap = Dime.psMap.map[type];
        return typeMap.childType;      
    },
    
    
    isChildType: function(type){
        return (Dime.psMap.map[type].parentType!==0);
    },
    
    guessLinkURL: function(path){        
        var myPath = path;
        if ((!myPath) || (myPath.toLowerCase().indexOf("http")===0)) {
            return myPath;
        }//else
        if (myPath.length>0 && myPath.charAt(0)!=='/'){
            myPath = "/"+myPath;
        }
        //case of starting with /dime-communications/ link is a absolute relative
        if ((myPath.indexOf('/dime-communications/')===0))  {
            return Dime.ps_configuration.getRealBasicUrlString()                
            +myPath;
        }
        
        
        //special case of images coming with a /services/.../....png are probably from images //HACK
        if ((myPath.indexOf('/services/')===0) || (myPath.indexOf('/situations/')===0))  {
            return Dime.ps_configuration.getRealBasicUrlString()
            +Dime.ps_configuration.uiImagePath
            +myPath;
        }
        
         
        //special case of uri coming with a /blob/ are probably from simplePS //HACK
        if ((myPath.indexOf('/blob/')===0))  {
            return Dime.ps_configuration.getRealBasicUrlString()            
            +myPath;
        }
        
        
        return Dime.ps_configuration.getBasicUrlString()+myPath;
    },

    canRetrievePlaces: function(callback, callerRef){
        var handleAccountResult=function(response){
            var result=false;
            if (response && response.length>0){
                jQuery.each(response, function(){
                    result = result || (this.serviceadapterguid==='YellowMapPlaceService');
                });

            }
            callback.call(callerRef, result);

        };
        Dime.REST.getAll(Dime.psMap.TYPE.ACCOUNT, handleAccountResult);
    },
    
    getURLparam: function(url) {
        var myUrl = window.location.search.substring(1);
        var compareKeyValuePair = function(pair) {
            var key_value = pair.split('=');
            var decodedKey = decodeURIComponent(key_value[0]);
            var decodedValue = decodeURIComponent(key_value[1]);
            if(decodedKey === url) return decodedValue;
            return null;
        };

        var comparisonResult = null;

        if(myUrl.indexOf('&') > -1) {
            var params = myUrl.split('&');
            for(var i = 0; i < params.length; i++) {
                comparisonResult = compareKeyValuePair(params[i]); 
                if(comparisonResult !== null) {
                    break;
                }
            }
        }else {
            comparisonResult = compareKeyValuePair(myUrl);
        }

        return comparisonResult;
    },
   
    getPathFromUrl: function(url){

        var splittedURL = url.split(/\/+/g);
        var result = "";
        for (var i = 0; i<splittedURL.length-1;i++){
            result += splittedURL[i]+"/";
        }
        console.log('Dime.psHelper.getPathFromUrl - parsed url (url, result)',url, result);
        return result;
    },    

    prepareRequest: function(entry){
        
        var myEntry = entry;      
       
        var envelope = {
            request: {
                meta: {
                    v:"0.9",
                    status:'OK',
                    code:200,
                    timeRef: new Date().getTime()
                },
                data:{
                    startIndex:0,
                    itemsPerPage:1,
                    totalResults:1,
                    entry:[]
                }
            }
        };
        envelope.request.data.entry.push(myEntry);
        return envelope;
    },

    conArray: function(arrayA, arrayB){
        if (!arrayB){
            return arrayA;
        }
        if (!arrayA){
            return arrayB;
        }
        
        var bLength = arrayB.length;
        for (var i=0; i<bLength;i++){
            arrayA.push(arrayB[i]);
        }
        return arrayA;
    },
    
    /**
     * returns all items from items that have a guid in guids
     */
    getAllItemsWithGuids: function(guids, items){
        var result = [];
        for (var i=0;i<items.length;i++){
            if (JSTool.arrayContainsItem(guids, items[i].guid)){
                result.push(items[i]);
            }
        }
        return result;
    },
    
    updateExtendedACLWithAgentItems: function(extendedACL, agentType, allItemsOfType){
        var exAclPackage;
        
        if (agentType===Dime.psMap.TYPE.GROUP){
            for (var i=0;i<extendedACL.length;i++){
                exAclPackage=extendedACL[i];
                exAclPackage.groupItems = 
                Dime.psHelper.getAllItemsWithGuids(exAclPackage.groups, allItemsOfType);
            }
        }else if (agentType===Dime.psMap.TYPE.PERSON){
            for (var i=0;i<extendedACL.length;i++){
                exAclPackage=extendedACL[i];
                //retrieve person ids
                var personIds = [];                
                for (var j=0;j<exAclPackage.persons.length;j++){
                    personIds.push(exAclPackage.persons[j].personId);
                }                
                exAclPackage.personItems= 
                Dime.psHelper.getAllItemsWithGuids(personIds, allItemsOfType);
            }
        }else if (agentType===Dime.psMap.TYPE.ACCOUNT){
            for (var i=0;i<extendedACL.length;i++){
                exAclPackage=extendedACL[i];
                exAclPackage.serviceItems = 
                Dime.psHelper.getAllItemsWithGuids(exAclPackage.services, allItemsOfType);
            }
        }
    },
    
    transferAclToBasicExtendedAcl: function(acl){
        //transfer acl into Dime.ExtendedACL
        var result = [];
        for (var i=0;i<acl.length;i++){
            var exAclPackage = acl[i]; 
            result.push(exAclPackage);            
        }
        return result;
    },
    
    getAllReceiversOfItem: function(item, callback){
        //get all receivers of this item
        if (!item["nao:includes"]){
            console.log("ERROR: \"nao:includes\" missing for item:",item);
            return;
        }
        
        var acl = item["nao:includes"];
        var result = Dime.psHelper.transferAclToBasicExtendedAcl(acl);

        var getPersonsCallHandler = function(response){
            Dime.psHelper.updateExtendedACLWithAgentItems(result, Dime.psMap.TYPE.PERSON, response);
            callback(result);
        };   

        var getGroupsCallHandler = function(response){
            
            Dime.psHelper.updateExtendedACLWithAgentItems(result, Dime.psMap.TYPE.GROUP, response);
            Dime.REST.getAll(Dime.psMap.TYPE.PERSON, getPersonsCallHandler);
        };   

        var getAccountsCallHandler = function(response){
            
            Dime.psHelper.updateExtendedACLWithAgentItems(result, Dime.psMap.TYPE.ACCOUNT, response);
            Dime.REST.getAll(Dime.psMap.TYPE.GROUP, getGroupsCallHandler);
        };   

        Dime.REST.getAll(Dime.psMap.TYPE.ACCOUNT, getAccountsCallHandler);        
            
        
    },

    getAllSharedItems: function(agent, callback){        
        //lookup all sharable items for this agent
        var agentId = agent.guid;
        Dime.REST.getSharedTo(Dime.psMap.TYPE.DATABOX ,agentId, callback);
        Dime.REST.getSharedTo(Dime.psMap.TYPE.RESOURCE, agentId,callback);
        Dime.REST.getSharedTo(Dime.psMap.TYPE.LIVEPOST, agentId,callback);
        Dime.REST.getSharedTo(Dime.psMap.TYPE.PROFILE, agentId,callback);
        return;
      
    },
    
    getAclOfItem: function(item){
        if (!item["nao:includes"]){
            item["nao:includes"]=[];
        }
        
        var acl = item["nao:includes"];
        return acl;
    },
    
    getOrCreateACLPackage: function(item, saidSender){
        var acl = Dime.psHelper.getAclOfItem(item);
        
        var aclPackage=null;
        for (var i=0; i<acl.length;i++){
            if (acl[i].saidSender===saidSender){
                aclPackage=acl[i];
                break;
            }
        }
        if (aclPackage===null){ //not existing yet
            aclPackage= new Dime.ACLPackage();
            aclPackage.saidSender=saidSender;
            acl.push(aclPackage);
        }
        return aclPackage;
    },

    addAccessForItem: function(personGuids, groupGuids, serviceGuids, item, saidSender){
        var aclPackage =Dime.psHelper.getOrCreateACLPackage(item, saidSender);

        for (var i=0;i<personGuids.length;i++){
            if(!JSTool.arrayContainsItem(aclPackage.persons,personGuids[i])){
                var aclPerson = new Dime.ACLPerson();
                aclPerson.personId=personGuids[i];
                aclPackage.persons.push(aclPerson);
            }
        }
        for (var i=0;i<groupGuids.length;i++){
            if(!JSTool.arrayContainsItem(aclPackage.groups,groupGuids[i])){
                aclPackage.groups.push(groupGuids[i]);
            }
        }
        for (var i=0;i<serviceGuids.length;i++){
            if(!JSTool.arrayContainsItem(aclPackage.services,serviceGuids[i])){
                aclPackage.services.push(serviceGuids[i]);
            }
        }
        return item;
    },
    
    addAccessForItemAndUpdateServer: function(personGuids, groupGuids, serviceGuids, item, saidSender, callback){
         item = Dime.psHelper.addAccessForItem(personGuids, groupGuids, serviceGuids, item, saidSender);
        
        //POST update                      
        Dime.REST.updateItem(item, function(response){
            if (callback){
                callback(response);
            }
        });
    },

    sortAgents: function(agentsAndServices){
        //sort persons and groups
        var pAgents = [];
        var gAgents = [];
        var sAgents = [];
        for (var i=0;i<agentsAndServices.length;i++){
            if (agentsAndServices[i].type===Dime.psMap.TYPE.PERSON){
                pAgents.push(agentsAndServices[i].guid);
            }else if (agentsAndServices[i].type===Dime.psMap.TYPE.GROUP){
                gAgents.push(agentsAndServices[i].guid);
            }else if (agentsAndServices[i].type===Dime.psMap.TYPE.ACCOUNT){
                sAgents.push(agentsAndServices[i].guid);
            }
        }
        return {
            pAgents:pAgents,
            gAgents:gAgents,
            sAgents:sAgents
        };
    },

    addAgentAccessForItemsAndUpdateServer: function(agentsAndServices, items, saidSender, callback){
        var sortedA = Dime.psHelper.sortAgents(agentsAndServices);
        Dime.psHelper.addAccessForItemsAndUpdateServer(sortedA.pAgents, sortedA.gAgents, sortedA.sAgents, items, saidSender, callback);
    },

    addAccessForItemsAndUpdateServer: function(persons, groups, services, items, saidSender, callback){
        var callBackCounter = items.length;
        var updateSuccessful=true;
        var callBackHandler=function(response){
            callBackCounter--;
            if (!response || response.length<1){
                updateSuccessful=false;
            }

            if (callBackCounter==0){
                if (callback){
                    callback(updateSuccessful);
                }
            }
        };

        for (var i=0; i<items.length;i++){
            Dime.psHelper.addAccessForItemAndUpdateServer(persons, groups, services, items[i], saidSender, callBackHandler);
        }
    },

   
      
    
    addItemToGroup: function(group, item){
        if (group.get('pid')!==item.get('pid')){
            alert('Unable to add item to foreign group!\ngroup-owner:'
                +group.get('pid')+' != item-owner:'
                +item.get('pid')
                + '\nAdd ignored!');
            return;
        }
        
        console.log('addItemToGroup: ', group, item, item+"");
        var groupItems = group.get('items');
        groupItems.pushObject(item);
    },
    

    getProfileForSaid: function(said, callback){
        var callerSelf=this;

        var jointCallBack=function(response){
            var foundProfile = false;

            jQuery.each(response, function(){
                if (!foundProfile //only first profile
                    && (this.said)
                    && (this.said===said)){
                    callback.call(callerSelf, this);
                    foundProfile=true; 
                }
            });
            if (foundProfile){
                return;
            }
            //else
            console.log('ERROR: unable to find profile with said: '+said);
            callback.call(callerSelf, null);
        };


        Dime.REST.getAll(Dime.psMap.TYPE.PROFILE, jointCallBack, "@me", callerSelf);
    },

    generateRestPath: function(itemType, owner, callType, guid, agentId){
        
        var result = Dime.ps_configuration.getBasicUrlString()
        +'/'
        + encodeURIComponent(Dime.ps_configuration.mainSaid)
        +'/';
        
        if (callType === Dime.psMap.CALLTYPE.ADVISORY){                
            //{mainSaId}/advisory/
            return result+"advisory/@request";
        }
         
        result += Dime.psHelper.getPathForItemType(itemType)+'/';
   
        if (callType === Dime.psMap.CALLTYPE.AT_ALL_ALL_GET){                
            return result + '@all';
        }
        
        result += (owner!=='@me'?encodeURIComponent(owner):'@me')+'/';
        
        if (callType){            
            if (callType === Dime.psMap.CALLTYPE.SHARE_QUERY){
                //@me/@all/shared?sharedWithAgent={agentId}
                result += '@all/shared?sharedWithAgent='+encodeURIComponent(agentId);                
            }else if (Dime.psMap.callTypeMap[callType].callString==="{guid}"){
                result += encodeURIComponent(guid);                
            }else{
                result += Dime.psMap.callTypeMap[callType].callString;
            }
        }
        return result;
    },
    
    getCometPath: function(){
        
        //https://dime-communications/push/{said_hoster}/{id_device}/@comet?startingFrom=[DATE_TIME_UNIX_TIME]
        
        var result = Dime.ps_configuration.getRealBasicUrlString()
        + '/dime-communications/push/'
        + encodeURIComponent(Dime.ps_configuration.mainSaid)
        + '/'
        + encodeURIComponent(Dime.ps_configuration.deviceGuid)
        +'/@comet?startingFrom=' + Dime.ps_configuration.startTime;
        
        return result;
    },
   
    getPathForImageUpload: function(){
        return Dime.ps_configuration.getBasicUrlString()
        +'/'
        + encodeURIComponent(Dime.ps_configuration.mainSaid)
        +'/'
        + Dime.psHelper.getPathForItemType(Dime.psMap.TYPE.RESOURCE)
        +'/@me/@uploadFile';
    },
   
    getPathForItemType: function(itemType){

        var psType = Dime.psMap.map[itemType];
        if (this.checkObject(psType, "psType for itemType:"+itemType)){
            return psType.path;
        }
        return "";
    },

    getCaptionForItemType: function(itemType){

        var psType = Dime.psMap.map[itemType];
        if (this.checkObject(psType, "psType for itemType:"+itemType)){
            return psType.caption;
        }
        return "";
    },

    getPluralCaptionForItemType: function(itemType){

        var psType = Dime.psMap.map[itemType];
        if (this.checkObject(psType, "psType for itemType:"+itemType)){
            return psType.pluralCaption;
        }
        return "";
    },


    getImageUrlFromImage: function(imageFileName){
        return Dime.ps_configuration.getRealBasicUrlString()+Dime.ps_configuration.iconPath + '/' + imageFileName;
    },

    getImageUrlForItemType: function(itemType){
        var psType = Dime.psMap.map[itemType];
        if (this.checkObject(psType, "psType for itemType:"+itemType)){
            return this.getImageUrlFromImage(psType.image);
        }
        return "";

    },

    /**
     * helper function to retieve full items from stubItems
     * stubItems[{
     *      guid: "guid",
     *      type: "type",
     *      userId: "userId"
     * },{
     *      guid: "guid",
     *      type: "type",
     *      userId: "userId"
     * }, ...
     * ]     
     */
    getMixedItems: function(stubItems, callBack, callerSelf){
        //handle trivial case first
        if (stubItems.length===0){
            callBack.call(callerSelf, []);
        }


        //unrole calls by types
        var typeUserIds ={};
        jQuery.each(stubItems, function(){
            var typeUserIdKey=this.type+'@'+this.userId;
            if (!typeUserIds[typeUserIdKey]){
                typeUserIds[typeUserIdKey] = {
                    type: this.type,
                    userId: this.userId,
                    guids: [this.guid]
                };

            }else{
                typeUserIds[typeUserIdKey].guids.push(this.guid);
            }
            
        });

        var typeUserIdArray = JSTool.getDefinedMembers(typeUserIds);

        var result=[];
        
        var receivedCallbacks=0;

        var handleResponse=function(response){
            receivedCallbacks++;
            if (!response || response.length===0){
                console.log("ERROR: getMixedItems: got empty response in callback");
                return;
            }
            var typeUserIdEntry = typeUserIds[response[0].type+'@'+response[0].userId];
            if (!typeUserIdEntry){
                console.log("ERROR: getMixedItems: received non-requested response!");
                return;
            }
            jQuery.each(response, function(){
                if(JSTool.arrayContainsItem(typeUserIdEntry.guids, this.guid)){
                    result.push(this);
                }
            });
            //check whether this was the last callback
            if (receivedCallbacks===typeUserIdArray.length){
                callBack.call(callerSelf, result);
            }
        };
        //trigger the various get calls
        jQuery.each(typeUserIdArray,function(){
            Dime.REST.getAll(this.type, handleResponse, this.userId, this);
        });

    },

    // -------------------------
    // CHECKING RESPONSES
    // -------------------------

    


    prepareResponseEntryItem: function(item){

        
        if (!this.checkObject(item, "item of received entry")){
            return;
        }
        //set pid
        

        if (!this.checkObject(item.type, "item.type")){
            console.log("ERROR Dime.psHelper.prepareResponseEntryItem - type not defined (item)", item);
            return;
        }
        if (this.getPathForItemType(item.type)===""){
            console.log("ERROR Dime.psHelper.prepareResponseEntryItem - found unknown type (type, item)", item.type, item);
        }

        //HACK for notification items
        if (item.type===Dime.psMap.TYPE.NOTIFICATION){
            return;
        }



        if (!(item.name)){

            //TODO in case its a livepost we need to look up the name of the creator here
            if (item.type===Dime.psMap.TYPE.LIVEPOST){
                item.name="Message";
            }else if (item.type===Dime.psMap.TYPE.PROFILEATTRIBUTE){
                item.name="";
            }else{
                console.log("Dime.psHelper.prepareResponseEntryItem - updating missing name for item with type", item.type);
                item.name = "Name missing!";
            }            
        }
        
        
        if (!(item.imageUrl)){
            var imageUrl = this.getImageUrlForItemType(item.type);
            //console.log("Dime.psHelper.prepareResponseEntryItem - updating missing imageUrl for item (type, imageUrl)", item.type, imageUrl);
            item.imageUrl = imageUrl;
        }else{
            item.imageUrl= this.guessLinkURL(item.imageUrl);
        }

    },


    prepareResponseEntry: function(entry){
        var result = entry;

        var eLength = entry.length;
        for (var i=0; i<eLength; i++){
            this.prepareResponseEntryItem(entry[i]);
        }
        return result;
    },



    sortResponseEntryByCreatedReverse: function(entry){
        if (!entry || (entry.length===0) || (!$.isArray(entry))){
            return entry;
        }

        var cmpEntry = function (a, b) {
            var A = a.created;
            var B = b.created;
            if (A < B){
                return 1;
            }else if (A > B){
                return  -1;
            }//else

            return 0;

        };

        entry.sort(cmpEntry);
        return entry;
    },
    
    sortResponseEntryByName: function(entry){
        if (!entry || (entry.length===0) || (!$.isArray(entry))){
            return entry;
        }
        
        var cmpEntry = function (a, b) {
            var A = a.name.toLowerCase();
            var B = b.name.toLowerCase();
            if (A < B){
                return -1;
            }else if (A > B){
                return  1;
            }//else
            
            return 0;
            
        };
        
        entry.sort(cmpEntry);
        return entry;
    },
    
    sortWarningsByLevel: function(warnings){
        if (!warnings || (warnings.length===0) || (!$.isArray(warnings))){
            return warnings;
        }
        
        var cmpEntry = function (a, b) {
            var A = a.warningLevel;
            var B = b.warningLevel;
            if (A < B){
                return 1;
            }else if (A > B){
                return -1;
            }//else
            
            return 0;
            
        };
        
        warnings.sort(cmpEntry);
        return warnings;
    },

    checkObject:function(object, name, quiet){
        if (!object) {
            if (!quiet){
                console.log('ERROR: Dime.psHelper.checkObject object:'+name+' is null!');
            }
            return NO;
        }
        return YES;
    },

    ResponseStructureCheckResult:{
        INVALID_RESPONSE: 1,
        DATA_MISSING: 2,
        ENTRY_MISSING: 3,
        META_MISSING: 4,
        META_STATUS_MISSING: 5,
        STRUCTURE_OK: 6
    },

    responseStructureCheck: function(bodyObject, quiet){

        if (!Dime.psHelper.checkObject(bodyObject, "body", quiet)){
            if (!quiet){
                console.log('ERROR: Dime.psHelper.getEntryOfResponseObject response is non-JSON message: (body)', bodyObject);
            }
            return Dime.psHelper.ResponseStructureCheckResult.INVALID_RESPONSE;
        }
        if (!Dime.psHelper.checkObject(bodyObject.response, "body.response", quiet)){
            if (!quiet){
                console.log('ERROR: Dime.psHelper.getEntryOfResponseObject incomplete response "response" object missing: (body)', bodyObject);
            }
            return Dime.psHelper.ResponseStructureCheckResult.INVALID_RESPONSE;
        }
        if (!Dime.psHelper.checkObject(bodyObject.response.meta, "body.response.meta", quiet)){
            if (!quiet){
                console.log('ERROR: Dime.psHelper.getEntryOfResponseObject incomplete response "meta" object missing: (body)', bodyObject);
            }
            return Dime.psHelper.ResponseStructureCheckResult.META_MISSING;
        }
        if (!Dime.psHelper.checkObject(bodyObject.response.meta.status, "body.response.meta.status", quiet)){
            if (!quiet){
                console.log('ERROR: Dime.psHelper.getEntryOfResponseObject incomplete response "status" object missing: (body)', bodyObject);
            }
            return Dime.psHelper.ResponseStructureCheckResult.META_STATUS_MISSING;
        }        
        if (!Dime.psHelper.checkObject(bodyObject.response.data, "body.response.data", quiet)){
            if (!quiet){
                console.log('ERROR: Dime.psHelper.getEntryOfResponseObject incomplete response "data" object missing: (body)', bodyObject);
            }
            return Dime.psHelper.ResponseStructureCheckResult.DATA_MISSING;
        }        
        if (!Dime.psHelper.checkObject(bodyObject.response.data.entry, "body.response.data.entry", quiet)){
            if (!quiet){
                console.log('WARN: Dime.psHelper.getEntryOfResponseObject incomplete response "entry" object missing: (body)', bodyObject);
            }
            return Dime.psHelper.ResponseStructureCheckResult.ENTRY_MISSING;
        }
        if (!$.isArray(bodyObject.response.data.entry)){
            if (!quiet){
                console.log('WARN: Dime.psHelper.getEntryOfResponseObject "entry" object is not an array! (entry)', bodyObject.response.data.entry);
            }

            return Dime.psHelper.ResponseStructureCheckResult.ENTRY_MISSING;
        }
        return Dime.psHelper.ResponseStructureCheckResult.STRUCTURE_OK;
    },
    
    extractEntryFromResponseObject: function(bodyObject, skipCheck){
        if (!skipCheck){

            var checkResult = Dime.psHelper.responseStructureCheck(bodyObject);
            if (checkResult<=Dime.psHelper.ResponseStructureCheckResult.DATA_MISSING){
                return null;
            }//else
            if (checkResult===Dime.psHelper.ResponseStructureCheckResult.ENTRY_MISSING){
                return [];
            }//else
            
            if (checkResult===Dime.psHelper.ResponseStructureCheckResult.STRUCTURE_OK){
                //check meta information
                //check for ok
                if (!bodyObject.response.meta.status.toLowerCase()==="ok"){                    
                    //not ok
                    console.log("ERROR: received error status in response:",
                        bodyObject.response.meta.status, bodyObject.response.meta.msg, bodyObject);
                    return null;
                //TODO more sophisticated analysis and throwing of exceptions?
                }            
            }
        }
        return bodyObject.response.data.entry;
    },
    


    getEntryOfResponseObject: function(bodyObject, skipCheck){
        //extract entries from response object
        var result = Dime.psHelper.extractEntryFromResponseObject(bodyObject, skipCheck);
        
        if (!result || result.length==0){
            return result;
        }
        if (!skipCheck){
            //check content and repair entries dependent on the type
            result = this.prepareResponseEntry(result);
        }
        
        if(result.length < 2){ //nothing to sort anyway
            return result;
        }

        if (result && result.length>0 && 
            (result[0].type===Dime.psMap.TYPE.NOTIFICATION
                ||result[0].type===Dime.psMap.TYPE.EVENT
                ||result[0].type===Dime.psMap.TYPE.USERNOTIFICATION
                ||result[0].type===Dime.psMap.TYPE.LIVEPOST
                )){
            return this.sortResponseEntryByCreatedReverse(result);
        }
        if (result[0].name){
            //sort result by name
            return this.sortResponseEntryByName(result);
        }
        return result;

    },
  
    getCombinedModels: function(searchViewFilter){
        

        if (searchViewFilter==='all'){

            return {
                groups: Dime.GenItem,
                items: Dime.GenItem
            };
        }
        if (searchViewFilter==='people'){
            return {
                groups: Dime.GroupsModel,
                items: Dime.PersonModel
            };

        }
        if (searchViewFilter==='data'){
            return {
                groups: Dime.DataboxModel,
                items: Dime.ResourceModel
            };
        }
        if (searchViewFilter==='profiles'){
            return {
                groups: Dime.PersonModel,
                items: Dime.ProfileModel
            };
        }
        if (searchViewFilter==='profile'){
            return {
                groups: Dime.ProfileModel,
                items: Dime.ProfileAttributeModel
            };
        }
        //else

        console.log('ERROR: Dime.psHelper:getCombinedModels filter not supported:',searchViewFilter);
        return null;
    },
    

    tiggerNewSituationEvent: function(situation, switchOff){
        var path = Dime.psHelper.serverContextPath+'/@me';
        if (!situation){
            situation="Event";
        }
        var placeName = switchOff?situation:"test:"+situation;
        var payload = {
            "entry": [
            {
                "timestamp": "2011-11-23T12:10:47+01:00",
                "expires": "2011-11-25T12:12:47+01:00",
                "scope": "civilAddress",
                "dataPart": {
                    "placeName": placeName
                },
                "source": {
                    "id": "Di.Me Crawler",
                    "v": "1.0.1"
                },
                "entity": {
                    "id": "@me",
                    "type": "user"
                }
            }
            ]
        };

        var myReq = SC.Request.postUrl(path, payload);
        myReq.json();
        myReq.send();

    },
    
    //REFACTOR: merge following 2 methods (postUpdateCurrentPlace + postCurrentContext)     
    postUpdateCurrentPlace: function(event, element, item, restoreLocation){
        
        var path = Dime.ps_configuration.getUserUrlString()+"/context/@me";
        
        var timestamp = new Date();
        timestamp.setMilliseconds(0);
        
        var expires = new Date();
        if(restoreLocation){
            expires.setSeconds(expires.getSeconds() + 1);
            expires.setMilliseconds(0);
        }else{
            //midnight
            expires.setHours(23);
            expires.setMinutes(59);
            expires.setSeconds(59);
            expires.setMilliseconds(0);
        }
        
        var entry = {
            "guid": "9f0dfb3a-938c-4e6c-aad3-2078c80d55fa",
            "type": "context",
            //HACK! force to this time format, need dynamically time offset
            "timestamp": timestamp.toISOString().replace("Z","+02:00").replace(".000",""),
            "expires": expires.toISOString().replace("Z","+02:00").replace(".000",""),
            "scope": "currentPlace",
            "entity": {
                "id": "@me",
                "type": "username"
            },
            "source": {
                "id": "desktop-crawler",
                "v": "0.9"
            },
            "dataPart": {
                "placeId": item.guid,
                "placeName": item.name
            }
        };
        
        var callback = function(response){
            console.log(response);
            
            //check response for errors
            if(response.response.meta.status.toLowerCase()!=="ok"){
                var error = response.response.meta.msg;
                (new Dime.Dialog.Toast('An Error occured: ' + error)).showLong();
                return;
            }
            
            if(restoreLocation){
                (new Dime.Dialog.Toast('Removing "' + item.name + '" as your current location was successfully!')).showLong();
            }else{
                (new Dime.Dialog.Toast('Setting "' + item.name + '" as your current location was successfully!')).showLong();
            }
                
        };
        
        var request = Dime.psHelper.prepareRequest(entry);
        $.postJSON(path, request, callback);
        
        //this.removeDialog();
    },
     
    postCurrentContext: function(latitude, longitude, accuracy){
        
        var path = Dime.ps_configuration.getUserUrlString()+"/context/@me";
        
        var expireMinutes = 240;
        var locMode = accuracy<15000?"GPS":"NET";
        
        //timestamp date
        var timestamp = new Date();
        timestamp.setMilliseconds(0);
        
        //expires date (midnight)
        var expires = new Date();
        expires.setHours(23);
        expires.setMinutes(59);
        expires.setSeconds(59);
        expires.setMilliseconds(0);
        
        var entry ={
                    "guid": JSTool.randomGUID(),
                    "type": "context",
                    //HACK! force to this time format, need dynamically time offset
                    "timestamp": timestamp.toISOString().replace("Z","+02:00").replace(".000",""),
                    "expires": expires.toISOString().replace("Z","+02:00").replace(".000",""), 
                    "scope": "position",
                    "entity": {
                        "id": "@me",
                        "type": "username"
                    },
                    "source": {
                        "id": "desktop-crawler",
                        "v": "0.9"
                    },
                    "dataPart": {
                        "latitude": latitude,
                        "longitude": longitude,
                        "accuracy": accuracy,
                        "locMode": locMode
                    }
        };
        
        var callback = function(response){
            console.log(response);
            
            //check response for errors
            if(response.response.meta.status.toLowerCase()!=="ok"){
                var error = response.response.meta.msg;
                (new Dime.Dialog.Toast('An Error occured: ' + error)).showLong();
                return;
            }
            
            (new Dime.Dialog.Toast("Getting current geolocation was successfully!")).showLong();
        };
        
        var request = Dime.psHelper.prepareRequest(entry);
        $.postJSON(path, request, callback);
    },
    
    /*
     *
     *"guid":"f630da79-d1c3-4fe6-bf61-aca7fe0b69c9",
     "type":"person",
     "lastModified":1349789975080,
     "name":"Chilla Marc",
     "imageUrl":"/img/Personen07.jpeg",
     "userId":"@me",
     "items":[],
     "nao:trustLevel":0.0
     *
     */
    createNewItem: function(type, name){
        var entry = {
            guid: JSTool.randomGUID(),
            type: type,
            lastModified: new Date().getTime(),            
            name: name,
            imageUrl: Dime.psHelper.getImageUrlForItemType(type),
            userId: Dime.ps_static_configuration.ME_OWNER,
            items:[]
        };
        
        // only persons are trustable if (Dime.psHelper.isAgentType(type)){
        if (type===Dime.psMap.TYPE.PERSON){
            entry["nao:trustLevel"]=0.5;
        }else if (Dime.psHelper.isShareableType(type)){
            entry["nao:privacyLevel"]=0.5;
        }
        if (type===Dime.psMap.TYPE.LIVEPOST){
            entry.created=entry.lastModified;
            //set empty for the view on android
            entry.imageUrl="";
        }
        
        return entry;
    }
    
    
};

//---------------------------------------------
//#############################################
//  Dime.cache
//#############################################
//


//TODO replace entries and history with single hash containing objects: {entry:<entry>, addIndex:<index>}
Dime.cache={
    maxCacheSize: 400,
    entries:{},
    history:{},
    addIndex: 0,
    size: 0,
    
    removeOldestEntry: function(){
        var oldestIndex = Dime.cache.addIndex;
        var oldKey=null;
        for (var k in Dime.cache.history){ 
            if (Dime.cache.history.hasOwnProperty(k)) {
                if (Dime.cache.history[k]<oldestIndex){
                    oldestIndex = Dime.cache.history[k];
                    oldKey = k;
                }
            }
        }
        if (oldKey!=null){
            delete Dime.cache.entries[oldKey];
            delete Dime.cache.history[oldKey];
        }
    },    
    get: function(key){
        var result = Dime.cache.entries[key];        
        if (result){
            Dime.cache.history[key]=Dime.cache.addIndex; //update read index
            return result;
        }
        return null;
    },
    put: function(key, value){
        if (key===null){
            throw "key must not be null!";
        }
        if (value===null){
            throw "value must not be null!";
        }
        
        var newEntry = (!Dime.cache.entries.hasOwnProperty(key));
        Dime.cache.entries[key]=value;    
        
        Dime.cache.addIndex++;
        Dime.cache.history[key]=Dime.cache.addIndex;
        
        if (newEntry){//check for removal of old cached value
            Dime.cache.size++;
            if (Dime.cache.size>Dime.cache.maxCacheSize){
                Dime.cache.removeOldestEntry();
            }
        }
    },
    deleteEntry: function(key){
        if (key===null){
            throw "key must not be null!";
        }
        if (Dime.cache.entries.hasOwnProperty(key)){
            Dime.cache.size--;
            delete Dime.cache.entries[key];
            delete Dime.cache.history[key];
        }       
    }
};

//---------------------------------------------
//#############################################
//  Dime.REST
//#############################################
//

Dime.REST = {
    
    clearCacheForType: function(type, userId){

        //special case type===auth
        if (type==='auth'){
            Dime.cache.deleteEntry(Dime.ps_configuration.getUserUrlString()+"/user/@me");
            return;
        }

        //@all        
        if (!userId || userId.length===0){
            userId=Dime.ps_static_configuration.ME_OWNER;
        }
        
        var callPath = Dime.psHelper.generateRestPath( type,
            userId, 
            Dime.psMap.CALLTYPE.AT_ALL_GET,
            null,
            null
            );
        Dime.cache.deleteEntry(callPath);
        
        //@allall
        var atAllCallPath = Dime.psHelper.generateRestPath( type,
            null, 
            Dime.psMap.CALLTYPE.AT_ALL_ALL_GET,
            null,
            null
            );
        Dime.cache.deleteEntry(atAllCallPath);
        
    },
    
    handleResponse: function (response, skipCheck){
        if (!response){
            return {};
        }
    
        return Dime.psHelper.getEntryOfResponseObject(response, skipCheck);
    
    },



    getAll: function(itemType, callBack, userId, callerSelf){
        
        
        if (!userId || userId.length===0){
            userId=Dime.ps_static_configuration.ME_OWNER;
        }
    
        if (!callerSelf){
            callerSelf = this;
        }
    
        var callPath = Dime.psHelper.generateRestPath( itemType,
            userId, 
            Dime.psMap.CALLTYPE.AT_ALL_GET,
            null,
            null
            );
             
        var jointCallBack = function(response){
            var responseEntries = Dime.REST.handleResponse(response);
            Dime.cache.put(callPath, responseEntries);
            callBack.call(callerSelf, responseEntries);
        };
        
         
        var cacheEntries = Dime.cache.get(callPath);
        if (cacheEntries){
            callBack.call(callerSelf, cacheEntries); 
        }else{                        
            //console.log(callPath);
            $.getJSON(callPath, "", jointCallBack);
        }
    },

    getAllAll: function(itemType, callBack, callerSelf){            
    
        if (!callerSelf){
            callerSelf = this;
        }
        
        var callPath = Dime.psHelper.generateRestPath( itemType,
            null, 
            Dime.psMap.CALLTYPE.AT_ALL_ALL_GET,
            null,
            null
            );
        
        var jointCallBack = function(response){
            var responseEntries = Dime.REST.handleResponse(response);
            Dime.cache.put(callPath, responseEntries);
            callBack.call(callerSelf, responseEntries);
        };
        
        var cacheEntries = Dime.cache.get(callPath);
        if (cacheEntries){
            callBack.call(callerSelf, cacheEntries); 
        }else{          
            //console.log(callPath);
            $.getJSON(callPath, "", jointCallBack);
        }
    },

    /**
     * returns single item - no array
     * @param itemGuid guid of requested item - mandantory
     * @param itemType type of requested item - mandantory
     * @param callBack callback for returning result - mandantory
     * @param userId userId of the requested item - optional - set to '@me' if not set
     * @param callerSelf reference to the calling object - will be used as this target when calling the callBack - optional
     */
    getItem: function(itemGuid, itemType, callBack, userId, callerSelf){
        
        if (!userId || userId.length===0){
            userId=Dime.ps_static_configuration.ME_OWNER;
        }
        
        if (!callerSelf){
            callerSelf = this;
        }
    
        var jointCallBack = function(response){
            var responseArray=Dime.REST.handleResponse(response);
            var responseEntry=null;
            if (responseArray && responseArray.length>0){
                responseEntry=responseArray[0];
            }
            callBack.call(callerSelf, responseEntry);
        };
    
        var callPath = Dime.psHelper.generateRestPath( itemType,
            userId, 
            Dime.psMap.CALLTYPE.AT_ITEM_GET,
            itemGuid);
        
        //try for at all in the cache
        var atAllPath = Dime.psHelper.generateRestPath(itemType,
            userId, 
            Dime.psMap.CALLTYPE.AT_ALL_GET,
            null,
            null
            );
        var cacheEntries = Dime.cache.get(atAllPath);
        if (cacheEntries){//we found the fitting at all call
            for (var i=0;i<cacheEntries.length;i++){
                if (cacheEntries[i].guid===itemGuid){ //check for the fitting entry
                    callBack.call(callerSelf, cacheEntries[i]);
                    return;
                }
            }
        }//else
        
        //console.log(callPath);
        $.getJSON(callPath, "", jointCallBack);        
    },

    getItems: function(itemGuids, itemType, callBack, userId, callerSelf){
        var handleResult = function(response){
            var result=[];
            jQuery.each(response, function(){
                if (JSTool.arrayContainsItem(itemGuids, this.guid)){
                    result.push(this);
                }
            });

            callBack.call(callerSelf,result);
        };
        Dime.REST.getAll(itemType, handleResult, userId, callerSelf);
    },

    getCOMETCall: function(callback, callerSelf){
    
        if (!callerSelf){
            callerSelf = this;
        }
        
        var jointCallBack = function(response){
            callback.call(callerSelf, Dime.REST.handleResponse(response));
        };
    
        var callPath = Dime.psHelper.getCometPath();
                        
        //console.log(callPath);
        $.getJSON(callPath, "", jointCallBack);
    },
    
    postNewItem: function(item, callBack, callerSelf){

        if (!callBack){
            callBack = function(response){
                console.log("postNewItem callback - response:", response);
            };
        }
        
        if (!callerSelf){
            callerSelf = this;
        }
        
        var jointCallBack = function(response){
            callBack.call(callerSelf, Dime.REST.handleResponse(response));
        };
        //clear cache
        Dime.REST.clearCacheForType(item.type, item.userId);
        
        var request = Dime.psHelper.prepareRequest(item);
        var path = Dime.psHelper.generateRestPath(item.type,
            item.userId, 
            Dime.psMap.CALLTYPE.AT_ITEM_POST_NEW);
        
        $.postJSON(path, request, jointCallBack);
    },
    
    /**
     * posts an update of the item to the ps
     * 
     * @param item the full item 
     * @param callBack function forwarded to the ajax call
     */
    updateItem: function(item, callBack, callerSelf){
        console.log("updateItem", item);
        
        if (!item.userId || item.userId.length===0) {
            console.log("ERROR: userId not defined for item:", item, "update aborted!");
            return;
        }
        
        if (!callBack){
            callBack = function(response){
                console.log("updateItem callback - response:", response);
            };
        }
        
        //clear cache
        Dime.REST.clearCacheForType(item.type, item.userId);
        
        if (!callerSelf){
            callerSelf = this;
        }
        
        var jointCallBack = function(response){
            callBack.call(callerSelf, Dime.REST.handleResponse(response));
        };
        
        
        var request = Dime.psHelper.prepareRequest(item);
        var path = Dime.psHelper.generateRestPath( item.type,
            item.userId, 
            Dime.psMap.CALLTYPE.AT_ITEM_POST_UPDATE,
            item.guid            
            );
        
        $.postJSON(path, request, jointCallBack);
    },
    
    removeItem: function(item, callBack, callerSelf){
        console.log("remove", item);
        
        if (!item.userId || item.userId.length===0) {
            console.log("ERROR: userId not defined for item:", item, "remove aborted!");
            return;
        }
        
        if (!callBack){
            callBack = function(response){
                console.log("removeItem callback - response:", response);
            };
        }
        
        //clear cache
        Dime.REST.clearCacheForType(item.type, item.userId);
        
        if (!callerSelf){
            callerSelf = this;
        }
        
        var jointCallBack = function(response){
            callBack.call(callerSelf, Dime.REST.handleResponse(response));
        };
        
        var path = Dime.psHelper.generateRestPath(item.type,
            item.userId, 
            Dime.psMap.CALLTYPE.AT_ITEM_DELETE,
            item.guid            
            );
        
        $.deleteJSON(path, "", jointCallBack);        
    },
        
    searchGlobal: function(query, callBack, callerSelf){
        //api/dime/rest/juan/search?query=
        var path = Dime.ps_configuration.getRealBasicUrlString() 
        + "/dime-communications/api/dime/rest/"
        + encodeURIComponent(Dime.ps_configuration.mainSaid)
        +"/search?query="
        + encodeURIComponent(query);
        
        
        if (!callerSelf){
            callerSelf = this;
        }
        
        var jointCallBack = function(bodyObject){
            //check response structure
            var checkResult = Dime.psHelper.responseStructureCheck(bodyObject);
            if (checkResult<=Dime.psHelper.ResponseStructureCheckResult.DATA_MISSING){
                return;
            }//else
            if (checkResult===Dime.psHelper.ResponseStructureCheckResult.ENTRY_MISSING){
                return;
            }//else
            
            if (checkResult===Dime.psHelper.ResponseStructureCheckResult.STRUCTURE_OK){
                //check meta information
                //check for ok
                if (!bodyObject.response.meta.status.toLowerCase()==="ok"){                    
                    //not ok
                    console.log("ERROR: received error status in response:",
                        bodyObject.response.meta.status, bodyObject.response.meta.msg, bodyObject);
                    return ;
                //TODO more sophisticated analysis and throwing of exceptions?
                }            
            }
            //structure ok
            
            callBack.call(callerSelf, bodyObject.response.data.entry);
        };
    
        $.getJSON(path, "", jointCallBack);
    },

    addPublicContact: function(publicContactEntry, callBack){

        var path = Dime.ps_configuration.getRealBasicUrlString() 
        + "/dime-communications/api/dime/rest/"
        + encodeURIComponent(Dime.ps_configuration.mainSaid)
        + "/person/addcontact";
        
        var request = Dime.psHelper.prepareRequest(publicContactEntry);
        
        if (!callBack){
            callBack = function(response){
                console.log("addPublicContact callback - (request, response):", request, response);
            };
        }
        
        //clear cache
        Dime.REST.clearCacheForType(Dime.psMap.TYPE.PERSON);
        
        $.postJSON(path, request, callBack);
    },
    
    getCurrentPlace:function(callBack){
        
        var metaCallBack=function(placeGuidAndNameObject){
            if (!placeGuidAndNameObject || !placeGuidAndNameObject.placeId){
                callBack(null);
                return;
            }
            
            var myPlaceGuid=placeGuidAndNameObject.placeId;

            var placeCallBack=function(response){
                callBack(response);
            };
            Dime.REST.getItem(myPlaceGuid,Dime.psMap.TYPE.PLACE, placeCallBack);
        };

        Dime.REST.getCurrentPlaceGuidAndName(metaCallBack);
    },
    
    getCurrentPlaceGuidAndName: function(callBack){
        //https://localhost:8443/dime-communications/api/dime/rest/<said>/context/@me
        var path = Dime.ps_configuration.getRealBasicUrlString() 
        + "/dime-communications/api/dime/rest/"
        + encodeURIComponent(Dime.ps_configuration.mainSaid)
        + "/context/@me/currentPlace";        
        
        if (!callBack){
            callBack = function(response){
                console.log("getCurrentPlaceGuidAndName callback - (request, response):", request, response);
            };
        }
        
        var metaCallBack=function(response){
            var entries = Dime.psHelper.getEntryOfResponseObject(response, false);
            if (!entries || entries.length===0 || (!entries[0].dataPart)){
                console.log("ERROR when looking up current place!(path, response)", path, response);
                callBack({});
                return;
            }
            console.log("received current place:", entries[0].dataPart);
            
            callBack(entries[0].dataPart);
        };
        
        $.getJSON(path, "", metaCallBack);
    },
    
    getSharedTo: function(itemType, agentId, callBack){

        var callPath = Dime.psHelper.generateRestPath( itemType,
            Dime.ps_static_configuration.ME_OWNER, 
            Dime.psMap.CALLTYPE.SHARE_QUERY, null, 
            agentId);
            
        var callerSelf=this;
                
        var jointCallBack = function(response){
            var responseEntries = Dime.REST.handleResponse(response);
            Dime.cache.put(callPath, responseEntries);
            callBack.call(callerSelf, responseEntries);
        };
        
         
        var cacheEntries = Dime.cache.get(callPath);
        if (cacheEntries){
            callBack.call(callerSelf, cacheEntries); 
        }else{                        
            //console.log(callPath);
            $.getJSON(callPath, "", jointCallBack);
        }  
    },
    
    postAdvisoryRequest: function(profileGuid, receivers, items, callBack, callerSelf){
        
        var requestItem = new Dime.AdvisoryRequest(profileGuid, receivers, items);
        
        
        if (!callerSelf){
            callerSelf = this;
        }
    
        var jointCallBack = function(response){
            callBack.call(callerSelf, Dime.psHelper.extractEntryFromResponseObject(response));
        };
    
        var callPath = Dime.psHelper.generateRestPath( null,
            null, 
            Dime.psMap.CALLTYPE.ADVISORY,
            null);
        
        var request = Dime.psHelper.prepareRequest(requestItem);
        
        //console.log(callPath);
        $.postJSON(callPath, request, jointCallBack);        
    },

    getServerInformation: function(callBack, callerSelf){

        if (!callerSelf){
            callerSelf = this;
        }

  

        var callPath = Dime.ps_configuration.getRealBasicUrlString()
        + '/dime-communications/api/dime/server';

        var jointCallBack = function(response){
            var responseEntries = Dime.REST.handleResponse(response, true);
            var result=null;
            if (responseEntries && responseEntries.length>0){
                result = responseEntries[0];
                Dime.cache.put(callPath, result);
            }            
            callBack.call(callerSelf, result);
        };


        var cacheEntries = Dime.cache.get(callPath);
        if (cacheEntries){
            callBack.call(callerSelf, cacheEntries);
        }else{
            //console.log(callPath);
            $.getJSON(callPath, "", jointCallBack);
        }

    },

    getUser: function(callBack, callerSelf){

        if (!callerSelf){
            callerSelf = this;
        }

        // api/dime/rest/{mainSaId}/user/*

        var callPath = Dime.ps_configuration.getUserUrlString()+"/user/@me";

        var jointCallBack = function(response){
            var responseEntries = Dime.REST.handleResponse(response, true);
            var result=null;
            if (responseEntries && responseEntries.length>0){
                result = responseEntries[0];
                //update configuration entry
                Dime.ps_configuration.userInformation=result;
                //update cache
                Dime.cache.put(callPath, result);
            }
            callBack.call(callerSelf, result);
        };


        var cacheEntries = Dime.cache.get(callPath);
        if (cacheEntries){
            callBack.call(callerSelf, cacheEntries);
        }else{
            //console.log(callPath);
            $.getJSON(callPath, "", jointCallBack);
        }
    },
    /**
     * posts an update of the user item to the ps
     *
     */
    updateUser: function(userItem, callBack, callerSelf){

        if (!callBack){
            callBack = function(response){
                console.log("updateUser callback - response:", response);
            };
        }

        if (!callerSelf){
            callerSelf = this;
        }

        var callPath = Dime.ps_configuration.getUserUrlString()+"/user/@me";

        var jointCallBack = function(response){
            var responseEntries = Dime.REST.handleResponse(response, true);
            var result=null;
            if (responseEntries && responseEntries.length>0){
                result = responseEntries[0];
                //update configuration entry
                Dime.ps_configuration.userInformation=result;
                //update cache
                Dime.cache.put(callPath, result);
            }
            callBack.call(callerSelf, result);
        };

        var request = Dime.psHelper.prepareRequest(userItem);

        $.postJSON(callPath, request, jointCallBack);
    },
    postEvaluation: function(evaluationItem){                     
        var callPath = Dime.ps_configuration.getUserUrlString()+"/evaluation/@me";

        var callback = function(response){
            //console.log(response);
        };

        var request = Dime.psHelper.prepareRequest(evaluationItem);

        $.postJSON(callPath, request, callback);
    }

    
};




//---------------------------------------------
//#############################################
//  Dime.Navigation
//#############################################
//

Dime.Navigation = {
    
    MAX_NOTIFICATION_ITEMS: 6,
    
    shownNotifications:0,
    
    receivedNotifications:0,
    
    updateView : function(notifications){
    //overwrite to update view on notifications incoming
    },

    createUserNotificationElement :function(userNotification){
        
        var unValues = Dime.un.getCaptionImageUrl(userNotification);
        
        var target;
        
        if (userNotification.unType===Dime.psMap.UN_TYPE.REF_TO_ITEM){
            var elementType=userNotification.unEntry.type;

            var groupType = elementType;
            if (Dime.psHelper.isChildType(elementType)){
                groupType=Dime.psHelper.getParentType(elementType);
            }

            var guid = encodeURIComponent(userNotification.unEntry.guid);
            var userId=userNotification.unEntry.userId;
            if (userId!=='@me'){
                userId=encodeURIComponent(userId);
            }

            target = "self.location.href='index.html?type="+ groupType 
            +"&guid="+guid
            +"&userId="+userId
            +"&dItemType="+elementType
            +"&msg="+unValues.caption
            +"'";

            
        }else{
            target = "self.location.href='index.html?type="+ Dime.psMap.TYPE.USERNOTIFICATION +"'";
        }

        
        var result = $("<div></div>")
        .addClass("notificationElement")
        .attr("onclick", target)
        .text(unValues.shortCaption.substr(0, 16))
        .click(function(){
            userNotification.read=true;
            Dime.REST.updateItem(userNotification);

        });

        return result;
        
    },
    
    updateNotificationBar: function(usernotifications){
        
        Dime.Navigation.receivedNotifications+=usernotifications.length;
        document.getElementById("notificationCounter").innerHTML=Dime.Navigation.receivedNotifications;
            
        var notificationContainer = document.getElementById('innerNotificationContainer');
        //generate some space
        var removeNotificationCount= Math.max(0, 
            (usernotifications.length + Dime.Navigation.shownNotifications)-Dime.Navigation.MAX_NOTIFICATION_ITEMS);
            
        while (removeNotificationCount>0 && notificationContainer.hasChildNodes()){
            JSTool.removelastChild(notificationContainer);
            removeNotificationCount--;
            Dime.Navigation.shownNotifications--;
        }            
            
        for(var i=0; i<usernotifications.length;i++){
            //check whether we received too many notifications to fit into the field
            if (removeNotificationCount>0){
                removeNotificationCount--;
                continue; //skip this
            }                
               
            //show the notification
            var notificationElement = Dime.Navigation.createUserNotificationElement(usernotifications[i]);                    
            JSTool.insertChildAtFront(notificationContainer, notificationElement.get(0));                    
            Dime.Navigation.shownNotifications++;
                
        }
    },
    
    notificationPassesFilter: function(notification){
        if (!notification 
            || !notification.operation 
            || !notification.element
            || !notification.element.guid 
            || !notification.element.userId
            || !notification.element.type){
            console.log("ERROR: received incomplete notification", notification);
            return false;
        }              
        
        return true; 
    },
    
    handleUserNotificationNotifications: function(usernotificationsNotifications){
        if (usernotificationsNotifications.length===0){
            return;
        }

        var handleResponse = function(response){
            var usernotifications = [];
            
            for (var i=0; i<usernotificationsNotifications.length;i++){
                var myGuid = usernotificationsNotifications[i].element.guid;
                var operation = usernotificationsNotifications[i].operation;
                for (var j=0; j<response.length;j++){
                    if ((response[j].guid===myGuid)
                        && (!response[j].read)){

                        usernotifications.push(response[j]);
                    }
                }
            }
            Dime.Navigation.updateNotificationBar(usernotifications);
        };         

         
        Dime.REST.getAll(Dime.psMap.TYPE.USERNOTIFICATION, handleResponse); 
    },

    handleNotification: function(notifications){
    
        if (notifications && $.isArray(notifications)){
            
                        
            var usernotificationsNotifications = [];
            for (var i=0; i<notifications.length;i++){
                var notification = notifications[i];
                if (!Dime.Navigation.notificationPassesFilter(notification)){ //check for filter
                    continue;
                }

                if ((notification.element.type===Dime.psMap.TYPE.USERNOTIFICATION)
                    && (notification.operation==='create')){
                    usernotificationsNotifications.push(notification);                    
                }                
                //clear the cache for type with notification
                Dime.REST.clearCacheForType(notification.element.type, notification.element.userId);
            }
            
            Dime.Navigation.handleUserNotificationNotifications(usernotificationsNotifications);
            Dime.Navigation.updateView(notifications);
        }
        //finally register again and execute
        Dime.Navigation.registerCometCall();
        Dime.initProcessor.executeFunctions();
    },

    updateButtonActiveStatus: function(buttonId, activeButtonId){
        
        if (buttonId===activeButtonId){
            $("#"+buttonId).addClass('active');
        }else{
            $("#"+buttonId).removeClass('active');            
        }
    },

    setButtonsActive: function(buttonId){
         
        Dime.Navigation.updateButtonActiveStatus("navButtonMessages", buttonId);
        Dime.Navigation.updateButtonActiveStatus("navButtonPeople", buttonId);
        Dime.Navigation.updateButtonActiveStatus("navButtonData", buttonId);
        Dime.Navigation.updateButtonActiveStatus("navButtonProfile", buttonId);
        Dime.Navigation.updateButtonActiveStatus("navButtonEvent", buttonId);
        Dime.Navigation.updateButtonActiveStatus("navButtonSettings", buttonId);
        Dime.Navigation.updateButtonActiveStatus("notificationIcon", buttonId);
        Dime.Navigation.updateButtonActiveStatus("currentPlace", buttonId);
        Dime.Navigation.updateButtonActiveStatus("currentSituation", buttonId);

    
    },

    updateSituations: function(){
        
        var handleSituationCallBack=function(response){
            
            var updateSituationElement=function(resultSituation){
                $('#currentSituationText')
                        .textOnly(DimeView.getShortNameWithLength(resultSituation, 31))
                        .attr("title", resultSituation);
            };
            
            var resultSituation = "";
            var moreSituations = false;
            for (var i=0;i<response.length;i++){
                if (response[i].active===true){
                    if(!moreSituations){
                        resultSituation += response[i].name;
                        moreSituations = true;
                    }else{
                        resultSituation = resultSituation + ", " + response[i].name;
                    }
                }
            }
            
            if(resultSituation!=""){
                updateSituationElement(resultSituation);
            }else{
                updateSituationElement("Situation: unknown");
            }
        };
        
        
        Dime.REST.getAll(Dime.psMap.TYPE.SITUATION, handleSituationCallBack);
    },
    
    updateCurrentPlace: function(){
        var handleCurrentPlaceCallBack=function(placeGuidAndNameObject){
            
            var updateCurPlaceElement=function(placeName, placeId){
                var placeElement = document.getElementById('currentPlace');
                placeElement.innerHTML =  '<div class="places">'
                + '<div class="placesIcon" id="currentPlaceGuid" data-guid="' + placeId + '"></div>'
                + DimeView.getShortNameWithLength(placeName, 34)+'</div>';
                $("#currentPlace").attr("title", placeName);
            };
            
            if (!placeGuidAndNameObject || !placeGuidAndNameObject.placeName || placeGuidAndNameObject.placeName===0){
                updateCurPlaceElement("Location: unknown");
                return;
            }
            
            updateCurPlaceElement(placeGuidAndNameObject.placeName, placeGuidAndNameObject.placeId);
            
        };
        Dime.REST.getCurrentPlaceGuidAndName(handleCurrentPlaceCallBack);
        
        
    },
    initNavigation: function(){
        var createNavCorner=function(){
            var userInformation= $('<div/>').attr('id','userInformation')
            .append($('<span/>').attr('id','username').text('748340@dime'))
            .append(
                $('<img/>')
                .attr('src','img/navigation/white/logOut.png')
                .attr('onclick','self.location.href=\'/dime-communications/j_spring_security_logout\'')
                );
            var situationLink = $('<a/>')
            .attr('id','currentSituation')
            .attr('href','index.html?type='+ Dime.psMap.TYPE.SITUATION)
            .append($('<div/>').addClass('clear'))
            .append($('<div/>').addClass('situation').attr('id','currentSituationText')
                .textOnly('Situation: unknown')
                .append($('<div/>').addClass('situationIcon'))
                );
            var placeLink = $('<a/>')
            .attr('id','currentPlace')
            .attr('href','index.html?type='+ Dime.psMap.TYPE.PLACE)
            .append(
                $('<div/>').addClass('places')
                .append($('<div/>').addClass('placesIcon'))
                );

            //TODO fix spoiled naming of classes etc.
            return $('<li/>').attr('id','navCornerMenu')
            .append(
                $('<div/>').attr('id','wrapUserInformation')
                .append(
                    $('<div/>').attr('id','wrapUserInformationBG')
                    .append(userInformation)
                    .append(situationLink)
                    .append(placeLink)
                    ));
        };

        var menuButton=$('<a/>').addClass("btn btn-navbar")
        .attr("data-toggle","collapse")
        .attr("data-target",".nav-collapse")
        .append($('<span/>').addClass('icon-bar'))
        .append($('<span/>').addClass('icon-bar'))
        .append($('<span/>').addClass('icon-bar'));
        var brand = $('<a/>').addClass('brand').attr('href','index.html')
        .append(
            $('<div/>').attr('id','logo')
            .append($('<img/>').attr('src', 'img/logo.png'))
            );

        var navigation = $('<div/>').addClass('nav-collapse')
        .append($('<ul/>').addClass('nav')
            .append(Dime.Navigation.createMenuLiButton("navButtonMessages","Livepost" ,Dime.psMap.TYPE.LIVESTREAM))
            .append(Dime.Navigation.createMenuLiButton("navButtonPeople","People" ,Dime.psMap.TYPE.GROUP))
            .append(Dime.Navigation.createMenuLiButton("navButtonData","My Data" ,Dime.psMap.TYPE.DATABOX))
            .append(Dime.Navigation.createMenuLiButton("navButtonProfile","My Profile Cards" ,Dime.psMap.TYPE.PROFILE))
            .append(Dime.Navigation.createMenuLiButton("navButtonEvent","Calendar" ,Dime.psMap.TYPE.EVENT))
            .append(Dime.Navigation.createMenuLiButtonSettings())
            .append(createNavCorner())
            );
                
        var notificationBar = $('<div/>').addClass('span8').attr('id','notificationContainer')
        .append($('<div/>').addClass('notificationBar')
            .append(Dime.Navigation.createNotificationIcon())
            .append($('<div/>').attr('id','innerNotificationContainer'))
            );
            
        var navContainer = $('<div/>').addClass('container')
        .append(menuButton)
        .append(brand)
        .append(navigation)
        .append(notificationBar)
        ;

        

        var navBarInner=$('<div/>').addClass('navbar-inner').append(navContainer);


        $('#navBarContainer').append(navBarInner);
    }
  
};

Dime.initProcessor.registerFunction( function(callback){

    
    if (!Dime.ps_configuration.createNavigation){ //if no navigation needed skip this
        callback();
        return;
    }
    

    Dime.Navigation.initNavigation();

    callback();
});

/*
 * get server information
 * handler of username
 */
Dime.initProcessor.registerFunction( function(callback){
    
 
    var serverInfoCallBack=function(response){

        Dime.ps_configuration.serverInformation = response;

        if (Dime.ps_configuration.createNavigation){
            var userString = Dime.ps_configuration.mainSaid+'@'+response.name;
            $('#username').text(userString.substr(0, 21)).click(function(){
                DimeView.showAbout.call(DimeView)
            });
        }
        callback();
    };
    Dime.REST.getServerInformation(serverInfoCallBack);
    
});

/*
 * evaluation information
 */
Dime.initProcessor.registerFunction( function(callback){


    var userCallBack=function(response){

        Dime.ps_configuration.userInformation = response;
        callback();
    };
    Dime.REST.getUser(userCallBack);

});


Dime.Navigation.registerCometCall = function(){    

    //register comet call
    Dime.initProcessor.registerFunction( function(callback){
        
        if (!Dime.ps_configuration.createNavigation){ //if no navigation needed skip this
            //FIXME comet call could also make sense without navigation
            callback();
            return;
        }

        Dime.REST.getCOMETCall(Dime.Navigation.handleNotification);
        callback();

    });
};
//initially register once - all subsequent registrations will be done in the error handler
Dime.Navigation.registerCometCall();


//overwrite to update view on notifications incoming
Dime.Navigation.createMenuLiButton=function(id, caption, containerGroupType){

    var linkText = 'index.html?type='+ containerGroupType;
    return $('<li/>').attr('id',id).append($('<a/>').attr('href',linkText).text(caption));
};
//overwrite to update view on notifications incoming
Dime.Navigation.createMenuLiButtonSettings=function(){
    return $('<li/>').attr('id','navButtonSettings').append($('<a/>').attr('href','settings.html').text('Settings'));

};
//overwrite to update view on notifications incoming
Dime.Navigation.createNotificationIcon=function(){
    return $('<div/>').addClass('notificationIcon').attr('id','notificationIcon')
    .append($('<div/>').attr('id','notificationCounter').text("0"));
};


//---------------------------------------------
//#############################################
//  Dime.BasicDialog
//#############################################
//

Dime.BasicDialog = function(title, caption, dialogId, bodyId, body, cancelHandler, okHandler, handlerSelf, okButtonLabel, dialogClass){

    var dialogRef = this;

    if (!okButtonLabel){
        okButtonLabel="Ok";
    }
    this.inactiveButtonClass='inactiveButton';
    this.dialog = document.createElement("div");
    this.dialog.setAttribute("class", "modal");
    this.dialog.setAttribute("id", dialogId);
    this.dialog.setAttribute("role", "dialog");
    this.dialog.setAttribute("aria-labelledby", title);

    this.okButton = $('<button class="YellowMenuButton">'+okButtonLabel+'</button>')
        .click(function(){
            //skip for inactive buttons
            if ($(this).hasClass(dialogRef.inactiveButtonClass)){
                return;
            }
            okHandler.call(handlerSelf);
        });

    
    if(!okHandler){
        this.okButton.addClass(this.inactiveButtonClass);
    }
    

    this.cancelButton = $('<button class="YellowMenuButton" data-dismiss="modal" aria-hidden="true">Cancel</button>')
            .clickExt(handlerSelf, cancelHandler);


    this.footerElement=$('<div></div>').addClass("modal-footer")
        .append(this.cancelButton)
        .append(this.okButton);

    if (dialogClass){
        $(this.dialog).addClass(dialogClass);
    }
    
    $(this.dialog)
    //header
    .append(
        $('<div></div>').addClass("modal-header")
        .append($('<button type="button" class="close" data-dismiss="modal" aria-hidden="true" >x</button>')
            .clickExt(handlerSelf, cancelHandler)
            )
        .append($('<h3 id="myModalLabel">'+caption+'</h3>\n'))            
        )
    //body
    .append(
        $('<div class="modal-body" id="'+bodyId+'" ></div>').append(body)
        )
    //footer
    .append(this.footerElement);
    
    $("#lightBoxBlack").fadeIn(300);
    
    //TODO for each dialog -> removeSelectionDialog()?
    //add ESC(27)-key-event on dialogs to return
    var thisDialog = this.dialog;
    $(document).keyup(thisDialog, function(e) {
        if (e.keyCode == 27) {
            thisDialog.remove();
            $("#lightBoxBlack").fadeOut(300);
            $('body').removeClass('stop-scrolling');
        }
        $(document).unbind("keyup");
    });
};

//---------------------------------------------
//#############################################
//  Dime.SelectDialog
//#############################################
//


/**
 *@param name name of the dialog
* @param selectionName name of the items to be selected     
* @param multiSelect defines whether multi-selection is enabled - default is false
*/
Dime.SelectDialog = function(name, selectionName, multiSelect){
        
    this.dialogId= "SelectDlg_"+JSTool.randomGUID();
    this.bodyId = this.dialogId+"_body";
    this.name = name;
    this.selectionName = selectionName;
    this.entries = {};
    this.multiSelect=false;
        
    this.multiSelect = multiSelect;      
    
    
    //create body
    
    //search
    this.searchTerm = "";
    this.searchElementId=this.dialogId+"_searchElem";
    this.searchInputId=this.dialogId+"_searchInput";
    
    this.searchElement=$('<div id="'+this.searchElementId+'" class="selectionDialgSearchElement" ></div>')
       
    .append($('<input type="text"  class="selectDlgSearchInput selectDlgSearchInputInit"></input>')
        .attr("id",this.searchInputId)
        .keyupExt(this, this.filterSearch)
        .click(function(){
            $(this).removeClass("selectDlgSearchInputInit");
        })
            
        );
   
    //element lists
    this.unselectedListId=this.dialogId+"_unSelList";
    this.selectedListId=this.dialogId+"_selList";
    
    this.lastSelectedElementGuid="";
    this.selectedList= $('<ul/>').attr('id',this.selectedListId).addClass('selectionDialogBodyList');
    this.unselectedList= $('<ul/>').attr('id',this.unselectedListId).addClass('selectionDialogBodyList');
    
    
    this.body = $('<div class="dimeDialogBody selectionDialogBody" ></div>')
    .append(this.searchElement)
    .append($('<div/>').addClass('selectionDialogListCaption').text("Selected:"))
    .append(this.selectedList)
    .append($('<div/>').addClass('selectionDialogListCaption').text("Other:"))
    .append(this.unselectedList);
      
};
  

Dime.SelectDialog.prototype = {
    
    
    
    filterSearch: function(event, jqueryItem){
        this.searchTerm = $("#"+this.searchInputId).val(); 
            
        this.updateLists();
      
    },
    
    updateLists: function(){
        
        this.selectedList.children().detach();
        this.unselectedList.children().detach();
        
        //FIXME add list description selected /all
        
        for (var guid in this.entries){ 
            
            var entry = this.entries[guid];            
            var listItem = entry.element; 
            
            //special handling of the last added selection
            if (entry.selected && (entry.item.guid===this.lastSelectedElementGuid)){
                listItem.addClass("selectedImageListSelected");                    
            }else{
                listItem.removeClass("selectedImageListSelected");                    
            }
            
            //sort items into right UL
            if (entry.selected){
                listItem.addClass("selectedImageList");
                this.selectedList.append(listItem);
            }else{
                listItem.removeClass("selectedImageList");
                if (entry.item.name.toLowerCase().indexOf(this.searchTerm.toLowerCase())!==-1){                
                    this.unselectedList.append(listItem);
                }
            }
        }    
        
        
    },
    
    getDialog: function(){
        return this.dialog.dialog;  
    },
    
    
    removeSelectionDialog: function(){
        //remove dialog if existing
        var dialog = document.getElementById(this.dialogId);
        if (dialog){
            document.body.removeChild(dialog);
            if (!this.bodyWasHidden){
                $('body').removeClass('stop-scrolling');
            }
        }        
    },
    
    getEntry: function(guid){
        var entry = this.entries[guid];
        if (!entry){
            console.log("ERROR - item not found with guid!");
            return null;
        }        
        return entry;
    },
    
    updateItemSelection: function(entry, select){
        
        if (entry.selected===select){
            //no change
            return;
        }
        
        if (select && (!this.multiSelect)){ //clean up other selections first
            for (var myEntry in this.entries){ 
                this.entries[myEntry].selected = false;
            }    
        }               
        entry.selected = select;        
        
        this.updateLists();
    },
    
    handleCancelClick:function(){        
        this.removeSelectionDialog();  
        this.resultFunction.call(this.handlerSelf, [], false);
    },
    
    handleOKClick: function(){
        this.removeSelectionDialog();
        
        var selectedItems = [];
        
        for (var entry in this.entries){ 
            if (this.entries[entry].selected){
                selectedItems.push(this.entries[entry].item);
            }
        }
        this.resultFunction.call(this.handlerSelf, selectedItems, true);       
    },
    
    addItemsToList: function(items){
        
        if (!items){
            console.log("ERROR: items is not defined");
            return;
        }
        
        var toggleSelectFunction=function(event, item, guid){

            var entry= this.getEntry(guid);
            this.lastSelectedElementGuid=guid;
            this.updateItemSelection(entry, (!entry.selected));  
        };
        
        for (var i=0; i<items.length;i++){
            var privTrust=Dime.privacyTrust.getClassAndCaptionForPrivacyTrustFromItem(items[i]);
            
            var listItem = $("<li></li>").addClass("loadImageListItem");    
            
            if (items[i].imageUrl && items[i].imageUrl.length>0){
                listItem.append( '<img class="loadImageListItemImage" src="'
                    +Dime.psHelper.guessLinkURL(items[i].imageUrl)
                    +'" alt="imageUrl image" height="50px" width="50px" />'
                    );
            }
            listItem.append(
                $('<div></div>')
                    .addClass("loadImageListItemName")
                    .append(
                        $('<span>'+ items[i].name.substring(0, 19) +'</span>')
                            .addClass(privTrust.thinClassString)      
                    )
                );
            listItem.clickExt(this, toggleSelectFunction, items[i].guid);
            listItem.mouseover(function(){
                $(this).removeClass("selectedImageListSelected");
            });
            listItem.append($('<div class="selectHintShareDialog"></div>'));
            listItem.hover(function(){
                if($(this).hasClass('selectedImageList')){
                    $(this).children('.selectHintShareDialog').append('(click to remove)');
                }else{
                    $(this).children('.selectHintShareDialog').append('(click to add)');
                }
            }, function(){
                $('.selectHintShareDialog').empty();
            });
            var entry = {
                item: items[i],
                selected: false,
                element: listItem
            };
            
            this.entries[items[i].guid]=entry;
        }        
        
        this.updateLists();
    },
    
    toggleSelectionByGuid: function(entryGuid){
        var entry = this.entries[entryGuid];
        if (entry){
            this.lastSelectedElementGuid=entryGuid;
            this.updateItemSelection(entry, (!entry.selected)); 
                
        }
    },

    selectEntryByGuid: function(entryGuid){
        var entry = this.entries[entryGuid];
        if (entry){
            this.lastSelectedElementGuid=entryGuid;
            this.updateItemSelection(entry, true); 
                
        }
    },

    /**
     * shows the dialog
     * @param loadingFunction function triggered to start loading the items into the list
     *        loading function can use Dime.SelectDialog.addItemsToList to add items with guid to the list
     * @param callbackFunction function handling parameters (selectedItems[], isOK)
     */
    show: function(loadingFunction, callbackFunction, handlerSelf){
        
        this.resultFunction = callbackFunction;
        this.handlerSelf=handlerSelf;
        
        this.dialog = new Dime.BasicDialog(
            this.name, "Select "+this.selectionName,
            this.dialogId,
            this.bodyId, 
            this.body,            
            this.handleCancelClick, this.handleOKClick,
            this,
            "OK",
            "SelectDialog"
        ); 
        
        this.removeSelectionDialog();
        
        document.body.appendChild(this.getDialog());
        this.bodyWasHidden=$('body').hasClass('stop-scrolling');
        $('body').addClass('stop-scrolling');
        
        loadingFunction();    
    }
};


//---------------------------------------------
//#############################################
//  Dime.DetailDialog
//#############################################
//

/**
 *@param caption caption of the dialog
* @param item the items to be shown   
*/
Dime.DetailDialog = function(caption, item, createNewItem, changeImageUrl, isEditable, message, infoHtml){
    
    this.selectedPerson = [];
    
    this.resultFunction= null;
    this.assembleFunctions=[];
    
    this.caption=caption;
    this.item=item;
    this.createNewItem=createNewItem;
    this.changeImageUrl=changeImageUrl;
    
        
    this.dialogId= "DimeDetailDialog_"+JSTool.randomGUID();
    this.bodyId = this.dialogId+"_body";
    
    this.picUploadElementId= this.dialogId+"_picUploadElementId";
    this.imageDetailPicId= this.dialogId+"_imageDetailPicId";
    this.pAValueId=this.dialogId+"_pAValId";
    this.pAValueListItemValueIdPrefix=this.dialogId+"_pAValLstItemValue_";
    this.itemDetailModalTextInput=this.dialogId+'_itemDetailModalTextInput';
    this.placeListContainer=this.dialogId+'plcLstCntr';
    
    this.readonly=(!isEditable);

    

    this.body = $('<div class="dimeDialogBody detailDialogBody" style="clear:both; float:left"></div>');
    this.body.append(Dime.Dialog.Helper.getInfoBox(infoHtml, "detailDialogInfoBox", "detailDialogInfoIcon"));
    if (message && message.length>0){
        this.body.append($('<div/>').addClass('dimeDetailDialogMessage').text(message));
    }

    var okClickHandler=isEditable?this.handleOKClick:null;
        
    this.dialog = new Dime.BasicDialog(
        caption, caption,
        this.dialogId,
        this.bodyId, 
        this.body,            
        this.handleCancelClick, okClickHandler, this);

    if (this.createNewItem){
        this.dialog.okButton.text("Create");
    }
    if (this.readonly){
        this.dialog.okButton.addClass('hidden');
        this.dialog.cancelButton.text('Close');
    }
    
    
};

Dime.DetailDialog.prototype = {

    createIcon: function(item, isEditable){

        var dialogRef = this;

        var thumbNail= $('<img src="' + Dime.psHelper.guessLinkURL(item.imageUrl)
            + '" height=15px" width="15px" ></img>').addClass("itemDetailPicImage");

        if (!isEditable){
            return thumbNail;
        }//else

        //add support for change picture
        var profilePic =$('<div></div>').attr('id', dialogRef.imageDetailPicId).addClass("hidden itemDetailPic")
            .append('<h1>Edit: Image</h1>')
            .append('<img src="' + Dime.psHelper.guessLinkURL(item.imageUrl)
                + '" class="itemDetailPicImageBig" alt="imageUrl image" height=100px" width="100px" ></img>')
            .append('<h2>Select or upload a new icon:</h2>')
            .append(
                $('<div class="itemDetailPicButtons"></div>')
                    .append($('<div class="itemDetailPicSelectBtn btn" >select</div>')
                        .clickExt(dialogRef, dialogRef.selectImageForImageUrl))
                    .append('<h3>or</h3>')
                    .append('<div id="'+dialogRef.picUploadElementId+'" class="itemDetailPicUploadBtn" >upload</div>')
            );
        this.body.append(profilePic);
        //activate thumbNail Click
        thumbNail.addClass('itemDetailPicImageActive').clickExt(dialogRef, dialogRef.toggleImageEdit);

        this.initUploader();

        return thumbNail;
    },

    createNameInput: function(item){
        var prefix=((item.type!==Dime.psMap.TYPE.LIVEPOST)?'name of ':'title of ');
        var nameInput=$('<input type="text"></input>').prop("readonly",this.readonly)
                .addClass("itemDetailNameInput")
                .attr('placeholder',prefix+Dime.psHelper.getCaptionForItemType(item.type))
                .val(item.name);

        //add assemble function for name
        var updateName = function(){
            this.item.name = nameInput.val();
        };
        this.assembleFunctions.push(updateName);

        return nameInput;
    },
     
    removeDialog: function(){
        //remove dialog if existing
        var dialog = document.getElementById(this.dialogId);
        if (dialog){
            $("#lightBoxBlack").fadeOut(300);
            document.body.removeChild(dialog);
            if (!this.bodyWasHidden){
                $('body').removeClass('stop-scrolling');
            }
        }
    },

    
    updateImageUrl: function(imageUrl){
        
        this.item.imageUrl = imageUrl;
        var imagePath = Dime.psHelper.guessLinkURL(imageUrl);
        
        JSTool.updateImageWithIdIfExists(this.imageId, imagePath);
        JSTool.updateImageWithIdIfExists(this.imageIdBig, imagePath);
    },
    
    selectImageForImageUrl: function(event, jqueryItem){
        var updateImageUrlFromSelectionDialog = function(selectedItems, isOK){
        
            if (!isOK){
                return; //was canceled
            }        
            if (!selectedItems || selectedItems.length===0){
                console.log("Error: selectedItems is undefined or nothing was selected");
                return;
            }
            this.updateImageUrl(selectedItems[0].imageUrl);
        };
        
        Dime.Dialog.showImageSelectionList.call(this, updateImageUrlFromSelectionDialog);
        
    },
    
    initUploader: function(){
        
        var self = this;
        
        var uploadedImageUrl = function(id, fileName, responseJSON){
            
            var responseEntries = Dime.REST.handleResponse(responseJSON);
            
            if (responseEntries && responseEntries[0]){            
                self.updateImageUrl(responseEntries[0].imageUrl);
                responseJSON.success=true; //set success == true for processing of result by fileuploader
            }
      
        };
   
        this.uploader = new qq.FileUploader({
            element:document.getElementById(this.picUploadElementId),
            action: Dime.psHelper.getPathForImageUpload(),
            uploadButtonText: 'upload',
            debug: true,
            extraDropzones: [],
            allowedExtensions: ["jpg", "JPG", "png", "PNG", "gif", "GIF"],
            multiple: false,        
            forceMultipart: false,
            onComplete: uploadedImageUrl
        }); 
    },
    
      
    handleCancelClick:function(event, jqueryItem){
        this.removeDialog();  
        this.resultFunction(this.item, false);
    },
    
    assembleItem: function(){
        //execute all assemble functions
        for (var i=0; i<this.assembleFunctions.length;i++){
            this.assembleFunctions[i].call(this);
        }
    },
    
    handleOKClick: function(event, jqueryItem){
        
        this.assembleItem();
        
        
        this.removeDialog();
        this.resultFunction(this.item, true);       
    },
    
    updateProfileAttributeValueItem: function(category){
        var dialogRef=this;

        //reset the value to remove fields that might exist from changed categories
        this.item.value = {}; 
        
        var getValueFromInputfield = function(key){
            var inputElement = $("#"+dialogRef.pAValueListItemValueIdPrefix+key);
            return inputElement.val();
        };
        
        for (var i=0; i<category.keys.length;i++){
            var key = category.keys[i];
            this.item.value[key]=getValueFromInputfield(key);
        }
    },
    
    updateProfileAttributeValueElement: function(category){
        
        //try to remove dialog if existing
        $("#"+this.pAValueId).remove();
        
        //init value with empty object if not exist
        if (!this.item.value){
            this.item.value = {};
        }
        
        var element =$('<div/>').attr('id',this.pAValueId).addClass('h2ModalScreen well DimeDetailDialogPAValues');
        var ul = $('<ul/>');
        element.append(ul);
     
        for (var i=0; i<category.keys.length;i++){
            var key = category.keys[i];
            var keyCaption = category.keyCaptions[i];
            
            if (!this.item.value[key]){
                this.item.value[key]="";
            }
            
            ul.append($('<li class="DimeDetailDialogPAValueListItem" />')                
                .append($('<input class="DimeDetailDialogPAValueListItemValue" type="text"/>')
                    .attr('id',this.pAValueListItemValueIdPrefix+key)
                    .attr('value',this.item.value[key]))
                .append($('<span class="DimeDetailDialogPAValueListItemKey"/>').text(keyCaption))
                );
        }
             
        this.body.append(element);
        
    },    
    
    toggleImageEdit: function(){
        var myImageEditElement = $("#"+this.imageDetailPicId);
        if ($(myImageEditElement).hasClass("hidden")){
            $(myImageEditElement).removeClass("hidden");
        }else{
            $(myImageEditElement).addClass("hidden");
        }
    },
    
    getPrivTrustElement: function(item, readOnly){

        if (!Dime.privacyTrust.hasPrivTrust(item)){
            return $('<span/>');
        }
        
        var createButtonLabel= function(privTrust){
            return '<span class="'+privTrust.thinClassString+'" >'+ privTrust.caption + '</span>';
        };
        
        var dropDownElements=[];
        
        var captions = Dime.privacyTrust.getLevels(item); 
        jQuery.each(captions, function(){
            
            var levelEntry=this;
            var updatePrivTrust=function(){
                Dime.privacyTrust.updatePrivacyTrust(item, levelEntry.limit);
            };            
            
            dropDownElements.push(new BSTool.DropDownEntry(this, createButtonLabel(this), updatePrivTrust));
        });
        var currPrivTrust = Dime.privacyTrust.getClassAndCaptionForPrivacyTrustFromItem(item);
        
        
        var result=$('<div/>')
        .addClass("DetailDialogPrivTrustElem")
        .append('<span >'+(currPrivTrust.isPrivacy?"How private is this: ":"Trust: ")+'</span>');
        if (!readOnly){
            result.append(BSTool.createDropdown(createButtonLabel(currPrivTrust),
                dropDownElements, "btn"));
        }else{
            result.append(createButtonLabel(currPrivTrust));
        }
                
        ;
        return result;
    },

    initProfileAttribute: function(){

        var item=this.item;
        var dialogSelf = this;

         this.body
            .append(this.createNameInput(item));


        //repair category if necessary
        if (!item.category || item.category.length===0){ 
            item.category = Dime.PACategory.getDefaultCategory().name;
        }

        var category = Dime.PACategory.getCategoryByName(item.category);
        if (!category){ //category not found
            category = Dime.PACategory.getDefaultCategory().name;
            item.category = category.name;
        }
        //end repair category if necessary
        this.assembleFunctions.push(function(){
            
            dialogSelf.updateProfileAttributeValueItem(category);
        });
               

        this.updateProfileAttributeValueElement(category);
    },

    createKeyValueInput: function(key, itemValueBase, defaultValue, id, isTextArea, isReadOnly){

        var readOnly = isReadOnly?"readonly":"";
        
        if (!itemValueBase[key]){
            itemValueBase[key] = defaultValue;
        }

        var innerHtml ='<span class="dimeDetailDialogKeyValueCaption">'+id+': </span>';

        if (!isTextArea){
            innerHtml += '<input class="dimeDetailDialogKeyValueValue" id="'+key+'" type="text" value="'
            +itemValueBase[key]+'" ' + readOnly + ' ></input>\n';
        }else{
            innerHtml += '<textarea class="dimeDetailDialogKeyValueValue" id="'+key+'" ' + readOnly + '>'
            +itemValueBase[key]+'</textarea>\n';
        }

        var result =
        $(JSTool.createHTMLElementString("li", key+"KeyValue", ["DimeDetailDialogPAValueListItem"], null, innerHtml));

        var updateKeyValue = function(){

            itemValueBase[key]=$("#"+key).val();
        };
        this.assembleFunctions.push(updateKeyValue);

        return result;
    },
            
    createPlaceDetail: function(item){

        this.body
            .append(this.createIcon(item, false))
            .append(this.createNameInput(item));

        var listContainer =
        $(JSTool.createHTMLElementString("ul", this.placeListContainer, [], null, ""));
        
        //CSS: center the input
        listContainer.attr("style", "margin-right: 100px");

        listContainer.append(
            this.createKeyValueInput.call(this, "information", item, "", "Information", true, true));

        if (!item.address){
            item.address={};
        }

        listContainer.append(
            this.createKeyValueInput.call(this, "streetAddress", item.address, "", "Street address", false, true));
        listContainer.append(
            this.createKeyValueInput.call(this, "postalCode", item.address, "", "Postal code", false, true));
        //FIXME locality seems not to be supported by server?!?
        listContainer.append(
            this.createKeyValueInput.call(this, "locality", item.address, "", "Locality", false, true));
        listContainer.append(
            this.createKeyValueInput.call(this, "region", item.address, "", "Region", false, true));
        listContainer.append(
            this.createKeyValueInput.call(this, "country", item.address, "", "Country", false, true));
        listContainer.append(
            this.createKeyValueInput.call(this, "phone", item, "", "Phone", false, true));
        listContainer.append(
            this.createKeyValueInput.call(this, "position", item, "", "Position", false, true));
        listContainer.append(
            this.createKeyValueInput.call(this, "url", item, "", "Website", false, true));
        listContainer.append(
            this.createKeyValueInput.call(this, "formatted", item.address, "", "Address", true, true));
    
        //adding favorite-checkbox
        listContainer.append(
            $("<li></li>")
                .addClass("DimeDetailDialogPAValueListItem")
                .append(
                    $('<span class="dimeDetailDialogKeyValueCaption"></span>').text("Mark as favorite: ")
                )
                .append(
                    $('<input></input>')
                        .addClass("dimeDetailDialogKeyValueValue")
                        .attr("type", "checkbox")
                        .attr("checked", item.favorite)
                        .attr("id", "favoriteCheckboxLocation")
                        .change(function(){
                            if($(this).attr("checked")){
                                item.favorite = true;
                            }else{
                                item.favorite = false;
                            }
                        })
                    )
            );
    
        //adding rating stars + click-handler
        listContainer.append(
            $("<li></li>")
                .addClass("DimeDetailDialogPAValueListItem")
                .append(
                    $('<span class="dimeDetailDialogKeyValueCaption"></span>').text("Rate this location: ")
                )
                .append(
                    $("<span></span>")
                        .addClass("ratingStarsYour")
                        .raty({
                            score: item.userRating*5,
                            cancel: true,
                            half: true,
                            precision: true,
                            click: function(score, event){
                                //TODO: 0 abfangen -> "cancel"-button
                                item.userRating = score/5;
                            }
                        })
                )
        );    
            
        return listContainer;
    },

    addToLivepostReceiverList: function(items){        
        var dialogRef = this;
        jQuery.each(items, function(){
            dialogRef.receiverList.append(Dime.Dialog.Helper.getAgentElement(this));
        });
        
    },
    
    initLivePost: function(item){
        var dialogRef = this;
        var receivers=this.selectedPerson;
        var fromSaid=null;
        var senderDropdown;
        var sendIsHidden=false;

        var addRemoveClick= function(){
            var newDialog = new Dime.SelectDialog("Share with", "Persons/Groups", true);

            var itemLoadingFunction = function(){
                var loadingHandler = function(response){
                    newDialog.addItemsToList(response);
                    for(var i=0;i<receivers.length;i++){
                        newDialog.selectEntryByGuid(receivers[i].guid);
                    }
                };
                Dime.REST.getAll(Dime.psMap.TYPE.PERSON, loadingHandler);
                Dime.REST.getAll(Dime.psMap.TYPE.GROUP, loadingHandler);

            };
            var handleResult = function(resultItems, isOK){
                if(!isOK){
                    return;
                }
                receivers = resultItems;
                dialogRef.receiverList.empty();
                jQuery.each(resultItems, function(){
                    dialogRef.receiverList.append(
                        Dime.Dialog.Helper.getAgentElement(this)
                    );
                });
                checkAndUpdateSendOk();

            };
            newDialog.show(itemLoadingFunction, handleResult, this);
        };



        var checkAndUpdateSendOk=function(){
            if (dialogRef.readonly){
                return;
            }

            if (fromSaid && fromSaid.length>0 && receivers.length>0){
                dialogRef.dialog.okButton.removeClass('inactiveButton');
                dialogRef.dialog.okButton.text("Share");
                //remove the hint
                dialogRef.dialog.footerElement.find('.livePostHint').remove();
                sendIsHidden=false;
            }else if (!sendIsHidden){
                dialogRef.dialog.okButton.addClass('inactiveButton');
                dialogRef.dialog.okButton.text("Share");
                dialogRef.dialog.footerElement.append($('<span/>').addClass('livePostHint').text('Please select "From" and "Recipients" to share livepost!'));
                sendIsHidden=true;
            }
        };

        var livePostSender = $('<div/>').addClass('livePostSender')
                    .append($('<span/>').addClass('livePostSenderCaption').text("From"));

        //sender
        var updateProfile=function(response){
            var profileDropdown=[];

            jQuery.each(response, function(){
                var entry = this;

                if (!entry.said || !entry.supportsSharing || !entry.editable){
                    return;
                }
                var updateProfileOnClick=function(){
                    fromSaid=entry.said;
                    checkAndUpdateSendOk();
                };

                profileDropdown.push(new BSTool.DropDownEntry(dialogRef, entry.name, updateProfileOnClick));
            });

            senderDropdown = BSTool.createDropdown("Select Profile Card", profileDropdown, "btn-large");
            livePostSender.append(senderDropdown);
        };


        Dime.REST.getAll(Dime.psMap.TYPE.PROFILE, updateProfile, null, this);

        //receivers
        var updateACL=function(){
            if (this.receiverList.length>0 && fromSaid && fromSaid.length>0){
                //actually share the item to the receivers
                var sortedAgents = Dime.psHelper.sortAgents(receivers);

                //update items
                Dime.psHelper.addAccessForItem(sortedAgents.pAgents, sortedAgents.gAgents, sortedAgents.sAgents, item, fromSaid);
            }
        };

        this.receiverList= $('<div/>').addClass('livePostReceiverList').click(addRemoveClick);

        var receiverButton = $('<button/>').text("Add/Remove").click(addRemoveClick);

        //only on new items
        if (this.createNewItem){


            this.assembleFunctions.push(updateACL);

            this.body
                .append(livePostSender)
                .append($('<div/>').addClass('livePostReceiver')
                    .append($('<span/>').addClass('livePostReceiverCaption').text("Recipient(s)"))
                    .append(this.receiverList)
                    .append(receiverButton)
                );
        }
        this.body
            .append($('<div/>').addClass('livePostName')
                .append($('<span/>').text("Subject:"))
                .append(this.createNameInput(item))
            )
            .append(this.getPrivTrustElement(item,this.readonly))
            .append(
                $('<div/>').addClass('DimeDetailDialogText')
                .append(
                    $('<textarea/>').addClass('itemDetailTextInput').prop("readonly",this.readonly)
                    .attr('id',this.itemDetailModalTextInput)
                    .attr('placeholder', 'Write a message ...')
                    .text(item.text)
                )
        );

        //add assemble function for text(
        var updateText = function(){
            this.item.text = $("#"+this.itemDetailModalTextInput).val();
            
        };
        this.assembleFunctions.push(updateText);
        checkAndUpdateSendOk();
    },

    initParentType: function(item){

        this.body
            .append(this.createIcon(item, true))
            .append(this.createNameInput(item));

        if(item.type!==Dime.psMap.TYPE.GROUP){
            this.body.append(this.getPrivTrustElement(item,this.readonly));
        }

        var childType = Dime.psHelper.getChildType(item.type);
            $(this.getDialog()).addClass('shareDlg');

            var shareContainer;
            if (item.type===Dime.psMap.TYPE.GROUP){
                shareContainer = this.getCanAccessItems(item);
            }else{
                shareContainer = this.getSharedToItems(item);
            }

            //add containers
            this.body.append(
                $("<div></div>")
                .addClass("dimeDialogBody_editContainer")
                //left container
                .append($("<div/>")
                    .addClass("dimeDialogBody_editContainerLeft")
                    .append($("<span class=label></span>")
                        .text(Dime.psHelper.getPluralCaptionForItemType(childType)))
                    .append(this.getChildTypeItems(item, childType)))
                //right container
                .append($("<div>")
                    .addClass("dimeDialogBody_editContainerRight")
                    .append($("<span class=label></span>")
                        .text(item.type===Dime.psMap.TYPE.GROUP?"can access":"shared with"))
                    .append(shareContainer))
            );

    },

    initResource: function(item){
        
        this.body
            .append(this.createIcon(item, false))
            .append(this.createNameInput(item))
            .append(this.getPrivTrustElement(item,this.readonly));

        if (item.downloadUrl && item.downloadUrl.length>0){
            var innerHtml = '<a href="' + Dime.psHelper.guessLinkURL(item.downloadUrl) + '" target="_blank">open</a>';
            this.body.append(
                $(JSTool.createHTMLElementString("div", null, ["dimeDetailDialogLink"], null, innerHtml)));
        }
    },

    initSituation: function(item){

        this.body
            .append(this.createIcon(item, false))
            .append(this.createNameInput(item));

        var innerHtmlSituation =
            '<input id="'+this.itemDetailModalTextInput+'" type="checkbox" '
            + (item.active?'checked ':'')+'>situation is active</input>\n';

        this.body.append(
            $(JSTool.createHTMLElementString("div", "DimeDetailDialogSituation", [], null, innerHtmlSituation)));

        //add assemble function for text
        var updateSituation = function(){
            this.item.active = $("#"+this.itemDetailModalTextInput).prop("checked");
        };
        this.assembleFunctions.push(updateSituation);

    },
    initPerson: function(item){
        this.body
            .append(this.createIcon(item, true))
            .append(this.createNameInput(item))
            .append(this.getPrivTrustElement(item,this.readonly));

    },
    
    initDetails: function(){
        
        var item = this.item;        
        
        //add different details for specific item types
        if (Dime.psHelper.isParentType(item.type)){
            this.initParentType(item);
            
        }else if (item.type===Dime.psMap.TYPE.PERSON){
           this.initPerson(item);

        }else if (item.type===Dime.psMap.TYPE.LIVEPOST){
           this.initLivePost(item);
            
        }else if (item.type===Dime.psMap.TYPE.RESOURCE){
            this.initResource(item);
            
        }else if (item.type===Dime.psMap.TYPE.PROFILEATTRIBUTE){            
            this.initProfileAttribute();
            
        }else if (item.type===Dime.psMap.TYPE.SITUATION){
            this.initSituation(item);
            
        }else if (item.type===Dime.psMap.TYPE.PLACE){
            this.body.append(this.createPlaceDetail(item));
            
            //TODO: move this to createPlaceDetail()
            var currentPlaceGuid = document.getElementById("currentPlaceGuid").getAttribute("data-guid");
            if(item.guid == currentPlaceGuid){
                //samePlace
                var button = $('<div></div>')
                            .addClass("setCurrentPlaceButton")
                            .attr("href", "#")
                            .clickExt(this, Dime.psHelper.postUpdateCurrentPlace, item, true)
                            .append("Remove this as current location");
            }else{
                //otherPlace
                var button = $('<div></div>')
                            .addClass("setCurrentPlaceButton")
                            .attr("href", "#")
                            .clickExt(this, Dime.psHelper.postUpdateCurrentPlace, item, false)
                            .append("Set as current location (valid today)");
            }
                   
            this.body.append(button);   
        }   
        
    },
    
    getCanAccessItems: function(item){
        var editDlgRef = this;
        var itemsArray = [];
             
        editDlgRef.itemsItemSection = $('<div/>');
        
        //getting accessible items of group
        var handleGetAllSharedItems = function(resultItems){
            for(var i=0; i<resultItems.length;i++){
                itemsArray.push(resultItems[i]);
            }

            editDlgRef.updateChildTypeContainer(editDlgRef.itemsItemSection, itemsArray);
        };
        Dime.psHelper.getAllSharedItems(item, handleGetAllSharedItems);
        
        
        //itemSection clickable + newDialog
        var showAddItemsDlg = function(event, jqueryItem){
            var newDialog = new Dime.SelectDialog("Share with", "Items", true);
            
            var itemLoadingFunction = function(){
                var loadingHandler = function(response){
                    newDialog.addItemsToList(response);
                    for(var i=0;i<itemsArray.length;i++){
                        newDialog.selectEntryByGuid(itemsArray[i].guid);
                    }
                };
                Dime.REST.getAll(Dime.psMap.TYPE.LIVEPOST, loadingHandler);        
                Dime.REST.getAll(Dime.psMap.TYPE.DATABOX, loadingHandler);        
                Dime.REST.getAll(Dime.psMap.TYPE.RESOURCE, loadingHandler); 
            };
            var handleResult = function(resultItems, isOK){
                if(!isOK){
                    return;
                }
                //TODO: update items on server!
                itemsArray = resultItems;
                editDlgRef.updateChildTypeContainer(editDlgRef.itemsItemSection, resultItems);
            };
            newDialog.show(itemLoadingFunction, handleResult, this);
        };
        return $('<div class="shareDlgSection shareDlgSectionHover"></div>')
        
        //FIXME interaction disabled
        //.clickExt(this, showAddItemsDlg);
            .click(function(){
                (new Dime.Dialog.Toast('Function not yet available. Please use the "Share..." button for sharing.')).showLong();
            })
            .append(editDlgRef.itemsItemSection)
            
            //additional hint for sharing items
            .append(
                $('<div/>').addClass('changeDetailsHintshareDialog').append("click to change items")
            );
      
    },            

    getSharedToItems: function(item){

        var editDlgRef = this;

        editDlgRef.itemsItemSection = $('<div/>');

        var createAgentListItem = function(name, value, imageUrl, className){

            if (value===undefined || value===null){
                //show undefined and null values in the browsers
                //while empty string is allowed
                value="undefined";
            }else if (value.length>22){getSharedToItems
                value=value.substr(0, 20)+"..";
            }

            var listIconElement=$('<div/>').addClass('listElementIcon');
            if (imageUrl){
                listIconElement.append('<img src="' + imageUrl+ '"/>');
            }
            var listItem=$('<li/>').addClass('listElement');
            if (className){
                listItem.addClass(className);
            }
            
            listItem.append(
                $('<div class="listElementText"/>')
                .append($('<span class="listElementTextName"/>').text(name))
                .append($('<span class="listElementTextValue"/>').text(value))
                )
            .append(listIconElement);
            return listItem;
        };


        var addAgentsToContainer=function(listItemContainer, agentItems){
            for (var j=0;j<agentItems.length;j++){
                listItemContainer.append(createAgentListItem( "", agentItems[j].name, agentItems[j].imageUrl, "metaDataShareItem"));
            }
        };

        //getting accessible items of group
        var handleGetAllReceivers = function(acl){

            if (acl.length===0){
                return;
            }

            for (var i=0;i<acl.length;i++){ //TODO adapt for more information (e.g. show said) in meta-bar
                var aclPackage = acl[i];
                if (!aclPackage.saidSender){
                    console.log("ERROR: oops - received aclPackage without saidSender - skipping!", aclPackage);
                    continue;
                }
                var writeACLPackage = function(profile){
                    var pName = (profile?profile.name:"No profile for "+aclPackage.saidSender);
                    var pImage = (profile?profile.imageUrl:null);

                    var profileContainerList = $('<ul/>');

                    addAgentsToContainer(profileContainerList, aclPackage.groupItems);
                    addAgentsToContainer(profileContainerList, aclPackage.personItems);
                    addAgentsToContainer(profileContainerList, aclPackage.serviceItems);

                    var profileContainer = createAgentListItem("shared as:", pName, pImage, "metaDataShareProfile")
                    .append(profileContainerList);

                    editDlgRef.itemsItemSection.append(profileContainer);
                };
                
                Dime.psHelper.getProfileForSaid(aclPackage.saidSender, writeACLPackage);
            }

        };

        Dime.psHelper.getAllReceiversOfItem(item, handleGetAllReceivers);                                                                                                      
        var agentsSection = $('<div class="shareDlgSection shareDlgSectionHover"></div>')
            .click(function(){
                (new Dime.Dialog.Toast('Function not yet available. Please use the "Share..." button for sharing.')).showLong();
            })
            .append(editDlgRef.itemsItemSection);
    
        //additional hint for sharing
        agentsSection.append(
            $('<div/>').addClass('changeDetailsHintshareDialog').append("click to change receivers")
        );

        return agentsSection;
    },


    getChildTypeItems: function(item, childType){
        var editDlgRef = this;
        var memberArray = [];
        var parentCaption = Dime.psHelper.getCaptionForItemType(item.type);
        var childCaption = Dime.psHelper.getPluralCaptionForItemType(childType);
        
        var memberSection = $('<div class="shareDlgSection shareDlgSectionHover"></div>');
        
        this.memberItemSection = $("</div>");
        memberSection.append(this.memberItemSection);
        
        //getting members of group as objects
        var callbackFunction = function(response){
            jQuery.each(response, function(){
                var entry = this;
                for (var i=0;i<item.items.length;i++){
                    if (item.items[i]===entry.guid){
                        memberArray.push(entry);
                    }
                }
            });
        };
        Dime.REST.getAll(childType, callbackFunction);
        
        this.updateChildTypeContainer(memberSection, memberArray, item.type, childCaption);
        
        
        //memberSection clickable + newDialog
        var showAddMembersDlg = function(event, jQueryItem){
            var newDialog = new Dime.SelectDialog("Add "+childCaption, childCaption+" in the "+parentCaption, true);
            
            var memberLoadingFunction = function(){
                var loadingHandler = function(response){
                    newDialog.addItemsToList(response);
                    for(var i=0;i<item.items.length;i++){
                        newDialog.selectEntryByGuid(item.items[i]);
                    }
                };
                Dime.REST.getAll(childType, loadingHandler);
            };
            var handleResult = function(resultMembers, isOK){
                if(!isOK){
                    return;
                }
                //clear and set item guid
                item.items = [];
                for(var i=0;i<resultMembers.length;i++){
                    item.items.push(resultMembers[i].guid);
                }
                editDlgRef.updateChildTypeContainer(memberSection, resultMembers, item.type, childCaption);
            };
            newDialog.show(memberLoadingFunction, handleResult, this);
        };
        if (!this.readonly){
            memberSection.clickExt(this, showAddMembersDlg);
            
            //additional hint for "click to change"
            memberSection.append(
                $('<div/>').addClass('changeDetailsHintshareDialog').append("click to change " + childCaption)
            );
        }else{
            memberSection.click(function(){
                (new Dime.Dialog.Toast(editDlgRef.caption + " ... is readonly and can not be updated.")).showLong();
            });

        }
        
        return memberSection;
    },
            
    updateChildTypeContainer: function(container, selectedMembers, parentType, childCaption){
        if(!container){
            return;
        }
        
        //remove all child nodes of the container
        container.empty();
        
        if(selectedMembers.length===0){
            if (!childCaption){
                childCaption="item";
            }
            if(parentType){
                container.append("No "+childCaption+" in the "+
                    Dime.psHelper.getCaptionForItemType(parentType));
            }else{
                container.append("No "+childCaption);
            }
            return;
        }
        
        $.each(selectedMembers, function(){
            var element = Dime.Dialog.Helper.getAgentElement(this);
            container.append(element);
        });  
    },
    
    getDialog: function(){
        return this.dialog.dialog;  
    },
    
    showDetailDialog: function(callbackFunction){
    
        this.resultFunction = callbackFunction;
        
        this.removeDialog();
        
        document.body.appendChild(this.getDialog());
        this.bodyWasHidden=$('body').hasClass('stop-scrolling');
        $('body').addClass('stop-scrolling');
        
        //add further details
        this.initDetails();
    }
};



//---------------------------------------------
//#############################################
//  Dime.ShareDialog
//#############################################
//

Dime.ShareDialog = function(){ 
    this.selectedReceivers=[];
    this.selectedItems=[];
    this.selectedProfile=null;
    
    this.dialogId= "ShareDlg_"+JSTool.randomGUID();
    this.bodyId = this.dialogId+"_body";
    
    this.dialog = $("<div></div>")
    .addClass("modal").addClass("shareDlg")
    .attr("id", this.dialogId)
    .attr("role", "dialog")
    .attr("aria-labelledby", "Share Dialog");
    
    this.body = $('<div class="modal-body" id="'+this.bodyId+'" ></div>');

    this.okButton = $('<button class="YellowMenuButton">Share</button>')
            .clickExt(this, this.okHandler).addClass('inactiveButton');

    this.dialog
    //header
    .append(
        $('<div></div>').addClass("modal-header")
        .append($('<button type="button" class="close" data-dismiss="modal" aria-hidden="true" >x</button>')
            .clickExt(this, this.cancelHandler)
            )
        .append($('<h3 id="myModalLabel">Share</h3>\n'))
        )
    //body
    .append(this.body)
    //footer
    .append(
        $('<div></div>').addClass("modal-footer")
        .append($('<button class="YellowMenuButton" data-dismiss="modal" aria-hidden="true">Cancel</button>')
            .clickExt(this, this.cancelHandler)
            )
        .append(this.okButton)
    );
            
    this.initBody();
    
    //TODO for each dialog -> removeSelectionDialog()?
    //add ESC(27)-key-event on dialogs to return
    var thisDialog = this.dialog;
    $(document).keyup(thisDialog, function(e) {
        if (e.keyCode == 27) {
            thisDialog.remove();
            $("#lightBoxBlack").fadeOut(300);
            $('body').removeClass('stop-scrolling');
        }
        $(document).unbind("keyup");
    });

};

Dime.ShareDialog.prototype={
    
    removeDialog:function(){
        //remove dialog if existing
        var dialog = document.getElementById(this.dialogId);
        if (dialog){
            $("#lightBoxBlack").fadeOut(300);
            document.body.removeChild(dialog);
            if (!this.bodyWasHidden){
                $('body').removeClass('stop-scrolling');
            }
        }
    },
    
    cancelHandler:function(){
        this.removeDialog();  
        this.resultFunction.call(this.handlerSelf, false);
        
    },
    
    okHandler: function(){
        if (!this.checkValidity()){
            window.alert("Please make sure a profile, a recipient and an item has been selected!");
            return;
        }

        this.removeDialog();
        this.resultFunction.call(this.handlerSelf, true, this.selectedProfile, this.selectedReceivers, this.selectedItems);

    },
    
    checkValidity: function(){
        return ((this.selectedReceivers.length>0)
            && (this.selectedItems.length>0)
            && (this.selectedProfile!==null));
    },
    
    updateWarnings: function(){
        
        if (!this.warnings || !this.warningsLabel){
            //this can be the case when this function is called while creating the dialog
            return;
        }
        
        this.warnings.empty();
        
        if (!this.checkValidity()){
            //this.warnings.append($('<div>Please select sender, recipient and items ...</div>').addClass("shareDlgWarn"));
            
            this.warningsLabel.text("Warnings (0)");
            return;
        }
        
        
        var shareDlgRef=this;
        
        var updateWarningView = function(entries, allMyData){

            //sort
            entries = Dime.psHelper.sortWarningsByLevel(entries);
                        
            var firstEntry=true;
                        
            jQuery.each(entries, function(){
                var advisory = new Dime.AdvisoryItem(this);
                
                var warnText=$('<div class="shareDlgWarnText well">'
                    +advisory.getTextForWarning(shareDlgRef.selectedReceivers, shareDlgRef.selectedItems, allMyData)
                    +'</div>');
            
                warnText.addClass("hidden");
                      
                shareDlgRef.warnings.append($('<div></div>').addClass("shareDlgWarn")
                    .append($('<div/>')
                        .append(advisory.getIconForWarning())
                        .append($('<span class="shareDlgWarnType">'+advisory.getTypeText()+'</div>'))                        
                        .append($('<span class="caret">more</span>'))
                        ).click(function(){
                            $(".shareDlgWarnText").addClass("hidden");
                            warnText.toggleClass("hidden");
                        })
                    .append(warnText)
                    );
            });
            
            shareDlgRef.warningsLabel.text("Warnings ("+entries.length+")");
            
        };
        
        var receivers=[];
        jQuery.each(this.selectedReceivers, function(){
            receivers.push(this.guid);
        });
        
        var items=[];
        jQuery.each(this.selectedItems, function(){
            items.push(this.guid);
        });

        var getAllMyDataAndUpdateWarningView=function(advisory){
            var allMyData=new Dime.AllMyDataContainer();

            var loadingDone=function(){
                updateWarningView(advisory, allMyData);
            };

            allMyData.load(loadingDone);
        };

        Dime.REST.postAdvisoryRequest(this.selectedProfile.guid, receivers, items, getAllMyDataAndUpdateWarningView, this);
    },
    
    getProfile: function(){
        
        var dialogRef=this;
        var profileSection=$('<div class="shareDlgSection"></div>');
        
        var updateProfile=function(response){
            var profileDropdown=[];
            
            jQuery.each(response, function(){
                var entry = this;
                
                if (!entry.said || !entry.supportsSharing || !entry.editable){
                    return;
                }
                
                var updateProfileOnClick=function(){
                    dialogRef.selectedProfile=entry;
                    dialogRef.updateView.call(dialogRef);
                        
                };
                                
                profileDropdown.push(new BSTool.DropDownEntry(dialogRef, entry.name, updateProfileOnClick));
            });
        
            profileSection.append(BSTool.createDropdown("Select Profile Card", profileDropdown, "btn-large"));
        };
        
        Dime.REST.getAll(Dime.psMap.TYPE.PROFILE, updateProfile, null, this);
        return profileSection;
        
    },
    
    updateContainer: function(container, selectedItems){
        
        if (!container){
            //this can be the case when this function is called while creating the dialog
            return;
        }
        
        container.empty();
        
        jQuery.each(selectedItems, function(){
            var item=this;
            
            var privTrustClass=Dime.privacyTrust.getClassAndCaptionForPrivacyTrustFromItem(item).thinClassString;
            
            var element = Dime.Dialog.Helper.getAgentElement(this);
            container.append(element);                    
        });
    },
    
    updateView: function(){
        //update receivers
        this.updateContainer(this.receiverItemSection, this.selectedReceivers);
        if (this.receiversLabel){
            this.receiversLabel.text("Recipients ("+this.selectedReceivers.length+")");
        }
          
        //update items
        this.updateContainer(this.itemItemsSection, this.selectedItems);
        if(this.itemsLabel){
            this.itemsLabel.text("Items ("+this.selectedItems.length+")");
        }
        //update share button
        if(this.checkValidity()){
            this.okButton.removeClass('inactiveButton');
        }else{
            this.okButton.addClass('inactiveButton');
        }

        //update warnings
        this.updateWarnings();
    },

    filterNonSharableAgents: function(allAgents){
        var result=[];
        for (var i=0;i<allAgents.length;i++){
            if (Dime.psHelper.isSharableAgent(allAgents[i])){
                result.push(allAgents[i]);
            }
        }
        return result;
    },
    
    getReceivers: function(){
        var shareDlgRef=this;
        
        var receiverSection = $('<div class="shareDlgSection shareDlgSectionHover"></div>');
            
        this.receiverItemSection = $('<div/>');
        receiverSection.append(this.receiverItemSection);
        
         
        var showAddReceiverDlg=function(event, jqueryItem){
            var dialog = new Dime.SelectDialog("Share With", "recipients for sharing", true);
       
            var itemLoadingFunction = function(){   

                var loadingHandler = function(agents){
                    var shareableAgents = shareDlgRef.filterNonSharableAgents(agents);

                    dialog.addItemsToList(shareableAgents);
                    for (var i=0;i<shareDlgRef.selectedReceivers.length;i++){
                        var receiver = shareDlgRef.selectedReceivers[i];
                        dialog.selectEntryByGuid(receiver.guid);
                    }
                };
                //set loading functions to get all groups and persons
                Dime.REST.getAll(Dime.psMap.TYPE.GROUP, loadingHandler);        
                Dime.REST.getAll(Dime.psMap.TYPE.PERSON, loadingHandler);        

            };
            var handleResult = function(resultItems, isOK){
           
                if (!isOK){
                    return;
                }
                shareDlgRef.selectedReceivers=resultItems;    
                shareDlgRef.updateView();
            };
        
            dialog.show(itemLoadingFunction, handleResult, this);
        };
        
        receiverSection.clickExt(this, showAddReceiverDlg);
        
        receiverSection.append(
                $('<div/>').addClass('changeDetailsHintshareDialog').append("click to change recipients")
            );
        
        return receiverSection;
    },
    
    getItems: function(){
        var shareDlgRef=this;
        
        var itemSection = $('<div class="shareDlgSection shareDlgSectionHover"></div>');
            
        this.itemItemsSection = $('<div/>');
        itemSection.append(this.itemItemsSection);
        
         
        var showAddItemsDlg=function(event, jqueryItem){
            var dialog = new Dime.SelectDialog("Share With", "Items", true);
       
            var itemLoadingFunction = function(){   

                var loadingHandler = function(response){
                    dialog.addItemsToList(response);
                    for (var i=0;i<shareDlgRef.selectedItems.length;i++){
                        dialog.selectEntryByGuid(shareDlgRef.selectedItems[i].guid);
                    }
                };
                //set loading functions to get all resouces, databoxes and liveposts
                Dime.REST.getAll(Dime.psMap.TYPE.LIVEPOST, loadingHandler);        
                Dime.REST.getAll(Dime.psMap.TYPE.DATABOX, loadingHandler);        
                Dime.REST.getAll(Dime.psMap.TYPE.RESOURCE, loadingHandler);      

            };
            var handleResult = function(resultItems, isOK){
           
                if (!isOK){
                    return;
                }
                shareDlgRef.selectedItems=resultItems;
                shareDlgRef.updateView();
            };
        
            dialog.show(itemLoadingFunction, handleResult, this);
        };
        
        itemSection.clickExt(this, showAddItemsDlg);
        
        itemSection.append(
                $('<div/>').addClass('changeDetailsHintshareDialog').append("click to change items")
            );
        
        return itemSection;
    },
        
    
    initBody: function(){
        
        
        this.profile=this.getProfile();
       
        this.receiversLabel=$('<span class="label">Recipients (0)</span>');
        this.receivers=this.getReceivers();
        
        this.itemsLabel=$('<span class="label">Items (0)</span>');        
        this.items=this.getItems();
        
        this.warningsLabel=$('<span class="label">Warnings (0)</span>');        
        this.warnings=$('<div class="shareDlgWarnings"></div>');

        var infoMsg="Sharing in di.me<br/><br/>When sharing something, you allow the recipient to get full access on the shared resource. Even when \"unsharing\" it later, the recipient may still be able to keep a copy of it. ";

        this.body.append(
            $('<div class="well"></div>')  
            .append($('<span class="label">Share as</span>'))
            .append(this.profile)
            .append(this.receiversLabel)
            .append(this.receivers)
            .append(this.itemsLabel)
            .append(this.items)
            )
        
        .append(
            $('<div class="well"></div>')
            .append(Dime.Dialog.Helper.getInfoBox(infoMsg, "shareInfoBox"))
            .append(this.warningsLabel)
            .append($('<div class="shareDlgSection"></div>').append(this.warnings))
            );
        
        
    },
    
    show: function(handlerSelf, callback){
        this.handlerSelf = handlerSelf;
        this.resultFunction=callback;
        
        this.bodyWasHidden=$('body').hasClass('stop-scrolling');
        $('body').append(this.dialog).addClass('stop-scrolling');
    }

};


Dime.ConfigurationDialog = function(handlerSelf, okHandler){
    this.handlerSelf = handlerSelf;
    this.okHandler = okHandler;
};

Dime.ConfigurationDialog.prototype = {
    
    //FIX: refactor -> better solution in show()?!
    showAuth: function(adapterName, adapterDescription, adapterAuthUrl){
        
        this.modal = document.createElement("div");
        this.modal.setAttribute("id", "ConfigServiceWrapperID");
        $(this.modal)
            .append(
                $("<div></div>").addClass("ConfigServiceDescription")
                //.append('<img src="' + serviceAccount.imageUrl + '" alt="service logo">')
                .append("<b>" + adapterName + "</b></br>")
                .append(adapterDescription)
                .append(
                    $("<p></p>").append('</br>If you click on "Ok" you will be redirected to ' + adapterName + '.')
                )
            );
                
        var myOkHandler = function(){
            window.open(adapterAuthUrl, "_blank", "");
            $("#lightBoxBlack").fadeOut(300);
            $('#ConfigServiceDialogID').remove();
        };

        var myCancelHandler = function() {
            $("#lightBoxBlack").fadeOut(300);
            $('#ConfigServiceDialogID').remove();
        };
               
        var dialog = new Dime.BasicDialog('ServiceModal', "Create new account", 'ConfigServiceDialogID', 'ConfigServiceBodyID', this.modal, myCancelHandler , myOkHandler, this);
        $('body').append(dialog.dialog);
    },

    show: function(adapterName, adapterDescription, serviceAccount, isNewAccount) {

        console.log(serviceAccount);
        var inputs = [];
        this.modal = document.createElement("div");
        this.modal.setAttribute("id", "ConfigServiceWrapperID");
        var dialogSelf=this;

        var setForm = function(){
            for(var i = 0; i < serviceAccount.settings.length; i++){
                //generate input fields
                $(this)
                .append(
                    $('<div id="InputWrapperID_' + serviceAccount.settings[i].name + '"></div>')
                    .addClass("control-group")
                    .append(function(){

                        switch(serviceAccount.settings[i]['fieldtype']){
                            case "string":
                            case "password":
                                if(serviceAccount.settings[i]['fieldtype'] === "string"){
                                    var type = "text";
                                    var value = serviceAccount.settings[i].value;
                                }else{
                                    var type = "password";
                                    //security issue: plain text?!
                                    var value = serviceAccount.settings[i].value;
                                }

                                var textInput =
                                $("<input></input>")
                                .attr("type", type)
                                .addClass("controls")
                                .attr("id", "InputFieldID_" + serviceAccount.settings[i].name)
                                .attr("value", value)
                                .attr("placeholder", serviceAccount.settings[i].name);
                                //required-attribute for HTML5-highlighting
                                if(serviceAccount.settings[i].mandatory){
                                    textInput.attr("required", "required");
                                }

                                $(this)
                                .append(
                                    $("<label></label>")
                                    .attr("for", "InputFieldID_" + serviceAccount.settings[i].name)
                                    .attr("class", "control-label")
                                    .append(serviceAccount.settings[i]['name'].substr(0, 1).toUpperCase()
                                        + serviceAccount.settings[i]['name'].substr(1, this.length) + ": ")
                                    )
                                .append(textInput);

                                inputs.push({
                                    type: type,
                                    name: serviceAccount.settings[i].name,
                                    mandatory: serviceAccount.settings[i].mandatory,
                                    value: function(){
                                        return textInput.val();
                                    },
                                    validation: function(){
                                        return (textInput.val()!=="");
                                    }
                                });
                                break;

                            case "account": //profile

                                var profileDropdown=[];
                                var appendTarget = $(this);
                                var mySettings = serviceAccount.settings[i];

                                //fill-in dropdown list with profiles
                                var callback = function(response){
                                    var caption = "Please select a profile:";
                                    $.each(response, function(index, value) {
                                        if (mySettings.value && value.said===mySettings.value){
                                            caption=value.name;
                                        }

                                        var updateProfileOnClick=function(){
                                            dialogSelf.selectedProfile=value;
                                            console.log("profile", value, "said", value.said);
                                        };
                                        if (value.said && value.said.length>0){
                                            profileDropdown.push(new BSTool.DropDownEntry(dialogSelf, value.name, updateProfileOnClick));
                                        }
                                        
                                    });


                                    appendTarget.append(BSTool.createDropdown(caption, profileDropdown, "btn-large"));
                                };
                                Dime.REST.getAll(Dime.psMap.TYPE.PROFILE, callback);
                                //at the end: setting focus on picked profile (for edit)

                                inputs.push({
                                    type: "select",
                                    name: serviceAccount.settings[i].name,
                                    mandatory: serviceAccount.settings[i].mandatory,
                                    value: function(){
                                        return dialogSelf.selectedProfile.said;
                                    },
                                    validation: function(){
                                        return (dialogSelf.selectedProfile!==undefined);
                                    }
                                });
                                break;

                            case "link":
                                var linkInput =
                                $("<a></a>")
                                .attr("class", "controls")
                                .attr("href", serviceAccount.settings[i].value)
                                .attr("target", "_blank")
                                .append("Click here to read the terms");

                                $(this)
                                .append(
                                    $("<label></label>")
                                    .attr("class", "control-label")
                                    .append("Please read the TOC: ")
                                    )
                                .append(linkInput);

                                inputs.push({
                                    type: "link",
                                    name: serviceAccount.settings[i].name,
                                    //mandatory: serviceAccount.settings[i].mandatory,
                                    value: function(){
                                        return linkInput.attr('href');
                                    },
                                    validation: function(){
                                        return true;
                                    }
                                });
                                break;

                            case "boolean":
                                var checkInput =
                                $("<input></input>")
                                .attr("type", "checkbox")
                                .attr("id", "InputFieldID_" + serviceAccount.settings[i].name)
                                .attr("name", "InputFieldName_" + serviceAccount.settings[i].name)
                                .attr("checked", isNewAccount ? false : true);

                                $(this)
                                .append(
                                    $('<div></div>')
                                    .addClass("controls-checkbox")
                                    .append(
                                        $("<label></label>")
                                        .addClass("checkbox")
                                        .append(checkInput)
                                        .append("<span>I have read the terms.</span>")
                                        )
                                    );

                                inputs.push({
                                    type: "boolean",
                                    name: serviceAccount.settings[i].name,
                                    mandatory: serviceAccount.settings[i].mandatory,
                                    value: function(){
                                        return checkInput.is(':checked');
                                    },
                                    validation: function(){
                                        return checkInput.is(':checked');
                                    }
                                });
                                break;
                        }
                    })
                    );
            }
        };
        
        var deleteAcc = "";
        if(!isNewAccount){
            deleteAcc = $('<div></div>')
            .addClass("ConfigServiceDeleteAccount")
            .attr("href", "#")
            .clickExt(Dime.Settings, Dime.Settings.deactivateServiceAccount, serviceAccount)
            .append("Delete");
        }

        $(this.modal)
        //service information and description
        .append(
            $("<div></div>").addClass("ConfigServiceDescription")
            //.append('<img src="' + serviceAccount.imageUrl + '" alt="service logo">')
            .append("<b>" + adapterName + "</b></br>")
            .append(adapterDescription)
            //TODO: CSS
            )
        //create form
        .append(
            $("<form action='#' id='ConfigServiceFormID'></form>").addClass("form-horizontal")
            .append(setForm)
            )
        //add option for deletion
        .append(deleteAcc);

        //preparing dialog
        var title = isNewAccount ? 'Create new account' : 'Edit account';

        var myOkHandler = function(){
            //getting input values and set them into account
            var val = true;
            for(var i=0; i<serviceAccount.settings.length; i++){
                serviceAccount.settings[i].value = inputs[i].value();
                val = val && inputs[i].validation();
                if(inputs[i].mandatory){
                    if(inputs[i].validation()){
                        $(".modal-body #" + "InputWrapperID_" + inputs[i].name).removeClass("formValidationFalse");
                    }else{
                        $(".modal-body #" + "InputWrapperID_" + inputs[i].name).addClass("formValidationFalse");
                    }
                }else{
                    $(".modal-body #" + "InputWrapperID_" + inputs[i].name).removeClass("formValidationFalse");
                }
            }

            if(val){
                $("#lightBoxBlack").fadeOut(300);
                $('#ConfigServiceDialogID').remove();
                this.okHandler.call(this.handlerSelf, serviceAccount, isNewAccount);
            }
        };

        var myCancelHandler = function() {
            $("#lightBoxBlack").fadeOut(300);
            $('#ConfigServiceDialogID').remove();
        };

        //show dialog
        var dialog = new Dime.BasicDialog('ServiceModal', title, 'ConfigServiceDialogID', 'ConfigServiceBodyID', this.modal, myCancelHandler , myOkHandler, this);
        $('body').append(dialog.dialog);

        //setting focus (selected) on picked profile
        if(!isNewAccount){
            for(var k = 0; k < serviceAccount.settings.length; k++){
                if(serviceAccount.settings[k].name === "profileID"){
                    var value = serviceAccount.settings[k].value;
                    $('#InputFieldID_profileID option:contains(' + value  + ')')
                    .attr("selected", "selected");
                }
            }
        }
    }
};

//---------------------------------------------
//#############################################
//  Dime.Dialog  general functions to show the various dialogs
//#############################################
//
Dime.Dialog={
    
    
   
    
    showImageSelectionList: function(callbackHandler){
        
        var myDialog = new Dime.SelectDialog("Image Selection", "image", false);
        
        var handleCallBack = function(response){      
            var myItems = [];
        
            for (var i=0; i<response.length; i++){
            
                //only take images into account
                var mimeType = response[i]["nie:mimeType"];           
                if ((response[i].downloadUrl.toLowerCase().match(/png$/))
                    || (response[i].downloadUrl.toLowerCase().match(/jpg/))
                    || (response[i].downloadUrl.toLowerCase().match(/jpeg/))
                    || (mimeType && (mimeType.indexOf("image")===0))){
                
                    myItems.push(response[i]);
                
                }
            }
        
            myDialog.addItemsToList(myItems);
            
        };
    
        var loadList = function(){       
            
            //load resources
            Dime.REST.getAll(Dime.psMap.TYPE.RESOURCE, handleCallBack);        
                        
            //ADD demo images
            var myItems = [];            
            for (var i=0;i<DEMO_DATA.IMAGES.length;i++){
                
                var myImg = DEMO_DATA.IMAGES[i];                
                var myItem = {
                    guid: myImg,
                    type: Dime.psMap.TYPE.RESOURCE,
                    name: myImg.substr(Math.max(0, myImg.lastIndexOf('/'))),
                    imageUrl: myImg,
                    "nao:privacyLevel": 0.0
                };
                myItems.push(myItem);
            }
            myDialog.addItemsToList(myItems);
        };
        
        myDialog.show(loadList, callbackHandler, this);
    
    },

    showDetailItemModal: function(entry, isEditable, message){
        var caption;
        
        if (entry.type===Dime.psMap.TYPE.PROFILEATTRIBUTE){
            caption = Dime.PACategory.getCategoryByName(entry.category).caption+': "'+entry.name+'"';
        }else{
            caption = Dime.psHelper.getCaptionForItemType(entry.type)+': "'+entry.name+'"';
        }
        if (isEditable){
            caption = "Edit "+ caption;        
        }else{
            caption = caption + ' - (read only)';
        }
        var dialog = new Dime.DetailDialog(caption, entry, false, true, isEditable, message, Dime.psMap.getInfoHtmlForType(entry.type));
        
        var callbackFunction = function(item, isOk){
            
            if (!isOk){ //cancel
                return;
            }
            var updateItemCallBack = function(response){
                console.log("createItem response:", response);
                if (!response|| response.length<1){
                    (new Dime.Dialog.Toast("Updating of "+caption+" failed!")).showLong();
                }else{
                    (new Dime.Dialog.Toast(caption+ " updated successfully.")).showLong();
                }
            };
            
            //post the update
            Dime.REST.updateItem(item, updateItemCallBack, this);
        };
        dialog.showDetailDialog(callbackFunction);
        
    },

    showNewItemModal: function(type, message, newItem){
        if (!newItem){
            newItem = Dime.psHelper.createNewItem(type, "");
        }
        
        var elementName;

        var caption;
        if (type===Dime.psMap.TYPE.PROFILEATTRIBUTE && newItem.category){
            elementName = Dime.PACategory.getCategoryByName(newItem.category).caption;
        }else{
            elementName = Dime.psHelper.getCaptionForItemType(type);
        }
        caption = "New "+ elementName+' ...';

        
        var dialog = new Dime.DetailDialog(caption, newItem, true, true, true, message, Dime.psMap.getInfoHtmlForType(type));
        
        var callbackFunction = function(item, isOk){
            
            if (!isOk){ //cancel
                return;
            }            
            var newItemCallBack = function(response){
                console.log("createItem response:", response);
                if (!response|| response.length<1){
                    (new Dime.Dialog.Toast("Creation of "+elementName+" failed!")).showLong();
                    
                }else{
                    (new Dime.Dialog.Toast(elementName+ " created successfully.")).showLong();
                }
            };
            
            //post the update
            Dime.REST.postNewItem(item, newItemCallBack);
        };
        
        dialog.showDetailDialog(callbackFunction);
    
    },

    
    showLivepostWithSelection: function(selectedPerson){
        $("#lightBoxBlack").fadeIn(300);
        //FIX: hard-coded
        var type = Dime.psMap.TYPE.LIVEPOST;
        var elementName = "livepost";
        var newItem = Dime.psHelper.createNewItem(type, "");
        var caption = "New Livepost";
        var message = "";
        
        var dialog = new Dime.DetailDialog(caption, newItem, true, true, true, message, Dime.psMap.getInfoHtmlForType(type));
        
        //add the current person as a recipient
        dialog.selectedPerson = selectedPerson;
        
        var callbackFunction = function(item, isOk){
            
            if (!isOk){ //cancel
                return;
            }            
            var newItemCallBack = function(response){
                console.log("createItem response:", response);
                if (!response|| response.length<1){
                    (new Dime.Dialog.Toast("Creation of "+elementName+" failed!")).showLong();
                    
                }else{
                    (new Dime.Dialog.Toast(elementName+ " created successfully.")).showLong();
                }
            };
            
            //post the update
            Dime.REST.postNewItem(item, newItemCallBack);
        };
        
        dialog.showDetailDialog(callbackFunction);
        dialog.addToLivepostReceiverList.call(dialog, selectedPerson);
    },
            
    showShareWithSelection: function(selectedItems){
       
        $("#lightBoxBlack").fadeIn(300);
        var dialog = new Dime.ShareDialog();

        //init dialog with selected items
        if (selectedItems && selectedItems.length>0){
            //selected items can either be all of agent type or of shareable type
            //so we just check the first one

            var isAgent = Dime.psHelper.isAgentType(selectedItems[0].type);
            if (isAgent){
                dialog.selectedReceivers=dialog.filterNonSharableAgents(selectedItems);
            }else{
                dialog.selectedItems=selectedItems;
            }
            dialog.updateView();
        }
        
        var callback = function(success, selectedProfile, selectedReceivers, selectedItems){
            console.log("sharing success:", success);
            if (!success){
                return;
            }

            if (!selectedProfile){
                window.alert("No profile selected - please select a profile.");
                return;
            }
            
            var said = selectedProfile.said;
           
            if (!said || said.length===0){
                window.alert("Service-Account-ID for profile: "+selectedProfile.name+" is missing! Sharing aborted.");
                return;
            }
            //update items
            Dime.psHelper.addAgentAccessForItemsAndUpdateServer(selectedReceivers, selectedItems, said, function(sharingSuccessful){
                if(sharingSuccessful){
                    (new Dime.Dialog.Toast("Sharing done!")).showLong();
                }else{
                    (new Dime.Dialog.Toast("Sharing failed!")).showLong();
                }
            });
        };
       
        dialog.show(this, callback);
    },
            
    Helper:{

        getInfoBox: function(infoHtml, infoBoxClass, infoBoxIconClass){
            var infoBoxId=JSTool.randomGUID();

            return $('<div/>').addClass('dimeDialogInfoBoxMeta')
            .append(
                $('<div/>')
                .addClass('infoBoxIcon').addClass(infoBoxIconClass)
                .append($('<img/>').attr('src','img/metaData/info.png'))
                    
                ).append(
                $('<div/>').addClass('dimeDialogInfoBox hidden').addClass(infoBoxClass)
                .attr('id',infoBoxId)
                .append(
                    $('<div/>').append(infoHtml)
                    ))
            .click(function(){
                $('#'+infoBoxId).toggleClass('hidden');
            });            
        },
                
        getAgentElement: function(item){

            var privTrustClass=Dime.privacyTrust.getClassAndCaptionForPrivacyTrustFromItem(item).thinClassString;

            var element = $('<div></div>')
            .addClass("shareDlgItem")
            .append(
                $('<img/>').attr("src", Dime.psHelper.guessLinkURL(item.imageUrl))
                )
            .append(
                $('<div>'+item.name+'</div>').addClass("shareDlgItemName").addClass(privTrustClass)
                );
            return element;
        }
    },
            
    Toast: function(text){
        this.id=JSTool.randomGUID();
        this.text = text;
        this.dialog = $('<div/>').addClass('dimeToast').attr('id',this.id)
        .append(
                $('<div/>').text(text)
            );
    },
            
    Alert: function(text){
        this.id=JSTool.randomGUID();
        this.text = text;
        
        this.dialog = $('<div/>').addClass('alert').attr('id',this.id)
        .append($('<a/>').addClass('close').attr('data-dismiss','alert').text('×'))
        .append($('<span/>').text(text));
                
        //        <div class="alert"><a class="close" data-dismiss="alert">×</a><span>'+message+'</span></div>')
                        
    }

};


Dime.Dialog.Toast.prototype={
    LONG: 3.5*1000,
    SHORT: 2000,

    showShort: function(){
        this.show(this.SHORT);
    },

    showLong: function(){
        this.show(this.LONG);
    },
    
    show:function(delay){
        if (!delay){
            delay=this.SHORT;
        }

        var dialogRef = this;
        var removeToast = function(){
            $('#'+dialogRef.id).remove();
        };

        $('body').append(this.dialog);

        window.setTimeout(removeToast, delay);
    }
};

Dime.Dialog.Alert.prototype={
    show:function(delay){
        $('body').append(this.dialog);
    }
};

/**
 * initially load situations and places
 */
Dime.initProcessor.registerFunction( function(callback){
    
    if (!Dime.ps_configuration.createNavigation){ //if no navigation needed skip this        
        callback();
        return;
    }
    
    Dime.Navigation.updateSituations();
    Dime.Navigation.updateCurrentPlace();
    callback();
});



setInterval(Dime.Tool.keep_alive,1000*60*5);  //repeat every 5 minutes