package com.carmanconsulting.osgi.spring.support;

import com.carmanconsulting.osgi.service.annotation.OsgiServiceReference;
import com.carmanconsulting.osgi.service.proxy.OsgiServiceTargetSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.util.BundleDelegatingClassLoader;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class OsgiServiceAutowireProcessor implements BeanPostProcessor, BundleContextAware
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private static final Log logger = LogFactory.getLog(OsgiServiceAutowireProcessor.class);
    private BundleContext bundleContext;
    private final Set<OsgiServiceTargetSource> invocationHandlers = new HashSet<OsgiServiceTargetSource>();

//----------------------------------------------------------------------------------------------------------------------
// BeanPostProcessor Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException
    {
        if (bundleContext != null)
        {
            logger.info("Searching bean \"" + beanName + "\" for @OsgiServiceReference-annotated fields...");
            ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback()
            {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException
                {
                    logger.info("Checking field " + field.getName() + "...");
                    OsgiServiceReference annotation = field.getAnnotation(OsgiServiceReference.class);
                    if (annotation != null)
                    {
                        logger.info("Found @OsgiServiceReference on " + field.getName() + " of bean \"" + beanName + ".\"");
                        ReflectionUtils.makeAccessible(field);
                        Object proxy = new ProxyFactory(field.getType(), new OsgiServiceTargetSource(annotation, bundleContext, field.getType())).getProxy(BundleDelegatingClassLoader.createBundleClassLoaderFor(bundleContext.getBundle()));
                        ReflectionUtils.setField(field, bean, proxy);
                    } else
                    {
                        logger.info("No annotation found!");
                    }
                }
            });
        }
        return bean;
    }

//----------------------------------------------------------------------------------------------------------------------
// BundleContextAware Implementation
//----------------------------------------------------------------------------------------------------------------------

    public void setBundleContext(BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @PostConstruct
    public void detectOsgiEnvironment()
    {
        if (bundleContext == null)
        {
            logger.warn("OSGi environment not detected; cannot inject OSGi services.");
        } else
        {
            logger.info("OSGi environment detected; all @OsgiServiceReferece-annotated fields will be auto-wired...");
        }
    }

    @PreDestroy
    public void stopAllInvocationHandlers()
    {
        logger.info("Stopping all invocation handlers...");
        for (OsgiServiceTargetSource invocationHandler : invocationHandlers)
        {
            invocationHandler.stop();
        }
    }
}
