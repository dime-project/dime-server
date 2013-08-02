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
    }

});


//---------------------------------------------
//#############################################
//  DIME.register
//#############################################
//

Dime={};
Dime.register={
    currentContainerId:'',

    ajaxError: function(x, e) {
        Dime.register.toggleWaitModal(false);
            
        if (x.status === 0) {
            console.log('Check Your Network.', e, x);
        }
        else if (x.status === 404) {
            console.log('Requested URL not found.', e, x);
        } else if (x.status === 500) {
            console.log('Internal Server Error.', e, x);
        } else {
            console.log('Unknow Error.\n' + x.responseText);
        }
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

    getServerInformation: function(callBack, callerSelf){

        if (!callerSelf){
            callerSelf = this;
        }



        var callPath = 'https://'+ window.location.host+ '/dime-communications/api/dime/server';

        var jointCallBack = function(response){
            var result = null;
            if (response){

                response=response.response;

                if (response && response.meta && response.meta.status && response.meta.code
                    && (response.meta.status.toLowerCase()==="ok")
                    && response.data && response.data.entry && response.data.entry.length>0){
                    result = response.data.entry[0];
                }
            }
            callBack.call(callerSelf, result);
        };
        $.getJSON(callPath, "", jointCallBack);
    },


    LINK_CLASSES:{
        'linkToRegister':{
            targetId:'registerContainer',
            urlFragment:'register'
        },
        'linkToLogin':{
            targetId:'loginContainer',
            urlFragment:'login'
        },
        'linkToHowto':{
            targetUrl:'/dime-communications/static/ui/dime/howto.html'

        },
        'linkToFeedback':{
            targetUrl:'questionaire?lang=en'
        },
        'linkToAbout':{
            targetId: 'aboutContainer',
            urlFragment:'about',
            loadUrl:'/dime-communications/static/ui/dime/register/html/about.html'

        },
        'linkToUsageTerms':{
            targetId: 'usageTermsContainer',
            urlFragment:'conditions',
            loadUrl:'/dime-communications/static/ui/dime/register/html/conditions.html'
        },
        'linkToPrivacyPolicy':{
            targetId:'privacyPolicyContainer',
            urlFragment:'privacypolicy',
            loadUrl:'/dime-communications/static/ui/dime/register/html/privacypolicy.html'
        },
		
        'linkToAbout_DE':{
            targetId: 'aboutContainer_DE',
            urlFragment:'about?lang=de',
            loadUrl:'/dime-communications/static/ui/dime/register/html/about_DE.html'
        },
        'linkToUsageTerms_DE':{
            targetId: 'usageTermsContainer_DE',
            urlFragment:'conditions?lang=de',
            loadUrl:'/dime-communications/static/ui/dime/register/html/conditions_DE.html'
        },
        'linkToPrivacyPolicy_DE':{
            targetId:'privacyPolicyContainer_DE',
            urlFragment:'privacypolicy?lang=de',
            loadUrl:'/dime-communications/static/ui/dime/register/html/privacypolicy_DE.html'
        }
    },
    activateLinks: function(jStartElem){
        jQuery.each(Dime.register.LINK_CLASSES,function(k){
            var linkElements = jStartElem.find('.'+k);
            var targetId = this.targetId;
            var targetUrl = this.targetUrl;
            if (linkElements){
                if (targetId){//local navigation
                    linkElements.click(function(event){
                        Dime.register.showContainer(targetId);
                    });
                }
                if (targetUrl){
                    linkElements.click(function(event){

                        window.open(targetUrl, "_blank", "");
                    });
                }
            }
        });
    },

    init: function(){
        //init error handler for ajax calls
        $.ajaxSetup({
            error: Dime.register.ajaxError
            });
        //enable links for navigation
        Dime.register.activateLinks($('body'));

        //set link for register
        $('#registerSubmitButton').click(function(){
            Dime.register.sendRegistrationForm();
        });

        //finally show containerId set from jsp if any
        if (initialContainerId){            
            Dime.register.showContainer(initialContainerId);
        }else{            
            Dime.register.showContainer('loginContainer');
        }

        //update server specific fields
        var serverInfoCallBack=function(serverInfo){
            document.title =serverInfo.name;
            var exp = /(\b(https?|ftp|file):\/\/)([-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/i;

            var servername = serverInfo.baseUrl.replace(exp, "$3");

            $('.institutionLogo').attr('src',serverInfo.imageUrl);
            $('#serverNameHeader').text(serverInfo.name);
            $('.adaptAffiliation').text(serverInfo.affiliation);
            $('.adaptServerName').text(servername);
        };
        Dime.register.getServerInformation(serverInfoCallBack, Dime.register);


    },


    showContainer:function(newId, dontSaveNavigation){
        jQuery.each(Dime.register.LINK_CLASSES,function(key){
            
            var linkElement=$('.'+key);
	
            //local navigation
            if (this.targetId){
                if(this.targetId===newId){
                    if(!dontSaveNavigation && Dime.register.currentContainerId){
                        window.history.pushState({
                            oldId:Dime.register.currentContainerId}
                            , "", this.urlFragment);
                    }
                    var targetDiv = $('#'+this.targetId);
                    if (this.loadUrl && targetDiv.is(':empty')){
                        targetDiv.load(this.loadUrl, null, function(){
                            Dime.register.activateLinks(targetDiv);
                        });
                    }
                    targetDiv.removeClass('hidden');
                    linkElement.addClass('active');
                }else{
                    $('#'+this.targetId).addClass('hidden');
                    linkElement.removeClass('active');
                }
            }
        });
        Dime.register.currentContainerId=newId;
    },

    REGISTRATION_REQUIRED_FIELDS:{
        'registrationUsername':{
            caption:'Please fill in a username!'
        },
        'registrationPassword':{
            caption:'Please fill in a password!'
        },
        'registrationEmail':{
            caption:'Please enter a valid email address!'
        }
    },

    validateReform: function(){
        var warnStr ="";
        var result=true;
        jQuery.each(this.REGISTRATION_REQUIRED_FIELDS, function(fieldId){
            if ((!$('#'+fieldId).val())||$('#'+fieldId).val().length===0){
                result=false;
                warnStr+="\n" + this.caption;
            }
        });

        if (!result){
            warnStr = "No all required fields ar provided."+ warnStr;
            window.alert(warnStr);
            return false;
        }


        if($('#registrationPassword').val()!==$('#registrationPassword2').val()){
            window.alert("The given passwords are not equal. Please try again.");
            return false;
        }

        return true;
    },

    getRegFormEntry: function(){
        return {
            username: $('#registrationUsername').val(),
            password: $('#registrationPassword').val(),
            nickname: $('#prsNickname').val(),
            firstname: $('#prsFirstname').val(),
            lastname: $('#prsLastname').val(),
            checkbox_agree: $('#registerAgreeYes').prop('checked'),
            emailAddress:  $('#registrationEmail').val()
        };
    },

    toggleWaitModal :function(switchOn){
        if (!switchOn){
            $('#Dime_register_modalWaitDlg').addClass('hide');
            return;
        }
        if ($('#Dime_register_modalWaitDlg').length){
            $('#Dime_register_modalWaitDlg').removeClass('hide');
            return; //to not create a second one if exists already
        }

        $('body').append($('<div/>').attr('id', 'Dime_register_modalWaitDlg').addClass("modal")
            .append($('<div/>').addClass("modal-header").text('Please wait ...'))
            .append($('<div/>').addClass("modal-body")
                .append(
                    $('<div/>').addClass("progress progress-striped active")
                    .append('<div class="bar" style="width: 99%;"></div>')
                    )
                )
            .append($('<div/>').addClass("modal-footer")));
    },

    sendRegistrationForm: function(){
        if (!this.validateReform()){
            return;
        }

        var request= this.getRegFormEntry();

        var callback=function(response){
            Dime.register.toggleWaitModal(false);
            if (!response){
                $('#registerErrorMessage').text('Registration failed for unknown reason!');
                return;
            }
            response=response.response;

            if (!response || !response.meta || !response.meta.status || !response.meta.code){
                $('#registerErrorMessage').text('Registration failed: Incomplete response structure (meta)!');
                return;
            }
            if(response.meta.status.toLowerCase()!=="ok"){
                $('#registerErrorMessage').text('Registration failed: '+response.meta.msg);
                return;
            }
            if (!response.data||!response.data.entry||!response.data.entry[0].username){
                $('#registerErrorMessage').text('Registration failed: Incomplete response structure (data)!');
                return;
            }
            //else
            $('#loginMessage').text('Registration was successful! Please login.');

            $('#usernameLogin').val(request.username);
            $('#passwordLogin').val(request.password);

            Dime.register.showContainer('loginContainer');

        };


        var path = 'https://'+ window.location.host+ '/dime-communications/api/dime/user';

        var envelope = Dime.register.prepareRequest(request);

        console.log("(request, path)\n",envelope, path);


        $.postJSON(path, envelope, callback);
        Dime.register.toggleWaitModal(true);

    }
};
	
	
$(document).ready(function() {
	
    $('.language').hover(function(){
        $(this).css('cursor','pointer');
    });
	
	
    $('#DE').click(function(){
        $('.term1').css('visibility','hidden');
        $('.term2').css('visibility','hidden');
        $('.term3').css('visibility','hidden');
				
        $('.term4').css('visibility','visible');
        $('.term5').css('visibility','visible');
        $('.term6').css('visibility','visible');
				
				
        $('.term5').css('width','150px');
        $('.term6').css('width','250px');
				
        $('#DE').css('color','lightgray');
        $('#EN').css('color','gray');
				
        $('.term1, .term2, .term3, .term4, .term5, .term6').css('margin-left','5px');

    });
	
	
    $('#EN').click(function(){

        $('.term1').css('visibility','visible');
        $('.term2').css('visibility','visible');
        $('.term3').css('visibility','visible');
				
        $('.term4').css('visibility','hidden');
        $('.term5').css('visibility','hidden');
        $('.term6').css('visibility','hidden');
				
				
        $('.term5').css('width','150px');
        $('.term6').css('width','250px');
				
				
        $('#EN').css('color','lightgray');
        $('#DE').css('color','gray');
				
        $('.term1, .term2, .term3, .term4, .term5, .term6').css('margin-left','55px');
				
    });

    Dime.register.init();
	
});


window.onpopstate = function(event) {
     var state = event.state;
     if (state){
         Dime.register.showContainer(state.oldId, true);
     }
};

	
	
