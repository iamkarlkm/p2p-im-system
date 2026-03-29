package com.im.miniprogram.model;

import java.util.*;

/**
 * 小程序清单（配置）模型
 */
public class MiniProgramManifest {
    
    private String appId;
    private String name;
    private String version;
    private String description;
    private String entryPath;
    private String packagePath;
    
    // 页面配置
    private List<String> pages = new ArrayList<>();
    private String rootPage = "pages/index/index";
    
    // TabBar配置
    private TabBarConfig tabBar;
    
    // 窗口配置
    private WindowConfig window;
    
    // 网络配置
    private NetworkConfig network;
    
    // 权限配置
    private PermissionConfig permission;
    
    // 依赖配置
    private Map<String, String> dependencies = new HashMap<>();
    
    // 编译配置
    private CompileConfig compile;
    
    // 云开发配置
    private CloudConfig cloud;
    
    // 插件配置
    private Map<String, PluginConfig> plugins = new HashMap<>();
    
    // 预加载规则
    private List<PreloadRule> preloadRules = new ArrayList<>();
    
    // 分包配置
    private List<SubPackage> subPackages = new ArrayList<>();
    
    // 运行配置
    private RuntimeConfig runtime;
    
    public MiniProgramManifest() {
        this.window = new WindowConfig();
        this.network = new NetworkConfig();
        this.permission = new PermissionConfig();
        this.compile = new CompileConfig();
        this.runtime = new RuntimeConfig();
    }
    
    // ============ Getter/Setter ============
    
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getEntryPath() { 
        return entryPath != null ? entryPath : rootPage; 
    }
    public void setEntryPath(String entryPath) { this.entryPath = entryPath; }
    
    public String getPackagePath() { return packagePath; }
    public void setPackagePath(String packagePath) { this.packagePath = packagePath; }
    
    public List<String> getPages() { return pages; }
    public void setPages(List<String> pages) { this.pages = pages; }
    public void addPage(String page) { this.pages.add(page); }
    
    public String getRootPage() { return rootPage; }
    public void setRootPage(String rootPage) { this.rootPage = rootPage; }
    
    public TabBarConfig getTabBar() { return tabBar; }
    public void setTabBar(TabBarConfig tabBar) { this.tabBar = tabBar; }
    
    public WindowConfig getWindow() { return window; }
    public void setWindow(WindowConfig window) { this.window = window; }
    
    public NetworkConfig getNetwork() { return network; }
    public void setNetwork(NetworkConfig network) { this.network = network; }
    
    public PermissionConfig getPermission() { return permission; }
    public void setPermission(PermissionConfig permission) { this.permission = permission; }
    
    public Map<String, String> getDependencies() { return dependencies; }
    public void setDependencies(Map<String, String> dependencies) { this.dependencies = dependencies; }
    
    public CompileConfig getCompile() { return compile; }
    public void setCompile(CompileConfig compile) { this.compile = compile; }
    
    public CloudConfig getCloud() { return cloud; }
    public void setCloud(CloudConfig cloud) { this.cloud = cloud; }
    
    public Map<String, PluginConfig> getPlugins() { return plugins; }
    public void setPlugins(Map<String, PluginConfig> plugins) { this.plugins = plugins; }
    
    public List<PreloadRule> getPreloadRules() { return preloadRules; }
    public void setPreloadRules(List<PreloadRule> preloadRules) { this.preloadRules = preloadRules; }
    
    public List<SubPackage> getSubPackages() { return subPackages; }
    public void setSubPackages(List<SubPackage> subPackages) { this.subPackages = subPackages; }
    
    public RuntimeConfig getRuntime() { return runtime; }
    public void setRuntime(RuntimeConfig runtime) { this.runtime = runtime; }
    
    // ============ 内部配置类 ============
    
    /**
     * TabBar配置
     */
    public static class TabBarConfig {
        private String color = "#999999";
        private String selectedColor = "#333333";
        private String backgroundColor = "#ffffff";
        private String borderStyle = "black";
        private List<TabItem> list = new ArrayList<>();
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        
        public String getSelectedColor() { return selectedColor; }
        public void setSelectedColor(String selectedColor) { this.selectedColor = selectedColor; }
        
        public String getBackgroundColor() { return backgroundColor; }
        public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }
        
        public String getBorderStyle() { return borderStyle; }
        public void setBorderStyle(String borderStyle) { this.borderStyle = borderStyle; }
        
        public List<TabItem> getList() { return list; }
        public void setList(List<TabItem> list) { this.list = list; }
    }
    
    public static class TabItem {
        private String pagePath;
        private String text;
        private String iconPath;
        private String selectedIconPath;
        
        public String getPagePath() { return pagePath; }
        public void setPagePath(String pagePath) { this.pagePath = pagePath; }
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public String getIconPath() { return iconPath; }
        public void setIconPath(String iconPath) { this.iconPath = iconPath; }
        
        public String getSelectedIconPath() { return selectedIconPath; }
        public void setSelectedIconPath(String selectedIconPath) { this.selectedIconPath = selectedIconPath; }
    }
    
    /**
     * 窗口配置
     */
    public static class WindowConfig {
        private String navigationBarTitleText = "";
        private String navigationBarTextStyle = "black";
        private String navigationBarBackgroundColor = "#ffffff";
        private boolean navigationStyle = false;
        private String backgroundColor = "#ffffff";
        private String backgroundTextStyle = "dark";
        private boolean enablePullDownRefresh = false;
        private int onReachBottomDistance = 50;
        
        public String getNavigationBarTitleText() { return navigationBarTitleText; }
        public void setNavigationBarTitleText(String navigationBarTitleText) { 
            this.navigationBarTitleText = navigationBarTitleText; 
        }
        
        public String getNavigationBarTextStyle() { return navigationBarTextStyle; }
        public void setNavigationBarTextStyle(String navigationBarTextStyle) { 
            this.navigationBarTextStyle = navigationBarTextStyle; 
        }
        
        public String getNavigationBarBackgroundColor() { return navigationBarBackgroundColor; }
        public void setNavigationBarBackgroundColor(String navigationBarBackgroundColor) { 
            this.navigationBarBackgroundColor = navigationBarBackgroundColor; 
        }
        
        public boolean isNavigationStyle() { return navigationStyle; }
        public void setNavigationStyle(boolean navigationStyle) { this.navigationStyle = navigationStyle; }
        
        public String getBackgroundColor() { return backgroundColor; }
        public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }
        
        public String getBackgroundTextStyle() { return backgroundTextStyle; }
        public void setBackgroundTextStyle(String backgroundTextStyle) { 
            this.backgroundTextStyle = backgroundTextStyle; 
        }
        
        public boolean isEnablePullDownRefresh() { return enablePullDownRefresh; }
        public void setEnablePullDownRefresh(boolean enablePullDownRefresh) { 
            this.enablePullDownRefresh = enablePullDownRefresh; 
        }
        
        public int getOnReachBottomDistance() { return onReachBottomDistance; }
        public void setOnReachBottomDistance(int onReachBottomDistance) { 
            this.onReachBottomDistance = onReachBottomDistance; 
        }
    }
    
    /**
     * 网络配置
     */
    public static class NetworkConfig {
        private List<String> requestDomain = new ArrayList<>();
        private List<String> uploadDomain = new ArrayList<>();
        private List<String> downloadDomain = new ArrayList<>();
        private List<String> socketDomain = new ArrayList<>();
        private boolean debug = false;
        
        public List<String> getRequestDomain() { return requestDomain; }
        public void setRequestDomain(List<String> requestDomain) { this.requestDomain = requestDomain; }
        
        public List<String> getUploadDomain() { return uploadDomain; }
        public void setUploadDomain(List<String> uploadDomain) { this.uploadDomain = uploadDomain; }
        
        public List<String> getDownloadDomain() { return downloadDomain; }
        public void setDownloadDomain(List<String> downloadDomain) { this.downloadDomain = downloadDomain; }
        
        public List<String> getSocketDomain() { return socketDomain; }
        public void setSocketDomain(List<String> socketDomain) { this.socketDomain = socketDomain; }
        
        public boolean isDebug() { return debug; }
        public void setDebug(boolean debug) { this.debug = debug; }
    }
    
    /**
     * 权限配置
     */
    public static class PermissionConfig {
        private Map<String, PermissionScope> scopes = new HashMap<>();
        
        public Map<String, PermissionScope> getScopes() { return scopes; }
        public void setScopes(Map<String, PermissionScope> scopes) { this.scopes = scopes; }
        
        public void addScope(String name, String desc) {
            PermissionScope scope = new PermissionScope();
            scope.setDesc(desc);
            scopes.put(name, scope);
        }
    }
    
    public static class PermissionScope {
        private String desc;
        
        public String getDesc() { return desc; }
        public void setDesc(String desc) { this.desc = desc; }
    }
    
    /**
     * 编译配置
     */
    public static class CompileConfig {
        private List<String> babelSetting = new ArrayList<>();
        private boolean es6 = true;
        private boolean enhance = true;
        private boolean postcss = true;
        private boolean minified = true;
        private boolean uglifyFileName = false;
        
        public List<String> getBabelSetting() { return babelSetting; }
        public void setBabelSetting(List<String> babelSetting) { this.babelSetting = babelSetting; }
        
        public boolean isEs6() { return es6; }
        public void setEs6(boolean es6) { this.es6 = es6; }
        
        public boolean isEnhance() { return enhance; }
        public void setEnhance(boolean enhance) { this.enhance = enhance; }
        
        public boolean isPostcss() { return postcss; }
        public void setPostcss(boolean postcss) { this.postcss = postcss; }
        
        public boolean isMinified() { return minified; }
        public void setMinified(boolean minified) { this.minified = minified; }
        
        public boolean isUglifyFileName() { return uglifyFileName; }
        public void setUglifyFileName(boolean uglifyFileName) { this.uglifyFileName = uglifyFileName; }
    }
    
    /**
     * 云开发配置
     */
    public static class CloudConfig {
        private String root = "./cloud/";
        
        public String getRoot() { return root; }
        public void setRoot(String root) { this.root = root; }
    }
    
    /**
     * 插件配置
     */
    public static class PluginConfig {
        private String version;
        private String provider;
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
    }
    
    /**
     * 预加载规则
     */
    public static class PreloadRule {
        private String path;
        private List<String> packages = new ArrayList<>();
        private boolean network = true;
        
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public List<String> getPackages() { return packages; }
        public void setPackages(List<String> packages) { this.packages = packages; }
        
        public boolean isNetwork() { return network; }
        public void setNetwork(boolean network) { this.network = network; }
    }
    
    /**
     * 分包配置
     */
    public static class SubPackage {
        private String root;
        private List<String> pages = new ArrayList<>();
        private String name;
        private boolean independent = false;
        
        public String getRoot() { return root; }
        public void setRoot(String root) { this.root = root; }
        
        public List<String> getPages() { return pages; }
        public void setPages(List<String> pages) { this.pages = pages; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public boolean isIndependent() { return independent; }
        public void setIndependent(boolean independent) { this.independent = independent; }
    }
    
    /**
     * 运行时配置
     */
    public static class RuntimeConfig {
        private long maxMemory = 256 * 1024 * 1024; // 256MB
        private int maxThreads = 20;
        private long maxExecutionTime = 5 * 60 * 1000; // 5分钟
        private long idleTimeout = 30 * 60 * 1000; // 30分钟
        private boolean debug = false;
        
        public long getMaxMemory() { return maxMemory; }
        public void setMaxMemory(long maxMemory) { this.maxMemory = maxMemory; }
        
        public int getMaxThreads() { return maxThreads; }
        public void setMaxThreads(int maxThreads) { this.maxThreads = maxThreads; }
        
        public long getMaxExecutionTime() { return maxExecutionTime; }
        public void setMaxExecutionTime(long maxExecutionTime) { this.maxExecutionTime = maxExecutionTime; }
        
        public long getIdleTimeout() { return idleTimeout; }
        public void setIdleTimeout(long idleTimeout) { this.idleTimeout = idleTimeout; }
        
        public boolean isDebug() { return debug; }
        public void setDebug(boolean debug) { this.debug = debug; }
    }
}
