import DAO.*;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class JSONFactory {
    String login;
    ServletContext context;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    StudentDao studentDao;
    ScedulesDao scedulesDao;
    ClassTimeDao classTimeDao;
    GeoRectanglesDao geoRectanglesDao;
    AbcenceDao abcenceDao;
    public JSONFactory(ServletContext context, String login,HttpServletRequest req,HttpServletResponse resp){
        this.context = context;
        this.login = login;
        this.req=req;
        this.resp=resp;
        //Получаем шаблоны из контекста
        studentDao = (StudentDao) context.getAttribute("studentDao");
        scedulesDao = (ScedulesDao) context.getAttribute("scedulesDao");
        classTimeDao = (ClassTimeDao) context.getAttribute("classTimeDao");
        geoRectanglesDao = (GeoRectanglesDao) context.getAttribute("geoRectanglesDao");
        abcenceDao = (AbcenceDao) context.getAttribute("abcenceDao");
    }
    public JSONObject buildJson(JSONObject json){

        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        List<SceduleModel> scedule=null;
        List<Time> times=null;
        try{
            //Получаем расписание группы и время начала пар
         scedule = scedulesDao.getGroupScedule(studentDao.getGroup(login),dayOfWeek);
         times = classTimeDao.getAllTime();
        }
        catch (Exception e){
           json.put("error", "database error");
           e.printStackTrace();
        }
        String querry = json.getString("querry");
        switch (querry){
            case("sced"):
                json = new JSONObject();
                json.put("querry",querry);
                json.put("scedule", scedule);
                break;

            case("classtime"):
                json = new JSONObject();
                json.put("querry",querry);

                json.put("starttime",times);
                break;

            case("logclass"):
                json = new JSONObject();
                json.put("querry",querry);
                Time timecurrent = new Time(System.currentTimeMillis());//Получаем время
                int clas =0;
                for(int i = 0;i<times.size();i++){ // Проверяем номер пары, на которой должен быть студент
                  if(timecurrent.after(times.get(i))){
                      clas = i;
                  }
                }
                int room = 0;
               for(int i=0;i<scedule.size();i++){

                   if(scedule.get(i).getClassN()==clas){//по номеру пары из расписания получем номер корпуса

                       room = Integer.valueOf(scedule.get(i).getRoom().substring(0,1)); //номер корпуса

                   }
                }
               System.out.println("room is " + room);
                double[] rect = geoRectanglesDao.getRectangle(room);//room);// По номеру корпуса получаем гео прямоугольник
                json.put("rect",rect);
                break;
            case("logabs"):

                //json.getString("login");
                Time timecurrent2 = new Time(System.currentTimeMillis());//Получаем время
                int clas2 = 0;
                for(int i = 0;i<times.size();i++){ // Проверяем номер пары, на которой должен быть студент
                    if(timecurrent2.after(times.get(i))){
                        clas2 = i;
                    }
                }
                SceduleModel sm = null;
                for(int i = 0;i<scedule.size();i++){
                    if(scedule.get(i).getClassN()==clas2) sm = scedule.get(i);
                }
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                if(sm!=null) {
                    abcenceDao.logAbcence(json.getString("login"), clas2, sm.getSubject(), timestamp, json.getString("absvar"));
                }
                json = new JSONObject();
                json.put("querry",querry);
                break;
            case("login"):
                json.put("logged on",true);
                break;
            default:
                json.put("querry","error");
                break;
        }
        return json;
    }

}
