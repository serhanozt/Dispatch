
public class User {
    static int idGenerator = 0;
    private int id;
    private Position[] request;
    public User(UserRequester userRequester) {
        this.id = idGenerator;
        idGenerator += 1;
        request = userRequester.generateRequest();
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
    public String toString() {
        return "User id: " + id + ". Pick up: " + this.getSrc() + ". Dest: " + this.getDest();
    }
}