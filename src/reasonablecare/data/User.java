package reasonablecare.data;

import java.sql.*;

public class User
{
  public int id;
  public String name;
  public UserType type;
  
  
  /* an enum works great for this particular purpose
   */
  public enum UserType
  {
    STUDENT("Student"),
    NURSE("Nurse"),
    DOCTOR("Doctor");
    public String dbName;
    UserType(String name)
    {
      this.dbName = name;
    }
  }
  
  public User(int newID, String newName, UserType newType)
  {
    this.id = newID;
    this.name = newName;
    this.type = newType;
  }
  
  
  /* returns the true if a given user of given type and given password match what is in the database
   * this is not very secure and I would never use this in production code but it works
   * for the project's requirements
   */
  public static boolean logInUser(UserType type, int uid, String password)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    int returnedID = -1;
    try
    {
      PreparedStatement ps = conn.prepareStatement("SELECT id FROM "+type.dbName+" WHERE id = ? AND Login_Info = ?");
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
  public static boolean modifyUser(UserType type, int uid, String newName, String newPass)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    int neededModifications = 0, doneModifications = 0;
    try
    {
      if(newName.length() > 0)
      {
        neededModifications++;
        PreparedStatement ps = conn.prepareStatement("UPDATE "+type.dbName+" SET Name = ? WHERE id = ?");
        ps.setString(1, newName);
        ps.setInt(2, uid);
        int numRowsAffected = ps.executeUpdate();
        doneModifications+= numRowsAffected;
      }
      if(newPass.length() > 0)
      {
        PreparedStatement ps = conn.prepareStatement("UPDATE "+type.dbName+" SET Login_Info = ? WHERE id = ?");
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