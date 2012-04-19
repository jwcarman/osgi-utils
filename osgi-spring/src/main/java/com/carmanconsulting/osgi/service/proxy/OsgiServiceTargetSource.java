package com.carmanconsulting.osgi.service.proxy;

import com.carmanconsulting.osgi.service.annotation.OsgiServiceReference;
import com.carmanconsulting.osgi.service.exception.ServiceUnavailableException;
import com.carmanconsulting.osgi.spring.support.OsgiServiceAutowireProcessor;
import com.carmanconsulting.osgi.util.ResettableLatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.springframework.aop.TargetSource;

import java.util.concurrent.TimeUnit;

public class OsgiServiceTargetSource implements TargetSource, ServiceTrackerCustomizer
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private static final Log logger = LogFactory.getLog(OsgiServiceAutowireProcessor.class);
    private final ResettableLatch latch = new ResettableLatch();
    private final ServiceTracker tracker;
    private final Class<?> targetClass;
    private long timeout = TimeUnit.SECONDS.toMillis(10);

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public OsgiServiceTargetSource(OsgiServiceReference serviceReference, BundleContext bundleContext, Class<?> targetClass)
    {
        this.timeout = serviceReference.timeout();
        this.targetClass = targetClass;
        this.tracker = new ServiceTracker(bundleContext, targetClass.getName(), this);
        tracker.open();
    }

//----------------------------------------------------------------------------------------------------------------------
// ServiceTrackerCustomizer Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Object addingService(ServiceReference reference)
    {
        latch.release();
        return tracker.getService(reference);
    }

    @Override
    public void modifiedService(ServiceReference reference, Object service)
    {
        // Do nothing!
    }

    @Override
    public void removedService(ServiceReference reference, Object service)
    {
        if(tracker.getTrackingCount() == 0)
        {
            latch.reset();
        }
    }

//----------------------------------------------------------------------------------------------------------------------
// TargetClassAware Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Class<?> getTargetClass()
    {
        return targetClass;
    }

//----------------------------------------------------------------------------------------------------------------------
// TargetSource Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Object getTarget() throws Exception
    {
        if (latch.await(timeout, TimeUnit.MILLISECONDS))
        {
            return tracker.getService();
        }
        throw new ServiceUnavailableException("Service unavailable.");
    }

    @Override
    public boolean isStatic()
    {
        return false;
    }

    @Override
    public void releaseTarget(Object target) throws Exception
    {
        // Do nothing.
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    public void stop()
    {
        tracker.close();
    }
}
