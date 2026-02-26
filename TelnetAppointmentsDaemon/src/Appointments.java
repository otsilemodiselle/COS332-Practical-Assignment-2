import java.time.LocalDateTime;

public class Appointments {

    private int id;
    private LocalDateTime arrivalTime;
    private String visitorName;
    private String reason;
    private boolean isConfirmed;

    public Appointments(int id, LocalDateTime arrivalTime, String visitorName, String reason) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.visitorName = visitorName;
        this.reason = reason;
        this.isConfirmed = false;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public String getReason() {
        return reason;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    @Override
    public String toString() {
        return "Appointments{" +
                "id='" + id + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", visitorName='" + visitorName + '\'' +
                ", reason='" + reason + '\'' +
                ", isConfirmed=" + isConfirmed +
                '}';
    }
}
