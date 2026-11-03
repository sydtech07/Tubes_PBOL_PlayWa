package com.playwa.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Penjaga pintu: halaman game hanya untuk yang sudah login. */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("playerId") != null) return true;
        res.sendRedirect("/login");
        return false;
    }
}
