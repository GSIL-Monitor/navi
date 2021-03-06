package com.youku.java.navi.server.module;

import com.youku.java.navi.boot.NaviJarClassLoader;
import lombok.Getter;
import lombok.Setter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 加载模块资源
 */
public class NaviModuleClassLoader extends NaviJarClassLoader {

    @Getter @Setter
    private String moduleNm;

    public NaviModuleClassLoader(ClassLoader parenter, String moduleNm)
        throws FileNotFoundException, MalformedURLException {
        super(parenter, NaviModulesUtil.getModuleLibsPath(moduleNm));
        this.moduleNm = moduleNm;
        loadJarFiles();
    }

    /**
     * 无缓存加载jar资源
     */
    public InputStream getResourceAsStream(String name) {
        URL url = getResource(name);
        if (url == null) {
            return null;
        }

        try {
            URLConnection uc = url.openConnection();
            uc.setUseCaches(false);
            return uc.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
}
