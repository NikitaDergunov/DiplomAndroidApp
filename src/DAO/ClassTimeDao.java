package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class ClassTimeDao extends Dao {

    public Time getTime(int classN) {
        Time time = null;
        String querry = "select \"StartTime\" from public.\"ClassTime\" where \"Class\" = ";
        try {
            ResultSet resultSet = statement.executeQuery(querry + String.valueOf(classN));
            resultSet.next();
            time = resultSet.getTime(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public List<Time> getAllTime() {
        String querry = "select \"StartTime\" from public.\"ClassTime\";";
        List<Time> time = new ArrayList<Time>();
        try {
            ResultSet resultSet = statement.executeQuery(querry);
            while (resultSet.next()) {
                time.add(resultSet.getTime(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return time;
    }
}