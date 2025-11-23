#!/bin/bash

# 测试脚本 - 检查Spring Boot应用是否正常运行
# 使用方法: ./test.sh [端口号，默认8080]

PORT=${1:-8080}
BASE_URL="http://localhost:${PORT}"
MAX_ATTEMPTS=30
ATTEMPT=0
SLEEP_INTERVAL=2

echo "=========================================="
echo "Spring Boot 应用测试脚本"
echo "=========================================="
echo "目标端口: $PORT"
echo "基础URL: $BASE_URL"
echo ""

# 检查端口是否在监听
check_port() {
    if netstat -tln 2>/dev/null | grep -q ":$PORT " || ss -tln 2>/dev/null | grep -q ":$PORT "; then
        return 0
    else
        return 1
    fi
}

# 等待应用启动
wait_for_app() {
    echo "等待应用启动..."
    while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
        if check_port; then
            echo "✓ 端口 $PORT 已开始监听"
            sleep 2  # 再等待2秒确保应用完全启动
            return 0
        fi
        ATTEMPT=$((ATTEMPT + 1))
        echo "  尝试 $ATTEMPT/$MAX_ATTEMPTS - 等待应用启动..."
        sleep $SLEEP_INTERVAL
    done
    echo "✗ 超时：应用在 $MAX_ATTEMPTS 次尝试后仍未启动"
    return 1
}

# 测试HTTP端点
test_endpoint() {
    local endpoint=$1
    local expected=$2
    local url="${BASE_URL}${endpoint}"
    
    echo ""
    echo "测试端点: $endpoint"
    echo "URL: $url"
    
    response=$(curl -s -w "\n%{http_code}" "$url" 2>/dev/null)
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" = "200" ]; then
        echo "✓ HTTP状态码: $http_code"
        echo "  响应内容: $body"
        if [ -n "$expected" ] && echo "$body" | grep -q "$expected"; then
            echo "✓ 响应内容包含预期文本: $expected"
            return 0
        elif [ -z "$expected" ]; then
            return 0
        else
            echo "✗ 响应内容不包含预期文本: $expected"
            return 1
        fi
    else
        echo "✗ HTTP状态码: $http_code (预期: 200)"
        echo "  响应内容: $body"
        return 1
    fi
}

# 主测试流程
main() {
    # 检查curl是否可用
    if ! command -v curl &> /dev/null; then
        echo "错误: 需要安装 curl 命令"
        echo "请运行: sudo apt-get install curl"
        exit 1
    fi
    
    # 等待应用启动
    if ! wait_for_app; then
        echo ""
        echo "=========================================="
        echo "测试失败: 应用未启动"
        echo "=========================================="
        exit 1
    fi
    
    echo ""
    echo "=========================================="
    echo "开始测试端点"
    echo "=========================================="
    
    # 测试根路径
    test_root=$(test_endpoint "/" "Hello")
    root_result=$?
    
    # 测试健康检查端点
    test_health=$(test_endpoint "/health" "OK")
    health_result=$?
    
    # 汇总结果
    echo ""
    echo "=========================================="
    echo "测试结果汇总"
    echo "=========================================="
    
    if [ $root_result -eq 0 ]; then
        echo "✓ 根路径 (/) 测试通过"
    else
        echo "✗ 根路径 (/) 测试失败"
    fi
    
    if [ $health_result -eq 0 ]; then
        echo "✓ 健康检查 (/health) 测试通过"
    else
        echo "✗ 健康检查 (/health) 测试失败"
    fi
    
    if [ $root_result -eq 0 ] && [ $health_result -eq 0 ]; then
        echo ""
        echo "=========================================="
        echo "✓ 所有测试通过！"
        echo "=========================================="
        exit 0
    else
        echo ""
        echo "=========================================="
        echo "✗ 部分测试失败"
        echo "=========================================="
        exit 1
    fi
}

# 运行主函数
main

