package org.company.utils.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConnectionConfig {

    private final List<HostConfig> hostConfigs = new ArrayList<>();

    public HostConfig getFirstHostConfig() {
        return hostConfigs.get(0);
    }

    public ConnectionConfig() {
    }

    public List<HostConfig> getHosts() {
        return this.hostConfigs;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ConnectionConfig other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$hosts = this.getHosts();
        final Object other$hosts = other.getHosts();
        return Objects.equals(this$hosts, other$hosts);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ConnectionConfig;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $hosts = this.getHosts();
        result = result * PRIME + ($hosts == null ? 43 : $hosts.hashCode());
        return result;
    }

    public String toString() {
        return "Connection(hosts=" + this.getHosts() + ")";
    }
}
