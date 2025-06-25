package io.pilet.kueb.workflow.controller;


import io.pilet.kueb.common.data.Lookups;
import io.pilet.kueb.workflow.model.iam.IamMethod;
import io.pilet.kueb.workflow.model.iam.IamTokenUrl;
import io.pilet.kueb.workflow.model.iam.IamUser;
import io.pilet.kueb.workflow.model.iam.IamUserToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class AuthorizationFilter extends GenericFilterBean {
    static Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String token = token(request);
        if(token == null) {
            sendUnauthorized(response);
            return;
        }

        String url = url(request.getRequestURI());
        IamMethod iamMethod = IamMethod.valueOf(request.getMethod());

        try {
            boolean allowed = allowed(token, url, iamMethod);
            if(!allowed) {
                sendUnauthorized(response);
                return;
            }
            IamUser iamUser = user(token);
            if(iamUser == null) {
                sendUnauthorized(response);
                return;
            }
            request.setAttribute("user", iamUser);

        } catch (RuntimeException exception) {
            log.error("Filter exception {}", exception.getMessage());
            sendUnauthorized(response);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public IamUser user(String token) {
        IamUserToken userToken = Lookups.of(IamUserToken.class).get(token);
        return new IamUser(userToken.email(), userToken.name(),userToken.org(), userToken.token());
    }

    boolean allowed(String token, String url, IamMethod method) {
        try {
            IamTokenUrl tokenUrl = Lookups.of(IamTokenUrl.class).get(token, url);
            return tokenUrl.permissions().contains(method);
        } catch (Exception e) {
            log.error("Error checking allowed", e);
            return false;
        }
    }

    String url(String uri) {
        String[] parts = uri.substring(1).split("/");
        if(parts.length < 2)
            return "/" + parts[0];
        else
            return "/" + parts[0] + "/" + parts[1] + "/" + parts[2];
    }

    String token(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer"))
            return authHeader.replace("Bearer ", "");
        else
            return null;
    }

    void sendUnauthorized(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }


}
