package com.cuckoo.framework.navi.engine.datasource.driver;

import com.cuckoo.framework.navi.engine.datasource.pool.NaviPoolConfig;
import com.cuckoo.framework.navi.utils.ServerUrlUtil.ServerUrl;

public class NaviMysqlDriver extends NaviJdbcDriver {

    public NaviMysqlDriver(ServerUrl server, String auth,
                           NaviPoolConfig poolConfig) {
        super(server, auth, poolConfig);
    }

    @Override
    protected void initConnection(String url, String auth,
                                  NaviPoolConfig poolConfig) {
        StringBuffer urlb = new StringBuffer(url);
        boolean nopara = false;
        if (!urlb.toString().contains("?")) {
            urlb.append("?");
            nopara = true;
        }
        if (!urlb.toString().contains("connectTimeout")) {
            if (nopara) {
                urlb.append("&");
            }
            urlb.append("connectTimeout=").append(poolConfig.getConnectTimeout());
        }
        if (!urlb.toString().contains("socketTimeout")) {
            urlb.append("&socketTimeout=").append(poolConfig.getSocketTimeout());
        }
        super.initConnection(urlb.toString(), auth, poolConfig);
    }


    @Override
    void loadDriver() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
    }

}
