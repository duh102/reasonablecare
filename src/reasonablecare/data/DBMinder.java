package reasonablecare.data;

import java.sql.*;

public class DBMinder
{
  private static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";
  private static final String user = "wemorrow";
  private static final String password = "trees";
  
  private static DBMinder minderSingleton = null;
  
  private Connection conn = null;
  
  private DBMinder()
  {
    getConnection();
  }
  
  /* returns the single instance of DBMinder
   * if it does not exist, it's created before returning
   */
  public static DBMinder instance()
  {
    if(minderSingleton == null)
    {
      minderSingleton = new DBMinder();
    }
    return minderSingleton;
  }
  
  
  /* getConnection() returns either a Connection object or null
   * Connection if the connection was successful
   * null if the connection was not successful
   */
  public Connection getConnection()
  {
    if(conn == null)
    {
      try
      {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        conn = DriverManager.getConnection(jdbcURL, user, password);
      }
      catch (ClassNotFoundException e)
      {
        e.printStackTrace();
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
      finally
      {
        return conn;
      }
    }
    else
    {
      return conn;
    }
  }
  
  /* close() closes the connection if it's still connected
   * not a whole lot to this guy
   */
  public void close()
  {
    if(conn != null)
    {
      try
      {
        conn.close();
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
  }
}