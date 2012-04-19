package com.carmanconsulting.osgi.example.a;

import com.carmanconsulting.osgi.service.annotation.OsgiService;
import org.springframework.stereotype.Service;

@OsgiService
@Service("greeter")
public class GreeterImpl implements Greeter
{
    @Override
    public String sayHi(String name)
    {
        return "Hi, " + name + "!";
    }
}
