/*
Author: David Solan
Date: October 13, 2019
Class: CMSC 335
Purpose: Imports a file with port, ship, and crew information. Program parses
the information into appropriate data structures, re-arranges and displays.
Search can be performed to find corresponding inputs.
Version 4;Ships will now dock and wait for resources to become available
 */
package cmscprojectthreevertwo;

//library imports
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;

public class SeaPortProgram extends JFrame {

    //class fields
    private static final long serialVersionUID = 123L;
    //main window elements
    private JLabel inputLabel = new JLabel("Select File and Que Sorting");
    private JButton inputBtn = new JButton("Import File and Sort");
    private JButton searchBtn = new JButton("Search");
    private JTextArea output = new JTextArea(10, 20);
    private JScrollPane listedOutput = new JScrollPane(output);
    private JTextField searchInput = new JTextField();
    private JLabel searchLabel = new JLabel("Enter Search Term:");
    private JTextArea searchOutput = new JTextArea(10, 10);
    private JScrollPane scrollSearchOut = new JScrollPane(searchOutput);

    private JFileChooser chooser = new JFileChooser(".");

    private JLabel radioLabel = new JLabel("Enter Index of Search");
    private JRadioButton portSearch = new JRadioButton("Port");
    private JRadioButton dockSearch = new JRadioButton("Dock");
    private JRadioButton shipSearch = new JRadioButton("Ship");
    private JRadioButton personSearch = new JRadioButton("Person");
    private ButtonGroup searchGroup = new ButtonGroup();

    private JRadioButton sortShipName = new JRadioButton("Ship Name");
    private JRadioButton sortQueWeight = new JRadioButton("Ship Weight");
    private JRadioButton sortQueWidth = new JRadioButton("Ship Width");
    private JRadioButton sortQueLength = new JRadioButton("Ship Length");
    private JRadioButton sortQueDraft = new JRadioButton("Ship Draft");
    private ButtonGroup sortGroup = new ButtonGroup();

    private World theWorld;

    private HashMap<Integer, Thing> elementHash = new HashMap<>();

    private JPanel mainPanel = new JPanel(new GridBagLayout());
    //Job Frame
    private JFrame jobFrame = new JFrame("Running Jobs");
    private JPanel jobPanel = new JPanel(new GridLayout(0, 5, 2, 5));
    private JScrollPane jobScroller = new JScrollPane(jobPanel);
    //JTree instatiation
    private DefaultMutableTreeNode worldRoot = new DefaultMutableTreeNode("The World");
    private JTree elementTree = new JTree(worldRoot);
    private JScrollPane treePane = new JScrollPane(elementTree);

    //Single arg constructor containing GUI
    public SeaPortProgram(String str) {
        //<editor-fold desc="GUI">
        super(str);
        setFrame(575, 950);
        setLayout(new FlowLayout());
        output.setEditable(false);
        searchOutput.setEditable(false);

        //Create Job panel
        jobFrame.setSize(1500, 500);
        jobFrame.setLocationRelativeTo(null);
        jobFrame.add(jobScroller);

        chooser.setDialogTitle("Choose Input File");

        sortShipName.setSelected(true);
        sortGroup.add(sortShipName);
        sortGroup.add(sortQueWeight);
        sortGroup.add(sortQueWidth);
        sortGroup.add(sortQueLength);
        sortGroup.add(sortQueDraft);

        searchGroup.add(portSearch);
        searchGroup.add(dockSearch);
        searchGroup.add(shipSearch);
        searchGroup.add(personSearch);
        portSearch.setSelected(true);

        JPanel sortRadioPanel = new JPanel(new FlowLayout());
        JPanel searchRadioPanel = new JPanel(new FlowLayout());

        //Adding components to main panel
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);

        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(10, 10, 10, 10);

        //First row
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        inputLabel.setLabelFor(inputBtn);
        mainPanel.add(inputLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        mainPanel.add(inputBtn, c);

        //Second Row
        sortRadioPanel.add(sortShipName);
        sortRadioPanel.add(sortQueWeight);
        sortRadioPanel.add(sortQueWidth);
        sortRadioPanel.add(sortQueLength);
        sortRadioPanel.add(sortQueDraft);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        mainPanel.add(sortRadioPanel, c);

        //Third Row
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        mainPanel.add(listedOutput, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        elementTree.setSize(50, 50);
        treePane.setSize(50, 50);
        mainPanel.add(treePane, c);

        searchRadioPanel.add(portSearch, c);
        searchRadioPanel.add(shipSearch, c);
        searchRadioPanel.add(dockSearch, c);
        searchRadioPanel.add(personSearch, c);

        //Fourth row
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        mainPanel.add(radioLabel, c);

        //Fifth row
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        mainPanel.add(searchRadioPanel, c);

        //Sixth row
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 1;
        mainPanel.add(searchLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 5;
        c.gridwidth = 1;
        mainPanel.add(searchInput, c);

        //Seventh row
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 2;
        searchBtn.setEnabled(false);
        mainPanel.add(searchBtn, c);

        //Eighth row
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 2;
        mainPanel.add(scrollSearchOut, c);

        add(mainPanel);

        //</editor-fold>
        //input button listener takes file and parses it to internal structures
        inputBtn.addActionListener(e -> {
            jobFrame.setVisible(true);
            Scanner scn = null;
            try {
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    scn = new Scanner(file);
                    //System.out.println("Scanner created");
                    theWorld = new World(scn, elementHash, mainPanel, jobPanel);

                    createNodes(theWorld, worldRoot);
//                    System.out.println("all nodes added");

//<editor-fold desc="sorting">
                    //After scanning the input into the proper classes
                    //sorting the lists can begin
                    //Example collections lambda sourced from
                    //https://www.mkyong.com/java8/java-8-lambda-comparator-example/
                    //for each port in the world
                    if (sortShipName.isSelected()) {
//                        System.out.println("sorting by ship");
                        theWorld.getPorts().forEach((tempPorts) -> {
                            Collections.sort(tempPorts.getPortQue(),
                                    (Ship s1, Ship s2) -> s1.getName().compareTo(s2.getName()));
//                            System.out.println("sorted");
                        });

                        //Testing
//                        for (SeaPort temp : theWorld.getPorts()) {
//                            for (Ship tempShip : temp.getPortQue()) {
//                                System.out.println(tempShip.getName());
//                            }
//                        }
                    } else if (sortQueWeight.isSelected()) {
                        //for each port in the world
                        theWorld.getPorts().forEach((tempPort) -> {
                            Collections.sort(tempPort.getPortQue(),
                                    (Ship s1, Ship s2) -> (int) s1.getWeight() - (int) s2.getWeight());
                        });
                    } else if (sortQueWidth.isSelected()) {
                        theWorld.getPorts().forEach((tempPort) -> {
                            Collections.sort(tempPort.getPortQue(),
                                    (Ship s1, Ship s2) -> (int) s1.getWidth() - (int) s2.getWidth());
                        });
                    } else if (sortQueLength.isSelected()) {
                        theWorld.getPorts().forEach((tempPort) -> {
                            Collections.sort(tempPort.getPortQue(),
                                    (Ship s1, Ship s2) -> (int) s1.getLength() - (int) s2.getLength());
                        });
                    } else if (sortQueDraft.isSelected()) {
                        theWorld.getPorts().forEach((tempPort) -> {
                            Collections.sort(tempPort.getPortQue(),
                                    (Ship s1, Ship s2) -> (int) s1.getDraft() - (int) s2.getDraft());
                        });
                    }
//</editor-fold>
                    //System.out.println(theWorld.toString());
                    output.setText(theWorld.toString());
                }

                //make the output searchable by item index
                searchBtn.setEnabled(true);
                searchBtn.addActionListener(k -> {
                    String tempString = "Search failed. Please check buttons and input"
                            + " then try again";
                    try {
                        int searchTerm = Integer.parseInt(searchInput.getText());

                        if (shipSearch.isSelected()) {
                            //System.out.println(shipHash.get(searchTerm).toString());
                            if (elementHash.get(searchTerm) != null) {
                                tempString = (elementHash.get(searchTerm).toString());
                            }
                        } else if (portSearch.isSelected()) {
                            if (elementHash.get(searchTerm) != null) {
                                tempString = (elementHash.get(searchTerm).toString());
                            }
                        } else if (personSearch.isSelected()) {
                            if (elementHash.get(searchTerm) != null) {
                                tempString = (elementHash.get(searchTerm).toString());
                            }
                        } else if (dockSearch.isSelected()) {
                            if (elementHash.get(searchTerm) != null) {
                                tempString = (elementHash.get(searchTerm).toString());
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Search term was not found\n"
                                    + "Please try again", "Error",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                        searchOutput.setText(tempString);
                    } catch (NumberFormatException ex) {
                        searchOutput.setText(tempString);
                    }
                }// end of search lambda
                );// end of search button listener       
            } catch (FileNotFoundException io) {
                System.out.println("File not found " + io.getMessage());
            } finally {
                try {
                    scn.close();
                } catch (Exception x) {
                    System.out.println("error closing scanner");
                }
            }
        }// end of input lamda
        );// end of input button action listener

        validate();
    }//End of constructor

    //Frame object default setter method
    public void setFrame(int width, int height) {
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //Change frame visibility
    public void display() {
        setVisible(true);
    }

    //Adding nodes to the JTree
    public void createNodes(World world, DefaultMutableTreeNode worldRoot) {
        DefaultMutableTreeNode portTopNode = new DefaultMutableTreeNode("Ports");
        worldRoot.add(portTopNode);//ports folder in world
        for (SeaPort port : world.getPorts()) {
            DefaultMutableTreeNode personTopNode = new DefaultMutableTreeNode("Persons");
            DefaultMutableTreeNode shipTopNode = new DefaultMutableTreeNode("Ships");
            //Port folder
            DefaultMutableTreeNode portNode = new DefaultMutableTreeNode(port.getName());
            portTopNode.add(portNode);//port folder in world
            portNode.add(personTopNode);//person folder in port
            portNode.add(shipTopNode);//Ship folder in port
            for (Person person : port.getPersons()) {
                personTopNode.add(new DefaultMutableTreeNode(person.getName()));
            }
            for (Ship ship : port.getPortShips()) {
                DefaultMutableTreeNode shipNode = new DefaultMutableTreeNode(ship.getName());
                DefaultMutableTreeNode jobTopNode = new DefaultMutableTreeNode("Jobs");
                shipTopNode.add(shipNode);//ship node in ship folder
                shipNode.add(jobTopNode);// job folder in ship node
                for (Job job : ship.getJobs()) {
                    DefaultMutableTreeNode jobNode = new DefaultMutableTreeNode(job.getName());
                    jobTopNode.add(jobNode);
                }
            }
        }
    }

    //getter methods
    public HashMap<Integer, Thing> getMap() {
        return elementHash;
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public static void main(String[] args) {
        SeaPortProgram app = new SeaPortProgram("Sea Port Program");
        app.display();
    }//End main method
}// End SeaPortProgram class

//<editor-fold desc="Thing">
class Thing implements Comparable<Thing> {

    //class fields
    private String name;
    private int index;
    private int parent;

    //single arg constructor
    public Thing(Scanner sc, HashMap<Integer, Thing> elementHash) {
        if (sc.hasNext()) {
            sc.next();//drop category
        }
        if (sc.hasNext()) {
            name = sc.next();
        }
        if (sc.hasNextInt()) {
            index = sc.nextInt();
        }
        if (sc.hasNextInt()) {
            parent = sc.nextInt();
        }
        elementHash.put(index, this);
    }

    @Override
    public String toString() {
        return name + " " + index;
    }

    @Override
    public int compareTo(Thing thingOne) {
        return 0;
    }

    //getter methods
    public int getIndex() {
        return index;
    }

    public int getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    //setter methods
    public void setIndex(int index) {
        this.index = index;
    }

    public synchronized void setParent(int parent) {
        this.parent = parent;
    }

    public void setName(String name) {
        this.name = name;
    }
}// End thing class
//</editor-fold>

//<editor-fold desc="WorldClass">
class World extends Thing {

    //class fields
    private ArrayList<SeaPort> ports = new ArrayList<>();
    private PortTime time;

    //single arg constructor
    public World(Scanner sc, HashMap<Integer, Thing> elementHash, JPanel mainPanel, JPanel jobPanel) {
        super(sc, elementHash);
//        System.out.println("starting world constructor");
        String inline;
        Scanner newLine;
        while (sc.hasNext()) {
            inline = sc.nextLine().trim();
            if (inline.length() == 0) {
                continue;
            }
            newLine = new Scanner(inline);
//            String line = sc.next();
            if (inline.startsWith("/")) {
//                System.out.println("line skipped");
                sc.nextLine();
            } else {
//                System.out.println("Switching " + inline);
                switch (newLine.next()) {
                    case "port":
//                        System.out.println("making a port");
                        addPort(new Scanner(inline), elementHash);
                        break;
                    case "dock":
//                        System.out.println("making a dock");
                        addDock(new Scanner(inline), elementHash);
                        break;
                    case "pship":
//                        System.out.println("making a pship");
                        addPship(new Scanner(inline), elementHash);
                        break;
                    case "cship":
//                        System.out.println("making a cship");
                        addCship(new Scanner(inline), elementHash);
                        break;
                    case "person":
//                        System.out.println("making a person");
                        addPerson(new Scanner(inline), elementHash);
                        break;
                    case "job":
//                        System.out.println("making a job");
                        addJob(new Scanner(inline), elementHash, jobPanel);
                        break;
                    default:
//                        System.out.println("no match found");
                        //Insert code and message for invalid data type
                        break;
                }
            }
        }
    }//end of World constructor

    //Methods to create new class objects
    public void addPort(Scanner sc, HashMap<Integer, Thing> elementHash) {
        //System.out.println(temp.toString());
        ports.add(new SeaPort(sc, elementHash));
    }

    public void addDock(Scanner sc, HashMap<Integer, Thing> elementHash) {
        Dock tempDock = new Dock(sc, elementHash);
        //if index of the new dock matches port index, add to port
        for (SeaPort temp : ports) {
            if (temp.getIndex() == tempDock.getParent()) {
                temp.addDockToPort(tempDock);
            }
        }
    }

    public void addPship(Scanner sc, HashMap<Integer, Thing> elementHash) {
        PassengerShip tempShip = new PassengerShip(sc, elementHash);
        for (SeaPort tempPort : ports) {
            //If new ship parent equals port, add to port que and ships
            if (tempShip.getParent() == tempPort.getIndex()) {
                tempPort.addShipToQue(tempShip);
                tempPort.addShipToPort(tempShip);
            } else {
                //if new ship parent equals dock, add to dock and ships
                ArrayList<Dock> tempList = tempPort.getPortDock();
                for (Dock dck : tempList) {
                    if (dck.getIndex() == tempShip.getParent()) {
                        dck.setDockShip(tempShip);
                        tempPort.addShipToPort(tempShip);
                    }
                }

            }
        }
    }

    public void addCship(Scanner sc, HashMap<Integer, Thing> elementHash) {
        CargoShip tempShip = new CargoShip(sc, elementHash);
        for (SeaPort temp : ports) {
            //If new ship parent equals port, add to port que and ships
            if (tempShip.getParent() == temp.getIndex()) {
                temp.addShipToQue(tempShip);
                temp.addShipToPort(tempShip);
            } else {
                //if new ship parent equals dock, add to dock and ships
                for (Dock dck : temp.getPortDock()) {
                    if (dck.getIndex() == tempShip.getParent()) {
                        dck.setDockShip(tempShip);
                        temp.addShipToPort(tempShip);
                    }
                }
            }
        }
    }

    public void addPerson(Scanner sc, HashMap<Integer, Thing> elementHash) {
        Person tempPerson = new Person(sc, elementHash);
        //if new person parent  matches port, add to port
        for (SeaPort temp : ports) {
            if (tempPerson.getParent() == temp.getIndex()) {
                temp.addPerson(tempPerson);
            }
        }
    }

    public void addJob(Scanner sc, HashMap<Integer, Thing> elementHash, JPanel jobPanel) {
        Job tempJob = new Job(sc, elementHash, jobPanel);
        //if new port parent matches port index, add to port
        for (SeaPort port : ports) {
            for (Ship ship : port.getPortShips()) {
                if (tempJob.getParent() == ship.getIndex()) {
                    ship.addJobToShip(tempJob);
                }
            }
        }
    }

    //getter method
    public ArrayList<SeaPort> getPorts() {
        return ports;
    }

    public PortTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        String output = ">>>>> The world:";
        for (SeaPort prt : ports) {
            output += prt.toString();
        }
        //System.out.println(output);
        return output;
    }
}// end world class
//</editor-fold>

//<editor-fold desc="SeaPort">
class SeaPort extends Thing {

    //class fields
    private ArrayList<Dock> docks = new ArrayList<>();
    private ArrayList<Ship> que = new ArrayList<>();
    private ArrayList<Ship> ships = new ArrayList<>();
    private ArrayList<Person> persons = new ArrayList<>();

    private ArrayList<Person> busyWorkers = new ArrayList<>();

    //single arg constructor
    public SeaPort(Scanner sc, HashMap<Integer, Thing> elementHash) {
        super(sc, elementHash);
        //System.out.println("making seaport");
    }

    //methods to add objects to class arraylists
    public synchronized void addToBusyWorkers(Person person) {
        busyWorkers.add(person);
    }

    public synchronized void removeFromBusyWorkers(Person person) {
        busyWorkers.remove(person);
    }

    public void addShipToQue(Ship s) {
        que.add(s);
    }

    public void addDockToPort(Dock d) {
        docks.add(d);
    }

    public void addShipToPort(Ship s) {
        ships.add(s);
    }

    public synchronized void addPerson(Person p) {
        persons.add(p);
    }

    //getter methods
    public synchronized ArrayList<Person> getBusyWorkers() {
        return busyWorkers;
    }

    public synchronized Ship nextShip() {
        return que.remove(0);
    }

    public ArrayList<Dock> getPortDock() {
        return docks;
    }

    public ArrayList<Ship> getPortQue() {
        return que;
    }

    public ArrayList<Ship> getPortShips() {
        return ships;
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }

    public Person removePerson(Person worker) {
        return persons.remove(persons.indexOf(worker));
    }

    @Override
    public String toString() {
        String output = "\n\nSeaPort: " + super.toString();
        for (Dock dck : docks) {
            output += "\n" + dck.toString();
        };
        output += "\n\n---List of all ships in que:";
        for (Ship shp : que) {
            output += "\n>" + shp.toString();
        }
        output += "\n\n---List of all ships:";
        for (Ship shp : ships) {
            output += "\n>" + shp.toString();
        }
        output += "\n\n--List of all persons:\n";
        for (Person prs : persons) {
            output += "\n>Person: " + prs.toString();
        }
        return output;
    }
}// end seaport class
//</editor-fold>

class Dock extends Thing {

    //class fields
    private Ship ship = null;

    //single arg constructor
    public Dock(Scanner sc, HashMap<Integer, Thing> elementHash) {
        super(sc, elementHash);
//        elementHash.put(this.getIndex(), this);
        //System.out.println("making dock");
    }

    //getter method
    public Dock getDock() {
        return this;
    }

    public Ship getDockShip() {
        return ship;
    }

    //setter method
    public synchronized void setDockShip(Ship s) {
        if (this.ship != null) {
            this.ship.setIsDocked(false);
            this.setParent(this.getParent());//set old ship parent as port
        }
        s.setParent(this.getIndex());
        s.setIsDocked(true);
        this.ship = s;
    }

    @Override
    public String toString() {
        String output = "Dock: " + super.toString();
        if (ship == null) {
            output += "\nShip: " + "There is no ship in dock";
        } else {
            output += "\nShip: " + ship.toString();
        }
        return output;
    }
}// end dock class

class Ship extends Thing {

    //class fields
    private PortTime arrivalTime, dockTime;
    private double weight, length, width, draft;
    private ArrayList<Job> jobs = new ArrayList<>();

    private boolean busyFlag = false;
    private AtomicBoolean isDocked = new AtomicBoolean(false);

    public Ship(Scanner sc, HashMap<Integer, Thing> elementHash) {
        super(sc, elementHash);
        //System.out.println("making a ship");
        if (sc.hasNextDouble()) {
            weight = sc.nextDouble();
        }
        if (sc.hasNextDouble()) {
            length = sc.nextDouble();
        }
        if (sc.hasNextDouble()) {
            width = sc.nextDouble();
        }
        if (sc.hasNextDouble()) {
            draft = sc.nextDouble();
        }
    }//end constructor

    //method to add to job arraylist
    public void addJobToShip(Job jb) {
        jobs.add(jb);
    }

    //setter methods
    public synchronized void setIsDocked(boolean tf) {
        this.isDocked.set(tf);
    }

    public void toggleBusyFlag() {
        busyFlag = !busyFlag;
    }

    public void setBusyFlag(boolean tf) {
        busyFlag = tf;
    }

    //getter method
    public synchronized boolean getIsDocked() {
        return isDocked.get();
    }

    public boolean getBusyFlag() {
        return busyFlag;
    }

    public PortTime getArrival() {
        return arrivalTime;
    }

    public PortTime getDockTime() {
        return dockTime;
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public double getWeight() {
        return weight;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public double getDraft() {
        return draft;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}// end ship class

class PassengerShip extends Ship {

    //class fields
    private int numberOfPassengers, numberOfRooms, numberOfOccupiedRooms;

    //single arg contructor
    public PassengerShip(Scanner sc, HashMap<Integer, Thing> elementHash) {
        super(sc, elementHash);
        if (sc.hasNextInt()) {
            numberOfPassengers = sc.nextInt();
        }
        if (sc.hasNextInt()) {
            numberOfRooms = sc.nextInt();
        }
        if (sc.hasNextInt()) {
            numberOfOccupiedRooms = sc.nextInt();
        }
    }//end constructor

    @Override
    public String toString() {
        String output = "Passenger ship: " + super.toString();
        return output;
    }
}// end passengership class

class CargoShip extends Ship {

    //class fields
    private double cargoValue, cargoVolume, cargoWeight;

    //single arg contructor
    public CargoShip(Scanner sc, HashMap<Integer, Thing> elementHash) {
        super(sc, elementHash);
        if (sc.hasNextDouble()) {
            cargoWeight = sc.nextDouble();
        }
        if (sc.hasNextDouble()) {
            cargoVolume = sc.nextDouble();
        }
        if (sc.hasNextDouble()) {
            cargoValue = sc.nextDouble();
        }
    }// end constructor

    @Override
    public String toString() {
        String output = "Cargo ship: " + super.toString();
        return output;
    }
}// end cargoship class

final class Person extends Thing {

    //class fields
    private String skill;

    //single arg contructor
    public Person(Scanner sc, HashMap<Integer, Thing> elementHash) {
        super(sc, elementHash);
        //System.out.println("making a person");
        if (sc.hasNext()) {
            skill = sc.next();
        }
    }//end constructor

    //setter
    public void setSkill(String skill) {
        this.skill = skill;
    }

    //getter
    public String getSkill() {
        return skill;
    }

    @Override
    public String toString() {
        return super.toString() + " " + skill;
    }
}// end person class

class PortTime {

    //class fields
    private int time;

    //no arg constructor
    public PortTime() {
    }

    //single arg constructor
    public PortTime(int time) {
        this.time = time;
    }
}// end porttime class

class Job extends Thing implements Runnable {

    //class fields
    //index, parent index, and name in super constructor
    private double duration;
    private ArrayList<String> requirements = new ArrayList<>();

    private JPanel parent = null;
    private Ship ship = null;
    private Dock dock = null;
    private final SeaPort PORT;
    private JProgressBar pm = new JProgressBar();
    private boolean goFlag = true;
    private boolean noKillFlag = true;
    private JButton jbGo = new JButton("Stop");
    private JButton jbKill = new JButton("Cancel");
    private boolean isComplete = false;

    private CopyOnWriteArrayList<Person> workers = new CopyOnWriteArrayList<>();

    private enum Status {
        RUNNING, SUSPENDED, WAITING, DONE
    };
    private Status status = Status.SUSPENDED;

    //single arg constructor
    public Job(Scanner sc, HashMap<Integer, Thing> elementHash, JPanel cv) {
        super(sc, elementHash);
        parent = cv;
        //System.out.println("job started");
        if (sc.hasNextDouble()) {
            duration = sc.nextDouble();
        }
        while (sc.hasNext()) {
            requirements.add(sc.next());
        }
        ship = (Ship) elementHash.get(this.getParent());
        if (ship.getIsDocked()) {
            dock = (Dock) elementHash.get(ship.getParent());
            PORT = (SeaPort) elementHash.get(dock.getParent());

        } else {
            PORT = (SeaPort) elementHash.get(ship.getParent());
        }
        pm = new JProgressBar();
        pm.setStringPainted(true);

        parent.add(pm);
        parent.add(new JLabel("Ship " + ship.getName(),
                SwingConstants.CENTER));
        parent.add(new JLabel(this.getName().toString(), SwingConstants.CENTER));
        parent.add(jbGo);
        parent.add(jbKill);

        jbGo.addActionListener(e -> {
            toggleGoFlag();
        });
        jbKill.addActionListener(e -> {
            setKillFlag();
        });
        new Thread(this, this.getName()).start();//Job passed to thread constructer and started
    }//end constructor

    public void toggleGoFlag() {
        goFlag = !goFlag;
    }

    public void setKillFlag() {
        noKillFlag = false;
        jbKill.setBackground(Color.red);
    }

    void showStatus(Status st) {
        status = st;
        switch (status) {
            case WAITING:
                jbGo.setBackground(Color.yellow);
                jbGo.setText("Waiting");
                break;
            case RUNNING:
                jbGo.setBackground(Color.green);
                jbGo.setText("Running");
                break;
            case SUSPENDED:
                jbGo.setBackground(Color.red);
                jbGo.setText("Suspended");
                break;
            case DONE:
                jbGo.setBackground(Color.red);
                jbGo.setText("Done");
                break;
            default:
                break;
        }//end switch
    }// end showStatus

    public synchronized boolean hasPeople() throws LackOfSkillException {
        boolean output = true;
        if (this.requirements.isEmpty()) {
            output = true;//If there is no requirements then hasPeople is true
        } else {

            for (String requirement : this.getRequirements()) {
                requirement = requirement.toLowerCase().trim();
                ArrayList<String> tempArrPeople = new ArrayList<>();
                ArrayList<String> tempArrBusy = new ArrayList<>();
                for (Person person : PORT.getPersons()) {
                    tempArrPeople.add(person.getSkill().toLowerCase().trim());
                }
                for (Person person : PORT.getBusyWorkers()) {
                    tempArrBusy.add(person.getSkill().toLowerCase().trim());
                }
                if (tempArrBusy.contains(requirement)) {
                }
                if (!tempArrPeople.contains(requirement)
                        && !tempArrBusy.contains(requirement)) {
                    throw new LackOfSkillException(requirement + " not at port");
                } else if (!tempArrPeople.contains(requirement)) {
                    output = false;
                }
            }
        }
        return output;
    }// end of has people

    public synchronized void getPeople() {
        for (String requirement : requirements) {
            requirement = requirement.toLowerCase().trim();
            NextRequirement:
            for (Person person : PORT.getPersons()) {
                String skill = person.getSkill().toLowerCase().trim();
                if (skill.equals(requirement)) {
//                    System.out.println(ship.getName() + "taking worker " + person.getSkill());
                    PORT.addToBusyWorkers(person);
                    this.workers.add(PORT.removePerson(person));
                    //break to avoid removing more than one person per requirement
                    break NextRequirement;
                }
            }
        }
    }// end of getpeople

    //add workers back to port persons list and clear workers
    public synchronized void returnPeople() {
//        System.out.println(this.getName() + " returning workers " + this.workers.toString());
        for (Person worker : this.workers) {
//            System.out.println(ship.getName() + " returning " + worker.toString());
            PORT.removeFromBusyWorkers(worker);
            PORT.addPerson(worker);
            this.workers.remove(worker);
//            System.out.println(ship.getName() + "returning worker " + worker.getSkill() + " to " + port.getName());
        }
    }// end returnPeople

    public void setDock(Dock dock) {
        this.dock = dock;
    }

    //First sync checks for ship to be docked && port has required people    
    //Second sync is after he job is completed, if there are no more jobs;
    //remove ship from dock, unlock the dock and notify
    //iterate to next ship in queue and set to isDocked
    //repeat until queue is empty
    public void run() {
        //fields used for progress bar
        long time = System.currentTimeMillis();
        long startTime = time;
        long stopTime = time + 1000 * (long) duration;
        synchronized (PORT) {
            while ((!ship.getIsDocked())) {//while the ship is not docked
                showStatus(Status.WAITING);
                pm.setString("Waiting to Dock");
                try {
                    PORT.wait();//wait until notified that dock is available
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }//end try/catch
            }//end while waiting for ship to be free
            //try to get people for the job
            try {
                showStatus(Status.WAITING);
                while (!hasPeople()) {
                    pm.setString("Waiting for People");
                }
                this.getPeople();
            } catch (LackOfSkillException ex) {
                //Skills unavailable at port, job suspends and adds message to user
                this.isComplete = true;
                returnPeople();
                showStatus(Status.SUSPENDED);
                pm.setString(ex.getMessage());
                return;
            }

            pm.setString(null);
        }//end synchronized

        //Start job and show progress bar
        while (time < stopTime && noKillFlag) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            if (goFlag) {
                this.showStatus(Status.RUNNING);
                time += 100;
                pm.setValue((int) ((time - startTime) / (duration * 100)));
            } else {
                this.showStatus(Status.SUSPENDED);
            }
        }//end while

        //job is finished
        this.returnPeople();
        pm.setValue(100);
        this.isComplete = true;
        showStatus(Status.DONE);

        synchronized (PORT) {
            this.returnPeople(); // release people back to port
            ArrayList<Boolean> jobCheck = new ArrayList<>();
            ship.getJobs().forEach((job) -> {
                jobCheck.add(job.isComplete);
            });
            keepWorking:
            if (jobCheck.contains(false)) {//if there are jobs incomplete
                break keepWorking;// exit loop if there are more jobs on the ship
            } else {
                //if there are ships in que, next ship is docked
                if (!PORT.getPortQue().isEmpty()) {
                    Ship nextShip = PORT.nextShip();// remove next ship from port que
                    nextShip.setParent(dock.getIndex());//set the ship's parent to the dock
                    dock.setDockShip(nextShip);//set the ship to the dock
                    for (Job job : nextShip.getJobs()) {// set the job dock to the dock
                        job.setDock(dock);
                    }
                }
            }
            PORT.notifyAll();
        }
    }// end run method

    //getter method
    public ArrayList<String> getRequirements() {
        return requirements;
    }

    @Override
    public String toString() {
        String output = super.toString();
        for (String req : requirements) {
            if (req == null) {
                continue;
            }
            output += req;
        }
        return output;
    }
}// end job class

//custom exception class used to handle skills being unavailable
class LackOfSkillException extends Exception {

    private static final long serialVersionUID = 123L;

    public LackOfSkillException(String str) {
        super(str);
    }
}//end of LackOfSkillException
