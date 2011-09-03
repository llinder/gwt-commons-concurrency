package org.gwtcommons.concurrency.thread;

import com.google.gwt.user.client.Timer;

public class PseudoThread
    implements Thread
{
    
    private Timer timer;
    private final String threadName;
    private final int msDelay;
    private Runnable runnable;
    private double maxRunTimes;
    
    private ProgressHandler progressHandler;
    private CompleteHandler completeHandler;
    private ErrorHandler errorHandler;
    
    private int totalTimesRan = 0;
    
    private boolean started = false;
    
    public PseudoThread( Runnable runnable, String threadName, int msDelay, int msTimeout )
    {
        this.threadName = threadName;
        this.msDelay = msDelay;
        this.runnable = runnable;
        
        if( msTimeout != -1 )
            if( msTimeout < msDelay )
                throw new RuntimeException( "Psuedothread cannot be constructed with a msTimeout that is less than the msDelay." );
        
        maxRunTimes = Math.ceil( msTimeout / msDelay );
    }
    
    private void processor()
    {
        try
        {
            this.runnable.run();
            this.totalTimesRan++;
        }
        catch( Exception e )
        {
            if( errorHandler != null )
                errorHandler.onThreadError( e );
        }
        
        if( runnable.isComplete() )
        {
            if( progressHandler != null )
                progressHandler.onThreadProgress( runnable.getProgress(), runnable.getTotal() );
            
            if( completeHandler != null )
                completeHandler.onThreadComplete();
            
            destroy();
        }
        else
        {
            if( this.maxRunTimes != 0 && this.maxRunTimes == this.totalTimesRan )
            {
                if( errorHandler != null )
                    errorHandler.onThreadTimout();

                destroy();
                return;
            }
            else
            {
                if( progressHandler != null )
                    progressHandler.onThreadProgress( runnable.getProgress(), runnable.getTotal() );
            }
        }
    }
    
    private void initTimer()
    {
        if( timer == null )
        {
            this.timer = new Timer()
            {
                @Override
                public void run()
                {
                    processor();
                }
            };
            this.timer.scheduleRepeating( msDelay );
        }
    }
    
    @Override
    public String name()
    {
        return threadName;
    }

    @Override
    public void start()
    {
        if( !started )
        {
            this.initTimer();
            
            this.started = true;
        }
    }

    @Override
    public void pause()
    {
        if( isRunning() )
        {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void resume()
    {
        if( !isRunning() )
        {
            initTimer();
        }
    }

    @Override
    public boolean isRunning()
    {
        return ( timer != null );
    }

    @Override
    public void stop()
    {
        if( isRunning() )
        {
            destroy();
        }
    }

    @Override
    public void destroy()
    {
        if( timer != null )
        {
            timer.cancel();
            timer = null;
        }
        
        if( runnable != null )
        {
            try
            {
                runnable.cleanup();
                runnable = null;
            }
            catch( Exception e )
            {
                if( errorHandler != null )
                    errorHandler.onThreadError( e );
            }
        }
    }
    
    public void setProgressHandler( ProgressHandler handler )
    {
        this.progressHandler = handler;
    }
    public void setCompleteHandler( CompleteHandler handler )
    {
        this.completeHandler = handler;
    }
    public void setErrorHandler( ErrorHandler handler )
    {
        this.errorHandler = handler;
    }

}
