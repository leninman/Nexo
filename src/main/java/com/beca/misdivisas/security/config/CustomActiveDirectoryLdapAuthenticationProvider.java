package com.beca.misdivisas.security.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticationProvider;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.beca.misdivisas.util.Constantes;


public final class CustomActiveDirectoryLdapAuthenticationProvider extends AbstractLdapAuthenticationProvider {
	
	

	private static final Pattern SUB_ERROR_CODE = Pattern.compile(".*data\\s([0-9a-f]{3,4}).*");

	private static final int USERNAME_NOT_FOUND = 0x525;
	private static final int INVALID_PASSWORD = 0x52e;
	private static final int NOT_PERMITTED = 0x530;
	private static final int PASSWORD_EXPIRED = 0x532;
	private static final int ACCOUNT_DISABLED = 0x533;
	private static final int ACCOUNT_EXPIRED = 0x701;
	private static final int PASSWORD_NEEDS_RESET = 0x773;
	private static final int ACCOUNT_LOCKED = 0x775;

	private final String domain;
	private final String rootDn;
	private final String url;
	private boolean convertSubErrorCodesToExceptions;
	private String searchFilter = "(&(objectClass=user)(sAMAccountName={0}))";
								  

// this is your DAO class attribute and setter
	//private RoleDao roleDao;

	/*public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}*/

	ContextFactory contextFactory = new ContextFactory();

	public CustomActiveDirectoryLdapAuthenticationProvider(String domain, String url, String rootDn) {
		Assert.isTrue(StringUtils.hasText(url), "Url cannot be empty");
		this.domain = StringUtils.hasText(domain) ? domain.toLowerCase() : null;
		this.url = url;
		this.rootDn = StringUtils.hasText(rootDn) ? rootDn.toLowerCase() : null;
	}

	public CustomActiveDirectoryLdapAuthenticationProvider(String domain, String url) {
		Assert.isTrue(StringUtils.hasText(url), "Url cannot be empty");
		this.domain = StringUtils.hasText(domain) ? domain.toLowerCase() : null;
		this.url = url;
		rootDn = this.domain == null ? null : rootDnFromDomain(this.domain);
	}

	@Override
	protected DirContextOperations doAuthentication(UsernamePasswordAuthenticationToken auth) {
		String username = auth.getName();
		String password = (String) auth.getCredentials();

		DirContext ctx = bindAsUser(username, password);

		try {
			return searchForUser(ctx, username);
		} catch (javax.naming.NamingException e) {
			//logger.error("Failed to locate directory entry for authenticated user: " + username, e);
			throw badCredentials(e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}

	private DirContext bindAsUser(String username, String password) {
		final String bindUrl = url;
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		String bindPrincipal = createBindPrincipal(username);
		env.put(Context.SECURITY_PRINCIPAL, bindPrincipal);
		env.put(Context.PROVIDER_URL, bindUrl);
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		
		env.put(Context.OBJECT_FACTORIES, DefaultDirObjectFactory.class.getName());

		try {
			return contextFactory.createContext(env);
			
		} catch (javax.naming.NamingException e) {
			if ((e instanceof AuthenticationException) || (e instanceof OperationNotSupportedException)) {
				handleBindException(bindPrincipal, e);
				throw badCredentials(e);
			} else {
				throw LdapUtils.convertLdapException(e);
			}
		}
	}

	private void handleBindException(String bindPrincipal, NamingException exception) {
		//if (logger.isDebugEnabled()) {
		//	logger.debug("Authentication for " + bindPrincipal + " failed:" + exception);
		//}
		int subErrorCode = parseSubErrorCode(exception.getMessage());
		if (subErrorCode <= 0) {
			//logger.debug("Failed to locate AD-specific sub-error code in message");
			return;
		}
		//logger.info("Active Directory authentication failed: " + subCodeToLogMessage(subErrorCode));
		if (convertSubErrorCodesToExceptions) {
			raiseExceptionForErrorCode(subErrorCode, exception);
		}
	}

	private int parseSubErrorCode(String message) {
		Matcher m = SUB_ERROR_CODE.matcher(message);
		if (m.matches()) {
			return Integer.parseInt(m.group(1), 16);
		}
		return -1;
	}

	private void raiseExceptionForErrorCode(int code, NamingException exception) {
		String hexString = Integer.toHexString(code);
		//Throwable cause = new ActiveDirectoryAuthenticationException(hexString, exception.getMessage(), exception);
		switch (code) {
		case PASSWORD_EXPIRED:
			throw new CredentialsExpiredException("LdapAuthenticationProvider.credentialsExpired"+
					"User credentials have expired", exception);
		case ACCOUNT_DISABLED:
			throw new DisabledException("LdapAuthenticationProvider.disabled" + "User is disabled",
					exception);
		case ACCOUNT_EXPIRED:
			throw new AccountExpiredException(
					"LdapAuthenticationProvider.expired"+"User account has expired", exception);
		case ACCOUNT_LOCKED:
			throw new LockedException(
					"LdapAuthenticationProvider.locked"+ "User account is locked", exception);
		default:
			throw badCredentials(exception);
		}
	}

	private String subCodeToLogMessage(int code) {
		switch (code) {
		case USERNAME_NOT_FOUND:
			return "User was not found in directory";
		case INVALID_PASSWORD:
			return "Supplied password was invalid";
		case NOT_PERMITTED:
			return "User not permitted to logon at this time";
		case PASSWORD_EXPIRED:
			return "Password has expired";
		case ACCOUNT_DISABLED:
			return "Account is disabled";
		case ACCOUNT_EXPIRED:
			return "Account expired";
		case PASSWORD_NEEDS_RESET:
			return "User must reset password";
		case ACCOUNT_LOCKED:
			return "Account locked";
		}
		return "Unknown (error code " + Integer.toHexString(code) + ")";
	}

	private BadCredentialsException badCredentials() {
		return new BadCredentialsException("LdapAuthenticationProvider.badCredentials" + "Bad credentials");
	}

	private BadCredentialsException badCredentials(Throwable cause) {
		return (BadCredentialsException) badCredentials().initCause(cause);
	}
	
	/*@Override
    public Authentication authenticate(Authentication auth) {
        WebAuthenticationDetails details = (WebAuthenticationDetails) auth.getDetails();
        String userIp = details.getRemoteAddress();
		return auth;
       
	}*/

	private DirContextOperations searchForUser(DirContext context, String username) throws javax.naming.NamingException {
		SearchControls searchControls = new SearchControls();
		String returnedAtts[] ={ "sn", "givenName", "name", "userPrincipalName", "displayName", "memberOf" };
		searchControls.setReturningAttributes(returnedAtts);
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String bindPrincipal = createBindPrincipal(username);
		String searchRoot = rootDn != null ? rootDn : searchRootFromPrincipal(bindPrincipal);

		try {
			return SpringSecurityLdapTemplate.searchForSingleEntryInternal(context, searchControls, searchRoot,	searchFilter, new Object[] { username });
		} catch (IncorrectResultSizeDataAccessException incorrectResults) {
			// Search should never return multiple results if properly configured - just
			// rethrow
			if (incorrectResults.getActualSize() != 0) {
				throw incorrectResults;
			}
			// If we found no results, then the username/password did not match
			UsernameNotFoundException userNameNotFoundException = new UsernameNotFoundException(
					"User " + username + " not found in directory.", incorrectResults);
			throw badCredentials(userNameNotFoundException);
		}
	}

	private String searchRootFromPrincipal(String bindPrincipal) {
		int atChar = bindPrincipal.lastIndexOf('@');
		if (atChar < 0) {
			//logger.debug("User principal '" + bindPrincipal	+ "' does not contain the domain, and no domain has been configured");
			throw badCredentials();
		}
		return rootDnFromDomain(bindPrincipal.substring(atChar + 1, bindPrincipal.length()));
	}

	private String rootDnFromDomain(String domain) {
		String[] tokens = StringUtils.tokenizeToStringArray(domain, ".");
		StringBuilder root = new StringBuilder();
		for (String token : tokens) {
			if (root.length() > 0) {
				root.append(',');
			}
			root.append("dc=").append(token);
		}
		return root.toString();
	}

	String createBindPrincipal(String username) {
		if (domain == null || username.toLowerCase().endsWith(domain)) {
			return username;
		}
		return username + "@" + domain;
	}

	public void setConvertSubErrorCodesToExceptions(boolean convertSubErrorCodesToExceptions) {
		this.convertSubErrorCodesToExceptions = convertSubErrorCodesToExceptions;
	}

	public void setSearchFilter(String searchFilter) {
		Assert.hasText(searchFilter, "searchFilter must have text");
		this.searchFilter = searchFilter;
	}

	static class ContextFactory {
		DirContext createContext(Hashtable<?, ?> env) throws javax.naming.NamingException {
			return new InitialLdapContext(env, null);
		}
	}

	@Override
	protected Collection<? extends GrantedAuthority> loadUserAuthorities(DirContextOperations userData, String username, String password) {
		
		String[] groups = userData.getStringAttributes("memberOf");
		if (groups == null) {
			//log.debug("No values for 'memberOf' attribute. No Authorities in Active Directory!");
			//return AuthorityUtils.NO_AUTHORITIES;
			throw badCredentials(new Exception());
		}
		//if (log.isDebugEnabled()) {
			//logger.debug("'memberOf' attribute values: " + Arrays.asList(groups));
		//}

		List<GrantedAuthority> authorities = createGrantedAuthoritiesFromLdapGroups(groups);
		
		if (authorities == null || authorities.isEmpty()) {
			//log.debug("No values for 'memberOf' attribute. No Authorities in Active Directory!");
			//return AuthorityUtils.NO_AUTHORITIES;
			throw badCredentials(new Exception());
		}
		return authorities;
	}
	
	private List<GrantedAuthority> createGrantedAuthoritiesFromLdapGroups(String[] groups) {
		
	List<String> groupNames = new ArrayList<>(groups.length);
	//'groups' is array of Acitve Directory groups which user that tries to authenticate has. 
	for (String group : groups) {
		String groupName = new DistinguishedName(group).removeLast().getValue();
		groupNames.add(groupName);
	}

	//I use Active Directory groups that user which tries to login has and get all application privileges for them from database.
	//You can map privileges or roles form database to application roles and easily use them in application for example in @Secured annotation
	
	///List<String> privileges = roleDao.findPrivilegesForLDAPGroups(groupNames);
	List<String> privileges = new ArrayList<String>();
	if(groupNames.contains("GSEG-Nexo-Divisas_ADMIN"))
		privileges.add(Constantes.ROL_ADMIN_BECA);
	//Your roles/privileges in database need to have 'ROLE_' prefix or you need to append it here.
	String DEFAULT_ROLE_PREFIX = "ROLE_";
	
	return privileges.stream()
			.map(privilege -> org.apache.commons.lang3.StringUtils.appendIfMissing(DEFAULT_ROLE_PREFIX, privilege))
			.map(privilege -> new SimpleGrantedAuthority((String) privilege)).collect(Collectors.toList());
	}
	
}