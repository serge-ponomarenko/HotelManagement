package ua.cc.spon.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RegularExecutor
{
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    ScheduledJob myTask;

    public RegularExecutor(ScheduledJob myTask)
    {
        this.myTask = myTask;

    }

    public void startExecutionForEvery(int unit, TimeUnit timeUnit)
    {

        Runnable taskWrapper = new Runnable(){

            @Override
            public void run()
            {
                myTask.execute();
            }

        };

        executorService.scheduleAtFixedRate(taskWrapper, 1, unit, timeUnit);

    }


    public void stop()
    {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException ex) {
           //Logger.getLogger(MyTaskExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}