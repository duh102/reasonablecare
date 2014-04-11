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
              System.out.println("\n======== Make Appointment ========");
              int doctorID = -1, reasonID = -1, beginTime = -1, endTime = -1;
              Date dateOfApt = null;
              //Deciding the doctor
              System.out.print("First, do you have a doctor in mind?\n[0] Yes [1] No\n> ");
              int choice = scan.nextInt();
              if(choice == 0)
              {
                System.out.print("What's the id of the doctor you had in mind?\n> ");
                doctorID = scan.nextInt();
                System.out.println("Ok, we'll use that doctor.");
              }
              else
              {
                System.out.println("That's ok, we'll find you one based on your reason for visiting.");
              }
              System.out.println("What is the reason for your visit? Please choose from the following.");
              List<Specialization> specials = Specialization.listSpecializations();
              int i = 0;
              for(Specialization special : specials)
              {
                System.out.printf("[%d] %s\n", i++, special.displayName);
              }
              System.out.print("> ");
              reasonID = scan.nextInt();
              Specialization special = specials.get(reasonID);
              reasonID = special.id;
              if(doctorID == -1)
              {
                List<User> doctors = doctorsWithSpecialization(reasonID);
                System.out.println("Here's doctors that can help you with that, please choose one to make an appointment with.");
                i = 0;
                for(User doctor : doctors)
                {
                  System.out.printf("[%d] %s\n", i++, doctor.name);
                }
                System.out.print("> ");
                //unhandled exception: out of bounds
                doctorID = doctors.get(scan.nextInt());
              }
              boolean goodDay = false;
              String tempDateString;
              while(!goodDay)
              {
                System.out.print("What day would you like to make an appointment for? Please enter in yyyy-mm-dd format.\n> ");
                tempDateString = scan.next();
                dateOfApt = Date.valueOf(tempDateString);
                List<Integer> schedule = Schedule.retrieveDoctorFreeSlots(dateOfApt, doctorID);
                if(schedule.size() > 0)
                {
                  System.out.println("On that date, that doctor has the following open time slots.\nPlease choose a start and end time for your appointment.");
                  i = 0;
                  for(Integer timeslot : schedule)
                  {
                    System.out.printf("[%02d] %s\n", timeslot, formatTimeslot(timeslot));
                  }
                  boolean satisfactoryTimes = false;
                  while(!satisfactoryTimes)
                  {
                    System.out.print("Start> ");
                    beginTime = scan.nextInt();
                    System.out.print("End> ");
                    endTime = scan.nextInt();
                    if(schedule.contains(beginTime) && schedule.contains(endTime))
                    {
                      boolean goodRange = true;
                      for(int i = beginTime; i < endTime && goodRange; i++)
                      {
                        if(!schedule.contains(i))
                        {
                          goodRange = false;
                        }
                      }
                      if(goodRange)
                      {
                        satisfactoryTimes = true;
                        System.out.printf("Ok, the appointment will be on %s from %s to %s.", dateOfApt, formatTimeslot(beginTime), formatTimeslot(endTime));
                      }
                      else
                      {
                        System.out.println("That is an invalid time range, please choose another range.");
                      }
                    }
                  }
                  Insurance studentsInsurance = insuranceForStudent(uid);
                  long paidCopay = Insurance.copayLeftForStudent(uid);
                  long copayAmount = paidCopay < studentsInsurance.deductible? special.baseCost * studentsInsurance.copayPercent: 0;
                  long insuranceCost = paidCopay < studentsInsurance.deductible? special.baseCost - copayAmount: special.baseCost;
                }
                else
                {
                  System.out.println("No open time slots on that day, please choose another date.");
                }
              }
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
  
  public static String formatTimeslot(int timeslot)
  {
    return String.format("%02d:%02d",(timeslot/2), timeslot%2==0? 0:30);
  }
}