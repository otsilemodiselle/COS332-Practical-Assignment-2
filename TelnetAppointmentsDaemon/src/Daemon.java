import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Daemon {
    private static Appointments[] appointmentsArray = new Appointments[100];
    private static int appointmentsArrayCounter = 0;
    private static int idCounter = 0;

    public static void main(String[] args) throws Exception {

        final int PORT = 2323;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Appointments Menu Server Daemon listening on " + PORT);

            while(true){
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Client connected: " + socket.getRemoteSocketAddress());

                    BufferedReader clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter serverOutput = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                    loadFromPath("appointmentsDatabase.txt", serverOutput);

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
                        String suggestedDate, suggestedName, suggestedReason, searchPhrase;
                        String formatter = "yyyy-MM-dd HH:mm";
                        boolean validDate = false;
                        boolean searchResultsFound = false;
                        LocalDateTime parsedDate = null;
                        int suggestedIdNumberToDelete = 0;


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
                                Appointments newAppointment = new Appointments(++idCounter, parsedDate,suggestedName,suggestedReason);
                                addAppointmentToArray(newAppointment);
                                serverOutput.println("New appointment for " + suggestedName + " on " + parsedDate + " successfully captured to database!");
                                break;
                            case "2":

                                serverOutput.println("");
                                serverOutput.println("                === Listing All Appointments ===");
                                serverOutput.println("");
                                for(int i = 0; i < appointmentsArrayCounter; i++){
                                    serverOutput.println("Aptmt ID: \t" + appointmentsArray[i].getId());
                                    serverOutput.println("Visitor: \t" + appointmentsArray[i].getVisitorName());
                                    serverOutput.println("Date: \t\t" +appointmentsArray[i].getArrivalTime().getDayOfWeek() + " "
                                            + appointmentsArray[i].getArrivalTime().format(DateTimeFormatter.ofPattern(formatter)));
                                    serverOutput.println("Reason: \t" + appointmentsArray[i].getReason());
                                    serverOutput.println("");
                                }

                                break;
                            case "3":
                                serverOutput.println("");
                                serverOutput.println("                === Search Appointments by Name ===");
                                serverOutput.println("");
                                do {
                                    serverOutput.println("Enter a Visitor name to search by: ");
                                    serverOutput.print("> ");
                                    searchPhrase = clientInput.readLine();
                                }while(searchPhrase.isEmpty());
                                serverOutput.println("");
                                serverOutput.println("Search Results:");
                                serverOutput.println("");
                                for(int i = 0; i < appointmentsArrayCounter; i++){
                                    if(appointmentsArray[i].getVisitorName().toLowerCase().contains(searchPhrase.toLowerCase().trim())){
                                        serverOutput.println("Aptmt ID: \t" + appointmentsArray[i].getId());
                                        serverOutput.println("Visitor: \t" + appointmentsArray[i].getVisitorName());
                                        serverOutput.println("Date: \t\t" +appointmentsArray[i].getArrivalTime().getDayOfWeek() + " "
                                                + appointmentsArray[i].getArrivalTime().format(DateTimeFormatter.ofPattern(formatter)));
                                        serverOutput.println("Reason: \t" + appointmentsArray[i].getReason());
                                        serverOutput.println("");
                                        searchResultsFound = true;
                                    } else {
                                        continue;
                                    }
                                    if (!searchResultsFound){
                                        serverOutput.println("No results found!");
                                    }
                                }
                                break;
                            case "4":
                                serverOutput.println("");
                                serverOutput.println("                === Delete Appointment by ID ===");
                                serverOutput.println("");
                                serverOutput.println("Enter Appointment ID of for cancellation:");
                                serverOutput.println("");
                                serverOutput.print("> ");
                                boolean idFlag = false;
                                do {
                                    try {
                                        suggestedIdNumberToDelete = Integer.parseInt(clientInput.readLine());
                                        idFlag = true;
                                    } catch (Exception e) {
                                            serverOutput.println("");
                                            serverOutput.println("Please enter a number as the appointment id to be delete!");
                                            serverOutput.println("");
                                    }
                                } while(!idFlag);
                                idFlag =false;
                                if(searchArrayByID(suggestedIdNumberToDelete)){
                                    deleteByIndex(suggestedIdNumberToDelete);
                                    serverOutput.println("");
                                    serverOutput.println("Appointment successfully cancelled!");
                                    serverOutput.println("");
                                } else {
                                    serverOutput.println("");
                                    serverOutput.println("Appointment id could not be found.");
                                    serverOutput.println("");
                                }
                                break;
                            case "5":
                                serverOutput.println("");
                                serverOutput.println("                === Save State ===");
                                serverOutput.println("");
                                saveToPath("appointmentsDatabase.txt", serverOutput);
                                serverOutput.println("");
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

    public static boolean searchArrayByID(int searchInt){

        for(int i = 0; i < appointmentsArrayCounter; i++){
            if(appointmentsArray[i].getId()==searchInt){
                return true;
            }
        }
        return false;
    }

    public static void deleteByIndex(int searchInt){
        int indexToDelete = 0;
        for(int i = 0; i < appointmentsArrayCounter; i++) {
            if (appointmentsArray[i].getId() == searchInt) {
                indexToDelete = i;
            }
        }
        for (int i = indexToDelete; i < appointmentsArrayCounter-1; i++){
            appointmentsArray[i] = appointmentsArray[i+1]; // shift array values to the left and over the deleted index
        }

        appointmentsArray[appointmentsArrayCounter-1] = null; // set last index to null
        appointmentsArrayCounter--;
    }

    private static void loadFromPath(String fileName, PrintWriter serverOut){

        Path path = Path.of(fileName);

        try {
            BufferedReader reader = Files.newBufferedReader(path);
            String line;
            while ((line = reader.readLine()) != null){
                String[] parts = line.split("\\|");
                int id = Integer.parseInt(parts[0]);
                LocalDateTime time = LocalDateTime.parse(parts[1]);
                String name = parts[2];
                String reason = parts[3];
                appointmentsArray[appointmentsArrayCounter++] =
                        new Appointments(id, time, name, reason);
                idCounter = id;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveToPath(String fileName, PrintWriter serverOut){

        String saveString = "";
        Path path = Path.of(fileName);
        boolean fileExists = Files.exists(path);

        if(fileExists) {
            System.out.println("Deleting File: " + fileName);
            try {
                Files.delete(path);
                fileExists = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(!fileExists){
            try {
                Files.createFile(path);
                for(int i = 0; i < appointmentsArrayCounter; i++){
                    saveString += appointmentsArray[i].getId()+"|"+
                            appointmentsArray[i].getArrivalTime()+"|"+
                            appointmentsArray[i].getVisitorName()+"|"+
                            appointmentsArray[i].getReason()+"\n";
                }
                if(Files.isWritable(path)){
                    Files.writeString(path,saveString);
                    serverOut.println("Successfully saved database!");
                }
            } catch (IOException e){
                serverOut.println("Something went wrong");
            }
        }
    }
}
