package com.excalibur.frame.service.priority;

import com.nd.hy.android.core.util.Ln;

import java.util.concurrent.*;

/**
 * PriorityThreadPoolExecutor
 * Date: 13-12-13
 * <p/>
 * 带有优先级队列的线程池实现
 *
 * @author Yangz
 */
public class PriorityThreadPoolExecutor<T extends PriorityRunnable> extends ThreadPoolExecutor {
    public PriorityThreadPoolExecutor(int nThread) {
        super(nThread, nThread, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>());
    }

    public Future<?> submit(T task) {
        if (task == null) {
            throw new NullPointerException();
        }
        PriorityFutureTask<Object> futureTask = new PriorityFutureTask<Object>(task, null);
        execute(futureTask);
        return futureTask;
    }

    @Override
    public Future<?> submit(Runnable task) {
        throw genSubmitError();
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        throw genSubmitError();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        throw genSubmitError();
    }

    private Error genSubmitError() {
        return new IllegalAccessError("task must extends " + PriorityRunnable.class.getSimpleName());
    }

    private class PriorityFutureTask<V> extends FutureTask<V> implements Comparable<PriorityFutureTask<V>> {

        private T mPriorityRunnable;

        public PriorityFutureTask(T runnable, V result) {
            super(runnable, result);
            this.mPriorityRunnable = runnable;
        }

        private T getComparable() {
            return mPriorityRunnable;
        }

        @Override
        @SuppressWarnings("unchecked")
        public int compareTo(PriorityFutureTask<V> other) {
            try {
                return getComparable().compareTo(other.getComparable());
            } catch (Exception e) {
                Ln.e(e, "compare fail");
                return 0;
            }
        }
    }


}
