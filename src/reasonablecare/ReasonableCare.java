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
    User.UserType userType = User.UserType.UNKNOWN;
    
    System.out.println("Welcome to ReasonableCare's client! Please log in below.");
    System.out.print("User types:\n"
                       +"[0] Student\n"
                       +"[1] Nurse\n"
                       +"[2] Doctor\n"
                       +"> ");
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
        userType = User.UserType.STUDENT;
        break;
      case 1:
        System.out.printf("\nYou're attempting to log in as a nurse with UID [%d] and password '%s'\n", uid, pass);
        loggedIn = User.logInUser(User.UserType.NURSE, uid, pass);
        userType = User.UserType.NURSE;
        break;
      case 2:
        System.out.printf("\nYou're attempting to log in as a doctor with UID [%d] and password '%s'\n", uid, pass);
        loggedIn = User.logInUser(User.UserType.DOCTOR, uid, pass);
        userType = User.UserType.DOCTOR;
        break;
      default:
        System.out.printf("\nYou're attempting to log in as a [%d] with UID [%d] and password '%s'\nYou can't do that, stop.\n", type, uid, pass);
        break;
    }
    if(loggedIn)
    {
      System.out.println("Logged in successfully.");
      switch(userType)
      {
        case STUDENT:
          System.out.print("Here's the operations you can perform!\n"
                               +"[0] Update user account\n"
                               +"[1] some other stuff\n"
                               +"[2] Make an Appointment\n"
                               +"> ");
          int command = scan.nextInt();
          switch(command)
          {
            case 0:
              System.out.println("\nUnimplemented");
              break;
            case 1:
              System.out.println("\nUnimplemented");
              break;
            case 2:
              System.out.println("\nAppointment things!");
              break;
            default:
              System.out.println("\nNot a valid command, logging out");
              break;
          }
          break;
        case NURSE:
          System.out.println("\nUnimplemented");
          break;
        case DOCTOR:
          System.out.println("\nUnimplemented");
          break;
      }
    }
    else
    {
      System.out.println("Unable to login with those credentials. Sorry!");
    }
    DBMinder.instance().close();
    System.out.println("Goodbye!");
  }
}