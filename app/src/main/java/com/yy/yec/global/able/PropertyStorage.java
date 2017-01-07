package com.yy.yec.global.able;

import android.support.v4.util.ArrayMap;

import com.yy.yec.global.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 存储Key Value能力，需要存储KV继承此类，实现设置存储路径方法
 * Created by zzq on 2016/10/21.
 */
public abstract class PropertyStorage {
    /**
     * 指出key数组获取value
     *
     * @param keyAry
     */
    public Map<String, String> getProperties(String... keyAry) {
        Map<String, String> ppt = new ArrayMap<>();//暂时不考虑内存节省，数量并不庞大
        Properties props = getConfigFile();
        String key;
        if (props != null)
            for (int i = 0; i < keyAry.length; i++) {
                key = keyAry[i];
                ppt.put(key, props.getProperty(key));
            }
        return ppt;
    }

    /**
     * 指出单一的key获取value
     *
     * @param key
     */
    public String getProperty(String key) {
        Properties props = getConfigFile();
        return (props != null) ? props.getProperty(key) : null;
    }

    /**
     * 传入实体存储的Map
     *
     * @param kv
     */
    public void setProperties(Map<String, String> kv) {
        Properties props = getConfigFile();
        props.putAll(kv);
        setConfigFile(props);
    }

    /**
     * 设置指出的key和value配置
     *
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {
        Properties props = getConfigFile();
        props.setProperty(key, value);
        setConfigFile(props);
    }

    /**
     * 清除指出key数组的配置
     *
     * @param key
     */
    public void removeProperties(String... key) {
        Properties props = getConfigFile();
        for (String k : key)
            props.remove(k);
        setConfigFile(props);
    }

    /**
     * 清除指出key的配置
     *
     * @param key
     */
    public void removeProperty(String key) {
        Properties props = getConfigFile();
        props.remove(key);
        setConfigFile(props);
    }

    private Properties getConfigFile() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            File dir = App.packgeDir(setDir());
            fis = new FileInputStream(dir.getPath() + File.separator + setFileName());
            props.load(fis);
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return props;
    }

    private void setConfigFile(Properties p) {
        FileOutputStream fos = null;
        try {
            File dir = App.packgeDir(setDir());//没有则创建一个名为xxx的文件夹
            File conf = new File(dir, setFileName());//创建一个文件
            fos = new FileOutputStream(conf);
            p.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    protected abstract String setFileName();

    protected abstract String setDir();
}