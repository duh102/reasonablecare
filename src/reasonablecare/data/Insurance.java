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
  
}