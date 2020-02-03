package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;


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

	@NotNull(message = "requerida")
	@NotBlank(message = "requerida")
	//@Size(min=8, max=20, message = "debe contener entre 8 y 20 caracteres") 
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
    @Email(message = "formato inválido")
	@Column(name="email")
	private String email;

	@Column(name="\"fecha_actualizacion_contrasena\"")
	private Timestamp fechaActualizacionContrasena;

	//@NotNull(message = "Es requerido")
	private Boolean habilitado;

	@Column(name="\"id_empresa\"")
	private Integer idEmpresa;
	
	@NotNull(message = "requerido")
	@NotBlank(message = "requerido")
	@Column(name="\"nombre_completo\"")
	@Size(max=255, message = "no debe contener mas de 255 caracteres") 
	private String nombreCompleto;

	@NotNull(message = "requerido")
	@NotBlank(message = "requerido")
	@Column(name="\"nombre_usuario\"")
	@Size(min=8, max=20, message = "debe contener entre 8 y 20 caracteres")
	private String nombreUsuario;
	
	private String estado;

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

	public Boolean getAdmin() {
		this.admin = false;
		for (Iterator<UsuarioRol> iterator = usuarioRols.iterator(); iterator.hasNext();) {
			UsuarioRol usuarioRol = (UsuarioRol) iterator.next();
			if (usuarioRol.getRol().getRol().equals("ADMIN"))
				this.admin = true;
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

}