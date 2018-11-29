package com.dis.rpc.test.client;

import com.dis.rpc.client.DisRpcClient;
import com.dis.rpc.client.RPCRespFuture;
import com.dis.rpc.client.aop.DefaultClientHook;
import com.dis.rpc.client.aop.RpcHook;
import com.dis.rpc.client.listener.CallbackListener;
import com.dis.rpc.client.proxy.IAsyncObjectProxy;
import com.dis.rpc.test.api.HelloService;

import java.util.concurrent.ExecutionException;

public class ClientTest {

    public static void main(String[] args) {
        RpcHook hook = new DefaultClientHook() ;

        DisRpcClient client = new DisRpcClient();
        client.creatChannle(hook,"127.0.0.1");

        while (client.handler == null){
            try {
                Thread.sleep(2000);
                System.out.println("---休息两秒");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("-------satrt---");

        //sync
        HelloService helloService =  client.create(HelloService.class);
        System.out.println("--helloService:"+helloService);
        String result =  helloService.say("hahaha");
        System.out.println("---收到的结果:"+result);

        //async
        IAsyncObjectProxy proxy = client.createAsync(HelloService.class);
        RPCRespFuture respFuture = proxy.call("say","hahaha");
        try {
            System.out.println("---收到的结果:"+respFuture.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // call back
        RPCRespFuture future = proxy.call("say","hahaha");
        future.addListeners(new CallbackListener() {
           @Override
           public void onSucces(Object resp) {
               System.out.println("--------------success-----resp-------"+resp);
           }

           @Override
           public void onException(Object resp) {
               System.out.println("--------------error-----resp-------");
           }
       });
    }
}
