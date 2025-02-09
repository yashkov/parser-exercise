package org.company.utils.parser.dbparsers;

import org.company.utils.parser.ConnectionConfig;
import org.company.utils.parser.HostConfig;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MySqlUrlParserTest {

    MySqlUrlParser mySqlUrlParser = new MySqlUrlParser();

    @Test
    void shouldReadSimpleHostPortFormat() {
        ConnectionConfig output = mySqlUrlParser.parseUrl("jdbc:mysql://localhost:3306/test");
        assertEquals(1, output.getHosts().size());
        assertEquals("mysql", output.getFirstHostConfig().getProtocol());
        assertEquals("localhost", output.getFirstHostConfig().getHost());
        assertEquals(3306, output.getFirstHostConfig().getPort());
        assertEquals("test", output.getFirstHostConfig().getDatabase());
        assertEquals(0, output.getFirstHostConfig().getProperties().size());
    }

    @Test
    void shouldReadSimpleHostPortFormatWithParameters() {
        ConnectionConfig output = mySqlUrlParser.parseUrl("jdbc:mysql://localhost:3306/test?user=root&password=password&encoding=utf8");
        assertEquals(1, output.getHosts().size());
        assertEquals("mysql", output.getFirstHostConfig().getProtocol());
        assertEquals("localhost", output.getFirstHostConfig().getHost());
        assertEquals(3306, output.getFirstHostConfig().getPort());
        assertEquals("test", output.getFirstHostConfig().getDatabase());

        Map<String, String> properties = output.getFirstHostConfig().getProperties();

        assertEquals(3, properties.size());
        assertEquals("root", properties.get("user"));
        assertEquals("password", properties.get("password"));
        assertEquals("utf8", properties.get("encoding"));
    }

    @Test
    void shouldReadSimpleHostPortFormatWithCredentials() {
        ConnectionConfig output = mySqlUrlParser.parseUrl("jdbc:mysql://sandy:secret@[myhost1:1111,myhost2:2222]/db");
        assertEquals(2, output.getHosts().size());

        // Check first host
        HostConfig host1 = output.getFirstHostConfig();

        assertEquals("mysql", host1.getProtocol());
        assertEquals("myhost1", host1.getHost());
        assertEquals(1111, host1.getPort());
        assertEquals("db", host1.getDatabase());

        Map<String, String> properties1 = host1.getProperties();

        assertEquals(2, properties1.size());
        assertEquals("sandy", properties1.get("user"));
        assertEquals("secret", properties1.get("password"));

        // Check second host
        HostConfig host2 = output.getHosts().get(1);

        assertEquals("mysql", host2.getProtocol());
        assertEquals("myhost2", host2.getHost());
        assertEquals(2222, host2.getPort());
        assertEquals("db", host2.getDatabase());

        Map<String, String> properties2 = host2.getProperties();

        assertEquals(2, properties2.size());
        assertEquals("sandy", properties1.get("user"));
        assertEquals("secret", properties1.get("password"));
    }

    @Test
    void shouldReadAddressFormat() {
        ConnectionConfig output = mySqlUrlParser.parseUrl("jdbc:mysql://address=(host=myhost1)(port=1111)(encoding=utf8)/db");
        assertEquals(1, output.getHosts().size());
        assertEquals("mysql", output.getFirstHostConfig().getProtocol());
        assertEquals("myhost1", output.getFirstHostConfig().getHost());
        assertEquals(1111, output.getFirstHostConfig().getPort());
        assertEquals("db", output.getFirstHostConfig().getDatabase());

        Map<String, String> properties = output.getFirstHostConfig().getProperties();

        assertEquals(1, properties.size());
        assertEquals("utf8", properties.get("encoding"));
    }

    @Test
    void shouldReadAddressFormatWithMultipleHosts() {
        ConnectionConfig output = mySqlUrlParser.parseUrl("jdbc:mysql://address=(host=myhost1)(port=1111)(key1=value1),address=(host=myhost2)(port=2222)(key2=value2)/db");
        assertEquals(2, output.getHosts().size());

        // Check first host
        HostConfig host1 = output.getFirstHostConfig();

        assertEquals("mysql", host1.getProtocol());
        assertEquals("myhost1", host1.getHost());
        assertEquals(1111, host1.getPort());
        assertEquals("db", host1.getDatabase());

        Map<String, String> properties1 = host1.getProperties();

        assertEquals(1, properties1.size());
        assertEquals("value1", properties1.get("key1"));

        // Check second host
        HostConfig host2 = output.getHosts().get(1);

        assertEquals("mysql", host2.getProtocol());
        assertEquals("myhost2", host2.getHost());
        assertEquals(2222, host2.getPort());
        assertEquals("db", host2.getDatabase());

        Map<String, String> properties2 = host2.getProperties();

        assertEquals(1, properties2.size());
        assertEquals("value2", properties2.get("key2"));
    }

    @Test
    void shouldReadKeyValueFormat() {
        ConnectionConfig output = mySqlUrlParser.parseUrl("jdbc:mysql://(host=myhost2,port=2222,timezone=utc)/db?encoding=utf8");

        assertEquals(1, output.getHosts().size());

        assertEquals("mysql", output.getFirstHostConfig().getProtocol());
        assertEquals("myhost2", output.getFirstHostConfig().getHost());
        assertEquals(2222, output.getFirstHostConfig().getPort());
        assertEquals("db", output.getFirstHostConfig().getDatabase());

        Map<String, String> properties = output.getFirstHostConfig().getProperties();

        assertEquals(2, properties.size());
        assertEquals("utf8", properties.get("encoding"));
        assertEquals("utc", properties.get("timezone"));
    }

    @Test
    void shouldReadMixedFormatWithoutOverwritingHostSpecificProperties() {
        ConnectionConfig output = mySqlUrlParser.parseUrl("jdbc:mysql://myhost1:1111,(host=myhost2,port=2222,encoding=utf8)/db?encoding=Unicode");

        assertEquals(2, output.getHosts().size());

        // Check first host
        HostConfig host1 = output.getFirstHostConfig();

        assertEquals("mysql", host1.getProtocol());
        assertEquals("myhost1", host1.getHost());
        assertEquals(1111, host1.getPort());
        assertEquals("db", host1.getDatabase());

        Map<String, String> properties1 = host1.getProperties();

        assertEquals(1, properties1.size());
        assertEquals("Unicode", properties1.get("encoding"));

        // Check second host
        HostConfig host2 = output.getHosts().get(1);

        assertEquals("mysql", host2.getProtocol());
        assertEquals("myhost2", host2.getHost());
        assertEquals(2222, host2.getPort());
        assertEquals("db", host2.getDatabase());

        Map<String, String> properties2 = host2.getProperties();

        assertEquals(1, properties2.size());
        assertEquals("utf8", properties2.get("encoding"));
    }

    // (etc... more extensive test coverage should be applied)
}