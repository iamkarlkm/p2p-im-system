#!/bin/bash

# 读取 roadmap.md 文件
ROADMAP_FILE="C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\roadmap.md"
DEV_PLAN_FILE="C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\development_plan.md"

# 提取功能 #129-#137 的信息
echo "正在从 roadmap 中提取功能 #129-#137..."

# 创建临时文件来存储提取的功能
TEMP_FEATURES=$(mktemp)

# 定义功能开始和结束的行号
declare -A feature_ranges=(
  ["129"]="3,87"
  ["130"]="88,176"
  ["131"]="177,263"
  ["132"]="264,359"
  ["133"]="360,435"
  ["134"]="436,512"
  ["135"]="513,589"
  ["136"]="590,667"
  ["137"]="668,753"
)

# 提取每个功能的核心信息
for feature_num in {129..137}; do
  echo "提取功能 #$feature_num..."
  
  # 获取功能的范围
  range=${feature_ranges[$feature_num]}
  start=$(echo $range | cut -d',' -f1)
  end=$(echo $range | cut -d',' -f2)
  
  # 提取功能标题和基本信息
  sed -n "${start},${end}p" "$ROADMAP_FILE" > "temp_feature_$feature_num.md"
  
  # 提取标题行（包含功能编号和名称）
  title_line=$(grep -m1 "^## 🆕 功能 #$feature_num" "temp_feature_$feature_num.md")
  if [ -z "$title_line" ]; then
    title_line=$(grep -m1 "^## 功能 #$feature_num" "temp_feature_$feature_num.md")
  fi
  
  # 提取优先级
  priority=$(grep -m1 "^**优先级**: " "temp_feature_$feature_num.md" | sed 's/\*\*优先级\*\*: //' | sed 's/🔴/高/' | sed 's/🟡/中/' | sed 's/🟢/低/')
  
  # 提取功能描述（从背景部分）
  background=$(sed -n '/^### 功能背景/,/^###/p' "temp_feature_$feature_num.md" | head -10 | grep -v "^###" | tr '\n' ' ' | sed 's/  / /g' | cut -c1-200)
  
  # 提取主要特性
  features=$(sed -n '/^### 功能特性/,/^###/p' "temp_feature_$feature_num.md" | grep "^- " | head -5 | sed 's/^- //' | tr '\n' '; ' | sed 's/; $//')
  
  # 确定模块
  modules="im-backend"
  if grep -q "Desktop (TypeScript)" "temp_feature_$feature_num.md"; then
    modules="$modules, im-desktop"
  fi
  if grep -q "Mobile (Flutter)" "temp_feature_$feature_num.md"; then
    modules="$modules, im-mobile"
  fi
  
  # 格式化输出
  echo "### $feature_num. $(echo "$title_line" | sed 's/^## 🆕 功能 #[0-9]*: //' | sed 's/ ⭐ 新增//')" >> "$TEMP_FEATURES"
  echo "- **功能描述**: $background" >> "$TEMP_FEATURES"
  echo "- **模块**: $modules" >> "$TEMP_FEATURES"
  echo "- **优先级**: $priority" >> "$TEMP_FEATURES"
  echo "- **状态**: 📋 待开发" >> "$TEMP_FEATURES"
  echo "- **主要特性**: $features" >> "$TEMP_FEATURES"
  echo "" >> "$TEMP_FEATURES"
  
  # 清理临时文件
  rm "temp_feature_$feature_num.md"
done

echo "功能提取完成，共提取了9个功能"
echo "功能列表已保存到临时文件: $TEMP_FEATURES"
cat "$TEMP_FEATURES"