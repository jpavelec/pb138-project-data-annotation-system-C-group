package cz.muni.pb138.annotationsystem.frontend.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
final class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private String remoteUser;
    private String authType;

    public MutableHttpServletRequest(HttpServletRequest request){
        super(request);
    }

    public void setRemoteUser(String remoteUser, String authType){
        this.remoteUser = remoteUser;
        this.authType = authType;
    }

    public String getRemoteUser() {

        if (remoteUser != null){
            return remoteUser;
        }
        // else return from into the original wrapped object
        return ((HttpServletRequest) getRequest()).getRemoteUser();
    }

    public String getAuthtype() {

        if (authType != null){
            return authType;
        }
        // else return from into the original wrapped object
        return ((HttpServletRequest) getRequest()).getAuthType();
    }
}