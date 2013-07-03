<%-- 
    Document   : login
    Created on : May 29, 2013, 4:14:44 PM
    Author     : simon
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:url var="postLoginUrl" value="/j_spring_security_check" />
<c:url value="/j_spring_security_logout" var="logoutUrl"/>
<!DOCTYPE html>
<html >
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
                            <li><div class="linkToHowto">How to use di.me</div></li>
                            <li><div class="linkToFeedback">Feedback</div></li>
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
                    <img class=".institutionLogo" src="/dime-communications/static/ui/dime/register/img/dummyInstitute.png"/>
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



            <br><hr>
            <h5>Getting started</h5>
            Get the <a href="">Android App</a>
            </br> how to use di.me: check out the <a class="linkToHowto">how-to page</a> <br>

            <br><hr>

            <h5>Please give us feedback!</h5>
            Fill out our very short <a class="linkToFeedback">questionnaire &lt;Link to LimeSurvey&gt;</a><br>
            <br>
            <a href="">Report errors, bugs, ideas or requirements &lt;link to github&gt;</a>

        </div>



        <c:if test="${param.failed == true}">
            <div>Your login attempt failed. Please try again.</div>
        </c:if>


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



    <div id="howtoContainer" class="container contentContainer hidden">


        <div class="text_wrapper">
            <div>
                <h1>
                    Page “How to use dime”

                    <hr>
                </h1>
            </div>
            <h3>
                Download the App for your Android smartphone
            </h3>
            <p>
                If you have an Android smartphone, try the App:
            </p>
            <p>
                &lt;howto&gt;
            </p>
            <h3>
                Connect di.me to services, e.g. Facebook
            </h3>
            <p>
                &lt;howto: service page, all important service connections&gt;
            </p>
            <h3>
                Look for other people
            </h3>
            <p>
                Look for friends or colleagues who have invited you, people from the di.me project consortium, etc.
            </p>
            <p>
                &lt;howto: Look in public resolver service for dime users and add them&gt;
            </p>
            <p>
                &lt;howto: the user adds (a single) contact from the the android phonebook&gt;
            </p>
            <h3>
                Invite others
            </h3>
            <p>
                &lt;howto: sends invitation to join dime to a person (e.g. for phone book contacts); invite people via tweet, email, …&gt;
            </p>
            <h3>
                Setup different identities
            </h3>
            <p>
                xxx profile cards
            </p>
            <p>
                &lt;howto Setting up new dime profile cards (private, business…), add existing profile attributes from mined external profile cards&gt;
            </p>
            <h3>
                Send messages and share information
            </h3>
            <p>
                Send messages to friends &lt;howto&gt;
            </p>
            <p>
                Upload documents or e.g. fotos on xxx and share them to others
            </p>
            <h3>
                Check out di.me’s privacy recommendations
            </h3>
            <p>
                &lt;howto: Check / change privacy levels &gt;
            </p>
            <p>
                Check / change trust levels
            </p>
            <p>
                Try out sharing to untrusted people, … other mechanisms
            </p>
            <h3>
                People nearby: Check out di.me’s suggestions for Nearby-groups
            </h3>
            <p>
                &lt;xxx&gt;
            </p>
            <h3>
                Give us feedback!
            </h3>
            <p>
                &lt;Please kindly ask you to send us anonymousclick data: howto&gt;
            </p>
            <p>
                &lt;When you have used di.me for a while, please fill out the short questionnaire&gt;
            </p>
        </div>
    </div>


    <div id="feedbackContainer" class="container contentContainer hidden"><h1>Feedback</h1></div>


    <div id="aboutContainer" class="container contentContainer hidden">
        <div class="text_wrapper">
            <!--Imprint_DE----------------------------------------------------------------------------->


            <p>
                <strong><h1>Imprint § 5 TMG</h1></strong>
            </p>
            <p>
                <strong> </strong>
            </p>
            <p>
                <strong>Provider of the prototype of the di.me userware under</strong>
                <strong>xxx.dime2013.iao.fraunhofer.de</strong>
                <strong>:</strong>
            </p>
            <p>

            </p>
            <p>
                Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
                <br/>
                Hansastraße 27 c
                <br/>
                80686 München
                <br/>
                <br/>
                Phone +49 89 1205-0
                <br/>
                Fax +49 89 1205-7531
            </p>
            <p>
                Mail: fabian.hermann@iao.fraunhofer.de
                <br/>
                <br/>
                VAT Identification Number in accordance with §27 a VAT Tax Act: DE 129515865
            </p>
            <p>
                <br/>
                Court of jurisdiction
                <br/>
                Amtsgericht München (district court)
                <br/>
                Registered nonprofit association
                <br/>
                Registration no. VR 4461
            </p>
            <p>
                Executive Board
                <br/>
                Prof. Dr. Reimund Neugebauer, President,Corporate Management and Research
                <br/>
                Prof. Dr. Ulrich Buller, Research Planning
                <br/>
                Prof. (Univ. Stellenbosch) Dr. Alfred Gossner, Finance and Controlling
                (incl. Business Administration, Purchasing and Real Estate), IT
                <br/>
                Dr. Alexander Kurz, Personnel Affairs and Legal Affairs
                <br/>
                <br/>
            </p>
            <p>
                The Fraunhofer Institute for Industrial Engineering IAO
            </p>
            <p>
                Nobelstr. 12
            </p>
            <p>
                70569 Stuttgart
            </p>
            <p>
                has no separate legal status and is a constituent entity of the Fraunhofer-Gesellschaft
                zur Förderung der angewandten Forschung e.V., which hosts and
                provides these web sites.
                <br/>
                <br/>
                <strong>Usage Rights</strong>
                <br/>
                Copyright © by Fraunhofer-Gesellschaft. All rights reserved.
                <br/>
                <br/>
                <strong></strong>
            </p>
            <p>
                Registered trademarks and proprietary names, and copyrighted text and images, are not generally indicated as such on our Web pages. But the absence of such
                indications in no way implies that these names, images or text belong to the public domain in the context of trademark or copyright law.
            </p>
            <p>
                All copyright for this web site are owned in full by the Fraunhofer-Gesellschaft e.V. or by project partners of the project digital.me according tot he web
                site www.dime-project.eu. <strong> </strong>
            </p>
            <p>
                <strong> </strong>
            </p>
            <p>
                Permission is granted to download or print material published on this site for personal use only. Its use for any other purpose, and in particular its
                commercial use or distribution, are strictly forbidden in the absence of prior written approval. Please address your requests for approval to:
            </p>
            <p>
                <br/>
                The Fraunhofer Institute for Industrial Engineering IAO
                <br/>
                Public Relations
                <br/>
                Nobelstraße 12
                <br/>
                70569 Stuttgart
                <br/>
                presse@iao.fraunhofer.de
                <br/>
                <br/>
                Notwithstanding this requirement, material may be downloaded or printed for use in connection with press reports on the activities of the
                Fraunhofer-Gesellschaft and its constituent institutes, on condition that the following terms are complied with:
                <br/>
                <br/>
                No alterations may be made to pictorial content, with the exception of framing modifications to emphasize the central motif. The source must be quoted and
                two free reference copies must be sent to the above-mentioned address. Such usage is free of charge.
            </p>
            <p>
                <strong>Disclaimer</strong>
                <br/>
                We cannot assume any liability for the content of external pages. Solely the operators of those linked pages are responsible for their content.
                <br/>
                <br/>
                We make every reasonable effort to ensure that the content of this Web site is kept up to date, and that it is accurate and complete. Nevertheless, the
                possibility of errors cannot be entirely ruled out. We do not give any warranty in respect of the timeliness, accuracy or completeness of material
                published on this Web site, and disclaim all liability for (material or non-material) loss or damage incurred by third parties arising from the use of
                content obtained from the Web site.
            </p>
            <div>

                <div>
                    <div id="_com_1">
                        <a name="_msocom_1"></a>
                        <p>

                        </p>
                    </div>
                </div>
            </div>

        </div>
    </div>


    <div id="aboutContainer_DE" class="container contentContainer hidden">
        <div class="text_wrapper">

            <p>
                <strong><h1>Impressum nach § 5 TMG</h1></strong>

            </p>
            <p>
                <strong> </strong>
            </p>
            <p>
                <strong>Anbieter des Prototypen der di.me userware unter xxx.dime2013.iao.fraunhofer.de:</strong>
            </p>
            <p>
                <strong> </strong>
            </p>
            <p>
                Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
                <br/>
                Hansastraße 27 c
                <br/>
                80686 München
                <br/>
                <br/>
                Telefon +49 89 1205-0
                <br/>
                Fax +49 89 1205-7531
            </p>
            <p>
                Mail: fabian.hermann@iao.fraunhofer.de
                <br/>
                <br/>
                Umsatzsteuer-Identifikationsnummer gemäß § 27 a
                <br/>
                Umsatzsteuergesetz: DE 129515865
                <br/>
                <br/>
                Registergericht
                <br/>
                Amtsgericht München
                <br/>
                Eingetragener Verein
                <br/>
                Register-Nr. VR 4461
            </p>
            <p>
                Vorstand
                <br/>
                Prof. Dr. Reimund Neugebauer, Präsident, Unternehmenspolitik und Forschung
                <br/>
                Prof. Dr. Ulrich Buller, Forschungsplanung
                <br/>
                Prof. (Univ. Stellenbosch) Dr. Alfred Gossner, Finanzen, Controlling (inkl. Betriebswirtschaft, Einkauf, Liegenschaften), IT
                <br/>
                Dr. Alexander Kurz, Personal und Recht
                <br/>
                <br/>
            </p>
            <p>
                Das Fraunhofer-Institut für Arbeitswirtschaft und Organisation IAO
                <br/>
                Nobelstraße 12
                <br/>
                70569 Stuttgart
                <br/>
                <br/>
                ist eine rechtlich nicht selbständige Einrichtung der Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V., welche diese Webseiten betreibt
                und inhaltlich betreut.
                <br/>
                <br/>
                <strong>Nutzungsrechte</strong>
                <br/>
                Copyright © by Fraunhofer-Gesellschaft e.V. Alle Rechte vorbehalten.
            </p>
            <p>
                <br/>
                Geschützte Marken und Namen, Bilder und Texte werden auf unseren Seiten in der Regel nicht als solche kenntlich gemacht. Das Fehlen einer solchen
                Kennzeichnung bedeutet jedoch nicht, dass es sich um einen freien Namen, ein freies Bild oder einen freien Text im Sinne des Markenzeichenrechts handelt.
            </p>
            <p>
                Die Urheberrechte bzw. sonstige Schutzrechte dieser Webseite liegen vollständig bei der Fraunhofer-Gesellschaft e.V. oder Projektpartnern des Projekts
                digital.me gem. der Webseite www.dime-project.eu.
                <br/>
                <br/>
            </p>
            <p>
                Ein Download oder Ausdruck von Inhalten ist ausschließlich für den persönlichen Gebrauch gestattet. Alle darüber hinaus gehenden Verwendungen, insbesondere
                die kommerzielle Nutzung und Verbreitung, sind grundsätzlich nicht gestattet und bedürfen der schriftlichen Genehmigung. Anfragen richten Sie bitte an
                folgende Adresse:
                <br/>
                <br/>
                Presse und Öffentlichkeitsarbeit
                <br/>
                Fraunhofer-Institut für Arbeitswirtschaft und Organisation IAO
                <br/>
                Nobelstr. 12
                <br/>
                70569 Stuttgart
                <br/>
                <a href="mailto:presse@iao.fraunhofer.de">presse@iao.fraunhofer.de</a>
                Diese E-Mail-Adresse ist vor Spambots geschützt! Zur Anzeige muss JavaScript eingeschaltet sein!
                <br/>
                <br/>
                Ein Download oder Ausdruck ist darüber hinaus lediglich zum Zweck der Berichterstattung über die Fraunhofer-Gesellschaft und Ihrer Institute nach Maßgabe
                untenstehender Nutzungsbedingungen gestattet:
                <br/>
                <br/>
                Grafische Veränderungen an Bildmotiven - außer zum Freistellen des Hauptmotivs - sind nicht gestattet. Es ist stets die Quellenangabe und Übersendung von
                zwei kostenlosen Belegexemplaren an die oben genannte Adresse erforderlich. Die Verwendung ist honorarfrei.
                <br/>
                <br/>
                <strong>Haftungshinweis</strong>
                <br/>
                Wir übernehmen keine Haftung für die Inhalte externer Links und kontrollieren auch nicht die verlinkten Seiten. Für den Inhalt der verlinkten Seiten sind
                ausschließlich deren Betreiber verantwortlich.
                <br/>
                <br/>
                Wir sind bemüht, das Webangebot stets aktuell und inhaltlich richtig sowie vollständig anzubieten. Dennoch ist das Auftreten von Fehlern nicht völlig
                auszuschließen. Die Fraunhofer-Gesellschaft e.V. übernimmt keine Haftung für die Aktualität, die inhaltliche Richtigkeit sowie für die Vollständigkeit der
                in ihrem Webangebot eingestellten Informationen. Dies bezieht sich auf eventuelle Schäden materieller oder ideeller Art Dritter, die durch die Nutzung
                dieses Webangebotes verursacht wurden.
            </p>

        </div>
    </div>

    <div id="usageTermsContainer" class="container contentContainer hidden">
        <div class="text_wrapper">
            <!--UsageTermes_EN------------------------------------------------------------------------->

            <p>
                <strong><h1>Usage Conditions</h1></strong>
            </p>
            <p>
                We offer this English translation of the German document for your convenience. The <span class="linkToUsageTerms_DE">German version</span>, however, is legally binding.
            </p>
            <h3>
                1. Registration and Privity of Contract
            </h3>
            <p>
                1.1 By registering to the di.me userware on the test-server xxx.dime2013.iao.fraunhofer.de (hereafter referred to as “di.me“) a licence agreement is closed
                between the registered person (hereafter referred to as “user“) and the Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V., Hansastraße
                27 c, 80686 München (hereafter referred to as “Fraunhofer“), acting by the Fraunhofer-Institut für Arbeitswirtschaft und Organisation IAO, Nobelstraße 12,
                70569 Stuttgart (hereafter referred to as “IAO“).
            </p>
            <p>
                1.2 By registering the user accepts these usage conditions.
            </p>
            <p>
                1.3 Only natural persons who have completed their 18th year and who have their residence in Germany may register and use di.me. All information provided at
                the registration must be correct and complete. The registration data may not be passed on and must be kept safe. The user may be excluded in case of breach
                of this clause.
            </p>
            <p>
                1.4 The registration and use is free of charge.
            </p>
            <h3>
                2. Description of the di.me userware and the operation of di.me servers
            </h3>
            <p>
                2.1 di.me is a software prototype that Fraunhofer provides to generate, evaluate, and demonstrate research results in the field of social networking. The
                user can use di.me as decentralized, privacy-friendly network. di.me can be operated on several servers. The user’s posted or uploaded data are stored only
                on the server that the user has registered to, or on servers other users have registered to these users are contacted. di.me has the following features and
                functionalities: di.me
            </p>
            <ul>
                <li>
                    is a decentrally organized social network that can be used to exchange and share profiles, messages, and data,
                </li>

                offers an own access (“personal service”) for each user that is technically separated from those of other users
                </li>
                <li>
                    is under the full control of the user who only decides who gets access to which information. No third party gets undesired access to a user’s data
                </li>
                <li>
                    can be accessed by the internet or the Android application “di.me mobile”,
                </li>
                <li>
                    manages data from different user devices on the server that the user has registered to
                </li>
                <li>
                    enables to connect to information from other social networks, e.g. messages, liveposts, profiles, or contacts of the user. Connected information from
                    other networks are updated regularly;
                </li>
                <li>
                    gives recommendations to data privacy and trust,
                </li>
                <li>
                    is an intelligent system that can analyse the situation of the user, e.g. to show which contacts of the user are nearby
                </li>
                <li>
                    runs decentralized on several servers, e.g. the server xxx.di.me2013.iao.fraunhofer.de provided by Fraunhofer
                </li>
                <li>
                    allows the user to select a server for registering, or to host an own server
                </li>
            </ul>
            <p>
                2.2 di.me is developed by the research project digital.me that is funded by the European Union (grant agreement FP7/2007- 2013, n° 257787).
                www.dime-project.eu. Partner of the project are: Fraunhofer IAO (DE) (Koordination), AMETIC (ES), Barcelona Digital (ES), CAS Software AG (DE), National
                University of Galway (IRL), Universität Siegen (DE), Telecom Italia (IT), Yellowmap GmbH (DE).
            </p>
            <p>
                2.3 Fraunhofer provides by its institute Fraunhofer IAO the server dime.hci.iao.fraunhofer.de as test-operation of the di.me. The test operation is planned
                for &lt;startdatumxxx&gt; until 31.12.2013.
            </p>
            <p>
                The objective of the test operation is to give access to the research results of the project digital.me. The project wants to collect feedback from test
                users in order to optimize the software. Further, the test operation shall demonstrate the development state of the open-source-software which shall be
                published at a later development state. For the test-operation, the software was provided with additional functions and services. The test operation is not
                intended to provide a service which can be used for other purposes than the above stated. The test operation has no commerical interests.
            </p>
            <p>
                Besides the server dime.hci.iao.fraunhofer.de, the project partners offer further test servers (see www.xxx). The users of all test-servers can get into
                contact via a user directory (the “di.me user directory”).
            </p>
            <p>
                <strong>3. Usage rights</strong>
            </p>
            <p>
                3.1 The user receives a simple right to use the provided state of di.me for the function as intended in the program code. This right is simple, limited in
                time to the duration of this usage contract, and can be revoked any time. There is no right to manipulate, distribute, transmit, or sublicence. Later,
                di.me shall be provided under the open-source-licence for the European Union (EUPL v.1.1).
            </p>
            <p>
                3.2 For the web page content, the Copyright © by Fraunhofer-Gesellschaft e.V. applies. All rights reserved. All copyright for this web site are owned in
                full by the Fraunhofer-Gesellschaft e.V. or by project partners of the project digital.me according to the web site www.dime-project.eu.
            </p>
            <p>
                3.3 Registered trademarks and proprietary names, and copyrighted text and images, are not generally indicated as such on our Web pages. But the absence of
                such indications in no way implies that these names, images or text belong to the public domain in the context of trademark or copyright law.
            </p>
            <p>
                3.4 Permission is granted to download or print material published on this site for personal use only. Its use for any other purpose, and in particular its
                commercial use or distribution, are strictly forbidden in the absence of prior written approval. Material may be downloaded or printed for use in
                connection with press reports on the activities of the Fraunhofer-Gesellschaft and its constituent institutes, on condition that the following terms are
                complied with.
            </p>
            <p>
                3.5 No alterations may be made to pictorial content, with the exception of framing modifications to emphasize the central motif. The source must be quoted
                and two free reference copies must be sent to the above-mentioned address. Such usage is free of charge.
            </p>
            <h3>
                4. Misuse
            </h3>
            <p>
                4.1 When using di.me the user is obliged to demonstrate a responsible use of the offered service. In particular, this means:
            </p>
            <ul>
                <li>
                    making no unauthorized access to data, or the IT infrastructure of Fraunhofer or its partners,
                </li>
                <li>
                    protection of data of users and the intellectual property rights and other personal rights of third parties, in particular of other users of the system
                    or users of systems connected to di.me,
                </li>
                <li>
                    Respecting the terms and conditions of external systems (e.g. social networks like LinkedIn or Facebook) which may be connected or accessed by di.me
                </li>
            </ul>


            <p>
                4.2 It is strictly forbidden to make deliberate or knowing use of the provided system for purposes that are likely to:
            </p>
            <ul>
                <li>
                    prejudice the interests of the Fraunhofer-Gesellschaft or its public image (e.g. distributing unsolicited mail (spam) or hacking into other IT systems),
                </li>
                <li>
                    compromise the security of the IT infrastructure, or
                </li>
                <li>
                    contravene the law.
                </li>
            </ul>
            <p>
                It is furthermore forbidden to distribute content of violating the prevailing laws on the confidentiality of personal data, intellectual property rights,
                or criminal offences, publish statements or images of an insulting, slanderous, anti-constitutional, racist, sexist, violent, or pornographic nature.
            </p>
            <p>
                4.3 The misuse of di.me or the test-server dime.hci.iao.fraunhofer.de will be prosecuted.
            </p>
            <p>
                4.4 Any indication of misuse has to be reported to Fraunhofer immediately.
            </p>
            <p>
                4.5 In the case of substantiated suspicion for misuse or manipulation, Fraunhofer may exclude the user from the service and claim compensatory damages.
            </p>
            <h3>
                5. Withdrawal, termination, and Change
            </h3>
            <p>
                5.1 Both contractors may end the participation at any time. The user can deactivate his or her access to the test-system any time and have his or her data
                deleted by sending an email with his user ID by the website of the service.
            </p>
            <p>
                5.2 Fraunhofer may temporarily pause or finally terminate the provision of the di.me prototype or the test server, or to amend or change the service.
            </p>
            <p>
                5.3 Fraunhofer may change or amend these usage conditions in the interest of easy and technically secure conduct or to prevent misuse. Substantial changes
                will be announced in advance by email or by the system. Changes are considered as accepted if the user does not withdraw from the participation within one
                month after the announcement or if the user uses dime after this period.
            </p>
            <h3>
                6. Warranty, Liability
            </h3>
            <p>
                6.1 The user knows that the development and operation of di.me and of the optional Android application “di.me mobile client” is a research project which
                provides prototypical systems but no products ready for market. The software is only provided in the currently available, non-tested development state,
                free of charge as it is.
            </p>
            <p>
                Because of that, Fraunhofer excludes any warranty for merchantable quality or deficiency in title of di.me, its operation, and the optional Android application
                “di.me mobile” that can be installed on the user’s personal devices.
            </p>
            <p>
                In particular, any warranty is excluded for
            </p>
            <ul>
                <li>
                    that the test-server or di.me or the Android application “di.me mobile” is available any time and permanently, and
                </li>
                <li>
                    that the installation and operation of the di.me Android app has no negative effects on other software of the Android device or on the data saved on it
                </li>
            </ul>
            <p>
                6.2 Fraunhofer is liable only in the cases of intention, gross negligence, damages caused to body and life, and under the regulation of the
                Produkthaftungsgesetz (product liability law). In particular this applies for damages caused by the use of di.me or the Android App “di.me mobile” and/or
                the misuse of them, also by third parties. Any other liability is excluded.
            </p>
            <p>
                In particular, Fraunhofer does not assume any liability for
            </p>

            <ul>
                <li>
                    the content of registered users. Only the registered users are responsible for content that is delivered by them to the system or published to third
                    parties. Fraunhofer does not control or influence the activities of users or the content provided by users and is not responsible for it. Nevertheless,
                    Fraunhofer reserves the right to delete content and to prohibit users from further use of the service at our own discretion, especially in cases where
                    posted content contravenes the law or is deemed incompatible with the objectives of the Fraunhofer-Gesellschaft.
                </li>
                <li>
                    the content for the contents of linked external websites which are under only under the responsibility of their providers. They are also not controlled
                    by Fraunhofer.
                </li>
                <li>
                    the accuracy, completeness or topicality of any of the content presented
                </li>
            </ul>
            <h3>
                7. Final provision
            </h3>
            <p>
                7.1 Should a provision of this contract prove to be wholly or partly void or should the contract have omissions, this shall not affect the validity of the
                remaining provisions. The invalid or impracticable provision shall be replaced by an appropriate provision that comes as close as possible in terms of
                economic impact to the invalid or unenforceable term.
            </p>
            <p>
                7.2 The contractual relations are exclusively subject to German laws to the exclusion of UN Purchase Law.
            </p>
            <p>
                7.3 For proceedings against Fraunhofer, Fraunhofer’s residence is the place of jurisdiction.
            </p>
            <p>
                7.4 These usage conditions are valid and came into effect on xxx.
            </p>

        </div>
    </div>

    <div id="usageTermsContainer_DE" class="container contentContainer hidden">
        <div class="text_wrapper">
            <!--UsageTermes_DE------------------------------------------------------------------------->
            <p>
                <strong><h1>Nutzungsbedingungen</h1></strong>
            </p>
            <h3>
                1. Registrierung und Vertragsbeziehung
            </h3>
            <p>
                1.1 Mit Registrierung beim Test-Server für die di.me userware xxxdime2013.iao.fraunhofer.de (Server und di.me userware nachstehend „di.me“) kommt ein
                Nutzungsvertrag zwischen der angemeldeten Person (nachstehend „Nutzer“) und der Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.,
                Hansastraße 27 c, 80686 München (nachstehend „Fraunhofer“), handelnd durch das Fraunhofer-Institut für Arbeitswirtschaft und Organisation IAO, Nobelstraße
                12, 70569 Stuttgart, (nachstehend „IAO“), zustande.
            </p>
            <p>
                1.2 Mit der Registrierung erkennt der Nutzer diese Bedingungen an.
            </p>
            <p>
                1.3 Anmelden und nutzen dürfen di.me nur natürliche Personen, die das 18. Lebensjahr vollendet und ihren Wohnsitz in Deutschland haben. Alle bei der
                Registrierung gemachten Angaben müssen, richtig und vollständig sein. Die Registrierungsdaten dürfen vom Nutzer nicht weitergegeben werden und sind sicher
                zu verwahren. Bei einem Verstoß hiergegen kann der betreffende Nutzer ausgeschlossen werden.
            </p>
            <p>
                1.4 Die Teilnahme ist kostenlos.
            </p>
            <h3>
                2. Beschreibung der di.me userware und des Betriebs von di.me Servern
            </h3>
            <p>
                2.1 di.me ist ein Software-Prototyp, mit dem Fraunhofer Forschungsergebnisse aus dem Bereich Sozialer Netzwerke generieren, bewerten und der Öffentlichkeit
                bereitstellen möchte. Der Nutzer kann di.me als dezentrales, datenschutzfreundliches Netzwerk benutzen. di.me kann auf verschiedenen Servern betrieben
                werden, wobei gepostete Daten des Nutzers grds. nur auf dem Server gespeichert und verarbeitet werden, an dem er sich angemeldet hat, bzw. bei einer
                Kontaktaufnahme mit anderen Nutzern auf den Servern, an denen diese sich angemeldet haben. Die Userware weist folgende Eigenschaften und Funktionen auf:
                di.me
            </p>
            <ul>
                <li>
                    ist ein dezentral organisiertes soziales Netzwerk, mit dem Nutzer Profile, Nachrichten, und Daten austauschen können,
                </li>
                <p>
                    bietet jedem Nutzer einen eigenen Zugang (“persönlicher Dienst“), der von dem anderer Nutzer technisch getrennt ist,
                    </li>
                <li>
                    ist unter voller Kontrolle des Nutzers, der alleine entscheidet, wer Zugriff auf welche Informationen bekommt. Kein Dritter erhält ungewollt Zugriff auf
                    die Daten der einzelner Nutzer,
                </li>
                <li>
                    kann über das Internet und mit der Android-Applikation „di.me mobile“ bedient werden,
                </li>
                <li>
                    verwaltet auf dem Server, an dem sich der Nutzer registriert hat, Daten des Nutzers von dessen verschiedenen Endgeräten,
                </li>
                <li>
                    ermöglicht es, Informationen aus anderen sozialen Netzwerken zu verknüpfen. Solche Informationen sind z.B. Nachrichten (messages, liveposts), Profile und
                    Kontakte des Nutzers. Freigegebene Informationen aus verknüpften Netzwerken werden regelmäßig aktualisiert;
                </li>
                <li>
                    gibt Empfehlungen zu Datenschutz und Vertrauenswürdigkeit,
                </li>
                <li>
                    ist ein intelligentes System, das die örtliche Situation auswerten kann, z.B. um anzuzeigen, welche Kontakte des Benutzers in der Nähe sind,
                </li>
                <li>
                    ist ein verteiltes System, das auf mehreren Servern betrieben wird, z.B. von Fraunhofer auf xxx.dime2013.iao.fraunhofer.de,
                </li>
                <li>
                    ermöglicht es Nutzern, einen Server für den eigenen Zugang auszusuchen, oder selbst einen Server zu betreiben.

            </ul>
            2.2 di.me wird im Forschungsprojekts digital.me entwickelt, das von der Europäischen Union gefördert wird (Förderkennzeichen FP7/2007- 2013, n° 257787).    <a href="http://www.dime-project.eu">www.dime-project.eu</a>. Partner des Projekts sind: Fraunhofer IAO (DE) (Koordination), AMETIC (ES), Barcelona Digital
            (ES), CAS Software AG (DE), National University of Galway (IRL), Universität Siegen (DE), Telecom Italia (IT), Yellowmap GmbH (DE).

            2.3 Fraunhofer betreibt über ihr Institut IAO den Server dime2013.iao.fraunhofer.de als Test-Betrieb für die di.me Userware. Der Test-Betrieb ist für den
            Zeitraum 01.06.2013 bis 31.12.2013 geplant.
            </p>
            <p>
                Ziel des Test-Betriebs ist, Interessierten die Möglichkeit geben, die Forschungsergebnisse des Projekts digital.me kennenzulernen. Das Projekt möchte damit
                Rückmeldungen von Benutzern zu gewinnen, um die Software zu verbessern und weiterzuentwickeln. Außerdem demonstriert der Test-Betrieb den Entwicklungsstand
                der Open-Source Software, die später in einem reiferen Entwicklungsstand publiziert werden soll. Für den Test-Betrieb wurde die Open-Source-Software mit
                zusätzlichen Funktionen und Diensten ausgestattet. Der Test-Betrieb hat nicht das Ziel, einen über die Testzwecke hinaus nutzbaren Dienst anzubieten. Mit
                dem Test-Betrieb sind keinerlei kommerziellen Interessen verbunden.
            </p>
            <p>
                Neben dem Server xxx.dime2013.iao.fraunhofer.de bieten die Partner des Forschungsprojekts weitere Test-Server an (vgl. <a href="http://dimetrials.bdigital.org:8080/dime/">http://dimetrials.bdigital.org:8080/dime/</a>). Die Nutzer aller Test-Server
                können über ein Nutzerverzeichnis (das „di.me user directory“) miteinander in Kontakt treten.
            </p>
            <p>
                <strong>3. Nutzungsrechte</strong>
            </p>
            <p>
                3.1 Der Nutzer erhält an dem jeweils betriebenen Stand von di.me ein jederzeit widerrufliches, einfaches, zeitlich auf die Laufzeit dieses Nutzungsvertrags
                beschränktes, Nutzungsrecht, die Userware im Rahmen der vorgesehenen Funktionen im Programmcode zu nutzen. Ein Recht zur Bearbeitung, Übertragung oder
                Unterlizenzierung besteht nicht. di.me soll zu einem späteren Zeitpunkt unter der    <a href="http://joinup.ec.europa.eu/system/files/DE/EUPL%20v.1.1%20-%20Lizenz.pdf">Open-Source-Lizenz für die Europäische Union V.1.1</a> zur Verfügung
                gestellt werden.
            </p>
            <p>
                3.2 Bezüglich der Webseiteninhalte gilt das Copyright © by Fraunhofer-Gesellschaft e.V. Alle Rechte vorbehalten. Die Urheberrechte bzw. sonstige
                Schutzrechte dieser Webseite liegen vollständig bei Fraunhofer oder Projektpartnern des di.me Projekts.
            </p>
            <p>
                3.3 Geschützte Marken und Namen, Bilder und Texte werden auf den Seiten von Fraunhofer in der Regel nicht als solche kenntlich gemacht. Das Fehlen einer
                solchen Kennzeichnung bedeutet jedoch nicht, dass es sich um einen freien Namen, ein freies Bild oder einen freien Text im Sinne des Markenzeichenrechts
                handelt.
            </p>
            <p>
                3.4 Ein Download oder Ausdruck von Inhalten ist ausschließlich für den persönlichen Gebrauch gestattet. Alle darüber hinaus gehenden Verwendungen,
                insbesondere die kommerzielle Nutzung und Verbreitung, sind grundsätzlich nicht gestattet und bedürfen der schriftlichen Genehmigung. Ein Download oder
                Ausdruck ist darüber hinaus lediglich zum Zweck der Berichterstattung über Fraunhofer und Ihrer Institute nach Maßgabe untenstehender Nutzungsbedingungen
                gestattet.
            </p>
            <p>
                3.5 Grafische Veränderungen an Bildmotiven - außer zum Freistellen des Hauptmotivs - sind nicht gestattet. Es ist stets die Quellenangabe und Übersendung
                von zwei kostenlosen Belegexemplaren an die oben genannte Adresse erforderlich. Die Verwendung ist honorarfrei.
            </p>
            <h3>
                4. Missbrauch
            </h3>
            <p>
                4.1 Der Nutzer von di.me ist zu einem verantwortungsbewussten Umgang mit dem angebotenen Dienst verpflichtet. Dies bedeutet insbesondere:
            </p>
            <ul>
                <li>
                    Unterlassung eines unberechtigten Eingriffs in Daten oder die IT-Infrastruktur von Fraunhofer oder deren Partner,
                </li>
                <li>
                    Schutz der Daten der Nutzer sowie der Urheber- und sonstigen Persönlichkeitsrechte aller Dritter, wie anderer Nutzer des Systems oder Nutzern weiterer
                    Systeme, die di.me verbunden sind,
                </li>
                <li>
                    Befolgung der Nutzungsbedingungen externer Systeme (z.B. sozialer Netzwerke wie LinkedIn oder Facebook), auf die über di.me ggf. zugegriffen werden kann.
                </li>
            </ul>
            4.2 Unzulässig ist jede Nutzung von di.me, die geeignet ist,
            <ul>
                <li>
                    den Interessen von Fraunhofer oder deren Ansehen in der Öffentlichkeit zu schaden (z.B. durch das Versenden von Spam-Mails oder das Hacken fremder
                    IT-Systeme), die Sicherheit der IT-Infrastruktur zu beeinträchtigen oder
                </li>
                <li>
                    gegen geltende Rechtsvorschriften zu verstoßen.
                </li>
                <li>
                    Unzulässig ist es insbesondere auch,
                </li>
                <li>
                    Inhalte zu verbreiten, die gegen Persönlichkeitsrechtliche, urheberrechtliche oder strafrechtliche Bestimmungen verstoßen,
                </li>
                <li>
                    beleidigende, verleumderische, verfassungsfeindliche, rassistische, sexistische, gewaltverherrlichende oder pornografische Äußerungen oder Abbildungen zu
                    verbreiten,
                </li>
                <li>
                    unerwünschte Werbekommunikation (z.B. Spam) zu verbreiten,
                </li>
            </ul>
            4.3 Die missbräuchliche Verwendung von di.me wird strafrechtlich verfolgt.
            </p>
            <p>
                4.4 Anhaltspunkte eines Missbrauchs sind Fraunhofer unverzüglich zu melden.
            </p>
            <p>
                4.5 Bei begründetem Verdacht auf Missbrauch oder Manipulation zum Schaden von di.me behält sich Fraunhofer vor, den Nutzer fristlos von der Teilnahme
                auszuschließen sowie Schadensersatz zu verlangen.
            </p>
            <h3>
                5. Kündigung, Beendigung und Änderung
            </h3>
            <p>
                5.1 Beide Vertragspartner können die Teilnahme jederzeit ohne Einhaltung einer Frist beenden. Der Nutzer kann seinen Zugang zu dem Test-System jederzeit
                deaktivieren und seine Daten löschen lassen indem er Fraunhofer eine E-Mail mit seiner Nutzerkennung über die entsprechende Webseite des Dienstes zusendet.
            </p>
            <p>
                5.2 Fraunhofer behält sich vor, den Betrieb des Prototypen von di.me endgültig oder vorübergehend einzustellen, aufgrund technischer Weiterentwicklungen zu
                ergänzen oder zu verändern.
            </p>
            <p>
                5.3 Fraunhofer behält sich ferner vor, diese Nutzungsbedingungen zu ändern oder zu ergänzen, soweit dies im Interesse einer einfachen und technisch
                sicheren Abwicklung oder zur Verhinderung von Missbräuchen erforderlich ist. Wesentliche Änderungen werden den Nutzern vorab per E-Mail oder über das
                System mitgeteilt. Eine Änderung gilt als genehmigt, wenn der Nutzer nicht innerhalb eines Monats nach Zugang der Mitteilung kündigt oder wenn er nach
                Ablauf dieser Frist di.me weiter nutzt.
            </p>
            <h3>
                6. Gewährleistung, Haftung
            </h3>
            <p>
                6.1 Dem Nutzer ist bekannt, dass es sich bei der Entwicklung und dem Betrieb von di.me und der optional verwendbaren Android Applikation um ein
                Forschungsprojekt handelt, das prototypischen Charakter hat und keine marktreifen Produkte zur Verfügung stellt. Software wird lediglich in dem jeweils bei
                Fraunhofer vorliegenden und nicht ausgetesteten Entwicklungsstand wie sie ist kostenlos zur Verfügung gestellt („as is“).
            </p>
            <p>
                Fraunhofer schließt daher jede Gewährleistung für Rechts- und Sachmängel von di.me und jede Gewährleistung für deren Betrieb als auch der optional zu
                beziehenden und auf eigenen Geräten des Nutzers zu installierenden Android Applikation „di.me mobile“ aus.
            </p>
            <p>
                Insbesondere ist jede Gewährleistung dafür ausgeschlossen, dass
            </p>
            <ul>
                <li>
                    der di.me und die Android App „dime mobile“ jederzeit und dauerhaft verfügbar sind, und dass
                </li>
                <li>
                    die Installation und der Betrieb der Android App „di.me mobile“ keine negativen Auswirkungen auf sonstige Software des Android Geräts oder der darauf
                    gespeicherten Daten haben.
                </li>
            </ul>

            <p>
                6.2 Fraunhofer haftet nur für Vorsatz, grobe Fahrlässigkeit, Schäden an Körper und Leben sowie nach den Vorschriften des Produkthaftungsgesetzes. Dies gilt
                insbesondere auch für Schäden, die durch die Nutzung von di.me bzw. der Android Applikation „di.me mobile“ und/oder deren Missbrauch, auch durch Dritte,
                entstehen. Eine darüber hinausgehende Haftung ist ausgeschlossen.
            </p>
            <p>
                Insbesondere übernimmt Fraunhofer im Rahmen von Ziffer 6.3 keine Haftung für
            </p>

            <ul>
                <li>
                    die Inhalte der registrierten Nutzer. Für Inhalte, die von registrierten Nutzern dem System übermittelt oder an Dritte veröffentlicht werden, sind
                    ausschließlich die jeweiligen Nutzer verantwortlich. Der Test-Server dime2013.iao.fraunhofer.de unterliegt grundsätzlich keiner inhaltlichen Kontrolle
                    durch Fraunhofer. Fraunhofer kontrolliert oder beeinflusst die Aktivitäten und insbesondere angebotene Inhalte der Nutzer nicht und ist dafür nicht
                    verantwortlich. Gleichwohl behält sich Fraunhofer vor, nach eigenem Ermessen Informationen zu löschen und Nutzer von der weiteren Nutzung auszuschließen,
                    insbesondere wenn Einträge strafrechtlich relevante Tatbestände erfüllen oder mit den Zielen von Fraunhofer nicht zu vereinbaren sind.
                </li>
                <li>
                    die Inhalte der extern verlinkten Webseiten, für die ausschließlich deren Betreiber verantwortlich sind; diese werden auch nicht von Fraunhofer
                    kontrolliert.
                </li>
                <li>
                    das Auftreten von Fehlern, die Aktualität, die inhaltliche Richtigkeit sowie für die Vollständigkeit der auf den Webseiten eingestellten Informationen.
                </li>
            </ul>
            <h3>
                7. Schlussbestimmungen
            </h3>
            <p>
                7.1 Sollte eine Bestimmung dieses Vertrags ganz oder teilweise unwirksam oder undurchführbar sein oder werden, berührt dies nicht die Wirksamkeit der
                anderen Bestimmungen.
            </p>
            <p>
                Anstelle der unwirksamen oder undurchführbaren Bestimmung soll eine solche Bestimmung vereinbart werden, die dem wirtschaftlichen Zweck der unwirksamen
                oder undurchführbaren Bestimmung möglichst nahe kommt. Entsprechendes gilt im Fall von Lücken in diesem Vertrag.
            </p>
            <p>
                7.2 Auf das gesamte Rechtsverhältnis aus diesem Vertrag und seiner Durchführung findet ausschließlich deutsches Recht unter Ausschluss des UN-Kaufrechts
                Anwendung.
            </p>
            <p>
                7.3 Für Klagen gegen Fraunhofer ist Gerichtsstand an deren Sitz.
            </p>
            <p>
                7.4 Die Nutzungsbedingungen sind aktuell gültig und datieren vom 01.06.2013.
                <br/>
                <br/>
                <br/>
            </p>

        </div>
    </div>

    <div id="privacyPolicyContainer" class="container contentContainer hidden">
        <div class="text_wrapper">
            <!--PrivacyPolicy_EN------------------------------------------------------------------------>

            <p>
                <strong><h1>Data privacy policy</h1></strong>
            </p>
            <p>
                We offer this English translation of the German document for your convenience. The German versionxxxLink, however, is legally binding.
            </p>
            <p>
                The Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V. (Fraunhofer-Gesellschaft) takes the protection of your personal data very seriously. When we process the personal data that is collected during your visits to our website xxxwww.dime2013.iao.fraunhofer.de, we always observe the rules laid down in the applicable data protection laws. Your data will not be disclosed publicly by us, nor transferred to any third parties without your consent. In the following sections, we explain what types of data we record when you visit our Web sites, and precisely how they are used:
                <br/>
                <br/>
            </p>
            <p>
                <strong>Table of Content</strong>
            </p>
            <p>
                <a href="#_Toc353982704">1. Recording and processing of data in connection with access over the Internet 1</a>
            </p>
            <p>
                <a href="#_Toc353982705">2. Recording and processing of data when using di.me 2</a>
            </p>
            <p>
                <a href="#_Toc353982706">3. Use and transfer of personal data 2</a>
            </p>
            <p>
                <a href="#_Toc353982707">4. Consent to use data in other contexts 3</a>
            </p>
            <p>
                <a href="#_Toc353982708">5. Cookies 3</a>
            </p>
            <p>
                <a href="#_Toc353982709">6. Security 3</a>
            </p>
            <p>
                <a href="#_Toc353982710">7. Links to Web sites operated by other providers 3</a>
            </p>
            <p>
                <a href="#_Toc353982711">8. Right to information and contact data 3</a>
            </p>
            <p>
                <a href="#_Toc353982712">9. Acceptance, validity and modification of data protection conditions 4</a>
            </p>
            <h3>
                <div id="_Toc353982704">1. Recording and processing of data in connection with access over the Internet</div>
            </h3>
            <p>
                When you visit our Web site, our Web server makes a temporary record of each access and stores it in a log file. The following data are recorded, and
                stored until an automatic deletion date:
            </p>
            <ul>
                <li>
                    IP address of the requesting processor
                </li>
                <li>
                    Date and time of access
                </li>
                <li>
                    Name and URL of the downloaded file
                </li>
                <li>
                    Volume of data transmitted
                </li>
                <li>
                    Indication whether download was successful
                </li>
                <li>
                    Data identifying the browser software and operating system
                </li>
                <li>
                    Web site from which our site was accessed
                </li>

                <li>
                    Name of your Internet service provider
                </li>
            </ul>
            <p>
                The purpose of recording these data is to allow use of the Web site (connection setup), for system security, for technical administration of the network
                infrastructure and in order to optimize our Internet service. The IP address is only evaluated in the event of fraudulent access to the network
                infrastructure of the Fraunhofer-Gesellschaft. Our servers are located in Stuttgart, Germany.
            </p>
            <h3>
                <div id="_Toc353982705">2. Recording and processing of data when using di.me</div>
            </h3>
            <ul>
                <li>
                    After registration to the test-server xxxdime2013.iao.fraunhofer.de , personal data are processed by the system with the only purpose to enable it’s
                    function as described in clause 2 of the usage conditionsxxxLink. The following data are processed:
                </li>
                <li>
                    Your email-address, user ID and password for the registration. By the E-Mail-address we also can contact you in case of dysfunction of the system or to
                    send you invitations to a feedback questionnaire for scientific evaluation.
                </li>
                <li>
                    A mini-profile for the “di.me user directory” which is visible on the other servers in the di.me decentralized network and which allows other users on
                    these servers to contact you in di.me.
                </li>
                <li>
                    Data that you provide to the system, e.g. names, pseudonyms, uploaded files or fotos, or contacts from an Android mobile phone. These data are first
                    stored on the server xxxdime2013.iao.fraunhofer.de, but the user can also publish it to other servers by communicating to other users.
                </li>
                <li>
                    Data from external systems that you connect to the di.me userware, e.g. profile information and messages from Twitter or LinkedIn in order to show them
                    in di.me. If personal data of third persons are connected, the user ensures that they agree to that.
                </li>
                <li>
                    Location information from an Android mobile phone if you install the Android application “dime mobile“ and activate the function “Store Android sensor data on your server“  in order to get
                    system recommendations to your situation or your place.
                </li>
            </ul>
            <p>
                You can deactivate your account to the test-system at any time and let your data be deleted by sending us an e-mail with your user ID via the website.
            </p>
            <p>
                Apart from the cases explained in clause 1 and 2, no personal data are processed, except that you explicitly agree to further processing. According to the
                explanation on web analysis (see below), pseudonymous use profiles can be recorded.
            </p>
            <h3>
                <div id="_Toc353982706">3. </div>
                Use and transfer of personal data
            </h3>
            <p>
                All use of your personal data is confined to the purposes stated above, and is only undertaken to the extent necessary for these purposes. Data will not be
                transferred to third parties.
            </p>
            <p>
                Personal data will not be transferred to government bodies or public authorities except in order to comply with mandatory national legislation or if the
                transfer of such data should be necessary in order to take legal action in cases of fraudulent access to our network infrastructure. No transfer for any
                other purpose will be done.
            </p>
            <h3>
                <div id="_Toc353982707">4. Consent to use data in other contexts</div>
            </h3>
            <p>
                The use of certain services on our Web site, such as newsletters or discussion forums, requires prior registration and involves more substantial processing
                of personal data, such as longer-term storage of e-mail addresses, user IDs and passwords. We only use such data insofar as they have been sent to us by
                you in person and you have given us your prior consent for this use.
            </p>
            <h3>
                <div id="_Toc353982708">5. Cookies</div>
            </h3>
            <p>
                We don’t normally use cookies on our Web sites, but in certain exceptional cases we may use cookies which place technical session-control data in your
                browser’s memory. These data contain no references of a personal nature, and are automatically erased when you close your browser.
                <br/>
                <br/>
                If, exceptionally, one of our applications requires the storage of personal data in a cookie, for instance a user ID, you will be asked to give your
                consent beforehand.
                <br/>
                <br/>
                Of course, it is perfectly possible to consult our Web sites without the use of cookies. Please note, however, that most browsers are programmed to accept
                cookies in their default configuration. You can prevent this by changing the appropriate setting in the browser options. If you set the browser to refuse
                all cookies, this may restrict your use of certain functions on our Web site.
            </p>
            <h3>
                <div id="_Toc353982709">6. Security</div>
            </h3>
            <p>
                The Fraunhofer-Gesellschaft implements technical and organizational security measures to safeguard stored personal data against inadvertent or deliberate
                manipulation, loss or destruction and against access by unauthorized persons. Our security measures are continuously improved in line with technological
                progress.
            </p>
            <h3>
                <div id="_Toc353982710">7. Links to Web sites operated by other providers</div>
            </h3>
            <p>
                Our Web pages may contain links to other providers’ Web pages. We would like to point out that this statement of data protection conditions applies
                exclusively to the Web pages managed by the Fraunhofer-Gesellschaft. We have no way of influencing the practices of other providers with respect to data
                protection, nor do we carry out any checks to ensure that they conform with the relevant legislation.
            </p>
            <h3>
                <div id="_Toc353982711">8.
                    Right to information and contact data</div>
            </h3>
            <p>
                You have a legal right to inspect any stored data concerning your person, and also the right to demand their correction or deletion, and to withdraw your
                consent for their further use.
                <br/>
                <br/>
                In some cases, if you are a registered user of certain services provided by the Fraunhofer-Gesellschaft, we offer you the possibility of inspecting these
                data online, and even of deleting or modifying the data yourself.
                <br/>
                <br/>
                If you wish to obtain information on your personal data, or want us to correct or erase such data, or if you have any other questions concerning the use of
                personal data held by us, please contact
            </p>
            <p>
                <strong>Dr. Niklas Speer</strong>
            </p>
            <p>
            <p>
                Datenschutzbeauftragter
                <br/>
                Zentrale der Fraunhofer-Gesellschaft
                <br/>
                Hansastraße 27 c
                <br/>
                80686 München, Deutschland
                <br/>
                niklas.speer@zv.fraunhofer.de
                <br/>
                Telefon +49 89 1205-2015
            </p>
            </p>
            <p>

                <br/>
                Telefon +49 89 1205-2015
            </p>
            <h3>
                <div id="_Toc353982712">9. Acceptance, validity and modification of data protection conditions</div>
            </h3>
            <p>
                By using our Web site, you implicitly agree to accept the use of your personal data as specified above. This present statement of data protection
                conditions came into effect on 1.06.2013.
                <br/>
                <br/>
                As our Web site evolves, and new technologies come into use, it may become necessary to amend the statement of data protection conditions. The
                Fraunhofer-Gesellschaft reserves the right to modify its data protection conditions at any time, with effect as of a future date. We recommend that you
                re-read the latest version from time to time.
            </p>

        </div>
    </div>


    <div id="privacyPolicyContainer_DE" class="container contentContainer hidden">
        <div class="text_wrapper">
            <!--PrivacyPolicy_DE------------------------------------------------------------------------>

            <p>
                <strong><h1>Datenschutzerklärung</h1></strong>

            </p>
            <p>
                Die Fraunhofer-Gesellschaft zur Förderung der angewandten
                Forschung e.V. (Fraunhofer-Gesellschaft) nimmt den Schutz Ihrer
                personenbezogenen Daten sehr ernst. Wir verarbeiten personenbezogene Daten, die
                beim Besuch unserer Webseiten unter xxx.dime2013.iao.fraunhofer.de erhoben
                werden, unter Beachtung der geltenden datenschutzrechtlichen Bestimmungen. Ihre
                Daten werden von uns weder veröffentlicht, noch unberechtigt an Dritte
                weitergegeben. Im Folgenden erläutern wir, welche Daten wir während Ihres
                Besuches auf unseren Webseiten erfassen und wie genau diese verwendet werden:
            </p>
            <p>
                <strong>Inhalt</strong>
            </p>
            <p>
                <a href="#_Toc354567359">1. Datenerhebung und -verarbeitung bei Zugriffen aus dem Internet</a>
            </p>
            <p>
                <a href="#_Toc354567360">2. Datenerhebung und -verarbeitung bei Nutzung von di.me</a>
            </p>
            <p>
                <a href="#_Toc354567361">3. Nutzung und Weitergabe personenbezogener Daten</a>
            </p>
            <p>
                <a href="#_Toc354567362">4. Einwilligung in weitergehende Nutzungen</a>
            </p>
            <p>
                <a href="#_Toc354567363">5. Cookies</a>
            </p>
            <p>
                <a href="#_Toc354567364">6. Sicherheit</a>
            </p>
            <p>
                <a href="#_Toc354567365">7. Links zu Webseiten anderer Anbieter</a>
            </p>
            <p>
                <a href="#_Toc354567366">8. Auskunftsrecht und Kontaktdaten</a>
            </p>
            <p>
                <a href="#_Toc354567367">9. Einbeziehung, Gültigkeit und Aktualität der Datenschutzerklärung</a>
            </p>
            <h3>
                <div id="_Toc354567359">1. Datenerhebung und -verarbeitung bei Zugriffen aus dem Internet</div>
            </h3>
            <p>
                Wenn
                Sie unsere Webseiten besuchen, speichern unsere Webserver temporär jeden Zugriff
                in einer Protokolldatei. Folgende Daten werden erfasst und bis zur automatisierten
                Löschung gespeichert:
            </p>
            <ul>
                <li>
                    IP-Adresse des anfragenden Rechners
                </li>
                <li>
                    Datum und Uhrzeit des Zugriffs
                </li>
                <p>
                    Name und URL der abgerufenen Datei
                    </li>
                <li>
                    Übertragene Datenmenge
                </li>
                <li>
                    Meldung, ob der Abruf erfolgreich war
                </li>
                <li>
                    Erkennungsdaten des verwendeten Browser- und Betriebssystems
                </li>
                <li>
                    Webseite, von der aus der Zugriff erfolgt
                </li>
                <li>
                    Name Ihres Internet-Zugangs-Providers
                </li>
            </ul>
            Die
            Verarbeitung dieser Daten erfolgt zum Zweck, die Nutzung der Webseite zu ermöglichen
            (Verbindungsaufbau), der Systemsicherheit, der technischen Administration der
            Netzinfrastruktur sowie zur Optimierung des Internetangebotes. Die IP-Adresse
            wird nur bei Angriffen auf die Netzinfrastruktur der Fraunhofer-Gesellschaft
            ausgewertet. Unsere Server befinden sich in Stuttgart, Deutschland.
            </p>
            <h3>
                <div id="_Toc354567360">2. Datenerhebung und -verarbeitung bei Nutzung von di.me</div>
            </h3>
            <p>
                Folgende personenbezogene
                Daten werden nach vorheriger Registrierung auf dem Test-Server xxxdime2013.iao.fraunhofer.de
                vom System zum alleinigen Zweck verarbeitet, dessen Funktion wie in Ziffer 2
                der NutzungsbedingungenXXXlink beschrieben zu ermöglichen
            </p>
            <ul>
                <li>
                    Ihre
                    E-Mail-Adresse, Nutzerkennung und Passwort für die Registrierung. Über die
                    E-Mail-Adresse kann zudem eine Kontaktaufnahme im Falle von Störungen des
                    Systems erfolgen, oder zur Versendung von Einladungen, am Feedback-Fragebogen zur
                    wissenschaftlichen Evaluation teilzunehmen.
                </li>
                <li>
                    Ein Mini-Profil
                    für das Nutzerverzeichnis „DimeUserDirectory“, welches auf den
                    Partner-Servern einsehbar ist und eine Kontaktaufnahme von Nutzern anderer
                    Server ermöglicht.
                </li>
                <li>
                    Daten, die Sie an
                    das System übermitteln, z.B. Namen, Pseudonyme, hochgeladene Dateien oder Fotos oder Adressbücher eines
                    Android-Mobiltelefons. Diese liegen zunächst auf dem Server, an dem sich der
                    Nutzer registriert hat, können auf Wunsch des Nutzers aber auch anderen Servern
                    freigegeben werden, wenn mit deren Nutzern kommuniziert wird.
                </li>
                <li>
                    Daten von
                    externen Systemen, mit denen Sie die di.me userware verbinden, z.B.
                    Profilinformationen und Nachrichten von Twitter oder LinkedIn, um diese Daten
                    in aggregierter Form in di.me anzuzeigen. Sofern personenbezogene Daten Dritter
                    verbunden werden sollen, stellt der Nutzer selbst sicher, dass diese damit verstanden
                    sind.
                </li>
                <li>
                    Ortsinformationen
                    Ihres Android Mobiltelefons, sofern die Android-App „di.me mobile“ installiert und die Funktion „Store Android sensor data on your server“ aktiviert wird, um Systemhinweise zur Ihrer Situation oder Ihrem Ort zu erhalten.
                </li>
            </ul>
            <p>
                Sie können Ihren Zugang zu dem angebotenen Test-System jederzeit deaktivieren und Ihre Daten löschen lassen indem Sie uns eine E-Mail mit Ihrer Nutzerkennung über die entsprechende Webseite des Dienstes zusenden.
            </p>

            Über
            die in Ziffern 1 und 2 genannten Fälle hinaus werden personenbezogene Daten
            nicht verarbeitet, es sei denn, Sie willigen ausdrücklich in eine weitergehende
            Verarbeitung ein. Pseudonyme Nutzungsprofile können gem. den Ausführungen
            zur Web-Analyse erstellt werden (s.u.).
            </p>
            <h3>
                <div id="_Toc354567361">3. Nutzung und Weitergabe personenbezogener Daten</div>
            </h3>
            <p>
                Jegliche
                Nutzung Ihrer personenbezogenen Daten erfolgt nur zu den genannten Zwecken und
                in dem zur Erreichung dieser Zwecke erforderlichen Umfang. Eine Weitergabe
                an Dritte erfolgt nicht.
            </p>
            <p>
                Übermittlungen
                personenbezogener Daten an staatliche Einrichtungen und Behörden erfolgen nur
                im Rahmen zwingender nationaler Rechtsvorschriften oder wenn die Weitergabe im
                Fall von Angriffen auf unsere Netzinfrastruktur zur Rechts- oder Strafverfolgung
                erforderlich ist. Eine Weitergabe zu anderen Zwecken findet nicht statt.
            </p>
            <h3>
                <div id="_Toc354567362">4. Einwilligung in weitergehende Nutzungen</div>
            </h3>
            <p>
                Die
                Nutzung bestimmter Angebote auf unserer Webseite wie etwa Newsletter oder Foren
                kann eine vorherige Registrierung und eine weitergehende Verarbeitung personenbezogener
                Daten erfordern, beispielsweise eine längerfristige Speicherung von
                E-Mail-Adressen, Nutzerkennungen und Passwörtern. Die Verwendung solcher Daten
                erfolgt nur, wenn Sie uns diese übermittelt und ausdrücklich vorab in die
                Verwendung eingewilligt haben.
            </p>
            <h3>
                <div id="_Toc354567363">5. Cookies</div>
            </h3>
            <p>
                Auf
                unseren Webseiten verwenden wir in der Regel keine Cookies. Lediglich ausnahmsweise
                werden Cookies eingesetzt, die Daten zur technischen Sitzungssteuerung im Speicher
                Ihres Browsers ablegen. Diese Daten werden&nbsp;mit dem Schließen Ihres
                Browsers gelöscht.
            </p>
            <p>
                Sollten wir ausnahmsweise in einem Cookie auch personenbezogene Daten speichern wollen,
                etwa eine Nutzerkennung, werden Sie besonders darauf hingewiesen.
            </p>
            <p>
                Natürlich können Sie unsere Webseiten auch ohne Cookies betrachten. Die meisten Browser
                akzeptieren Cookies jedoch automatisch. Sie können das Speichern von Cookies
                verhindern, indem Sie dies in Ihren Browser-Einstellungen festlegen. Wenn Sie
                keine Cookies akzeptieren, kann dies zu Funktionseinschränkungen unserer
                Angebote führen.
            </p>
            <h3>
                <div id="_Toc354567364">6. Sicherheit</div>
            </h3>
            <p>
                Die Fraunhofer-Gesellschaft setzt technische und organisatorische Sicherheitsmaßnahmen
                ein, um Ihre durch uns verwalteten personenbezogenen Daten gegen zufällige oder
                vorsätzliche Manipulationen, Verlust, Zerstörung oder gegen den Zugriff unberechtigter
                Personen zu schützen. Unsere Sicherheitsmaßnahmen werden entsprechend der
                technologischen Entwicklung fortlaufend verbessert.
            </p>
            <h3>
                <div id="_Toc354567365">7. Links zu Webseiten anderer Anbieter</div>
            </h3>
            <p>
                Unsere Webseiten können Links zu Webseiten anderer Anbieter enthalten. Wir weisen
                darauf hin, dass diese Datenschutzerklärung ausschließlich für die Webseiten
                der Fraunhofer-Gesellschaft gilt. Wir haben keinen Einfluss darauf und
                kontrollieren nicht, dass andere Anbieter die geltenden Datenschutzbestimmungen
                einhalten.
            </p>
            <h3>
                <div id="_Toc354567366">8. Auskunftsrecht und Kontaktdaten</div>
            </h3>
            <p>
                Ihnen steht ein Auskunftsrecht bezüglich der über Sie gespeicherten personenbezogenen
                Daten und ferner ein Recht auf Berichtigung unrichtiger Daten, Sperrung und Löschung
                zu.
            </p>
            <p>
                Wenn Sie bei einzelnen Diensten der Fraunhofer-Gesellschaft als Nutzer registriert sind, bieten wir Ihnen teilweise auch an, die Daten über ein
                Nutzer-Konto selbst einzusehen und gegebenenfalls zu löschen oder zu ändern.
            </p>
            <p>
                Wenn
                Sie bei einzelnen Diensten der Fraunhofer-Gesellschaft als Nutzer registriert
                sind, bieten wir Ihnen teilweise auch an, die Daten über ein Nutzer-Konto selbst
                einzusehen und gegebenenfalls zu löschen oder zu ändern. Wenn
                Sie Auskunft über Ihre personenbezogenen Daten beziehungsweise deren Korrektur
                oder Löschung wünschen oder weitergehende Fragen über die Verwendung Ihrer uns
                überlassenen personenbezogenen Daten haben, kontaktieren Sie bitte:
            </p>
            <p>
                <strong>Dr. Niklas Speer</strong>
            </p>
            <p>
                Datenschutzbeauftragter
                <br/>
                Zentrale der Fraunhofer-Gesellschaft
                <br/>
                Hansastraße 27 c
                <br/>
                80686 München, Deutschland
                <br/>
                niklas.speer@zv.fraunhofer.de
                niklas.speer@zv.fraunhofer.de
                <br/>
                Telefon +49 89 1205-2015
            </p>
            <h3>
                <div id="_Toc354567367">9. Einbeziehung, Gültigkeit und Aktualität der Datenschutzerklärung</div>
            </h3>
            <p>
                Mit der Nutzung unserer Webseite willigen Sie in die vorab beschriebene Datenverwendung
                ein. Die Datenschutzerklärung ist aktuell gültig und datiert vom 01.06.2013.
            </p>
            <p>
                Durch die Weiterentwicklung unserer Webseite oder die Implementierung neuer Technologien
                kann es notwendig werden, diese Datenschutzerklärung zu ändern. Die Fraunhofer-Gesellschaft
                behält sich vor, die Datenschutzerklärung jederzeit mit Wirkung für die Zukunft
                zu ändern und wird Sie über das di.me System darüber informieren. Wir empfehlen
                Ihnen, sich die aktuelle Datenschutzerklärung von Zeit zu Zeit erneut durchzulesen.
            </p>

        </div>
    </div>







    <!-- JS -->
    <script src="/dime-communications/static/ui/dime/register/js/jquery-1.9.1.min.js" type="text/javascript"></script>
    <script src="/dime-communications/static/ui/dime/register/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/dime-communications/static/ui/dime/register/js/registry_script.js" type="text/javascript"></script>

</body>
</html>