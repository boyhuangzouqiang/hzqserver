package com.paymen.sm234.common.config;

import com.paymen.sm234.common.constants.SecretConstant;
import com.paymen.sm234.common.secret.SecretFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;


/**
 * @description:
 * @author: boykaff
 * @date: 2022-03-26-0026
 */
@Configuration
public class WebConfig {
    @Bean
    public Filter secretFilter() {
        return new SecretFilter();
    }


    @Bean
    public FilterRegistrationBean filterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new DelegatingFilterProxy("secretFilter"));
        registration.setName("secretFilter");
        registration.addUrlPatterns(SecretConstant.PREFIX + "/*");
        registration.setOrder(1);
        return registration;
    }
}
