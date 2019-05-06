#############################################################################
# zkweb
#
# 容器构建镜像
#  1. 使用命令将dockerfile和maven打包好的jar包放在target目录下：
#           $ mvn clean package -f pom-jar.xml
#
#  2. 在target目录下运行命令构建镜像：
#           $ docker build -t zkweb:v1.2.1 .
#
# 容器启动
#  1. 命令： docker run -p 8099:8099 --name zkweb  -d zkweb:v1.2.1
#
#
#############################################################################


# java镜像
FROM daocloud.io/java:8

# 将本地文件夹挂载到当前容器
# 创建/tmp目录并持久化到Docker数据文件夹，因为Spring Boot使用的内嵌Tomcat容器默认使用/tmp作为工作目录。
VOLUME ["/tmp"]

# 解决容器时间和宿主主机时间不一致问题
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone


# 拷贝文件到容器
COPY zkWeb-v1.2.1.jar   /opt/app.jar

# 打开服务端口
EXPOSE 8099 8099

# 配置环境变量 todo jvm优化参数可以设置这里
ENV JAVA_OPTS='-Xmx4096m -Xms4096m ' APP_OPTS=''

# 配置容器启动后执行的命令
ENTRYPOINT java $JAVA_OPTS -server -Dfile.encoding=UTF-8 -Duser.language=zh -Duser.region=CN -Djava.security.egd=file:/dev/./urandom -jar /opt/app.jar $APP_OPTS