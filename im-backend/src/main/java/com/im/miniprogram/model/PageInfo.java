package com.im.miniprogram.model;

import java.util.*;

/**
 * 页面信息
 */
public class PageInfo {
    
    private String route;
    private Map<String, Object> loadParams;
    private Map<String, Object> query;
    private String pageId;
    
    // 页面状态
    private boolean loaded = false;
    private boolean shown = false;
    private boolean ready = false;
    
    // 滚动位置
    private int scrollTop = 0;
    private int scrollHeight = 0;
    
    // 停留时间统计
    private long totalStayTime = 0;
    private List<Long> stayTimes = new ArrayList<>();
    
    public PageInfo(String route, Map<String, Object> params) {
        this.route = route;
        this.loadParams = params != null ? new HashMap<>(params) : new HashMap<>();
        this.query = new HashMap<>();
        this.pageId = UUID.randomUUID().toString().substring(0, 8);
        
        // 解析query参数
        parseQuery();
    }
    
    private void parseQuery() {
        if (route != null && route.contains("?")) {
            int idx = route.indexOf("?");
            String queryString = route.substring(idx + 1);
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    query.put(kv[0], kv[1]);
                }
            }
            route = route.substring(0, idx);
        }
    }
    
    // ============ Getter/Setter ============
    
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    
    public Map<String, Object> getLoadParams() { return loadParams; }
    public void setLoadParams(Map<String, Object> loadParams) { this.loadParams = loadParams; }
    
    public Map<String, Object> getQuery() { return query; }
    public void setQuery(Map<String, Object> query) { this.query = query; }
    
    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }
    
    public boolean isLoaded() { return loaded; }
    public void setLoaded(boolean loaded) { this.loaded = loaded; }
    
    public boolean isShown() { return shown; }
    public void setShown(boolean shown) { this.shown = shown; }
    
    public boolean isReady() { return ready; }
    public void setReady(boolean ready) { this.ready = ready; }
    
    public int getScrollTop() { return scrollTop; }
    public void setScrollTop(int scrollTop) { this.scrollTop = scrollTop; }
    
    public int getScrollHeight() { return scrollHeight; }
    public void setScrollHeight(int scrollHeight) { this.scrollHeight = scrollHeight; }
    
    public void setScrollPosition(int scrollTop, int scrollHeight) {
        this.scrollTop = scrollTop;
        this.scrollHeight = scrollHeight;
    }
    
    public long getTotalStayTime() { return totalStayTime; }
    
    public void addStayTime(long time) {
        stayTimes.add(time);
        totalStayTime += time;
    }
    
    public List<Long> getStayTimes() { return new ArrayList<>(stayTimes); }
    
    public long getAverageStayTime() {
        if (stayTimes.isEmpty()) return 0;
        return totalStayTime / stayTimes.size();
    }
    
    @Override
    public String toString() {
        return "PageInfo{" +
            "route='" + route + '\'' +
            ", pageId='" + pageId + '\'' +
            ", loaded=" + loaded +
            ", shown=" + shown +
            ", ready=" + ready +
            '}';
    }
}
