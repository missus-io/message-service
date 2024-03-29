package io.missus.messageservice.data.repository;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.missus.messageservice.config.CassandraTestConfig;
import lombok.extern.java.Log;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.Properties;


@Import(CassandraTestConfig.class)
@Log
public abstract class AbstractRepositoryTest {

    @BeforeClass
    public static void initCassandra() {
        try {
            Properties prop = new Properties();
            prop.load(AbstractRepositoryTest.class.getClassLoader().getResourceAsStream("application.yml"));
            String cassandraHosts = prop.getProperty("service.cassandra.hosts");
            String cassandraPort = prop.getProperty("service.cassandra.port");

            EmbeddedCassandraServerHelper.startEmbeddedCassandra("another-cassandra.yaml", 20000);
            log.info("Connect to embedded db");
            Cluster cluster = Cluster.builder().addContactPoints(cassandraHosts).withPort(Integer.parseInt(cassandraPort)).build();
            Session session = cluster.connect();

            log.info("Initialize keyspace");
            session.execute("create keyspace  \"mykeyspace\" WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};");
            session.execute("use \"mykeyspace\";");
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void initTest() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void cleanCassandra() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }
}