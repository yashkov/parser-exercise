package org.company.utils.parser;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class JdbcUrlParserTest {

    private final JdbcUrlParser jdbcUrlParser = new JdbcUrlParser();

    @Test
    void shouldThrowExceptionWhenUrlIsNull() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> jdbcUrlParser.parseUrl(null));
        assertTrue(thrown.getMessage().contains("Provided URL is null or empty"));
    }

    @Test
    void shouldThrowExceptionWhenUrlIsEmpty() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> jdbcUrlParser.parseUrl(""));
        assertTrue(thrown.getMessage().contains("Provided URL is null or empty"));
    }

    @Test
    void shouldThrowExceptionWhenNonJdbcUrlIsUsed() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> jdbcUrlParser.parseUrl("mysqlx://(address=host:1111,priority=1,key1=value1)/db"));
        assertTrue(thrown.getMessage().contains("Provided URL not recognized as JDBC protocol:"));
    }

    @Test
    void shouldThrowExceptionForNotSupportedDatabase() {
        NotImplementedException thrown = assertThrows(NotImplementedException.class, () -> jdbcUrlParser.parseUrl("jdbc:new-flavour-of-the-month-db://localhost:3306/test"));
        assertTrue(thrown.getMessage().contains("Database type not supported: new-flavour-of-the-month-db"));
    }

    @Test
    void shouldReturnValidOutput() {
        ConnectionConfig output = jdbcUrlParser.parseUrl("jdbc:mysql://localhost:3306/test");
        assertEquals(1, output.getHosts().size());
        assertEquals("localhost", output.getFirstHostConfig().getHost());
        assertEquals(3306, output.getFirstHostConfig().getPort());
    }
}