package reasonablecare.data;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class Schedule
{
  /* returns true if the update was successful
   */
  public static boolean updateDoctorSchedule(int doctorID, int dayNum, int slotStart, int slotEnd)
  {
    DBMinder minder = DBMinder.instance();
    Connection conn = minder.getConnection();
    try
    {
      PreparedStatement ps;
      if(slotStart < 48)
      {
         ps = conn.prepareStatement("UPDATE WorkDay SET start_timeslot = ?, end_timeslot = ? WHERE doctor_id = ? AND day_of_week = ?");
         ps.setInt(1, slotStart);
         ps.setInt(2, slotEnd);
         ps.setInt(3, doctorID);
         ps.setInt(4, dayNum);
      }
      else
      {
         ps = conn.prepareStatement("DELETE FROM WorkDay WHERE doctor_id = ? AND day_of_week = ?");
         ps.setInt(1, doctorID);
         ps.setInt(2, dayNum);
      }
      int numRowsAffected = ps.executeUpdate();
      //each update really shouldn't affect more than one row, that'd be really weird
      if(numRowsAffected >= 1)
      {
        return true;
      }
      else
      {
        if(slotStart >= 0 && slotStart < 48)
        {
          ps = conn.prepareStatement("INSERT INTO WorkDay (start_timeslot, end_timeslot, doctor_id, day_of_week) "
                                    +"VALUES (?, ?, ?, ?)");
          ps.setInt(1, slotStart);
          ps.setInt(2, slotEnd);
          ps.setInt(3, doctorID);
          ps.setInt(4, dayNum);
          numRowsAffected = ps.executeUpdate();
          //each insert really shouldn't affect more than one row, that'd be really weird
          if(numRowsAffected >= 1)
          {
            return true;
          }
          else
          {
            return false;
          }
        }
        return false;
      }
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return false;
  }
  
  /* returns the free slots during the day that a given doctor has on a given day
   */
  public static List<Integer> retrieveDoctorFreeSlots(Date date, int doctorID)
  {
    DBMinder minder = DBMinder.instance();
    ArrayList<Integer> toReturn = new ArrayList<Integer>();
    Connection conn = minder.getConnection();
    PreparedStatement ps;
    ResultSet rs;
    try
    {
      //retrieve hours working
      int timeBegin = -1, timeEnd = -1;
      ps = conn.prepareStatement("SELECT start_timeslot, end_timeslot FROM WorkDay WHERE doctor_id = ? AND day_of_week = (SELECT D - 1 FROM(SELECT TO_CHAR (?, 'D') D FROM DUAL))");
      ps.setInt(1, doctorID);
      ps.setDate(2, date);
      rs = ps.executeQuery();
      while(rs.next())
      {
        timeBegin = rs.getInt(1);
        timeEnd = rs.getInt(2);
      }
      rs.close();
      
      //expand hours working
      if(timeBegin != -1 && timeEnd != -1)
      {
        for(int i = timeBegin; i <= timeEnd; i++)
        {
          toReturn.add(new Integer(i));
        }
      }
      
      //retrieve appointments that date
      ps = conn.prepareStatement("select time_slot_beginning, time_slot_end from appointment where doctor_id = ? and apt_date = ?");
      ps.setInt(1, doctorID);
      ps.setDate(2, date);
      rs = ps.executeQuery();
      
      //remove occupied time slots
      while(rs.next())
      {
        timeBegin = rs.getInt(1);
        timeEnd = rs.getInt(2);
        for(int i = timeBegin; i <= timeEnd; i++)
        {
          toReturn.remove(new Integer(i));
        }
      }
      rs.close();
      //return list
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return toReturn;
  }
}