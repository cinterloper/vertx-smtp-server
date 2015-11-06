/**
 * Created by grant on 8/20/15.
 */
/**
 * Created by grant on 8/20/15.
 */
import org.apache.james.mailbox.MailboxSession
import org.apache.james.mailbox.maildir.mail.model.MaildirMailbox
import org.apache.james.mailbox.store.SimpleMailboxSession
import org.subethamail.smtp.server.SMTPServer;
import org.subethamail.smtp.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.vertx.core.json.JsonObject
import org.apache.james.mailbox.maildir.MaildirStore


def logger = io.vertx.core.logging.LoggerFactory.getLogger(this.getClass().getName())


public class mdirStore {
    def MaildirStore str;
    public mdirStore () {
        str = new MaildirStore("/tmp/mdir")
        mbox = new MaildirMailbox(new SimpleMailboxSession(1, "grant", "pass", logger,new ArrayList(),'/', MailboxSession.SessionType.System ), )
    }
}



MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory();
smtpServer = new SMTPServer(myFactory);
smtpServer.setPort(25000);
smtpServer.start();

public class MyMessageHandlerFactory implements MessageHandlerFactory {

    public MessageHandler create(MessageContext ctx) {
        return new Handler(ctx);
    }

    class Handler implements MessageHandler {
        MessageContext ctx;

        public Handler(MessageContext ctx) {
            this.ctx = ctx;

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
            System.out.println(this.convertStreamToString(data));
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
}