package com.mibanco.exchangerate.infrastructure.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter implements Filter {

    private static final String AUTH_TOKEN = "Bearer 123456";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {


        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader("Authorization");

        String path = httpRequest.getRequestURI();
        if (path.startsWith("/swagger") || path.startsWith("/v3/api-docs")) {
            chain.doFilter(request, response);
            return;
        }
        if (!AUTH_TOKEN.equals(authHeader)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("No autorizado: token inválido o ausente");
            return;
        }

        chain.doFilter(request, response);
    }
}
