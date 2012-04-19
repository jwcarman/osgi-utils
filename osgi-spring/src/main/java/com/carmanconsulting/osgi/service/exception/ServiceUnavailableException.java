package com.carmanconsulting.osgi.service.exception;

public class ServiceUnavailableException extends RuntimeException
{
//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public ServiceUnavailableException()
    {
    }

    public ServiceUnavailableException(String message)
    {
        super(message);
    }

    public ServiceUnavailableException(Throwable cause)
    {
        super(cause);
    }

    public ServiceUnavailableException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ServiceUnavailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
