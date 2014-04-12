package reasonablecare.data;

public class CreditCard
{
  /* this is just a dummy function to allow us to simulate making an appointment
   * with a possible credit card check failure
   * the credit check will fail 20% of the time
   */
  public static boolean checkCardAmount(String ccnum, String ccexp, long amount)
  {
    return Math.random() > 0.2;
  }
}