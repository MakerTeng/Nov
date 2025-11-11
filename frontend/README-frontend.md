# 抖音类运营前端（Vue 3 + Vite）

该前端工程基于仓库中的 Spring Cloud Alibaba 微服务后端搭建，所有接口统一通过网关 `http://localhost:8080` 访问，实现运营管理、内容分析与推荐数据可视化。

## 目录结构
```
frontend/
  src/
    api/            # Axios 实例与 user/video/log/recommend 请求封装
    store/          # Pinia 状态（auth、app）
    router/         # 路由与角色守卫
    views/          # 页面（登录、仪表盘、用户/视频/日志/推荐/个人中心等）
    components/     # 布局与通用组件
    utils/          # 角色菜单等辅助工具
```

## 后端接口映射
- `/api/user/login`、`/api/user/profile`、`/api/user/menu`、`/api/user/list`（仅管理员） → `user-service`（参见 `user-service/src/.../UserController.java`）。
- `/api/video/list`、`/api/video/mylist`、`/api/video/{id}`、`/api/video/{id}/like`、`/api/video/upload`、`/api/video/batch` → `video-service/src/.../VideoController.java`。
- `/api/log/collect`、`/api/log/user/{userId}`、`/api/log/video/{videoId}`（仅管理员） → `log-service/src/.../LogController.java`。
- `/api/recommend/list`、`/api/recommend/admin/{userId}`（仅管理员） → `recommend-service/src/.../RecommendationController.java`。

前端所有 Axios 请求均指向上述端点并由网关统一转发。

## 安装与运行
1. 安装依赖
   ```bash
   cd frontend
   npm install
   ```
2. 启动本地开发服务器
   ```bash
   npm run dev
   ```
   默认访问地址为 `http://localhost:5173`。

## 后端启动顺序（建议）
1. 运行 `start-infra.bat` 启动 Nacos + Seata。
2. 启动数据库、MQ（如果有）。
3. 启动各微服务模块（user/video/log/recommend）以及 gateway（监听 8080）。
4. 最后启动本前端（Vite）。

> ⚠️ 所有前端请求都会通过 `http://localhost:8080` 的网关中转，禁止绕过网关直接访问微服务端口，以保证鉴权与注册中心可用。
