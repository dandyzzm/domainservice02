package com.lonntec.sufservice.lang;


import com.lonntec.framework.lang.MicroServiceException;
import com.lonntec.framework.lang.StateCode;

public class DeploySystemException extends MicroServiceException {

    public DeploySystemException(StateCode stateCode) {
        super(stateCode);
    }

    public DeploySystemException(StateCode stateCode, String message) {
        super(stateCode, message);
    }

    public DeploySystemException(StateCode stateCode, Throwable cause) {
        super(stateCode, cause);
    }

    public DeploySystemException(StateCode stateCode, String message, Throwable cause) {
        super(stateCode, message, cause);
    }
}
