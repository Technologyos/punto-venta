package com.technology.os.authentication;

import com.technology.os.domain.User;

public interface SentenciasDao {

    public User AutentificacionUser(String pUsuario, String pPassword) throws Exception;

    public int registrarUser(User user) throws Exception;

}
