#!/usr/bin/env python3
"""
脚本模板 - 用于创建新的Python工作流脚本

设计原则：
1. 结构清晰，易于理解和维护
2. 完善的错误处理和日志记录
3. 可配置的参数和选项
4. 输出结构化结果（JSON优先）
"""

import os
import sys
import json
import argparse
import logging
from datetime import datetime
from typing import Dict, List, Any, Optional

# 设置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


class WorkflowScript:
    """
    工作流脚本基类模板
    
    所有新的工作流脚本应该继承此类或参考此模板
    """
    
    def __init__(self, args: argparse.Namespace):
        """
        初始化脚本
        
        Args:
            args: 命令行参数
        """
        self.args = args
        self.script_name = os.path.basename(__file__)
        self.start_time = datetime.now()
        
        logger.info(f"启动脚本: {self.script_name}")
        logger.info(f"参数: {vars(args)}")
        logger.info(f"开始时间: {self.start_time}")
    
    def validate_inputs(self) -> bool:
        """
        验证输入参数
        
        Returns:
            bool: 输入是否有效
        """
        try:
            # 检查必需的参数
            if not self.args.input_path:
                logger.error("必须指定输入路径")
                return False
            
            # 检查路径是否存在
            if not os.path.exists(self.args.input_path):
                logger.error(f"输入路径不存在: {self.args.input_path}")
                return False
            
            # 检查输出目录
            if self.args.output_dir:
                os.makedirs(self.args.output_dir, exist_ok=True)
            
            logger.info("输入验证通过")
            return True
            
        except Exception as e:
            logger.error(f"输入验证失败: {e}")
            return False
    
    def execute_main_task(self) -> Dict[str, Any]:
        """
        执行主要任务
        
        Returns:
            Dict: 任务执行结果
        """
        try:
            logger.info("开始执行主任务")
            
            # 这里是具体的任务逻辑
            # 应该根据实际需求重写这个方法
            
            result = {
                "status": "success",
                "processed_files": 0,
                "output_files": [],
                "summary": "任务执行完成"
            }
            
            logger.info(f"主任务完成: {result['summary']}")
            return result
            
        except Exception as e:
            logger.error(f"主任务执行失败: {e}")
            return {
                "status": "error",
                "error": str(e),
                "summary": f"任务执行失败: {e}"
            }
    
    def save_results(self, result: Dict[str, Any]) -> bool:
        """
        保存执行结果
        
        Args:
            result: 执行结果
            
        Returns:
            bool: 保存是否成功
        """
        try:
            # 添加元数据
            result["metadata"] = {
                "script": self.script_name,
                "start_time": self.start_time.isoformat(),
                "end_time": datetime.now().isoformat(),
                "duration_seconds": (datetime.now() - self.start_time).total_seconds(),
                "args": vars(self.args)
            }
            
            # 保存JSON结果
            if self.args.output_file:
                with open(self.args.output_file, 'w', encoding='utf-8') as f:
                    json.dump(result, f, ensure_ascii=False, indent=2)
                logger.info(f"JSON结果已保存到: {self.args.output_file}")
            
            # 保存文本摘要
            if self.args.summary_file:
                summary = self.generate_text_summary(result)
                with open(self.args.summary_file, 'w', encoding='utf-8') as f:
                    f.write(summary)
                logger.info(f"文本摘要已保存到: {self.args.summary_file}")
            
            # 控制台输出
            if self.args.verbose:
                print(json.dumps(result, ensure_ascii=False, indent=2))
            
            return True
            
        except Exception as e:
            logger.error(f"保存结果失败: {e}")
            return False
    
    def generate_text_summary(self, result: Dict[str, Any]) -> str:
        """
        生成文本摘要
        
        Args:
            result: 执行结果
            
        Returns:
            str: 格式化文本摘要
        """
        summary_lines = [
            "=" * 60,
            f"工作流脚本执行摘要",
            "=" * 60,
            f"脚本: {self.script_name}",
            f"执行时间: {result['metadata']['start_time']}",
            f"持续时间: {result['metadata']['duration_seconds']:.2f}秒",
            "",
            "执行结果:",
            f"状态: {result['status']}",
            f"处理文件数: {result.get('processed_files', 0)}",
        ]
        
        if result['status'] == 'success':
            summary_lines.extend([
                "",
                "输出文件:",
            ])
            for file in result.get('output_files', []):
                summary_lines.append(f"- {file}")
        elif result['status'] == 'error':
            summary_lines.extend([
                "",
                "错误信息:",
                f"{result.get('error', '未知错误')}",
            ])
        
        summary_lines.extend([
            "",
            "=" * 60,
        ])
        
        return "\n".join(summary_lines)
    
    def run(self) -> int:
        """
        运行脚本的主要流程
        
        Returns:
            int: 退出码 (0=成功, 1=失败)
        """
        try:
            # 1. 验证输入
            if not self.validate_inputs():
                return 1
            
            # 2. 执行主任务
            result = self.execute_main_task()
            
            # 3. 保存结果
            if not self.save_results(result):
                return 1
            
            # 4. 输出摘要
            if not self.args.quiet:
                print(self.generate_text_summary(result))
            
            logger.info(f"脚本执行完成: {self.script_name}")
            
            # 根据执行状态返回退出码
            return 0 if result['status'] == 'success' else 1
            
        except KeyboardInterrupt:
            logger.warning("用户中断执行")
            return 130  # 标准中断退出码
        except Exception as e:
            logger.error(f"脚本执行异常: {e}")
            return 1


def parse_args():
    """
    解析命令行参数
    
    Returns:
        argparse.Namespace: 解析后的参数
    """
    parser = argparse.ArgumentParser(
        description='工作流脚本模板',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  %(prog)s --input ./data --output-dir ./results
  %(prog)s --input ./file.txt --output-file results.json --verbose
        """
    )
    
    # 必需参数
    parser.add_argument(
        '--input', '-i',
        dest='input_path',
        required=True,
        help='输入文件或目录路径'
    )
    
    # 可选参数
    parser.add_argument(
        '--output-dir', '-o',
        dest='output_dir',
        default='./output',
        help='输出目录 (默认: ./output)'
    )
    
    parser.add_argument(
        '--output-file',
        dest='output_file',
        help='JSON输出文件路径'
    )
    
    parser.add_argument(
        '--summary-file',
        dest='summary_file',
        default='summary.txt',
        help='文本摘要文件路径 (默认: summary.txt)'
    )
    
    # 功能开关
    parser.add_argument(
        '--verbose', '-v',
        action='store_true',
        help='详细输出模式'
    )
    
    parser.add_argument(
        '--quiet', '-q',
        action='store_true',
        help='安静模式，不输出摘要'
    )
    
    # 其他参数
    parser.add_argument(
        '--config',
        dest='config_file',
        help='配置文件路径'
    )
    
    return parser.parse_args()


def main():
    """主函数"""
    # 解析参数
    args = parse_args()
    
    # 创建脚本实例
    script = WorkflowScript(args)
    
    # 运行脚本
    exit_code = script.run()
    
    # 退出
    sys.exit(exit_code)


if __name__ == '__main__':
    main()