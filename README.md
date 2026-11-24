# AI Router - Go 后端

AI 网关的 Go 实现，对外提供与 OpenAI 兼容的模型调用接口，支持多厂商模型接入、智能路由、故障转移和调用计费。

## 技术栈

| 组件    | 说明                                    |
| ------- | --------------------------------------- |
| Gin     | HTTP 框架                               |
| GORM    | ORM，MySQL 8.0                          |
| Redis   | Session 存储、响应缓存、限流、黑名单    |
| Stripe  | 在线充值                                |
| Session | gin-contrib/sessions + Redis 存储登录态 |

## 目录结构

```
go-backend/
├── cmd/server/            # 程序入口
├── internal/
│   ├── adapter/           # 模型适配器（OpenAI 兼容 / 智谱 / 默认）
│   ├── common/            # 统一返回结构
│   ├── config/            # 配置加载（.env + flag）
│   ├── controller/        # 接口层
│   ├── middleware/        # 登录校验、限流、IP 黑名单、CORS、Recovery
│   ├── model/             # 实体、DTO、VO
│   ├── repository/        # 数据访问
│   ├── router/            # 路由注册
│   ├── service/           # 业务逻辑
│   ├── strategy/          # 路由策略（auto / fixed / 成本优先 / 延迟优先）
│   └── task/              # 模型健康检查定时任务
├── .env                   # 本地配置
├── .env.prod              # 容器部署配置
└── docker-compose.yml     # MySQL + Redis + 后端 + 前端
```

## 快速开始

### 环境要求

- Go 1.24+
- MySQL 8.0+
- Redis 7+

### 1. 初始化数据库

```bash
mysql -u root -p < ../sql/create_table.sql
```

脚本会建库 `ai_router`，并写入 admin / user / test 三个初始账号（密码均为 `12345678`）。

### 2. 准备配置

仓库里已经有 `.env`，直接改就行（配置优先级：命令行参数 > 环境变量 > `.env`）。下面两项**必填**，缺了启动会直接报错退出：

```bash
MYSQL_DSN=root:123456@tcp(127.0.0.1:3306)/ai_router?charset=utf8mb4&parseTime=true&loc=Local
SESSION_SECRET=随便一串足够长的随机字符串
```

模型 Key 至少填一个，否则对话接口调不通：

```bash
AI_API_KEY=sk-your-qwen-api-key
```

### 3. 启动

```bash
go mod download
go run ./cmd/server
```

启动后访问：

| 地址                                          | 说明                    |
| --------------------------------------------- | ----------------------- |
| http://localhost:8123/api/health/             | 健康检查                |
| http://localhost:8123/api/v1/chat/completions | 对话接口（兼容 OpenAI） |

## Docker 部署

```bash
chmod +x start.sh
./start.sh
```

等价于 `docker compose up -d --build`，会一起拉起 MySQL(3307)、Redis(6379)、后端(8123) 和前端(80)。注意容器模式下环境变量读的是 `.env.prod`。

## 配置项

| 变量                      | 默认值           | 说明                                  |
| ------------------------- | ---------------- | ------------------------------------- |
| `SERVER_PORT`             | 8123             | 服务端口                              |
| `SERVER_CONTEXT_PATH`     | /api             | 接口前缀                              |
| `MYSQL_DSN`               | 无（必填）       | MySQL 连接串                          |
| `SESSION_SECRET`          | 无（必填）       | Session 签名密钥                      |
| `SESSION_NAME`            | SESSION          | Cookie 名                             |
| `SESSION_MAX_AGE_SECONDS` | 2592000          | Session 过期时间（30 天）             |
| `REDIS_ADDR`              | 127.0.0.1:6379   | Redis 地址                            |
| `REDIS_PASSWORD`          | 空               | Redis 密码                            |
| `REDIS_DB`                | 0                | Redis 库号                            |
| `AI_BASE_URL`             | 通义千问兼容地址 | 上游模型地址                          |
| `AI_MODEL`                | qwen-plus        | 默认模型                              |
| `AI_API_KEY`              | 无               | 上游模型 Key（也兼容 `QWEN_API_KEY`） |
| `AI_CACHE_ENABLED`        | true             | 是否开启响应缓存                      |
| `AI_CACHE_TTL_SECONDS`    | 300              | 缓存有效期                            |
| `STRIPE_API_KEY`          | 空               | Stripe 密钥，不充值可不填             |
| `STRIPE_WEBHOOK_SECRET`   | 空               | Stripe 回调验签密钥                   |
| `PLUGIN_SERPAPI_API_KEY`  | 空               | Web 搜索插件所需                      |
| `ENCRYPTION_SECRET_KEY`   | 空               | BYOK 用户密钥加密用                   |

所有配置也支持命令行参数覆盖，例如 `go run ./cmd/server --server-port 9000 --mysql-dsn "..."`。

## 主要接口

| 分组     | 路径                              | 说明                         |
| -------- | --------------------------------- | ---------------------------- |
| 健康     | `/api/health/`                    | 存活检查                     |
| 用户     | `/api/user/*`                     | 注册、登录、额度、启停用     |
| API Key  | `/api/api/key/*`                  | 创建、列表、撤销             |
| 模型     | `/api/model/*`、`/api/provider/*` | 模型与提供者管理             |
| 对话     | `/api/v1/chat/completions`        | 对外接口，走 API Key 鉴权    |
| 内部对话 | `/api/internal/chat/*`            | 走登录态鉴权，支持文件上传   |
| 绘图     | `/api/v1/images/generations`      | 文生图                       |
| 统计     | `/api/stats/*`                    | Token、费用、调用日志        |
| 插件     | `/api/plugin/*`                   | Web 搜索、PDF 解析、图片识别 |
| BYOK     | `/api/byok/*`                     | 用户自带 Key                 |
| 充值     | `/api/recharge/*`                 | Stripe 下单与回调            |
| 黑名单   | `/api/admin/blacklist/*`          | IP 黑名单管理                |

鉴权分两种：管理接口走 Session，对外调用接口走 `Authorization: Bearer <apiKey>`。

## 说明

- 本项目与仓库根目录的 Java 版后端共用同一套数据表，二选一部署，不要同时连一个库。
- 模型调用失败会按路由策略自动切到备用模型，健康状态由定时任务刷新，结果缓存在 Redis 里。
- 路由策略在 `internal/strategy` 下，新增一种只需实现 `RoutingStrategy` 接口并在 `cmd/server/main.go` 里注册。
