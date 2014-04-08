package reasonablecare.data;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class Specialization
{
  /* these attributes are left public because this is not production code and making
   * accessors and mutators would be annoying and mostly pointless
   */
  public int id;
  public String displayName;
  public long baseCost;
  
  public Specialization(int newID, String newName, long newCost)
  {
    id = newID;
    displayName = newName;
    baseCost = newCost;
  }
  
  /* this is likely mostly useless but I put it in for the hell of it
   */
  public int compareTo(Specialization other)
  {
    return (new Integer(id)).compareTo(new Integer(other.id));
  }
  
  /* returns a list of all specializations in the database
   */
  public static List<Specialization> listSpecializations()
  {
    DBMinder minder = DBMinder.instance();
    ArrayList<Specialization> toReturn = new ArrayList<Specialization>();
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    try
    {
      //retrieve the list of specializations
      ps = conn.prepareStatement("SELECT id, display_name, cost FROM Specialization ORDER BY id ASC;");
      rs = ps.executeQuery();
      while(rs.next())
      {
        toReturn.add(new Specialization(rs.getInt(1), rs.getString(2), rs.getLong(3)));
      }
      rs.close();
      return toReturn;
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return null;
  }
  
  /* returns a list of doctors with the given specialization ID
   */
  public static List<User> doctorsWithSpecialization(int specID)
  {
    DBMinder minder = DBMinder.instance();
    ArrayList<User> toReturn = new ArrayList<User>();
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    try
    {
      //retrieve the list of specializations
      ps = conn.prepareStatement("SELECT id, name FROM Doctor ORDER BY id ASC;");
      rs = ps.executeQuery();
      while(rs.next())
      {
        toReturn.add(new User(rs.getInt(1), rs.getString(2), User.UserType.DOCTOR));
      }
      rs.close();
      return toReturn;
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return null;
  }
}