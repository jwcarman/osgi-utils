package com.carmanconsulting.osgi.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ResettableLatch
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private AtomicReference<CountDownLatch> latchReference = new AtomicReference<CountDownLatch>();

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public ResettableLatch()
    {
        reset();
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    public void await() throws InterruptedException
    {
        latchReference.get().await();
    }

    public boolean await(long timeout,TimeUnit timeUnit) throws InterruptedException
    {
        return latchReference.get().await(timeout, timeUnit);
    }

    public void release()
    {
        latchReference.get().countDown();
    }

    public final void reset()
    {
        latchReference.set(new CountDownLatch(1));
    }
}
