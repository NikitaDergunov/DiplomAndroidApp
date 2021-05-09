import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnector {
    Connection conn = null;
    DbConnector(String url, String driver, String user, String password, ServletContext context){
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url,user,password);

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}