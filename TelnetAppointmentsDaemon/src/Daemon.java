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
                    boolean sessionActive = true;

                    while(sessionActive){
                        printHeader("Server Daemon Appointments Menu", serverOutput);
                        serverOutput.println("");
                        serverOutput.println("");
                        serverOutput.println("\t\t\t\tPlease input the appropriate menu option number to continue");
                        serverOutput.println("");
                        serverOutput.println("\t\t\t\t1. Add Appointment");
                        serverOutput.println("\t\t\t\t2. View All Appointments");
                        serverOutput.println("\t\t\t\t3. Search Appointments by name");
                        serverOutput.println("\t\t\t\t4. Delete Appointment by ID");
                        serverOutput.println("\t\t\t\t5. Save Changes");
                        serverOutput.println("\t\t\t\t6. End Session");
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
                                clearScreen(serverOutput);
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
                                        serverOutput.println("\u001B[31mIncorrect input. Try again\u001B[0m");
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
                                addAppointmentToArray(newAppointment, serverOutput);
                                serverOutput.println("\u001B[32mNew appointment for " + suggestedName + " on " + parsedDate + " successfully captured to database!\u001B[0m");
                                break;
                            case "2":

                                clearScreen(serverOutput);
                                printHeader("Listing All Upcoming Appoints in Order", serverOutput);
                                sortArrayByDate();
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
                                clearScreen(serverOutput);
                                printHeader("Search Appointments by Name", serverOutput);
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
                                        serverOutput.println("\u001B[31mNo results found!\u001B[0m");
                                    }
                                }
                                break;
                            case "4":
                                clearScreen(serverOutput);
                                printHeader("Delete Appointment by ID", serverOutput);
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
                                            serverOutput.println("\u001B[31mPlease enter a number as the appointment id to be delete!\u001B[0m");
                                            serverOutput.println("");
                                    }
                                } while(!idFlag);
                                idFlag =false;
                                if(searchArrayByID(suggestedIdNumberToDelete)){
                                    deleteByIndex(suggestedIdNumberToDelete);
                                    serverOutput.println("");
                                    serverOutput.println("\u001B[32mAppointment successfully cancelled!\u001B[0m");
                                    serverOutput.println("");
                                } else {
                                    serverOutput.println("");
                                    serverOutput.println("\u001B[31mAppointment id could not be found.\u001B[0m");
                                    serverOutput.println("");
                                }
                                break;
                            case "5":
                                clearScreen(serverOutput);
                                printHeader("Save State", serverOutput);
                                saveToPath("appointmentsDatabase.txt", serverOutput);
                                serverOutput.println("");
                                break;
                            case "6":
                                clearScreen(serverOutput);
                                serverOutput.println("");
                                serverOutput.println("\u001B[32mGoodbye!\u001B[0m");
                                serverOutput.println("");
                                sessionActive = false;
                                break;
                            default:
                                serverOutput.println("\u001B[31mResponse not understood\u001B[0m");
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

    private static void clearScreen(PrintWriter serverOutput){
        serverOutput.print("\u001B[2J\u001B[H");
        serverOutput.flush();
    }

    private static void printHeader(String text, PrintWriter serverOutput){
        serverOutput.println("");
        serverOutput.println("\u001B[33m\t\t\t\t=== " + text + " ===\u001B[0m");
        serverOutput.println("");
    }


    public static void addAppointmentToArray(Appointments newAppointment, PrintWriter serverOutput){

        if (appointmentsArrayCounter < 100){
            appointmentsArray[appointmentsArrayCounter] = newAppointment;
            appointmentsArrayCounter++;
            System.out.println("\u001B[32mSuccessfully added new appointment for " +
                    newAppointment.getVisitorName() + "\u001B[0m");
        } else {
            serverOutput.println("\u001B[31mError! Appointments list full.\u001B[0m");
        }
    }

    public static void searchArrayByName(String searchString, PrintWriter serverOutput){

        for(int i = 0; i < appointmentsArrayCounter; i++){
            if(appointmentsArray[i].getVisitorName().toLowerCase().contains(searchString.toLowerCase())){
                serverOutput.println(appointmentsArray[i].toString());
                return;
            }
        }
        serverOutput.println("\u001B[31mCould not be found\u001B[0m");
    }

    public static void sortArrayByDate(){
        for(int j = 0; j < appointmentsArrayCounter-1; j++) {
            for (int i = 0; i < appointmentsArrayCounter - 1; i++) {
                if (appointmentsArray[i].getArrivalTime().isAfter(appointmentsArray[i + 1].getArrivalTime())) {
                    Appointments tempAppointment = appointmentsArray[i];
                    appointmentsArray[i] = appointmentsArray[i + 1];
                    appointmentsArray[i + 1] = tempAppointment;
                }
            }
        }
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
        appointmentsArrayCounter = 0;
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
//            System.out.println("Deleting File: " + fileName);
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
                    serverOut.println("\u001B[32mSuccessfully saved database!\u001B[0m");
                }
            } catch (IOException e){
                serverOut.println("\u001B[31mSomething went wrong\u001B[0m");
            }
        }
    }
}
