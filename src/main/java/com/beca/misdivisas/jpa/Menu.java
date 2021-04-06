package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the "MENU" database table.
 * 
 */
@Entity
@Table(name="\"MENU\"", schema ="\"SEGURIDAD\"")
@NamedQuery(name="Menu.findAll", query="SELECT m FROM Menu m")
public class Menu implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"SEGURIDAD\".\"seq_menu_idMenu\"", sequenceName ="\"SEGURIDAD\".\"seq_menu_idMenu\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"SEGURIDAD\".\"seq_menu_idMenu\"")
	@Column(name="\"id_menu\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idMenu;
	
	@Column(name="accion")
	private String accion;

	@Column(name="estado")
	private String estado;

	@Column(name="icono")
	private String icono;

	@Column(name="nivel")
	private Integer nivel;

	@Column(name="\"nombre_opcion\"")
	private String nombreOpcion;

	@Column(name="orden")
	private Integer orden;

	@Column(name="\"tipo_menu\"")
	private String tipoMenu;

	@Column(name="\"tipo_vista\"")
	private String tipoVista;

	@Column(name="\"visible_externo\"")
	private Boolean visibleExterno;

	@Column(name="\"visible_interno\"")
	private Boolean visibleInterno;

	@Column(name="\"visible_solo_admin\"")
	private Boolean visibleSoloAdmin;

	@Column(name="\"id_menu_padre\"")
	private Integer idMenuPadre;
	
	//bi-directional many-to-one association to Menu
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_menu_padre\"", insertable = false, updatable = false)
	})
	private Menu menu;

	//bi-directional many-to-one association to Menu
	@OneToMany(mappedBy="menu")
	private List<Menu> menus;

	@Column(name="\"id_rol\"")
	private Integer idRol;
	
	//bi-directional many-to-one association to Rol
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_rol\"", insertable = false, updatable = false)
	})
	private Rol rol;

	//bi-directional many-to-one association to PerfilMenu
	@OneToMany(mappedBy="menu")
	private List<PerfilMenu> perfilMenus;

	public Menu() {
	}

	public Integer getIdMenu() {
		return this.idMenu;
	}

	public void setIdMenu(Integer idMenu) {
		this.idMenu = idMenu;
	}

	public String getAccion() {
		return this.accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getIcono() {
		return this.icono;
	}

	public void setIcono(String icono) {
		this.icono = icono;
	}

	public Integer getNivel() {
		return this.nivel;
	}

	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}

	public String getNombreOpcion() {
		return this.nombreOpcion;
	}

	public void setNombreOpcion(String nombreOpcion) {
		this.nombreOpcion = nombreOpcion;
	}

	public Integer getOrden() {
		return this.orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public String getTipoMenu() {
		return this.tipoMenu;
	}

	public void setTipoMenu(String tipoMenu) {
		this.tipoMenu = tipoMenu;
	}

	public String getTipoVista() {
		return this.tipoVista;
	}

	public void setTipoVista(String tipoVista) {
		this.tipoVista = tipoVista;
	}

	public Boolean getVisibleExterno() {
		return this.visibleExterno;
	}

	public void setVisibleExterno(Boolean visibleExterno) {
		this.visibleExterno = visibleExterno;
	}

	public Boolean getVisibleInterno() {
		return this.visibleInterno;
	}

	public void setVisibleInterno(Boolean visibleInterno) {
		this.visibleInterno = visibleInterno;
	}

	public Boolean getVisibleSoloAdmin() {
		return this.visibleSoloAdmin;
	}

	public void setVisibleSoloAdmin(Boolean visibleSoloAdmin) {
		this.visibleSoloAdmin = visibleSoloAdmin;
	}

	public Integer getIdMenuPadre() {
		return idMenuPadre;
	}

	public void setIdMenuPadre(Integer idMenuPadre) {
		this.idMenuPadre = idMenuPadre;
	}

	public Integer getIdRol() {
		return idRol;
	}

	public void setIdRol(Integer idRol) {
		this.idRol = idRol;
	}

	public Menu getMenu() {
		return this.menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public List<Menu> getMenus() {
		return this.menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public Menu addMenus(Menu menus) {
		getMenus().add(menus);
		menus.setMenu(this);

		return menus;
	}

	public Menu removeMenus(Menu menus) {
		getMenus().remove(menus);
		menus.setMenu(null);

		return menus;
	}

	public Rol getRol() {
		return this.rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public List<PerfilMenu> getPerfilMenus() {
		return this.perfilMenus;
	}

	public void setPerfilMenus(List<PerfilMenu> perfilMenus) {
		this.perfilMenus = perfilMenus;
	}

	public PerfilMenu addPerfilMenus(PerfilMenu perfilMenus) {
		getPerfilMenus().add(perfilMenus);
		perfilMenus.setMenu(this);

		return perfilMenus;
	}

	public PerfilMenu removePerfilMenus(PerfilMenu perfilMenus) {
		getPerfilMenus().remove(perfilMenus);
		perfilMenus.setMenu(null);

		return perfilMenus;
	}

	@Override
	public boolean equals(Object obj) {
		return this.idMenu == ((Menu)obj).getIdMenu();
		
	}

}