package com.youku.java.navi.engine.core;

public interface ILengthBasedUDPService extends INaviUDPClientService {

    byte[] parseUDPPacket(String service, String module, String api, String extra, byte[] msg);

}
