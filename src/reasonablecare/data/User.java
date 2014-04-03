package reasonablecare.data;

import java.sql.*;

public class User
{
  public static boolean logInUser(int type, DBMinder minder, int uid, String password)
  {
    Connection conn = minder.getConnection();
    String personType = "";
    int returnedID = -1;
    switch(type)
    {
      case 0:
        personType = "Student";
        break;
      case 1:
        personType = "Nurse";
        break;
      case 2:
        personType = "Doctor";
        break;
    }
    try
    {
      PreparedStatement ps = conn.prepareStatement("SELECT id FROM "+personType+" WHERE id = ? AND Login_Info = ?");
      ps.setInt(1, uid);
      ps.setString(2, password);
      ResultSet rs = ps.executeQuery();
      if(rs.next())
      {
        returnedID = rs.getInt(1);
      }
      rs.close();
    }
    catch(SQLException sqle)
    {
    }
    return returnedID == uid;
  }
}