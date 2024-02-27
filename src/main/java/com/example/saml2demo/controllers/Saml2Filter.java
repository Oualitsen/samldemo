package com.example.saml2demo.controllers;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Saml2Filter extends OncePerRequestFilter {

    final String forwardRequestUrl = "/login/saml2/sso/adfs";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if ("/saml/SSO".equals(uri)) {
            log.info("Forwarding request to {}", forwardRequestUrl);
            
            request.getRequestDispatcher(forwardRequestUrl).forward(request, response);
            //response.sendRedirect(request.getContextPath() + "/login/saml2/sso/adfs");
            return;
        }

        filterChain.doFilter(request, response);
    }

}
