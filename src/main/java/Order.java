import java.util.List;

public class Order
{
    private List<Ticket> tickets;

    public Order(List<Ticket> tickets)
    {
        this.tickets = tickets;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
