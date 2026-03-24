#!/bin/bash

# 更新 roadmap 文件中 #129-#137 功能的状态
ROADMAP_FILE="C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\roadmap.md"

# 创建临时文件
TEMP_FILE=$(mktemp)

# 读取原文件并替换状态
awk '
BEGIN { in_transfer_range = 0 }
/^## 🆕 功能 #12[0-9]/ { in_transfer_range = 1 }
/^## 🆕 功能 #/ && !/^## 🆕 功能 #12[0-9]/ { in_transfer_range = 0 }
/\*\*状态\*\*: / && in_transfer_range == 1 { 
    sub(/\*\*状态\*\*: 待开发/, "**状态**: ✅ 已转移到开发计划")
}
{ print }
' "$ROADMAP_FILE" > "$TEMP_FILE"

# 替换原文件
mv "$TEMP_FILE" "$ROADMAP_FILE"

echo "已更新 roadmap 中 #129-#137 功能的状态为'已转移到开发计划'"