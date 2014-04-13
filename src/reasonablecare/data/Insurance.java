package reasonablecare.data;

import java.sql.*;

import java.util.List;
import java.util.ArrayList;

public class Insurance
{
  public int id;
  public String name;
  public long deductible;
  public double copayPercent;
  
  public Insurance(int id, String name, long deductible, double copayPercent)
  {
    this.id = id;
    this.name = name;
    this.deductible = deductible;
    this.copayPercent = copayPercent;
  }
  
  public static boolean studentHasFreePhysical(int studentID)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    boolean toReturn = true;
    try
    {
      //the mathematical expression that is subtracted from CURRENT_DATE is a semester in days, estimated
      ps = conn.prepareStatement("SELECT apt_date FROM Appointment WHERE student_id = ? "
                                +"AND to_char(apt_date, 'YYYY') = to_char(CURRENT_DATE, 'YYYY') "
                                +"AND reason = (SELECT id FROM Specialization WHERE display_name = 'Physical')");
      ps.setInt(1, studentID);
      rs = ps.executeQuery();
      while(rs.next())
      {
        toReturn = false;
      }
      rs.close();
      return toReturn;
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return false;
  }
  
  public static boolean updateInsurance(int id, String newName, long newDeductible, double newCopay)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    int neededModifications = 0, doneModifications = 0;
    try
    {
      if(newName.length() > 0)
      {
        neededModifications++;
        PreparedStatement ps = conn.prepareStatement("UPDATE Insurance SET name = ? WHERE id = ?");
        ps.setString(1, newName);
        ps.setInt(2, id);
        int numRowsAffected = ps.executeUpdate();
        doneModifications+= numRowsAffected;
      }
      if(newDeductible >= 0)
      {
        neededModifications++;
        PreparedStatement ps = conn.prepareStatement("UPDATE Insurance SET deductible = ? WHERE id = ?");
        ps.setLong(1, newDeductible);
        ps.setInt(2, id);
        int numRowsAffected = ps.executeUpdate();
        doneModifications+= numRowsAffected;
      }
      if(newCopay >= 0.0)
      {
        neededModifications++;
        PreparedStatement ps = conn.prepareStatement("UPDATE Insurance SET copay_percentage = ? WHERE id = ?");
        ps.setInt(1, (int)newCopay*100);
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
  
  public static Insurance insuranceForStudent(int studentID)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    Insurance toReturn = null;
    try
    {
      //the mathematical expression that is subtracted from CURRENT_DATE is a semester in days, estimated
      ps = conn.prepareStatement("SELECT Insurance.id, Insurance.name, Insurance.deductible, Insurance.copay_percentage "
                                +"FROM Student, Insurance WHERE Student.id = ? AND Student.insurance_id = Insurance.id");
      ps.setInt(1, studentID);
      rs = ps.executeQuery();
      while(rs.next())
      {
        toReturn = new Insurance(rs.getInt(1), rs.getString(2), rs.getLong(3), ((double)rs.getInt(4))/100.0);
      }
      rs.close();
      return toReturn;
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return toReturn;
  }
  
  public static List<Insurance> insurances()
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    List<Insurance> toReturn = new ArrayList<Insurance>();
    try
    {
      ps = conn.prepareStatement("SELECT id, name, deductible, copay_percentage FROM Insurance ORDER BY id ASC");
      rs = ps.executeQuery();
      while(rs.next())
      {
        toReturn.add(new Insurance(rs.getInt(1), rs.getString(2), rs.getLong(3), ((double)rs.getInt(4))/100.0));
      }
      rs.close();
      return toReturn;
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return toReturn;
  }
  
  public static long copayLeftForStudent(int studentID)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    long toReturn = -1;
    try
    {
      //the mathematical expression that is subtracted from CURRENT_DATE is a semester in days, estimated
      ps = conn.prepareStatement("SELECT sum(copay) FROM Appointment WHERE student_id = ? AND to_char(apt_date, 'YYYY') = to_char(CURRENT_DATE, 'YYYY')");
      ps.setInt(1, studentID);
      rs = ps.executeQuery();
      while(rs.next())
      {
        toReturn = rs.getInt(1);
      }
      rs.close();
      return toReturn;
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return toReturn;
  }
}