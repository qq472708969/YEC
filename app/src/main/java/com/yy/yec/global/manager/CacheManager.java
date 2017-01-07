package com.yy.yec.global.manager;

import com.yy.yec.global.App;
import com.yy.yec.utils.DeviceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CacheManager {
    // wifi缓存时间为3分钟
    private static long wifiCacheTime = 3 * 60 * 1000;
    // 其他网络环境为30分钟
    private static long otherCacheTime = 30 * 60 * 1000;
    //app 全局 自定义缓存文件夹
    public static final String app_global_cache = "global_cache";
    //app 列表 自定义缓存文件夹
    public static final String app_list_cache = "list_cache";
    //app 详细页面 自定义缓存文件夹
    public static final String app_detail_cache = "detail_cache";

    /**
     * 保存对象
     */
    public static synchronized boolean saveObjectData(Serializable ser, String fileName, String cacheType) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(new File(App.packgeDir(cacheType), fileName)); //向外部文件输出
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 读取对象，执行缓存过期失效策略
     */
    public static synchronized Serializable readObjectData(String fileName, String cacheType) {
        return readObjectData(fileName, cacheType, false, false);
    }

    /**
     * 选择读取缓存方式
     */
    public static synchronized Serializable readObjectData(String fileName, String cacheType, boolean alwaysReadCacheNoNet) {
        return readObjectData(fileName, cacheType, false, alwaysReadCacheNoNet);
    }

    /**
     * 始终读取缓存
     */
    public static synchronized Serializable alwaysReadObjectData(String fileName, String cacheType) {
        return readObjectData(fileName, cacheType, true, true);
    }

    /**
     * 读取缓存对象
     *
     * @param fileName             文件名称
     * @param cacheType            类型
     * @param alwaysReadCache      true：始终读取，false：始终不读取（false时第四个参数才有效）
     * @param alwaysReadCacheNoNet true：无网始终读取，有网始终不读取，false：执行缓存策略
     * @return
     */
    public static synchronized Serializable readObjectData(String fileName, String cacheType, boolean alwaysReadCache, boolean alwaysReadCacheNoNet) {
        File file = new File(App.packgeDir(cacheType), fileName);
        if (!alwaysReadCache && !cacheDataEffective(fileName, file, alwaysReadCacheNoNet))//是否允许缓存过期失效
            return null;
        else if (!file.exists()) return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(file);//从外部文件向内部程序读取
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof InvalidClassException)
                file.delete();//反序列化失败 - 删除缓存文件
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static synchronized void clear(String cacheType) {
        clear(cacheType, null);
    }

    public static synchronized void clear(String cacheType, String fileNameInclude) {
        File dir = App.packgeDir(cacheType);
        if (dir != null && dir.exists() && dir.isDirectory()) {
            for (File item : dir.listFiles()) {
                if (fileNameInclude.equals(null)) {
                    item.delete();
                    continue;
                }
                if (item.getName().indexOf(fileNameInclude) > -1)
                    item.delete();
            }
        }
    }

    /**
     * 判断缓存是否已经失效（包括判断是否存在）
     *
     * @return true为有效，false为无效
     * @alwaysReadCache 有网络始终不读取缓存，无网络始终读取缓存
     */
    private static boolean cacheDataEffective(String fileName, File file, boolean alwaysReadCacheNoNet) {
        if (!file.exists()) return false;
        if (DeviceUtil.getNetworkType() == 0) return true;//没网缓存始终有效
        if (!alwaysReadCacheNoNet) {
            long existTime = System.currentTimeMillis() - file.lastModified();//long代表毫秒数
            long time;
            if (DeviceUtil.getNetworkType() == DeviceUtil.NETTYPE_WIFI)
                time = wifiCacheTime;
            else
                time = otherCacheTime;
            return time > existTime ? true : false;
        } else
            return false;
    }

    public static String getCachePageFullTag(String tag, String pageIndex) {//获取具体的每页缓存数据标记
        StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append("_");//3下划线
        sb.append(pageIndex);//首页为""
        return sb.toString();
    }
}