package t3waii.tasklists;

/**
 * Created by matti on 4/7/16.
 */
public class User {
    private String name;
    private Long id;

    public User(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() { return this.name; }
    public Long getId() { return this.id; }

    @Override
    public String toString() {
        return this.name;
    }
}
