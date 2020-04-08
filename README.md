# spring-boot-docker
docker部署Spring Boot应用
# 使用Docker部署Spring Boot应用

​     首先构建一个简单的 Spring Boot 项目，然后给项目添加 Docker 支持，最后对项目进行部署。

## 构建一个简单的Spring Boot项目

1、在pom.xml文件中，使用Spring Boot 2.2.5相关依赖

```xml
<parent>
    <artifactId>spring-boot-starter-parent</artifactId>
    <groupId>org.springframework.boot</groupId>
     <version>2.2.5.RELEASE</version>
</parent>
```

2、添加web和测试依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-web</artifactId>
     </dependency>
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-test</artifactId>
         <scope>test</scope>
      </dependency>
</dependencies>
```

3、创建一个 `DockerController`，在其中有一个`index`()方法，访问时返回：`Hello Docker!`

```java
package com.example.springbootdocker.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class DockerController {

    @GetMapping("/")
    public String index() {
        return "Docker hello";
    }
}

```

4、创建启动类Application.java

```java
package com.example.springbootdocker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}

```

5、启动项目，浏览器访问：http://localhost:8080/，页面返回：Hello Docker!，说明 Spring Boot 项目配置正常。

## Spring Boot项目添加Docker支持

1、在pom.xml文件中添加Docker镜像名称

```xml
<properties>
       <docker.image.prefix>springboot</docker.image.prefix>
</properties>
```

2、plugins中添加Docker构建插件

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        <!--Add docker maven plugin-->
        <plugin>
            <groupId>com.spotify</groupId>
            <artifactId>docker-maven-plugin</artifactId>
            <version>1.0.0</version>
            <configuration>
                <imageName>${docker.image.prefix}/${project.artifactId}</imageName>
                <dockerDirectory>src/main/docker</dockerDirectory>
                <resources>
                    <resource>
                        <targetPath>/</targetPath>
                        <directory>${project.build.directory}</directory>
                        <include>${project.build.finalName}.jar</include>
                    </resource>
                </resources>
            </configuration>
        </plugin>
    </plugins>
</build>
```

3、 在目录`src/main/docker`下创建 Dockerfile 文件，Dockerfile 文件用来说明如何来构建镜像。 

```dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD spring-boot-docker-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

 这样 Spring Boot 项目添加 Docker 依赖就完成了。 

## 构建打包环境

我们需要有一个 Docker 环境来打包 Spring Boot 项目，在 Windows 搭建 Docker 环境很麻烦，因此我这里以 Centos 7 为例。

安装Docker、jdk8、maven，本章不讲解安装。

## 使用 Docker 部署 Spring Boot 项目

1、将项目 spring-boot-docker 拷贝centos7服务器中，进入项目路径下进行打包测试。

```linux
#打包
mvn package
#启动
java -jar target/spring-boot-docker-1.0-SNAPSHOT.jar
```

2、 看到 Spring Boot 的启动日志后表明环境配置没有问题，接下来我们使用 DockerFile 构建镜像。 

```maven
mvn package docker:build

```

3、使用docker images命令查看构建好的镜像

```
docker images
REPOSITORY      TAG     IMAGE ID    CREATED      SIZE
springboot/spring-boot-docker   latest              99ce9468da74        6 seconds ago       117.5 MB

```

 `springboot/spring-boot-docker` 就是我们构建好的镜像

4、运行镜像

```docker
docker run -it -p 8080:8080 springboot/spring-boot-docker

```

5、 启动完成之后我们使用`docker ps`查看正在运行的镜像

6、 访问浏览器：`http://192.168.0.x:8080/`,返回 Hello Docker! 说明使用 Docker 部署 Spring Boot 项目成功！ 

