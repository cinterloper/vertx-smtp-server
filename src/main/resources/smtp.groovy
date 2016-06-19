/**
 * Created by grant on 8/20/15.
 */

import io.vertx.core.logging.Logger
import org.subethamail.smtp.server.SMTPServer;
import org.subethamail.smtp.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.vertx.core.json.JsonObject
MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory(vertx);
smtpServer = new SMTPServer(myFactory);
smtpServer.setPort(25000);
smtpServer.start();

public class MyMessageHandlerFactory implements MessageHandlerFactory {
    def vx

    MyMessageHandlerFactory(vertx) {
        vx = vertx
    }

    public MessageHandler create(MessageContext ctx) {
        return new MailHandler(ctx, vx);

    }
}


class MailHandler implements MessageHandler {
    MessageContext ctx;
    def vx
    Logger logger
    String from, recipient
    public MailHandler(MessageContext ctx, vtx) {
        this.ctx = ctx;
        vx = vtx;
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

        logger.info("MAIL DATA");
        logger.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        def strdata = this.convertStreamToString(data);
        println(strdata)
        def eb = vx.eventBus()
        eb.send("mail", [FROM:from, RCPT:recipient, data:strdata])
        logger.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
    }

    public void done() {
        logger.info("Finished");
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

}
