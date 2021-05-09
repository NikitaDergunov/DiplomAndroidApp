package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class AbcenceDao extends Dao{
    public void logAbcence(String name, int classN, String subject, Timestamp dateandtime, String abcencevar){
        String qurry = "insert into \"AbcenceLog\" (\"Name\", \"Class\", \"Subject\", \"DateAndTime\", \"AbcenceVariable\") VALUES (?,?,?,?,?)";

        String ins = "('"+name+"',"+classN+",'"+subject+"','"+dateandtime+"',"+abcencevar+");";
        Connection con = (Connection) (context.getAttribute("Connection"));

        try{
            PreparedStatement ps = con.prepareStatement(qurry);
            ps.setString(1,name);
            ps.setInt(2,classN);
            ps.setString(3,subject);
            ps.setTimestamp(4,dateandtime);
            ps.setString(5,abcencevar);
            ps.executeUpdate();

        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
