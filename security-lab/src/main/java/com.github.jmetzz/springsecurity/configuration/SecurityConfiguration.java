package com.github.jmetzz.springsecurity.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * This configuration creates a Servlet Filter known as the springSecurityFilterChain
 * which is responsible for all the security (protecting the application URLs,
 * validating submitted username and passwords, redirecting to the log in form, etc)
 * within our application
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        // using in-memory authentication for the sake of simplicity,
        // while you are free to choose from JDBC, LDAP or any other authentication method.
        auth.inMemoryAuthentication().withUser("bill").password("abc123").roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password("root123").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("dba").password("dba123").roles("ADMIN", "DBA");
    }


    /**
     * Configures HttpSecurity which allows configuring web based security for specific http requests
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/", "/home").permitAll() //URL’s ‘/’ & ‘/home’ are not secured
                .antMatchers("/admin/**").access("hasRole('ADMIN')") //URL ‘/admin/**’ can only be accessed by someone who have ADMIN role
                .antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')") //URL ‘/db/**’ can only be accessed by someone who have both ADMIN and DBA roles
                .and().formLogin()
                    .loginPage("/login") // set our own Custom login form to be launched instead of the default one provided by spring
                    .usernameParameter("ssoId")
                    .passwordParameter("password")
                    .and().csrf() // Cross Site Request Forgery security is enable by default. It is explicitly stated here for the sake of clarity
                .and().exceptionHandling().accessDeniedPage("/Access_Denied"); // in this case will catch all 403 [http access denied]
                                                                               // exceptions and display our user defined page instead of showing default HTTP 403 page

    }


    /* All this configuration could be done in xml as follow: */
    /*
    <beans:beans xmlns="http://www.springframework.org/schema/security"
    xmlns:beans="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
    http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-4.0.xsd">

    <http auto-config="true" >
        <intercept-url pattern="/" access="permitAll" />
        <intercept-url pattern="/home" access="permitAll" />
        <intercept-url pattern="/admin**" access="hasRole('ADMIN')" />
        <intercept-url pattern="/dba**" access="hasRole('ADMIN') and hasRole('DBA')" />
        <form-login  authentication-failure-url="/Access_Denied" />
        <form-login  login-page="/login" username-parameter="ssoId" password-parameter="password" authentication-failure-url="/Access_Denied" />
        <csrf/>
    </http>

    <authentication-manager >
        <authentication-provider>
            <user-service>
                <user name="bill"  password="abc123"  authorities="ROLE_USER" />
                <user name="admin" password="root123" authorities="ROLE_ADMIN" />
                <user name="dba"   password="dba123" authorities="ROLE_ADMIN,ROLE_DBA" />
            </user-service>
        </authentication-provider>
    </authentication-manager>

    </beans:beans>
     */
}
