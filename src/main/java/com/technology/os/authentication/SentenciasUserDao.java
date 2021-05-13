package com.technology.os.authentication;

import com.technology.os.domain.User;
import com.technology.os.authentication.properties.GetConexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;




public class SentenciasUserDao extends GetConexion implements SentenciasDao {

    private Connection conexion;

    public SentenciasUserDao() {//ParamConexDB = getConexionDB().clone();
        try {
            conexion = getConexionDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User AutentificacionUser (String pUsuario, String pPassword) throws Exception{

        User usuario = null;
        PreparedStatement stmt = null;
        try{

            String query ="select UserID,RoleID,EmailUser,PwdUser,Status from users where UserID = ? and PwdUser = ? ";

            stmt = conexion.prepareStatement(query);
            stmt.setString(1,pUsuario);
            stmt.setString(2,pPassword);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String vUserID = rs.getString("UserID");
                Integer vRole = rs.getInt("RoleID");
                String vEmail = rs.getString("EmailUser");
                String vPwd = rs.getString("PwdUser");
                Integer vStatus = rs.getInt("Status");

               usuario = new User (vUserID,vRole,vEmail,vPwd,vStatus);
            }

        }
        catch (Exception e){
            System.out.println(e.getMessage());

        }finally {
            conexion.close();
            stmt.close();
        }
        return usuario;
    }

    public int registrarUser(User user) throws SQLException {

        int rows = 0;
        PreparedStatement stmt = null;
        try{
        String query ="INSERT INTO Users(UserID,RoleID,EmailUser,PwdUser,Status,Token)values(?,?,?,?,?,?)";
        stmt = conexion.prepareStatement(query);
        stmt.setString(1,user.getvUserID());
        stmt.setInt(2,user.getvRole());
        stmt.setString(3,user.getvEmail());
        stmt.setString(4,user.getvPwd());
        stmt.setInt(5,user.getvStatus());
        stmt.setString(6,user.getvToken());

        rows = stmt.executeUpdate();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            conexion.close();
            stmt.close();
        }
        return rows;
    }

}
