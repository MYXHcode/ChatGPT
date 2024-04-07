# 《ChatGPT 微服务应用体系构建》 - dev-ops 第4节：Nginx环境配置

## 1. 拷贝 Docker 容器中的 Nginx 配置文件到 Linux 云服务器或 Window 本地进行映射

Linux 命令：

```sh
docker container cp Nginx:/etc/nginx/nginx.conf /data/nginx/conf
docker container cp Nginx:/etc/nginx/conf.d/default.conf /data/nginx/conf/conf.d
docker container cp Nginx:/usr/share/nginx/html/index.html /data/nginx/html
```

Window 命令：

```sh
docker container cp Nginx:/etc/nginx/nginx.conf D:\CodeProjects\Java\ChatGPT\dev-ops\nginx\conf
docker container cp Nginx:/etc/nginx/conf.d/default.conf D:\CodeProjects\Java\ChatGPT\dev-ops\nginx\conf\conf.d
docker container cp Nginx:/usr/share/nginx/html/index.html D:\CodeProjects\Java\ChatGPT\dev-ops\nginx\html
```

## 2. 在 Linux 云服务器中的 Docker 容器中重新部署 Nginx，配置映射在 Linux 云服务器

Linux 命令：

```sh
docker run \
--name Nginx \
-p 80:80 \
-v /data/nginx/logs:/var/log/nginx \
-v /data/nginx/html:/usr/share/nginx/html \
-v /data/nginx/conf/nginx.conf:/etc/nginx/nginx.conf \
-v /data/nginx/conf/conf.d:/etc/nginx/conf.d \
-v /data/nginx/ssl:/etc/nginx/ssl/  \
--privileged=true -d --restart=always nginx
```

## 3. 在 Window 本地中的 Docker 容器中重新部署 Nginx，配置映射在 Window 本地

Window 命令：

```sh
docker run --name Nginx -p 80:80 -v D:\CodeProjects\Java\ChatGPT\dev-ops\nginx\logs:/var/log/nginx -v D:\CodeProjects\Java\ChatGPT\dev-ops\nginx\html:/usr/share/nginx/html -v D:\CodeProjects\Java\ChatGPT\dev-ops\nginx\conf\nginx.conf:/etc/nginx/nginx.conf -v D:\CodeProjects\Java\ChatGPT\dev-ops\nginx\conf\conf.d:/etc/nginx/conf.d -v D:\CodeProjects\Java\ChatGPT\dev-ops\nginx\ssl:/etc/nginx/ssl/  --privileged=true -d --restart=always nginx
```

具体操作详细请看《ChatGPT 微服务应用体系构建》 - dev-ops 第4节：Nginx环境配置
