package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the "PERFIL_MENU" database table.
 * 
 */
@Entity
@Table(name="\"PERFIL_MENU\"", schema ="\"SEGURIDAD\"")
@NamedQuery(name="PerfilMenu.findAll", query="SELECT p FROM PerfilMenu p")
public class PerfilMenu implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"SEGURIDAD\".\"seq_perfil_menu_idPerfilMenu\"", sequenceName ="\"SEGURIDAD\".\"seq_perfil_menu_idPerfilMenu\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"SEGURIDAD\".\"seq_perfil_menu_idPerfilMenu\"")
	@Column(name="\"id_perfil_menu\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idPerfilMenu;

	@Column(name="\"id_menu\"")
	private Integer idMenu;

	@Column(name="\"id_perfil\"")
	private Integer idPerfil;
	
	//bi-directional many-to-one association to Menu
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_menu\"", insertable = false, updatable = false)
	})
	private Menu menu;

	//bi-directional many-to-one association to Perfil
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_perfil\"", insertable = false, updatable = false)
	})
	private Perfil perfil;

	public PerfilMenu() {
		this.perfil = new Perfil();
		this.menu = new Menu();
	}

	public Integer getIdPerfilMenu() {
		return this.idPerfilMenu;
	}

	public void setIdPerfilMenu(Integer idPerfilMenu) {
		this.idPerfilMenu = idPerfilMenu;
	}

	public Integer getIdMenu() {
		return idMenu;
	}

	public void setIdMenu(Integer idMenu) {
		this.idMenu = idMenu;
	}

	public Integer getIdPerfil() {
		return idPerfil;
	}

	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}

	public Menu getMenu() {
		return this.menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public Perfil getPerfil() {
		return this.perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

}