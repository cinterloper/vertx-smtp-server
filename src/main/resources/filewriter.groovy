/**
 * Created by grant on 3/2/16.
 */

import io.vertx.core.AsyncResult
import io.vertx.core.logging.LoggerFactory
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.buffer.Buffer

import java.security.MessageDigest

def generateMD5_A(String s){
    MessageDigest.getInstance("MD5").digest(s.bytes).encodeHex().toString()
}

def vx = vertx as Vertx
eb = vx.eventBus()
fs = vx.fileSystem()
logger = LoggerFactory.getLogger("database.groovy");

eb.consumer("mail", { ebmsg ->
    def message = ebmsg.body()
    def rcpt = message['RCPT'] as String
    testCreate(rcpt,message, { mesg ->
        def from = mesg['FROM'] as String
        testCreate("${rcpt}/${from}",message, { msg ->
            String dst = "${rcpt}/${from}/${generateMD5_A(msg['data'] as String )}.mail"
            fs.writeFile(dst, Buffer.buffer(msg['data'] as String), { ar ->
                if(!ar.failed()){
                    println("wrote mail")
                    eb.send("storedMail", dst )
                }
                else{
                    println("failed writing mail ${ar.cause()}")
                }
            })


        })
    })
    println("I have received a message: ${message}")
})


def testCreate(path, ctx, cb){
    logger.info("calling testCreate ${path}")
    fs.exists(path, { existsRes ->
        def ar = existsRes as AsyncResult
        if(ar.result() == false){
            logger.info("no dir ${path}, attempting to create")
                fs.mkdir(path,{ mkdirRes ->
                    def br = mkdirRes as AsyncResult
                    if(br.failed()){
                        logger.error("cant make dir ${path}: ${br.cause()} ")
                    }else{
                        logger.info("created ${path}")
                        cb(ctx)
                    }
                })

        }else{
            cb(ctx)
        }
    })
}