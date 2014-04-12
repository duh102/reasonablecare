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
    DOCTOR("Doctor"),
    UNKNOWN("Unknown");
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
  
  public static User registerStudent(String name, String password, Date admitDate, int insuranceID)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    User toReturn = null;
    try
    {
      //don't want to view other people's inserts when we're selecting
      conn.setAutoCommit(false);
      PreparedStatement ps = conn.prepareStatement("INSERT INTO Student (id, name, login_info, admit_date, insurance_id)"
                                                  +"VALUES (studentID.nextval, ?, ?, ?, ?)");
      ps.setString(1, name);
      ps.setString(2, password);
      ps.setDate(3, admitDate);
      ps.setInt(4, insuranceID);
      int numRowsAffected = ps.executeUpdate();
      //each insert really shouldn't affect more than one row, that'd be really weird
      if(numRowsAffected >= 1)
      {
        ps = conn.prepareStatement("SELECT * FROM (SELECT id, name FROM Student ORDER BY id DESC) WHERE ROWNUM <=1");
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
          toReturn = new User(rs.getInt(1),rs.getString(2),UserType.STUDENT);
        }
        conn.commit();
        return toReturn;
      }
      else
      {
        conn.commit();
        return toReturn;
      }
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return toReturn;
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
      PreparedStatement ps = conn.prepareStatement("SELECT id FROM "+type.dbName+" WHERE id = ? AND login_info = ?");
      ps.setInt(1, uid);
      ps.setString(2, password);
      ResultSet rs = ps.executeQuery();
      while(rs.next())
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
  
  /* returns the number of vaccines the student has received
   * assumes that the vaccination reason is 'Vaccination'
   */
  public static int getVaccinesForStudent(int studentID)
  {
    DBMinder minder = DBMinder.instance();
    int numVaccinations = 0;
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    try
    {
      //retrieve the number of vaccinations the student has received
      //we assume that a non-canceled appointment in the past has been attended
      ps = conn.prepareStatement("SELECT count(*) AS vaccines FROM Appointment WHERE reason = (SELECT id FROM Specialization "
                                +"WHERE display_name = 'Vaccination') AND apt_date < CURRENT_DATE "
                                +"AND timestamp_canceled IS NULL AND student_id = ?");
      ps.setInt(1, studentID);
      rs = ps.executeQuery();
      while(rs.next())
      {
        numVaccinations = rs.getInt(1);
      }
      rs.close();
      return numVaccinations;
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return -1;
  }
  
  /* returns whether or not a student is on hold based on their vaccination record
   */
  public static boolean isStudentHeld(int student)
  {
    DBMinder minder = DBMinder.instance();
    int numVaccinations = getVaccinesForStudent(student);
    boolean semesterPassed = false;
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    try
    {
      //the mathematical expression that is subtracted from CURRENT_DATE is a semester in days, estimated
      ps = conn.prepareStatement("SELECT (CASE WHEN (admit_date <= (CURRENT_DATE - (30*4.5))) THEN 1 ELSE 0 END) AS semester "
                                +"FROM Student WHERE id = ?");
      ps.setInt(1, student);
      rs = ps.executeQuery();
      while(rs.next())
      {
        semesterPassed = rs.getInt(1) == 1;
      }
      rs.close();
      return numVaccinations < 3 && semesterPassed;
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return false;
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
        neededModifications++;
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
  
  public static boolean updateDoctor(int doctorID, String phoneNum)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    try
    {
      PreparedStatement ps = conn.prepareStatement("UPDATE Doctor SET phone_num = ? WHERE id = ?");
      ps.setString(1, phoneNum);
      ps.setInt(2, doctorID);
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
}