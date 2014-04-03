package reasonablecare;

import java.util.*;

public class ReasonableCare
{
  public static void main(String args[])
  {
    Scanner scan = new Scanner(System.in);
    scan.useDelimiter("\\n");
    System.out.println("Welcome to ReasonableCare's client! Please log in below.");
    System.out.print("User types:\n[0] Student\n[1] Nurse\n[2] Doctor\n> ");
    int type = scan.nextInt();
    System.out.print("\nUser ID:\n> ");
    int uid = scan.nextInt();
    System.out.print("\nPassword:\n> ");
    String pass = scan.next();
    switch(type)
    {
      case 0:
        System.out.printf("\nYou're attempting to log in as a student with UID [%d] and password '%s'\n", uid, pass);
        break;
      case 1:
        System.out.printf("\nYou're attempting to log in as a nurse with UID [%d] and password '%s'\n", uid, pass);
        break;
      case 2:
        System.out.printf("\nYou're attempting to log in as a doctor with UID [%d] and password '%s'\n", uid, pass);
        break;
      default:
        System.out.printf("\nYou're attempting to log in as a [%d] with UID [%d] and password '%s'\n", type, uid, pass);
        break;
    }
  }
}