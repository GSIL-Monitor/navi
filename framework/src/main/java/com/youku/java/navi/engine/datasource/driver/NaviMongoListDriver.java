package com.youku.java.navi.engine.datasource.driver;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import com.youku.java.navi.common.NaviError;
import com.youku.java.navi.common.ServerUrlUtil;
import com.youku.java.navi.common.exception.NaviSystemException;
import com.youku.java.navi.engine.datasource.pool.NaviMongoPoolConfig;
import com.youku.java.navi.engine.datasource.pool.NaviPoolConfig;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class NaviMongoListDriver extends AbstractNaviDriver {

    private final int SLEEPTIME = 60000;// 1min
    private final int MAX_CLEARCOUNT = 3;

    @Setter
    private Mongo mongo;

    public NaviMongoListDriver(ServerUrlUtil.ServerUrl server, String auth, NaviPoolConfig poolConfig) throws NumberFormatException, MongoException, UnknownHostException {
        super(server, auth, poolConfig);

        String masterUrl = null;
        if (server.getHost() != null && server.getPort() != 0)
            masterUrl = server.getHost() + ":" + server.getPort();
        List<ServerAddress> addresslist = new ArrayList<>();
        // 找到master
        List<String> listHostPorts = new ArrayList<>();
        String[] hostPorts = server.getUrl().split(",");
        Collections.addAll(listHostPorts, hostPorts);
        for (int i = 0; i < listHostPorts.size(); i++) {
            if (listHostPorts.get(0).equals(masterUrl))
                break;
            listHostPorts.add(listHostPorts.remove(0));
        }
        for (String hostPort : listHostPorts) {
            addresslist.add(new ServerAddress(hostPort));
        }

        mongo = new Mongo(addresslist, getMongoOptions(poolConfig));
        // mongo.setReadPreference(ReadPreference.SECONDARY);

        startIdleConnCheck();
    }

    private MongoOptions getMongoOptions(NaviPoolConfig poolConfig) {
        if (poolConfig instanceof NaviMongoPoolConfig) {
            return ((NaviMongoPoolConfig) poolConfig).getOptions();
        }

        MongoOptions options = new MongoOptions();
        options.connectionsPerHost = poolConfig.getMaxActive();
        options.connectTimeout = poolConfig.getConnectTimeout();
        options.safe = true;

        return options;
    }

    private void startIdleConnCheck() {
        new MongoIdleCleaner().start();
    }

    public void destroy() throws NaviSystemException {
        close();
    }

    @Override
    public void close() throws NaviSystemException {
        try {
            mongo.close();
            log.info("mongos instance is destoried!");
        } catch (Exception e) {
            log.warn("mongos instance is destoried failly!");
        }

        setClose(true);
    }

    public Mongo getMongo() {
        if (isClose()) {
            throw new NaviSystemException("the driver has been closed!",
                NaviError.SYSERROR);
        }
        /*
        try {
			log.info("get mongo is open:" + mongo.getConnector().isOpen());
			// int count = mongo.getConnector().getDBPortPool(new
			// ServerAddress(server.getHost(), server.getPort())).available();
			// log.info("get mongo available:" + count);
			log.info("getConnectPoint:"
					+ mongo.getConnector().getConnectPoint());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// UnknownHostException
			e.printStackTrace();
		}
		*/
        return mongo;
    }

    public boolean isAlive() throws NaviSystemException {
        // mongo..getConnector();
        return mongo.getConnector().isOpen();
    }

    /**
     * 在热部署点，因为并发的问题，会出现该driver实例已被关闭而仍还有线程调用该实例连接
     * 数据库，导致连接泄露，所以开此线程，在driver关闭情况下仍在1min内重复关闭2次，已确保 连接确实均被关闭
     */
    class MongoIdleCleaner extends Thread {
        private int initCount = 0;

        public MongoIdleCleaner() {
            setDaemon(true);
            setName("MongoIdleCleaner" + hashCode());
        }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(SLEEPTIME);
                } catch (InterruptedException ignored) {

                }
                try {
                    if (isClose()) {
                        initCount++;
                        close();
                    }
                } catch (Exception ignored) {
                }

                if (initCount > MAX_CLEARCOUNT) {
                    break;
                }
            }
        }

    }

    public boolean open() {
        return false;
    }

    public void afterPropertiesSet() throws Exception {

    }

    public Mongo getDriver() {
        return getMongo();
    }

    public Mongo getDB() {
        return getMongo();
    }

}