# DevNote 博客系统

我的第一个 Spring Boot 全栈项目，从零搭建博客基础功能，已完成首页展示与新建文章页面跳转。

## 🛠️ 技术栈

- **后端**：Spring Boot 3.x
- **模板引擎**：Thymeleaf
- **数据访问**：Spring Data JPA
- **数据库**：MySQL
- **前端**：HTML + Bootstrap 5
- **构建工具**：Maven
- **版本控制**：Git + Gitee

## 📦 当前功能

- [x] 首页展示文章列表（待填充数据）
- [x] 点击“写新文章”跳转到新建页面
- [x] 解决页面跳转 404 问题
- [x] 项目初始化与依赖配置
- [x] 数据库连接配置
- [x] 代码托管至 Gitee

## 🚀 如何运行

1. 克隆项目：
   ```bash
   git clone https://gitee.com/ItWjf/devnote-blog.git
2. 配置数据库： 确保已安装 MySQL
3. 创建数据库：CREATE DATABASE devnote;
修改 src/main/resources/application.properties 中的数据库连接信息
4. 启动项目：
./mvnw spring-boot:run
5. 访问应用：
http://localhost:8080
## 📝 开发说明
本项目通过 Spring Boot 提供动态页面服务，不可使用 VS Code Live Server 等静态服务器预览。
所有页面跳转必须通过 http://localhost:8080 访问，以确保后端路由正常工作。
## 🌟 后续计划
* 实现“保存文章”功能（表单提交 + 数据入库）
* 添加文章详情页
* 支持编辑与删除
* 增加创建时间显示
* 优化页面样式
## 🙌 感谢
感谢 Spring Boot 官方文档与开源社区，本项目为学习实践项目。