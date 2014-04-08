package reasonablecare.data;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class Schedule
{
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
      //retrieve appointments that date
      ps = conn.prepareStatement("select time_slot_beginning, time_slot_end from appointment where doctor_id = ? and apt_date = ?;");
      ps.setInt(1, doctorID);
      ps.setDate(2, date);
      rs = ps.executeQuery();
      //expand hours working and do set difference
      //return list
    }
    catch(SQLException sqle)
    {
    }
    return toReturn;
  }
}