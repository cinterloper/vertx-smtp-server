import org.apache.directory.ldap.client.api.LdapConnection
import org.apache.directory.ldap.client.api.LdapNetworkConnection
import org.apache.directory.server.core.DefaultDirectoryService
import java.io.File;
import java.util.List;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.ldap.model.schema.registries.SchemaLoader;
import org.apache.directory.api.ldap.schema.extractor.SchemaLdifExtractor;
import org.apache.directory.api.ldap.schema.extractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.api.ldap.schema.loader.LdifSchemaLoader;
import org.apache.directory.api.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.api.util.exception.Exceptions;
import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.api.CacheService;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.api.schema.SchemaPartition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.api.ldap.model.name.Dn


import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;

import io.vertx.core.json.JsonObject

def logger = io.vertx.core.logging.LoggerFactory.getLogger(this.getClass().getName())


public class EmbeddedADS {
    private DirectoryService service;

    /** The LDAP server */
    private LdapServer server;

    private static EmbeddedADS instance;



    public static EmbeddedADS getInstance(){
        if(instance == null){
            instance= new EmbeddedADS();
        }
        return instance;
    }
    private void initSchemaPartition() throws Exception {
        final InstanceLayout instanceLayout = this.service.getInstanceLayout();

        final File schemaPartitionDirectory = new File(
                instanceLayout.getPartitionsDirectory(), "schema");

        // Extract the schema on disk (a brand new one) and load the registries
        if (schemaPartitionDirectory.exists()) {
            System.out
                    .println("schema partition already exists, skipping schema extraction");
        } else {
            final SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor(
                    instanceLayout.getPartitionsDirectory());
            extractor.extractOrCopy();
        }

        final SchemaLoader loader = new LdifSchemaLoader(
                schemaPartitionDirectory);
        final SchemaManager schemaManager = new DefaultSchemaManager(loader);

        // We have to load the schema now, otherwise we won't be able
        // to initialize the Partitions, as we won't be able to parse
        // and normalize their suffix Dn
        schemaManager.loadAllEnabled();

        final List<Throwable> errors = schemaManager.getErrors();

        if (errors.size() != 0) {
            throw new Exception(I18n.err(I18n.ERR_317,
                    Exceptions.printErrors(errors)));
        }

        this.service.setSchemaManager(schemaManager);

        // Init the LdifPartition with schema
        final LdifPartition schemaLdifPartition = new LdifPartition(schemaManager, service.getDnFactory());
        schemaLdifPartition.setPartitionPath(schemaPartitionDirectory.toURI());

        // The schema partition
        final SchemaPartition schemaPartition = new SchemaPartition(
                schemaManager);
        schemaPartition.setWrappedPartition(schemaLdifPartition);
        this.service.setSchemaPartition(schemaPartition);
    }

    /**
     * Initialize the server. It creates the partition, adds the index, and
     * injects the context entries for the created partitions.
     *
     * @param workDir
     *            the directory to be used for storing the data
     * @throws Exception
     *             if there were some problems while initializing the system
     */
    private void initDirectoryService(final File workDir) throws Exception {
        // Initialize the LDAP service
        this.service = new DefaultDirectoryService();
        this.service.setInstanceLayout(new InstanceLayout(workDir));

        final CacheService cacheService = new CacheService();
        cacheService.initialize(this.service.getInstanceLayout());

        this.service.setCacheService(cacheService);

        // first load the schema
        this.initSchemaPartition();

        // then the system partition
        // this is a MANDATORY partition
        // DO NOT add this via addPartition() method, trunk code complains about
        // duplicate partition
        // while initializing
        final JdbmPartition systemPartition = new JdbmPartition(
                this.service.getSchemaManager(), service.getDnFactory());
        systemPartition.setId("system");
        systemPartition.setPartitionPath(new File(this.service
                .getInstanceLayout().getPartitionsDirectory(), systemPartition
                .getId()).toURI());
        systemPartition.setSuffixDn(new Dn(ServerDNConstants.SYSTEM_DN));
        systemPartition.setSchemaManager(this.service.getSchemaManager());

        // mandatory to call this method to set the system partition
        // Note: this system partition might be removed from trunk
        this.service.setSystemPartition(systemPartition);

        // Disable the ChangeLog system
        this.service.getChangeLog().setEnabled(false);
        this.service.setDenormalizeOpAttrsEnabled(true);

        // And start the service
        this.service.startup();

        // We are all done !
    }

    /**
     * Creates a new instance of EmbeddedADS. It initializes the directory
     * service.
     *
     * @throws Exception
     *             If something went wrong
     */
    public EmbeddedADS(final File workDir) throws Exception {
        if (!workDir.exists()) {
            workDir.mkdirs();
            this.initDirectoryService(workDir);
            this.service.shutdown();
        }

        this.initDirectoryService(workDir);
    }

    /**
     * starts the LdapServer
     *
     * @throws Exception
     */
    public void startServer() throws Exception {
        this.server = new LdapServer();
        final int serverPort = 10389;
        this.server.setTransports(new TcpTransport(serverPort));
        this.server.setDirectoryService(this.service);

        this.server.start();

        System.out.println("The server is running.");
    }

    /**
     * Main class.
     *
     * @param args
     *            Not used.
     */
    public static void main(final String[] args) {

    }
}

try {
    final File workDir = new File(System.getProperty("java.io.tmpdir")
            + "/server-work");

    // Create the server
    EmbeddedADS ads = new EmbeddedADS(workDir);

    // optionally we can start a server too
    ads.startServer();
    lookup = ads.service.getAdminSession().lookup(new Dn("uid=admin,ou=system"))
    logger.info("lookup results: ${lookup.toString()}")
} catch (final Exception e) {
    // Ok, we have something wrong going on ...
    e.printStackTrace();
}
