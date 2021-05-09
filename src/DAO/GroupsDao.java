package DAO;

import java.sql.ResultSet;

public class GroupsDao extends Dao{
    public boolean checkGroup(String group){
        boolean answ = false;
        String querry ="SELECT COUNT(1) FROM \"Groups\" WHERE \"Group\" = ";
        try{
            ResultSet resultSet = statement.executeQuery(querry + "'" + group + "'");
            resultSet.next();
            answ =  (resultSet.getInt(1) == 1);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return answ;
    }

}
