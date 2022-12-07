# dis-rpc
公司内部做分享，为了帮助同事们理解RPC的核心原理，于是从0编写一个了简单的rpc。<p>
## 该项目分为两块：<p>
1.dis-rpc是rpc的核心模块,他主要由三大模块组成：<p>
   rpc-comm 通用模块，提供缓存，序列化等能力  <p>
   rpc-server rpc的服务端模板，主要将收到的client请求通过路由找到对应的Service及方法，并将执行结果返回给client  <p>
   rpc-client rpc的客户端，提供和rpc-server建立链接的能力并将用户请求发送给server端

2.dis-rpc-test应用层服务(测试代码) <p>
   test-api 主要为test-server提供对外标准协议 <p>
   test-server rpc的server端。内部主要依赖rpc-server的能力对外提供各种接口能力 <p>
   test-client rpc的client端(类似dubbo的consumer端)，通过调用远程服务对外的接口来获取/写入数据 <p>

## 使用方式： <p>
1.先将dis-rpc项目通过maven  install 到本地 或者直接在Idea中将dis-rpc添加到dis-rpc-test项目中 <p>
2.先启动dis-rpc-test项目中test-server模块的App类  <p>
再运行dis-rpc-test中test-client模块中的ClientTest类 <p>

## 3.整个设计思路
 todo
