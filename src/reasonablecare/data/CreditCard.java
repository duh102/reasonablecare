package reasonablecare.data;

public class CreditCard
{
  /* this is just a dummy function to allow us to simulate making an appointment
   * with a possible credit card check failure
   */
  public static boolean checkCardAmount(String ccnum, String ccexp, int amount)
  {
    return Math.random() > 0.2;
  }
}