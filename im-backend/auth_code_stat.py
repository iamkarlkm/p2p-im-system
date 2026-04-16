#!/usr/bin/env python3
"""
im-service-auth 代码统计脚本
"""

import os
import json
from pathlib import Path

def count_lines_in_file(filepath):
    """统计单个文件的行数"""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            lines = f.readlines()
            total = len(lines)
            code = 0
            comment = 0
            blank = 0
            
            for line in lines:
                stripped = line.strip()
                if not stripped:
                    blank += 1
                elif stripped.startswith('//'):
                    comment += 1
                elif stripped.startswith('/*') or stripped.startswith('*'):
                    comment += 1
                elif stripped.startswith('*/'):
                    comment += 1
                else:
                    code += 1
            
            return {
                'file': os.path.basename(filepath),
                'total': total,
                'code': code,
                'comment': comment,
                'blank': blank
            }
    except Exception as e:
        print(f"Error reading {filepath}: {e}")
        return None

def main():
    base_path = Path("C:/Users/Administrator/.openclaw/workspace-clawd3/multi_agent/projects/im-modular/im-service-auth/src/main/java/com/im/service/auth")
    
    # 统计的文件列表
    files_to_count = [
        "security/JwtTokenProvider.java",
        "security/JwtAuthenticationFilter.java",
        "security/CustomUserDetailsService.java",
        "security/TokenBlacklistService.java",
        "config/SecurityConfig.java",
        "service/AuthService.java",
        "controller/AuthController.java",
        "entity/RefreshToken.java",
        "repository/RefreshTokenRepository.java",
        "dto/LoginRequest.java",
        "dto/LoginResponse.java",
        "dto/RegisterRequest.java",
        "dto/RegisterResponse.java",
        "dto/TokenRefreshRequest.java",
        "dto/TokenRefreshResponse.java",
        "dto/ForgotPasswordRequest.java",
        "dto/ResetPasswordRequest.java",
        "dto/TokenVerifyRequest.java",
        "dto/TokenVerifyResponse.java",
        "dto/ApiResponse.java",
    ]
    
    results = []
    total_total = 0
    total_code = 0
    total_comment = 0
    total_blank = 0
    
    print("=" * 80)
    print("im-service-auth 代码统计报告")
    print("=" * 80)
    print()
    print(f"{'文件名':<35} {'总行':>8} {'代码':>8} {'注释':>8} {'空行':>8}")
    print("-" * 80)
    
    for file_path in files_to_count:
        full_path = base_path / file_path
        if full_path.exists():
            stats = count_lines_in_file(str(full_path))
            if stats:
                results.append(stats)
                total_total += stats['total']
                total_code += stats['code']
                total_comment += stats['comment']
                total_blank += stats['blank']
                print(f"{stats['file']:<35} {stats['total']:>8} {stats['code']:>8} {stats['comment']:>8} {stats['blank']:>8}")
        else:
            print(f"{os.path.basename(file_path):<35} {'文件不存在':>32}")
    
    print("-" * 80)
    print(f"{'总计':<35} {total_total:>8} {total_code:>8} {total_comment:>8} {total_blank:>8}")
    print("=" * 80)
    print()
    print(f"总文件数: {len(results)}")
    print(f"总行数: {total_total}")
    print(f"代码行数: {total_code} ({total_code/total_total*100:.1f}%)")
    print(f"注释行数: {total_comment} ({total_comment/total_total*100:.1f}%)")
    print(f"空行数: {total_blank} ({total_blank/total_total*100:.1f}%)")
    
    # 保存到JSON
    report = {
        'module': 'im-service-auth',
        'date': '2026-04-07',
        'files': results,
        'summary': {
            'total_files': len(results),
            'total_lines': total_total,
            'code_lines': total_code,
            'comment_lines': total_comment,
            'blank_lines': total_blank,
            'code_percentage': round(total_code/total_total*100, 1),
            'comment_percentage': round(total_comment/total_total*100, 1),
            'blank_percentage': round(total_blank/total_total*100, 1)
        }
    }
    
    output_path = Path("C:/Users/Administrator/.openclaw/workspace-clawd3/multi_agent/projects/im-modular/im-service-auth-code-stats.json")
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(report, f, indent=2, ensure_ascii=False)
    
    print()
    print(f"统计报告已保存到: {output_path}")

if __name__ == '__main__':
    main()
