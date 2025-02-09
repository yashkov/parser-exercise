package org.company.utils.parser.dbparsers;

import org.company.utils.parser.ConnectionConfig;

public interface DatabaseUrlParser {

    ConnectionConfig parseUrl(String url);
}
