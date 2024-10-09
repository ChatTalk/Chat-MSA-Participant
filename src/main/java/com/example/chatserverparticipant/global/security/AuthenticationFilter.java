package com.example.chatserverparticipant.global.security;

import com.example.chatserverparticipant.global.user.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("인증 시도: {}", requestURI);

        log.info("preFlight 요청인가? : {}", CorsUtils.isPreFlightRequest(request));
        log.info("cors 요청인가? : {}", CorsUtils.isCorsRequest(request));

        String username = request.getHeader("email");
        log.info("헤더 확인(username): {}", username);

        String role = request.getHeader("role");
        log.info("헤더 확인(role): {}", role);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(createAuthentication(username, role));
        SecurityContextHolder.setContext(context);

        log.info("인증 객체 생성 성공");

        filterChain.doFilter(request, response);
    }

    // Authentication 객체 생성 (UPAT 생성)
    private Authentication createAuthentication(String username, String role) {
        UserDetails userDetails = new UserDetailsImpl(username, role);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
