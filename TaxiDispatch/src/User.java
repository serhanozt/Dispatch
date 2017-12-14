
public class User {
    static int idGenerator = 0;
    private int id;
    private Position[] request;
    private int waitToMatch = 0;
    private int waitToPickup = 0;
    private int tripTime = 0;

    public User(UserRequester userRequester) {
        this.id = idGenerator;
        idGenerator += 1;
        request = userRequester.generatePosition();
        tripTime = Position.dist(request[0], request[1]);
    }
    public Position getSrc() {
        return request[0];
    }

    public Position getDest() {
        return request[1];
    }
    public int getId() {
        return id;
    }

    public int getServeTime() {
        return waitToMatch + waitToPickup + tripTime;
    }

    public void incrementWaitToMatch() {
        waitToMatch += 1;
    }

    public int getWaitToMatch() {
        return waitToMatch;
    }

    public void setWaitToPickup(int waitToPickup) {
        this.waitToPickup = waitToPickup;
    }

    public int getWaitToPickup() {
        return waitToPickup;
    }

    public int getTripTime() {
        return tripTime;
    }

    public static void refreshIdGenerator() {
        idGenerator = 0;
    }

    public String toString() {
        return "User id: " + this.id + ". Pick up: " + this.getSrc() + ". Dest: " + this.getDest();
    }

}
