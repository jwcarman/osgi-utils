package com.carmanconsulting.osgi.spring.support;

import com.carmanconsulting.osgi.service.annotation.OsgiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class OsgiServiceExporterProcessor implements BeanPostProcessor, BundleContextAware
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------
    private static final String[] NO_INTERFACES = new String[0];

    private static final Log logger = LogFactory.getLog(OsgiServiceExporterProcessor.class);
    private BundleContext bundleContext;
    private final List<ServiceRegistration> serviceRegistrations = new LinkedList<ServiceRegistration>();

//----------------------------------------------------------------------------------------------------------------------
// BeanPostProcessor Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        if (bundleContext != null)
        {
            OsgiService osgiServiceAnntation = bean.getClass().getAnnotation(OsgiService.class);
            if (osgiServiceAnntation != null)
            {
                final String[] interfaces = extractServiceInterfaces(bean);
                if(interfaces.length > 0)
                {
                    logger.info("Exporting bean \"" + beanName + "\" as an OSGi service supporting interfaces " + StringUtils.arrayToCommaDelimitedString(interfaces) + "...");
                    serviceRegistrations.add(bundleContext.registerService(interfaces, bean, null));
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
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

    private String[] extractServiceInterfaces(Object bean)
    {
        Set<Class> serviceInterfaces = ClassUtils.getAllInterfacesAsSet(bean);
        if(serviceInterfaces.isEmpty())
        {
            return NO_INTERFACES;
        }
        List<String> serviceInterfaceNames = new ArrayList<String>(serviceInterfaces.size());
        for (Class serviceInterface : serviceInterfaces)
        {
            Method[] methods = serviceInterface.getMethods();
            if (methods != null && methods.length > 0)
            {
                serviceInterfaceNames.add(serviceInterface.getName());
            }
        }
        return serviceInterfaceNames.toArray(new String[serviceInterfaceNames.size()]);
    }

    @PreDestroy
    public void unregisterServices()
    {
        logger.info("Unregistering all registered services...");
        for (ServiceRegistration serviceRegistration : serviceRegistrations)
        {
            serviceRegistration.unregister();
        }
    }

    @PostConstruct
    public void detectOsgiEnvironment()
    {
        if (bundleContext == null)
        {
            logger.warn("OSGi environment not detected; cannot export OSGi services.");
        }
        else
        {
            logger.info("OSGi environment detected; all @OsgiService-annotated beans will be registered as OSGi services...");
        }
    }
}
