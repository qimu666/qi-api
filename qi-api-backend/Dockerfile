# 基础镜像
FROM openjdk:8-jdk

# 指定工作目录
WORKDIR /app

# 将 jar 包添加到工作目录
ADD target/qi-api-backend-0.0.1.jar .

ENV TZ=Asia/Shanghai

EXPOSE 7529
ENV SPRING_PROFILES_ACTIVE=docker
# 启动命令
ENTRYPOINT ["java","-jar","/app/qi-api-backend-0.0.1.jar","--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]

