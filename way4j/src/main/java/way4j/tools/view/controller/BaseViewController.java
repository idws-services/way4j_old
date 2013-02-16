package way4j.tools.view.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.component.dialog.Dialog;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.component.tabview.TabView;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;
import org.springframework.stereotype.Component;

@ManagedBean(name="baseView")
@Component(value="baseView")
@SessionScoped
public class BaseViewController {
	
	private Dialog actionDialog;
	private TabView tabView;
	private MenuModel navMenuModel;
	
	public BaseViewController(){
		createNavMenu();
	}
	  
	public Dialog getActionDialog() {
		return actionDialog;
	}

	private void createNavMenu(){
		navMenuModel = new DefaultMenuModel();  
        
        Submenu submenu = new Submenu();  
        submenu.setLabel("Dynamic Submenu 1");  
          
        MenuItem item = new MenuItem();  
        item.setValue("Dynamic Menuitem 1.1");  
        item.setUrl("#");  
        submenu.getChildren().add(item);  
          
        navMenuModel.addSubmenu(submenu);  
          
        //Second submenu  
        submenu = new Submenu();  
        submenu.setLabel("Dynamic Submenu 2");  
          
        item = new MenuItem();  
        item.setValue("Dynamic Menuitem 2.1");  
        item.setUrl("#");  
        submenu.getChildren().add(item);  
          
        item = new MenuItem();  
        item.setValue("Dynamic Menuitem 2.2");  
        item.setUrl("#");  
        submenu.getChildren().add(item);  
          
        navMenuModel.addSubmenu(submenu);		
	}
	
	public void setActionDialog(Dialog actionDialog) {
		this.actionDialog= actionDialog;
	}

	public TabView getTabView() {
		return tabView;
	}

	public void setTabView(TabView tabView) {
		this.tabView = tabView;
	}

	public MenuModel getNavMenuModel() {
		return navMenuModel;
	}

	public void setNavMenuModel(MenuModel navMenuModel) {
		this.navMenuModel = navMenuModel;
	}
	
}
