package DAO;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ScedulesDao extends Dao {
    public List<SceduleModel> getGroupScedule(String group, int day){
        List<SceduleModel> scedule = new ArrayList<SceduleModel>();
        String querry = "";
        if(day!=0){
         querry ="select * from \"Scedules\" where \"Group\" = '" + group + "' and \"WeekDay\"= " + day +
                   " order by  \"Class\" asc;";}
        else {
             querry = "select * from \"Scedules\" where \"Group\" ='" +group+ "'  order by  \"Class\" asc;";
        }
        try {
            ResultSet resultSet = statement.executeQuery(querry);
            while (resultSet.next()){
              SceduleModel sceduleModel = new SceduleModel();
              sceduleModel.setGroup(resultSet.getString(1));
              sceduleModel.setWeekday(resultSet.getInt(2));
              sceduleModel.setRoom(resultSet.getString(3));
              sceduleModel.setClassN(resultSet.getInt(4));
              sceduleModel.setType(resultSet.getString(5));
              sceduleModel.setSubject(resultSet.getString(6));
              scedule.add(sceduleModel);
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
        return scedule;
    }
    //Перегружаем метод
    public List<SceduleModel> getGroupScedule(String group){
         return getGroupScedule(group,0);
    }

}
