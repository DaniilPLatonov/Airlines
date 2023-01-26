package FlightMngr;

import java.io.*;
import java.util.*;

class Flight implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String date;
    private String timeLiftoff;
    private String placeOut;
    private String placeIn;
    private float cost;
    private float flightLength;
    private int freeSeats;

    public Flight(String _date, String _timeLiftoff, String _placeOut, String _placeIn, float _cost, float _flightLength, int _freeSeats) {
        date = _date;
        timeLiftoff = _timeLiftoff;
        placeOut = _placeOut;
        placeIn = _placeIn;
        cost = _cost;
        flightLength = _flightLength;
        freeSeats = _freeSeats;
    }

    public String GetDate() {
        return date;
    }

    public String GetOut() {
        return placeOut;
    }

    public String GetIn() {
        return placeIn;
    }

    public String GetLift() {
        return timeLiftoff;
    }

    public float GetCost() {
        return cost;
    }

    public float GetLength() {
        return flightLength;
    }

    public int GetSeats() {
        return freeSeats;
    }

    void writeToFile(String fileName) {
        try (ObjectOutputStream write = new ObjectOutputStream(new FileOutputStream(fileName, true))) {
            write.writeObject(this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Best flight:" + "\ndate - " + date + "\nliftoff time - " + timeLiftoff + "\nDeparture from - " + placeOut + "\nArrival to - " + placeIn + "\nTime in the air - " + flightLength + "\nAmount of free seats - " + freeSeats + "\nCost - " + cost;
    }
}

class ExceptionNoFlights extends Exception {
    public ExceptionNoFlights() {
        super("Exception: There are no flights that meet your request!");
    }
}

class Search {
    String placeOut;
    String placeIn;
    String date;
    String sortby;
    String change;

    LinkedList<Flight> readFromFile(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        LinkedList<Flight> flightlist = new LinkedList<>();
        while (fis.available() > 0) {
            ObjectInputStream read = new ObjectInputStream(fis);
            flightlist.add((Flight) read.readObject());
        }
        fis.close();
        return flightlist;
    }

    Flight EyeOfGod() throws IOException, ClassNotFoundException, ExceptionNoFlights {
        LinkedList<Flight> flightlist = readFromFile("./db.txt");
        LinkedList<Flight> Once = new LinkedList<>();
        LinkedList<LinkedList<Flight>> Double = new LinkedList<>();
        Double.add(new LinkedList<>());
        Double.add(new LinkedList<>());
        LinkedList<Flight> Compare;
        for (Flight flight : flightlist) {
            if (Objects.equals(flight.GetDate(), this.date) & Objects.equals(flight.GetOut(), this.placeOut)
                    & Objects.equals(flight.GetIn(), this.placeIn)) {
                Once.add(flight);
            } else if (Objects.equals(flight.GetDate(), this.date) & Objects.equals(flight.GetOut(), this.placeOut) & !Objects.equals(flight.GetIn(), this.placeIn)) {
                Double.get(0).add(flight);
            } else if (Objects.equals(flight.GetDate(), this.date) & Objects.equals(flight.GetIn(), this.placeIn) & !Objects.equals(flight.GetOut(), this.placeOut)) {
                Double.get(1).add(flight);
            }
        }
        if ((Once.size() == 0 & Objects.equals(this.change, "N")) || ((Double.get(0).size() == 0 || Double.get(1).size() == 0) & Once.size() == 0 & Objects.equals(this.change, "Y"))) {
            throw new ExceptionNoFlights();
        }
        LinkedList<Flight> Twice = new LinkedList<>();
        if (Objects.equals(this.change, "Y")) {
            for (int j = 0; j < Double.get(0).size(); j++) {
                for (int k = 0; k < Double.get(1).size(); k++) {
                    if (Objects.equals(Double.get(0).get(j).GetIn(), Double.get(1).get(k).GetOut()) /*& Double.get(0).get(j).GetLift()+Double.get(0).get(j).GetLength()<Double.get(1).get(k).GetLift()*/)
                        Twice.add(new Flight(this.date, Double.get(0).get(j).GetLift(), Double.get(1).get(j).GetOut(), Double.get(1).get(k).GetIn(), Double.get(1).get(k).GetCost() + Double.get(0).get(j).GetCost(), Double.get(0).get(j).GetLength() + Double.get(1).get(k).GetLength(), (Double.get(1).get(k).GetSeats() + Double.get(0).get(j).GetSeats()) / 2));
                }
            }
            Compare = Twice;
            Compare.addAll(Once);
        } else {
            Compare = Once;
        }
        switch (sortby) {
            case "1" -> {
                Compare.sort(Comparator.comparingDouble(Flight::GetSeats));
                return Compare.getLast();
            }
            case "2" -> Compare.sort(Comparator.comparingDouble(Flight::GetLength));
            case "3" -> Compare.sort(Comparator.comparingDouble(Flight::GetCost));
            case "4" -> {
                if (Objects.equals(this.change, "N")) {
                    System.out.println("To work with this option properly enter Y on the previous step");
                    return Compare.get(0);
                }
            }
        }
        return Compare.getFirst();
    }
}

public class Main {

    public static void main(String[] args) {
        int answr;
        do {
            Scanner console = new Scanner(System.in);
            System.out.println("You are\nAdmin User");
            switch (console.nextLine()) {
                case "Admin" -> {
                    System.out.println("Enter a new flight: " +
                            "departure date (DD.MM.YY)," +
                            "liftoff time (HH:MM)," +
                            "place out (e.g. Moscow, Russia)," +
                            "place in (e.g. Moscow, Russia)," +
                            "cost (in rub)," +
                            "flight length (in hours)," +
                            "free seats");
                    try {
                        Flight tmp = new Flight(console.nextLine(),
                                console.nextLine(), console.nextLine(), console.nextLine(), console.nextInt(), console.nextInt(), console.nextInt());
                        tmp.writeToFile("./db.txt");
                    } catch (InputMismatchException i) {
                        System.out.println("Input mismatch");
                        console.skip(".*\\n");
                    }
                }
                case "User" -> {
                    Search srch = new Search();
                    System.out.println("From: (e.g. Moscow, Russia)");
                    srch.placeOut = console.nextLine();
                    System.out.println("To: (e.g. Moscow, Russia)");
                    srch.placeIn = console.nextLine();
                    System.out.println("Departure date: (DD.MM.YY)");
                    srch.date = console.nextLine();
                    System.out.println("Include changing flights? (Y|N)");
                    srch.change = console.nextLine();
                    while (!Objects.equals(srch.change, "Y") & !Objects.equals(srch.change, "N")) {
                        System.out.println("Exception: The char is not on the list!");
                        System.out.println("Include changing flights? (Y|N)");
                        srch.change = console.nextLine();
                    }
                    System.out.print("""
                            Choose by:
                            1 - Most free seats
                            2 - Shortest flight duration
                            3 - Lowest cost
                            4 - Least wait time between flight changes
                            """);
                    srch.sortby = console.nextLine();
                    if (!Objects.equals(srch.sortby, "1") & !Objects.equals(srch.sortby, "2") & !Objects.equals(srch.sortby, "3") & !Objects.equals(srch.sortby, "4")) {
                        System.out.println("Exception: The char is not on the list! Default used: 3 - Lowest cost");
                        srch.sortby = "3";
                    }
                    try {
                        System.out.println(srch.EyeOfGod().toString());
                    } catch (ExceptionNoFlights f) {
                        System.out.println(f.getMessage());
                    } catch (IOException io) {
                        System.out.println("Most likely, there is no DB to work with");
                    } catch (ClassNotFoundException c) {
                        System.out.println("Pls reley the logs to our support group at support@mephi.ru");
                    }
                }
                default -> System.out.println("Enter either Admin or User");
            }
            do {
                try {
                    System.out.println("Exit?\n1 - Yes 2 - No");
                    answr = console.nextInt();
                } catch (InputMismatchException a) {
                    System.out.println("Exception: The number is not on the list!");
                    answr = 0;
                    console.skip(".*\\n");
                }
            } while (answr != 1 & answr != 2);
        } while (answr == 2);
    }
}


