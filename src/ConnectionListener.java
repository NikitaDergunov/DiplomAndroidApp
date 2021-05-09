import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ConnectionListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {

            ServletContext context=servletContextEvent.getServletContext();
            String url=context.getInitParameter("url");;
            String user=context.getInitParameter("user");;
            String pass=context.getInitParameter("password");;
            String driver=context.getInitParameter("driver");
            DbConnector connect = new DbConnector(url,driver,user,pass,context); //оздаем соединение
            context.setAttribute("Connection",connect.conn);
           //Создаем экземпляры ДАО шаблонов
            DAO.StudentDao studentDao = new DAO.StudentDao();
            studentDao.init(context);
            context.setAttribute("studentDao",studentDao);
            DAO.ClassTimeDao classTimeDao = new DAO.ClassTimeDao();
            classTimeDao.init(context);
            context.setAttribute("classTimeDao", classTimeDao);
            DAO.GeoRectanglesDao geoRectanglesDao = new DAO.GeoRectanglesDao();
            geoRectanglesDao.init(context);
            context.setAttribute("geoRectanglesDao", geoRectanglesDao);
            DAO.GroupsDao groupsDao = new DAO.GroupsDao();
            groupsDao.init(context);
            context.setAttribute("groupsDao", groupsDao);
            DAO.ScedulesDao scedulesDao = new DAO.ScedulesDao();
            scedulesDao.init(context);
            context.setAttribute("scedulesDao",scedulesDao);
            DAO.AbcenceDao abcenceDao = new DAO.AbcenceDao();
            abcenceDao.init(context);
            context.setAttribute("abcenceDao",abcenceDao);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Shutting down!");
    }
}