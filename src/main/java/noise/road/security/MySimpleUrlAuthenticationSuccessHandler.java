package noise.road.security;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import noise.road.admin.listener.LoggedUser;
import noise.road.admin.model.ActiveUserStore;
import noise.road.service.UserDataCleanupService;


@Slf4j
public class MySimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	@Autowired
	private UserDataCleanupService userDataCleanupService;
	
	@Autowired
    ActiveUserStore activeUserStore;
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
      HttpServletResponse response, Authentication authentication)
      throws IOException {
		
		// keep track of logged in users
		HttpSession session = request.getSession(false);
        if (session != null) {
            LoggedUser user = new LoggedUser(authentication.getName(), getEmail(authentication), activeUserStore);
            activeUserStore.getLoggedUsers().add(user);
            session.setAttribute("user", user);
        }
        
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }
	
	protected void handle(
	        HttpServletRequest request,
	        HttpServletResponse response, 
	        Authentication authentication
	) throws IOException {

	    String targetUrl = determineTargetUrl(authentication);

	    if (response.isCommitted()) {
	        log.debug(
	                "Response has already been committed. Unable to redirect to "
	                        + targetUrl);
	        return;
	    }
	    
	    // Set the session timeout 
        request.getSession().setMaxInactiveInterval(600);
        
        // clear tables before login
        userDataCleanupService.cleanupUserData(authentication.getName());

	    redirectStrategy.sendRedirect(request, response, targetUrl);
	}
	
	protected String determineTargetUrl(final Authentication authentication) {

	    Map<String, String> roleTargetUrlMap = new HashMap<>();
	    roleTargetUrlMap.put("ROLE_USER", "/console/upload");
	    roleTargetUrlMap.put("ROLE_ADMIN", "/admin/home");
	    roleTargetUrlMap.put("ROLE_GUEST", "/console/display");

	    final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
	    for (final GrantedAuthority grantedAuthority : authorities) {
	        String authorityName = grantedAuthority.getAuthority();
	        if(roleTargetUrlMap.containsKey(authorityName)) {
	            return roleTargetUrlMap.get(authorityName);
	        }
	    }

	    throw new IllegalStateException();
	}
	
	protected void clearAuthenticationAttributes(HttpServletRequest request) {
	    HttpSession session = request.getSession(false);
	    if (session == null) {
	        return;
	    }
	    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}
	
	private String getEmail(Authentication authentication) {
	    Object principal = authentication.getPrincipal();
	    if (principal instanceof CustomUserDetails) {
	        return ((CustomUserDetails) principal).getEmail();
	    } else {
	        // Handle other cases as needed
	        return null;
	    }
	}
	

}
