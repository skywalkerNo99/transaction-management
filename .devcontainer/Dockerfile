FROM ubuntu:latest

# 避免在构建过程中出现交互式提示
ENV DEBIAN_FRONTEND=noninteractive

# 安装基本工具和依赖
RUN apt-get update && apt-get install -y \
    curl \
    git \
    wget \
    unzip \
    zip \
    build-essential \
    libz-dev \
    zlib1g-dev \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /workspace

# 安装 SDKMAN!
ENV SDKMAN_DIR=/root/.sdkman
RUN curl -s "https://get.sdkman.io" | bash
RUN bash -c "source $SDKMAN_DIR/bin/sdkman-init.sh && \
    sdk install java 21.0.1-graal && \
    sdk install maven 3.9.6"

# 设置环境变量
ENV JAVA_HOME=$SDKMAN_DIR/candidates/java/current
ENV PATH=$JAVA_HOME/bin:$PATH
ENV MAVEN_HOME=$SDKMAN_DIR/candidates/maven/current
ENV PATH=$MAVEN_HOME/bin:$PATH

# 验证安装
RUN bash -c "source $SDKMAN_DIR/bin/sdkman-init.sh && \
    java -version && \
    mvn -version" 