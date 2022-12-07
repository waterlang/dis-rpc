package com.dis.rpc.client;

import com.dis.rpc.client.listener.CallbackListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class RPCRespFuture<T> implements Future<Object> {

    private T result;

    private Exception exception;

    private List<CallbackListener> listeners = new ArrayList<CallbackListener>();

    private CountDownLatch downLatch = new CountDownLatch(1);

    private static final Long WAIT_TIME = 3000L;

    /**
     * 
     * @param result
     */
    public void setResult(T result) {
        this.result = result;
        downLatch.countDown();
        notifyAllCallBack();
    }

    /**
     * notify all callBack
     */
    private void notifyAllCallBack() {
        if (!listeners.isEmpty()) {
            for (CallbackListener callbackListener : listeners) {
                if (exception != null) {
                    callbackListener.onException(exception);
                } else {
                    callbackListener.onSuccess(result);
                }
            }
        }
    }

    /**
     * 
     * @param e
     */
    public void setException(Exception e) {
        this.exception = e;
        downLatch.countDown();
        notifyAllCallBack();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return downLatch.getCount() == 0;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        if (downLatch.await(WAIT_TIME, TimeUnit.MILLISECONDS)) {
            return result;
        }

        log.warn("get result failed,cause time out");
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (downLatch.await(timeout, unit)) {
            return result;
        } else {
            throw new TimeoutException();
        }
    }

    

}
