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

/* 
 *  Description of index.js
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 04.07.2012
 */


//---------------------------------------------
//#############################################
//  DimeView Manager
//#############################################
//


DimeViewStatus = function(viewType, groupType, itemType, personGuid, detailItemGuid, detailItemType, message){ //constructor for view status
    
    var makeNumberIfExists = function(myVar){
        if (!myVar){
            return null;
        }
        if (typeof myVar === 'string' || myVar instanceof String){
            return parseInt(myVar);
        }
        return myVar;
    };
    
    this.viewType = makeNumberIfExists(viewType);
    this.groupType = groupType;
    this.itemType = itemType;
    this.personGuid = personGuid;
    this.detailItemGuid = detailItemGuid;
    this.detailItemType = detailItemType;
    this.message = message;   
    
};


DimeViewStatus.GROUP_CONTAINER_VIEW = 1;
DimeViewStatus.SETTINGS_VIEW = 2;
DimeViewStatus.PERSON_VIEW = 3;
DimeViewStatus.LIVEPOST_VIEW = 4;

DimeViewStatus.VIEW_TYPE_STR = "vT";
DimeViewStatus.GROUP_TYPE_STR = "gT";
DimeViewStatus.ITEM_TYPE_STR = "iT";
DimeViewStatus.PERSON_GUID_STR = "pG";
DimeViewStatus.DETAIL_ITEM_GUID = "dIG";
DimeViewStatus.DETAIL_ITEM_TYPE_STR = "dIT";
DimeViewStatus.MESSAGE_STR = "msg";

DimeViewStatus.readUrlString = function(){


    //set grouptype
    var groupType = Dime.psHelper.getURLparam(DimeViewStatus.GROUP_TYPE_STR);
    var itemType =  Dime.psHelper.getURLparam(DimeViewStatus.ITEM_TYPE_STR);
    var viewType = Dime.psHelper.getURLparam(DimeViewStatus.VIEW_TYPE_STR);
    var detailItemGuid = Dime.psHelper.getURLparam(DimeViewStatus.DETAIL_ITEM_GUID);
    var detailItemType = Dime.psHelper.getURLparam(DimeViewStatus.DETAIL_ITEM_TYPE_STR);
    var personGuid = Dime.psHelper.getURLparam(DimeViewStatus.PERSON_GUID_STR);
    var message = Dime.psHelper.getURLparam(DimeViewStatus.MESSAGE_STR);

    //set default values in case not provided:

    viewType=viewType?viewType:DimeViewStatus.GROUP_CONTAINER_VIEW;    

    if (viewType!==DimeViewStatus.SETTINGS_VIEW){

        groupType=groupType?groupType:Dime.psMap.TYPE.GROUP;
        itemType=itemType?itemType:Dime.psHelper.getChildType(groupType);        
        personGuid=personGuid?personGuid:'@me';                

    }//else no further information required


    return new DimeViewStatus(viewType, groupType, itemType, personGuid, detailItemGuid, detailItemType, message); 
};


DimeViewStatus.prototype = {
    
    
    toUrlString: function(){
        var counter=0;
        var addComponent = function(tag, content){
            if (content && content!==""){
                var componentStr = tag+"="+ encodeURIComponent(content);
                componentStr=counter>0?"&"+componentStr:componentStr;
                counter++;
                return componentStr;
            }
            return "";
        };
        
        var result = "index.html?"
                + addComponent(DimeViewStatus.VIEW_TYPE_STR, this.viewType)
                + addComponent(DimeViewStatus.GROUP_TYPE_STR, this.groupType)
                + addComponent(DimeViewStatus.ITEM_TYPE_STR, this.itemType)
                + addComponent(DimeViewStatus.DETAIL_ITEM_GUID, this.detailItemGuid)
                + addComponent(DimeViewStatus.DETAIL_ITEM_TYPE_STR, this.detailItemType)
                + addComponent(DimeViewStatus.PERSON_GUID_STR, this.personGuid)
                + addComponent(DimeViewStatus.MESSAGE_STR, this.message);
        return result;
    },
    isGroupContainer: function(){
        return (this.viewType===DimeViewStatus.GROUP_CONTAINER_VIEW);
    },
    isPersonView: function(){
        return (this.viewType===DimeViewStatus.PERSON_VIEW);
    },
    isSettingsView: function(){
        return (this.viewType===DimeViewStatus.SETTINGS_VIEW);
    },
    isLivePostView: function(){
        return (this.viewType===DimeViewStatus.LIVEPOST_VIEW);
    }
         
};


    
/**
 * constructor for viewMapEntries
 * @param id id of div in index.html
 * @param groupActive
 * @param settingsActive
 * @param personViewActive
 * @param livepostViewActive
 * @param getType
 * 
 */
DimeViewMapEntry = function(id, groupActive, settingsActive, personViewActive, livepostViewActive, getType){
    this.id = id;
    this.groupActive = groupActive;
    this.settingsActive = settingsActive; 
    this.personViewActive = personViewActive;
    this.livepostViewActive = livepostViewActive;    
    this.getType = getType;        
};


DimeViewManager = function(dimeViewRef){
    var viewManagerRef = this;
    this.dimeViewRef=dimeViewRef;
    
    //VIEW_MAP
    this.viewMap={}; 
     
        
    var addToViewMap = function(viewMapEntry){
        viewManagerRef.viewMap[viewMapEntry.id]=viewMapEntry;
    };
   
    //initialize views                               id, groupActive, settingsActive, personViewActive, livepostViewActive, type
    addToViewMap(new DimeViewMapEntry('groupNavigation', false, false, false, false, this.getCurrentGroupType)); //initially set to false, so it will only be shown with some content in place
    addToViewMap(new DimeViewMapEntry('itemNavigation', false, false, false, true, this.getCurrentItemType)); //initially set to false, so it will only be shown with some content in place
    addToViewMap(new DimeViewMapEntry('searchBox', true, false, false, true, null));
    addToViewMap(new DimeViewMapEntry('metabarMetaContainer', true, false, true, true, null));
    addToViewMap(new DimeViewMapEntry('backToGroupButton', false, false, true, false, null));
    addToViewMap(new DimeViewMapEntry('currentPersonOverview', false, false, true, false, null));
    addToViewMap(new DimeViewMapEntry('currentPersonLabel', false, false, true, false, null));
    addToViewMap(new DimeViewMapEntry('personProfileAttributeNavigation', false, false, true, false, function(){return Dime.psMap.TYPE.PROFILEATTRIBUTE;}));
    addToViewMap(new DimeViewMapEntry('personProfileNavigation', false, false, true, false, function(){return Dime.psMap.TYPE.PROFILE;}));
    addToViewMap(new DimeViewMapEntry('personLivepostNavigation', false, false, true, false, function(){return Dime.psMap.TYPE.LIVEPOST;}));
    addToViewMap(new DimeViewMapEntry('personDataboxNavigation', false, false, true, false, function(){return Dime.psMap.TYPE.DATABOX;}));
    addToViewMap(new DimeViewMapEntry('personResourceNavigation', false, false, true, false, function(){return Dime.psMap.TYPE.RESOURCE;}));
    addToViewMap(new DimeViewMapEntry('settingsNavigationContainer', false, true, false, false, function(){return Dime.psMap.TYPE.SERVICEADAPTER;}));
    //the following are deactivated by default and only shown when required
    addToViewMap(new DimeViewMapEntry('dropzoneNavigation', false, false, false, false, null));
    addToViewMap(new DimeViewMapEntry('globalItemNavigation', false, false, false, false, null));  
    addToViewMap(new DimeViewMapEntry('alertStatusNavigation', false, false, false, false, null));  
    addToViewMap(new DimeViewMapEntry('placeDetailNavigation', false, false, false, false, function(){return Dime.psMap.TYPE.PLACE;}));  
    
    //init view status
    this.status = new DimeViewStatus( DimeViewStatus.GROUP_CONTAINER_VIEW,
        Dime.psMap.TYPE.GROUP, Dime.psMap.TYPE.PERSON, '@me', null,null,"");
    
    this.visibleViews={}; //contains ids of views currently visible
};


DimeViewManager.prototype = {
       
    getCurrentGroupType: function(){
        return this.status.groupType;
    },
    
    getCurrentItemType: function(){
        return this.status.itemType;
    },
    
    getCurrentViewType: function(){
        return this.status.viewType;
    },

    showAlertStatusNavigation: function(innerHtml){
        $("#alertStatusNavigation").empty().append(innerHtml);
        this.setViewVisible('alertStatusNavigation', true);                            
    },      
            
    setViewVisible: function(viewId, setVisible){
        this.visibleViews[viewId]=setVisible;
        if (setVisible){
            $('#'+viewId).removeClass('hidden');
        }else{
            $('#'+viewId).addClass('hidden');
        }
    },
    
   
    pushHistory: function(status){
        var url = status.toUrlString();
        window.history.pushState(status, "", url);
    },
            
            
            
    updateViewInternal: function(newStatus, skipHistory){
        //store status for reference by called functions e.g. DimeView.search //REFACTOR
        this.status = newStatus;
        var dimeViewRef = this.dimeViewRef;
        
        //update evaluation data
        Dime.evaluation.updateViewStack(newStatus.groupType, newStatus.viewType);
        
        this.resetContainers(newStatus); //based on viewtype

        if (!skipHistory){ 
            this.pushHistory(newStatus); //update navigation history
        }

        //refresh general navigation buttons, action buttons and meta-bar
        this.dimeViewRef.updateNavigation(newStatus);
        
        //handle groupview
        if(newStatus.isGroupContainer() || newStatus.isLivePostView()){
            this.dimeViewRef.resetSearch.call(dimeViewRef);
        }
        //handle personview
        if(newStatus.isPersonView()){
            var handleResult = function(response){
                if(response){
                    dimeViewRef.updatePersonViewContainers.call(dimeViewRef, response);
                }
            };
            Dime.REST.getItem(newStatus.personGuid, Dime.psMap.TYPE.PERSON, handleResult, '@me', this);
            
        }
        //show detail dialog if required
        if (newStatus.detailItemGuid&& newStatus.detailItemGuid.length>0 
                && newStatus.personGuid && newStatus.personGuid.length>0 ){
            var showDialog = function (response){
                if (response){
                    dimeViewRef.editItem.call(dimeViewRef, null, null, response, newStatus.message);
                    //reset detail item in status to avoid showing the dialog over again
                    this.status.detailItemGuid=null;
                    this.status.detailItemType=null;
                }
            };
            Dime.REST.getItem(newStatus.detailItemGuid, newStatus.detailItemType, showDialog, newStatus.personGuid, this);
        }
        
    },

    resetContainers: function(status){        
        var managerRef = this;
        
        jQuery.each(this.viewMap, function(){
            //viewMapEntry: id, groupActive, settingsActive, personViewActive getType()
            var displayMe=false;
            if (status.isGroupContainer()){
                displayMe=this.groupActive;
            }else if (status.isSettingsView()){
                displayMe=this.settingsActive;
            }else if (status.isPersonView()){
                displayMe=this.personViewActive;
            }else if (status.isLivePostView()){
                displayMe=this.livepostViewActive;
            }
            managerRef.setViewVisible(this.id, displayMe);
        });
    },
            
    updateViewFromStatus: function(status, skipHistory){
        //to support also plain objects as deliverd from history we create a new status object
        var myStatus = new DimeViewStatus(status.viewType, status.groupType, 
                status.itemType, status.personGuid, status.detailItemGuid, status.detailItemType, status.message);
        this.updateViewInternal(myStatus, skipHistory);
    },
    
    updateView: function(groupType, viewType, skipHistory){
        var status = new DimeViewStatus(viewType, groupType,
            Dime.psHelper.getChildType(groupType), 
            this.status.personGuid, null, 
            null, "");            
        
        this.updateViewInternal(status, skipHistory);
    },
            
    updateViewFromUrl: function(){
        var status = DimeViewStatus.readUrlString();
        this.updateViewInternal(status, false);
    },
    
    getViewsByType: function(type){
         var result = [];
         jQuery.each(this.viewMap, function(){
            if (this.getType && this.getType()===type){
                result.push(this);
            } 
         });
         return result;
    },        
            
    viewForTypeIsShown: function(type){
        var views = this.getViewsByType(type);
        jQuery(views, function(){
            if (this.visibleViews[this.id]){
                return true;
            }
        });
        return false;
    },
    
    updateViewFromNotifications: function(notifications){
        
        var status = this.status;
        
        var refreshSearch = false;
        var refreshSituations = false;
        var refreshPlaces = false;
        var refreshServices = false;
        var refreshAccounts = false;
        var refreshUserSettings=false;

        for (var i=0;i<notifications.length;i++){
            if (notifications[i].element){
                var notificationType=notifications[i].element.type;
                
                if ((notificationType===status.groupType) 
                        || (notificationType===status.itemType)){
                        refreshSearch=true;
                }

                //no else here, since the following might be called also in the case notificationType===status.groupType
                if (notificationType===Dime.psMap.TYPE.SITUATION){
                    refreshSituations=true;
                }else if (notificationType===Dime.psMap.TYPE.PLACE){
                    refreshPlaces=true;                
                }else if (notificationType === Dime.psMap.TYPE.SERVICEADAPTER) {
                    refreshServices = true;
                } else if (notificationType === Dime.psMap.TYPE.ACCOUNT) {
                    refreshAccounts = true;
                }else if (notificationType==='user'){
                    refreshUserSettings=true;
                }

            }
        }
        if (refreshSituations){
            Dime.Navigation.updateSituations();
        }
        if (refreshPlaces){
            Dime.Navigation.updateCurrentPlace();
        }
        if (status.viewType===DimeViewStatus.SETTINGS_VIEW){
            if(refreshUserSettings){
                Dime.Settings.updateSettings();
            }
            if (refreshServices) {
                Dime.Settings.updateServices();
            }
            if (refreshAccounts) {
                Dime.Settings.updateAccounts();
            }
            
        } else {
            
            if (refreshSearch){
                this.updateViewInternal(status, true);
            }
        }
    },
            
    updateViewForPerson: function(event, element, entry){
        var personGuid = entry.guid;
        
        var status = new DimeViewStatus(DimeViewStatus.PERSON_VIEW, 
            Dime.psMap.TYPE.GROUP,
            Dime.psMap.TYPE.PERSON,
            personGuid, null, null, ""); 
            
        this.updateViewInternal(status, false);
    }

};
//---------------------------------------------
//#############################################
//  END DimeView Manager
//#############################################
//

//---------------------------------------------
//#############################################
//  DimeView 
//#############################################
//


DimeView = {
    
    searchFilter: "",
    pushState:{}, //browser history to manage back-button
    
    getShortNameWithLength: function(name, length){
        var myName = name;
        if(myName.length>length){
            myName = myName.substr(0, length) + " ..";
        }
        return myName;
    },
    
    getShortName: function(name){
        var myName = name;
        
        
        if (myName.length>24){
            myName = myName.substr(0, 24);
        }
        
        //insert some spaces after 12 letters
        var result = "";
        var hasSpace = false;
        for (var i=0; i<myName.length;i++){
            if (myName[i]===" "){
                hasSpace = true;
            }
            
            result = result + myName[i];
            
            if (i===12 || i===16){
                if (!hasSpace){
                    result = result + " ";     
                    hasSpace=true;
                }else{
                    hasSpace = false;
                }
            }
            
        }
        return result;
    },
    
    actionMenuActivatedForItem: function(entry){
        var result = (entry.userId==='@me'); //TODO improve?
        
        if (entry.editable!==undefined && entry.editable!==null&&entry.editable===false){
            result=false;
        }

        return result;
    },
    

    createMark: function(entry, className, isGroup){
        var result=$('<div/>').addClass('mark').addClass(className);

        if (!DimeView.actionMenuActivatedForItem(entry)){
            result.addClass('noActionMark');
            return result;
        }//else
        
        
        if(!isGroup){
            var allSelectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
            for(var j=0; j<allSelectedItems.length; j++){
                if(entry.guid === allSelectedItems[j].guid){
                    result.addClass("ItemChecked");
                }
            }
        }
        
        result.clickExt(DimeView, DimeView.selectItem, entry, isGroup);
        return result;
    },
    
    setActionAttributeForElements: function(entry, jElement, isGroupItem, showEditOnClick){
       
        jElement.mouseoverExt(DimeView, DimeView.showMouseOver, entry, isGroupItem);
        jElement.mouseoutExt(DimeView, DimeView.hideMouseOver, entry);
        
        //FIX: add additional handler for situations
        if(isGroupItem){
            jElement.clickExt(DimeView, DimeView.showGroupMembers, entry);
        }else if (entry.type===Dime.psMap.TYPE.PERSON){
            jElement.clickExt(DimeView.viewManager, DimeView.viewManager.updateViewForPerson, entry);
        }else if(showEditOnClick){
            jElement.clickExt(DimeView, DimeView.editItem, entry);
        }
        
    },
   
    addGroupElement: function(jParent, entry){
   
        var groupClass=(entry.type!==Dime.psMap.TYPE.GROUP?entry.type+"Item groupItem":"groupItem");
        
        var jGroupItem=$('<div/>').addClass(groupClass).append($('<div/>')
                .append(
                    Dime.psHelper.getImageUrlJImageFromEntry(entry))
                .append(
                    DimeView.createMark(entry, "", true)
                )
                .append('<div class="groupItemCounter" ><h1>'+ entry.items.length + '</h1></div>')
                .append('<div class="clear"></div>')
        
                //additional hint: "click to edit"
                .append(
                    $('<div/>')
                        .addClass('captionForGroupElement')
                        .attr('title', entry.name)
                        .append(
                            $('<h4>'+ DimeView.getShortNameWithLength(entry.name, 11) + '</h4>')
                        )
                        .append(
                            $('<div class="editHintGroupElement"></div>')  
                        )
                        .hover(function(){
                                $(this).children('.editHintGroupElement').append('(click to edit)');
                            }, function(){
                                $(this).children('.editHintGroupElement').empty();
                        })
                        .clickExt(DimeView, DimeView.editItem, entry)
                )
        );
   

        DimeView.setActionAttributeForElements(entry, jGroupItem, true, false);

        jParent.append(jGroupItem);

    },    
    
    createAttributeItemJElement: function(entry){
        //HACK - check if name is empty and set it with the category caption 
        //should be provided by the server
        if (!entry.name || entry.name.length<1){
            entry.name = Dime.PACategory.getCategoryByName(entry.category).caption;
        }
        
        var jChildItem = $('<div/>').addClass("childItemProfileAttribute")
            .append(DimeView.createMark(entry, "profileAttributeMark", false))
            .append('<div class="profileAttributeCategory">'
                    +Dime.PACategory.getCategoryByName(entry.category).caption+'</div>')
            .append('<div class="profileAttributeName">'+ entry.name.substr(0, 23) + '</div>');

        var profileAttributeValues = $('<div class="profileAttributeValues"/>');

        if (entry.value){
            for (var key in entry.value){
                var value = entry.value[key];
                if (value && value.length>0){
                    profileAttributeValues.append(
                            $('<div class="profileAttributeValue"/>')
                            .append('<span class="profileAttributeValueKey"/>').text(key)
                            .append('<span class="profileAttributeValueValue"/>').text(value)
                            );
                }
            }
        }
        jChildItem.append(profileAttributeValues)
            .append('<div class="clear">');
        
        
        DimeView.setActionAttributeForElements(entry, jChildItem, false, true);
        
        return jChildItem;
    },
            
    createUserNotificationLinkAndContent: function(entry, senderPersonItem, deployFunction){
        var clickFunction;
        var unValues=Dime.un.getCaptionImageUrl(entry);
        
        var updateUserNotification=function(myUN, read, callback){
                myUN.read=read;
                //update entry
                Dime.REST.updateItem(myUN, function(response){                    
                    DimeView.viewManager.updateViewFromStatus(DimeView.viewManager.status, true);
                    if (callback){
                        callback(response);
                    }
                }, this);
        };
        
        //update caption with sender name if available
        unValues.caption = senderPersonItem ? unValues.caption + " by " + senderPersonItem.name : unValues.caption;
        
        if (entry.unType===Dime.psMap.UN_TYPE.REF_TO_ITEM){


            var viewType = DimeViewStatus.GROUP_CONTAINER_VIEW;
            var detailItemType=entry.unEntry.type;
            var groupType;            
            var detailUserId=entry.unEntry.userId;                                
            var message=unValues.caption;

            if (detailUserId!=='@me'){
                groupType=Dime.psMap.TYPE.GROUP;
                viewType = DimeViewStatus.PERSON_VIEW;
            }else if (Dime.psHelper.isChildType(detailItemType)){
                //for @me adjust groupType if detailItemType is set
                groupType=Dime.psHelper.getParentType(detailItemType);            
            }
            var itemType = Dime.psHelper.getChildType(groupType);

            var status = new DimeViewStatus(viewType, groupType, itemType, detailUserId, 
                    entry.unEntry.guid, detailItemType, message);
            clickFunction = function(){                
                DimeView.viewManager.updateViewFromStatus(status, false);
                updateUserNotification(entry, true);
            };
        }else if (entry.unType===Dime.psMap.UN_TYPE.MERGE_RECOMMENDATION) {
            clickFunction = function(){
                var myNotification = entry;
               
                var mergeGuids = [myNotification.unEntry.sourceId, myNotification.unEntry.targetId];
                var dialog = new Dime.MergeDialog(mergeGuids, myNotification.unEntry.similarity, true);
                dialog.show(function(resultStatus){
                    myNotification.unEntry.status = resultStatus;                    
                    if (resultStatus!==dialog.STATUS_PENDING){  //"status":"accepted/dismissed/pending"
                       updateUserNotification(myNotification, true, function(){                         
                            //delete afterwards
                            Dime.REST.removeItem(myNotification);
                         });
                    }else{
                        updateUserNotification(myNotification, true);
                    }
                     
                }, DimeView);
                
            };  
        } else{
            clickFunction = function(){
                //TODO fix
                window.alert("This function is not supported in the research prototype.");
                updateUserNotification(entry, true);
            };
        }
        deployFunction(unValues, clickFunction);

    },

    createUserNotification: function(entry){

        var jChildItem = $("<div/>");

        var markRead=function(){
            entry.read=true;
            Dime.REST.updateItem(entry);
            jChildItem.addClass('userNotificationWasRead');
        };


        //classes
        var itemClass=entry.type+"Item childItem";
        jChildItem.addClass(itemClass);
        if (entry.read){
            jChildItem.addClass('userNotificationWasRead');
        }else{
            jChildItem.click(markRead);
        }
        
        var handleChildItemContent=function(senderPersonItem){  
            var deployFunction = function(unValues, clickFunction){
                jChildItem.click(clickFunction);
                
                //img
                jChildItem.append(Dime.psHelper.getImageUrlJImageFromEntry(entry));

                jChildItem
                    .append(Dime.psHelper.getImageUrlJImage(unValues.imageUrl, Dime.psMap.TYPE.USERNOTIFICATION).addClass('childItemNotifElemType'))
                    .append($('<div/>').addClass('childItemNotifDate').text(JSTool.millisToDateString(entry.created)))
                    .append('<h4 style="font-size: 12px">'+ unValues.caption + '</h4>')                    
                    .append($('<div/>').addClass('childItemNotifOperation').append('<span>'+ unValues.operationName + '</span>'))
                    .append($('<span/>').addClass("childItemNotifElemCaption").text(unValues.childName)
                    );
                
            };
            DimeView.createUserNotificationLinkAndContent(entry, senderPersonItem, deployFunction);
        };
        if (entry.unType===Dime.psMap.UN_TYPE.REF_TO_ITEM && Dime.un.isShareOperation(entry.unEntry.operation)){
            Dime.REST.getItem(entry.unEntry.userId, Dime.psMap.TYPE.PERSON, handleChildItemContent, '@me', this);
        }else{
            handleChildItemContent(null);
        }        
        
        return jChildItem;
    },
            
    createLocationItemJElement: function(entry){
        
        var idSocial = "#" + entry.guid + "Social";
        var idOwn = "#" + entry.guid + "Own";
        var fav = entry.favorite?"favorite":false;
        var jChildItem = $("<div/>");
        var itemClass = entry.type + "Item childItem";
        
        jChildItem.attr("id", entry.guid + "Div");
        jChildItem.addClass(itemClass);
        
        //get current placeGuid stored in #currentPlaceGuid
        var currentPlaceGuid = document.getElementById("currentPlaceGuid").getAttribute("data-guid");
        if(entry.guid === currentPlaceGuid){
            jChildItem.addClass("highlightCurrentPlaceItem");
        }else{
            jChildItem.removeClass("highlightCurrentPlaceItem");
        }
        
        //replace resource.png (no-image) with default
        jChildItem.append(Dime.psHelper.getImageUrlJImageFromEntry(entry));
        jChildItem.append(DimeView.createMark(entry, "", false));
        jChildItem.append('<h4 title="' + entry.name + '"><b>'+ DimeView.getShortNameWithLength(entry.name, 40) +  '</b></h4>');
        if(fav){
            jChildItem.append('<p>' + fav + '</p>');
        }
        
        jChildItem.append(
                $("<div></div>")
                    .addClass("ratingContainerSocial")
                    .append(
                        $("<div></div>")
                            .addClass("ratingStarsSocial")
                            .text("Social Rating: ")
                            .raty({
                                width: 175,
                                half: true,
                                readOnly: true,
                                score: entry.socialRecRating*5
                            })
                    )
        );
        
        jChildItem.append(
                $("<div></div>")
                    .addClass("ratingContainerOwn")
                    .append(
                        $("<div></div>")
                            .addClass("ratingStarsOwn")
                            .text("Your Rating: ")
                            .raty({
                                width: 175,
                                half: true,
                                readOnly: true,
                                score: entry.userRating*5
                            })
                    )
        );
        
        jChildItem.append('<div class="ratingContainerOwn"><div class="rateStarsOwn" id="' + entry.guid + 'Own"></div></div>');
        DimeView.setActionAttributeForElements(entry, jChildItem, false, entry.type);
        return jChildItem;
    },


    createSituationItemJElement: function(entry){

        var jChildItem = $("<div/>");

        //classes
        var itemClass=entry.type+"Item childItem";

        if (entry.active){
            itemClass += " childItemSituationActive";
        }

        var myScore = (entry['nao:score'] ? entry['nao:score'] : 0.001);

        jChildItem.addClass(itemClass)
            .append(
                DimeView.createMark(entry, "", false)
            )
            .append(
                $('<div/>').append(Dime.psHelper.getImageUrlJImageFromEntry(entry))
            )
            .append(
                $('<div/>').addClass('situationTextBlock').append(
                    $('<div/>').text(DimeView.getShortNameWithLength(entry.name, 12))
                )
                .append(
                    $('<div/>').addClass('situationScore').text('Score: '+Math.round((myScore*100))+'%')
                )
            )
            .append(
                $('<div/>').addClass('situationSwitch').append(
                    $('<div/>').text('active')
                ).append(
                    $('<div/>').addClass('situationSwitchSwitch').addClass(entry.active?'situationSwitchActive':'')
                    .click(function(event){
                        if (event){
                            event.stopPropagation();
                        }

                        var handleResponse=function(response){
                            if (response && response.length>0){
                                (new Dime.Dialog.Toast("Situation "
                                    + (response[0].active?"activated":"deactivated")
                                    +" successfully."
                                )).show();
                            }
                        };                        
                        entry.active = !entry.active;
                        Dime.REST.updateItem(entry,handleResponse , DimeView);
                    })
                )
            );

        //set action attributes
        DimeView.setActionAttributeForElements(entry, jChildItem, false, true);
        return jChildItem;
    },
    
    createItemJElement: function(entry){
        //handle profileattributes separately
        if (entry.type===Dime.psMap.TYPE.PROFILEATTRIBUTE){
            return DimeView.createAttributeItemJElement(entry);
        }else if (entry.type===Dime.psMap.TYPE.USERNOTIFICATION) {
            return DimeView.createUserNotification(entry);
        }else if (entry.type===Dime.psMap.TYPE.PLACE){
            return DimeView.createLocationItemJElement(entry);
        }else if (entry.type===Dime.psMap.TYPE.SITUATION){
            return DimeView.createSituationItemJElement(entry);
        }
        
        var showEditOnClick =
                    (entry.type===Dime.psMap.TYPE.PLACE)
                    || (entry.type===Dime.psMap.TYPE.RESOURCE)
                    || (entry.type===Dime.psMap.TYPE.LIVEPOST);
        
                
        var jChildItem = $("<div/>");
        
        //classes
        var itemClass=entry.type+"Item childItem";
        
        jChildItem.addClass(itemClass);
        
        
        //innerChild
        //innerChild - img        
        if (entry.type===Dime.psMap.TYPE.PERSON){
            jChildItem.append($('<div/>').addClass('wrapProfileImage')
                .append(Dime.psHelper.getImageUrlJImageFromEntry(entry))
            )
        }else{
            jChildItem.append(Dime.psHelper.getImageUrlJImageFromEntry(entry));
        }
        
        //innerChild - mark
        jChildItem.append(DimeView.createMark(entry, "", false));
        
        //innerChild - name
        var entryName;
        if (entry.type===Dime.psMap.TYPE.LIVEPOST){
            entryName = DimeView.getShortNameWithLength(entry.name, 125);
        }else{
            entryName = DimeView.getShortNameWithLength(entry.name, 30);
        }
        
        
        jChildItem.append('<h4>'+ entryName + '</h4>');
        
          
        //innerChild - type specific fields
        if (entry.type===Dime.psMap.TYPE.LIVEPOST && entry.text){
            jChildItem.append(
                $('<div>').addClass('childItemLivepostText').text(entry.text.substr(0, 150))
            );
        }                
        //set action attributes
        DimeView.setActionAttributeForElements(entry, jChildItem, false, showEditOnClick);
        return jChildItem;
    },
    
    
    addItemElement: function(jParent, entry){
        jParent.append(DimeView.createItemJElement(entry));

        
    },
    
    CONTAINER_ID_INFORMATION: 1,
    CONTAINER_ID_SHAREDWITH: 2,
    
    
    /**
     * returns a container by id
     * @param {String} containerId valid ids are  CONTAINER_ID_INFORMATION CONTAINER_ID_SHAREDWITH
     */
    getMetaListContainer: function(containerId){
        if (containerId === DimeView.CONTAINER_ID_INFORMATION){
            return $("#metaDataInformationContainer");
        }else if(containerId === DimeView.CONTAINER_ID_SHAREDWITH){
            return $("#metaDataSharedWithContainer");
        }//else
        throw "containerId not supported: "+containerId;        
    },
    
    clearMetaBar: function(){
        DimeView.getMetaListContainer(DimeView.CONTAINER_ID_INFORMATION).empty();
        DimeView.getMetaListContainer(DimeView.CONTAINER_ID_SHAREDWITH).empty();
        
    },
   

    initContainer: function(jContainer, caption, selectingGroupName){  
        
        var isItemNavigation = (jContainer.selector === "#itemNavigation");

        var containerCaption = JSTool.upCaseFirstLetter(caption);
        if (selectingGroupName){
            containerCaption += ' in "' +selectingGroupName+'"';
        }else if(isItemNavigation){
            if(DimeView.searchFilter.length===0){
                containerCaption = "All "+containerCaption;            
            }
        }

        jContainer.empty();
        jContainer.append(
            $('<div/>').attr('id','containerCaption').addClass('h2ModalScreen')
                .text(containerCaption)
                .append($('<div/>').addClass('clear')));

    },
            
    getInnerPosition: function(placeLocation, addCoords){
        var result;
        
        var cutCoord=function(value){
            var myStrVal = value+"";
            return myStrVal.substr(0,8);
        };
        
        if (placeLocation.nextPlace && (placeLocation.nextPlace.distance!==undefined)){
            if (placeLocation.nextPlace.distance<25){
                result = placeLocation.nextPlace.location.name;
            }else{
                result = placeLocation.nextPlace.distance +'km outside of '+placeLocation.nextPlace.location.name;
            }
        }
        if (addCoords){
            result+=result?'<br/>':'';
            result += cutCoord(placeLocation.currPos.latitude)+', '+ cutCoord(placeLocation.currPos.longitude);
        }
        return result;  
    },
            
    updatePlaceSummary: function(placeView, placeLocation){
        var placeSummary = $('<div/>').attr('id','placeSummaryView')
            .append($('<div/>').text('Place Summary'));
        placeView.append(placeSummary);
        
        /*
         * z is the zoom level (1-20)
           t is the map type ("m" map, "k" satellite, "h" hybrid, "p" terrain, "e" GoogleEarth)
           q is the search query, if it is prefixed by loc: then google assumes it is a lat lon separated by a +
         */
        var getGoogleLatLon=function(lat, lon, z, t){
            z=z?z:12;
            t=t?t:"m";
            return 'http://maps.google.com/maps?z='+z+'&t='+t+'&q=loc:'+lat+'+'+lon;
        };
        
        var getWikiLatLon=function(lat, lon, callback){
            //http://api.wikilocation.org/articles?lat=51.500688&lng=-0.124411&limit=1
            var handleResult= function(response){
                if (response && response.articles.length>0 && response.response.articles[0].title){
                    callback(response.response.articles[0]);
                }
                callback(null);
            };
            
            var callPath = 'http://api.wikilocation.org/articles?lat='+lat+'&lng='+lon+'&limit=1';
            
            $.getJSON(callPath, "", handleResult);
            
        };
        
        //show current position
        placeSummary.append($('<div/>')
            .append($('<span/>').text("Your current position: ").css('float','left'))
            .append(
                $('<a/>').attr('href', getGoogleLatLon(placeLocation.currPos.latitude, placeLocation.currPos.longitude))
                .attr('target','_blank').prop('title', 'show in google maps')
                .css('float','right').append(DimeView.getInnerPosition(placeLocation, true)))
            );
        if (placeLocation.currPlace && placeLocation.currPlace.placeId && placeLocation.currPlace.placeName){
            placeSummary.append($('<div/>')
            .append($('<span/>').text("Checked in place: "))
            .append($('<span/>').addClass('pseudoLink')
                .text(DimeView.getShortNameWithLength(placeLocation.currPlace.placeName,26))
                .click(function(){
                    DimeView.viewManager.updateViewFromStatus(new DimeViewStatus(
                            DimeViewStatus.GROUP_CONTAINER_VIEW, Dime.psMap.TYPE.PLACE, null, '@me', 
                            placeLocation.currPlace.placeId,  Dime.psMap.TYPE.PLACE, ""
                        ), true);
                }))
            );
        }else{
             placeSummary.append($('<div/>')
            .append($('<span/>').text("Your current place: "))
            .append($('<span/>').text('not set'))
            );
        }
    },
            
    handlePlaceResult: function(entries){
        var placeView = $('#placeDetailNavigation').empty();
        DimeView.viewManager.setViewVisible('placeDetailNavigation',true);
        
        var refreshUI=function(){            
            DimeView.viewManager.updateView(Dime.psMap.TYPE.PLACE, DimeViewStatus.GROUP_CONTAINER_VIEW, true);
            Dime.Navigation.updateCurrentPlace();
        };        
        
        var updatePlaceView=function(placeLocation){
            
            if(!placeLocation.connected){
                //not even connected 
                placeView.append($('<div/>')
                        .append('To enable places nearby, you should activate the') 
                        .append($('<span/>').text('YellowmapPlaceService').addClass('pseudoLink').click(function(){
                            DimeView.viewManager.updateView(Dime.psMap.TYPE.PLACE, DimeViewStatus.SETTINGS_VIEW, false);
                        }))
                        .append(' first!')
                );
                return;
            
            }else if ((!placeLocation.currPos)||!(placeLocation.currPos.latitude && placeLocation.currPos.longitude)){
                //we don't have a position                   
                placeView.append($('<div/>').append($('$<div/>').text('di.me was not able to detect your position.')));
                
            }else{
                DimeView.updatePlaceSummary(placeView, placeLocation);
                        
            }
            placeView.append($('<div/>').attr('id','placeDetailOptionView')
                    .append($('$<div/>').addClass('placeDetailOptions').text('You can choose your position manually:'))
                    .append(new Dime.Dialog.KnownPlacesDropdown(refreshUI, DimeView).addClass('placeDetailOptions'))
                    .append($('$<div/>').addClass('placeDetailOptions').text('... or use geolocation provided by your browser:'))
                    .append($('$<div/>').addClass('placeDetailOptions').addClass('btn')
                        .text("Get Position")                            
                        .click(function(){
                            if (navigator.geolocation) {
                                //getCurrentPosition() fires once - watchPosition() fires continuosly
                                //maybe: https://github.com/estebanav/javascript-mobile-desktop-geolocation
                                //maybe: http://dev.w3.org/geo/api/spec-source.html#get-current-position (enableHighAccuracy)
                                navigator.geolocation.getCurrentPosition(function(position) {                       
                                    var lat = position.coords.latitude;
                                    var lon = position.coords.longitude;
                                    var acc = position.coords.accuracy;
                                    Dime.psHelper.postCurrentContext(lat, lon, acc, function(){
                                        DimeView.viewManager.updateView.call(DimeView.viewManager, Dime.psMap.TYPE.PLACE, DimeViewStatus.GROUP_CONTAINER_VIEW, true);
                                        Dime.Navigation.updateCurrentPlace();
                                    });                                    

                                }); 
                            }else{
                                (new Dime.Dialog.Toast("Geolocation services are not supported by your browser.")).show();
                            };                                
                        })));
        };
        
        Dime.psHelper.getPositionAndPlaceInformation(updatePlaceView, DimeView);

    },
    
    handleSearchResultForContainer: function(type, entries, jContainerElement, isGroupContainer){
        if (type===Dime.psMap.TYPE.PLACE){
                DimeView.handlePlaceResult(entries);
        }

        if (!entries || entries.length===0){
            DimeView.viewManager.setViewVisible.call(DimeView.viewManager, jContainerElement.attr('id'), false);            
            return;
        }//else
        
        

        var isInFilter = function(entry){
            return JSTool.isSubString(DimeView.searchFilter, entry.name);
        };

        //special search when searching on usernotifications
        if (type===Dime.psMap.TYPE.USERNOTIFICATION){
            isInFilter = function(entry){

                var unValues=Dime.un.getCaptionImageUrl(entry);

                var result =
                    JSTool.isSubString(DimeView.searchFilter, entry.name)
                    || JSTool.isSubString(DimeView.searchFilter, unValues.caption)
                    || JSTool.isSubString(DimeView.searchFilter, unValues.operation)
                    || JSTool.isSubString(DimeView.searchFilter, unValues.childName)
                    || JSTool.isSubString(DimeView.searchFilter, unValues.shortCaption)
                ;

                return result;
            };
        }

        //for @me profiles we skip profiles with no said
        if (type===Dime.psMap.TYPE.PROFILE){
            isInFilter = function(entry){
                if ((entry.userId==='@me')
                    
                    &&(!entry.said || entry.said.length<1)){
                    return false; //skip this
                }
                return JSTool.isSubString(DimeView.searchFilter, entry.name);
                //TODO - also search in attributes
            };

        }
        
        var containerCaption=Dime.psHelper.getPluralCaptionForItemType(entries[0].type);
        if (type===Dime.psMap.TYPE.PLACE){
            containerCaption+=" (nearby to your position)"
        }

        DimeView.initContainer(jContainerElement, containerCaption);

        DimeView.viewManager.setViewVisible.call(DimeView.viewManager, jContainerElement.attr('id'), true);                    

        for (var i=0; i<entries.length; i++){ 
            if (isInFilter(entries[i])){
                if (isGroupContainer){
                    DimeView.addGroupElement(jContainerElement, entries[i]);

                }else{                    
                    DimeView.addItemElement(jContainerElement, entries[i]);
                }
            }
        }        
    }, 
    
    handleSearchResult: function(entries, type){          
        
        //select container element
        var isGroupContainer=Dime.psHelper.isParentType(type);
        
        var jContainerElement;
        if (isGroupContainer){
            jContainerElement=$('#groupNavigation');
        }else{
            jContainerElement=$('#itemNavigation');
        }
        DimeView.handleSearchResultForContainer(type, entries, jContainerElement, isGroupContainer);
    }, 
    
    selectedItems: {},
    
    clearSelectedItems: function(){
        var mySelectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
        for (var i=0;i<mySelectedItems.length;i++){
            var item = mySelectedItems[i];
            if (item && item.element){
                item.element.removeClass("ItemChecked");
            }
        }
        DimeView.selectedItems = {};
        $("#globalActionButton").empty();
        DimeView.updateActionView(0);
    },
            
    getSelectedItemsForView: function(){
        
        //in person view, the person is also a selected item
        if (DimeView.viewManager.status.isPersonView()){
            var myGuid = DimeView.viewManager.status.personGuid;
            DimeView.selectedItems[myGuid] = { 
                guid:myGuid,
                userId:'@me',
                type:Dime.psMap.TYPE.PERSON,
                isGroupItem:false,
                element:null
            };
        }
        return JSTool.getDefinedMembers(DimeView.selectedItems);        
    },
    
    ACTION_BUTTON_ID:[
        {
            id: "actionButtonNew",
            minItems: -1,
            maxItems: -1,
            supportedGroupTypes: [Dime.psMap.TYPE.GROUP, 
                Dime.psMap.TYPE.DATABOX, 
                Dime.psMap.TYPE.LIVESTREAM, 
                Dime.psMap.TYPE.PROFILE, 
                Dime.psMap.TYPE.SITUATION],
            supportedViewTypes: [DimeViewStatus.GROUP_CONTAINER_VIEW, DimeViewStatus.LIVEPOST_VIEW],
            handlerFunction: null
            
        },
        {
            id: "actionButtonEdit",
            minItems: 1,
            maxItems: 1,
            supportedGroupTypes: [Dime.psMap.TYPE.GROUP, 
                Dime.psMap.TYPE.DATABOX, 
                Dime.psMap.TYPE.PROFILE, 
                Dime.psMap.TYPE.SITUATION],
            supportedViewTypes: [DimeViewStatus.GROUP_CONTAINER_VIEW, DimeViewStatus.PERSON_VIEW, DimeViewStatus.LIVEPOST_VIEW],
            handlerFunction: function(){DimeView.editSelected.call(DimeView);}
        },
        {
            id: "specialActionButton",
            minItems: -1,
            maxItems: 0,
            supportedGroupTypes: [Dime.psMap.TYPE.PLACE],
            supportedViewTypes: [DimeViewStatus.GROUP_CONTAINER_VIEW, DimeViewStatus.PERSON_VIEW],
            handlerFunction: function(){DimeView.specialButtonSelected.call(DimeView);}
        },
        {
            id: "actionButtonDelete",
            minItems: 1,
            maxItems: -1,
            supportedGroupTypes: [Dime.psMap.TYPE.GROUP, 
                Dime.psMap.TYPE.DATABOX, 
                Dime.psMap.TYPE.LIVESTREAM, 
                Dime.psMap.TYPE.PROFILE, 
                Dime.psMap.TYPE.SITUATION],
            supportedViewTypes: [DimeViewStatus.GROUP_CONTAINER_VIEW, DimeViewStatus.PERSON_VIEW, DimeViewStatus.LIVEPOST_VIEW],
            handlerFunction: function(){DimeView.removeSelected.call(DimeView);}
        },
        {
            id: "actionButtonShare",
            minItems: -1,
            maxItems: -1,
            supportedGroupTypes: [Dime.psMap.TYPE.GROUP, 
                Dime.psMap.TYPE.LIVESTREAM, 
                Dime.psMap.TYPE.DATABOX],
            supportedViewTypes: [DimeViewStatus.GROUP_CONTAINER_VIEW, DimeViewStatus.PERSON_VIEW, DimeViewStatus.LIVEPOST_VIEW],
            handlerFunction: function(){DimeView.shareSelected.call(DimeView);}
        },
        {
            id: "actionButtonMore",
            minItems: -1,
            maxItems: -1,
            supportedGroupTypes: [Dime.psMap.TYPE.GROUP, 
                Dime.psMap.TYPE.LIVESTREAM, 
                Dime.psMap.TYPE.DATABOX, 
                Dime.psMap.TYPE.PROFILE, 
                Dime.psMap.TYPE.SITUATION],
            supportedViewTypes: [DimeViewStatus.GROUP_CONTAINER_VIEW, DimeViewStatus.LIVEPOST_VIEW],
            handlerFunction: null
        }
    ],
    
    updateActionView: function(selectionCount){
        var checkUpdateActionButton = function(actionButton){
            //HACK update values for special button - for supporting multi-use buttons refactoring would be necessary
            if (actionButton.id!=='specialActionButton'){
                return;
            }
            if (DimeView.viewManager.status.groupType===Dime.psMap.TYPE.PLACE){
                $('#'+actionButton.id).text("Check In");
                actionButton.maxItems=1;
                actionButton.minItems=1;
            }else if (DimeView.viewManager.status.viewType===DimeViewStatus.PERSON_VIEW){
                $('#'+actionButton.id).text("Send Livepost ...");
                actionButton.maxItems=0;
                actionButton.minItems=-1;
            }
        };
        var disabledButtonClass = "disabled";
        
        var hideButton=function(buttonId){
            $("#"+buttonId)
                    .addClass(disabledButtonClass)
                    .off('click.defaultActionButtonHandler')            
            ;
        };
    
        var showButton=function(myActionButton){
            var button = $("#"+myActionButton.id);
            button.removeClass(disabledButtonClass);
            if (myActionButton.handlerFunction){
                button
                    .off('click.defaultActionButtonHandler')            
                    .on('click.defaultActionButtonHandler',
                    myActionButton.handlerFunction);
            }
            
        };
        var curViewType = DimeView.viewManager.status.viewType;
        var curGroupType = DimeView.viewManager.status.groupType;
        
        var isSupportedForStatus = function(myActionButton){
            
            if (!JSTool.arrayContainsItem(myActionButton.supportedViewTypes, curViewType)){
                return false;
            }
            if (curViewType===DimeViewStatus.PERSON_VIEW){
                return true; //if PERSON VIEW and supported don't care about groupType
            }
            if (JSTool.arrayContainsItem(myActionButton.supportedGroupTypes, curGroupType)){
                return true;
            }
            return false;
        };
        
        for (var i=0;i<DimeView.ACTION_BUTTON_ID.length;i++){
            
            var actionButton=DimeView.ACTION_BUTTON_ID[i];

            checkUpdateActionButton(actionButton);
            
            if (!isSupportedForStatus(actionButton)){
                hideButton(actionButton.id);
                continue;
            }
            
            if (curViewType===DimeViewStatus.PERSON_VIEW){
                showButton(actionButton);
                continue;//if PERSON VIEW and supported don't care about groupType
            }
            
            if (actionButton.minItems===-1){ //always show this
                showButton(actionButton);
                continue;
            }
            if (selectionCount<actionButton.minItems){ 
                hideButton(actionButton.id);
                                   
            } else if ((actionButton.maxItems===-1) || (selectionCount<=actionButton.maxItems)){ 
                showButton(actionButton);
                
            }else{ //too many selected
                hideButton(actionButton.id);
            }
        }
    },

    specialButtonSelected: function(){
        if (DimeView.viewManager.status.groupType===Dime.psMap.TYPE.PLACE){
            var selectedPlaces = DimeView.getSelectedItemsForView();
            var restoreCurrentPlace=false;
            var handleResponse= function(response){
                DimeView.viewManager.updateView(Dime.psMap.TYPE.PLACE, DimeViewStatus.GROUP_CONTAINER_VIEW, true);
                Dime.Navigation.updateCurrentPlace();
            };
            var fullPlace = [];

            var handlePlaceInformation=function(placeLocation){
                if (!placeLocation.connected){
                    (new Dime.Dialog.Toast("Please, first get connected to the YellowMap service!")).show();
                }
                if (placeLocation.currPlace && placeLocation.currPlace.placeId && fullPlace[0].guid===placeLocation.currPlace.placeId){
                    restoreCurrentPlace=true;
                }
                Dime.psHelper.postUpdateCurrentPlace(fullPlace[0], restoreCurrentPlace, handleResponse, this);
            };


            var handleFullItems=function(fullItems){
                fullPlace=fullItems;
                Dime.psHelper.getPositionAndPlaceInformation(handlePlaceInformation, this);
            };

            if (!selectedPlaces || selectedPlaces.length!==1){
                (new Dime.Dialog.Toast("Please select a single place!")).show();
            }else{
                Dime.psHelper.getMixedItems(selectedPlaces, handleFullItems, this);
            }
        }else if (DimeView.viewManager.status.viewType===DimeViewStatus.PERSON_VIEW){
            
            var selectedPerson = DimeView.getSelectedItemsForView();

            var triggerDialog=function(response){
                Dime.Dialog.showLivepostWithSelection(response);
            };
            Dime.psHelper.getMixedItems(selectedPerson, triggerDialog, this);
        }
    },
    
    selectItem: function(event, element, entry, isGroupItem){
        
        if (event){
            event.stopPropagation();
        }
        var guid=entry.guid;
        
        var doSelectItem = function(myGuid){
            DimeView.selectedItems[myGuid] = { 
                guid:myGuid,
                userId:entry.userId,
                type:entry.type,
                isGroupItem:isGroupItem,
                element:element
            };
            $(element).addClass("ItemChecked");
        };
                
        var selectedItemsAll = JSTool.getDefinedMembers(DimeView.selectedItems);
        var lastMemberCount = selectedItemsAll.length;
        if(DimeView.selectedItems[guid]){ //click on a already selected item
            DimeView.selectedItems[guid] = null;
            $(element).removeClass("ItemChecked"); //unselect
        
        //first item to be selected or type not changed
        }else if(lastMemberCount === 0 || entry.type === selectedItemsAll[lastMemberCount-1].type){
            doSelectItem(guid);
        //if an item was selected already, but type (container) has been changed
        }else{
            //remove previous selection first
            DimeView.clearSelectedItems();
            doSelectItem(guid);
        }
        
        var memberCount = JSTool.countDefinedMembers(DimeView.selectedItems);     
        $("#globalActionButton").text(memberCount);
        
        this.updateActionView(memberCount);
        
        if(isGroupItem){
            //when selected, set focus on groupElement
            this.showGroupMembers(event, element, entry);
            element.parent().parent().addClass("groupChecked");
        }

    },
    
    showSelectedItems: function(){
        //FIXME show this nicely
        var message = "Selected items:\n";
        
        var mySelectedItems = DimeView.getSelectedItemsForView();
        for (var i=0;i<mySelectedItems.length;i++){
            var item = mySelectedItems[i];
            message += item.type + ": "+item.guid+"("+item.userId+")\n";
        }
        
        window.alert(message);
    },
    
    createMetaBarListItem: function(name, value, type, imageUrl, className){

        if (value===undefined || value===null){
            //show undefined and null values in the browsers
            //while empty string is allowed
            value="undefined";
        }else if (value.length>22){
            value=value.substr(0, 20)+" ..";
        }

        var listIconElement=$('<div/>').addClass('listElementIcon');
        if (imageUrl){
            listIconElement.append(Dime.psHelper.getImageUrlJImage(imageUrl, type));
        }
        var listItem=$('<li/>').addClass('listElement');
        if (className){
            listItem.addClass(className);
        }

        listItem.append(
                $('<div/>').addClass('listElementText')
                    .append($('<span class="listElementTextName"/>').text(DimeView.getShortNameWithLength(name, 28)))
                    .append($('<span class="listElementTextValue"/>').text(value))
                )
                .append(listIconElement);
        
        return listItem;
    },

    createMetaBarListItemForItem: function(item){
        var result = DimeView.createMetaBarListItem(
            Dime.psHelper.getCaptionForItemType(item.type),
            item.name,
            item.type,
            item.imageUrl
        );
        //TODO add link to item

        return result;
    },
    
    
  
    addInformationToMetabar: function(entry){
        $("#metaDataInformationHeader").text("Information");

        var informationViewContainer = DimeView.getMetaListContainer(DimeView.CONTAINER_ID_INFORMATION);  
      
        informationViewContainer.append(DimeView.createMetaBarListItem(
            DimeView.getShortNameWithLength(entry.name, 35), "", entry.type, entry.imageUrl));
        informationViewContainer.append(DimeView.createMetaBarListItem(
            "changed:", JSTool.millisToFormatString(entry.lastModified), entry.type, null));
             
        
        if (entry.userId!=='@me'){
            var setProviderName=function(response){
                if (response){
                informationViewContainer.append(DimeView.createMetaBarListItem(
                    "shared by:", response.name, response.type, response.imageUrl));
                }
            };
            Dime.REST.getItem(entry.userId, Dime.psMap.TYPE.PERSON, setProviderName, '@me', this);
            
        }
        if (entry.hasOwnProperty("nao:trustLevel")){
            
            var tCaptionAndClass=Dime.privacyTrust.getClassAndCaptionForPrivacyTrust(entry["nao:trustLevel"], false);
            if (!tCaptionAndClass){
                return;
            }                
           
            var tElement = DimeView.createMetaBarListItem("trust:", tCaptionAndClass.caption, null, null);
            tElement.addClass(tCaptionAndClass.classString);
            informationViewContainer.append(tElement);
        }
        if (entry.hasOwnProperty("nao:privacyLevel")){
            var pCaptionAndClass=Dime.privacyTrust.getClassAndCaptionForPrivacyTrust(entry["nao:privacyLevel"], true);
            if (!pCaptionAndClass){
                return;
            }                
           
            var pElement = DimeView.createMetaBarListItem("privacy:", pCaptionAndClass.caption, null, null);
            pElement.addClass(pCaptionAndClass.classString);
            informationViewContainer.append(pElement);
        }
    },
        
    updateMetabarForSelection: function(){
        DimeView.previousHoverGUID = 0;
        
        DimeView.clearMetaBar(); 
        
        //show selected items summary
        //FIXME - more efficient solution should generate this on select and then only load the items
        
       
        var informationViewContainer = DimeView.getMetaListContainer(DimeView.CONTAINER_ID_INFORMATION);        
        
        var mySelectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
        for (var i=0;i<mySelectedItems.length;i++){
            var updateInformationViewContainer = function(response){
                informationViewContainer.append(DimeView.createMetaBarListItemForItem(response));
            };
            Dime.REST.getItem(mySelectedItems[i].guid, mySelectedItems[i].type, updateInformationViewContainer,
                mySelectedItems[i].userId, this);
        }
    
        $("#metaDataInformationHeader").text("Selected");        
        $("#metaDataShareAreaId").addClass("hidden");
      
    },
    
    previousHoverGUID:null,

    hideMouseOver: function(event, element, entry){
        
        if ((!DimeView.previousHoverGUID) || DimeView.previousHoverGUID===0){ //avoid multiple calls
            return;
        }
        
        if (!event) {event = window.event;}
	var tg = (window.event) ? event.srcElement : event.target;
	if (tg.nodeName !== 'DIV') {
            return;
        }
	var reltg = (event.relatedTarget) ? event.relatedTarget : event.toElement;
	while (reltg !== tg && reltg && reltg.nodeName !== 'BODY'){
		reltg= reltg.parentNode;
        }
	if (reltg===tg) {
            return;
        }
        
	// Mouseout took place when mouse actually left layer
	// Handle event
        //commented out in order to stay visible after mouseout (web ui issue)
        //won't show list of selected items on the metabar
        //DimeView.updateMetabarForSelection();       
    },
       

    showMouseOver: function(event, element, entry, isGroupEntry){        
        
        //FIXME - this check is not required if this is already checked at a prior instance
        if (!entry  || !entry.guid || !entry.userId || !entry.type || entry.type.length===0 ){
            console.log("ERROR: received invalid entry: (entry)", entry);
            return;
        }
        
        //ATTENTION - this solution is not realy call-response-order-safe - see also comment below...
        if (entry.guid===DimeView.previousHoverGUID){ //avoid reloading the same user again
            return;
        }
        var guid = entry.guid;
        
        DimeView.previousHoverGUID = guid;
        
        DimeView.clearMetaBar(); //TODO -  ATTENTION !!!
        //this solution cannot guarantee that after calling clearMetaBar 
        //- still old callbacks are incoming and added to the meta bar of a 
        //different person that has been hovered on in the meantime...
        //better would be some synchronous call or -better- handling, so
        //it's assured all data belongs to the same person
        //this could be done by using the same approach as for initalProcessor
        
        JSTool.removeClassIfSet($("#metaDataShareAreaId"),"hidden");
        DimeView.addInformationToMetabar(entry);      
        
        var addAgentsToMetaBar=function(listItemContainer, agentItems){
            for (var j=0;j<agentItems.length;j++){
                listItemContainer.append(DimeView.createMetaBarListItem( "", agentItems[j].name, agentItems[j].type, agentItems[j].imageUrl, "metaDataShareItem"));
            }
        };
        
        var handleSharedToAgentResult=function(acl){
        
            //rewrite the GUID in case the callback is handled at a later time
            //so it will be overwritten again on the next mouse-over call
            DimeView.previousHoverGUID = guid; //TODO check potential consequences for rewriting
            
            
            if (acl.length===0){
                return;
            }

            var listItemContainer = DimeView.getMetaListContainer(DimeView.CONTAINER_ID_SHAREDWITH);
        
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
                    var profileContainer = DimeView.createMetaBarListItem("shared as:", pName, Dime.psMap.TYPE.PROFILE, pImage, "metaDataShareProfile")
                        .append(profileContainerList);
                    listItemContainer.append(profileContainer);
                    addAgentsToMetaBar(profileContainerList, aclPackage.groupItems);
                    addAgentsToMetaBar(profileContainerList, aclPackage.personItems);
                    addAgentsToMetaBar(profileContainerList, aclPackage.serviceItems);
                };

                Dime.psHelper.getProfileForSaid(aclPackage.saidSender, writeACLPackage);

                
            }
        };
        
        var handleSharedItemsResult=function(resultItems){
            //rewrite the GUID in case the callback is handled at a later time
            //so it will be overwritten again on the next mouse-over call
            DimeView.previousHoverGUID = guid; //TODO check potential consequences for rewriting
            
            
            if (resultItems.length===0){
                return;
            }
            var listItemContainer = DimeView.getMetaListContainer(DimeView.CONTAINER_ID_SHAREDWITH);
        
            for (var i=0;i<resultItems.length;i++){                        
                var entry = resultItems[i];
                if (!entry.name){
                    console.log("ERROR: oops - received entry without name - skipping!", entry);
                    continue;
                }
            
                listItemContainer.append(DimeView.createMetaBarListItem(entry.name, "", entry.type, entry.imageUrl));
            }
        };
        
        var isAgent = Dime.psHelper.isAgentType(entry.type);
        if (isAgent){
            Dime.psHelper.getAllSharedItems(entry, handleSharedItemsResult);
        }else if (Dime.psHelper.isShareableType(entry.type) ){
            Dime.psHelper.getAllReceiversOfItem(entry, handleSharedToAgentResult);
        }
    },
        
    updateItemContainerFromArray: function(entries, selectingGroupName){

        DimeView.initContainer($('#itemNavigation'), 
            Dime.psHelper.getPluralCaptionForItemType(DimeView.itemType),
            selectingGroupName);

        var itemContainer = $('#itemNavigation');
        for (var i=0; i<entries.length; i++){ 
            if (entries[i].name.toLowerCase().indexOf(DimeView.searchFilter.toLowerCase())!==-1){              
                DimeView.addItemElement(itemContainer, entries[i]);
            }
        }  
    },
    
    showGroupMembers: function(event, element, groupEntry){
        event.stopPropagation();
        
        if ((!groupEntry) || (!groupEntry.items)){
         return;
        }
        
        $('.groupItem').removeClass('groupChecked');
        element.addClass('groupChecked');

        var updateGroupMembers = function(response){
            this.updateItemContainerFromArray(response, groupEntry.name);
        };

        Dime.REST.getItems(groupEntry.items,Dime.psHelper.getChildType(groupEntry.type), updateGroupMembers, groupEntry.userId, this);
                           
    }, 
    
    editItem: function(event, element, entry, message){
        if (event){
            event.stopPropagation();
        }

        Dime.evaluation.createAndSendEvaluationItemForAction("action_editItem");
        var isEditable=DimeView.actionMenuActivatedForItem(entry);
        
        if (entry.type===Dime.psMap.TYPE.LIVEPOST){
            isEditable=false;
        }

        Dime.Dialog.showDetailItemModal(entry, isEditable, message);
    },
            
    
    
    editSelected: function(){

        Dime.evaluation.createAndSendEvaluationItemForAction("action_editItem");

        var selectedItems = DimeView.getSelectedItemsForView();
        if (selectedItems.length!==1){
            window.alert("Please select a single item.");
            return;
        }
        var triggerDialog=function(response){
            var isEditable=DimeView.actionMenuActivatedForItem(response);
            Dime.Dialog.showDetailItemModal(response, isEditable);
        };

        Dime.REST.getItem(selectedItems[0].guid, selectedItems[0].type, triggerDialog, selectedItems[0].userId, this);
    },
    
    removeSelected: function(){
        var mySelectedItems = DimeView.getSelectedItemsForView();
        
        if (mySelectedItems.length<1){
            window.alert("Please select at least one item to be deleted!");
            return;
        }

        if (confirm("Are you sure, you want to delete "+mySelectedItems.length+" items?")){
            
            Dime.evaluation.createAndSendEvaluationItemForAction("action_removePerson");

            for (var i=0;i<mySelectedItems.length;i++){
                var item = mySelectedItems[i];
                Dime.REST.removeItem(item);
            }
        }
    },    
           
    
    shareSelected: function(){
        Dime.evaluation.createAndSendEvaluationItemForAction("action_share");

        var selectedItems = DimeView.getSelectedItemsForView();
        
        var triggerDialog=function(response){

            Dime.Dialog.showShareWithSelection(response);
        };

        Dime.psHelper.getMixedItems(selectedItems, triggerDialog, this);
    },
        
    

    /*
     * @param: entry {"surname":"Thiel",
     *  "name":"Simon",
     *  "nickname":"SIT",
     *  "said":"account:urn:4e46c7ec-8e19-45eb-953c-91f68583fd39"},
     */
    createGlobalSearchResultElement: function(entry){
        
        var createNameEntryString = function(baseRef, key, idPostFix){
            if (!baseRef[key] || baseRef[key].lenght===0){
                return "";
            }            
            return $('<span class="globalSearchResult'+idPostFix+'">'
                    +baseRef[key]+'</span>');
        };
                    
        var myJElement = $('<div/>').addClass("publicContactEntry")
            .append($('<div/>').addClass('addPublicPersonBtn btn')
                    .clickExt(DimeView, DimeView.addPublicPerson, entry.said)
                    .text('add'))
            .append('<div class="globalSearchResultBackgroundText">Public Profile</div>')
            .append(
                $('<div class="globalSearchResultName"/>')
                    .append(createNameEntryString(entry, "name","FirstName"))
                    .append(createNameEntryString(entry, "surname","SurName"))
                    .append(createNameEntryString(entry, "nickname","NickName"))
                    );

        return myJElement;
    
    },
        
    addPublicPerson: function(event, jqueryItem, said){
        if (!said || said.length===0){
            console.log("ERROR: said is not said!");
            return;
        }
    
        for (var i=0; i<DimeView.globalSearchResults.length; i++){
            var myResult = DimeView.globalSearchResults[i];
            if (myResult && myResult.said===said){
                
                Dime.REST.addPublicContact(myResult);    
                DimeView.resetSearch();
                return;
            }
        }
        console.log("ERROR: said not found in DimeView.globalSearchResults!");
    },    
    
    globalSearchResults:[],
    
    handleGlobalSearchResult: function(response){
        
        var jGlobalSearchResultContainer = $('#globalItemNavigation');
        
        if (!response || response.length===0){
            DimeView.viewManager.setViewVisible.call(DimeView.viewManager, 'globalItemNavigation', false);            
            return;
        }else{
            DimeView.viewManager.setViewVisible.call(DimeView.viewManager, 'globalItemNavigation', true);
        }
        
        DimeView.globalSearchResults = response;
      
        
        
        for (var i=0; i<response.length;i++){
            var jElement = DimeView.createGlobalSearchResultElement(response[i]);
            jGlobalSearchResultContainer.append(jElement);
        }
    },
    
    searchCallForType: function(type){
      
        var callBack=function(response){
            DimeView.handleSearchResult(response, type);
        };       

        Dime.REST.getAll(type, callBack);
    },
    
    cleanUpView: function(){
        //clear container
        $('#groupNavigation').empty();
        $('#itemNavigation').empty();

        DimeView.clearSelectedItems();
        DimeView.clearMetaBar(); 

        DimeView.globalSearchResults =[];        
  
    },
    
    fileUploader: null,
    
    initFileUploaderForDropzone: function(){
        if (DimeView.fileUploader){
            return;
        }
        
        DimeView.fileUploader = new qq.FileUploader({
            element: document.getElementById("dropzoneNavigationBtn"),
            action: Dime.psHelper.getPathForImageUpload(),
            uploadButtonText: 'upload resource',
            debug: true,            
            multiple: false,        
            forceMultipart: false,
            hideDropzones: false,
            onComplete: function(id, fileName, responseJSON){
                console.log("received response:", responseJSON);
                if (responseJSON 
                    && responseJSON.response
                    && responseJSON.response.meta
                    && responseJSON.response.meta.status               
                    && (responseJSON.response.meta.status.toLowerCase()==="ok")
                    ){
                            
                    responseJSON.success=true; //set success == true for processing of result by fileuploader
                    DimeView.search(); //update container
                }
      
            }
            
        }); 
    },
    
    resetSearch: function(){
        
        var searchText = document.getElementById('searchText');
        searchText.value="";
        DimeView.search();
    },
    
    search: function(){

        var searchText = document.getElementById('searchText');
        //console.log('search:', searchText.value);    
        DimeView.searchFilter = searchText.value;

        DimeView.cleanUpView();

        if (DimeView.viewManager.getCurrentViewType()===DimeViewStatus.LIVEPOST_VIEW){
            DimeView.LivePostView.search(searchText.value);
            return;
        }
        
        DimeView.searchCallForType(DimeView.viewManager.getCurrentGroupType());

        //also search on global search if groupType==GROUP
        if (DimeView.viewManager.getCurrentGroupType()===Dime.psMap.TYPE.GROUP && searchText.value && (searchText.value.length>0)){

            DimeView.initContainer($('#globalItemNavigation'), "di.me Users in the di.me User Directory");
            Dime.REST.searchGlobal(searchText.value, DimeView.handleGlobalSearchResult);

        }else{
            DimeView.viewManager.setViewVisible.call(DimeView.viewManager, 'globalItemNavigation', false);
        }        

        if (DimeView.viewManager.getCurrentItemType()){ 
            DimeView.searchCallForType(DimeView.viewManager.getCurrentItemType());
        }
    },

    updateNewButton: function(groupType){

        var createPAMenuItems=function(){
            var result=[];
            var type = Dime.psMap.TYPE.PROFILEATTRIBUTE;
            var paCategories = Dime.PACategory.getListOfCategories();

            jQuery.each(paCategories, function(){

                var pACategory = this;
                var link= $('<a tabindex="-1" href="#" />')
                .append('<span style="margin-left: 5px;">- '+pACategory.caption+' ..</span>')
                .clickExt(Dime.Dialog,function(){
                        var newItem = Dime.psHelper.createNewItem(type, "My "+pACategory.caption);
                        newItem.category=pACategory.name;
                        Dime.Dialog.showNewItemModal(type, null, newItem);
                    });

                result.push($('<li/>').attr('role','menuitem').append(link));
            });

            return result;
        };

        //populate new dialog
        var createMenuItem = function(type){
            var link= $('<a tabindex="-1" href="#" />')
                .text('New '+Dime.psHelper.getCaptionForItemType(type)+' ..')
                .clickExt(Dime.Dialog,
                    function(event, jElement, type){Dime.Dialog.showNewItemModal(type);}, type);
            return $('<li/>').attr('role','menuitem').append(link);
        };
        var dropDownUl=$('#actionButtonDropDownNew');
        dropDownUl.empty();

        if (groupType===Dime.psMap.TYPE.GROUP){
            dropDownUl
                .append(createMenuItem(Dime.psMap.TYPE.GROUP))
                .append(createMenuItem(Dime.psMap.TYPE.PERSON));

        } else if(groupType===Dime.psMap.TYPE.DATABOX){
            dropDownUl
                .append(createMenuItem(Dime.psMap.TYPE.DATABOX))
                .append(createMenuItem(Dime.psMap.TYPE.RESOURCE));

        }else if(groupType===Dime.psMap.TYPE.LIVESTREAM){
            dropDownUl
                .append(createMenuItem(Dime.psMap.TYPE.LIVEPOST));

        } else if(groupType===Dime.psMap.TYPE.PROFILE){
            dropDownUl
                .append(createMenuItem(Dime.psMap.TYPE.PROFILE))
                .append($('<li/>').attr('role','menuitem')
                    .addClass('newMenuItemGroupCaption')
                        .text('New '+Dime.psHelper.getCaptionForItemType(Dime.psMap.TYPE.PROFILEATTRIBUTE))
                    );
            
            var paItems = createPAMenuItems();
            for (var j=0;j<paItems.length;j++){
                dropDownUl.append(paItems[j]);
            }   

        } else if(groupType===Dime.psMap.TYPE.SITUATION){
            dropDownUl.append(createMenuItem(Dime.psMap.TYPE.SITUATION));

        } 

    },

     

    updateMoreButton: function(groupType){
        //populate more dialog
        var createMenuItem = function(caption, callBack){
            var link= $('<a tabindex="-1" href="#" />')
                .text(caption)
                .clickExt(DimeView, callBack, DimeView.selectedItems);
            return $('<li/>').attr('role','menuitem').append(link);
        };

        var dropDownUl=$('#actionButtonDropDownMore');
        dropDownUl.empty();

        if (groupType===Dime.psMap.TYPE.GROUP){
            dropDownUl                
                .append(createMenuItem("Merge persons ..", function(event, jElement){
                    var selectedItems = DimeView.getSelectedItemsForView();
                    var mergeGuids = [];
                    for (var i=0; i<selectedItems.length;i++){
                        mergeGuids.push(selectedItems[i].guid);
                    }
                
                    var dialog = new Dime.MergeDialog(mergeGuids);                
                    dialog.show(function(resultStatus){
                        if (resultStatus!==dialog.STATUS_PENDING){
                            DimeView.viewManager.updateViewFromStatus(DimeView.viewManager.status, true);
                        }
                    }, DimeView);
                })); 
        }
        dropDownUl
                .append(createMenuItem("Create new rule for selected ..", function(event, jElement, selectedItems){
                    window.alert("Create new rule for selected currently not supported!");
                }));

    },
            
    
    updateMetaBar: function(groupType){
        
        if (groupType===Dime.psMap.TYPE.GROUP){
            $('#metaDataShareAreaSharedWithCaption').text('Can access');
        }else{
            $('#metaDataShareAreaSharedWithCaption').text('Shared with');
        }
    },

  updateNavigation: function(newStatus){
        var groupType = newStatus.groupType;
        //update navigation button shown
        if (groupType===Dime.psMap.TYPE.DATABOX){
            Dime.Navigation.setButtonsActive("navButtonData");
            $('#searchText').attr('placeholder', 'find data');
        }else if (groupType===Dime.psMap.TYPE.PROFILE){
            Dime.Navigation.setButtonsActive("navButtonProfile");
            $('#searchText').attr('placeholder', 'find my profile cards');
        }else if (groupType===Dime.psMap.TYPE.GROUP){
            Dime.Navigation.setButtonsActive("navButtonPeople");
            //$('#searchText').attr('placeholder', 'find persons and groups (single search-word, avoid incomplete names)');
            $('#searchText').attr('placeholder', 'search in your contacts and di.me user directory');
        }else if (groupType===Dime.psMap.TYPE.LIVESTREAM){
            Dime.Navigation.setButtonsActive("navButtonMessages");
            $('#searchText').attr('placeholder', 'find liveposts');
        }else if (groupType===Dime.psMap.TYPE.EVENT){
            Dime.Navigation.setButtonsActive("navButtonEvent");
            $('#searchText').attr('placeholder', 'find events');
            //added alert for not supported calendar
            DimeView.viewManager.showAlertStatusNavigation.call(DimeView.viewManager, 
                                "The calendar is not yet supported in the research prototype, sorry.");
        }else if (groupType===Dime.psMap.TYPE.USERNOTIFICATION){
            Dime.Navigation.setButtonsActive("notificationIcon");
            $('#searchText').attr('placeholder', 'find notifications');
        }else if (groupType===Dime.psMap.TYPE.SITUATION){
            Dime.Navigation.setButtonsActive("currentSituation");
            $('#searchText').attr('placeholder', 'find situations');
        }else if (groupType===Dime.psMap.TYPE.PLACE){
            Dime.Navigation.setButtonsActive("currentPlace");
            $('#searchText').attr('placeholder', 'find places');         
        }
        
        //activate dropzone
        if (newStatus.itemType===Dime.psMap.TYPE.RESOURCE){
            DimeView.viewManager.setViewVisible.call(DimeView.viewManager, 'dropzoneNavigation', true);
            DimeView.initFileUploaderForDropzone();
        }

        DimeView.updateNewButton(groupType);        
        DimeView.updateMoreButton(groupType);

        DimeView.updateMetaBar(groupType);
        
        if (newStatus.isSettingsView()){
            Dime.Navigation.setButtonsActive("navButtonSettings");
            Dime.Settings.updateView();
         
        }
    },
            
    updatePersonViewContainers: function(personEntry){
        //select the clicked person for preselection in the share-dialog
        DimeView.clearSelectedItems();
        DimeView.updateActionView(1);
        
        var handleUpdatedFunction = function(updatedItem){
            Dime.REST.updateItem(updatedItem,function(){
                (new Dime.Dialog.Toast("Trust level for "+ personEntry.name + " has been updated.")).show();
            }, DimeView);
        };
        
        //adding person information
        $('#currentPersonOverview')
            .empty()
            .append(
                $('<div></div>')
                    .addClass("currentPersonImage")
                    .append(
                        Dime.psHelper.getImageUrlJImageFromEntry(personEntry)
                    )
            )
            .append(
                $('<div></div>')
                    .addClass("currentPersonInformation")
                    .append(
                        $('<div></div>')
                            .addClass("personInformationName")
                            .append(personEntry.name)
                    )
                    .append(
                        $('<div></div>')
                            .addClass("personInformationChange")
                            .append("Last change: ")
                            .append(JSTool.millisToFormatString(personEntry.lastModified))
                    )
                    .append(
                        Dime.Dialog.getPrivTrustElement(personEntry, false, handleUpdatedFunction)
                            .addClass("personInformationTrust")
                    )
            )
            .append(
                $('<div></div>').addClass("clear")
            );
        
        
        var updateProfileContainer=function(response){            
            DimeView.handleSearchResultForContainer(Dime.psMap.TYPE.PROFILE,
                    response, $('#personProfileNavigation'), true);
        };
        
        var updateProfileAttributeContainer=function(response){                        
            DimeView.handleSearchResultForContainer(Dime.psMap.TYPE.PROFILEATTRIBUTE,
                   response, $('#personProfileAttributeNavigation'), false);
        };
        
        var updateLivepostContainer=function(response){              
            DimeView.handleSearchResultForContainer(Dime.psMap.TYPE.LIVEPOST,
                    response, $('#personLivepostNavigation'), false);
        };
        
        var updateDataboxContainer=function(response){   
            DimeView.handleSearchResultForContainer(Dime.psMap.TYPE.DATABOX,
                    response, $('#personDataboxNavigation'), true);
        };
        var updateResourceContainer=function(response){  
            DimeView.handleSearchResultForContainer(Dime.psMap.TYPE.RESOURCE,
                    response, $('#personResourceNavigation'), false);
        };
                
        Dime.REST.getAll(Dime.psMap.TYPE.PROFILE, updateProfileContainer, personEntry.guid);
        Dime.REST.getAll(Dime.psMap.TYPE.PROFILEATTRIBUTE, updateProfileAttributeContainer, personEntry.guid);       
        Dime.REST.getAll(Dime.psMap.TYPE.LIVEPOST, updateLivepostContainer, personEntry.guid);        
        Dime.REST.getAll(Dime.psMap.TYPE.DATABOX, updateDataboxContainer, personEntry.guid);
        Dime.REST.getAll(Dime.psMap.TYPE.RESOURCE, updateResourceContainer, personEntry.guid);
        
    },
            
    /**
     * called from back button in index.html
     */
    restoreGroupView:function(){
        DimeView.viewManager.updateView.call(DimeView.viewManager, Dime.psMap.TYPE.GROUP, DimeViewStatus.GROUP_CONTAINER_VIEW, false);
    },            
    
    LivePostView:{
        search: function(searchTerm){
            
            var callbackLivePost = function(livePosts){
                DimeView.LivePostView.handleSearchResult(livePosts, searchTerm);                
            };
            
            Dime.REST.getAllAll(Dime.psMap.TYPE.LIVEPOST, callbackLivePost);
        },
        handleSearchResult: function(allLivePosts, searchTerm){
            /* 
               contains items: :{                            
                acl: acl
                sender: userId of sender // used for non-acl entries 
                livePosts:[]
                } 
             */
            var livePostByReceivers = []; 
            
            var assignSentLivePost= function(livePost){
                var myAcl = Dime.psHelper.getAclOfItem(livePost);
                
                for (var i=0;i<livePostByReceivers.length;i++){
                    var entry = livePostByReceivers[i];
                    if (Dime.psHelper.aclsAreEqual(entry.acl, myAcl)){
                        entry.livePosts.push(livePost);
                        return;
                    }
                }
                //else not found --> create new entry
                livePostByReceivers.push({
                   acl: myAcl,
                   sender: null,
                   livePosts:[livePost]
                });
            };            
            
            var receivedLivePosts=[];
            
            //sort my livepost by receivers
            jQuery.each(allLivePosts, function(){
                if (this.userId==='@me'){
                    assignSentLivePost(this);
                }else{
                    //collect received LPs in array
                    receivedLivePosts.push(this);
                }
            });
            
            var personArraysContainPersonGuid= function(arr1, personGuid){                
                for (var i=0; i<arr1.length;i++){                    
                    if (arr1[i].personId===personGuid){
                        return true;
                    }
                }
                return false;
            };
            
            var personFitsToEntry=function(saidSender, personGuid, entry){
                if (entry.acl){                    
                    for (var l=0;l<entry.acl.length;l++){
                        //check for said sender first if available
                        if (saidSender && saidSender!==entry.acl[l].saidSender){
                            return false;
                        }                    
                        
                        if (personArraysContainPersonGuid(entry.acl[l].persons, personGuid)){
                            return true;
                        }
                    }
                    return false;
                }//else
                return personGuid===entry.sender;
            };
            
            var assignReceivedLivePost=function(livePost){
                var saidSender = livePost['nso:sharedWith'];
                for (var i=0;i<livePostByReceivers.length;i++){
                    var entry = livePostByReceivers[i];                    
                    if (personFitsToEntry(saidSender, livePost.userId, entry)){
                        entry.livePosts.push(livePost);
                        return;
                    }
                }
                //no fitting acl found
                livePostByReceivers.push({
                   acl: null,
                   sender: livePost.userId,
                   livePosts:[livePost]
                });
            };
            
            //sort in received LivePosts
            jQuery.each(receivedLivePosts, function(){
               assignReceivedLivePost(this); 
            });
            
            //sort livePostByReceivers by creation date
            //TODO sort livePostByReceivers by creation date
            //currently this is done on the raw liveposts on fetching
            
            //load all data required for the next steps
            var allMyData=new Dime.AllMyDataContainer();
            
            var filterThread=function(threadEntry){
                if (!searchTerm || searchTerm.length===0){
                    return true;
                }
                for (var i=0;i<threadEntry.livePosts.length;i++){
                    if (JSTool.isSubString(searchTerm, threadEntry.livePosts[i].name)
                        || JSTool.isSubString(searchTerm, threadEntry.livePosts[i].text)
                    ){
                        
                        return true;
                    }
                }
                return false;
            };


            var loadingDone=function(){
                //generate items and add them to the item container
                jQuery.each(livePostByReceivers, function(){
                    //filter based on searchTerm
                    //TODO search also for thread party members (requires small refactoring)
                    if (filterThread(this)){
                        DimeView.LivePostView.addItemForThread(this, allMyData);
                    }
                });
            };
            allMyData.load(loadingDone);            
        },
        
        addItemForThread: function(threadEntry, allMyData){
            var getMyEntry=function(guid, type){
                for (var i=0; i<allMyData[type].length;i++){
                    if (allMyData[type][i].guid===guid){
                        return allMyData[type][i];
                    }                    
                }
                return null;
            };
        
            var getCaption = function(){
                if (threadEntry.acl){
                    var firstEntry = true;
                    var result = "";
                    var addNamesFromArray=function(myArr, type){                        
                        for (var i=0; i<myArr.length; i++){
                            var person = (type===Dime.psMap.TYPE.PERSON)?getMyEntry(myArr[i].personId, type):getMyEntry(myArr[i], type);
                            if (person){
                                firstEntry?firstEntry=false:result+=", ";                                
                                result+=person.name;
                            }
                        }
                    };
                    for (var k=0;k<threadEntry.acl.length;k++){
                        addNamesFromArray(threadEntry.acl[k].groups, Dime.psMap.TYPE.GROUP);
                        addNamesFromArray(threadEntry.acl[k].persons, Dime.psMap.TYPE.PERSON);
                        addNamesFromArray(threadEntry.acl[k].services, Dime.psMap.TYPE.ACCOUNT);
                    }
                    
                    return result;
                    
                }//else
                var senderEntry = getMyEntry(threadEntry.sender, Dime.psMap.TYPE.PERSON);
                if (senderEntry){
                    return senderEntry.name;
                }
                return "unknown";
            };
            
            var getProfileForSaid=function(said){
                var allProfiles=allMyData[Dime.psMap.TYPE.PROFILE];
                for (var p=0;p<allProfiles.length;p++){                
                    if (allProfiles[p].said
                            && allProfiles[p].said===said){
                        return allProfiles[p];
                    }
                }
                //else
                console.log('ERROR: unable to find profile with said: '+said);
                return null;
            };
            
            var getMeCaption = function(){
                if (threadEntry.acl){
                    var firstEntry=true;
                    var result="You (";
                    for (var a=0; a<threadEntry.acl.length;a++){
                        var profile = getProfileForSaid(threadEntry.acl[a].saidSender);
                        if (profile){
                            firstEntry?firstEntry=false:result+=", ";                                
                            result+=profile.name;
                        }
                    }
                    result+=')';
                }else{
                    result = 'You';
                }
                return result;
            };
            
            var livePostContainer = $('<div>').addClass('livePostThreadBody');
            
            jQuery.each(threadEntry.livePosts, function(){
               var jLivePostElement = $('<div>')
                    .addClass('livePost')
                    .addClass(this.userId==='@me'?'livePostMe':'livePostThem')
                    .append(DimeView.createMark(this, "", false))
                    .append($('<div/>').text(JSTool.millisToDateString(this.created)).addClass("livePostTime"))
                    .append($('<div/>').text(this.name).addClass('livePostTitle'))
                    .append($('<div/>').text(this.text).addClass('livePostText'));
               
                DimeView.setActionAttributeForElements(this, jLivePostElement, false, true);
                
                livePostContainer.append(jLivePostElement);
               
            });
    
            $('#itemNavigation')
                .append($('<div/>').addClass('livePostThread')
                    .append($('<div/>').addClass('livePostThreadHeader')
                        .append($('<span/>').text(getMeCaption()).addClass('livePostMeCaption')) 
                        .append($('<span/>').text(getCaption()).addClass('livePostThemCaption'))
                        .click(function(){
                            livePostContainer.toggleClass('livePostThreadBodyReduced');
                        })
                    )
                    .append(livePostContainer)
                );        
        }

    },
    

    OrangeBubble: function(handlerSelf, caption, bubbleBody, dismissHandler){

        var bubbleSelf = this;

        this.bubbleId="Bubble_"+JSTool.randomGUID();

        //bubbleBody.addClass('modal-body');
        bubbleBody.addClass('bubble-body');

        var headerElement=
            $('<div></div>').addClass("modal-header")
            .append($('<button type="button" class="close" data-dismiss="modal" aria-hidden="true" >&lt&lt</button>')
                .click(function(){
                        bubbleSelf.dismiss.call(bubbleSelf);
                        dismissHandler.call(handlerSelf);
                    }
                ))
            .append($('<h3 id="myModalLabel">'+caption+'</h3>\n'));


        this.bubble= $('<div/>')
          //  .addClass('modal')
            .addClass('orangeBubble')
            .attr('id',this.bubbleId)
            .append(headerElement)
            .append(bubbleBody)
            ;
    },

    showAbout: function(){

        //if dialog is shown already - dismiss
        if (DimeView.bubble){
            DimeView.bubble.dismiss();
            DimeView.bubble=null;
            return;
        }
        var serverInfo = Dime.ps_configuration.serverInformation; //initially retrieved in main.js

        var loginbaselink=Dime.ps_configuration.getRealBasicUrlString()
        +'/dime-communications/web/access/';
        var githubLink='http://dime-project.github.io/';
        var loginFromServerSettings=serverInfo.baseUrl+'/access/login';
        var questionaireLink = Dime.ps_configuration.getQuestionairePath();


        var bubbleBody = $('<div/>')
        .append(
            $('<div/>')
                .append($('<h3/>').text('Your feedback to the concept of di.me is very important for us!').css('font-size','16px'))
                .append($('<p/>')
                    .append($('<span/>').text('Please follow our')).css('margin-top','10px')
                    .addHrefOpeningInNewWindow('/dime-communications/static/ui/dime/howto.html','Guided Tour','orangeBubbleLink')
                    .append($('<span/>').text('... and get the mobile App'))
                    .addHrefOpeningInNewWindow('http://dimetrials.bdigital.org:8080/dimemobile.apk','for Android.','orangeBubbleLink')

                    
                    .css('font-size','16px')

                    )
                .append($('<h3/>').text('Please fill out our brief questionaire:')
                    .addHrefOpeningInNewWindow(questionaireLink+'?lang=en',' (English)','orangeBubbleLink')
                    .addHrefOpeningInNewWindow(questionaireLink+'?lang=de','(German)','orangeBubbleLink')
                    .css('font-size','16px').css('margin-bottom','30px')
                )
                

                .append($('<h3/>').text('This is a demonstration prototype'))
                .append($('<p/>')
                    .append($('<span/>').text('.. so you will find many bugs and issues.'))
                    .append($('<br/>')).append($('<span/>').text('Please report them on:'))
                    .addHrefOpeningInNewWindow(githubLink+'issues',githubLink+'issues','orangeBubbleLink')
                )
                .append($('<h3/>').text('About'))
                .append($('<p/>')
                    .append($('<span/>').text('The test trial homepage:'))
                    .addHrefOpeningInNewWindow('http://dimetrials.bdigital.org:8080/dime','dimetrials.bdigital.org','orangeBubbleLink')
                )
                .append($('<p/>')
                    .append($('<span/>').text('di.me open source: '))
                    .addHrefOpeningInNewWindow(githubLink,githubLink,'orangeBubbleLink')
                )
                .append($('<p/>')
                    .append($('<span/>').text('The research project:'))
                    .addHrefOpeningInNewWindow('http://www.dime-project.eu','www.dime-project.eu','orangeBubbleLink')
                )
                .append($('<p/>')
                    .append($('<span/>').text('Your dime-server @ '+serverInfo.affiliation+":"))
                    .addHrefOpeningInNewWindow(loginFromServerSettings,serverInfo.baseUrl+"/[..]/login",'orangeBubbleLink')
                )
                .append($('<table/>')
                .append($('<tr/>')
                    .append($('<td/>')
                        .addHrefOpeningInNewWindow(loginbaselink+"conditions",'Usage Conditions (EN)','orangeBubbleLink'))
                    .append($('<td/>')
                        .addHrefOpeningInNewWindow(loginbaselink+"conditions?lang=de",'Nutzungsbedingungen (DE)','orangeBubbleLink'))
                    )
                .append($('<tr/>')
                    .append($('<td/>')
                        .addHrefOpeningInNewWindow(loginbaselink+"privacypolicy",'Privacy declaration (EN)','orangeBubbleLink'))
                    .append($('<td/>')
                        .addHrefOpeningInNewWindow(loginbaselink+"privacypolicy?lang=de",'Datenschutzerklärung (DE)','orangeBubbleLink'))
                )
                .append($('<tr/>')
                    .append($('<td/>')
                        .addHrefOpeningInNewWindow(loginbaselink+"about",'Imprint (EN)','orangeBubbleLink'))
                    .append($('<td/>')
                        .addHrefOpeningInNewWindow(loginbaselink+"about?lang=de",'Impressum (DE)','orangeBubbleLink'))

                ))
        );

        DimeView.bubble = new DimeView.OrangeBubble(this,'Welcome and many thanks for trying out di.me!',  bubbleBody, function(){
            //dismiss handler
            DimeView.bubble=null;
        });
        DimeView.bubble.show();


    }//END DimeView.showAbout()
};

DimeView.OrangeBubble.prototype = {

    show: function(){
        $('body').append(this.bubble);
    },
    dismiss: function(){
        $(this.bubble).remove();
    }
};

DimeView.viewManager = new DimeViewManager(DimeView);

//---------------------------------------------
//#############################################
//  DimeView - END
//#############################################
//



//---------------------------------------------
//#############################################
//  Dime.Settings
//#############################################
//


Dime.Settings = {
    evaluationInfoHtml:'<span>The following data will be collected for evaluation:</span><br><ul><li>an anonymous identifyier which allows us to know what click data and questionaire answers come from the same account. No other identity information like your di.me username, nickname, real name, or email-address is sent. No location information is sent.</li><li>statistics about how many contacts, files, messages, and connected systems you use in your system. Only the number and time when created, but no information about names, content, or anything else is sent.</li><li>data about what type of pages you click in the system (e.g. a page „person“). This includes the time the page was clicked. No title, text, or other content of the pages are sent.</li></ul><br/><span>We do not use other click analysis (like e.g. Google Analytics). You can switch this off at any time on the page "Settings".</span>',

    //in segovia some services have been hidden
    //hiddenServices: ['SocialRecommenderServiceAdapter', 'AMETICDummyAdapter', 'Facebook'],
    hiddenServices: [],

    createServiceAccountElement: function(item) {
        
        return $('<div title="' + item.name + '"></div>')
                .addClass("wrapConnect")
                .clickExt(Dime.Settings, Dime.Settings.editServiceAccount, item)
                .append(Dime.psHelper.getImageUrlJImageFromEntry(item))
                .append("<b>" + DimeView.getShortNameWithLength(item.name, 22) + "</b></br>")
                .append(
                    $("<span></span>")
                    .addClass("serviceActiveMessage")
                    .append("[active]")
                    );
    },

    getAdapterByGUID: function(guid, callback) {

        Dime.REST.getItem(guid, Dime.psMap.TYPE.SERVICEADAPTER, callback, "@me", Dime.Settings);
    },

    initServiceAdapters: function(adapters) {
        
        var dropdownList = $("#addNewServiceAdapterDropDown").empty();

        for (var i = 0; i < adapters.length; i++) {

            //hide adapter if in Dime.Settings.hiddenServices
            var hideAdapter = false;
            for (var j = 0; j < Dime.Settings.hiddenServices.length; j++) {
                if (adapters[i].name === Dime.Settings.hiddenServices[j]) {
                    hideAdapter = true;
                }
            }
            if (hideAdapter) {
                continue;
            }
            //end hide

            //append service adapter to dropdown
            var dropItem = $("<li></li>").attr("role", "menuitem")
            .append(
                $('<a tabindex="-1" target="_blank"  id="'
                    + adapters[i].guid + '">' + adapters[i].name.substring(0, 16) + '</a>')
                //DimeView.getShortNameWithLength(adapters[i].name, 16)
                .clickExt(this, Dime.Settings.dropdownOnClick, adapters[i])
                .append(
                    Dime.psHelper.getImageUrlJImageFromEntry(adapters[i]).attr('width','20px').attr('align','middle')
                ));
            dropdownList.append(dropItem);
        }

    },

    initServiceAccounts: function(accounts) {

        var serviceContainer=$('#serviceContainer').empty();
        

        for (var i = 0; i < accounts.length; i++) {

            //hide adapter if in Dime.Settings.hiddenServices
            var hideAdapter = false;
            for (var j = 0; j < Dime.Settings.hiddenServices.length; j++) {
                if (accounts[i].name === Dime.Settings.hiddenServices[j]) {
                    hideAdapter = true;
                }
            }
            if (hideAdapter) {
                continue;
            }
            //end hide
            serviceContainer.append(Dime.Settings.createServiceAccountElement(accounts[i]));

        }
        serviceContainer.append($('<div/>').addClass('clear'));
    },

    editServiceAccount: function(event, element, item){
        var serviceAdapterCallback=function(serviceAdapter){
            var dialog = new Dime.ConfigurationDialog(this, this.configurationSubmitHandler);
            dialog.show(serviceAdapter.name, serviceAdapter.description, item, false);
        };

        Dime.Settings.getAdapterByGUID(item.serviceadapterguid, serviceAdapterCallback);
        
    },

    deactivateServiceAccount: function(event, element, serviceAccount) {
        $("#lightBoxBlack").fadeOut(300);
        $('#ConfigServiceDialogID').remove();
        var callBackHandler = function(response) {
            console.log("DELETED service " + serviceAccount.guid + " - response:", response);
        };

        Dime.REST.removeItem(serviceAccount, callBackHandler, Dime.Settings);
    },

    toggleTab: function(element, containerId) {
        if (!containerId || !containerId.length === 0 || !element) {
            return;
        }

        var containerElement = $('#'+containerId);

        if (containerElement.hasClass("expandedSettingsTab")) {
            //collapse
            containerElement.removeClass("expandedSettingsTab");
            containerElement.addClass("collapsedSettingsTab");
            element.setAttribute("src", "img/settings/open.png");
        } else {
            //expand
            containerElement.removeClass("collapsedSettingsTab");
            containerElement.addClass("expandedSettingsTab");
            element.setAttribute("src", "img/settings/collaps.png");
        }
    },

    updateView: function(){
        Dime.Settings.updateSettings();
        Dime.Settings.updateServices();
        Dime.Settings.updateAccounts();
    },


    updateSettings: function(){
       
        var self = this;
        var handleUserSettings=function(user){
            var evaluationCheckbox=function(){
                var checkboxId=JSTool.randomGUID();

                var input = $('<input/>')
                    .attr('id',checkboxId)
                    .attr('type','checkbox')
                    .prop("checked", user.evaluationDataCapturingEnabled)
                    .css('clear', 'both')
                    .click(function(){
                        //update user
                        user.evaluationDataCapturingEnabled=input.prop("checked");
                        Dime.ps_configuration.userInformation = user; //direct update settings - will be refreshed from update user with the callback
                        Dime.REST.updateUser(user,Dime.Settings.updateSettings, Dime.Settings);
                        if (user.evaluationDataCapturingEnabled){
                            (new Dime.Dialog.Toast("Send evaluation-data: activated")).show();
                        }else{
                            (new Dime.Dialog.Toast("Send evaluation-data: deactivated")).show();
                        }
                    });
                    
                $(this)
                    .append($('<div/>').text("Evaluation:").css('float', 'left'))
                    .append(Dime.Dialog.Helper.getInfoBox(self.evaluationInfoHtml, "evaluationInfoBox", "evaluationInfoIcon"))
                    .append(input)
                    .append($('<label/>')
                        .attr('for',checkboxId)
                        .text('send evaluation-data')
                        .css('width', '186px').css('float', 'right')
                );
            };

            var changePasswordButton=function(){
                var myPass=null;
                var passDlgShown=false;
                var myContainer = $(this);
                var input= $('<button/>').addClass('YellowMenuButton').text("Change Password")
                    .click(function(){
                        if (passDlgShown){
                            myContainer.find('.settingsPasswdField').remove();
                            passDlgShown=false;
                            myPass=null;
                            return;
                        }//else
                        passDlgShown=true;
                        myContainer.append($('<div/>').addClass('settingsPasswdField')
                            .append($('<input/>').attr('type','password').attr('placeholder','enter new password')
                                .keyup(function(event){
                                    if (event.keyCode === 13) {
                                        if (!myPass){//first time
                                            myPass=$(this).val();
                                            $(this).attr('placeholder','enter password again').val("");
                                        }else{ //retyped
                                            var secondPass = $(this).val();
                                            if (secondPass!==myPass){                                            
                                                (new Dime.Dialog.Toast("Please try again! - Your passwords didn't match!")).showLong();
                                            }else{
                                                user.password=myPass;
                                                Dime.REST.updateUser(user, Dime.Settings.updateSettings, Dime.Settings);
                                                (new Dime.Dialog.Toast("Password updated successfully!")).show();
                                            }
                                            myPass=null;
                                            myContainer.find('.settingsPasswdField').remove();
                                        }
                                    }
                                })
                        )
                        );
                        });
                myContainer.append(input);
            };


            var container = $('#generalSettingsContainer');
            container.empty();
            container
                .append($('<div/>').addClass('settingsSettings').append(evaluationCheckbox))
                .append($('<div/>').addClass('settingsSettings').append(changePasswordButton))
            ;
        };
        Dime.REST.getUser(handleUserSettings, Dime.Settings);
    },

    updateServices: function() {
                
        var callback = function(response) {
            Dime.Settings.initServiceAdapters(response);
        
        };
        
        Dime.REST.getAll(Dime.psMap.TYPE.SERVICEADAPTER, callback , '@me', Dime.Settings);

    },

    updateAccounts: function() {

        var callback = function(response) {
            Dime.Settings.initServiceAccounts(response);
        
        };

        Dime.REST.getAll(Dime.psMap.TYPE.ACCOUNT, callback , '@me', Dime.Settings);
    },

    createAccount: function(serviceAdapter) {
        var newAccount = Dime.psHelper.createNewItem(Dime.psMap.TYPE.ACCOUNT, serviceAdapter.name+"_account");
        newAccount.imageUrl=serviceAdapter.imageUrl;
        newAccount.serviceadapterguid = serviceAdapter.guid;
        //deep copy of settings
        var clonedArray = $.map(serviceAdapter.settings, function(obj){
            return $.extend(true, {}, obj);
        });
        newAccount.settings = clonedArray;
        return newAccount;
    },

    dropdownOnClick: function(event, jqueryItem, serviceAdapter) {
        
        if (serviceAdapter.authUrl) {
            //showing service description before open in new window
            var dialog = new Dime.ConfigurationDialog(this, this.configurationSubmitHandler);
            dialog.showAuth(serviceAdapter.name, serviceAdapter.description, serviceAdapter.authUrl);
        } else {
            //create account item
            var newAccountItem = Dime.Settings.createAccount(serviceAdapter);
            var dialog = new Dime.ConfigurationDialog(this, this.configurationSubmitHandler);
            dialog.show(serviceAdapter.name, serviceAdapter.description, newAccountItem, true);
        }
    },

    configurationSubmitHandler: function(serviceAccount, isNewAccount) {
        var callBack;

        if(isNewAccount){
            callBack = function(response) {
                console.log("NEW ACCOUNT: ", response);
                if (!response|| response.length<1){
                    (new Dime.Dialog.Toast("Creation of "+serviceAccount.name+" failed!")).show();
                }else{
                    (new Dime.Dialog.Toast(serviceAccount.name+ " created successfully.")).show();
                }
            };
            Dime.REST.postNewItem(serviceAccount, callBack);
        }else{
            callBack = function(response) {
                console.log("ACCOUNT UPDATED: ", response);
                if (!response || response.length<1){
                    (new Dime.Dialog.Toast("Updating "+serviceAccount.name+" failed!")).show();
                }else{
                    (new Dime.Dialog.Toast(serviceAccount.name+ " updated successfully.")).show();
                }
            };
            Dime.REST.updateItem(serviceAccount, callBack);
        }
    }
};


//---------------------------------------------
//#############################################
//  Dime.Settings - END
//#############################################
//---------------------------------------------


//---------------------------------------------
//#############################################
//  Dime.initProcessor
//#############################################
//---------------------------------------------


/**
 * init page
 * 
 * handler for URL-parameter
 * @param callback 
 */
Dime.initProcessor.registerFunction(function(callback){ 
    
    $('#contentContainer').mouseoutExt(DimeView, DimeView.hideMouseOver);
    
    
    //set event listeners to group container
    $('#groupNavigation').click(function(){
        //reset search text
        
       DimeView.search();  //TODO move to update Navigation?
    });
    
    DimeView.viewManager.updateViewFromUrl.call(DimeView.viewManager);
    
    
    callback();
});

/**
 * show info page on first login
 *
 * handler for URL-parameter
 * @param callback 
 */
Dime.initProcessor.registerFunction(function(callback){
    var getUserCallback=function(response){
        if (!response){
            return;
        }
        if (response.userStatusFlag===0){            
            //update user status
            response.userStatusFlag=1;
            Dime.REST.updateUser(response);
            DimeView.showAbout();
        }
    };
    Dime.REST.getUser(getUserCallback, DimeView);

    callback();
});


/**
 * show info page on first login
 *
 * handler for URL-parameter
 * @param callback 
 */
Dime.initProcessor.registerFunction(function(callback){
    var getUsernotificationCallback=function(userNotifications){
        var unReadUNs=[];
        jQuery.each(userNotifications, function(){
           if (!this.read){
               unReadUNs.push(this);
           }
        });
        Dime.Navigation.updateNotificationBar(unReadUNs);        
    };
    Dime.REST.getAll(Dime.psMap.TYPE.USERNOTIFICATION, getUsernotificationCallback, "@me", this);
    
    callback();
});

//---------------------------------------------
//#############################################
//  Dime.initProcessor - END
//#############################################
//---------------------------------------------
//---------------------------------------------
//#############################################
//  Dime.Navigation 
//#############################################
//---------------------------------------------

Dime.Navigation = {
    
    MAX_NOTIFICATION_ITEMS: 6,
    
    shownNotifications:0,
    
    receivedNotifications:0,
    
    updateView : function(notifications){
        DimeView.viewManager.updateViewFromNotifications.call(DimeView.viewManager, notifications);
    },
            
    createMenuLiButtonWithViewType : function(id, caption, containerGroupType, viewType){

        return $('<li/>').attr('id',id).append($('<a/>')
            .click(function(){
                //update view
                DimeView.viewManager.updateView.call(DimeView.viewManager, containerGroupType, viewType);
            })
            .text(caption));
    },

    createMenuLiButton : function(id, caption, containerGroupType){
        return Dime.Navigation.createMenuLiButtonWithViewType(id, caption, containerGroupType, DimeViewStatus.GROUP_CONTAINER_VIEW);
    },

    createMenuLiButtonSettings: function(){
        return $('<li/>').attr('id','navButtonSettings').append($('<a/>')
            .click(function(){
                //update view
                DimeView.viewManager.updateView.call(DimeView.viewManager, "", DimeViewStatus.SETTINGS_VIEW);
            })
            .text('Settings'));

    },
    
    createNotificationIcon:function(){
        return $('<div/>').addClass('notificationIcon').attr('id','notificationIcon')
            .click(function(){
                //update view
                DimeView.viewManager.updateView.call(DimeView.viewManager, Dime.psMap.TYPE.USERNOTIFICATION, DimeViewStatus.GROUP_CONTAINER_VIEW);
            })
            .append($('<div/>').attr('id','notificationCounter').text("0"));
    },

    createUserNotificationElement :function(userNotification){      
       
        var jUnBarElement = $("<div></div>").addClass("notificationElement");
       
        var handleChildItemContent=function(senderPersonItem){  
            var deployFunction = function(unValues, clickFunction){
                jUnBarElement
                    .click(clickFunction)
                    .text(unValues.shortCaption.substr(0, 16))
                    .click(function(){
                        jUnBarElement.remove();
                     });
                
            };
            DimeView.createUserNotificationLinkAndContent(userNotification, senderPersonItem, deployFunction);
        };
        if (userNotification.unType===Dime.psMap.UN_TYPE.REF_TO_ITEM && Dime.un.isShareOperation(userNotification.unEntry.operation)){
            Dime.REST.getItem(userNotification.unEntry.userId, Dime.psMap.TYPE.PERSON, handleChildItemContent, '@me', this);
        }else{
            handleChildItemContent(null);
        }        
       
        return jUnBarElement;
        
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
                        .textOnly(DimeView.getShortNameWithLength(resultSituation, 26))
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
            
            if(resultSituation!==""){
                updateSituationElement(resultSituation);
            }else{
                updateSituationElement("Situation: unknown");
            }
        };
        
        
        Dime.REST.getAll(Dime.psMap.TYPE.SITUATION, handleSituationCallBack);
    },
    
    updateCurrentPlace: function(){
        var handleCurrentPlaceCallBack=function(placeLocation){
            
            var updateCurPlaceElement=function(placeName, placeId){
                var placeElement = document.getElementById('currentPlace');
                placeElement.innerHTML =  '<div class="places">'
                + '<div class="placesIcon" id="currentPlaceGuid" data-guid="' + placeId + '"></div>'
                + DimeView.getShortNameWithLength(placeName, 26)+'</div>';
                $("#currentPlace").attr("title", placeName);
            };
            
            if (!placeLocation || !placeLocation.nextPlace){
                updateCurPlaceElement("Location: unknown");                
            }else if (placeLocation.currPlace && placeLocation.currPlace.placeId && placeLocation.currPlace.placeName){
               updateCurPlaceElement(placeLocation.currPlace.placeName, placeLocation.currPlace.placeId);                              
            }else{
                updateCurPlaceElement(DimeView.getInnerPosition(placeLocation, false));            
            }
            
        };
        Dime.psHelper.getPositionAndPlaceInformation(handleCurrentPlaceCallBack, DimeView);
        
        
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
            var situationLink = $('<div/>') 
            .attr('id','currentSituation')
            .append($('<div/>').addClass('clear'))
            .append($('<div/>').addClass('situation').attr('id','currentSituationText')
                .textOnly('Situation: unknown')
                .append($('<div/>').addClass('situationIcon'))
                )
            .click(function(){
                DimeView.viewManager.updateView.call(DimeView.viewManager, Dime.psMap.TYPE.SITUATION, DimeViewStatus.GROUP_CONTAINER_VIEW, false);
            });
            var placeLink = $('<div/>')
            .attr('id','currentPlace')
            .append(
                $('<div/>').addClass('places')
                .append($('<div/>').addClass('placesIcon'))
                )
            .click(function(){
                DimeView.viewManager.updateView.call(DimeView.viewManager, Dime.psMap.TYPE.PLACE, DimeViewStatus.GROUP_CONTAINER_VIEW, false);
            });

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
            .append(Dime.Navigation.createMenuLiButtonWithViewType("navButtonMessages","Livepost" ,Dime.psMap.TYPE.LIVESTREAM, DimeViewStatus.LIVEPOST_VIEW))
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
        .append($('<div/>').attr('id','navBarSpacer'))
        .append(navigation)
        .append(notificationBar)
        ;

        

        var navBarInner=$('<div/>').addClass('navbar-inner').append(navContainer);


        $('#navBarContainer').append(navBarInner);

        /* RESPONSIVE NAVIGATION */

        $(window).scroll(function(){
            var yOffset=57;
            //console.log($(this).scrollTop());
            $('#metabarMetaContainer').css('top', $(this).scrollTop()+yOffset);
        });

        var adaptUIToSize = function(){
            var width=$(window).width();
            if (width>1170){
                $('#navButtonMessages').find('a').text('Livepost');
            }else if (width>=670 &&width<=1170){
                $('#navButtonMessages').find('a').text('');
            }
        };

        $(window).resize(function(){
            //adapt on resize
            adaptUIToSize();
        });

        //adapt initial size
        adaptUIToSize();
    }

  
};

Dime.initProcessor.registerFunction( function(callback){

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

        var userString = Dime.ps_configuration.mainSaid+'@'+response.name;
        $('#username').text(userString.substr(0, 21)).click(function(){
            DimeView.showAbout.call(DimeView);
        });
        callback();
    };
    Dime.REST.getServerInformation(serverInfoCallBack);
    
});


Dime.Navigation.registerCometCall = function(){    

    //register comet call
    Dime.initProcessor.registerFunction( function(callback){
        
        Dime.REST.getCOMETCall(Dime.Navigation.handleNotification);
        callback();

    });
};
//initially register once - all subsequent registrations will be done in the error handler
Dime.Navigation.registerCometCall();

/**
 * initially load situations and places
 * @param callback 
 */
Dime.initProcessor.registerFunction( function(callback){
    
    Dime.Navigation.updateSituations();
    Dime.Navigation.updateCurrentPlace();
    callback();
});











/**
 * handle back button
 * @param event
 */
 window.onpopstate = function(event) {
     var viewState = event.state;     
     if (viewState){
         console.log(viewState);
         DimeView.viewManager.updateViewFromStatus.call(DimeView.viewManager, viewState, true);
     }
};

