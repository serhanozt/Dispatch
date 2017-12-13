
public class User {
    static int idGenerator = 0;
    private int id;
    private Position[] request;
    public User(UserRequester userRequester) {
        this.id = idGenerator;
        idGenerator += 1;
        request = userRequester.generatePosition();
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
    public static void refreshIdGenerator() {
        idGenerator = 0;
    }
    public String toString() {
        return "User id: " + this.id + ". Pick up: " + this.getSrc() + ". Dest: " + this.getDest();
    }
}
