package nl.saxion;

import nl.saxion.Models.*;
import nl.saxion.facade.PrintingFacade;
import nl.saxion.strategy.LessSpoolChangesStrategy;
import nl.saxion.strategy.OptimalSpoolUsageStrategy;

import java.util.*;

public class Main {
    Scanner scanner = new Scanner(System.in);
    private PrintingFacade printingFacade = new PrintingFacade();

    public static void main(String[] args) {
        new Main().run(args);
    }

    public void run(String[] args) {
        if (args.length > 0) {
            printingFacade.readPrintsFromFile(args[0]);
            printingFacade.readSpoolsFromFile(args[1]);
            printingFacade.readPrintersFromFile(args[2]);
        } else {
            printingFacade.readPrintsFromFile("src/main/resources/prints.json");
            printingFacade.readSpoolsFromFile("src/main/resources/spools.csv");
            printingFacade.readPrintersFromFile("src/main/resources/printers.json");
        }
        // Initialize the dashboard observer
        printingFacade.initializeDashboardObserver();

        int choice = 1;
        while (choice > 0 && choice < 11) {
            menu();
            choice = menuChoice(10);
            System.out.println("-----------------------------------");
            switch (choice) {
                case 1 -> addNewPrintTask();
                case 2 -> registerPrintCompletion();
                case 3 -> registerPrinterFailure();
                case 4 -> changePrintStrategy();
                case 5 -> {
                    System.out.println("---------- Starting Print Queue ----------");
                    printingFacade.startPrintQueue();
                    System.out.println("-----------------------------------");
                }
                case 6 -> showPrints();
                case 7 -> showPrinters();
                case 8 -> showSpools();
                case 9 -> showPendingPrintTasks();
                case 10 -> showDashboardStats();
            }
        }
        exit();
    }

    public void menu() {
        System.out.println("------------- Menu ----------------");
        System.out.println("- 1) Add new Print Task");
        System.out.println("- 2) Register Printer Completion");
        System.out.println("- 3) Register Printer Failure");
        System.out.println("- 4) Change printing style");
        System.out.println("- 5) Start Print Queue");
        System.out.println("- 6) Show prints");
        System.out.println("- 7) Show printers");
        System.out.println("- 8) Show spools");
        System.out.println("- 9) Show pending print tasks");
        System.out.println("- 10) Show dashboard stats of : Total number of times a spool is changed\n" +
                "- Total number of prints that are done");
        System.out.println("- 0) Exit");

    }

    private void exit() {

    }

    private void changePrintStrategy() {
        System.out.println("---------- Change Strategy -------------");
        System.out.println("- Current strategy: " + printingFacade.getPrintingStrategy().toString());
        System.out.println("- 1: Less Spool Changes");
        System.out.println("- 2: Efficient Spool Usage");
        System.out.println("- Choose strategy: ");
        int strategyChoice = numberInput(1, 2);
        if (strategyChoice == 1) {
            printingFacade.changePrintingStrategy(new LessSpoolChangesStrategy());
        } else if (strategyChoice == 2) {
            printingFacade.changePrintingStrategy(new OptimalSpoolUsageStrategy());
        }
        System.out.println("-----------------------------------");
    }

    private void registerPrintCompletion() {
        //Print running printers
        List<Printer> runningPrinters = printingFacade.printCurrentlyRunningPrinters();
        int numberOfRunningPrinters = runningPrinters.size();
        printRunningPrinters(runningPrinters);



        System.out.print("- Printer that is done (ID): ");
        int printerId = numberInput(1, printingFacade.getPrinters().size());

        printingFacade.registerSucceededPrinter(printerId);

        System.out.println("-----------------------------------");
    }

    private void printRunningPrinters(List<Printer> printers) {
        System.out.println("---------- Currently Running Printers ----------");
        for(Printer p: printers) {
            PrintTask printerCurrentTask= printingFacade.getCurrentTaskOfAPrinter(p);
            if(printerCurrentTask != null) {
                System.out.println("- " + p.getId() + ": " +p.getName() + " - " + printerCurrentTask);
            }
        }
    }

    private void registerPrinterFailure() {
        //Print running printers
        List<Printer> runningPrinters = printingFacade.printCurrentlyRunningPrinters();
        int numberOfRunningPrinters = runningPrinters.size();
        printRunningPrinters(runningPrinters);

        System.out.print("- Printer ID that failed: ");
        int printerId = numberInput(1, printingFacade.getPrinters().size());

        printingFacade.registerFailedPrinter(printerId);

        System.out.println("-----------------------------------");
    }

    private void addNewPrintTask() {
        //Selecting the print
        Print print = selectPrint();
        String printName = print.getName();

        //Select filament type
        FilamentType type = selectFilamentType();
        if (type == null) {
            return;
        }

        //Select available colors
        List<String> colors = selectColors(type, print);

        //Create the print task
        printingFacade.createPrintTask(printName, colors, type);
        System.out.println("----------------------------");
    }

    public void showPrints() {
        List<Print> prints = printingFacade.getPrints();
        System.out.println("---------- Available prints ----------");
        for (Print p : prints) {
            System.out.println(p);
        }
        System.out.println("--------------------------------------");
    }

    public void showPrintsNames() {
        List<Print> prints = printingFacade.getPrints();
        System.out.println("---------- Available prints ----------");
        System.out.println("(You can view details of prints with option 6 in the main menu)");
        int counter = 1;
        for (Print p : prints) {
            System.out.println(counter + ": " + p.getName());
            counter++;
        }
        System.out.println("--------------------------------------");
    }

    public void showSpools() {
        List<Spool> spools = printingFacade.getSpools();
        System.out.println("---------- Spools ----------");
        for (Spool spool : spools) {
            System.out.println(spool);
        }
        System.out.println("----------------------------");
    }

    public void showPrinters() {
        List<Printer> printers = printingFacade.getPrinters();
        System.out.println("--------- Available printers ---------");
        for (Printer p : printers) {
            String output = p.toString();
            PrintTask currentTask = printingFacade.getCurrentTaskOfAPrinter(p);
            if (currentTask != null) {
                output = output.replace("--------", "- Current Print Task: " + currentTask + System.lineSeparator() +
                        "--------");
            }
            System.out.println(output);
        }
        System.out.println("--------------------------------------");
    }

    public void showPendingPrintTasks() {
        List<PrintTask> printTasks = printingFacade.getPendingPrintTasks();
        System.out.println("--------- Pending Print Tasks ---------");
        for (PrintTask p : printTasks) {
            System.out.println(p);
        }
        System.out.println("--------------------------------------");
    }

    public List<FilamentType> showFilamentTypes() {
        List<FilamentType> filamentTypes = printingFacade.getFilamentTypes();
        System.out.println("---------- Filament Type ----------");
        int counter = 1;
        for (FilamentType filamentType : filamentTypes) {
            System.out.println("- " + counter + ": " + filamentType.name());
            counter++;
        }


        return filamentTypes;
    }

    public List<String> showAvailableColors(FilamentType filamentType) {
        List<String> availableColors = printingFacade.getAvailableColors(filamentType);
        System.out.println("---------- Colors ----------");
        for (int i = 1; i <= availableColors.size(); i++) {
            String colorString = availableColors.get(i - 1);
            System.out.println("- " + i + ": " + colorString + " (" + filamentType.name() + ")");
        }

        return availableColors;
    }

    private Print selectPrint() {
        List<Print> prints = printingFacade.getPrints();
        showPrintsNames();
        System.out.print("- Print number: ");
        int printNumber = numberInput(1, prints.size());
        System.out.println("--------------------------------------");
        return printingFacade.findSelectedPrint(printNumber);
    }

    private FilamentType selectFilamentType() {
        List<FilamentType> filamentTypes = showFilamentTypes();
        System.out.print("- Filament type number: ");
        int ftype = numberInput(1, filamentTypes.size());
        System.out.println("--------------------------------------");
        return printingFacade.getSelectedFilamentType(ftype, filamentTypes);
    }

    private List<String> selectColors(FilamentType type, Print print) {
        List<String> colors = new ArrayList<>();
        List<String> availableColors = showAvailableColors(type);
        System.out.print("- Color number: ");
        int colorChoice = numberInput(1, availableColors.size());
        colors.add(availableColors.get(colorChoice - 1));

        for (int i = 1; i < print.getFilamentLength().size(); i++) {
            System.out.print("- Color number: ");
            colorChoice = numberInput(1, availableColors.size());
            colors.add(availableColors.get(colorChoice - 1));
        }
        System.out.println("--------------------------------------");
        return colors;
    }

    private void showDashboardStats() {
        printingFacade.displayDashboardStats();
    }

    public int menuChoice(int max) {
        int choice = -1;
        while (choice < 0 || choice > max) {
            System.out.print("- Choose an option: ");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                //try again after consuming the current line
                System.out.println("- Error: Invalid input");
                scanner.nextLine();
            }
        }
        return choice;
    }

    public String stringInput() {
        String input = null;
        while (input == null || input.length() == 0) {
            input = scanner.nextLine();
        }
        return input;
    }

    public int numberInput() {
        int input = scanner.nextInt();
        return input;
    }

    public int numberInput(int min, int max) {
        int input = numberInput();
        while (input < min || input > max) {
            input = numberInput();
        }
        return input;
    }
}
