package org.company.utils.parser;

import java.util.HashMap;
import java.util.Objects;

public class HostConfig {

    private String protocol;
    private String host;
    private Integer port;
    private String database;
    private HashMap<String, String> properties = new HashMap<>();

    public HostConfig(String protocol, String host, String database, Integer port) {
        this.protocol = protocol;
        this.host = host;
        this.database = database;
        this.port = port;
    }

    public HostConfig(String protocol, String host, String database, Integer port, HashMap<String, String> properties) {
        this.protocol = protocol;
        this.host = host;
        this.database = database;
        this.port = port;
        this.properties = properties;
    }

    public HostConfig() {
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getHost() {
        return this.host;
    }

    public Integer getPort() {
        return this.port;
    }

    public String getDatabase() {
        return database;
    }

    public HashMap<String, String> getProperties() {
        return this.properties;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof HostConfig other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$protocol = this.getProtocol();
        final Object other$protocol = other.getProtocol();
        if (!Objects.equals(this$protocol, other$protocol)) return false;
        final Object this$host = this.getHost();
        final Object other$host = other.getHost();
        if (!Objects.equals(this$host, other$host)) return false;
        final Object this$port = this.getPort();
        final Object other$port = other.getPort();
        if (!Objects.equals(this$port, other$port)) return false;
        final Object this$properties = this.getProperties();
        final Object other$properties = other.getProperties();
        return Objects.equals(this$properties, other$properties);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof HostConfig;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $protocol = this.getProtocol();
        result = result * PRIME + ($protocol == null ? 43 : $protocol.hashCode());
        final Object $host = this.getHost();
        result = result * PRIME + ($host == null ? 43 : $host.hashCode());
        final Object $port = this.getPort();
        result = result * PRIME + ($port == null ? 43 : $port.hashCode());
        final Object $properties = this.getProperties();
        result = result * PRIME + ($properties == null ? 43 : $properties.hashCode());
        return result;
    }

    public String toString() {
        return "Host(protocol=" + this.getProtocol() + ", host=" + this.getHost() + ", port=" + this.getPort() + ", properties=" + this.getProperties() + ")";
    }
}
