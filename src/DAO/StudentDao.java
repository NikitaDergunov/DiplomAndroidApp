package DAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDao extends Dao{

    //проверяем существует ли такой студент в группе
    public boolean checkAuth(String name, byte[] pass){
        boolean response = false;

        String querry = "SELECT exists(select 1 from \"Students\" where \"Name\" = '"+name+"' AND md5 = ?);";
        try{
            Connection con = (Connection) (context.getAttribute("Connection"));
            PreparedStatement ps = con.prepareStatement(querry);
            ps.setBytes(1,pass);
            ResultSet resultSet = ps.executeQuery();;
            resultSet.next();
            response = resultSet.getBoolean(1);
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(response);
        return response;
    }

    public String getGroup(String name){
        String querry = "SELECT \"Group\" From \"Students\" Where \"Name\" = '" + name + "';";
        //System.out.println(querry);
        String group = null;
        try{
            ResultSet resultSet = statement.executeQuery(querry);
            resultSet.next();
            group=resultSet.getString(1);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return group;
    }
}
