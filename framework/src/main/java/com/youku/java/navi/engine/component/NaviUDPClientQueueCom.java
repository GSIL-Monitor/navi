package com.youku.java.navi.engine.component;

import com.youku.java.navi.engine.component.NaviMQContext.MessageQueueType;
import com.youku.java.navi.engine.core.*;
import com.youku.java.navi.server.api.INaviUDPResponseHandler;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 除了需要配置父类的属性，本类只需要配置service(缓存service和UDPservice)和handler(UDP响应处理类)，<br>
 * msgType(发送消息的class),还有queueKey(缓存key)，如果不需要处理UDP响应，handler也可以不配置。<br>
 * 其他配置已提供默认值。
 */
@SuppressWarnings("rawtypes")
public class NaviUDPClientQueueCom implements InitializingBean, INaviUDPClientQueueCom {
    private IBaseDataService service;
    private String queueKey;
    private INaviUDPClientService udpService;
    private INaviUDPResponseHandler handler;
    private Serializable msgType;

    private INaviMessageQueue queue;
    private int mqType = 0;
    private int consumeRate = 3;
    private int consumeThread = 3;
    private NaviMQContext mqContext;
    private NaviMQConsumeController controller;
    private List<NaviMQConsumeTask> tasks;
    private INaviMQConsumeStrategy strategy;


    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void init() {
        getController().start();
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> void sendQueue(T msg) {
        getQueue().offer(queueKey, msg);
    }

    public INaviMQConsumeStrategy getStrategy() {
        if (null != strategy) {
            return strategy;
        } else {
            return new INaviMQConsumeStrategy() {
                public void afterPropertiesSet() throws Exception {
                }

                public void destroy() throws Exception {
                }

                public void consume(List list) {
                    if (null != list) {
                        for (Object obj : list) {
                            Serializable msg = (Serializable) obj;
                            if (null != handler) {
                                udpService.sendAndHandle(msg, handler);
                            } else {
                                udpService.send(msg);
                            }

                        }
                    }
                }

                public Class<? extends Serializable> getClassNM() {
                    return msgType.getClass();
                }
            };
        }
    }

    public void setStrategy(INaviMQConsumeStrategy strategy) {
        this.strategy = strategy;
    }

    public INaviMessageQueue getQueue() {
        if (null != queue) {
            return queue;
        } else {
            queue = new NaviMessageQueueFactory().createMQ(service, queueKey, MessageQueueType.values()[mqType]);
        }
        return queue;
    }

    public void setQueue(INaviMessageQueue queue) {
        this.queue = queue;
    }

    public String getQueueKey() {
        return queueKey;
    }

    public void setQueueKey(String queueKey) {
        this.queueKey = queueKey;
    }

    public int getMqType() {
        return mqType;
    }

    public void setMqType(int mqType) {
        this.mqType = mqType;
    }

    public int getConsumeRate() {
        return consumeRate;
    }

    public void setConsumeRate(int consumeRate) {
        this.consumeRate = consumeRate;
    }

    public int getConsumeThread() {
        return consumeThread;
    }

    public void setConsumeThread(int consumeThread) {
        this.consumeThread = consumeThread;
    }

    public IBaseDataService getService() {
        return service;
    }

    public void setService(IBaseDataService service) {
        this.service = service;
    }

    public INaviUDPResponseHandler getHandler() {
        return handler;
    }

    public void setHandler(INaviUDPResponseHandler handler) {
        this.handler = handler;
    }

    public NaviMQContext getMqContext() {
        if (null != mqContext) {
            return mqContext;
        } else {
            mqContext = new NaviMQContext();
            mqContext.setConsumeRate(consumeRate);
            mqContext.setMqType(mqType);
        }
        return mqContext;
    }

    public void setMqContext(NaviMQContext mqContext) {
        this.mqContext = mqContext;
    }

    public NaviMQConsumeController getController() {
        if (null != controller) {
            return controller;
        } else {
            controller = new NaviMQConsumeController();
            List<NaviMQConsumeTask> taskList = getTasks();
            controller.setTasks(taskList);
        }
        return controller;
    }

    public void setController(NaviMQConsumeController controller) {
        this.controller = controller;
    }

    @SuppressWarnings("unchecked")
    public List<NaviMQConsumeTask> getTasks() {
        if (null != tasks) {
            return tasks;
        } else {
            List<NaviMQConsumeTask> list = new ArrayList<NaviMQConsumeTask>();
            for (int i = 0; i < consumeThread; i++) {
                NaviMQConsumeTask task = new NaviMQConsumeTask();
                task.setService(service);
                task.setQueue(getQueue());
                task.setContext(getMqContext());
                task.setQueueKey(queueKey);
                task.setStrategy(getStrategy());
                list.add(task);
            }
            return list;
        }
    }

    public void setTasks(List<NaviMQConsumeTask> tasks) {
        this.tasks = tasks;
    }

    public Serializable getMsgType() {
        return msgType;
    }

    public void setMsgType(Serializable msgType) {
        this.msgType = msgType;
    }

    public INaviUDPClientService getUdpService() {
        return udpService;
    }

    public void setUdpService(INaviUDPClientService udpService) {
        this.udpService = udpService;
    }

}