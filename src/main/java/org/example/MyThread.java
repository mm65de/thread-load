package org.example;

/**
 * You should not use wait() and notify() on instances of java.lang.Thread because this could interfere
 * with the thread status. So we encapsulate the java.lang.Thread here in our own class that has not that thread state.
 */
public class MyThread {

    private Thread thread;

    public MyThread(final Thread thread) {
        this.thread = thread;
    }

    public String threadDescription() {
        return thread.getName() + " (" + thread.threadId() + ") ";
    }

    public void start() {
        thread.start();
    }

    public void join() throws InterruptedException {
        thread.join();
    }

    public void interrupt() {
        thread.interrupt();
    }

}
