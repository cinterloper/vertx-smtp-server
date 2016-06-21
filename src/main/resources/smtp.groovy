/**
 * Created by grant on 8/20/15.
 */

import io.vertx.core.logging.Logger
import io.vertx.groovy.core.Vertx
import org.subethamail.smtp.server.SMTPServer;
import org.subethamail.smtp.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.core.json.JsonObject

import java.nio.file.Path
import java.security.MessageDigest

MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory(vertx);
smtpServer = new SMTPServer(myFactory);

if(System.getenv("SMTP_PORT").toInteger())
    smtpServer.setPort(System.getenv("SMTP_PORT").toInteger())
else
    smtpServer.setPort(25000)
smtpServer.start();

public class MyMessageHandlerFactory implements MessageHandlerFactory {
    Vertx vx
    def strpath = ""

    MyMessageHandlerFactory(vertx) {
        if(System.getenv("SMTP_STORE_DIR"))
            strpath = System.getenv("SMTP_STORE_DIR") + '/'

        vx = vertx
    }

    public MessageHandler create(MessageContext ctx) {
        return new MailHandler(ctx, strpath, vx);
    }
}


class MailHandler implements MessageHandler {
    MessageContext ctx;
    Vertx vx
    Logger logger
    String from, recipient, dstfl, strpath

    public MailHandler(MessageContext ctx, s, vtx) {
        this.ctx = ctx;
        vx = vtx;
        strpath = s;
        this.logger = io.vertx.core.logging.LoggerFactory.getLogger(this.getClass().getName())
    }

    public void from(String f) throws RejectException {
        from = f
        logger.info("FROM:" + f);
    }

    public void recipient(String r) throws RejectException {
        recipient = r
        logger.info("RECIPIENT:" + r);
    }

    public void data(InputStream data) throws IOException {

        logger.debug("MAIL DATA");
        def strdata = this.convertStreamToString(data);
        logger.debug(strdata)
        this.testCreate("${strpath}${recipient}")
        this.testCreate("${strpath}${recipient}/${from}")
        dstfl = "${strpath}${recipient}/${from}/${generateMD5_A(strdata as String )}.mail"
        vx.fileSystem().writeFileBlocking(dstfl, Buffer.buffer(strdata as String))
        vx.eventBus().send("mail", [FROM: from, RCPT: recipient, path: dstfl])
    }

    public void done() {
        logger.info("Finished ${dstfl}");
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    private boolean testCreate (String path){
        if(!vx.fileSystem().existsBlocking(path)){
           try{ vx.fileSystem().mkdirBlocking(path)}catch(e){
               logger.error(e)
           }
        }
    }
    def generateMD5_A(String s){
        MessageDigest.getInstance("MD5").digest(s.bytes).encodeHex().toString()
    }

}
