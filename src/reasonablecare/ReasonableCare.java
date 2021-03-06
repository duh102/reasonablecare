package reasonablecare;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.sql.*;

import reasonablecare.data.*;

public class ReasonableCare
{
  /* This large function is the user interface for the application
   * some commands are looped until the user wishes to exit, most are
   * single loop through, have to reconnect to do more
   */
  public static void main(String args[])
  {
    Scanner scan = new Scanner(System.in).useLocale(Locale.US);;
    scan.useDelimiter(Pattern.compile("[\\r\\n;]+"));
    
    boolean loggedIn = false;
    User.UserType userType = User.UserType.UNKNOWN;
    
    System.out.println("Welcome to ReasonableCare's client! Please log in below.");
    System.out.print("User types:\n"
                       +"[0] Student\n"
                       +"[1] Nurse\n"
                       +"[2] Doctor\n"
                       +"[3] Register a new Student\n"
                       +"> ");
    int type = scan.nextInt();
    if(type >= 0 && type <= 2)
    {
      System.out.print("\nUser ID:\n> ");
      int uid = scan.nextInt();
      System.out.print("\nPassword:\n> ");
      String pass = scan.next();
      User user = null;
      
      switch(type)
      {
        case 0:
          System.out.printf("\nYou're attempting to log in as a %s with UID [%d] and password '%s'\n", User.UserType.STUDENT.dbName, uid, pass);
          loggedIn = (user = User.logInUser(User.UserType.STUDENT, uid, pass)) != null;
          userType = User.UserType.STUDENT;
          break;
        case 1:
          System.out.printf("\nYou're attempting to log in as a nurse with UID [%d] and password '%s'\n", uid, pass);
          loggedIn = (user = User.logInUser(User.UserType.NURSE, uid, pass)) != null;
          userType = User.UserType.NURSE;
          break;
        case 2:
          System.out.printf("\nYou're attempting to log in as a doctor with UID [%d] and password '%s'\n", uid, pass);
          loggedIn = (user = User.logInUser(User.UserType.DOCTOR, uid, pass)) != null;
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
            //########################################################
            //###################### STUDENT #########################
            //########################################################
            System.out.print("Here's the operations you can perform!\n"
                               +"[0] Update user account\n"
                               +"[1] Check if your account is held\n"
                               +"[2] Make an Appointment\n"
                               +"[3] Check appointments\n"
                               +"[4] Cancel an appointment\n"
                               +"> ");
            int command = scan.nextInt();
            switch(command)
            {
              case 0:
                System.out.print("If you want to change your name, enter the new name below:\n> ");
                String newName = scan.next();
                System.out.print("If you want to change your password, enter the new password below:\n> ");
                String newPass = scan.next();
                if(User.modifyUser(user.type, user.id, newName, newPass))
                {
                  System.out.println("User modified successfully.");
                }
                else
                {
                  System.out.println("User not modified.");
                }
                break;
              case 1:
                boolean isHeld = User.isStudentHeld(user.id);
                int numberVaccinations = User.getVaccinesForStudent(user.id);
                if(isHeld)
                {
                  System.out.printf("Your account has been held, you have %d of 3 required vaccinations."
                                      +"\nPlease make appointment(s) to receive your vaccinations.\n", numberVaccinations);
                }
                else
                {
                  System.out.printf("Your account is not held and you have had %d of 3 required vaccinations."
                                      +"\nIf you have not gotten all of your required vaccinations, please make appointment(s) to receive them.", numberVaccinations);
                }
                break;
              case 2:
                System.out.println("\n======== Make Appointment ========");
                try
                {
                  Connection conn = DBMinder.instance().getConnection();
                  conn.setAutoCommit(false);
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
                    List<User> doctors = Specialization.doctorsWithSpecialization(reasonID);
                    System.out.println("Here's doctors that can help you with that, please choose one to make an appointment with.");
                    i = 0;
                    for(User doctor : doctors)
                    {
                      System.out.printf("[%d] %s\n", i++, doctor.name);
                    }
                    System.out.print("> ");
                    //unhandled exception: out of bounds
                    doctorID = doctors.get(scan.nextInt()).id;
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
                          for(int timeTest = beginTime; timeTest < endTime && goodRange; timeTest++)
                          {
                            if(!schedule.contains(timeTest))
                            {
                              goodRange = false;
                            }
                          }
                          if(goodRange)
                          {
                            satisfactoryTimes = true;
                            goodDay = true;
                            System.out.printf("Ok, the appointment will be on %s from %s to %s.\n", dateOfApt, formatTimeslot(beginTime), formatTimeslot(endTime));
                          }
                          else
                          {
                            System.out.println("That is an invalid time range, please choose another range.");
                          }
                        }
                      }
                    }
                    else
                    {
                      System.out.println("No open time slots on that day, please choose another date.");
                    }
                  }
                  Insurance studentsInsurance = Insurance.insuranceForStudent(user.id);
                  long paidCopay = Insurance.copayLeftForStudent(user.id);
                  long copayAmount = paidCopay < studentsInsurance.deductible?
                    Math.round(Math.min(special.baseCost * studentsInsurance.copayPercent,
                                        studentsInsurance.deductible - paidCopay))
                    : 0;
                  if(special.displayName.equals("Physical") && Insurance.studentHasFreePhysical(user.id))
                  {
                    copayAmount = 0;
                  }
                  long insuranceCost = paidCopay < studentsInsurance.deductible? special.baseCost - copayAmount: special.baseCost;
                  System.out.printf("Your copayment for this visit will be $%d, please enter your credit card information."
                                      +"\nYou will not be charged until we confirm the appointment.\n", copayAmount);
                  boolean goodCard = false;
                  String ccNum = "", ccExp = "";
                  while(!goodCard)
                  {
                    System.out.print("Credit card number> ");
                    ccNum = scan.next();
                    System.out.print("Expiration date (mm/yy)> ");
                    ccExp = scan.next();
                    if(CreditCard.checkCardAmount(ccNum, ccExp, copayAmount))
                    {
                      System.out.println("You've been preapproved for the copay amount.");
                      goodCard = true;
                    }
                    else
                    {
                      System.out.println("The credit card company for that card refused preapproval, please try again or try a different card.");
                    }
                  }
                  System.out.print("Before we go ahead and make the appointment, you can describe the problem in more detail to the doctor\n> ");
                  String studentNotes = scan.next();
                  System.out.println("Ok! We've got all the information we need. Attempting to confirm your appointment now...");
                  if(Appointment.makeAppointment(doctorID, user.id, dateOfApt, beginTime, endTime,
                                                 insuranceCost, copayAmount, ccExp, ccNum,
                                                 studentNotes, reasonID))
                  {
                    System.out.printf("You've successfully created the appointment! We will now charge your credit card."
                                        +"\nYour appointment is on %s from %s to %s.\nNow logging you out.\n",
                                      dateOfApt, formatTimeslot(beginTime), formatTimeslot(endTime));
                    conn.commit();
                    
                  }
                  else
                  {
                    System.out.println("We're sorry but we were unable to create your appointment."
                                         +"\nPlease log in again and attempt to make another appointment.");
                    conn.rollback();
                  }
                  conn.setAutoCommit(true);
                }
                catch(SQLException sqle)
                {
                  sqle.printStackTrace();
                }
                break;
              case 3:
                System.out.println("\nHere are all your appointments:");
                List<Appointment> appointments = Appointment.allAppointmentsForStudent(user.id);
              for(Appointment apt : appointments)
              {
                System.out.println(apt);
              }
              break;
              case 4:
                List<Appointment> pendingAppointments = Appointment.allPendingAppointmentsForStudent(user.id);
                if(pendingAppointments.size() > 0)
                {
                  System.out.println("Here are all your pending appointments, choose one to cancel:");
                  int i = 0;
                  for(Appointment apt : pendingAppointments)
                  {
                    System.out.printf("[%d] %s\n", i++, apt);
                  }
                  int aptToCancel = scan.nextInt();
                  System.out.println("Ok, we'll cancel that appointment.");
                  Appointment toCancel = pendingAppointments.get(aptToCancel);
                  if(Appointment.cancelAppointment(toCancel.doctorID, toCancel.studentID,toCancel.whenMade))
                  {
                    System.out.println("Appointment canceled, logging you out.");
                  }
                  else
                  {
                    System.out.println("Unable to cancel appointment, please speak with a receptionist.");
                  }
                }
                else
                {
                  System.out.println("You don't have any pending appointments. Logging you out.");
                }
                break;
              default:
                System.out.println("\nNot a valid command, logging out");
                break;
            }
            //########################################################
            //###################### STUDENT #########################
            //########################################################
            break;
          case NURSE:
            //########################################################
            //####################### NURSE ##########################
            //########################################################
            System.out.print("Here's the operations you can perform!\n"
                               +"[0] Update user account\n"
                               +"[1] Check held Student accounts\n"
                               +"> ");
            command = scan.nextInt();
            switch(command)
            {
              case 0:
                System.out.print("If you want to change your name, enter the new name below:\n> ");
                String newName = scan.next();
                System.out.print("If you want to change your password, enter the new password below:\n> ");
                String newPass = scan.next();
                if(User.modifyUser(user.type, user.id, newName, newPass))
                {
                  System.out.println("User modified successfully.");
                }
                else
                {
                  System.out.println("User not modified.");
                }
                break;
              case 1:
                System.out.println("Here are all the students with held accounts:");
                List<StudentHeldRecord> records = User.heldList();
                System.out.println("[sid] Name: HOLD/NOHOLD\n---------------------");
              for(StudentHeldRecord record : records)
              {
                System.out.println(record);
              }
              break;
            }
            //########################################################
            //####################### NURSE ##########################
            //########################################################
            break;
          case DOCTOR:
            //########################################################
            //####################### DOCTOR #########################
            //########################################################
            System.out.print("Here's the operations you can perform!\n"
                               +"[0] Update user account\n"
                               +"[1] Update schedule\n"
                               +"[2] Area(s) of specialization\n"
                               +"> ");
            command = scan.nextInt();
            switch(command)
            {
              case 0:
                System.out.print("If you want to change your name, enter the new name below:\n> ");
                String newName = scan.next();
                System.out.print("If you want to change your password, enter the new password below:\n> ");
                String newPass = scan.next();
                System.out.print("If you want to change your phone number, enter the new number below:\n> ");
                String newPhone = scan.next();
                if(newName.length() > 0 || newPass.length() > 0)
                {
                  if(User.modifyUser(user.type, user.id, newName, newPass))
                  {
                    System.out.println("User modified successfully.");
                  }
                  else
                  {
                    System.out.println("User not modified.");
                  }
                }
                if(newPhone.length() > 0)
                {
                  if(User.updateDoctor(user.id, newPhone))
                  {
                    System.out.println("Phone number updated successfully.");
                  }
                  else
                  {
                    System.out.println("Phone number not modified.");
                  }
                }
                break;
              case 1:
                boolean done = false;
                while(!done)
                {
                  System.out.println("What day of the week are you changing your schedule for?");
                  System.out.print("[0] Sunday\n[1] Monday\n[2] Tuesday\n[3] Wednesday\n[4] Thursday\n[5] Friday\n[6] Saturday\n[7] Exit\n> ");
                  int dayOfWeek = scan.nextInt();
                  if(dayOfWeek > 6)
                  {
                    done = true;
                  }
                  else
                  {
                    for(int time = 0; time < 48; time++)
                    {
                      System.out.printf("[%02d] %s\n", time, formatTimeslot(time));
                    }
                    System.out.println("[48] Do not work this day");
                    System.out.print("When do you start work on this day?\n> ");
                    int startTime = scan.nextInt();
                    if(startTime > 47)
                    {
                      if(Schedule.updateDoctorSchedule(user.id, dayOfWeek, 48, -1))
                      {
                        System.out.println("Schedule updated, you are no longer scheduled to work on that day.");
                      }
                      else
                      {
                        System.out.println("Schedule unable to be updated.");
                      }
                    }
                    else
                    {
                      System.out.print("And when do you end?\n> ");
                      int endTime = scan.nextInt();
                      //we trust the user to put in sensical times, endTime must come after startTime
                      if(Schedule.updateDoctorSchedule(user.id, dayOfWeek, startTime, endTime))
                      {
                        System.out.printf("Schedule updated, you are now scheduled to work from %s to %s on that day.\n", formatTimeslot(startTime), formatTimeslot(endTime));
                      }
                      else
                      {
                        System.out.println("Schedule unable to be updated.");
                      }
                    }
                  }
                }
                break;
              case 2:
                List<Specialization> doctorSpecials = Specialization.listSpecializationsForDoctor(user.id);
                int i = 0;
                System.out.println("You have the following specializations recorded in the database.");
                for(Specialization special : doctorSpecials)
                {
                  System.out.printf("[%d] %s\n", i++, special.displayName);
                }
                System.out.printf("[%d] Don't remove any\nWhich one do you want to remove?\n> ", i);
                int toRemove = scan.nextInt();
                if(toRemove >= i)
                {
                  //don't remove
                  List<Specialization> specials = Specialization.listSpecializationsNotForDoctor(user.id);
                  System.out.println("In that case, do you want to add any?");
                  i = 0;
                  for(Specialization special : specials)
                  {
                    System.out.printf("[%d] %s\n", i++, special.displayName);
                  }
                  System.out.printf("[%d] Don't add any\nWhich one do you want to add?\n> ", i);
                  int toAdd = scan.nextInt();
                  if(toAdd >=0 && toAdd < i && Specialization.setDoctorHasSpecialization(user.id, specials.get(toAdd).id, true))
                  {
                    System.out.println("Specialization added to your record.");
                  }
                  else
                  {
                    System.out.println("Specialization not added.");
                  }
                }
                else
                {
                  //remove
                  if(Specialization.setDoctorHasSpecialization(user.id, doctorSpecials.get(toRemove).id, false))
                  {
                    System.out.println("Specialization removed successfully.");
                  }
                  else
                  {
                    System.out.println("Unable to remove specialization.");
                  }
                }
                break;
            }
            //########################################################
            //####################### DOCTOR #########################
            //########################################################
            break;
        }
      }
      else
      {
        System.out.println("Unable to login with those credentials. Sorry!");
      }
    }
    else
    {
      System.out.print("\nName:\n> ");
      String name = scan.next();
      System.out.print("\nPassword:\n> ");
      String pass = scan.next();
      System.out.println("Please select from the below insurances your insurance provider.");
      List<Insurance> insurances = Insurance.insurances();
      int i = 0;
      for(Insurance ins : insurances)
      {
        System.out.printf("[%d] %s\n", i++, ins.name);
      }
      System.out.print("> ");
      int insuranceIndex = scan.nextInt();
      System.out.println("When were you admitted to school? Please enter in yyyy-mm-dd format.");
      Date whenAdmit = Date.valueOf(scan.next());
      System.out.println("Registering you in the database...");
      User newStudent = User.registerStudent(name, pass, whenAdmit, insurances.get(insuranceIndex).id);
      if(newStudent != null)
      {
        System.out.printf("You've been registered as user [%d], please remember this number in order to log in.\n", newStudent.id);
      }
      else
      {
        System.out.println("Unable to register you, please check your credentials and try again.");
      }
    }
    DBMinder.instance().close();
    System.out.println("Goodbye!");
  }
  
  public static String formatTimeslot(int timeslot)
  {
    return String.format("%02d:%02d",(timeslot/2), timeslot%2==0? 0:30);
  }
}