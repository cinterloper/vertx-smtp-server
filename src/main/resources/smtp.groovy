/**
 * Created by grant on 8/20/15.
 */
/**
 * Created by grant on 8/20/15.
 */
import org.subethamail.smtp.server.SMTPServer;
import org.subethamail.smtp.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.vertx.core.json.JsonObject



logger = io.vertx.core.logging.LoggerFactory.getLogger(this.getClass().getName())




MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory(vertx);
smtpServer = new SMTPServer(myFactory);
smtpServer.setPort(25000);
smtpServer.start();

public class MyMessageHandlerFactory implements MessageHandlerFactory {
    def vx
    MyMessageHandlerFactory(vertx){
        vx = vertx
    }
    public MessageHandler create(MessageContext ctx) {
        return new MailHandler(ctx,vx);

    }
}

class MailHandler implements MessageHandler {
        MessageContext ctx;
        def vx

        public MailHandler(MessageContext ctx,vtx) {

            this.ctx = ctx;
            vx=vtx;

        }

        public void from(String from) throws RejectException {
            System.out.println("FROM:"+from);
        }

        public void recipient(String recipient) throws RejectException {
            System.out.println("RECIPIENT:"+recipient);
        }

        public void data(InputStream data) throws IOException {
         
        System.out.println("MAIL DATA");
            System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
            def strdata=this.convertStreamToString(data);
        println(strdata)
        def eb = vx.eventBus()
        eb.send("mail",strdata)
            System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        }

        public void done() {
            System.out.println("Finished");
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