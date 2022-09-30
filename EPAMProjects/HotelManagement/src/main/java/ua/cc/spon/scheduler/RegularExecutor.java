package ua.cc.spon.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RegularExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegularExecutor.class);

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    ScheduledJob myTask;

    public RegularExecutor(ScheduledJob myTask) {
        this.myTask = myTask;
    }

    public void startExecutionForEvery(int unit, TimeUnit timeUnit) {
        Runnable taskWrapper = () -> myTask.execute();
        executorService.scheduleAtFixedRate(taskWrapper, 1, unit, timeUnit);
    }

    public boolean stop() {
        executorService.shutdown();
        try {
            return executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
}