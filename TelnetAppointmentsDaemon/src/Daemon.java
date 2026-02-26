import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Daemon {
    private static Appointments[] appointmentsArray = new Appointments[100];
    private static int appointmentsArrayCounter = 0;

    public static void main(String[] args) throws Exception {

        final int PORT = 2323;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Appointments Menu Server Daemon listening on " + PORT);

            while(true){
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Client connected: " + socket.getRemoteSocketAddress());

                    BufferedReader clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter serverOutput = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                    while(true){
                        serverOutput.println("");
                        serverOutput.println("              === Server Daemon Appointments Menu ===");
                        serverOutput.println("");
                        serverOutput.println("");
                        serverOutput.println("");
                        serverOutput.println("              Please input the appropriate menu option number to continue");
                        serverOutput.println("");
                        serverOutput.println("              1. Add Appointment");
                        serverOutput.println("              2. View All Appointments");
                        serverOutput.println("              3. Search Appointments by name");
                        serverOutput.println("              4. Delete Appointment by ID");
                        serverOutput.println("              5. Save Changes");
                        serverOutput.println("              6. End Session");
                        serverOutput.println("");
                        serverOutput.print("> ");
                        serverOutput.flush();

                        String menuSelection = clientInput.readLine();
                        String suggestedDate, suggestedName, suggestedReason;
                        String formatter = "yyyy-MM-dd HH:mm";
                        boolean validDate = false;
                        LocalDateTime parsedDate = null;

//                        if (menuSelection == null) continue;

                        switch (menuSelection.trim()) {
                            case "1":
                                serverOutput.println("");
                                validDate = false;
                                do {
                                    serverOutput.println("Enter Appointment Date (YYYY-MM-DD HH:MM)");
                                    serverOutput.println("");
                                    serverOutput.print("> ");
                                    suggestedDate = clientInput.readLine();

                                    try{
                                        parsedDate = LocalDateTime.parse(suggestedDate, DateTimeFormatter.ofPattern(formatter));
                                        validDate = true;
                                    } catch (DateTimeException e){
                                        serverOutput.println("Incorrect input. Try again");
                                    }
                                }while(!validDate);
                                do {
                                    serverOutput.println("Enter Visitor name");
                                    serverOutput.println("");
                                    serverOutput.print("> ");
                                    suggestedName = clientInput.readLine();
                                } while(suggestedName.isEmpty());
                                do {
                                    serverOutput.println("Enter appointment reason");
                                    serverOutput.println("");
                                    serverOutput.print("> ");
                                    suggestedReason = clientInput.readLine();
                                } while(suggestedName.isEmpty());
                                Appointments newAppointment = new Appointments("100", parsedDate,suggestedName,suggestedReason);
                                addAppointmentToArray(newAppointment);
                                serverOutput.println("New appointment for " + suggestedName + " on " + parsedDate + " successfully captured to database!");
                                break;
                            case "2":
                                serverOutput.println("You want view all appointments");
                                serverOutput.print("> ");
                                break;
                            case "3":
                                serverOutput.println("You want to search appointments by id");
                                serverOutput.print("> ");
                                break;
                            case "4":
                                serverOutput.println("You want to delete an appointment by id");
                                serverOutput.print("> ");
                                break;
                            case "5":
                                serverOutput.println("You want to save changes");
                                serverOutput.print("> ");
                                break;
                            case "6":
                                serverOutput.println("You want to end session");
                                serverOutput.print("> ");
                                break;
                            default:
                                serverOutput.println("Response not understood");
                                serverOutput.print("> ");
                                break;

                        }
                    }

                }
            }
        }

//        Appointments appointment1 = new Appointments("001",
//                LocalDateTime.of(2026,3,14, 14,30,00),
//                "Peter Park", "Fitting braces");
//
//        addAppointment(appointment1);
//
//
//        Appointments appointment2 = new Appointments("002",
//                LocalDateTime.of(2026,3,15, 9,00,00),
//                "Thato Piri", "Root Canal");
//
//        addAppointment(appointment2);
//
//        searchArrayByName("thato");
    }

    public static void addAppointmentToArray(Appointments newAppointment){

        if (appointmentsArrayCounter < 100){
            appointmentsArray[appointmentsArrayCounter] = newAppointment;
            appointmentsArrayCounter++;
            System.out.println("Successfully added new appointment for " +
                    newAppointment.getVisitorName());
        } else {
            System.out.println("Error! Appointments list full.");
        }
    }

    public static void searchArrayByName(String searchString){

        for(int i = 0; i < appointmentsArrayCounter; i++){
            if(appointmentsArray[i].getVisitorName().toLowerCase().contains(searchString.toLowerCase())){
                System.out.println(appointmentsArray[i].toString());
                return;
            }
        }
        System.out.println("Could not be found");
    }

    public static String searchArrayByID(String searchString){

        int foundIndex;
        for(int i = 0; i < appointmentsArrayCounter; i++){
            if(appointmentsArray[i].getId().equals(searchString)){
                foundIndex = i;
                return appointmentsArray[foundIndex].toString();
            }
        }
        return "Could not be found";
    }

    public static void deleteByIndex(int indexToDelete){
        if(indexToDelete < 0 || indexToDelete > appointmentsArrayCounter){
            System.out.println("Invalid record index to delete");
            return;
        }

        for (int i = indexToDelete; i < appointmentsArrayCounter-1; i++){
            appointmentsArray[i] = appointmentsArray[i+1]; // shift array values to the left and over the deleted index
        }

        appointmentsArray[appointmentsArrayCounter-1] = null; // set last index to null
        appointmentsArrayCounter--;

        System.out.println("Appointment record deleted");
    }
}
