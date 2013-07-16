<!DOCTYPE html>
<%-- 
    Document   : login
    Created on : May 29, 2013, 4:14:44 PM
    Author     : simon
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="postLoginUrl" value="/j_spring_security_check" />
<c:url value="/j_spring_security_logout" var="logoutUrl"/>
<html>
    <head>


        <title>di.me Server</title>
        <link href="/dime-communications/static/ui/dime/register/css/bootstrap.css" rel="stylesheet" type="text/css" />
        <link href="/dime-communications/static/ui/dime/register/css/generic.css" rel="stylesheet" type="text/css" />
        <link href="/dime-communications/static/ui/dime/register/css/style.css" rel="stylesheet" type="text/css" />
        <link href="/dime-communications/static/ui/dime/register/css/bootstrap-responsive.min.css" rel="stylesheet"  type="text/css"/>
        <link href="/dime-communications/static/ui/dime/register/css/style-responsive.css" rel="stylesheet"  type="text/css"/>


    </head>
    <body>


        <!-- receive some flags from jsp -->
        <script type="text/javascript">
            var initialContainerId='<c:out value="${jspContainerId}"/>';
        </script>


        <div class="navbar navbar-inverse navbar-fixed-top">
            <!-- --------------------------------------------------------
            ----------------------   navigation container----------------
            ------------------------------------------------------------->

            <div class="navbar-inner">
                <div class="container">

                    <div class="language-push">

                        <div data-selected="0" id="DE"  class="language">DE</div>
                        <div data-selected="0" id="EN"  class="language">EN</div>

                        <div class="buttonleiste">
                            <div class="term1"><div class="linkToAbout" >Imprint</div></div>
                            <div class="term2"><div class="linkToPrivacyPolicy" >Privacy Policy</div></div>
                            <div  class="term3"><div class="linkToUsageTerms">Usage Terms</div></div>

                            <div class="term4"><div class="linkToAbout_DE">Impressum</div></div>
                            <div class="term5"><div class="linkToPrivacyPolicy_DE">Datenschutzerklärung</div></div>
                            <div  class="term6"><div class="linkToUsageTerms_DE">Nutzungsbedingungen</div></div>

                        </div>
                    </div>

                    <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <div class="brand linkToLogin"><img class="logo" src="/dime-communications/static/ui/dime/register/img/logo.png"/></div>

                    <div class="nav-collapse collapse">


                        <ul class="nav">
                            <li><div class="linkToLogin">Login</div></li>
                            <li><div class="linkToRegister" >Register</div></li>
                        </ul>

                    </div><!--/.nav-collapse -->

                </div>
            </div>



        </div> 
        <div class="clear"></div>




        <div id="projectAbstract" class="container">

            <div class="bumper"></div>

            <div class="dime_logo_small"><img src="/dime-communications/static/ui/dime/register/img/logo.png"/></div><br>

            <div id="projectAbstractText">
                <div class="float_this">

                    Trusted Social Networking and Personal Identity<br>
                    The European research project digital.me evaluates a system prototype.<br>

                    <h2 id="serverNameHeader">di.me Server &lt;name of institution&gt;</h2>
                    <h5>A server of the di.me prototype evaluation 2013</h5>

                </div>

                <div class="float_this">                    
                    <img class="institutionLogo" src="/dime-communications/static/ui/dime/register/img/dummyInstitute.png"/>
                </div>
            </div>

        </div>

        <div class="container">
            <hr>
        </div>


        <div class="clear"></div>
        <!-- --------------------------------------------------------
        ----------------------   loginContainer ---------------------
        ------------------------------------------------------------->

        <div id="loginContainer" class="container contentContainer">
            <div id="header1" class="container">

                <h1>Login</h1>

            </div>
            <div id="intro" class="container">
                <div class="row-fluid show-grid">

                    <div class="span6" data-role="signin"><h4>Login with a existing account</h4>
                        <br>

                        <form class="login" action="${postLoginUrl}" method="post">

                            <div class="loginGroup">

                                <div class="controls">
                                    <input id="usernameLogin" class="required" type="text" size="20" name="j_username"maxlength="80" placeholder="di.me username"/>

                                </div>



                                <div class="controls">
                                    <input id="passwordLogin" type="password" class="required" type="text" size="20" name="j_password" maxlength="80" placeholder="Password"/>
                                </div>

                            </div>
                            <div class="loginGroup" id="loginMessage">${jspLoginMessage}
                            </div>
                            <div class="loginGroup"  id="loginRememberAndSubmitButton">
                                <div class="controls">

                                    <input type="checkbox" name="_spring_security_remember_me"/> Remember me
                                </div>
                                <div class="controls">
                                    <button id="loginButton" type="submit" data-selected="button" class="btn btn-large btn-block btn-primary">
                                        Dime Login</button>
                                </div>
                            </div>

                        </form>


                    </div>

                    <div id="register_push" class="span6" data-role="signin"><h4>Get a new di.me account</h4>
                        Please try out the prototype and give us feedback!
                        <div class="regist registButton">
                            <a data-selected="button" class="btn btn-large btn-block btn-primary linkToRegister" >
                                Register...
                            </a>
                        </div>
                    </div>
                </div>
            </div>       
            <br/>
            <hr/>
            <h5>Getting started</h5>
            <p >
                <img width="120px" id="qr" src="/dime-communications/static/ui/dime/register/img/dime-mobile-qr.jpg"><br>
                <a id="pushthis" href="http://dimetrials.bdigital.org:8080/dimemobile.apk"> Get the the android App!</a></p>
            
            </br> How to use di.me: check out the <a class="linkToHowto">how-to page</a> <br>

            <br/>
            <hr/>


            <c:if test="${param.failed == true}">
                <div>Your login attempt failed. Please try again.</div>
            </c:if>
        </div>



        <!-- --------------------------------------------------------
        ----------------------   registerContainer ------------------
        ------------------------------------------------------------->


        <div id="registerContainer" class="container contentContainer hidden">
            <h1>Register</h1>
            <div class="row-fluid show-grid">

                <div class="span6">

                    <form class="Registration" name="registrationForm">
                        <h3>1. Registration data</h3>
                        <div class="control-group">
                            <div class="controls">
                                <input type="text" class="required" id="registrationUsername" placeholder="Username">
                                <h6>The username is your master-identity in di.me It is not shown to other people</h6>
                                <input type="password" class="required" id="registrationPassword" placeholder="Choose Password">
                                <input type="password" class="required" id="registrationPassword2" placeholder="Retype Password">
                                <h6>Please enter a valid email</h6>
                                <input type="text" class="required" id="registrationEmail" placeholder="me@my-email.com">

                            </div>
                        </div>
                    </form>
                </div>


                <div class="span6">
                    <form class="Registration">
                        <h3>2. Connect to other users via the DimeUserDirectory</h3>
                        <div class="control-group">
                            <div class="controls">
                                <p>To use di.me, you should publish a public mini-profile on the DimeUserDirectory. This directory is public for all di.me users, so that you can get contacted by others in the network. You can use a nickname, or also your real name.</p>
                                <input type="text" id="prsNickname" placeholder="public nickname">
                                <input type="text" id="prsFirstname" placeholder="Firstname">
                                <input type="text" id="prsLastname" placeholder="Lastname">
                                <p>To change this later go to the page "Settings".</p>

                            </div>
                        </div>
                    </form>
                </div>


            </div>
            <div class="row-fluid show-grid">
                <div class="span12">

                    <p>
                    <h3>3. Please contribute to the scientific evaluation</h3>
                    </p>
                    <p>
                        We kindly ask you to send us <em>anonymous click data.</em>
                        These data cannot be traced back to your person. We use these data only to
                        analyse how users use our prototype.
                    </p>
                    <p>
                        The following data are collected:
                    </p>
                    <ul>
                        <li>
                            an <em>anonymous identifyier </em>which allows us to know what click data and questionaire answers come
                            from the same account.
                            No other identity information like your di.me username, nickname, real name, or email-address is sent.
                            No location information is sent.
                        </li>
                        <li>
                            statistics about how many contacts, files, messages, and connected systems you use in your system.
                            <em>Only the number and time when created, </em>but no
                            information about names, content, or anything else is sent.
                        </li>
                        <li>
                            data about <em>what type of pages you click </em>in the system (e.g. a page „person“). 
                            This includes the time the page was clicked.
                            No title, text, or other content of the pages are sent.
                        </li>
                    </ul>
                    <p>
                        We do not use other click analysis (like e.g. Google Analytics).
                        You can <em>switch this off at any time </em>on the page “Settings“.
                    </p>


                    <br>
                    <div class="control-group white-well">
                        <div class="controls">
                            <input class="radio_1" type="radio" id ="registerAgreeYes" name="registerAgree" value="yes" checked> I agree </input>
                            <input class="radio_1"  type="radio" name="registerAgree" value="no"> I don't agree</input>
                        </div>
                    </div>
                </div>
                <div class="row-fluid show-grid">
                    <div class="span12" id="register_push">

                        <p>
                            <strong>Scientific purpose of the test server</strong>
                        </p>
                        <p>
                            <span class="adaptAffiliation"></span> provides this server as test operation for a system prototype of the dime userware. The objective is to give access to and to evaluate the
                            results of the research project digital.me (www.dime-project.eu). The test operation is not intended to provide a service for end-users or enterprises for
                            other purposes than the above stated. The test operation has no commercial interests.
                        </p>
                        <p>
                            By registering to our server, you agree to our usage conditions
                        </p>
                        <p>
                            Please read our full usage conditions: 
                            <span class="linkToUsageTerms_DE">Nutzungsbedingungen (Deutsch)</span>, 
                            <span class="linkToUsageTerms">Usage Terms (English)</span>
                        </p>

                        <div class="regist">
                            <div id="registerSubmitButton" data-selected="button" class="btn btn-large btn-block btn-primary">
                                Register
                            </div>

                        </div>
                        <div id="registerErrorMessage"/>
                    </div>
                </div>
            </div>

        </div>
    </div> 



    <div id="aboutContainer" class="container contentContainer hidden"></div>


    <div id="aboutContainer_DE" class="container contentContainer hidden"></div>

    <div id="usageTermsContainer" class="container contentContainer hidden"></div>

    <div id="usageTermsContainer_DE" class="container contentContainer hidden"></div>

    <div id="privacyPolicyContainer" class="container contentContainer hidden"></div>


    <div id="privacyPolicyContainer_DE" class="container contentContainer hidden"></div>







    <!-- JS -->
    <script src="/dime-communications/static/ui/dime/register/js/jquery-1.9.1.min.js" type="text/javascript"></script>
    <script src="/dime-communications/static/ui/dime/register/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/dime-communications/static/ui/dime/register/js/registry_script.js" type="text/javascript"></script>

</body>
</html>