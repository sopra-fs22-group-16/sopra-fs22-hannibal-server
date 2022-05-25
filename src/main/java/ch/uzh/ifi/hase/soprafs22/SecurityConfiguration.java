package ch.uzh.ifi.hase.soprafs22;

import ch.uzh.ifi.hase.soprafs22.service.UserPropsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Value("${api.version}")
    private String apiVersion;

    @Override
    protected void configure(@NotNull HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests().antMatchers("/").permitAll().and()
                .authorizeRequests().antMatchers("/console/**").permitAll()
                .and().cors();
        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
    }

    private static final String ORIGIN_LOCALHOST = "http://localhost:3000";
    private static final String ORIGIN_PROD = "https://sopra-fs22-group-16-client.herokuapp.com";


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowedOriginPatterns(List.of(ORIGIN_LOCALHOST, ORIGIN_PROD));
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.applyPermitDefaultValues();
        source.registerCorsConfiguration("/"+apiVersion+"/**", configuration);
        source.registerCorsConfiguration("/"+apiVersion+"/*", configuration);
        source.registerCorsConfiguration("/v1/*", configuration);
        source.registerCorsConfiguration("/v1/*", configuration);
        source.registerCorsConfiguration("/hannibal-websocket/*", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserPropsService();
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(@NotNull AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }
}