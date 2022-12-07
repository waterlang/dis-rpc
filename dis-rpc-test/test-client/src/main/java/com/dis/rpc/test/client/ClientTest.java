package com.dis.rpc.test.client;

import com.dis.rpc.client.DisRpcClient;
import com.dis.rpc.client.RPCRespFuture;
import com.dis.rpc.client.aop.DefaultClientHook;
import com.dis.rpc.client.aop.RpcHook;
import com.dis.rpc.client.proxy.IAsyncObjectProxy;
import com.dis.rpc.test.api.HelloService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * 
 */

@Slf4j
public class ClientTest {

    public static void main(String[] args) {
        RpcHook hook = new DefaultClientHook();

        DisRpcClient client = new DisRpcClient();
        client.creatChannel(hook, "127.0.0.1", 8888);

        while (client.getClientRespHandler() == null) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("--------client starting---");

        // simple
        // HelloService helloService = client.create(HelloService.class);
        // helloService.say("ni hao");

         // proxy async
         IAsyncObjectProxy proxy = client.createProxy(HelloService.class);
         RPCRespFuture respFuture = proxy.call("say", "hello world");
         try {
             log.info("--proxy 调用结果:{}", respFuture.get());
         } catch (InterruptedException | ExecutionException e) {
             log.warn("e", e);
         }

        // // call back
        // RPCRespFuture future = proxy.call("say", "hello world");
        // future.addListeners(new CallbackListener() {
        // @Override
        // public void onSuccess(Object resp) {
        // log.warn("---回调成功结果:{}", resp);
        // }
        //
        // @Override
        // public void onException(Object resp) {
        // log.warn("---回调失败结果:{}", resp);
        // }
        // });
    }
}
