package com.example.crud.config;

import com.vaadin.flow.spring.SpringServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Configuration
public class AppConfig {

    @Bean
    public ServletRegistrationBean<SpringServlet> springServlet(WebApplicationContext context) {
        SpringServlet servlet = new SpringServlet(context, true) {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                // Set the default language if not already set
                if (request.getSession().getAttribute("locale") == null) {
                    request.getSession().setAttribute("locale", new Locale("id", "ID"));
                }
                super.service(request, response);
            }
        };
        ServletRegistrationBean<SpringServlet> registration = new ServletRegistrationBean<>(servlet, "/*");
        registration.setName("vaadin");
        return registration;
    }
}