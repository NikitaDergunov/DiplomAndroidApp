import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.AbcenceDao;
import DAO.StudentDao;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.*;
@WebServlet("/DoubleMeServlet")
public class DoubleMeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public DoubleMeServlet() {
        super();
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getOutputStream().println("Hurray !! This Servlet Works");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context=getServletContext();

        response.setContentType("application/json");
        StudentDao studentDao = (StudentDao) context.getAttribute("studentDao");
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append(System.lineSeparator());
        }
        String data = buffer.toString();
        data = URLDecoder.decode(data,"utf-8");
        JSONObject json = new JSONObject(data); //получили запрс
        System.out.println("request: "+ json.toString());
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        if(studentDao.checkAuth(json.getString("login"), Base64.getDecoder().decode(json.getString("password").substring(0,json.getString("password").lastIndexOf("\n"))))){ //Проверяем логин и пароль

            JSONFactory jsonFactory = new JSONFactory(context,json.getString("login"),request,response);//Создаем класс - создатель ответа

            String querry = json.getString("querry");

            json = jsonFactory.buildJson(json);//строим ответ

        }
        else {// если не правильный логин и пароль
            json = new JSONObject();
            json.put("querry","login");
            json.put("logged on",false);
        }

        System.out.println("response: "+ json.toString());
        out.print(json);
        out.flush();
    }
}