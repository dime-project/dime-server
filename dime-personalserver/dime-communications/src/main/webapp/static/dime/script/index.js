/* 
 *  Description of index.js
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 04.07.2012
 */


//---------------------------------------------
//#############################################
//  DimeView 
//#############################################
//


DimeView = {
    
    searchFilter: "",
    groupType:Dime.psMap.TYPE.GROUP,
    itemType:Dime.psMap.TYPE.PERSON,
    currentGuid: null,
    pushState:{}, //browser history to manage back-button
    
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

        result.clickExt(DimeView, DimeView.selectItem, entry, isGroup);
        return result;
    },
    
    setActionAttributeForElements: function(entry, jElement, isGroupItem, showEditOnClick){
       
        
        jElement.mouseoverExt(DimeView, DimeView.showMouseOver, entry, isGroupItem);
        jElement.mouseoutExt(DimeView, DimeView.hideMouseOver, entry);
            
        

        if(isGroupItem){
            jElement.clickExt(DimeView, DimeView.showGroupMembers, entry);
        }else if (entry.type===Dime.psMap.TYPE.PERSON){
            jElement.clickExt(DimeView, DimeView.updateViewForPerson, entry);
        
        }else if(showEditOnClick){
            jElement.clickExt(DimeView, DimeView.editItem, entry);
        }
        
        
    },
   
    addGroupElement: function(jParent, entry){
   
        var groupClass=(entry.type!==Dime.psMap.TYPE.GROUP?entry.type+"Item groupItem":"groupItem");
        
        var jGroupItem=$('<div/>').addClass(groupClass).append($('<div/>')
                .append(
                    $('<img/>').attr('src', Dime.psHelper.guessLinkURL(entry.imageUrl)))
                .append(
                    DimeView.createMark(entry, "", true)
                )
                .append('<div class="groupItemCounter" ><h1>'+ entry.items.length + '</h1></div>')
                .append('<div class="clear"></div>')
                .append(
                    $('<h4>'+ DimeView.getShortName(entry.name) + '</h4>')
                        .clickExt(DimeView, DimeView.editItem, entry)
                ));
   

        DimeView.setActionAttributeForElements(entry, jGroupItem, true, false);

        jParent.append(jGroupItem);

    },    
    
    createAttributeItemJElement: function(entry){
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

    createUserNotification: function(entry){


        var jChildItem = $("<div/>");


        var markRead=function(){
            entry.read=true;
            Dime.REST.updateItem(entry);
            jChildItem.addClass('userNotificationWasRead');
        }


        //classes
        var itemClass=entry.type+"Item childItem";
        jChildItem.addClass(itemClass);
        if (entry.read){
            jChildItem.addClass('userNotificationWasRead');
        }else{
            jChildItem.click(markRead);
        }
        
        var unValues=Dime.un.getCaptionImageUrl(entry);

        if (entry.unType===Dime.psMap.UN_TYPE.REF_TO_ITEM){
            var elementType=entry.unEntry.type;

            var groupType = elementType;
            if (Dime.psHelper.isChildType(elementType)){
                groupType=Dime.psHelper.getParentType(elementType);
            }

            var guid = encodeURIComponent(entry.unEntry.guid);
            var userId=entry.unEntry.userId;
            if (userId!=='@me'){
                userId=encodeURIComponent(userId);
            }

            var target = "self.location.href='index.html?type="+ groupType
                +"&guid="+guid
                +"&userId="+userId
                +"&dItemType="+elementType
                +"&msg="+unValues.caption
                +"'";

            jChildItem.attr("onclick", target);
        }else{
            jChildItem.click(function(){
                //TODO fix
                window.alert("This function is not supported in the research prototype.");
            });
        }

        //img
        jChildItem.append($('<img/>').attr('src',Dime.psHelper.guessLinkURL(entry.imageUrl)));
        
        

        
        jChildItem
            .append($('<img/>').attr('src', unValues.imageUrl).addClass('childItemNotifElemType'))
            .append('<h4>'+ unValues.caption + '</h4>')
            .append($('<div/>').addClass('childItemNotifOperation').append('<span>'+ unValues.operation + '</span>'))
            .append($('<span/>').addClass("childItemNotifElemCaption").text(unValues.childName)
            );


        return jChildItem;


    },
    
    createItemJElement: function(entry){
        //handle profileattributes separately
        if (entry.type===Dime.psMap.TYPE.PROFILEATTRIBUTE){
            return DimeView.createAttributeItemJElement(entry);
        }else if (entry.type===Dime.psMap.TYPE.USERNOTIFICATION) {
            return DimeView.createUserNotification(entry);

        }
        
        var showEditOnClick = (entry.type===Dime.psMap.TYPE.SITUATION)
                    || (entry.type===Dime.psMap.TYPE.PLACE)
                    || (entry.type===Dime.psMap.TYPE.RESOURCE)
                    || (entry.type===Dime.psMap.TYPE.LIVEPOST);
        
                
        var jChildItem = $("<div/>");
        
        //classes
        var itemClass=entry.type+"Item childItem";
        
        if (entry.type===Dime.psMap.TYPE.SITUATION){
            if (entry.active){
                itemClass += " childItemSituationActive";
            }
        }        
        jChildItem.addClass(itemClass);
        
        
        //innerChild
        //innerChild - img        
        if (entry.type===Dime.psMap.TYPE.PERSON){
            jChildItem.append('<div class="wrapProfileImage" ><img src="'+ Dime.psHelper.guessLinkURL(entry.imageUrl)+ '" /></div>');
        }else{
            jChildItem.append('<img src="'+ Dime.psHelper.guessLinkURL(entry.imageUrl)+ '" />');
        }
        
        //innerChild - mark
        jChildItem.append(DimeView.createMark(entry, "", false));
        
        //innerChild - name
        var entryName = DimeView.getShortName(entry.name);
        
        
        if (entry.type!==Dime.psMap.TYPE.USERNOTIFICATION){
            jChildItem.append('<h4>'+ entryName + '</h4>');
        }
          
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
    
    groupEntries: [],
    
    itemEntries: [],

    initContainer: function(jContainer, caption, selectingGroupName){
        var containerCaption = JSTool.upCaseFirstLetter(caption);
        if (selectingGroupName){
            containerCaption += ' in "' +selectingGroupName+'"';
        }else if (DimeView.searchFilter.length===0){
            containerCaption = "All "+containerCaption;
        }

        jContainer.empty();
        jContainer.append(
            $('<div/>').attr('id','containerCaption').addClass('h2ModalScreen')
                .text(containerCaption)
                .append($('<div/>').addClass('clear')));

    },
    
    handleSearchResultForContainer: function(type, entries, jContainerElement, isGroupContainer){
        if (!entries || entries.length===0){
            jContainerElement.addClass('hidden');
            return;
        }

        var isSubString=function(fullString, subString){
            return (subString.toLowerCase().indexOf(fullString.toLowerCase())!==-1)
        }

        var isInFilter = function(entry){
            return isSubString(DimeView.searchFilter, entry.name);
        }

        //special search when searching on usernotifications
        if (type===Dime.psMap.TYPE.USERNOTIFICATION){
            isInFilter = function(entry){

                var unValues=Dime.un.getCaptionImageUrl(entry);

                var result =
                    isSubString(DimeView.searchFilter, entry.name)
                    || isSubString(DimeView.searchFilter, unValues.caption)
                    || isSubString(DimeView.searchFilter, unValues.operation)
                    || isSubString(DimeView.searchFilter, unValues.childName)
                    || isSubString(DimeView.searchFilter, unValues.shortCaption)
                ;

                return result;
            }
        }

        //for @me profiles we skip profiles with no said
        if (type===Dime.psMap.TYPE.PROFILE){
            isInFilter = function(entry){
                if ((entry.userId==='@me')
                    && (entry.editable) //only di.me profile is editable and will therefore skipped out
                    &&(!entry.said || entry.said.length<1)){
                    return false; //skip this
                }
                return isSubString(DimeView.searchFilter, entry.name);
                //TODO - also search in attributes
            }

        }


        DimeView.initContainer(jContainerElement, Dime.psHelper.getPluralCaptionForItemType(entries[0].type));

        jContainerElement.removeClass('hidden');

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
            if (item){
                item.element.removeClass("ItemChecked");
            }
        }
        DimeView.selectedItems = {};
        $("#globalActionButton").empty();
        DimeView.updateActionView(0);
    },
    
    ACTION_BUTTON_ID:[
        {
            id: "actionButtonNew",
            minItems: -1,
            maxItems: -1
        },
        {
            id: "actionButtonEdit",
            minItems: 1,
            maxItems: 1
        },
        {
            id: "actionButtonAddRemove",
            minItems: 1,
            maxItems: 0
        },
        {
            id: "actionButtonDelete",
            minItems: 1,
            maxItems: -1
        },
        {
            id: "actionButtonShare",
            minItems: -1,
            maxItems: -1
        },
        {
            id: "actionButtonMore",
            minItems: -1,
            maxItems: -1
        }
    ],
    
    updateActionView: function(selectionCount){
        //var disabledButtonClass = "actionButtonDisabled";
        var disabledButtonClass = "disabled";
        
        var hideButton=function(buttonId){
            var myButton = $("#"+buttonId);
            if (!myButton.hasClass()){
                myButton.addClass(disabledButtonClass);
            }
        };
    
        var showButton=function(buttonId){
            var myButton = $("#"+buttonId);
            myButton.removeClass(disabledButtonClass);
        };
        
        for (var i=0;i<DimeView.ACTION_BUTTON_ID.length;i++){
            
            var actionButton=DimeView.ACTION_BUTTON_ID[i];
            
            if (actionButton.minItems===-1){ //always show this
                //since this is never removed no explicit show required
                continue;
            }
            if (selectionCount<actionButton.minItems){ 
                hideButton(actionButton.id);
                                   
            } else if ((actionButton.maxItems===-1) || (selectionCount<=actionButton.maxItems)){ 
                showButton(actionButton.id);
                
            }else{ //too many selected
                hideButton(actionButton.id);
            }
        }
    },
    
    selectItem: function(event, element, entry, isGroupItem){
        if (event){
            event.stopPropagation();
        }
        var guid=entry.guid;
        
        //item was selected --> unselect item       
        if (DimeView.selectedItems[guid]){     
            DimeView.selectedItems[guid] = null;
            $(element).removeClass("ItemChecked");
            
        }else{
            DimeView.selectedItems[guid] = { //FIXME refactor - only use a hash with guid as key for all items!!!
                guid:guid, 
                userId:entry.userId,
                type:entry.type,
                isGroupItem:isGroupItem,
                element:element
            };
            $(element).addClass("ItemChecked");
        }
        
        var memberCount = JSTool.countDefinedMembers(DimeView.selectedItems);     
        $("#globalActionButton").text(memberCount);
        
        this.updateActionView(memberCount);

    },
    
    showSelectedItems: function(){
        //FIXME show this nicely
        var message = "Selected items:\n";
        
        var mySelectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
        for (var i=0;i<mySelectedItems.length;i++){
            var item = mySelectedItems[i];
            message += item.type + ": "+item.guid+"("+item.userId+")\n";
        }
        
        window.alert(message);
    },
    
    createMetaBarListItem: function(name, value, imageUrl, className){

        if (value===undefined || value===null){
            //show undefined and null values in the browsers
            //while empty string is allowed
            value="undefined";
        }else if (value.length>22){
            value=value.substr(0, 20)+" ..";
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
  
    },

    createMetaBarListItemForItem: function(item){
        var result = DimeView.createMetaBarListItem(
            Dime.psHelper.getCaptionForItemType(item.type),
            item.name,
            item.imageUrl
        );
        //TODO add link to item

        return result;
    },
    
    
  
    addInformationToMetabar: function(entry){
        $("#metaDataInformationHeader").text("Information");

        var informationViewContainer = DimeView.getMetaListContainer(DimeView.CONTAINER_ID_INFORMATION);  
        
        informationViewContainer.append(DimeView.createMetaBarListItem(entry.name,"", entry.imageUrl));
        informationViewContainer.append(DimeView.createMetaBarListItem(
            "changed:", JSTool.millisToFormatString(entry.lastModified), null));
             
        
        if (entry.userId!=='@me'){
            var setProviderName=function(response){
                if (response){
                informationViewContainer.append(DimeView.createMetaBarListItem(
                    "provided by:", response.name, response.imageUrl));
                }
            };
            Dime.REST.getItem(entry.userId, Dime.psMap.TYPE.PERSON, setProviderName, '@me', this);
            
        }
        if (entry.hasOwnProperty("nao:trustLevel")){
            
            var tCaptionAndClass=Dime.privacyTrust.getClassAndCaptionForPrivacyTrust(entry["nao:trustLevel"], false);
            if (!tCaptionAndClass){
                return;
            }                
           
            var tElement = DimeView.createMetaBarListItem("trust:", tCaptionAndClass.caption, null);
            tElement.addClass(tCaptionAndClass.classString);
            informationViewContainer.append(tElement);
        }
        if (entry.hasOwnProperty("nao:privacyLevel")){
            var pCaptionAndClass=Dime.privacyTrust.getClassAndCaptionForPrivacyTrust(entry["nao:privacyLevel"], true);
            if (!pCaptionAndClass){
                return;
            }                
           
            var pElement = DimeView.createMetaBarListItem("privacy:", pCaptionAndClass.caption, null);
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
            }
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
        DimeView.updateMetabarForSelection();
       
    },
       

    showMouseOver: function(event, element, entry, isGroupEntry){        
        
        //FIXME - this check is not required if this is already checked at a prior instance
        if (!entry  || !entry.guid || !entry.userId || !entry.type || entry.type.length===0 ){
            console.log("ERROR: received invalid entry: (entry)", entry);
            return;
        }
        
        //ATTENTION - this solution is not realy thread-safe - see also comment below...
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
                listItemContainer.append(DimeView.createMetaBarListItem( "", agentItems[j].name, agentItems[j].imageUrl, "metaDataShareItem"));
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
                    var profileContainer = DimeView.createMetaBarListItem("shared as:",pName,pImage, "metaDataShareProfile")
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
            
                listItemContainer.append(DimeView.createMetaBarListItem(entry.name, "", entry.imageUrl));
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
        }

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

        var selectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
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
        if (mySelectedItems.length<1){
            window.alert("Please select at least one item to be deleted!");
            return;
        }

        var mySelectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
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

        var selectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);

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
            jGlobalSearchResultContainer.addClass("hidden");
            return;
        }else{
            jGlobalSearchResultContainer.removeClass("hidden");
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
        //HACK??
        if (type===Dime.psMap.TYPE.LIVEPOST){
            Dime.REST.getAllAll(type, callBack);
            return;
        }

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
        console.log('search:', searchText.value);    
        DimeView.searchFilter = searchText.value;

        DimeView.cleanUpView();
        
        if (DimeView.groupType
            && (DimeView.groupType!==Dime.psMap.TYPE.LIVESTREAM) //HACK avoid call for unsupported livestream
        ){
            DimeView.searchCallForType(DimeView.groupType);
            
            //also search on global search if groupType==GROUP
            if (DimeView.groupType===Dime.psMap.TYPE.GROUP && searchText.value && (searchText.value.length>0)){

                DimeView.initContainer($('#globalItemNavigation'), "di.me Users in the di.me User Directory");
                
                Dime.REST.searchGlobal(searchText.value, DimeView.handleGlobalSearchResult);
                
            }else{
                $('#globalItemNavigation').addClass("hidden");
            }
        }else if(DimeView.groupType===Dime.psMap.TYPE.LIVESTREAM){ //HACK avoid call for unsupported livestream
            $("#groupNavigation").addClass("hidden");
        }
        if (DimeView.itemType){ 
            DimeView.searchCallForType(DimeView.itemType);
        }
        
       
    },

    updateNewButton: function(groupType){
        $('#actionButtonNew').removeClass('disabled');

        var createPAMenuItems=function(){
            var result=[];
            var type = Dime.psMap.TYPE.PROFILEATTRIBUTE;
            var paCategories = Dime.PACategory.getListOfCategories();

            jQuery.each(paCategories, function(){

                var pACategory = this;
                var link= $('<a tabindex="-1" href="#" />')
                .text('New '+pACategory.caption+' ..')
                .clickExt(Dime.Dialog,function(){
                        var newItem = Dime.psHelper.createNewItem(type, "My "+pACategory.caption);
                        newItem.category=pACategory.name;
                        Dime.Dialog.showNewItemModal(type, null, newItem);
                    });

                result.push($('<li/>').attr('role','menuitem').append(link));
            });

            return result;
        }

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
            dropDownUl
                .append(createMenuItem(Dime.psMap.TYPE.SITUATION));

        } else if(groupType===Dime.psMap.TYPE.PLACE){
            $('#actionButtonNew').addClass('disabled');
        } 

    },

    updateShareButton: function(groupType){
        var shareBtn = $('#actionButtonShare');

         if(groupType===Dime.psMap.TYPE.SITUATION){
            shareBtn.addClass('disabled');
        } else if(groupType===Dime.psMap.TYPE.PLACE){
            shareBtn.addClass('disabled');
        }else{
            shareBtn.removeClass('disabled');
        }
    },

    updateMoreButton: function(groupType){
        var moreButton = $('#actionButtonMore');

         if(groupType===Dime.psMap.TYPE.SITUATION){
            moreButton.addClass('disabled');
        } else if(groupType===Dime.psMap.TYPE.PLACE){
            moreButton.addClass('disabled');
        }else{
            moreButton.removeClass('disabled');
        }

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
                .append(createMenuItem("Merge persons ..", function(event, jElement, selectedItems){
                    window.alert("Merging of selected items currently not supported!");
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

    //VIEW_MAP

    GROUP_CONTAINER_VIEW: 1,
    SETTINGS_VIEW: 2,
    PERSON_VIEW: 3,

    currentView:0,

    viewMapEntry: function(id, groupActive, settingsActive, personViewActive){
        this.id = id;
        this.groupActive = groupActive;
        this.settingsActive = settingsActive; 
        this.personViewActive = personViewActive;
    },

    viewMap:{}, //initialization at the end of DimeView

    addToViewMap: function(viewMapEntry){
        DimeView.viewMap[viewMapEntry.id]=viewMapEntry;
    },
    /**
     *
     *
     */
    updateView: function(groupType, viewType, avoidPushingHistory){

        if (!viewType){
            viewType = DimeView.GROUP_CONTAINER_VIEW;
        }

        //send some evaluation data
        Dime.evaluation.updateViewStack(groupType, viewType);

        DimeView.currentView=viewType;
        DimeView.switchViewForViewType();

        if (groupType && !avoidPushingHistory){ //FIXME add support for settings by furhter url parameters --> refactoring required!
            window.history.pushState({groupType:groupType, viewType:viewType}, "", "index.html?type="+groupType);
        }

        if (viewType===DimeView.SETTINGS_VIEW){
            Dime.Navigation.setButtonsActive("navButtonSettings");
            Dime.Settings.updateView();
            return;

        }

        if (!groupType){
            //GO on with viewType===PERSON_VIEW and viewType===GROUP_CONTAINER_VIEW
            console.log("grouptype not set - using default: GROUP");
            groupType = Dime.psMap.TYPE.GROUP;
        }
        
        //update grouptype and itemtype
        DimeView.groupType = groupType;        
        DimeView.itemType =  Dime.psHelper.getChildType(DimeView.groupType);

        
        
        //update navigation button shown
        if (DimeView.groupType===Dime.psMap.TYPE.DATABOX){
            Dime.Navigation.setButtonsActive("navButtonData");
            $('#searchText').attr('placeholder', 'find data');
        }else if (DimeView.groupType===Dime.psMap.TYPE.PROFILE){
            Dime.Navigation.setButtonsActive("navButtonProfile");
            $('#searchText').attr('placeholder', 'find my profile cards');
        }else if (DimeView.groupType===Dime.psMap.TYPE.GROUP){
            Dime.Navigation.setButtonsActive("navButtonPeople");
            $('#searchText').attr('placeholder', 'find persons and groups (single search-word, avoid incomplete names)');
        }else if (DimeView.groupType===Dime.psMap.TYPE.LIVESTREAM){
            Dime.Navigation.setButtonsActive("navButtonMessages");
            $('#searchText').attr('placeholder', 'find liveposts');
        }else if (DimeView.groupType===Dime.psMap.TYPE.EVENT){
            Dime.Navigation.setButtonsActive("navButtonEvent");
            $('#searchText').attr('placeholder', 'find events');
        }else if (DimeView.groupType===Dime.psMap.TYPE.USERNOTIFICATION){
            Dime.Navigation.setButtonsActive("notificationIcon");
            $('#searchText').attr('placeholder', 'find notifications');
        }else if (DimeView.groupType===Dime.psMap.TYPE.SITUATION){
            Dime.Navigation.setButtonsActive("currentSituation");
            $('#searchText').attr('placeholder', 'find situations');
        }else if (DimeView.groupType===Dime.psMap.TYPE.PLACE){
            Dime.Navigation.setButtonsActive("currentPlace");
            $('#searchText').attr('placeholder', 'find places');

        }
        
        //activate dropzone
        if (DimeView.itemType===Dime.psMap.TYPE.RESOURCE){
            $("#dropzoneNavigation").removeClass("hidden");
            DimeView.initFileUploaderForDropzone();
        }

        DimeView.updateNewButton(groupType);
        DimeView.updateShareButton(groupType);
        DimeView.updateMoreButton(groupType);

        DimeView.updateMetaBar(groupType);
        DimeView.resetSearch();
    },

    switchViewForViewType: function(){
        var viewType=DimeView.currentView;

        jQuery.each(DimeView.viewMap,function(){
            //viewMapEntry: id, groupActive, settingsActive, personViewActive
            var displayMe=false;
            if (viewType===DimeView.GROUP_CONTAINER_VIEW){
                displayMe=this.groupActive;
            }else if (viewType===DimeView.SETTINGS_VIEW){
                displayMe=this.settingsActive;
            }else if (viewType===DimeView.PERSON_VIEW){
                displayMe=this.personViewActive;
            }
            if (displayMe){
                $('#'+this.id).removeClass('hidden');
            }else{
                $('#'+this.id).addClass('hidden');
            }
        });
    },

    //called from back button in index.html
    restoreGroupView:function(){
        DimeView.updateView(DimeView.groupType, DimeView.GROUP_CONTAINER_VIEW);
        
    },


    updateViewForPerson: function(event, element, entry){
        var guid = entry.guid;

        //check and get item
        if (!guid || guid.length===0){
            console.log("ERROR wrong call updateViewForPerson with guid:", guid);
            return;
        }
        
        //hide other container
        DimeView.currentView=DimeView.PERSON_VIEW;
        DimeView.switchViewForViewType();

        DimeView.currentGuid=guid;

        Dime.evaluation.updateViewStack("", DimeView.currentView);


        
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
                
        Dime.REST.getAll(Dime.psMap.TYPE.PROFILE, updateProfileContainer, guid);
        Dime.REST.getAll(Dime.psMap.TYPE.PROFILEATTRIBUTE, updateProfileAttributeContainer, guid);       
        Dime.REST.getAll(Dime.psMap.TYPE.LIVEPOST, updateLivepostContainer, guid);        
        Dime.REST.getAll(Dime.psMap.TYPE.DATABOX, updateDataboxContainer, guid);
        Dime.REST.getAll(Dime.psMap.TYPE.RESOURCE, updateResourceContainer, guid);
        
        
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
        var githubLink='https://github.com/dime-project/meta/';
        var loginFromServerSettings=serverInfo.baseUrl+'/dime-communications/web/access/login';
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
                    .append($('<span/>').text('di.me open source: comming soon'))
                    //.addHrefOpeningInNewWindow(githubLink,githubLink,'orangeBubbleLink')
                )
                .append($('<p/>')
                    .append($('<span/>').text('The research project:'))
                    .addHrefOpeningInNewWindow('http://www.di.me-project.eu','www.di.me-project.eu','orangeBubbleLink')
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
//                                               id, groupActive, settingsActive, personViewActive
DimeView.addToViewMap(new DimeView.viewMapEntry('groupNavigation', false, false, false)); //initially set to false, so it will only be shown with some content in place
DimeView.addToViewMap(new DimeView.viewMapEntry('itemNavigation', false, false, false)); //initially set to false, so it will only be shown with some content in place
DimeView.addToViewMap(new DimeView.viewMapEntry('searchBox', true, false, false));
DimeView.addToViewMap(new DimeView.viewMapEntry('metabarMetaContainer', true, false, true));
DimeView.addToViewMap(new DimeView.viewMapEntry('backToGroupButton', false, false, true));
DimeView.addToViewMap(new DimeView.viewMapEntry('personProfileAttributeNavigation', false, false, true));
DimeView.addToViewMap(new DimeView.viewMapEntry('personProfileNavigation', false, false, true));
DimeView.addToViewMap(new DimeView.viewMapEntry('personLivepostNavigation', false, false, true));
DimeView.addToViewMap(new DimeView.viewMapEntry('personDataboxNavigation', false, false, true));
DimeView.addToViewMap(new DimeView.viewMapEntry('personResourceNavigation', false, false, true));
DimeView.addToViewMap(new DimeView.viewMapEntry('settingsNavigationContainer', false, true, false));
//the following are deactivated by default and only shown when required
DimeView.addToViewMap(new DimeView.viewMapEntry('dropzoneNavigation', false, false, false));
DimeView.addToViewMap(new DimeView.viewMapEntry('globalItemNavigation', false, false, false));


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
        
        return $('<div></div>')
                .addClass("wrapConnect")
                .clickExt(Dime.Settings, Dime.Settings.editServiceAccount, item)
                .append(
                    $('<img></img>')                    
                    .attr("src", Dime.psHelper.guessLinkURL(item.imageUrl))                    
                   )
                .append("<b>" + item.name.substr(0,17) + "</b></br>")
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
                    + adapters[i].guid + '">' + adapters[i].name.substring(0, 10) + '</a>')
                .clickExt(this, Dime.Settings.dropdownOnClick, adapters[i])
                .append('<img class="serviceAdapterIconDropdown" src="' + adapters[i].imageUrl + '" width="20px" align="middle" />')
                );
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
            dialog.show(serviceAdapter.name,serviceAdapter.description, item, false);
        }

        Dime.Settings.getAdapterByGUID(item.serviceadapterguid, serviceAdapterCallback);
        
    },

    deactivateServiceAccount: function(event, element, serviceAccount) {
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
                                    if (event.keyCode == 13) {
                                        if (!myPass){//first time
                                            myPass=$(this).val();
                                            $(this).attr('placeholder','enter password again').val("")
                                        }else{ //retyped
                                            var secondPass = $(this).val();
                                            if (secondPass!==myPass){                                            
                                                (new Dime.Dialog.Toast("Please try again! - Your passwords didn't match!")).showLong();
                                            }else{
                                                user.password=myPass;
                                                Dime.REST.updateUser(user,Dime.Settings.updateSettings, Dime.Settings);
                                                (new Dime.Dialog.Toast("Password updated successfully!")).show();
                                            }
                                            myPass=null;
                                            myContainer.find('.settingsPasswdField').remove();
                                        }
                                    }
                                })
                        )
                        )
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
            window.open(serviceAdapter.authUrl, "_blank", "");
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
 */
Dime.initProcessor.registerFunction(function(callback){ 
    
    $('#contentContainer').mouseoutExt(DimeView, DimeView.hideMouseOver);
    
    
    //set event listeners to group container
    $('#groupNavigation').click(function(){
        //reset search text
        
       DimeView.search(); 
    });
    
    //set grouptype
    var groupType = Dime.psHelper.getURLparam("type");
    var guid = Dime.psHelper.getURLparam("guid");
    var dItemType = Dime.psHelper.getURLparam("dItemType");
    var userId = Dime.psHelper.getURLparam("userId");
    var message = Dime.psHelper.getURLparam("msg");
    
    //update view
    DimeView.updateView(groupType, DimeView.GROUP_CONTAINER_VIEW);
    
    if (guid && guid.length>0 && userId && userId.length>0 ){
        var showDialog = function (response){
            if (response){
                DimeView.editItem(null, null, response, message);
            }
        }
        Dime.REST.getItem(guid, dItemType, showDialog, userId, DimeView);

    }

    callback();
});

/**
 * show info page on first login
 *
 * handler for URL-parameter
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
    }
    Dime.REST.getUser(getUserCallback, DimeView);

    callback();
});


/**
 * show info page on first login
 *
 * handler for URL-parameter
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
    }
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
//  Dime.Navigation - override
//#############################################
//---------------------------------------------



Dime.Navigation.updateView = function(notifications){
    var refreshContainer = false;
    var refreshSituations = false;
    var refreshPlaces = false;
    var refreshServices = false;
    var refreshAccounts = false;
    var refreshUserSettings=false;
    
    for (var i=0;i<notifications.length;i++){
        if (notifications[i].element){
            var notificationType=notifications[i].element.type;
            if ((notificationType===DimeView.groupType) 
                    || (notificationType===DimeView.itemType)){
                    refreshContainer=true;
            }
            
            //no else here, since the following might be called also in the case notificationType===DimeView.groupType
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


    if (DimeView.currentView===DimeView.GROUP_CONTAINER_VIEW){
        if (refreshContainer){
            DimeView.search();
        }
    }else if (DimeView.currentView===DimeView.SETTINGS_VIEW){
        if(refreshUserSettings){
            Dime.Settings.updateSettings();
        }
        if (refreshServices) {
            Dime.Settings.updateServices();
        }
        if (refreshAccounts) {
            Dime.Settings.updateAccounts();
        }
    }



   
};

Dime.Navigation.createMenuLiButton=function(id, caption, containerGroupType){

    return $('<li/>').attr('id',id).append($('<a/>')
        .click(function(){

            //update view
            DimeView.updateView.call(DimeView, containerGroupType, DimeView.GROUP_CONTAINER_VIEW);
        })
        .text(caption));
};

Dime.Navigation.createMenuLiButtonSettings=function(){
    return $('<li/>').attr('id','navButtonSettings').append($('<a/>')
        .click(function(){
            //update view
            DimeView.updateView.call(DimeView, "", DimeView.SETTINGS_VIEW);
        })
        .text('Settings'));

};


Dime.Navigation.createNotificationIcon=function(){
    return $('<div/>').addClass('notificationIcon').attr('id','notificationIcon')
        .click(function(){
            //update view
            DimeView.updateView.call(DimeView, Dime.psMap.TYPE.USERNOTIFICATION, DimeView.GROUP_CONTAINER_VIEW);
        })
        .append($('<div/>').attr('id','notificationCounter').text("0"));
};

//---------------------------------------------
//#############################################
//  Dime.Navigation - override - END
//#############################################
//---------------------------------------------

/**
 * handle back button --- FIXME not working as intended
 */
 window.onpopstate = function(event) {
     var viewState = event.state;
     if (viewState){
         DimeView.updateView(viewState.groupType, viewState.viewType, true);
     }

  
};