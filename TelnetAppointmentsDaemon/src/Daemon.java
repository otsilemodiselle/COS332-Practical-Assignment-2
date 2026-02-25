import java.time.LocalDateTime;

public class Daemon {
    private static Appointments[] appointmentsArray = new Appointments[100];
    private static int appointmentsArrayCounter = 0;

    public static void main(String[] args) {

        Appointments appointment1 = new Appointments("001",
                LocalDateTime.of(2026,3,14, 14,30,00),
                "Peter Park", "Fitting braces");

        addAppointment(appointment1);


        Appointments appointment2 = new Appointments("002",
                LocalDateTime.of(2026,3,15, 9,00,00),
                "Thato Piri", "Root Canal");

        addAppointment(appointment2);

        searchArrayByName("thato");
    }

    public static void addAppointment(Appointments newAppointment){

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
