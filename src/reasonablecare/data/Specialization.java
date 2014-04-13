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
  
  public static boolean updateSpecialization(int id, String newName, long newCost)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    int neededModifications = 0, doneModifications = 0;
    try
    {
      if(newName.length() > 0)
      {
        neededModifications++;
        PreparedStatement ps = conn.prepareStatement("UPDATE Specialization SET display_name = ? WHERE id = ?");
        ps.setString(1, newName);
        ps.setInt(2, id);
        int numRowsAffected = ps.executeUpdate();
        doneModifications+= numRowsAffected;
      }
      if(newCost != -1)
      {
        neededModifications++;
        PreparedStatement ps = conn.prepareStatement("UPDATE Specialization SET base_cost = ? WHERE id = ?");
        ps.setLong(1, newCost);
        ps.setInt(2, id);
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
  
  public static boolean setDoctorHasSpecialization(int doctorID, int specializationID, boolean doesHave)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    try
    {
      if(doesHave)
      {
        ps = conn.prepareStatement("INSERT INTO HasSpecialization (doctor_id, specialization_id) VALUES (?, ?)");
      }
      else
      {
        ps = conn.prepareStatement("DELETE FROM HasSpecialization WHERE doctor_id = ? AND specialization_id = ?");
      }
      ps.setInt(1, doctorID);
      ps.setInt(2, specializationID);
      int numRowsAffected = ps.executeUpdate();
      //each update really shouldn't affect more than one row, that'd be really weird
      if(numRowsAffected >= 1)
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
      ps = conn.prepareStatement("SELECT id, display_name, cost FROM Specialization ORDER BY id ASC");
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
  
  /* returns a list of all specializations for a doctor
   */
  public static List<Specialization> listSpecializationsForDoctor(int doctorID)
  {
    DBMinder minder = DBMinder.instance();
    ArrayList<Specialization> toReturn = new ArrayList<Specialization>();
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    try
    {
      //retrieve the list of specializations
      ps = conn.prepareStatement("SELECT id, display_name, cost FROM HasSpecialization, Specialization WHERE doctor_id = ? "
                                +"AND HasSpecialization.specialization_id = Specialization.id ORDER BY id ASC");
      ps.setInt(1, doctorID);
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
  
  /* returns a list of all specializations a doctor does not have
   */
  public static List<Specialization> listSpecializationsNotForDoctor(int doctorID)
  {
    DBMinder minder = DBMinder.instance();
    ArrayList<Specialization> toReturn = new ArrayList<Specialization>();
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    try
    {
      //retrieve the list of specializations
      ps = conn.prepareStatement("SELECT id, display_name, cost FROM Specialization "
                                +"WHERE id NOT IN (SELECT specialization_id FROM HasSpecialization WHERE doctor_id = ?) ORDER BY id ASC");
      ps.setInt(1, doctorID);
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
      ps = conn.prepareStatement("SELECT id, name FROM Doctor ORDER BY id ASC");
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