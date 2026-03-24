# GraalVM Native Image 构建指南

## 环境要求

### 1. 安装 GraalVM

#### macOS
```bash
# 使用 Homebrew
brew install --cask graalvm/tap/graalvm-ce-java17

# 设置 JAVA_HOME
export JAVA_HOME=/Library/Java/JavaVirtualMachines/graalvm-ce-java17-22.3.0/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

#### Linux
```bash
# 下载 GraalVM
wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.3.0/graalvm-ce-java17-linux-amd64-22.3.0.tar.gz

# 解压
tar -xzf graalvm-ce-java17-linux-amd64-22.3.0.tar.gz
sudo mv graalvm-ce-java17-22.3.0 /opt/graalvm

# 设置环境变量
export JAVA_HOME=/opt/graalvm
export PATH=$JAVA_HOME/bin:$PATH
```

#### Windows
```powershell
# 使用 Chocolatey
choco install graalvm

# 设置环境变量
setx JAVA_HOME "C:\Program Files\Java\graalvm-ce-java17-22.3.0"
setx PATH "%JAVA_HOME%\bin;%PATH%"
```

### 2. 安装 Native Image 工具
```bash
gu install native-image
```

### 3. 验证安装
```bash
java -version
native-image --version
```

## 构建命令

### 开发环境构建（JVM 模式）
```bash
mvn clean package
```

### 生产环境构建（Native Image）
```bash
# 使用 Native  profile
mvn clean package -Pnative

# 或者直接使用 native 插件
mvn -Pnative native:compile
```

### Docker 构建 Native 镜像
```bash
# 使用 Spring Boot Maven 插件构建 Docker 镜像
mvn spring-boot:build-image -Pnative

# 或使用 Dockerfile
docker build -f Dockerfile.native -t im-backend-native:1.0.0 .
```

## 性能对比

| 指标 | JVM 模式 | Native Image |
|------|---------|--------------|
| 启动时间 | ~5-8 秒 | ~50-100ms |
| 内存占用 | ~500MB | ~50MB |
| 峰值性能 | 100% | 85-95% |
| 构建时间 | ~30 秒 | ~3-5 分钟 |
| 镜像大小 | ~200MB | ~50MB |

## 优化建议

### 1. 构建优化
```bash
# 使用并行构建
mvn -Pnative native:compile -Dnative.image.buildArgs=-J-Xmx4g

# 启用构建缓存
export GRAALVM_HOME=/path/to/graalvm
export NativeImageBuildCache=true
```

### 2. 运行时优化
```bash
# 设置最优化的运行时参数
./im-backend \
  -XX:MaxHeapPercent=50 \
  -Dspring.profiles.active=prod \
  -Dserver.port=8080
```

### 3. 镜像大小优化
```bash
# 在 pom.xml 中配置
<buildArgs>
  <arg>--no-fallback</arg>
  <arg>--omit-in-progress-bar</arg>
  <arg>-H:-SpawnIsolates</arg>
</buildArgs>
```

## 常见问题

### Q1: 构建失败 - Class Initialization Error
**解决方案**: 在 pom.xml 中添加初始化配置
```xml
<arg>--initialize-at-build-time=org.springframework</arg>
<arg>--initialize-at-run-time=org.postgresql</arg>
```

### Q2: 反射错误 - Reflection Error
**解决方案**: 生成反射配置文件
```bash
# 使用 Tracing Agent 运行应用
mvn -Pagent agent:run

# 运行集成测试以收集反射信息
# 配置文件将自动生成到 target/agent/

# 复制配置文件到资源目录
cp target/agent/reflect-config.json src/main/resources/META-INF/native-image/
```

### Q3: 资源文件缺失 - Resource Not Found
**解决方案**: 生成资源配置文件
```bash
# 资源文件会自动被 Agent 收集
# 手动添加常用资源到 resource-config.json
{
  "resources": [
    {"pattern": ".*\\.properties$"},
    {"pattern": ".*\\.xml$"},
    {"pattern": "META-INF/spring/.*"}
  ]
}
```

### Q4: 动态代理错误 - Proxy Error
**解决方案**: 生成代理配置文件
```bash
# 使用 Agent 收集代理信息
# 或使用预设配置
echo '[{"interfaces":["org.springframework.boot.SpringApplication"]}]' > proxy-config.json
```

## 测试 Native Image

### 单元测试
```bash
mvn test -Pnative
```

### 集成测试
```bash
# 启动 Native Image
./target/im-backend &
PID=$!

# 运行集成测试
mvn verify

# 停止服务
kill $PID
```

### 性能测试
```bash
# 使用 JMeter 或 wrk 进行压力测试
wrk -t12 -c400 -d30s http://localhost:8080/api/health

# 对比 JVM 和 Native 的性能差异
```

## 部署

### Kubernetes 部署
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: im-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: im-backend
  template:
    metadata:
      labels:
        app: im-backend
    spec:
      containers:
      - name: im-backend
        image: im-backend-native:1.0.0
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "64Mi"
            cpu: "250m"
          limits:
            memory: "128Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /api/health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /api/health
            port: 8080
          initialDelaySeconds: 3
          periodSeconds: 5
```

## 监控与调试

### 启用 Native Image 调试
```bash
# 构建时启用详细日志
mvn -Pnative native:compile -Dnative.image.buildArgs=-H:Log=registerResource:
```

### 运行时监控
```bash
# 使用 JMX (需要额外配置)
./im-backend -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=9090 \
  -Dcom.sun.management.jmxremote.authenticate=false
```

## 参考资料

- [GraalVM 官方文档](https://www.graalvm.org/22.3/docs/)
- [Spring Native 参考指南](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)
- [Native Image 性能调优](https://www.graalvm.org/22.3/reference-manual/native-image/guides/optimize-native-image-size/)

---
*最后更新：2026-03-22 12:15*
*功能#105: IM 后端 GraalVM 原生镜像配置完成*
