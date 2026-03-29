package com.im.miniprogram.lifecycle;

import com.im.miniprogram.sandbox.MiniProgramSandbox;
import com.im.miniprogram.model.MiniProgramContext;
import com.im.miniprogram.model.PageInfo;

import java.util.*;
import java.util.concurrent.*;

/**
 * 小程序生命周期管理器
 * 管理小程序的启动、显示、隐藏、卸载等生命周期事件
 */
public class MiniProgramLifecycle {
    
    private final MiniProgramSandbox sandbox;
    private final List<LifecycleListener> listeners = new CopyOnWriteArrayList<>();
    private final Map<String, PageLifecycle> pageLifecycles = new ConcurrentHashMap<>();
    
    private LifecycleState currentState = LifecycleState.CREATED;
    private PageInfo currentPage;
    private final Stack<PageInfo> pageStack = new Stack<>();
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> backgroundTask;
    
    // 生命周期数据统计
    private long launchTime;
    private long firstRenderTime;
    private int pageSwitchCount = 0;
    private int backgroundCount = 0;
    private int foregroundCount = 0;
    private long totalBackgroundTime = 0;
    private long lastBackgroundTime = 0;
    
    public enum LifecycleState {
        CREATED,
        LAUNCHING,
        LAUNCHED,
        SHOWING,
        SHOWN,
        HIDING,
        HIDDEN,
        UNLOADING,
        UNLOADED,
        ERROR
    }
    
    public MiniProgramLifecycle(MiniProgramSandbox sandbox) {
        this.sandbox = sandbox;
    }
    
    /**
     * 注册生命周期监听器
     */
    public void addListener(LifecycleListener listener) {
        listeners.add(listener);
    }
    
    /**
     * 移除生命周期监听器
     */
    public void removeListener(LifecycleListener listener) {
        listeners.remove(listener);
    }
    
    // ============ 应用生命周期 ============
    
    /**
     * 小程序启动
     */
    public void onLaunch(MiniProgramContext context) {
        currentState = LifecycleState.LAUNCHING;
        launchTime = System.currentTimeMillis();
        
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.APP_LAUNCH,
            context,
            null
        );
        
        notifyListeners(event);
        
        // 启动后台保活任务
        startBackgroundKeepAlive();
        
        currentState = LifecycleState.LAUNCHED;
    }
    
    /**
     * 小程序显示（进入前台）
     */
    public void onShow() {
        currentState = LifecycleState.SHOWING;
        foregroundCount++;
        
        // 计算后台停留时间
        if (lastBackgroundTime > 0) {
            totalBackgroundTime += System.currentTimeMillis() - lastBackgroundTime;
        }
        
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.APP_SHOW,
            null,
            currentPage
        );
        
        notifyListeners(event);
        
        // 恢复页面生命周期
        if (currentPage != null) {
            PageLifecycle pageLifecycle = pageLifecycles.get(currentPage.getRoute());
            if (pageLifecycle != null) {
                pageLifecycle.onShow();
            }
        }
        
        currentState = LifecycleState.SHOWN;
    }
    
    /**
     * 小程序隐藏（进入后台）
     */
    public void onHide() {
        currentState = LifecycleState.HIDING;
        backgroundCount++;
        lastBackgroundTime = System.currentTimeMillis();
        
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.APP_HIDE,
            null,
            currentPage
        );
        
        notifyListeners(event);
        
        // 暂停页面生命周期
        if (currentPage != null) {
            PageLifecycle pageLifecycle = pageLifecycles.get(currentPage.getRoute());
            if (pageLifecycle != null) {
                pageLifecycle.onHide();
            }
        }
        
        currentState = LifecycleState.HIDDEN;
    }
    
    /**
     * 小程序出错
     */
    public void onError(String error) {
        currentState = LifecycleState.ERROR;
        
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.APP_ERROR,
            null,
            currentPage
        );
        event.setData("error", error);
        
        notifyListeners(event);
    }
    
    /**
     * 小程序页面未找到
     */
    public void onPageNotFound(String path) {
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.APP_PAGE_NOT_FOUND,
            null,
            null
        );
        event.setData("path", path);
        
        notifyListeners(event);
    }
    
    /**
     * 小程序终止
     */
    public void onTerminate() {
        currentState = LifecycleState.UNLOADING;
        
        // 停止后台保活任务
        stopBackgroundKeepAlive();
        
        // 清理所有页面
        while (!pageStack.isEmpty()) {
            PageInfo page = pageStack.pop();
            PageLifecycle pageLifecycle = pageLifecycles.remove(page.getRoute());
            if (pageLifecycle != null) {
                pageLifecycle.onUnload();
            }
        }
        
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.APP_TERMINATE,
            null,
            null
        );
        
        notifyListeners(event);
        
        // 关闭调度器
        scheduler.shutdown();
        
        currentState = LifecycleState.UNLOADED;
    }
    
    /**
     * 小程序内存警告
     */
    public void onMemoryWarning() {
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.APP_MEMORY_WARNING,
            null,
            currentPage
        );
        
        notifyListeners(event);
        
        // 触发垃圾回收建议
        suggestMemoryCleanup();
    }
    
    // ============ 页面生命周期 ============
    
    /**
     * 页面加载
     */
    public void onPageLoad(String route, Map<String, Object> params) {
        PageInfo pageInfo = new PageInfo(route, params);
        pageStack.push(pageInfo);
        currentPage = pageInfo;
        
        PageLifecycle pageLifecycle = new PageLifecycle(pageInfo);
        pageLifecycles.put(route, pageLifecycle);
        
        pageLifecycle.onLoad(params);
        
        pageSwitchCount++;
        
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.PAGE_LOAD,
            null,
            pageInfo
        );
        
        notifyListeners(event);
    }
    
    /**
     * 页面显示
     */
    public void onPageShow(String route) {
        PageLifecycle pageLifecycle = pageLifecycles.get(route);
        if (pageLifecycle != null) {
            pageLifecycle.onShow();
            currentPage = pageLifecycle.getPageInfo();
        }
        
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.PAGE_SHOW,
            null,
            currentPage
        );
        
        notifyListeners(event);
    }
    
    /**
     * 页面隐藏
     */
    public void onPageHide(String route) {
        PageLifecycle pageLifecycle = pageLifecycles.get(route);
        if (pageLifecycle != null) {
            pageLifecycle.onHide();
        }
        
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.PAGE_HIDE,
            null,
            currentPage
        );
        
        notifyListeners(event);
    }
    
    /**
     * 页面卸载
     */
    public void onPageUnload(String route) {
        PageLifecycle pageLifecycle = pageLifecycles.remove(route);
        if (pageLifecycle != null) {
            pageLifecycle.onUnload();
            
            // 从页面栈中移除
            pageStack.removeIf(p -> p.getRoute().equals(route));
            
            // 更新当前页面
            if (!pageStack.isEmpty()) {
                currentPage = pageStack.peek();
            } else {
                currentPage = null;
            }
        }
        
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.PAGE_UNLOAD,
            null,
            null
        );
        
        notifyListeners(event);
    }
    
    /**
     * 页面初次渲染完成
     */
    public void onPageReady(String route) {
        if (firstRenderTime == 0) {
            firstRenderTime = System.currentTimeMillis();
        }
        
        PageLifecycle pageLifecycle = pageLifecycles.get(route);
        if (pageLifecycle != null) {
            pageLifecycle.onReady();
        }
        
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.PAGE_READY,
            null,
            currentPage
        );
        
        notifyListeners(event);
    }
    
    /**
     * 页面滚动
     */
    public void onPageScroll(String route, int scrollTop, int scrollHeight) {
        PageLifecycle pageLifecycle = pageLifecycles.get(route);
        if (pageLifecycle != null) {
            pageLifecycle.onScroll(scrollTop, scrollHeight);
        }
    }
    
    /**
     * 页面下拉刷新
     */
    public void onPagePullDownRefresh(String route) {
        PageLifecycle pageLifecycle = pageLifecycles.get(route);
        if (pageLifecycle != null) {
            pageLifecycle.onPullDownRefresh();
        }
        
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.PAGE_PULL_DOWN_REFRESH,
            null,
            currentPage
        );
        
        notifyListeners(event);
    }
    
    /**
     * 页面上拉触底
     */
    public void onPageReachBottom(String route) {
        PageLifecycle pageLifecycle = pageLifecycles.get(route);
        if (pageLifecycle != null) {
            pageLifecycle.onReachBottom();
        }
        
        LifecycleEvent event = new LifecycleEvent(
            LifecycleEventType.PAGE_REACH_BOTTOM,
            null,
            currentPage
        );
        
        notifyListeners(event);
    }
    
    /**
     * 页面切换（返回上一个页面）
     */
    public void onPageBack() {
        if (pageStack.size() > 1) {
            // 卸载当前页面
            PageInfo current = pageStack.pop();
            PageLifecycle currentLifecycle = pageLifecycles.remove(current.getRoute());
            if (currentLifecycle != null) {
                currentLifecycle.onUnload();
            }
            
            // 显示上一个页面
            currentPage = pageStack.peek();
            PageLifecycle prevLifecycle = pageLifecycles.get(currentPage.getRoute());
            if (prevLifecycle != null) {
                prevLifecycle.onShow();
            }
        }
    }
    
    // ============ 后台保活 ============
    
    private void startBackgroundKeepAlive() {
        backgroundTask = scheduler.scheduleAtFixedRate(() -> {
            if (currentState == LifecycleState.HIDDEN) {
                // 在后台时定期发送保活信号
                LifecycleEvent event = new LifecycleEvent(
                    LifecycleEventType.APP_KEEP_ALIVE,
                    null,
                    null
                );
                notifyListeners(event);
            }
        }, 30, 30, TimeUnit.SECONDS); // 每30秒保活一次
    }
    
    private void stopBackgroundKeepAlive() {
        if (backgroundTask != null) {
            backgroundTask.cancel(false);
        }
    }
    
    // ============ 内存管理建议 ============
    
    private void suggestMemoryCleanup() {
        // 建议清理图片缓存
        // 建议释放非活跃页面资源
        // 建议触发垃圾回收
    }
    
    // ============ 事件通知 ============
    
    private void notifyListeners(LifecycleEvent event) {
        for (LifecycleListener listener : listeners) {
            try {
                listener.onLifecycleEvent(event);
            } catch (Exception e) {
                // 监听器异常不应影响生命周期继续
            }
        }
    }
    
    // ============ Getter方法 ============
    
    public LifecycleState getCurrentState() { return currentState; }
    public PageInfo getCurrentPage() { return currentPage; }
    public Stack<PageInfo> getPageStack() { return new Stack<>() {{ addAll(pageStack); }}; }
    public int getPageStackSize() { return pageStack.size(); }
    
    public long getLaunchTime() { return launchTime; }
    public long getColdStartTime() { 
        return firstRenderTime > 0 ? firstRenderTime - launchTime : 0; 
    }
    public int getPageSwitchCount() { return pageSwitchCount; }
    public int getBackgroundCount() { return backgroundCount; }
    public int getForegroundCount() { return foregroundCount; }
    public long getTotalBackgroundTime() { return totalBackgroundTime; }
    
    /**
     * 获取生命周期统计报告
     */
    public LifecycleReport generateReport() {
        return new LifecycleReport(
            launchTime,
            getColdStartTime(),
            pageSwitchCount,
            backgroundCount,
            foregroundCount,
            totalBackgroundTime,
            currentState.name(),
            currentPage != null ? currentPage.getRoute() : null,
            pageStack.size()
        );
    }
    
    // ============ 内部类 ============
    
    /**
     * 生命周期事件类型
     */
    public enum LifecycleEventType {
        APP_LAUNCH,
        APP_SHOW,
        APP_HIDE,
        APP_ERROR,
        APP_PAGE_NOT_FOUND,
        APP_TERMINATE,
        APP_MEMORY_WARNING,
        APP_KEEP_ALIVE,
        APP_THEME_CHANGE,
        
        PAGE_LOAD,
        PAGE_SHOW,
        PAGE_HIDE,
        PAGE_UNLOAD,
        PAGE_READY,
        PAGE_SCROLL,
        PAGE_PULL_DOWN_REFRESH,
        PAGE_REACH_BOTTOM,
        PAGE_SHARE,
        PAGE_TAB_ITEM_TAP
    }
    
    /**
     * 生命周期事件
     */
    public static class LifecycleEvent {
        private final LifecycleEventType type;
        private final MiniProgramContext context;
        private final PageInfo pageInfo;
        private final Map<String, Object> data = new HashMap<>();
        private final long timestamp;
        
        public LifecycleEvent(LifecycleEventType type, MiniProgramContext context, PageInfo pageInfo) {
            this.type = type;
            this.context = context;
            this.pageInfo = pageInfo;
            this.timestamp = System.currentTimeMillis();
        }
        
        public void setData(String key, Object value) {
            data.put(key, value);
        }
        
        public Object getData(String key) {
            return data.get(key);
        }
        
        public LifecycleEventType getType() { return type; }
        public MiniProgramContext getContext() { return context; }
        public PageInfo getPageInfo() { return pageInfo; }
        public Map<String, Object> getAllData() { return new HashMap<>(data); }
        public long getTimestamp() { return timestamp; }
    }
    
    /**
     * 生命周期监听器接口
     */
    public interface LifecycleListener {
        void onLifecycleEvent(LifecycleEvent event);
    }
    
    /**
     * 页面生命周期管理
     */
    private class PageLifecycle {
        private final PageInfo pageInfo;
        private long loadTime;
        private long showTime;
        private int showCount = 0;
        
        public PageLifecycle(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
        }
        
        public void onLoad(Map<String, Object> params) {
            loadTime = System.currentTimeMillis();
            pageInfo.setLoadParams(params);
        }
        
        public void onShow() {
            showTime = System.currentTimeMillis();
            showCount++;
        }
        
        public void onHide() {
            // 记录页面停留时间
            if (showTime > 0) {
                long stayTime = System.currentTimeMillis() - showTime;
                pageInfo.addStayTime(stayTime);
            }
        }
        
        public void onUnload() {
            onHide(); // 确保记录最后的停留时间
        }
        
        public void onReady() {
            // 页面初次渲染完成
        }
        
        public void onScroll(int scrollTop, int scrollHeight) {
            pageInfo.setScrollPosition(scrollTop, scrollHeight);
        }
        
        public void onPullDownRefresh() {
            // 下拉刷新
        }
        
        public void onReachBottom() {
            // 上拉触底
        }
        
        public PageInfo getPageInfo() { return pageInfo; }
    }
    
    /**
     * 生命周期报告
     */
    public static class LifecycleReport {
        public final long launchTimestamp;
        public final long coldStartDuration;
        public final int pageSwitches;
        public final int backgroundSwitches;
        public final int foregroundSwitches;
        public final long totalBackgroundDuration;
        public final String currentState;
        public final String currentRoute;
        public final int pageStackDepth;
        
        public LifecycleReport(long launchTimestamp, long coldStartDuration,
                               int pageSwitches, int backgroundSwitches,
                               int foregroundSwitches, long totalBackgroundDuration,
                               String currentState, String currentRoute, int pageStackDepth) {
            this.launchTimestamp = launchTimestamp;
            this.coldStartDuration = coldStartDuration;
            this.pageSwitches = pageSwitches;
            this.backgroundSwitches = backgroundSwitches;
            this.foregroundSwitches = foregroundSwitches;
            this.totalBackgroundDuration = totalBackgroundDuration;
            this.currentState = currentState;
            this.currentRoute = currentRoute;
            this.pageStackDepth = pageStackDepth;
        }
    }
}
