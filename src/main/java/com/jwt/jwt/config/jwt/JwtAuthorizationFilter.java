package com.jwt.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jwt.jwt.config.auth.PrincipalDetails;
import com.jwt.jwt.model.Member;
import com.jwt.jwt.repository.MemberRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter{
	
	private MemberRepository memberRepository;
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository) {
		super(authenticationManager);
		this.memberRepository = memberRepository;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String header = request.getHeader(JwtProperties.HEADER_STRING);
		if(header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
			chain.doFilter(request, response);
                        return;
		}
		System.out.println("header : "+header);
		String token = request.getHeader(JwtProperties.HEADER_STRING)
				.replace(JwtProperties.TOKEN_PREFIX, "");

		String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
				.getClaim("username").asString();
		
		if(username != null) {	
			Member member = memberRepository.findByUsername(username);

			PrincipalDetails principalDetails = new PrincipalDetails(member);
			Authentication authentication =
					new UsernamePasswordAuthenticationToken(
							principalDetails,
							null,
							principalDetails.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
	
		chain.doFilter(request, response);
	}
	
}
