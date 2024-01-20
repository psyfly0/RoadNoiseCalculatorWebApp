package noise.road.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
    private CustomLogoutHandler logoutHandler;
	
	@Bean
	AccessDeniedHandler accessDeniedHandler(){
	    return new CustomAccessDeniedHandler();
	}
    
	@Bean
	PasswordEncoder encoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
	RoleHierarchy roleHierarchy() {
	    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
	    String hierarchy = "ROLE_ADMIN > ROLE_USER > ROLE_GUEST";
	    roleHierarchy.setHierarchy(hierarchy);
	    return roleHierarchy;
	}
	
	@Bean
	DefaultWebSecurityExpressionHandler overriddenWebSecurityExpressionHandler() {
	    DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
	    expressionHandler.setRoleHierarchy(roleHierarchy());
	    return expressionHandler;
	}
	
	@Bean
	AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
	    return new MySimpleUrlAuthenticationSuccessHandler();
	}
	
	@Bean
	@Order(1)
	SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
	    return http
	    		
	    		.csrf(csrf -> csrf.disable())
	            .securityMatcher("/console/**", "/calculations/**", "/sortAndDifferences/**", "/modification/**")
	            .authorizeHttpRequests(auth -> {
	            	auth.requestMatchers("/console/display", "/console/displayData",
	            			"/console/saveFile/**", "/console/saveProtectiveDistance/**",
	            			"/console/saveImpactAreaDistance/**", "/console/saveToExcel/**").hasRole("GUEST");
	            	auth.requestMatchers("/console/**").hasAnyRole("ADMIN", "USER");
	                auth.anyRequest().authenticated();
	            })
	            .sessionManagement(session -> session
	            		.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
	            		.invalidSessionUrl("/")
	            		.sessionFixation().newSession()
	            		.maximumSessions(1).maxSessionsPreventsLogin(true)
	            		.expiredUrl("/"))
	            .exceptionHandling(configurer -> configurer
	                    .accessDeniedHandler(accessDeniedHandler())
	                )
	            .httpBasic(Customizer.withDefaults())
	            .build();
	}
	
	@Bean
	@Order(2)
	SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
	    return http
	            .securityMatcher(AntPathRequestMatcher.antMatcher("/h2-console/**"))
	            .authorizeHttpRequests( auth -> {
	                auth.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll();
	            })
	            .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")))
	            .headers(headers -> headers
	                    .frameOptions(frameOptions -> frameOptions
	                        .sameOrigin()
	                    )
	                )
	            .build();
	}

	@Bean
	@Order(3)
	SecurityFilterChain loginSecurityFilterChain(HttpSecurity http) throws Exception {
	    return http
	            .authorizeHttpRequests(auth -> {
	                    auth.requestMatchers("/").permitAll();
	                    auth.requestMatchers("/registration/**").permitAll();
	                    auth.requestMatchers("/error/**").permitAll();
	                    auth.requestMatchers("/login").permitAll();
	                    auth.requestMatchers("/logout").permitAll();
	                    auth.anyRequest().authenticated();
	                }
	            )
	            .formLogin(form -> form
	            		.loginPage("/login")
	            		.loginProcessingUrl("/login")
	            		.successHandler(myAuthenticationSuccessHandler())
	            		.failureUrl("/login?error=true	")
	            		.permitAll()
	            		)
	            .logout(logout -> logout
	            		.logoutUrl("/logout")
	            		.addLogoutHandler(logoutHandler)
	            		.logoutSuccessUrl("/")
	            		.invalidateHttpSession(true)
	            		.deleteCookies("JSESSIONID")
	            		)       
	            .build();
	}


}
