package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Group {

    private final String threadNamePrefix;
    private final int threadCount;
    private final Object semaphore;
    private AtomicBoolean terminate;

    private final List<Thread> threads;


    public Group(final String threadNamePrefix, final int threadCount) {
        this.threadNamePrefix = threadNamePrefix;
        this.threadCount = threadCount;
        this.semaphore = new Object();
        this.terminate = new AtomicBoolean(false);
        this.threads = new ArrayList<>(threadCount);
        IntStream.range(0, threadCount).forEach(this::buildThread);
    }

    private void buildThread(final int index) {
        final int threadNumber = index + 1;
        final Thread thread = new Thread(buildRunnable(threadNumber));
        thread.setName(threadNamePrefix + threadNumber);
        threads.add(thread);
    }

    private Runnable buildRunnable(final int threadNumber) {
        return new Runnable() {
            @Override
            public void run() {
                runMethod(threadNumber);
            }
        };
    }

    private void runMethod(int threadNumber) {
        System.out.println("starting " + threadNamePrefix + threadNumber);
        synchronized (semaphore) {
            try {
                while (!terminate.get()) {
                    final int mainLoopCount = threadNumber;
                    calculateHeavily(mainLoopCount);
                    if (threadCount > 1) {
                        semaphore.notify();
                        semaphore.wait();
                    }
                }
            } catch (InterruptedException e) {
                //System.out.println("Thread " + threadNamePrefix + threadNumber + " got exception: " + e);
                Thread.currentThread().interrupt();
            }
        }
        //System.out.println("Thread " + threadNamePrefix + threadNumber + " terminates.");
    }

    private long calculateHeavily(int mainLoopCount) {
        final long MAX_LOOP = 2 * 1000;
        final long MODULO = 4294970149L;

        long someSum = 0;
        for(int n = 0; n < mainLoopCount; n++) {
            for (long l = 0; l < MAX_LOOP; l++) {
                for (long m = 0; m < MAX_LOOP; m++) {
                    someSum = (someSum + l * m + 1) % MODULO;
                }
            }
        }
        return someSum;
    }

    public void startThreads() {
        threads.forEach(Thread::start);
    }

    public void terminateThreads()  {
        terminate.set(true);
        threads.forEach(Thread::interrupt);
    }

    public void waitForThreadTermination()  {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}