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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.http.HttpServletResponse;
import noise.road.admin.model.ActiveUserStore;



@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
    private CustomLogoutHandler logoutHandler;
	
	@Autowired
	private CustomLogoutSuccessHandler logoutSuccessHandler;
	
	@Bean
	AuthenticationEntryPoint customAuthenticationEntryPoint() {
		return new CustomAuthenticationEntryPoint();
	}
	
	@Bean
	HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}
	
	@Bean
	InvalidSessionStrategy myCustomInvalidSessionStrategy() {
		return new MyCustomInvalidSessionStrategy();
	}
	
	@Bean
    ActiveUserStore activeUserStore(){
        return new ActiveUserStore();
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
	            	auth.requestMatchers("/admin/**").hasRole("ADMIN");
	            	auth.requestMatchers("/console/**").hasAnyRole("ADMIN", "USER");
	                auth.anyRequest().authenticated();
	            })	 
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
	                    auth.requestMatchers("/static/**").permitAll();
	                    auth.requestMatchers("/registration/**").permitAll();
	                    auth.requestMatchers("/contact/**").permitAll();
	                    auth.requestMatchers("/error/**").permitAll();
	                    auth.requestMatchers("/login").permitAll();
	                    auth.requestMatchers("/logout").permitAll();
	                    auth.requestMatchers("/session/**").permitAll();
	                    auth.anyRequest().authenticated();
	                }
	            )
	            .formLogin(form -> form
	            		.loginPage("/login")
	            		.loginProcessingUrl("/login")
	            		.successHandler(myAuthenticationSuccessHandler())
	            		.failureUrl("/login?error=true")
	            		.permitAll()
	            		)	 
	            .sessionManagement(session -> session
	            		.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
	            	//	.invalidSessionUrl("/session-expired")
	            	//	.sessionAuthenticationErrorUrl("/login?sessionExpired=true")
	            		.sessionFixation((sessionFixation) -> sessionFixation
	                            .newSession()
	                        )
	            		.maximumSessions(1)
	            		.maxSessionsPreventsLogin(true)
	            //		.expiredUrl("/session-expired")
	            		)
	            .logout(logout -> logout
	            		.logoutUrl("/logout")
	            		.addLogoutHandler(logoutHandler)
	            		.logoutSuccessHandler(logoutSuccessHandler)
	            		.invalidateHttpSession(true)
	            		.deleteCookies("JSESSIONID")
	            		)  
	         //   .exceptionHandling(exceptionHandling -> exceptionHandling
	         //   		.authenticationEntryPoint(customAuthenticationEntryPoint()))
	            .build();
	   
	}

}
