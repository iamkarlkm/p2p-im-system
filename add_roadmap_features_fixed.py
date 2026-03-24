#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从roadmap.md中提取功能并添加到development_plan.md中
"""

import os
import re
from datetime import datetime

def read_roadmap(filepath):
    """读取roadmap.md文件，提取功能模块"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 查找功能模块（以##或###开头，包含功能描述的段落）
    modules = []
    current_module = None
    
    lines = content.split('\n')
    for i, line in enumerate(lines):
        # 检测功能模块标题 (以###开头的)
        if line.startswith('### '):
            module_match = re.match(r'### (\d+\. )?(.+?)( 🆕)?$', line)
            if module_match:
                if current_module and current_module['name']:
                    modules.append(current_module)
                
                module_name = module_match.group(2)
                current_module = {
                    'name': module_name,
                    'description': '',
                    'tech_stack': '',
                    'value': ''
                }
        
        # 收集模块描述
        elif current_module:
            if line.startswith('**核心功能**：') or line.startswith('**核心功能**:'):
                # 下一行开始是功能描述
                desc_lines = []
                j = i + 1
                while j < len(lines) and not lines[j].startswith('**'):
                    if lines[j].strip():
                        desc_lines.append(lines[j].strip())
                    j += 1
                current_module['description'] = ' '.join(desc_lines)
            
            elif line.startswith('**技术栈建议**：') or line.startswith('**技术栈建议**:'):
                # 下一行开始是技术栈
                tech_lines = []
                j = i + 1
                while j < len(lines) and not lines[j].startswith('**'):
                    if lines[j].strip():
                        tech_lines.append(lines[j].strip())
                    j += 1
                current_module['tech_stack'] = ' '.join(tech_lines)
            
            elif line.startswith('**价值**：') or line.startswith('**价值**:'):
                # 下一行开始是价值描述
                value_lines = []
                j = i + 1
                while j < len(lines) and not lines[j].startswith('**') and not lines[j].startswith('#'):
                    if lines[j].strip():
                        value_lines.append(lines[j].strip())
                    j += 1
                current_module['value'] = ' '.join(value_lines)
    
    # 添加最后一个模块
    if current_module and current_module['name']:
        modules.append(current_module)
    
    return modules

def read_current_plan(filepath):
    """读取当前的development_plan，获取下一个功能编号"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 查找已完成的最高功能编号
    pattern = r'### (\d+)\. (.+?) ✅'
    matches = re.findall(pattern, content)
    
    if matches:
        last_number = max(int(match[0]) for match in matches)
        return last_number
    else:
        # 如果没找到，查找所有功能编号
        pattern = r'### (\d+)\. (.+?)'
        matches = re.findall(pattern, content)
        if matches:
            last_number = max(int(match[0]) for match in matches)
            return last_number
        else:
            return 0

def generate_feature_entry(feature_num, module):
    """生成单个功能的markdown条目"""
    now = datetime.now().strftime('%Y-%m-%d %H:%M')
    
    # 确定模块分配
    modules = []
    if '消息队列' in module['name'] or '负载均衡' in module['name']:
        modules = ['im-backend']
    elif '推送服务' in module['name']:
        modules = ['im-backend', 'im-desktop']
    elif 'AI' in module['name'] or '智能' in module['name']:
        modules = ['im-backend', 'im-desktop', 'im-mobile']
    else:
        modules = ['im-backend', 'im-desktop', 'im-mobile']
    
    entry = f"""
### {feature_num}. {module['name']} 📋
- **功能描述**: {module['description']}
- **模块**: {', '.join(modules)}
- **优先级**: {'高' if any(x in module['name'] for x in ['消息队列', '推送服务', '负载均衡']) else '中'}
- **状态**: 📋 待开发
- **技术栈**: {module['tech_stack']}
- **预期价值**: {module['value']}
- **预估复杂度**: {'高' if '消息队列' in module['name'] or 'AI' in module['name'] else '中'}
- **创建时间**: {now}
- **相关文件**:
  - **后端**: 待开发
  - **桌面端**: 待开发  
  - **移动端**: 待开发
"""
    return entry

def main():
    workspace_dir = r'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects'
    roadmap_path = os.path.join(workspace_dir, 'roadmap.md')
    devplan_path = os.path.join(workspace_dir, 'development_plan.md')
    
    print("读取roadmap:", roadmap_path)
    print("读取开发计划:", devplan_path)
    
    # 1. 读取roadmap功能模块
    modules = read_roadmap(roadmap_path)
    print(f"发现 {len(modules)} 个功能模块:")
    for i, module in enumerate(modules):
        print(f"  {i+1}. {module['name']}")
    
    if not modules:
        print("未找到功能模块")
        return
    
    # 2. 读取当前开发计划
    current_max = read_current_plan(devplan_path)
    print(f"当前最高功能编号: {current_max}")
    
    # 3. 读取开发计划内容
    with open(devplan_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 4. 找到"待开发功能"部分
    sections = content.split('---\n')
    if len(sections) < 3:
        print("开发计划格式错误")
        return
    
    # 第一部分：项目概述
    overview = sections[0]
    
    # 第二部分：已完成功能列表
    completed = sections[1]
    
    # 第三部分：开发计划正文（如果有的话）
    if len(sections) >= 3:
        main_body = sections[2]
    else:
        main_body = ""
    
    # 5. 生成新的功能条目
    new_features = []
    for i, module in enumerate(modules):
        feature_num = current_max + i + 1
        new_features.append(generate_feature_entry(feature_num, module))
    
    new_features_text = '\n'.join(new_features)
    
    # 6. 更新项目概述中的待开发功能数量
    updated_overview = re.sub(
        r'待开发功能: \d+ 个',
        f'待开发功能: {len(modules)} 个',
        overview
    )
    
    # 7. 构造新的开发计划内容
    new_content = updated_overview + '---\n' + completed + '---\n' + new_features_text
    
    # 8. 备份原文件
    backup_path = devplan_path + '.backup'
    with open(backup_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print("原文件已备份到:", backup_path)
    
    # 9. 写入新内容
    with open(devplan_path, 'w', encoding='utf-8') as f:
        f.write(new_content)
    
    print(f"成功添加 {len(modules)} 个新功能到开发计划")
    print(f"待开发功能数量更新为: {len(modules)} 个")
    
    # 10. 生成简单的报告
    report = f"""
## 从Roadmap添加的功能列表
    
已成功从roadmap.md中提取以下 {len(modules)} 个功能并添加到开发计划：
    
"""
    for i, module in enumerate(modules):
        feature_num = current_max + i + 1
        report += f"{feature_num}. **{module['name']}** - {module['description'][:100]}...\n"
    
    report_path = os.path.join(workspace_dir, 'roadmap_to_plan_report.md')
    with open(report_path, 'w', encoding='utf-8') as f:
        f.write(report)
    
    print(f"详细报告已保存到: {report_path}")

if __name__ == '__main__':
    main()