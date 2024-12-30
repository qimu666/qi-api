# 基础镜像
FROM openjdk:8-jdk

# 指定工作目录
WORKDIR /app

# 将 jar 包添加到工作目录
ADD target/qi-api-gateway-0.0.1-SNAPSHOT.jar .

ENV TZ=Asia/Shanghai

EXPOSE 8090

# 启动命令
ENTRYPOINT ["java","-jar","/app/qi-api-gateway-0.0.1-SNAPSHOT.jar","--spring.profiles.active=docker"]

