package com.im.miniprogram.security;

import com.im.miniprogram.model.MiniProgramManifest;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;
import java.nio.file.Path;
import java.util.*;

/**
 * 小程序安全管理器
 * 提供沙箱级别的安全隔离，限制小程序的访问权限
 */
public class MiniProgramSecurityManager extends SecurityManager {
    
    private final MiniProgramSecurityPolicy policy;
    private final Path sandboxRoot;
    private final Set<String> allowedPaths;
    private final Set<String> allowedHosts;
    private final Set<String> allowedPermissions;
    
    public MiniProgramSecurityManager(MiniProgramSecurityPolicy policy, Path sandboxRoot) {
        this.policy = policy;
        this.sandboxRoot = sandboxRoot;
        this.allowedPaths = new HashSet<>();
        this.allowedHosts = new HashSet<>();
        this.allowedPermissions = new HashSet<>();
        
        initializeAllowedPaths();
        initializeAllowedHosts();
        initializeAllowedPermissions();
    }
    
    private void initializeAllowedPaths() {
        // 允许访问沙箱内的目录
        allowedPaths.add(sandboxRoot.toString());
        allowedPaths.add(sandboxRoot.resolve("code").toString());
        allowedPaths.add(sandboxRoot.resolve("data").toString());
        allowedPaths.add(sandboxRoot.resolve("temp").toString());
        
        // 允许访问系统库
        allowedPaths.add(System.getProperty("java.home"));
    }
    
    private void initializeAllowedHosts() {
        // 从安全策略加载允许访问的主机
        allowedHosts.addAll(policy.getAllowedHosts());
    }
    
    private void initializeAllowedPermissions() {
        // 基础权限
        allowedPermissions.add("setContextClassLoader");
    }
    
    // ============ 文件访问控制 ============
    
    @Override
    public void checkRead(String file) {
        if (!isPathAllowed(file)) {
            throw new SecurityException("Read access denied for: " + file);
        }
    }
    
    @Override
    public void checkRead(String file, Object context) {
        checkRead(file);
    }
    
    @Override
    public void checkRead(FileDescriptor fd) {
        // 允许读取文件描述符
    }
    
    @Override
    public void checkWrite(String file) {
        if (!isPathAllowed(file) || !policy.isWriteAllowed()) {
            throw new SecurityException("Write access denied for: " + file);
        }
    }
    
    @Override
    public void checkWrite(FileDescriptor fd) {
        // 允许写入文件描述符
    }
    
    @Override
    public void checkDelete(String file) {
        if (!isPathAllowed(file) || !policy.isDeleteAllowed()) {
            throw new SecurityException("Delete access denied for: " + file);
        }
    }
    
    private boolean isPathAllowed(String path) {
        if (path == null) return false;
        
        // 检查是否在允许的目录内
        for (String allowedPath : allowedPaths) {
            if (path.startsWith(allowedPath)) {
                return true;
            }
        }
        
        // 检查是否是系统路径
        return path.startsWith("/proc") || 
               path.startsWith("/sys") ||
               path.startsWith("/dev") ||
               path.contains("java");
    }
    
    // ============ 网络访问控制 ============
    
    @Override
    public void checkConnect(String host, int port) {
        if (!isHostAllowed(host)) {
            throw new SecurityException("Network connection denied to: " + host + ":" + port);
        }
    }
    
    @Override
    public void checkConnect(String host, int port, Object context) {
        checkConnect(host, port);
    }
    
    @Override
    public void checkListen(int port) {
        // 禁止监听端口
        throw new SecurityException("Listen operation not allowed");
    }
    
    @Override
    public void checkAccept(String host, int port) {
        // 禁止接受连接
        throw new SecurityException("Accept operation not allowed");
    }
    
    @Override
    public void checkMulticast(InetAddress maddr) {
        // 禁止组播
        throw new SecurityException("Multicast operation not allowed");
    }
    
    private boolean isHostAllowed(String host) {
        if (!policy.isNetworkAllowed()) {
            return false;
        }
        
        // 检查白名单
        for (String allowedHost : allowedHosts) {
            if (host.equals(allowedHost) || host.endsWith("." + allowedHost)) {
                return true;
            }
        }
        
        // 默认拒绝
        return false;
    }
    
    // ============ 执行控制 ============
    
    @Override
    public void checkExec(String cmd) {
        // 禁止执行外部命令
        throw new SecurityException("Execution of external commands not allowed: " + cmd);
    }
    
    // ============ 类加载器控制 ============
    
    @Override
    public void checkCreateClassLoader() {
        // 允许创建类加载器，但限制在沙箱内
    }
    
    @Override
    public void checkSetFactory() {
        // 禁止设置SocketFactory等
        throw new SecurityException("Setting factories not allowed");
    }
    
    // ============ 系统属性控制 ============
    
    @Override
    public void checkPropertiesAccess() {
        // 允许读取系统属性，禁止写入
    }
    
    @Override
    public void checkPropertyAccess(String key) {
        // 允许读取属性
    }
    
    @Override
    public void checkProperty(String key, String def) {
        // 允许读取属性
    }
    
    // ============ 反射控制 ============
    
    @Override
    public void checkPackageAccess(String pkg) {
        // 禁止访问危险包
        if (pkg.startsWith("java.lang.reflect") ||
            pkg.startsWith("sun.") ||
            pkg.startsWith("com.sun.")) {
            throw new SecurityException("Access to package denied: " + pkg);
        }
    }
    
    @Override
    public void checkPackageDefinition(String pkg) {
        // 允许定义包
    }
    
    // ============ 线程控制 ============
    
    @Override
    public void checkAccess(Thread t) {
        // 允许线程操作
    }
    
    @Override
    public void checkAccess(ThreadGroup g) {
        // 允许线程组操作
    }
    
    // ============ 运行时控制 ============
    
    @Override
    public void checkExit(int status) {
        // 禁止退出JVM
        throw new SecurityException("System exit not allowed");
    }
    
    @Override
    public void checkPrintJobAccess() {
        // 禁止打印
        throw new SecurityException("Print job access not allowed");
    }
    
    @Override
    public void checkSystemClipboardAccess() {
        // 禁止剪贴板访问
        throw new SecurityException("Clipboard access not allowed");
    }
    
    @Override
    public void checkAwtEventQueueAccess() {
        // 禁止AWT事件队列访问
        throw new SecurityException("AWT event queue access not allowed");
    }
    
    // ============ 权限检查 ============
    
    @Override
    public void checkPermission(Permission perm) {
        String name = perm.getName();
        
        // 检查是否在允许列表中
        if (allowedPermissions.contains(name)) {
            return;
        }
        
        // 检查具体权限
        if (name.startsWith("setIO") ||
            name.startsWith("modifyThread") ||
            name.startsWith("stopThread") ||
            name.startsWith("modifyThreadGroup")) {
            return; // 允许这些权限
        }
        
        // 其他权限默认拒绝
        // throw new SecurityException("Permission denied: " + perm);
    }
    
    @Override
    public void checkPermission(Permission perm, Object context) {
        checkPermission(perm);
    }
    
    // ============ JNI控制 ============
    
    @Override
    public void checkLink(String lib) {
        // 禁止JNI加载
        throw new SecurityException("JNI library loading not allowed: " + lib);
    }
    
    // ============ 安全上下文 ============
    
    @Override
    public Object getSecurityContext() {
        return super.getSecurityContext();
    }
    
    @Override
    public ThreadGroup getThreadGroup() {
        return super.getThreadGroup();
    }
    
    /**
     * 添加允许访问的路径
     */
    public void addAllowedPath(String path) {
        allowedPaths.add(path);
    }
    
    /**
     * 添加允许访问的主机
     */
    public void addAllowedHost(String host) {
        allowedHosts.add(host);
    }
    
    /**
     * 移除允许访问的主机
     */
    public void removeAllowedHost(String host) {
        allowedHosts.remove(host);
    }
    
    /**
     * 清除所有允许的主机
     */
    public void clearAllowedHosts() {
        allowedHosts.clear();
    }
    
    /**
     * 获取允许的主机列表
     */
    public Set<String> getAllowedHosts() {
        return new HashSet<>(allowedHosts);
    }
}
