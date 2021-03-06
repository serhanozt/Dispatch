
public class Vehicle {
    static int idGenerator = 0;
    private int id;
    private Position position = null;
    private Position destination = null;
    private int idleTime = 0;

    public Vehicle(Position position) {
        this.id = idGenerator;
        idGenerator += 1;
        this.position = position;
    }
    public int getId() {
        return this.id;
    }

    public Position getPosition() {
        return this.position;
    }

    public void updatePosition(Position position) {
        this.position = position;
    }

    public Position getDest() {
        return this.destination;
    }

    public void updateDest(Position destination) {
        if(this.destination == null) {
            this.destination = new Position(destination.row, destination.col);
        } else {
            this.destination.update(destination.row, destination.col);
        }
    }

    public void incrementIdleTime() {
        this.idleTime += 1;
    }

    public int getIdleTime() {
        return this.idleTime;
    }

    public static void refreshIdGenerator() {
        idGenerator = 0;
    }

    public String toString() {
        return "Vehicle id: " + this.id + ". Position: " + this.position + ". Destination: " + this.destination;
    }
}
