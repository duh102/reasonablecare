package reasonablecare.data;

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
      ps = conn.prepareStatement("SELECT id, name, deductible, copay_percentage FROM Student, Insurance WHERE Student.id = ? AND Student.insurance_id = Insurance.id;");
      ps.setInt(1, student);
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
      ps = conn.prepareStatement("SELECT sum(copay) FROM Appointment WHERE student_id = ? AND to_char(apt_date, 'YYYY') = to_char(CURRENT_DATE, 'YYYY');");
      ps.setInt(1, student);
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