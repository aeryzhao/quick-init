package top.amfun.quickinit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhaoxg
 * @date 2023/2/23 17:20
 */
@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 设置 ThreadPoolExecutor 的核心池大小
     */
    private static final int CORE_POOL_SIZE = 20;
    /**
     * 设置 ThreadPoolExecutor 的线程数最大值
     */
    private static final int MAX_POOL_SIZE = 20;
    /**
     * 设置 ThreadPoolExecutor 的等待队列的容量。
     */
    private static final int QUEUE_CAPACITY = 300;
    /**
     * 当池子的线程数大于corePoolSize时，多余的线程会等待keepAliveTime长时间，如果无请求可处理就自行销毁
     */
    private static final int KEEP_ALIVE_SECONDS = 60;

    /**
     *  ThredPoolTaskExcutor的处理流程:
     *  当池子大小小于corePoolSize，就新建线程，并处理请求
     *  当池子大小等于corePoolSize，把请求放入workQueue中，池子里的空闲线程就去workQueue中取任务并处理
     *  当workQueue放不下任务时，就新建线程入池，并处理请求，如果池子大小撑到了maximumPoolSize，就用RejectedExecutionHandler来做拒绝处理
     *  当池子的线程数大于corePoolSize时，多余的线程会等待keepAliveTime长时间，如果无请求可处理就自行销毁
     *
     *  当线程池的任务缓存队列已满并且线程池中的线程数目达到maximumPoolSize，如果还有任务到来就会采取任务拒绝策略
     *  通常有以下四种策略：
     *  ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。
     *  ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。
     *  ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
     *  ThreadPoolExecutor.CallerRunsPolicy：重试添加当前的任务，自动重复调用 execute() 方法，直到成功
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        taskExecutor.setQueueCapacity(QUEUE_CAPACITY);
        taskExecutor.setAllowCoreThreadTimeOut(false);
        taskExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        taskExecutor.setThreadNamePrefix("Async-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> log.error("Async自定义线程池发生未知异常, {}", ex.getMessage());
    }

}