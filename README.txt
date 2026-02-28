Telnet Appointment Server  by Otsile Modiselle u10275381
========================================================


Introduction
_____________

Java was used to develop this Telnet server, which has a user 
interface with a menu to allow users to interactively control 
a data base of appointments over a network. The server uses 
TCP Port 2323 to listen for incoming Telnet connection 
requests. This is required by the assignment spec for all 
communication between clients and the server.


Features 
________

Below is a list of features developed for this server:

1. Add Appointment 
    Capture appointment information (date/time, visitor name, 
    and reason).

2. Viewing All Appointments (in order)
    All appointments that have been entered into the server will 
    be displayed by order of soonest onwards.

3. Search Appointments by Name 
    Users can search for appointments based on a visitor name.

4. Delete Appointment by ID 
    An appointment may be removed from the data base by entering 
    the ID of the appointment to delete.

5. Save Changes 
    Any changes made while in the application will be written to 
    the data base at the end of each use of the application.

6. End Session 
    The telnet session is closed when you choose this option.

The internal data base is maintained in an array of Appointments 
objects.


Data Base Storage
_________________

A text file named "appointmentDatabase.txt" contains the data base 
of appointments. Each line contains a pipe-delimited string with the 
following elements in the order they appear in the string:

- Appointment ID
- Date/Time
- Name of Visitor
- Reason for Visit


Extra Marks Features
____________________

The following additional features were added to the basic requirements 
of the system:

- Initial Data Base Load 
    When the server starts it reads all of the appointments that have 
    previously been entered into the data base.

- Sort Order 
    All appointments are sorted in ascending chronological order so that 
    the next upcoming appointment appears first.

- Terminal Formatting 
    ANSI escape sequences are used to enhance the visual quality of the 
    display to highlight important status messages.


Compiling
_________

You can compile the program using the Java Compiler with the two source 
files provided: Daemon.java and Appointments.java.


Running the Server
__________________

Once you start the server, it will begin accepting incoming connections 
on port 2323.


Connecting to the Server
________________________

You connect to the server using a Telnet Client by providing the server's 
IP address and port 2323.


Project files
_____________

- Daemon.java
- Appointments.java
- appointmentDatabase.txt
- appointments-server.jar
- README.md