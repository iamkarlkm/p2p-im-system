#!/bin/bash
# im-modular 测试环境启动脚本
# 使用方法: ./start-test-env.sh [start|stop|restart|status|logs]

COMPOSE_FILE="docker-compose.test.yml"

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

print_green() {
    echo -e "${GREEN}$1${NC}"
}

print_yellow() {
    echo -e "${YELLOW}$1${NC}"
}

print_red() {
    echo -e "${RED}$1${NC}"
}

start_env() {
    print_yellow "正在启动 im-modular 测试环境..."
    
    # 检查 Docker 是否运行
    if ! docker info > /dev/null 2>&1; then
        print_red "错误: Docker 未运行，请先启动 Docker"
        exit 1
    fi
    
    # 检查 docker-compose 是否安装
    if ! command -v docker-compose &> /dev/null; then
        print_red "错误: docker-compose 未安装"
        exit 1
    fi
    
    # 创建必要的目录
    mkdir -p mysql-data redis-data minio-data
    
    # 启动服务
    docker-compose -f $COMPOSE_FILE up -d
    
    if [ $? -eq 0 ]; then
        print_green "测试环境启动成功!"
        echo ""
        echo "服务访问地址:"
        echo "  MySQL:     localhost:3307 (user: im_test / password: test123)"
        echo "  Redis:     localhost:6380"
        echo "  MailHog:   http://localhost:8025 (邮件Web界面)"
        echo "  MinIO:     http://localhost:9001 (对象存储控制台)"
        echo "  phpMyAdmin: http://localhost:8081 (数据库管理)"
        echo "  Redis Commander: http://localhost:8082 (Redis管理)"
        echo ""
        print_yellow "等待服务启动中..."
        sleep 5
        
        # 检查服务状态
        check_status
    else
        print_red "启动失败，请检查日志"
        exit 1
    fi
}

stop_env() {
    print_yellow "正在停止 im-modular 测试环境..."
    docker-compose -f $COMPOSE_FILE down
    
    if [ $? -eq 0 ]; then
        print_green "测试环境已停止"
    else
        print_red "停止失败"
        exit 1
    fi
}

restart_env() {
    print_yellow "正在重启 im-modular 测试环境..."
    stop_env
    sleep 2
    start_env
}

status_env() {
    echo "im-modular 测试环境状态:"
    echo "=========================="
    docker-compose -f $COMPOSE_FILE ps
}

logs_env() {
    service=${2:-}
    if [ -z "$service" ]; then
        docker-compose -f $COMPOSE_FILE logs -f
    else
        docker-compose -f $COMPOSE_FILE logs -f $service
    fi
}

check_status() {
    print_yellow "检查服务状态..."
    
    # 检查 MySQL
    if docker-compose -f $COMPOSE_FILE exec -T mysql mysqladmin ping -h localhost &> /dev/null; then
        print_green "  MySQL: 运行正常"
    else
        print_red "  MySQL: 未就绪"
    fi
    
    # 检查 Redis
    if docker-compose -f $COMPOSE_FILE exec -T redis redis-cli ping | grep -q "PONG"; then
        print_green "  Redis: 运行正常"
    else
        print_red "  Redis: 未就绪"
    fi
    
    # 检查 MailHog
    if curl -s http://localhost:8025 > /dev/null; then
        print_green "  MailHog: 运行正常"
    else
        print_red "  MailHog: 未就绪"
    fi
    
    # 检查 MinIO
    if curl -s http://localhost:9000/minio/health/live > /dev/null; then
        print_green "  MinIO: 运行正常"
    else
        print_red "  MinIO: 未就绪"
    fi
}

# 主逻辑
case "${1:-start}" in
    start)
        start_env
        ;;
    stop)
        stop_env
        ;;
    restart)
        restart_env
        ;;
    status)
        status_env
        ;;
    logs)
        logs_env "$@"
        ;;
    check)
        check_status
        ;;
    *)
        echo "使用方法: $0 [start|stop|restart|status|logs|check]"
        echo ""
        echo "命令说明:"
        echo "  start   - 启动测试环境"
        echo "  stop    - 停止测试环境"
        echo "  restart - 重启测试环境"
        echo "  status  - 查看服务状态"
        echo "  logs    - 查看日志 (可指定服务名，如: logs mysql)"
        echo "  check   - 检查各服务是否就绪"
        exit 1
        ;;
esac
