package org.gwtcommons.concurrency.thread;

public interface Thread
{
    public static interface ProgressHandler
    {
        public void onThreadProgress( int progress, int total );
    }
    
    public static interface CompleteHandler
    {
        public void onThreadComplete();
    }
    
    public static interface ErrorHandler
    {
        public void onThreadError( Throwable e );
        public void onThreadTimout();
    }
    
    public void start();
    public void pause();
    public void resume();
    public boolean isRunning();
    public void stop();
    public void destroy();
    public String name();
    
    public void setProgressHandler( ProgressHandler handler );
    public void setCompleteHandler( CompleteHandler handler );
    public void setErrorHandler( ErrorHandler handler );
}
