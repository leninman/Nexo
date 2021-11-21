package com.beca.misdivisas.security.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import javax.naming.ldap.Rdn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

	private static final int PASSWORD_EXPIRED = 0x532; // 1330
	private static final int ACCOUNT_DISABLED = 0x533; // 1331
	private static final int ACCOUNT_EXPIRED = 0x701; // 1793
	private static final int ACCOUNT_LOCKED = 0x775; // 1909
	private static final int PASSWORD_MUST_CHANGED = 0x773; // 1907
	private static final int USER_NOT_FOUND = 0x525; // 1317
	private static final int BAD_PASSWORD = 0x52e; // 1326

	private final String domain;
	private final String rootDn;
	private final String url;
	private boolean convertSubErrorCodesToExceptions;
	private String searchFilter = "(&(objectClass=user)(sAMAccountName={0}))";

	private String ipOrigen;
	private Boolean peticionInterna;
	private Authentication autenticacion;
	private Integer numeroAgencia;
	
	@Value("${ldap.oficinas.dominio}")
	private String dominio;
	
	@Value("${ldap.oficinas.ou}")
	private String ou;

	ContextFactory contextFactory = new ContextFactory();

	@Autowired
	private static final Logger logger = LoggerFactory.getLogger(CustomActiveDirectoryLdapAuthenticationProvider.class);

	public CustomActiveDirectoryLdapAuthenticationProvider(String domain, String ip, String port) {
		Assert.isTrue(StringUtils.hasText(port), "El puerto es requerido para la conexion via LDAP");
		Assert.isTrue(StringUtils.hasText(domain), "El dominio es requerido para la conexion via LDAP");
		this.domain = domain.toLowerCase();
		this.url = "ldaps://" + (StringUtils.hasText(ip) ? ip : this.domain) + ":" + port;
		rootDn = rootDnFromDomain(this.domain);
	}
	/*
	 * public CustomActiveDirectoryLdapAuthenticationProvider(String domain, String
	 * url, String rootDn) { Assert.isTrue(StringUtils.hasText(url),
	 * "Url cannot be empty"); this.domain = StringUtils.hasText(domain) ?
	 * domain.toLowerCase() : null; this.url = url; this.rootDn =
	 * StringUtils.hasText(rootDn) ? rootDn.toLowerCase() : null; }
	 * 
	 * public CustomActiveDirectoryLdapAuthenticationProvider(String domain, String
	 * url) { Assert.isTrue(StringUtils.hasText(url), "Url cannot be empty");
	 * this.domain = StringUtils.hasText(domain) ? domain.toLowerCase() : null;
	 * this.url = url; rootDn = this.domain == null ? null :
	 * rootDnFromDomain(this.domain); }
	 */

	@Override
	public Authentication authenticate(Authentication authentication) {
		autenticacion = authentication;
		CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) authentication.getDetails();
		setIpOrigen(details.getIpOrigen());
		peticionInterna = details.isPeticionInterna();

		if (peticionInterna)
			return super.authenticate(authentication);
		else
			throw new InternalAuthenticationServiceException("No se encuentra conectado a la red interna");
	}

	@Override
	protected DirContextOperations doAuthentication(UsernamePasswordAuthenticationToken auth) {
		String username = auth.getName().toUpperCase();
		String password = (String) auth.getCredentials();

		DirContext ctx = bindAsUser(username, password);

		try {
			return searchForUser(ctx, username);
		} catch (javax.naming.NamingException e) {
			logger.error(e.getLocalizedMessage());
			throw new InternalAuthenticationServiceException("Error del sistema", e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}

	private DirContext bindAsUser(String username, String password) {
		final String bindUrl = url;
		System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", "true");
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		String bindPrincipal = createBindPrincipal(username);
		env.put(Context.SECURITY_PRINCIPAL, bindPrincipal);
		env.put(Context.PROVIDER_URL, bindUrl);
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put(Context.SECURITY_PROTOCOL, "ssl");
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put("java.naming.ldap.factory.socket", CustomSSLSocketFactory.class.getName());
		env.put(Context.OBJECT_FACTORIES, DefaultDirObjectFactory.class.getName());

		try {
			return contextFactory.createContext(env);

		} catch (javax.naming.NamingException e) {
			logger.error(e.getLocalizedMessage());
			if ((e instanceof AuthenticationException) || (e instanceof OperationNotSupportedException)) {
				int subErrorCode = parseSubErrorCode(e.getMessage());
				if (subErrorCode > 0) {
					if (convertSubErrorCodesToExceptions) {
						switch (subErrorCode) {
						case PASSWORD_EXPIRED:
							throw new CredentialsExpiredException("ContraseÃ±a expirÃ³", e);
						case ACCOUNT_DISABLED:
							throw new DisabledException("Cuenta deshabilitada", e);
						case ACCOUNT_EXPIRED:
							throw new AccountExpiredException("Cuenta expirada", e);
						case ACCOUNT_LOCKED:
							throw new LockedException("Cuenta bloqueada", e);
						case PASSWORD_MUST_CHANGED:
							throw new CredentialsExpiredException("ContraseÃ±a debe cambiarse", e);
						case USER_NOT_FOUND:
							throw new AuthenticationCredentialsNotFoundException("Usuario no encontrado", e);
						case BAD_PASSWORD:
							throw new BadCredentialsException("Credenciales incorrectas", e);
						default:
							throw new AuthenticationServiceException("Credenciales incorrectas - Error General", e);
						}
					} else {
						throw LdapUtils.convertLdapException(e);
					}
				} else {
					throw LdapUtils.convertLdapException(e);
				}
			} else if (e instanceof javax.naming.CommunicationException) {
				// Error en la comunicacion
				throw new InternalAuthenticationServiceException("No hay comunicacion con el AD", e);
			} else {
				throw LdapUtils.convertLdapException(e);
			}
		}
	}

	private int parseSubErrorCode(String message) {
		Matcher m = SUB_ERROR_CODE.matcher(message);
		if (m.matches()) {
			return Integer.parseInt(m.group(1), 16);
		}
		return -1;
	}

	private DirContextOperations searchForUser(DirContext context, String username)
			throws javax.naming.NamingException {
		SearchControls searchControls = new SearchControls();
		String returnedAtts[] = { "userPrincipalName", "givenName", "name", "sn", "displayName", "department",
				"description", "distinguishedName", "memberOf", "l" };
		searchControls.setReturningAttributes(returnedAtts);
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String bindPrincipal = createBindPrincipal(username);
		String searchRoot = rootDn != null ? rootDn : searchRootFromPrincipal(bindPrincipal);

		try {
			return SpringSecurityLdapTemplate.searchForSingleEntryInternal(context, searchControls, searchRoot,
					searchFilter, new Object[] { username });
		} catch (IncorrectResultSizeDataAccessException incorrectResults) {
			if (incorrectResults.getActualSize() != 0) {
				throw incorrectResults;
			}
			// If we found no results, then the username/password did not match
			UsernameNotFoundException userNameNotFoundException = new UsernameNotFoundException(
					"User " + username + " not found in directory.", incorrectResults);
			throw new AuthenticationCredentialsNotFoundException("Usuario no encontrado", userNameNotFoundException);
		}
	}

	private String searchRootFromPrincipal(String bindPrincipal) {
		int atChar = bindPrincipal.lastIndexOf('@');
		if (atChar < 0) {
			throw new AuthenticationCredentialsNotFoundException("Usuario no encontrado");
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
	protected Collection<? extends GrantedAuthority> loadUserAuthorities(DirContextOperations userData, String username,
			String password) {

		String[] groups = userData.getStringAttributes("memberOf");
		if (groups == null) {
			throw new AuthenticationServiceException("No pertenece a ningun grupo en el AD");
		}
		List<GrantedAuthority> authorities = createGrantedAuthoritiesFromLdapGroups(groups);

		if (authorities == null || authorities.isEmpty()) {
			throw new AuthenticationServiceException("No pertenece a ningun grupo en el AD");
		}
		return authorities;
	}

	private List<GrantedAuthority> createGrantedAuthoritiesFromLdapGroups(String[] groups) {
		List<String> privileges = new ArrayList<>();
		List<String> groupNames = new ArrayList<>(groups.length);

		CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) autenticacion.getDetails();

		if (!domain.contains("oficinas")) {
			details.setNumeroAgencia(1);
			numeroAgencia = 1;
		} else {
			// TODO si esta instancia es para torre no se debe buscar la agencia en los
			// grupos solo setear el valor de 1
			// si esta instancia es para oficinas alli si buscar la agencia en grupos
			boolean validDC = false;
			boolean validOU = false;
			String valorDC = null;
			String valorOU = "";
			String valorCN = null;
			List<String> valoresOU = new ArrayList();
			List<String> valoresCN = new ArrayList();
			
			numeroAgencia = 0;
			
			if(domain.equals(dominio)) {
				validDC = true;
			}
			
			for (String group : groups) {
				valoresOU.clear();
				valoresCN.clear();
				for (Rdn rdn : LdapUtils.newLdapName(group).getRdns()) {
					// TODO
					// recorrer la estructura de cada grupo y si corresponde con la indicada tomar
					// el numero de la agencia
					// validar que sea un numero
					// si es un numero, rompen el ciclo y setean en details.setNumeroAgencia(null);
					
					if (rdn.getType().equalsIgnoreCase("OU")) {
						valoresOU.add((String) rdn.getValue());
					}
					if (rdn.getType().equalsIgnoreCase("CN")) {
                        valoresCN.add((String) rdn.getValue());
                    }
				}
				Collections.reverse(valoresOU);
				Collections.reverse(valoresCN);
			    for (int k = 0; k < valoresOU.size(); k++) {
                    if (k > 0) {
                        valorOU = valorOU.concat(".").concat(valoresOU.get(k));
                    } else
                        valorOU = valoresOU.get(0);
                }
			    if(valorOU.equalsIgnoreCase(ou.trim())) {
			    	validOU = true;
			    }
			    for (int j = 0; j < valoresCN.size(); j++) {
                    if (j > 0) {
                        valorCN = valorCN.concat(".").concat(valoresCN.get(j));
                    } else
                        valorCN = valoresCN.get(0);
                }
				if (validDC && validOU) {
					int slashPosition = valorCN.lastIndexOf("-");
					if(slashPosition != -1) {
						try {
							numeroAgencia = Integer.valueOf(valorCN.substring(slashPosition + 1).trim());	
						} catch (Exception e) {
							numeroAgencia = 0;
						}
					}
				}
				if(numeroAgencia!=0) {
					details.setNumeroAgencia(numeroAgencia);
					break;
				}
			}				
		}
		
		if(numeroAgencia!=0) {
			for (String group : groups) {
				for (Rdn rdn : LdapUtils.newLdapName(group).getRdns()) {
					if (rdn.getType().equalsIgnoreCase("CN")) {
						groupNames.add((String) rdn.getValue());
						String role = getRole((String) rdn.getValue());
						if (!role.isEmpty())
							privileges.add(role);
					}
				}
			}
		}

		String DEFAULT_ROLE_PREFIX = Constantes.ROL_PRE;

		return privileges.stream()
				.map(privilege -> org.apache.commons.lang3.StringUtils.appendIfMissing(DEFAULT_ROLE_PREFIX, privilege))
				.map(privilege -> new SimpleGrantedAuthority(privilege)).collect(Collectors.toList());
	}

	private static String getRole(String groupName) {
		String roleName = "";
		String aux = "";
		String subGroup[] = null;

		int index = groupName.lastIndexOf("GSEG-Nexo-Divisas_");
		if (index != -1) {
			aux = groupName.replaceAll("GSEG-Nexo-Divisas_", "");
			subGroup = aux.split("_");
			if (subGroup.length == 1) {
				roleName = subGroup[0];
			} else if (subGroup.length == 2) {
				roleName = subGroup[1];
			} else if (subGroup.length > 2) {
				roleName = subGroup[1] + "_" + subGroup[2];
			}
		}
		return roleName;
	}

	public String getIpOrigen() {
		return ipOrigen;
	}

	public void setIpOrigen(String ipOrigen) {
		this.ipOrigen = ipOrigen;
	}

}