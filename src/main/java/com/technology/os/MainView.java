package com.technology.os;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.technology.os.acceso.LogIn;
import com.technology.os.authentication.properties.ConexionBD;
import com.technology.os.authentication.properties.GetConexion;
import com.technology.os.authentication.properties.GetProperties;
import com.technology.os.views.Home;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The main view contains a button and a click listener.
 */
@PWA(name = "Project Base for Vaadin", shortName = "Project Base", enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
public class MainView extends AppLayout implements BeforeEnterObserver {

    private static final Logger log = LoggerFactory.getLogger(MainView.class);

    private GetProperties getProperties = new GetProperties();
    private ConexionBD conexionBD;
    private GetConexion con;

    //Componentes
    private Tabs tabs = new Tabs();
    private Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();
    private DrawerToggle dtgMenu = new DrawerToggle();
    public MainView() {
        cargaConexion();
        VaadinSession.getCurrent()
                .setErrorHandler((ErrorHandler) errorEvent -> {
                    log.error("Uncaught UI exception",
                            errorEvent.getThrowable());
                    Notification.show(
                            "Lo sentimos, Pero un error interno ha ocurrido.");
                });
        //addClassName("centered-content");
        inicializar();
        cargaListener();

    }



    private void cargaConexion(){
        try {
            conexionBD = new ConexionBD();
            getProperties.getConexionProperties("conexion", conexionBD,"mysql");
            con.setConexionDB(conexionBD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  void signOut() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().getSession().close();
        UI.getCurrent().navigate("Login");
    }

    private void inicializar(){
        //menu
        createMenuLink(Home.class, Home.HOME_VIEW,
                VaadinIcon.HOME.create());
        tabs.setOrientation(Tabs.Orientation.VERTICAL);

        setPrimarySection(AppLayout.Section.DRAWER);
        addToDrawer(tabs);
        addToNavbar(dtgMenu);
    }
    private void cargaListener(){

    }

    private void  createMenuLink(Class<? extends Component> target,
                                      String caption, Icon icon) {
        final Tab tab = new Tab();
        final RouterLink routerLink = new RouterLink(null, target);
        routerLink.setClassName("menu-link");
        navigationTargetToTab.put(target, tab);
        routerLink.add(icon);
        routerLink.add(new Span(caption));
        icon.setSize("24px");
        tab.add(routerLink);
        tabs.add(tab);
        //tab.add(routerLink);
        //return routerLink;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        tabs.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));
        if (VaadinSession.getCurrent().getAttribute("USUARIOID") == null) {
            VaadinSession.getCurrent().setAttribute("intededPath", event.getLocation().getPath());
            event.forwardTo(LogIn.class);
        }
    }
}
