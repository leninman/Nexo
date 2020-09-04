package com.beca.misdivisas.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.beca.misdivisas.interfaces.ILogRepo;

@EnableWebSecurity
@Configuration
@EnableAutoConfiguration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${ldap.domain}")
	private String ldapDomain;

	@Value("${ldap.url}")
	private String ldapUrl;
	
	@Value("${ldap.base.dn}")
	private String ldapBaseDn;	
	
	@Autowired
	private UserDetailsService userDetailsService;	
	
	@Autowired
	private BCryptPasswordEncoder bCrypt;
	
	@Autowired
	private ILogRepo logRepo;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;

	}
	

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {		
			auth.authenticationProvider(activeDAOAuthenticationProvider());		
			auth.authenticationProvider(activeDirectoryLdapAuthenticationProvider());

	}

	
	@Bean
	public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
		CustomActiveDirectoryLdapAuthenticationProvider provider = new CustomActiveDirectoryLdapAuthenticationProvider(
				ldapDomain, ldapUrl, ldapBaseDn);
		provider.setConvertSubErrorCodesToExceptions(true);
		provider.setUseAuthenticationRequestCredentials(true);
		//provider.setRoleDao(roleDao);
		return provider;
	}
	
	@Bean
	public DaoAuthenticationProvider activeDAOAuthenticationProvider() {
	    CustomDAOAuthenticationProvider authProvider = new CustomDAOAuthenticationProvider();
		//DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userDetailsService);
	    authProvider.setPasswordEncoder(bCrypt);
	    return authProvider;
	}
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {

		 http
		 .csrf().disable()		 
		 .authorizeRequests()		 
		 .antMatchers("/css/**", "/ol/**",
		 "/img/**",
		 "/js/**",
		 "/scss/**",
		 "/tmp/**",
		 "/vendor/**").permitAll()		 
		 .antMatchers("/login*").permitAll()		 
		 .anyRequest().authenticated()		 
         .and()
         .formLogin()
         .loginPage("/login") //the custom login page
         .loginProcessingUrl("/login") //the url to submit the username and password to
         .usernameParameter("username")
         .passwordParameter("password")
         .successHandler(new CustomAuthenticationSuccessHandler())
		 //.defaultSuccessUrl("/main", true)  //the landing page after a successful login
         .failureUrl("/login?error") //the landing page after an unsuccessful login
         //.failureHandler(authenticationFailureHandler())         
         .and()		 
		 .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).clearAuthentication(true)
		 .addLogoutHandler(new CustomLogoutHandler(logRepo))
		 .logoutSuccessUrl("/login?logout").deleteCookies("JSESSIONID")
		 .invalidateHttpSession(true);
    }
}