package com.youku.java.navi.server;

import com.youku.java.navi.boot.NaviDefine;
import com.youku.java.navi.server.module.NaviModuleContextFactory;
import com.youku.java.navi.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import java.net.InetSocketAddress;
import java.rmi.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public abstract class ANaviPacketServer extends ANaviServer {

    private ConnectionlessBootstrap bootstrap;
    protected DefaultChannelGroup channelGroup = new DefaultChannelGroup("navi-channels");
    protected Timer timer = new HashedWheelTimer();

    @Override
    protected boolean preStartServer(Properties serverCfg) {
        // 设置为server共享信息
        ServerConfigure.setServerCfg(serverCfg);
        log.info("prepared for starting server successfully!");
        log.info("the server work mode is " + ServerConfigure.getWorkMode() + ".");
        return true;
    }

    @Override
    protected int startServer() {
        log.info("starting listening the port!");
        try {
            ExecutorService executor = Executors.newCachedThreadPool();

            bootstrap = new ConnectionlessBootstrap(new NioDatagramChannelFactory(executor));
            bootstrap.setPipelineFactory(getPipelineFactory());
            configBootstrap(bootstrap);

            if (StringUtils.isNotEmpty(ServerConfigure.getPort())) {
                bootstrap.bind(new InetSocketAddress(Integer.parseInt(ServerConfigure.getPort())));
            } else {
                throw new UnknownHostException("the server port hasn't been setted");
            }
        } catch (RuntimeException e) {
            log.error("the server starting is fail!" + e.getMessage());
            return FAIL;
        } catch (Exception e) {
            log.error("the server starting is fail!" + e.getMessage());
            return FAIL;
        }

        return SUCCESS;
    }

    public void stopServer() {
        if (bootstrap != null) {
            bootstrap.getFactory().releaseExternalResources();
        }
    }

    public abstract ChannelPipelineFactory getPipelineFactory();

    @Override
    protected void postStartServer() {
        // 初始化ModuleFactory，检测模块版本
        NaviModuleContextFactory.getInstance().startCheckModuleProccess();
        log.info("the providing server is " + ServerConfigure.getServer() + ",the listening port is " + ServerConfigure.getPort() + ".");
        log.info("the server has been started successfully!");
    }

    private void configBootstrap(ConnectionlessBootstrap bootstrap) {
        if (ServerConfigure.containsKey(NaviDefine.BACKLOG)) {
            bootstrap.setOption(NaviDefine.BACKLOG, ServerConfigure.get(NaviDefine.BACKLOG));
        }

        if (ServerConfigure.containsKey(NaviDefine.REUSEADDRESS)) {
            bootstrap.setOption(NaviDefine.REUSEADDRESS, Boolean.valueOf(ServerConfigure.get(NaviDefine.REUSEADDRESS)));
        }

        if (ServerConfigure.containsKey(NaviDefine.CHILD_KEEPALIVE)) {
            bootstrap.setOption(NaviDefine.CHILD_KEEPALIVE, Boolean.valueOf(ServerConfigure.get(NaviDefine.CHILD_KEEPALIVE)));
        }

        if (ServerConfigure.containsKey(NaviDefine.CHILD_TCPNODELAY)) {
            bootstrap.setOption(NaviDefine.CHILD_TCPNODELAY, Boolean.valueOf(ServerConfigure.get(NaviDefine.CHILD_TCPNODELAY)));
        }
        if (ServerConfigure.containsKey(ServerConfigure.CHILD_SENDBUFFERSIZE)) {
            bootstrap.setOption(ServerConfigure.CHILD_SENDBUFFERSIZE, Integer.valueOf(ServerConfigure.get(ServerConfigure.CHILD_SENDBUFFERSIZE)));
        }
        if (ServerConfigure.containsKey(ServerConfigure.CHILD_RECEIVEBUFFERSIZE)) {
            bootstrap.setOption(ServerConfigure.CHILD_RECEIVEBUFFERSIZE, Integer.valueOf(ServerConfigure.get(ServerConfigure.CHILD_RECEIVEBUFFERSIZE)));
        }
        if (ServerConfigure.containsKey(ServerConfigure.WRITEBUFFERHIGHWATERMARK)) {
            bootstrap.setOption(ServerConfigure.WRITEBUFFERHIGHWATERMARK, Integer.valueOf(ServerConfigure.get(ServerConfigure.WRITEBUFFERHIGHWATERMARK)));
        }
        if (ServerConfigure.containsKey(ServerConfigure.WRITEBUFFERLOWWARTERMARK)) {
            bootstrap.setOption(ServerConfigure.WRITEBUFFERLOWWARTERMARK, Integer.valueOf(ServerConfigure.get(ServerConfigure.WRITEBUFFERLOWWARTERMARK)));
        }
    }

    @Override
    protected NaviServerType getServerType() {
        return NaviServerType.NettyServer;
    }

    protected int getChildChannelIdleTime() {
        try {
            return Integer.valueOf(ServerConfigure.get(NaviDefine.CHILD_CHANNEL_IDLTIME));
        } catch (Exception e) {
            return 0;
        }
    }
}
