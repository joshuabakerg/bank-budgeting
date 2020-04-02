package za.co.joshuabakerg.bankimport.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.core.impl.UserDetailsServiceImpl;
import za.co.joshuabakerg.bankimport.utils.JwtTokenUtil;

/**
 * @author Joshua Baker on 18/12/2019
 */
@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        final Optional<String> jwtToken = getToken(requestTokenHeader);
        final Optional<String> oUsername = jwtToken.map(jwtTokenUtil::getUsernameFromToken);

        oUsername.filter(s -> SecurityContextHolder.getContext().getAuthentication() == null)
                .ifPresent(username -> {
                    final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtTokenUtil.validateToken(jwtToken.get(), userDetails)) {
                        final UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                });

        chain.doFilter(request, response);
    }

    private Optional<String> getToken(final String requestTokenHeader) {
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            return Optional.of(requestTokenHeader.substring(7));
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }
        return Optional.empty();
    }

}
