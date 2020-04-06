package de.bl4ckskull666.teamspeakbot.classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class MyLogger {
    private File _file = new File("info.log");

    public void log(String msg) {
        String line = Config.formatString(getDate() + "|" + getTime() + "|" + msg);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(_file, true));
            bw.append(line);
            bw.newLine();
            bw.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(line);
        checkFilesize();
    }

    public String getTime() {
        Calendar cal = Calendar.getInstance();
        String h = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String m = String.valueOf(cal.get(Calendar.MINUTE));
        String s = String.valueOf(cal.get(Calendar.SECOND));

        if(cal.get(Calendar.HOUR_OF_DAY) < 10)
            h = "0" + h;

        if(cal.get(Calendar.MINUTE) < 10)
            m = "0" + m;

        if(cal.get(Calendar.SECOND) < 10)
            s = "0" + s;

        return h + ":" + m + ":" + s;
    }

    public String getDate() {
        Calendar cal = Calendar.getInstance();
        String Day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        if(cal.get(Calendar.DAY_OF_MONTH) < 10)
            Day = "0" + Day;

        String Month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        if((cal.get(Calendar.MONTH) + 1) < 10)
            Month = "0" + Month;

        return Day + "." + Month + "." + cal.get(Calendar.YEAR);
    }

    private void checkFilesize() {
        if(_file.length() < 80000000L)
            return;

        File backup = new File("info_backup.log");
        if(backup.exists())
            backup.delete();

        _file.renameTo(backup);
        _file = new File("info.log");
    }
}
