import io.vertx.core.json.JsonObject

def logger = io.vertx.core.logging.LoggerFactory.getLogger(this.getClass().getName())


def JsonObject config = vertx.getOrCreateContext().config()

def options = [
        "config": config.getMap(),
	  worker:true
]
logger.info("deploying with config: " + options)
vertx.deployVerticle('smtp.groovy', options)
