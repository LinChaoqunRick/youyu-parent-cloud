# 基础镜像
FROM openjdk:8-jdk-alpine

# 指定工作目录
WORKDIR /app

# 将 jar 包添加到工作目录，比如 target/yuoj-backend-user-service-0.0.1-SNAPSHOT.jar
ADD {本地 jar 包路径} .

# 暴露端口
EXPOSE {服务端口号}
# 启动命令
ENTRYPOINT ["java","-jar","/app/{jar 包名称}","--spring.profiles.active=prod"]
