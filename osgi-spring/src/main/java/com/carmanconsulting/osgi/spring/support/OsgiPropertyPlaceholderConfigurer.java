package com.carmanconsulting.osgi.spring.support;

import org.osgi.application.ApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.osgi.context.BundleContextAware;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

public class OsgiPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements BundleContextAware
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private BundleContext bundleContext;
    private String pid;

//----------------------------------------------------------------------------------------------------------------------
// BundleContextAware Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void setBundleContext(BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
    }

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public void setPid(String pid)
    {
        this.pid = pid;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    protected void loadProperties(Properties props) throws IOException
    {
        super.loadProperties(props);
        if (bundleContext == null)
        {
            logger.warn("OSGi environment not detected; skipping OSGi configuration...");
            return;
        }
        logger.debug("Searching for OSGi Configuration Admin service...");
        ServiceReference serviceReference = bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin configurationAdmin = serviceReference == null ? null : (ConfigurationAdmin) bundleContext.getService(serviceReference);
        if (configurationAdmin == null)
        {
            logger.warn("OSGi Configuration Admin service not found, skipping OSGi configuration...");
            return;
        }

        final String effectivePid = pid == null ? bundleContext.getBundle().getSymbolicName() : pid;
        logger.debug("Looking up configuration for pid " + effectivePid + "...");
        final Configuration configuration = configurationAdmin.getConfiguration(effectivePid);
        if (configuration == null)
        {
            logger.warn("OSGi configuration for pid " + effectivePid + " not found.");
            return;
        }
        logger.debug("OSGi configuration for pid " + effectivePid + " found, extracting properties...");
        Dictionary osgiProperties = configuration.getProperties();
        if (osgiProperties == null)
        {
            logger.warn("Unable to extract properties from OSGi configuration for pid " + effectivePid + ".");
            return;
        }
        logger.info("Loading properties from OSGi configuration for pid " + effectivePid + "...");
        copyDictionaryIntoProperties(osgiProperties, props);
        logger.info("Successfully loaded properties from OSGi configuration for pid " + effectivePid + ".");

    }

    private void copyDictionaryIntoProperties(Dictionary dictionary, Properties properties)
    {
        Enumeration<?> osgiPropertyNames = dictionary.keys();
        while (osgiPropertyNames.hasMoreElements())
        {
            String osgiPropertyName = (String) osgiPropertyNames.nextElement();
            Object configValue = dictionary.get(osgiPropertyName);
            if (properties.containsKey(osgiPropertyName))
            {
                logger.debug("Overriding property \"" + osgiPropertyName + "\" with Configuration Admin service value...");
            }
            properties.put(osgiPropertyName, configValue);
        }
    }
}
