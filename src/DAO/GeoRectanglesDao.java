package DAO;

import java.sql.ResultSet;

public class GeoRectanglesDao extends Dao{
    public double[] getRectangle(int building){
        double[] rect = {0,0,0,0};
        String nw, ne;
         String querry = "select \"nw\" , \"se\" from public.\"GeoRectangles\" where \"Building\" =";
        try{
            ResultSet resultSet = statement.executeQuery(querry + building);
            resultSet.next();
            nw = resultSet.getString("nw");
            ne = resultSet.getString("se");
            rect[0] = Double.parseDouble(nw.substring(0,nw.lastIndexOf(",")));
            rect[1] = Double.parseDouble(nw.substring(nw.lastIndexOf(",")+1));
            rect[2]= Double.parseDouble(ne.substring(0,ne.lastIndexOf(",")));
            rect[3]= Double.parseDouble(ne.substring(ne.lastIndexOf(",")+1));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return rect;
    }

}
