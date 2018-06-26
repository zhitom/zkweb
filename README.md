## zkWeb-en

zkWeb is zookeeper web to manager and monitor zookeeper cluster with builtin H2 database.This is based on TaoBao God（yasenagat）'s zkWeb code,and have a big upgrade and modification,It can put war-file to tomcat and execute it!

### old zkWeb code address

yasenagat-zkweb svn: [http://code.taobao.org/svn/zkweb/](http://code.taobao.org/svn/zkweb/ "yasenagat-zkweb")

### Major Modification

- Upgrade depend jars include spring\zookeeper ...
- Upgrade easyui to EasyUI for jQuery 1.5.5.4、jQuery v1.12.4
- Optimize page layout,such as: multi-tabs switch,one zk to one tab;add filter within cfg pages.
- Support High version of Tomcat,and tested ok with tomcat 7
- Add zookeeper cluster's state-monitor function,and use four-word cmd to get state infomation
- Add zookeeper loop-check connect state
- Front-end web add i18n Internationalization plugin，Support english and zh_CN，and server-end data don't added this.

## zkWeb-zh_CN
zookeeper web管理和监控界面，使用内置的H2数据库，此版本基于淘宝大神yasenagat的zkWeb源码基础之上进行了大幅升级和修改，直接将war包放入tomcat即可运行！

### 旧zkWeb源码地址

yasenagat-zkweb svn: [http://code.taobao.org/svn/zkweb/](http://code.taobao.org/svn/zkweb/ "yasenagat-zkweb")

### 重大修改点

- 升级依赖的第三方库，包括spring、zookeeper等
- 升级easyui到EasyUI for jQuery 1.5.5.4、jQuery v1.12.4
- 优化页面布局，如：支持多TAB切换，一个zk连接一个TAB标签；在配置界面增加过滤器；
- 支持tomcat高版本，目前在tomcat7测试通过
- 增加zk集群状态的监控功能，使用了四字命令获取监控信息
- 增加zk集群自动检测连接状态功能
- 前端web增加i18n国际化插件，支持界面英文展示，注：服务端数据未支持国际化。







