package com.yy.yec.global.manager;

import android.app.Activity;

import java.util.Stack;

/**
 * activity维护，方便操作
 *
 * @author zzq
 * @created 2016年10月30日
 */
public class AppActivityManager {
    private static Stack<Activity> activityStack;
    private static AppActivityManager _this;

    private AppActivityManager() {
    }

    public static AppActivityManager getInstance() {
        if (_this == null)
            synchronized (AppActivityManager.class) {
                _this = new AppActivityManager();
            }

        if (activityStack == null)
            synchronized (AppActivityManager.class) {
                activityStack = new Stack<Activity>();
            }
        return _this;
    }

    /**
     * 从堆栈获取Activity
     */
    public Activity getActivity(Class<?> cls) {
        if (activityStack != null)
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（栈顶的一定是当前的）
     */
    public Activity getCurrentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity
     */
    public void finishCurrentActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && activityStack.contains(activity)) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 移除指定的Activity（在调用activity自己的Destroy中调用）
     */
    public void removeActivity(Activity activity) {
        if (activity != null && activityStack.contains(activity)) {
            activityStack.remove(activity);
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                finishActivity(activityStack.get(i));
            }
        }
        activityStack.clear();
    }
}
