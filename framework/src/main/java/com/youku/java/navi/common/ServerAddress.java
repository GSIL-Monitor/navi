package com.youku.java.navi.common;

import com.youku.java.navi.common.exception.NaviSystemException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServerAddress {

    private String host;
    private int port;

    private static final String hostRegex = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";

    public ServerAddress(String host, int port) throws NaviSystemException {
        validateHost(host);
        this.host = host;
        this.port = port;
    }

    private void validateHost(String host) throws NaviSystemException {
        if (!host.matches(hostRegex)) {
            throw new NaviSystemException("host is invalid!", NaviError.INVALID_HOST);
        }
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

}
