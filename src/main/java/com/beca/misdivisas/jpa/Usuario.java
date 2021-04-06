package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.beca.misdivisas.util.Util;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * The persistent class for the "USUARIO" database table.
 * 
 */
@Entity
@Table(name="\"USUARIO\"", schema ="\"SEGURIDAD\"")
@NamedQuery(name="Usuario.findAll", query="SELECT u FROM Usuario u")
public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"SEGURIDAD\".\"seq_usuario_idUsuario\"", sequenceName ="\"SEGURIDAD\".\"seq_usuario_idUsuario\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"SEGURIDAD\".\"seq_usuario_idUsuario\"")
	@Column(name="\"id_usuario\"", unique = true, nullable = false, columnDefinition = "serial")

	private Integer idUsuario;

	//@NotNull(message = "es requerida")
	//@NotBlank(message = "es requerida") 
	@Column(name="contrasena")
	private String contrasena;

	@Column(name="contrasena_1")
	private String contrasena1;

	@Column(name="contrasena_2")
	private String contrasena2;

	@Column(name="contrasena_3")
	private String contrasena3;

	@Column(name="contrasena_4")
	private String contrasena4;

	@Column(name="contrasena_5")
	private String contrasena5;

	@NotNull(message = "requerido")
	@NotBlank(message = "requerido")
    @Email(message = "formato inv\u00E1lido")
	@Column(name="email")
	private String email;

	@Column(name="\"fecha_actualizacion_contrasena\"")
	private Timestamp fechaActualizacionContrasena;

	private Boolean habilitado;

	@Column(name="\"id_empresa\"")
	private Integer idEmpresa;
	
	//@NotNull(message = "requerido")
	//@NotBlank(message = "requerido")
	@Column(name="\"nombre_completo\"")
	//@Size(max=255, message = "no debe contener mas de 255 caracteres") 
	private String nombreCompleto;

	@NotNull(message = "requerido")
	@NotBlank(message = "requerido")
	@Column(name="\"nombre_usuario\"")
	//@Size(min=8, max=20, message = Constantes.MENSAJE_VAL_CONTRASENA_1)
	private String nombreUsuario;
	
	private String estado;

	//bi-directional many-to-one association to PerfilUsuario
	@OneToMany(mappedBy="usuario")
	private List<PerfilUsuario> perfilUsuarios;

	//bi-directional many-to-one association to UsuarioRol
	@OneToMany(mappedBy="usuario")
	private List<UsuarioRol> usuarioRols;
	
	@Transient
	private Boolean admin;
	
	@Transient
	private String nuevaContrasena;
	
	@Transient
	private String repitaContrasena;
	
	@Transient
	private String tipoUsuario;
	
	//bi-directional many-to-one association to Empresa
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_empresa\"", insertable = false, updatable = false)
		})
	private Empresa empresa;
	
	public Usuario() {
	}

	public Integer getIdUsuario() {
		return this.idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getContrasena() {
		return this.contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getContrasena1() {
		return this.contrasena1;
	}

	public void setContrasena1(String contrasena1) {
		this.contrasena1 = contrasena1;
	}

	public String getContrasena2() {
		return this.contrasena2;
	}

	public void setContrasena2(String contrasena2) {
		this.contrasena2 = contrasena2;
	}

	public String getContrasena3() {
		return this.contrasena3;
	}

	public void setContrasena3(String contrasena3) {
		this.contrasena3 = contrasena3;
	}

	public String getContrasena4() {
		return this.contrasena4;
	}

	public void setContrasena4(String contrasena4) {
		this.contrasena4 = contrasena4;
	}

	public String getContrasena5() {
		return this.contrasena5;
	}

	public void setContrasena5(String contrasena5) {
		this.contrasena5 = contrasena5;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getFechaActualizacionContrasena() {
		return this.fechaActualizacionContrasena;
	}

	public void setFechaActualizacionContrasena(Timestamp fechaActualizacionContrasena) {
		this.fechaActualizacionContrasena = fechaActualizacionContrasena;
	}

	public Boolean getHabilitado() {
		return this.habilitado;
	}

	public void setHabilitado(Boolean habilitado) {
		this.habilitado = habilitado;
	}

	public Integer getIdEmpresa() {
		return this.idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public String getNombreCompleto() {
		return this.nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getNombreUsuario() {
		return this.nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	
	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public Empresa getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public List<UsuarioRol> getUsuarioRols() {
		return this.usuarioRols;
	}

	public void setUsuarioRols(List<UsuarioRol> usuarioRols) {
		this.usuarioRols = usuarioRols;
		
	}

	/*
	 * public Boolean getAdmin() { this.admin = false; for (Iterator<UsuarioRol>
	 * iterator = usuarioRols.iterator(); iterator.hasNext();) { UsuarioRol
	 * usuarioRol = iterator.next(); if
	 * (usuarioRol.getRol().getRol().equals("ADMIN")) this.admin = true; } return
	 * admin; }
	 */
	
	public Boolean getAdmin() {
		this.admin = false;
		if(perfilUsuarios != null) {
			for (PerfilUsuario perfilUsuario : perfilUsuarios) {
				if (perfilUsuario.getPerfil().getPerfil().equals("ADMIN"))
					this.admin = true;
			}
		}
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public String getNuevaContrasena() {
		return nuevaContrasena;
	}

	public void setNuevaContrasena(String nuevaContrasena) {
		this.nuevaContrasena = nuevaContrasena;
	}

	public String getRepitaContrasena() {
		return repitaContrasena;
	}

	public void setRepitaContrasena(String repitaContrasena) {
		this.repitaContrasena = repitaContrasena;
	}

	public String getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(String tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public List<PerfilUsuario> getPerfilUsuarios() {
		return this.perfilUsuarios;
	}

	public void setPerfilUsuarios(List<PerfilUsuario> perfilUsuarios) {
		this.perfilUsuarios = perfilUsuarios;
	}

	public PerfilUsuario addPerfilUsuario(PerfilUsuario perfilUsuario) {
		getPerfilUsuarios().add(perfilUsuario);
		perfilUsuario.setUsuario(this);

		return perfilUsuario;
	}

	public PerfilUsuario removePerfilUsuario(PerfilUsuario perfilUsuario) {
		getPerfilUsuarios().remove(perfilUsuario);
		perfilUsuario.setUsuario(null);

		return perfilUsuario;
	}


	public UsuarioRol addUsuarioRol(UsuarioRol usuarioRol) {
		getUsuarioRols().add(usuarioRol);
		usuarioRol.setUsuario(this);

		return usuarioRol;
	}

	public UsuarioRol removeUsuarioRol(UsuarioRol usuarioRol) {
		getUsuarioRols().remove(usuarioRol);
		usuarioRol.setUsuario(null);

		return usuarioRol;
	}
	
	/*
	 * public Boolean hasAnyRol(String... nombreRol) { boolean result = false;
	 * 
	 * for (String rol : nombreRol) { for (Iterator<UsuarioRol> iterator =
	 * usuarioRols.iterator(); iterator.hasNext();) { UsuarioRol usuarioRol =
	 * iterator.next(); if (usuarioRol.getRol().getRol().equals(rol)) result = true;
	 * } }
	 * 
	 * return result; }
	 */
	
	public Boolean hasAnyPerfil(String... nombrePerfil) {
		boolean result = false;
		
		for (String perfil : nombrePerfil) {
			for (PerfilUsuario pu : this.perfilUsuarios) {
				if(pu.getPerfil().getPerfil().equalsIgnoreCase(perfil)) {
					result=true;
					break;
				}
			}			
		}
		return result;		
	}
	
	public boolean esClaveVencida(long dias) {
		if(this.fechaActualizacionContrasena != null) {
			long dif = Util.getDateDiff(this.fechaActualizacionContrasena, TimeUnit.DAYS);
			if( dif > dias ){
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(Object obj) {
		return this.idUsuario == ((Usuario) obj).getIdUsuario();
	}

}