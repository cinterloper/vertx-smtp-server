This should listen on port 25000 and put the mail on the vertx bus address "mail"

it will accept all messages delivered to it.




[grant@silverbullet ~]$ echo "This is the message body" | swaks --to someone@gmail.com --from "you@example.com" --server 192.168.7.101:25000 --attach testcp.groovy 

=== Trying 192.168.7.101:25000...
=== Connected to 192.168.7.101.
<-  220 nighthawk ESMTP SubEthaSMTP null
 -> EHLO silverbullet
<-  250-nighthawk
<-  250-8BITMIME
<-  250 Ok
 -> MAIL FROM:<you@example.com>
<-  250 Ok
 -> RCPT TO:<someone@gmail.com>
<-  250 Ok
 -> DATA
<-  354 End data with <CR><LF>.<CR><LF>
 -> Date: Mon, 21 Dec 2015 00:59:49 -0700
 -> To: someone@gmail.com
 -> From: you@example.com
 -> Subject: test Mon, 21 Dec 2015 00:59:49 -0700
 -> X-Mailer: swaks v20130209.0 jetmore.org/john/code/swaks/
 -> MIME-Version: 1.0
 -> Content-Type: multipart/mixed; boundary="----=_MIME_BOUNDARY_000_31820"
 -> 
 -> ------=_MIME_BOUNDARY_000_31820
 -> Content-Type: text/plain
 -> 
 -> This is a test mailing
 -> ------=_MIME_BOUNDARY_000_31820
 -> Content-Type: application/octet-stream; name="testcp.groovy"
 -> Content-Description: testcp.groovy
 -> Content-Disposition: attachment; filename="testcp.groovy"
 -> Content-Transfer-Encoding: BASE64
 -> 
 -> CgpDTEFTU1BBVEggPSAiamF2YS5jbGFzcy5wYXRoIgovL2NwID0gU3lzdGVtLmdldFByb3Bl
 -> KENMQVNTUEFUSCkKY3AgPSBDbGFzc0xvYWRlci5nZXRTeXN0ZW1DbGFzc0xvYWRlcigpLmdl
 -> THMoKTsKcHJpbnRsbiBjcAo=
 -> 
 -> ------=_MIME_BOUNDARY_000_31820--
 -> 
 -> 
 -> .
<-  250 Ok
 -> QUIT
<-  221 Bye
=== Connection closed with remote host.


*****************************************************************************************************

grant@nighthawk ~/sandbox/vertx/graphmail (git)-[master] % java -jar build/libs/graphmail-3.2.0-fat.jar
Dec 21, 2015 7:59:56 AM mail
INFO: deploying with config: [config:[:]]
Dec 21, 2015 7:59:56 AM io.vertx.core.Starter
INFO: Succeeded in deploying verticle
SLF4J: Failed to load class "org.slf4j.impl.StaticMDCBinder".
SLF4J: Defaulting to no-operation MDCAdapter implementation.
SLF4J: See http://www.slf4j.org/codes.html#no_static_mdc_binder for further details.
FROM:you@example.com
RECIPIENT:someone@gmail.com
MAIL DATA
= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
Received: from silverbullet ([192.168.7.116])
        by nighthawk
        with SMTP (SubEthaSMTP null) id IIFO9TQR
        for someone@gmail.com;
        Mon, 21 Dec 2015 07:59:57 +0000 (UTC)
Date: Mon, 21 Dec 2015 00:59:49 -0700
To: someone@gmail.com
From: you@example.com
Subject: test Mon, 21 Dec 2015 00:59:49 -0700
X-Mailer: swaks v20130209.0 jetmore.org/john/code/swaks/
MIME-Version: 1.0
Content-Type: multipart/mixed; boundary="----=_MIME_BOUNDARY_000_31820"

------=_MIME_BOUNDARY_000_31820
Content-Type: text/plain

This is a test mailing
------=_MIME_BOUNDARY_000_31820
Content-Type: application/octet-stream; name="testcp.groovy"
Content-Description: testcp.groovy
Content-Disposition: attachment; filename="testcp.groovy"
Content-Transfer-Encoding: BASE64

CgpDTEFTU1BBVEggPSAiamF2YS5jbGFzcy5wYXRoIgovL2NwID0gU3lzdGVtLmdldFByb3BlcnR5
KENMQVNTUEFUSCkKY3AgPSBDbGFzc0xvYWRlci5nZXRTeXN0ZW1DbGFzc0xvYWRlcigpLmdldFVS
THMoKTsKcHJpbnRsbiBjcAo=

------=_MIME_BOUNDARY_000_31820--



= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
Finished
