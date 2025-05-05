# 数据库本地配置方法（MacBook M3）

## 安装 Homebrew
首先需要安装 Homebrew，它是 Mac 上一款强大的包管理工具，通过它可以方便地安装 MySQL。如果尚未安装 Homebrew，可以在终端中执行相关安装命令。

## 安装 MySQL
在终端中输入以下命令来安装 MySQL：
```bash
brew install mysql
```

## 启动 MySQL 服务
安装完成后，使用以下命令启动 MySQL 服务：
```bash
brew services start mysql
```

## 停止 MySQL 服务
若需要停止 MySQL 服务，可在终端中执行：
```bash
brew services stop mysql
```

## 重启 MySQL 服务
当需要重启 MySQL 服务时，运行以下命令：
```bash
brew services restart mysql
```

## 初次启动设置 root 密码
首次启动 MySQL 后，需要设置 root 用户的密码，在终端中输入：
```bash
mysql_secure_installation
```
在密码配置时，选择简单模式，设置密码为 `12345678`，然后一路选择 `YES` 完成配置。

## 验证安装
在终端中输入以下命令，验证 MySQL 是否安装成功：
```bash
mysql -u root -p
```
输入上述命令后，会提示输入密码，输入设置的 `12345678` 密码后，若出现类似以下内容，则表示安装成功：
```
(base) panghu@panghudeMacBook-Air ~ % mysql -u root -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 13
Server version: 9.3.0 Homebrew

Copyright (c) 2000, 2018, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.
```

## 使用 Navicat Premium 连接
打开 Navicat Premium，选择连接 MySQL 数据库，设置连接参数：
- 主机名/地址：`localhost`
- 端口：`3306`
按照上述步骤配置完成后，即可通过 Navicat Premium 对 MySQL 数据库进行操作和管理。 