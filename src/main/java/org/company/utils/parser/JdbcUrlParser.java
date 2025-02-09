package org.company.utils.parser;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.company.utils.parser.dbparsers.DatabaseType;
import org.company.utils.parser.dbparsers.DatabaseUrlParser;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class JdbcUrlParser {

    public ConnectionConfig parseUrl(String url) {

        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Provided URL is null or empty");
        }

        if (!url.toLowerCase().startsWith("jdbc:")) {
            throw new IllegalArgumentException("Provided URL not recognized as JDBC protocol: " + url);
        }

        String requestedDatabaseType = StringUtils.substringBetween(url, "jdbc:", "://").toLowerCase();

        DatabaseType databaseType = Arrays.stream(DatabaseType.values())
                .filter(p -> requestedDatabaseType.contains(p.name().toLowerCase()))
                .findFirst().orElseThrow(() -> new NotImplementedException("Database type not supported: " + requestedDatabaseType));

        try {
            DatabaseUrlParser databaseUrlParser = databaseType.getClazzType().getDeclaredConstructor().newInstance();
            return databaseUrlParser.parseUrl(url);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Failed to parse URL: " + url, e);
        }
    }
}
