package com.carmanconsulting.osgi.example.b;

import com.carmanconsulting.osgi.example.a.Greeter;
import com.carmanconsulting.osgi.service.annotation.OsgiService;
import com.carmanconsulting.osgi.service.annotation.OsgiServiceReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("dependentBean")
public class DependentBean
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private static final Log log = LogFactory.getLog(DependentBean.class);

    @OsgiServiceReference
    private Greeter greeter;

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @PostConstruct
    public void sayHi()
    {
        final AopProxy aop = new AopProxy()
        {
            @Override
            public Object getProxy()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Object getProxy(ClassLoader classLoader)
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        aop.getProxy(null);
        //log.info(greeter.sayHi("Jim"));
    }
}
