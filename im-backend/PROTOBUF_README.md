# IM 协议缓冲 (Protobuf) Schema

## 概述
本模块定义了即时通讯系统的 gRPC API 接口协议，采用 Proto3 IDL 定义，支持 grpc-gateway 提供 RESTful API。

## 目录结构
```
src/main/proto/
├── im/
│   ├── common.proto           # 通用类型和枚举
│   ├── message.proto          # 消息相关接口
│   ├── user.proto             # 用户管理接口
│   ├── group.proto            # 群组管理接口
│   ├── auth.proto             # 认证授权接口
│   └── websocket.proto        # WebSocket 事件接口
└── google/
    └── api/
        └── annotations.proto  # grpc-gateway 注解
```

## 编译配置
- 使用 `protobuf-maven-plugin` 进行 Protobuf 编译
- 生成 Java、TypeScript、Dart 客户端代码
- 支持 grpc-gateway 生成 RESTful API