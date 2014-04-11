package reasonablecare.data;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class Appointment
{
  public int doctorID, studentID, reasonID, startTime, endTime;
  public Date when;
  public Timestamp whenMade, whenCanceled;
  
  public Appointment(int doctorID, int studentID, int reasonID, Date when, int startTime, int endTime, Timestamp whenMade, Timestamp whenCanceled)
  {
    this.doctorID = doctorID;
    this.studentID = studentID;
    this.reasonID = reasonID;
    this.when = when;
    this.startTime = startTime;
    this.endTime = endTime;
    this.whenMade = whenMade;
    this.whenCanceled = whenCanceled;
  }
  
  public static boolean cancelAppointment(int doctorID, int studentID, Timestamp timeCreated)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    try
    {
      PreparedStatement ps = conn.prepareStatement("UPDATE Appointment SET timestamp_canceled = (SELECT CURRENT_TIMESTAMP FROM Dual) WHERE doctor_id = ? AND student_id = ? AND timestamp_created = ?");
      ps.setInt(1, doctorID);
      ps.setInt(2, studentID);
      ps.setTimestamp(3, timeCreated);
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
  
  public static List<Appointment> allAppointmentsForStudent(int studentID)
  {
    DBMinder minder = DBMinder.instance();
    ArrayList<Appointment> toReturn = new ArrayList<Appointment>();
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    try
    {
      //retrieve the list of appointments
      ps = conn.prepareStatement("SELECT doctor_id, student_id, reason, apt_date, time_slot_beginning, time_slot_end, timestamp_created, timestamp_canceled"
                                +"FROM Appointment where student_id = ? ORDER BY timestamp_created DESC;");
      rs = ps.executeQuery();
      while(rs.next())
      {
        toReturn.add(new Appointment(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getInt(5), rs.getInt(6), rs.getTimestamp(7), rs.getTimestamp(8)));
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
  
  public static List<Appointment> allAppointmentsForDoctor(int doctorID)
  {
    DBMinder minder = DBMinder.instance();
    ArrayList<Appointment> toReturn = new ArrayList<Appointment>();
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    try
    {
      //retrieve the list of appointments
      ps = conn.prepareStatement("SELECT doctor_id, student_id, reason, apt_date, time_slot_beginning, time_slot_end, timestamp_created, timestamp_canceled"
                                +"FROM Appointment where doctor_id = ? ORDER BY timestamp_created DESC;");
      rs = ps.executeQuery();
      while(rs.next())
      {
        toReturn.add(new Appointment(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getInt(5), rs.getInt(6), rs.getTimestamp(7), rs.getTimestamp(8)));
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