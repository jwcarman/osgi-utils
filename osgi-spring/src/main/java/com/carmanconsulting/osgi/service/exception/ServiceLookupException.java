package com.carmanconsulting.osgi.service.exception;

public class ServiceLookupException extends RuntimeException
{
//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public ServiceLookupException()
    {
    }

    public ServiceLookupException(String message)
    {
        super(message);
    }

    public ServiceLookupException(Throwable cause)
    {
        super(cause);
    }

    public ServiceLookupException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ServiceLookupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
