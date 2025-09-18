package com.example.crud.config;

import com.vaadin.flow.spring.SpringServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class AppConfig {

    @Bean
    public ServletRegistrationBean<SpringServlet> springServlet(WebApplicationContext context) {
        SpringServlet servlet = new SpringServlet(context, true) {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                if (request.getSession().getAttribute("locale") == null) {
                    request.getSession().setAttribute("locale", new java.util.Locale("id", "ID"));
                }
                super.service(request, response);
            }
        };
        ServletRegistrationBean<SpringServlet> registration = new ServletRegistrationBean<>(servlet, "/*");
        registration.setName("vaadin");
        return registration;
    }
}