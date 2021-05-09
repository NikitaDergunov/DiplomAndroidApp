package DAO;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Dao {

    Statement statement;
    ServletContext context;
    //инициализация
    public void init( ServletContext context) throws SQLException {
       this.context=context;
        statement = ((Connection) (context.getAttribute("Connection"))).createStatement();
    }
}
