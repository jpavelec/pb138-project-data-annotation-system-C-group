package cz.muni.pb138.annotationsystem.frontend.controller;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class RemoteUserFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(req);
        mutableRequest.setRemoteUser("Karlik", HttpServletRequest.BASIC_AUTH);
        chain.doFilter(mutableRequest, response);
    }

    @Override
    public void destroy() {

    }
}
