package com.technology.os.acceso;

import com.technology.os.domain.User;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.technology.os.authentication.inicializar;
import com.technology.os.authentication.properties.ConexionBD;
import com.technology.os.authentication.properties.GetConexion;
import com.technology.os.authentication.properties.GetProperties;
import com.technology.os.utilerias.Notifications;
import com.technology.os.utilerias.RecoverPassword;

import java.io.IOException;

@Route(value = "login")
public class LogIn extends VerticalLayout {

    //----clases externas-----//
    private GetProperties getProperties = new GetProperties();
    private ConexionBD conexionBD;
    private GetConexion con;

    //---Componentes vaadin----//
    private TextField txtUser = new TextField("Username or Email");
    private PasswordField pwdUser = new PasswordField("Password");
    private H3 hTitle = new H3("Log In");
    private VerticalLayout vltConte = new VerticalLayout();
    private Button btnLogIn = new Button("login");
    private Button btnSignUp = new Button("sign up");
    private String route = UI.getCurrent().getRouter().getUrl(RecoverPassword.class);

    private final Anchor aRecuperar = new Anchor(route, "Has olvidado tu contraseña?");
    private Binder<User> binder = new Binder<>(User.class);

    private final Notifications ntfError = new Notifications(Notification.Position.MIDDLE,
            NotificationVariant.LUMO_ERROR,3000);
    
    public LogIn(){
        setSizeFull();
        setClassName("log-in");
        cargaConexion();
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

    private void inicializar(){
        setAlignItems(FlexComponent.Alignment.CENTER);
        vltConte.setAlignItems(FlexComponent.Alignment.CENTER);
        vltConte.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        txtUser.setWidth("300px");
        pwdUser.setWidth("300px");

        btnLogIn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnLogIn.addClickShortcut(Key.ENTER);
        btnLogIn.setWidth("300px");

        btnSignUp.setWidth("300px");
        btnSignUp.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        //------------------------Validation beginning---------------------------------------------/
        binder.forField(txtUser)
                .asRequired("Obligatory field")
                .bind(User::getvUserID, User::setvUserID);
        binder.forField(pwdUser)
                .asRequired("Obligatory field")
                .bind(User::getvPwd, User::setvPwd);

        //------------------------Validation end ---------------------------------------------/

        //vltConte.getStyle().set("border", "3px solid #6699FF");
        vltConte.add(hTitle,txtUser,pwdUser,aRecuperar,btnLogIn, btnSignUp);
        add(vltConte);

    }
    private void cargaListener(){
        btnLogIn.addClickListener(this::authentication);
        btnSignUp.addClickListener(event->
                getUI().ifPresent(ui -> ui.navigate(SignUp.class))
        );
    }

    private void authentication(ClickEvent<Button> buttonClickEvent) {
        if(binder.validate().isOk()){
            User usuario;
            try {
                usuario = inicializar.getInstance().getRecursosDao().AutentificacionUser(txtUser.getValue().toUpperCase(),pwdUser.getValue());
                if(usuario != null) {
                    if (usuario.getvStatus() == 1) {
                        UI.getCurrent().getSession().setAttribute("USUARIOID", usuario);
                        UI.getCurrent().getSession().setAttribute(User.class, usuario);
                        getUI().ifPresent(ui -> ui.navigate("sistema-de-acceso"));
                    } else {
                        ntfError.setText("Esta cuenta no esta activada porfavor comuniquece con su administrador");
                        ntfError.open();
                    }
                }else{
                    ntfError.setText("Usuario no válido, necesita revisar su información.");
                    ntfError.open();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
