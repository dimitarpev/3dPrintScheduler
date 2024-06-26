package nl.saxion.Models;

import java.util.ArrayList;
import java.util.List;

/* Standard cartesian FDM printer */
public class StandardFDM extends Printer {
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private Spool currentSpool;
    private boolean isHoused;

    public StandardFDM(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
        super(id, printerName, manufacturer);
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        isHoused = false;
    }

    public void setCurrentSpools(List<Spool> spools) {
        this.currentSpool = spools.get(0);
    }

    public void setCurrentSpool(Spool spool) {
        if(this.currentSpool != spool) {
            this.currentSpool = spool;
            notifyObservers("spoolChange", spool); // Notify observers of the spool change
        }
    }

    public void onPrintComplete() {
        notifyObservers("printComplete", null); // notify subscribers when a print job is completed
    }

    public boolean isHoused() {
        return isHoused;
    }

    public void setHoused(boolean housed) {
        isHoused = housed;
    }

    public Spool getCurrentSpool() {
        return currentSpool;
    }

    public Spool[] getCurrentSpools() {
        Spool[] spools = new Spool[1];
        if(currentSpool != null) {
            spools[0] = currentSpool;
        }
        return spools;
    }

    @Override
    public boolean printFits(Print print) {
        return print.getHeight() <= maxZ && print.getWidth() <= maxX && print.getLength() <= maxY;
    }

    @Override
    public String toString() {
        String result = super.toString();
        String append = "- maxX: " + maxX + System.lineSeparator() +
                "- maxY: " + maxY + System.lineSeparator() +
                "- maxZ: " + maxZ + System.lineSeparator();
        if (currentSpool != null) {
            append += "- Spool(s): " + currentSpool.getId()+ System.lineSeparator();
        }
        append += "--------";
        result = result.replace("--------", append);
        return result;
    }
}
