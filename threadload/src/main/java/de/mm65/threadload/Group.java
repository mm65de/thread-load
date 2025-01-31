package de.mm65.threadload;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Group {

    private final String threadNamePrefix;
    private final int threadCount;
    private AtomicBoolean terminate;

    private final List<MyThread> threads;


    public Group(final String threadNamePrefix, final int threadCount) {
        this.threadNamePrefix = threadNamePrefix;
        this.threadCount = threadCount;
        this.terminate = new AtomicBoolean(false);
        this.threads = new ArrayList<>(threadCount);
        IntStream.range(0, threadCount).forEach(this::buildThread);
    }

    private void buildThread(final int threadIndex) {
        final Thread thread = new Thread(buildRunnable(threadIndex));
        thread.setName(threadNamePrefix + (threadIndex + 1));
        threads.add(new MyThread(thread));
    }

    private Runnable buildRunnable(final int threadIndex) {
        return () -> runMethod(threadIndex);
    }

    private void runMethod(int threadIndex) {
        final MyThread currentThread = threads.get(threadIndex);
        final MyThread nextThread = threads.get((threadIndex + 1) % threadCount);
        //System.out.println("starting " + currentThread.threadDescription());
        try {
            while (!terminate.get()) {
                final int mainLoopCount = threadIndex + 1;
                calculateHeavily(mainLoopCount);
                if (threadCount > 1) {
                    synchronized (currentThread) {
                        synchronized (nextThread) {
                            nextThread.notify();
                        }
                        currentThread.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            //System.out.println("Thread " + currentThread.threadDescription() + " got exception: " + e);
            currentThread.interrupt();
        }
        //System.out.println("Thread " + currentThread.threadDescription() + " terminates.");
    }

    private long calculateHeavily(int mainLoopCount) {
        final long MAX_LOOP = 2 * 1000L;
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
        threads.forEach(MyThread::start);
    }

    public void terminateThreads()  {
        terminate.set(true);
        threads.forEach(MyThread::interrupt);
    }

    public void waitForThreadTermination()  {
        for (MyThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}