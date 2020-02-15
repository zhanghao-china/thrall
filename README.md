# Thrall
## 总体介绍
* thrall旨在解决微服务多实例情况下，异常堆栈信息，微服务的接口信息，参数信息，异常类，异常接口信息的收录，提供异常回溯及异常监控，方便开发人员及时的修复异常
* thrall完全基本spring5 webflux框架reactor开发，完全抛弃了tomcat和serverlet api的BIO线程模型，拥抱netty Reactor的nio模型，thrall属于响应式微服务，流式微服务，天然支持高性能，高可用！
* thrall当前支持单机部署，或接入nacos的多实例部署


## 安装部署
  1. git clone https://github.com/zhanghao-china/thrall.git
  2. 在thrall目录下 执行mvn install 打包
  3. 在thrall-server/target目录下可以看到生成的tar包