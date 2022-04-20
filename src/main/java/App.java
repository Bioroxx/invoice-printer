import java.util.LinkedList;

public class App
{
    public static void main(String[] args)
    {
        InvoicePrinter printer = new InvoicePrinter();

        LinkedList<Ticket> tickets = new LinkedList<>();

        tickets.add(new Ticket("THE ROLLING STONES - SIXTY Sektor A (Sitzplatz)", 39.95));
        tickets.add(new Ticket("THE ROLLING STONES - SIXTY Sektor A (Sitzplatz)", 39.95));
        tickets.add(new Ticket("THE ROLLING STONES - SIXTY Sektor B (Stehplatz)", 19.95));

        Order order = new Order(tickets);

        printer.print(order);
    }
}
