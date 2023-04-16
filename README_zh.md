# 在线五子棋

[English](README.md)

## 简介

在线五子棋 - 哈尔滨工业大学（深圳）(HITSZ) 分布式系统课程项目 1， 2023 年春季

请参阅 `docs/` 目录下的文档：

- [setup-and-run-the-project](docs/setup-and-run-the-project.md): 详细的项目设置指南。
- [documentation.md](docs/documentation.md): 项目文档。
- [documentation-appendix.md](docs/documentation-appendix.md): 文档的附录（包括演示）。
- [workflow.md](docs/workflow.md): 工作流程的草稿设计。
- [debugging-notes.md](docs/debugging-notes.md): 调试笔记。

## 技术栈

| 技术栈                                                | 描述                                                                                               |
| ----------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| [Vue.js](https://vuejs.org/)                          | A progressive JavaScript framework used for building user interfaces and single-page applications. |
| [Spring Boot](https://spring.io/projects/spring-boot) | A popular Java-based open-source framework used for building web applications.                     |

<!-- | [Mybatis](https://mybatis.org/mybatis-3/)             | A persistence framework with support for custom SQL, stored procedures and advanced mappings. | -->
<!-- | [MySQL](https://www.mysql.com/)                       | A relational database management system.                                                      | -->

### 如何运行

前端是一个 `Vue.js` 项目，通过 `npm run build` 打包产生的 `dist` 目录可以由 `nginx` 这种服务器加载，或者你可以使用 `npm` 在本地运行它，如下所示：

```sh
cd gomoku-online-frontend/  # 切换到前端的工作目录下
npm install  # 安装项目依赖
npm run dev  # 编译，并且热重载以便于开发
```

后端是一个 `Spring Boot` 项目，以 `maven` 来管理依赖，所以你可以通过以下命令手动构建它：

```sh
cd gomoku-online-backend/  # 切换到后端的工作目录下
mvn install  # 安装必要的依赖并且尝试打包项目
mvn clean package  # 为后端项目构建一个可执行的 jar 文件
```

或者你也可以直接运行已经发布的 `jar` 文件，需要注意的是你需要替换 `<version>` 为确切的 `jar` 文件版本号（例如 `0.1.0`）。

```sh
java -jar gomoku-online-<version>.jar # 直接运行发布的 jar 文件
```

## 参考资料

- 参考自这个在线五子棋游戏的功能：[Play Gomoku Online](https://gomokuonline.com/)
