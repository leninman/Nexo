package com.beca.misdivisas.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beca.misdivisas.interfaces.IMenuRepo;
import com.beca.misdivisas.jpa.PerfilUsuario;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.Menu;
import com.beca.misdivisas.util.Constantes;

@Service
public class MenuService {

	@Autowired
	private IMenuRepo menuRepo;

	public List<Menu> loadMenuByUserId(int idUsuario) {
		List<com.beca.misdivisas.jpa.Menu> menuItem = menuRepo.findByIdUsuario(idUsuario, 1);
		List<com.beca.misdivisas.jpa.Menu> subMenuItem = menuRepo.findByIdUsuario(idUsuario, 2);
		List<Menu> menuList = new ArrayList<Menu>();
		List<Menu> subMenuList = new ArrayList<Menu>();

		Menu m = null;
		Menu sm = null;

		for (com.beca.misdivisas.jpa.Menu menu : menuItem) {
			m = getMenu(menu);
			subMenuList = new ArrayList<Menu>();

			for (com.beca.misdivisas.jpa.Menu subMenu : subMenuItem) {
				if (menu.getIdMenu() == subMenu.getIdMenuPadre()) {
					sm = getMenu(subMenu);
					subMenuList.add(sm);
				}
			}

			m.setSubMenu(subMenuList);
			menuList.add(m);

		}

		return menuList;

	}	
	
	public List<Menu> loadMenuByTipoExterno(String tipoMenu, String tipoVista, boolean visibleSoloAdmin) {
		List<com.beca.misdivisas.jpa.Menu> menuItem = null;
		Optional<com.beca.misdivisas.jpa.Menu> aux = null;
		
		menuItem = menuRepo.findByTipoMenuExterno(tipoMenu, tipoVista, visibleSoloAdmin);
		
		List<Menu> menuList = new ArrayList<Menu>();
		Menu m = null;
		String nombre;
		for (com.beca.misdivisas.jpa.Menu menu : menuItem) {
			nombre = null;
			if(menu.getIdMenuPadre() != null) {
				m = getMenu(menu);
				if(menu.getNivel() == 2) {
					aux = menuRepo.findById(menu.getIdMenuPadre());
					nombre = aux.get().getNombreOpcion();
				}else if(menu.getNivel() == 3) {
					aux = menuRepo.findById(menu.getIdMenuPadre());
					nombre = aux.get().getNombreOpcion();
					aux = menuRepo.findById(aux.get().getIdMenuPadre());
					nombre = aux.get().getNombreOpcion()+" - " + nombre;
				}
					
				m.setNombreOpcion(nombre+" - "+m.getNombreOpcion());	
				menuList.add(m);
			}
		}
		return menuList;

	}

	public List<Menu> loadMenuByTipoInterno(String tipoMenu, String tipoVista, boolean visibleSoloAdmin) {
		List<com.beca.misdivisas.jpa.Menu> menuItem = null;
		Optional<com.beca.misdivisas.jpa.Menu> aux = null;
		
		menuItem = menuRepo.findByTipoMenuInterno(tipoMenu, tipoVista, visibleSoloAdmin);
		
		List<Menu> menuList = new ArrayList<Menu>();
		Menu m = null;
		String nombre;
		for (com.beca.misdivisas.jpa.Menu menu : menuItem) {
			nombre = null;
			if(menu.getIdMenuPadre() != null) {
				m = getMenu(menu);
				if(menu.getNivel() == 2) {
					aux = menuRepo.findById(menu.getIdMenuPadre());
					nombre = aux.get().getNombreOpcion();
				}else if(menu.getNivel() == 3) {
					aux = menuRepo.findById(menu.getIdMenuPadre());
					nombre = aux.get().getNombreOpcion();
					aux = menuRepo.findById(aux.get().getIdMenuPadre());
					nombre = aux.get().getNombreOpcion()+" - " + nombre;
				}
					
				m.setNombreOpcion(nombre+" - "+m.getNombreOpcion());	
				menuList.add(m);
			}
		}
		return menuList;

	}
	
	public List<Menu> loadMenuByNombrePerfilAndTipo(String nombrePerfil, String tipoMenu) {
		List<Menu> menuList = null;
		try {
			List<com.beca.misdivisas.jpa.Menu> menuItems = menuRepo.findByNombrePerfilAndEstado(nombrePerfil, Constantes.ACTIVO, tipoMenu);
			menuList = getMenuListFromJPA1(menuItems);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return menuList;

	}
	
	
	public List<Menu> loadMenuByUserIdAndIdEmpresa(int userId, int idEmpresa, String tipoMenu) {
		List<Menu> menuList = null;
		List<com.beca.misdivisas.jpa.Menu> menuItems = menuRepo.findByIdUsuarioAndIdEmpresaAndTipo(userId, idEmpresa,tipoMenu);
		
		try {
			menuList = getMenuListFromJPA1(menuItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return menuList;
	  }
	 
	public  List<Menu> getMenuListFromJPA(List<com.beca.misdivisas.jpa.Menu> menuItems) throws Exception {
		List<Menu> menuList = new ArrayList<Menu>();
		try {
			Menu m,mp = null;
		
			Menu menuPadre = null;
			com.beca.misdivisas.jpa.Menu menuPadreJPA = null;

			for (com.beca.misdivisas.jpa.Menu menu : menuItems) {
				
				if(menu.getIdMenuPadre() == null) {
					m = getMenu(menu);
					menuList.add(m);
				}else {
					m = getMenu(menu);
					menuPadre = new Menu();
					menuPadre.setIdMenu(menu.getIdMenuPadre());
					
					int pos = menuList.indexOf(menuPadre);
					
					if(pos>=0) {
						List<Menu> lista = menuList.get(pos).getSubMenu();
						
						if(lista == null) {
							lista = new ArrayList<Menu>();
							lista.add(m);
							menuList.get(pos).setSubMenu(lista);;
						}else {							
							lista.add(m);
							menuList.get(pos).setSubMenu(lista);
						}
					}else {						
						menuPadreJPA = menuRepo.findById(menuPadre.getIdMenu());
						if(menuPadreJPA != null) {
							mp = getMenu(menuPadreJPA);
							mp.getSubMenu().add(m);
							menuList.add(mp);
							
						}

					}
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return menuList;
	}
	  
	public Menu getMenu(com.beca.misdivisas.jpa.Menu menu) {
		Menu m = new Menu();
		m.setIdMenu(menu.getIdMenu());

		if (menu.getIdMenuPadre() != null)
			m.setIdMenuPadre(menu.getIdMenuPadre());

		m.setNivel(menu.getNivel());
		m.setNombreOpcion(menu.getNombreOpcion());
		if (menu.getAccion() != null)
			m.setAccion(menu.getAccion());

		m.setIcono(menu.getIcono());
		m.setTipoMenu(menu.getTipoMenu());
		m.setTipoVista(menu.getTipoVista());
		m.setOrden(menu.getOrden());

		return m;
	}
	
	public List<Menu> getMenu(Usuario us, String tipo) {
		List<com.beca.misdivisas.jpa.Menu> menuJpa=null;
		List<Menu> menuModel=null;
		if(us != null)
				menuJpa = menuRepo.findByIdUsuarioAndIdEmpresaAndTipo((int)us.getIdUsuario(), (int)us.getIdEmpresa(), tipo);
		else
				menuJpa = menuRepo.findByNombrePerfilAndIdEmpresaNullAndEstadoAndTipo("Consultor", "A", tipo);
		try {
			menuModel= getMenuListFromJPA1(menuJpa);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return menuModel;
	}
	
	public List<Menu> getMenuBy(Usuario us, String tipoMenu, String tipoVista, boolean visibleSoloAdmin) {
		List<com.beca.misdivisas.jpa.Menu> menuJpa=null;
		List<Menu> menuModel=null;
			menuJpa = menuRepo.findByIdEmpresaNullAndEstadoAndTipoVista(Constantes.ACTIVO, tipoMenu, tipoVista, visibleSoloAdmin);
		try {
			menuModel= getMenuListFromJPA1(menuJpa);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return menuModel;
	}

	public List<Menu> getMenuBy(Usuario us, String tipoMenu, String tipoVista)throws Exception {
		List<com.beca.misdivisas.jpa.Menu> menuJpa= new ArrayList<com.beca.misdivisas.jpa.Menu>();
		List<Menu> menuModel=null;
		if(us != null ) {
			if(us.getPerfilUsuarios()!=null) {
				for (PerfilUsuario perfilUs : us.getPerfilUsuarios()) {
					if(perfilUs != null && perfilUs.getPerfil()!=null) {
						List<com.beca.misdivisas.jpa.Menu> m = menuRepo.findByIdEmpresaNullAndEstadoAndTipoVista(perfilUs.getPerfil().getPerfil(), Constantes.ACTIVO, tipoMenu, tipoVista);
						for (com.beca.misdivisas.jpa.Menu menu : m) {
							if(!menuJpa.contains(menu))
								menuJpa.add(menu);
						}
						//menuJpa.addAll(menuRepo.findByIdEmpresaNullAndEstadoAndTipoVista(perfilUs.getPerfil().getPerfil(), Constantes.ACTIVO, tipoMenu, tipoVista));
					}
				}
			}
		} 
		else {
			menuJpa = menuRepo.findByIdEmpresaNullAndEstadoAndTipoVista(Constantes.ACTIVO, tipoMenu, tipoVista);
		}
		try {
			menuModel= getMenuListFromJPA1(menuJpa);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return menuModel;
	}
	
	public List<Menu> getMenuList(String[] mr){
		List<Menu> menuList = new ArrayList<Menu>();
		
		for (int i = 0; i < mr.length; i++) {
			if(!mr[i].equalsIgnoreCase("1")) {
				com.beca.misdivisas.jpa.Menu menu = menuRepo.findById(Integer.parseInt(mr[i]));
				menuList.add(getMenu(menu));
			}
			
		}
		return menuList;
	}
	
	public List<Menu> loadMenuByIdPerfil(int idPerfil) {
		List<com.beca.misdivisas.jpa.Menu> menuItem = null;
		Optional<com.beca.misdivisas.jpa.Menu> aux = null;

			menuItem = menuRepo.findByIdPerfil(idPerfil);
		
		List<Menu> menuList = new ArrayList<Menu>();
		Menu m = null;
		String nombre;
		for (com.beca.misdivisas.jpa.Menu menu : menuItem) {
			nombre = null;
			if(menu.getIdMenuPadre() != null) {
				m = getMenu(menu);
				if(menu.getNivel() == 2) {
					aux = menuRepo.findById(menu.getIdMenuPadre());
					nombre = aux.get().getNombreOpcion();
				}else if(menu.getNivel() == 3) {
					aux = menuRepo.findById(menu.getIdMenuPadre());
					nombre = aux.get().getNombreOpcion();
					aux = menuRepo.findById(aux.get().getIdMenuPadre());
					nombre = aux.get().getNombreOpcion()+" - " + nombre;
				}
					
				m.setNombreOpcion(nombre+" - "+m.getNombreOpcion());	
				menuList.add(m);
			}
		}
		return menuList;

	}
	

	public  List<Menu> getMenuListFromJPA1(List<com.beca.misdivisas.jpa.Menu> menuItems) throws Exception {
		List<Menu> menuList = new ArrayList<Menu>();
		try {
			Menu m,mp,ma = null;
			com.beca.misdivisas.jpa.Menu menuPadreJPA,menuAbueloJPA = null;

			for (com.beca.misdivisas.jpa.Menu menu : menuItems) {
				m = getMenu(menu);				
				switch(menu.getNivel()) {
					case 1:
						menuList.add(m);
						break;
					case 2:
						menuPadreJPA = menuRepo.findById(m.getIdMenuPadre());
						mp= getMenu(menuPadreJPA);
						int posMP = menuList.indexOf(mp);
						if(posMP>=0) {
							menuList.get(posMP).getSubMenu().add(m);
						}else {
							mp.getSubMenu().add(m);
							menuList.add(mp);
						}
						break;
					case 3:
						menuPadreJPA = menuRepo.findById(m.getIdMenuPadre());
						mp= getMenu(menuPadreJPA);
						menuAbueloJPA = menuRepo.findById(mp.getIdMenuPadre());
						ma = getMenu(menuAbueloJPA);
						int posMA = menuList.indexOf(ma);
						if(posMA>=0) {
							List<Menu> listaP = menuList.get(posMA).getSubMenu();
							int posLP = listaP.indexOf(mp);
							
							if(posLP>=0) {
								listaP.get(posLP).getSubMenu().add(m);
							}else {
								mp.getSubMenu().add(m);
								listaP.add(mp);
							}
							menuList.get(posMA).setSubMenu(listaP);
						
						}else {
							mp.getSubMenu().add(m);
							ma.getSubMenu().add(mp);
							menuList.add(ma);
						}
						break;
						default:
							break;
						}
				}				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.sort(menuList);
		return menuList;
	}
}
