# 实时音视频架构（WebRTC、SFU/MCU架构）

## 学习日期
2026年3月22日

## 1. WebRTC 核心概念

### 1.1 WebRTC 架构组成
WebRTC（Web Real-Time Communication）提供浏览器/移动端的实时音视频通信能力，主要由以下组件构成：

**核心接口：**
- `RTCPeerConnection`: 管理对等连接
- `RTCDataChannel`: 双向数据通道
- `RTCSessionDescription`: 会话描述协议（SDP）
- `RTCIceCandidate`: ICE候选地址

**媒体处理：**
- `MediaStream`: 媒体流（音视频轨道）
- `MediaStreamTrack`: 媒体轨道
- `getUserMedia()`: 获取本地媒体设备

### 1.2 连接建立流程
1. **信令交换**：通过信令服务器交换SDP和ICE候选
2. **ICE收集**：收集本地和远程ICE候选地址
3. **连接建立**：建立P2P连接
4. **媒体协商**：协商音视频编解码参数

## 2. WebRTC 协议栈

### 2.1 STUN/TURN/ICE
- **STUN（Session Traversal Utilities for NAT）**: 发现公网地址和NAT类型
- **TURN（Traversal Using Relays around NAT）**: NAT穿透失败时的中继方案
- **ICE（Interactive Connectivity Establishment）**: 自动选择最佳连接路径

### 2.2 SDP（Session Description Protocol）
- 描述会话的多媒体内容（编解码器、分辨率、格式等）
- 标准格式：UTF-8文本，每行以字母开头（如m-line媒体描述）

## 3. 多方视频会议架构

### 3.1 SFU（Selective Forwarding Unit）架构
**核心特点：**
- 每个客户端发送单流到SFU
- SFU根据接收者条件选择合适流转发
- 支持Simulcast和SVC两种编码方式
- 典型应用：Zoom、Google Meet

**SFU优势：**
1. **带宽友好**：客户端仅上传单流
2. **灵活转发**：SFU选择合适流发送给接收者
3. **编码适配**：支持多分辨率、比特率转发

### 3.2 MCU（Multipoint Control Unit）架构
**核心特点：**
- 每个客户端发送单流到MCU
- MCU解码所有流，重新编码生成复合流
- 典型应用：传统视频会议系统

**MCU优势：**
1. **终端兼容性好**：客户端只需解码单一复合流
2. **带宽优化**：MCU统一编码复合流
3. **处理集中**：支持复杂转码和混音

**MCU劣势：**
1. **延迟较高**：需解码、混合、再编码
2. **服务器负载重**：编解码计算压力大
3. **成本高**：硬件编码器成本较高

## 4. Simulcast vs SVC（Scalable Video Coding）

### 4.1 Simulcast（同时编码）
**工作原理：**
- 同时编码多个分辨率的视频流
- 典型配置：720p、360p、180p三档
- SFU根据网络条件选择合适流转发

**优势：**
- 实现简单，兼容性好
- 切换速度快，响应及时
- 支持现有编解码器（VP8/VP9/H.264）

**劣势：**
- 带宽浪费（同时上传多流）
- 编码冗余度高

### 4.2 SVC（可扩展视频编码）
**工作原理：**
- 单流内包含多层（Base Layer + Enhancement Layers）
- 支持时域（帧率）、空域（分辨率）、质量可扩展
- SFU可选择性转发部分层

**优势：**
- 带宽效率高（单流多层）
- 动态适应性强
- 支持渐进式增强

**劣势：**
- 实现复杂
- 编码效率略低于单层编码
- 浏览器支持度较低

## 5. 依赖描述符（Dependency Descriptor）

### 5.1 DD RTP头部扩展
- **标准**：RFC 8832（WebRTC数据通道协议）
- **特点**：编解码器无关，支持E2EE场景
- **功能**：描述帧间依赖关系，支持SFU选择转发

### 5.2 浏览器支持
- Chrome 89+：支持DD扩展
- Firefox 136+：支持DD扩展
- Safari：正在开发中

## 6. WebRTC 编解码器支持

### 6.1 视频编解码器
**必选编解码器：**
- **VP8**: 开源，WebRTC标准要求
- **H.264**: 通用兼容性好，硬件加速支持好

**可选编解码器：**
- **VP9**: 更好的压缩效率，需要更多计算资源
- **AV1**: 下一代视频编码，压缩效率最高

### 6.2 音频编解码器
**必选编解码器：**
- **Opus**: 主流选择，支持6-510kbps动态调整
- **G.711 PCM**: 传统电话标准

## 7. IM系统实时音视频设计

### 7.1 架构选择
**中小规模（<1000并发）：**
- 直接P2P WebRTC
- 使用STUN/TURN服务商（Twilio、Agora）

**中大规模（1000-10000并发）：**
- SFU架构
- 自建TURN服务器
- 边缘节点部署

**大规模（>10000并发）：**
- 分布式SFU集群
- 全球负载均衡
- 混合CDN架构

### 7.2 技术选型建议
**开源SFU方案：**
- **Jitsi**: 完整开源视频会议方案
- **mediasoup**: 高性能WebRTC SFU
- **Pion**: Go语言WebRTC实现

**商业服务：**
- **Twilio Programmable Video**: 稳定可靠，生态完整
- **Agora**: 中国本土化好，延迟低
- **Zoom Video SDK**: Zoom技术栈

### 7.3 实时音视频功能设计
**基本功能：**
1. **一对一视频通话**
   - 信令服务器 + STUN/TURN
   - 媒体协商 + ICE连接

2. **多方视频会议**
   - SFU架构
   - 发言者检测 + 智能布局

3. **屏幕共享**
   - `getDisplayMedia()` API
   - 分辨率/帧率优化

4. **实时白板**
   - `RTCDataChannel`传输绘图数据
   - 协同编辑 + 版本同步

**高级功能：**
1. **虚拟背景**: AI背景分割
2. **美颜滤镜**: 实时图像处理
3. **录制回放**: SFU录制 + 云端存储
4. **实时字幕**: ASR语音转文字

## 8. 性能优化策略

### 8.1 网络适应性
- **带宽估计**: REMB/REMB + TMMBR
- **拥塞控制**: Google Congestion Control（GCC）
- **丢包恢复**: NACK、FEC、Retransmission

### 8.2 编码优化
- **分辨率自适应**: 根据网络条件动态调整
- **帧率控制**: 动态调整帧率平衡流畅度
- **码率控制**: CBR/VBR自适应码率

### 8.3 移动端优化
- **硬件编码**: 优先使用硬件编解码器
- **功耗管理**: 动态调整CPU使用率
- **网络切换**: 支持Wi-Fi/4G/5G无缝切换

## 9. 监控与调试

### 9.1 关键指标
**连接质量：**
- ICE连接状态、RTT延迟、丢包率
- 带宽估计、抖动缓冲深度

**媒体质量：**
- 视频分辨率/帧率/码率
- 音频码率/丢包率/延迟

**用户体验：**
- 连接成功率、首帧时间
- 卡顿率、画面质量评分

### 9.2 调试工具
- **Chrome://webrtc-internals**: Chrome原生调试工具
- **WebRTC Stats**: 实时统计数据API
- **Wireshark**: 网络包分析

## 10. 安全考虑

### 10.1 端到端加密
- **DTLS-SRTP**: WebRTC默认加密方案
- **E2EE扩展**: 支持端到端加密信令

### 10.2 访问控制
- **身份验证**: JWT/OAuth2.0
- **权限管理**: 通话权限、录制权限
- **内容审核**: 实时内容检测

## 11. 部署架构

### 11.1 边缘SFU部署
```
全球用户
   ↓
GSLB（Anycast DNS）
   ↓
边缘SFU节点（东京、新加坡、法兰克福、硅谷）
   ↓
中心信令服务器
   ↓
媒体存储/CDN
```

### 11.2 混合云架构
- **公有云**: SFU节点部署
- **私有云**: 信令/用户数据
- **CDN**: 静态资源分发

## 12. 未来趋势

### 12.1 WebTransport
- 基于QUIC协议
- 更低的延迟，更好的拥塞控制
- 替代WebRTC数据通道

### 12.2 WebCodecs API
- 低级别编解码器API
- 更灵活的媒体处理
- 自定义编码管道

### 12.3 AI增强
- **智能降噪**: AI语音增强
- **超分辨率**: AI画质提升
- **自动构图**: AI画面布局优化

---

## 参考资料
1. [WebRTC官方文档](https://webrtc.org/)
2. [MDN WebRTC API](https://developer.mozilla.org/en-US/docs/Web/API/WebRTC_API)
3. [RFC 8825: WebRTC概述](https://datatracker.ietf.org/doc/rfc8825/)
4. [Jitsi架构文档](https://jitsi.org/architecture/)
5. [mediasoup文档](https://mediasoup.org/documentation/)

---

**下一步学习方向：**
1. 消息推送服务架构（APNs/FCM/厂商推送）
2. 分布式数据库架构（TiDB/CockroachDB）
3. 边缘计算与CDN集成
4. 微信/Telegram音视频架构分析