package org.gwtcommons.concurrency.thread;

public interface Runnable
{
    public void run();
    
    public void cleanup();
    
    public boolean isComplete();
    
    public int getTotal();
    
    public int getProgress();
}
