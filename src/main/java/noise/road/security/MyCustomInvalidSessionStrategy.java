package noise.road.security;

import java.io.IOException;

import org.springframework.security.web.session.InvalidSessionStrategy;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyCustomInvalidSessionStrategy implements InvalidSessionStrategy {

	@Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.sendRedirect("/login?logout=true");
    }
}
