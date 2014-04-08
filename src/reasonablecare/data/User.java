package reasonablecare.data;

import java.sql.*;

public class User
{
  /* returns the true if a given user of given type and given password match what is in the database
   * this is not very secure and I would never use this in production code but it works
   * for the project's requirements
   */
  public static boolean logInUser(int type, int uid, String password)
  {
    DBMinder minder = DBMinder.instance();
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
      sqle.printStackTrace();
    }
    return returnedID == uid;
  }
  
  /* returns true if an update on a user processed successfully
   * newName and newPass can be empty, in which case nothing happens
   * if either or both contain new values, the user is updated with those new values
   */
  public static boolean modifyUser(int type, int uid, String newName, String newPass)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    String personType = "";
    int neededModifications = 0, doneModifications = 0;;
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
      if(newName.length() > 0)
      {
        neededModifications++;
        PreparedStatement ps = conn.prepareStatement("UPDATE "+personType+" SET Name = ? WHERE id = ?");
        ps.setString(1, newName);
        ps.setInt(2, uid);
        int numRowsAffected = ps.executeUpdate();
        doneModifications+= numRowsAffected;
      }
      if(newPass.length() > 0)
      {
        PreparedStatement ps = conn.prepareStatement("UPDATE "+personType+" SET Login_Info = ? WHERE id = ?");
        ps.setString(1, newPass);
        ps.setInt(2, uid);
        int numRowsAffected = ps.executeUpdate();
        doneModifications+= numRowsAffected;
      }
      //each update really shouldn't affect more than one row, that'd be really weird
      if(doneModifications >= neededModifications)
      {
        return true;
      }
      else
      {
        return false;
      }
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return false;
  }
}