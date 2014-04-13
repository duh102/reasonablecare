package reasonablecare.data;

public class StudentHeldRecord
{
  public int studentID;
  public String studentName;
  public int numVaccinations;
  public boolean semesterPassed;
  
  public StudentHeldRecord(int id, String name, int vacc, boolean passed)
  {
    studentID = id;
    studentName = name;
    numVaccinations = vacc;
    semesterPassed = passed;
  }
  
  public String toString()
  {
    return String.format("[%d] %s: %s", studentID, studentName, numVaccinations < 3 && semesterPassed? "HOLD" : "NOHOLD");
  }
  public String toRaw()
  {
    return String.format("%d\t%s\t%d\t%b", studentID, studentName, numVaccinations, semesterPassed);
  }
}