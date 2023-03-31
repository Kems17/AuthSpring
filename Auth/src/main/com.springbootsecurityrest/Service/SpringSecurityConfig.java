package Service;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import Service.UserService;
import org.springframework.stereotype.Repository;
import security.JwtTokenRepository;
import security.JwtCsrfFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import java.util.Collections;
import org.springframework.context.annotation.ComponentScan;



@Repository
@Configuration
@EnableWebSecurity
@ComponentScan
public class SpringSecurityConfig {

    @Autowired
    private UserService service;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Bean
    public PasswordEncoder devPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public JwtCsrfFilter jwtCsrfFilter() {
        return new JwtCsrfFilter(jwtTokenRepository, resolver);
    }

    @Bean
    public SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> securityConfigurer() {
        return new SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity>() {
            @Override
            public void init(HttpSecurity http) throws Exception {
                http
                        .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                        .and()
                        .csrf()
                        .ignoringAntMatchers("/**")
                        .and()
                        .authorizeRequests()
                        .antMatchers("/auth/login")
                        .authenticated()
                        .and()
                        .httpBasic()
                        .authenticationEntryPoint((request, response, e) -> resolver.resolveException(request, response, null, e))
                        .and()
                        .apply(jwtCsrfFilter());
            }

            @Override
            public void configure(HttpSecurity http) throws Exception {
                init(http);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .apply(securityConfigurer())
                .and()
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Collections.singletonList(userDetailsService()));
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return service;
    }
}
