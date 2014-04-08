package reasonablecare;

import java.util.*;

import reasonablecare.data.*;

public class ReasonableCare
{
  public static void main(String args[])
  {
    Scanner scan = new Scanner(System.in);
    scan.useDelimiter("\\n");
    
    boolean loggedIn = false;
    
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
        loggedIn = User.logInUser(User.UserType.STUDENT, uid, pass);
        break;
      case 1:
        System.out.printf("\nYou're attempting to log in as a nurse with UID [%d] and password '%s'\n", uid, pass);
        loggedIn = User.logInUser(User.UserType.NURSE, uid, pass);
        break;
      case 2:
        System.out.printf("\nYou're attempting to log in as a doctor with UID [%d] and password '%s'\n", uid, pass);
        loggedIn = User.logInUser(User.UserType.DOCTOR, uid, pass);
        break;
      default:
        System.out.printf("\nYou're attempting to log in as a [%d] with UID [%d] and password '%s'\nYou can't do that, stop.\n", type, uid, pass);
        break;
    }
    if(loggedIn)
    {
      System.out.println("Logged in successfully.");
    }
    else
    {
      System.out.println("Unable to login with those credentials. Sorry!");
    }
    DBMinder.instance().close();
    System.out.println("Goodbye!");
  }
}