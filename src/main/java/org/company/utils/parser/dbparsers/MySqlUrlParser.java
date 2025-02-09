package org.company.utils.parser.dbparsers;

import org.apache.commons.lang3.ObjectUtils;
import org.company.utils.parser.ConnectionConfig;
import org.company.utils.parser.HostConfig;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySqlUrlParser implements DatabaseUrlParser {

    private final String MYSQL_GENERAL_PATTERN = "^jdbc:(.*)://(.*)/(.*)(\\?(.*))?$";

    @Override
    public ConnectionConfig parseUrl(String url) {

        Pattern pattern = Pattern.compile(MYSQL_GENERAL_PATTERN);
        Matcher matcher = pattern.matcher(url);

        if (matcher.matches()) {

            ConnectionConfig output = new ConnectionConfig();

            String protocol = matcher.group(1);
            String hosts = matcher.group(2);
            String database = matcher.group(3).contains("?") ? matcher.group(3).split("\\?", 2)[0] : matcher.group(3);
            String properties = matcher.group(3).contains("?") ? matcher.group(3).split("\\?", 2)[1] : null;

            if (ObjectUtils.anyNull(protocol, hosts, database)) {
                throw new IllegalArgumentException("Protocol, host and database information are required.");
            }

            output.getHosts().addAll(extractData(protocol, hosts, database, properties));
            return output;
        } else {
            throw new IllegalArgumentException("Invalid JDBC URL format for MySQL database.");
        }
    }

    private List<HostConfig> extractData(String protocol, String hosts, String database, String properties) {

        List<HostConfig> output = new ArrayList<>();

        String user = null;
        String password = null;

        // Check if the input matches pattern with separate credentials e.g. jdbc:mysql://user:password@[...]
        // It's assumed that credentials in this format will be still placed in properties map on the host level
        Pattern credentialsPattern = Pattern.compile("^(\\w+:.*)@\\[(.*)]$");
        Matcher matcher = credentialsPattern.matcher(hosts);

        if (matcher.matches()) {
            String[] credentials = matcher.group(1).split(":", 2);
            user = credentials[0];
            password = credentials[1];
            hosts = matcher.group(2); // Proceed with the rest of the host data (with credentials being removed)
        }

        List<Integer> commas = getAllIndicesOfACharacter(hosts, ",");
        List<Integer> closingParentheses = getAllIndicesOfACharacter(hosts, ")");

        int index = 0;

        while (index < hosts.length()) {
            int nextComma = getNextOccurrenceOfACharacter(index, hosts.length(), commas);
            int nextClosingParenthesis = getNextOccurrenceOfACharacter(index, hosts.length(), closingParentheses);

            if (hosts.substring(index, nextComma).matches("(\\w+:\\d+)")) {
                output.add(createHostFromSimpleHostAndPortFormat(hosts.substring(index, nextComma), protocol, database));
                index = nextComma + 1;
            } else if (hosts.substring(index).startsWith("address=")) {
                output.add(createHostFromAddressFormat(hosts.substring(index, nextComma), protocol, database));
                index = nextComma + 1;
            } else if (hosts.charAt(index) == '(') {
                output.add(createHostFromKeyValueFormat(hosts.substring(index + 1, nextClosingParenthesis), protocol, database));
                index = nextClosingParenthesis + 1;
            } else {
                throw new IllegalArgumentException("Could not parse malformed URL: " + hosts);
            }
        }

        if (ObjectUtils.anyNotNull(properties, user, password)) {
            Map<String, String> propertiesMap = extractProperties(properties, user, password);
            output.forEach(hostConfig -> appendPropertiesToEachHost(hostConfig, propertiesMap));
        }

        return output;
    }

    private HostConfig createHostFromSimpleHostAndPortFormat(String currentSubstring, String protocol, String database) {
        String[] hostAndPort = currentSubstring.split(":");
        return new HostConfig(protocol, hostAndPort[0], database, Integer.parseInt(hostAndPort[1]));
    }

    private HostConfig createHostFromKeyValueFormat(String currentSubstring, String protocol, String database) {
        List<String> keyValueList = Arrays.stream(currentSubstring.split(",")).toList();
        return doCreateHostFromKeyValues(keyValueList, protocol, database);
    }

    private HostConfig createHostFromAddressFormat(String currentSubstring, String protocol, String database) {
        // Identify key-value pairs placed in separate parentheses (key1=value1) and transform into list of strings
        Pattern pattern = Pattern.compile("\\([^)]+\\)");
        Matcher matcher = pattern.matcher(currentSubstring);

        List<String> keyValueList = new ArrayList<>();

        while (matcher.find()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                keyValueList.add(matcher.group(i).substring(1, matcher.group(i).length() - 1));
            }
        }
        return doCreateHostFromKeyValues(keyValueList, protocol, database);
    }

    private HostConfig doCreateHostFromKeyValues(List<String> keyValueList, String protocol, String database) {
        String host = null;
        Integer port = null;
        HashMap<String, String> propertiesMap = new HashMap<>();

        for (String keyValueString : keyValueList) {
            String[] keyValue = keyValueString.split("=");
            switch (keyValue[0]) {
                case "host":
                    host = keyValue[1];
                    break;
                case "port":
                    port = Integer.parseInt(keyValue[1]);
                    break;
                default:
                    propertiesMap.putIfAbsent(keyValue[0], keyValue[1]);
                    break;
            }
        }
        return new HostConfig(protocol, host, database, port, propertiesMap);
    }

    private Map<String, String> extractProperties(String properties, String user, String password) {
        Map<String, String> propertiesMap = new HashMap<>();

        if (user != null) propertiesMap.putIfAbsent("user", user);
        if (password != null) propertiesMap.putIfAbsent("password", password);
        if (properties != null) {
            String[] propertiesArray = properties.split("&");
            for (String property : propertiesArray) {
                String[] keyValue = property.split("=");
                propertiesMap.putIfAbsent(keyValue[0], keyValue[1]);
            }
        }
        return propertiesMap;
    }

    private void appendPropertiesToEachHost(HostConfig hostConfig, Map<String, String> propertiesMap) {
        propertiesMap.forEach((key, value) -> hostConfig.getProperties().putIfAbsent(key, value));
    }

    private List<Integer> getAllIndicesOfACharacter(String text, String c) {
        return Pattern.compile(Pattern.quote(c))
                .matcher(text)
                .results()
                .map(MatchResult::start)
                .toList();
    }

    private int getNextOccurrenceOfACharacter(int currentIndex, int textLength, List<Integer> indicesOfACharacter) {
        return indicesOfACharacter.stream().filter(i -> i > currentIndex).findFirst().orElse(textLength);
    }
}
