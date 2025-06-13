package view;

public class TimeView {

    public void showInsertTime(long milliseconds) {
        System.out.println("Time to insert 10 million records: " + milliseconds + " ms");
    }

    public void showReadTime(long milliseconds) {
        System.out.println("Time to read 10 million records: " + milliseconds + " ms");
    }
}
