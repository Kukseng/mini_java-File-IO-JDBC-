package view;

public class Header {
    public static void header() {
        final String ANSI_RED = "\u001B[31m"; // Red text
        final String ANSI_ORANGE = "\u001B[38;5;208m"; // Approximation of orange
        final String ANSI_BLUE = "\u001B[34m"; // Blue text
        final String ANSI_RESET = "\u001B[0m";
        String[] colors = {ANSI_RED, ANSI_ORANGE, ANSI_BLUE, ANSI_RED, ANSI_ORANGE, ANSI_BLUE, ANSI_RESET, ANSI_RESET};
        String[] asciiArt = {
                "",
                "███████╗               ██████╗ ██████╗ ███╗   ███╗███╗   ███╗███████╗██████╗  ██████╗███████╗",
                "██╔════╝              ██╔════╝██╔═══██╗████╗ ████║████╗ ████║██╔════╝██╔══██╗██╔════╝██╔════╝",
                "█████╗      █████╗    ██║     ██║   ██║██╔████╔██║██╔████╔██║█████╗  ██████╔╝██║     █████╗  ",
                "██╔══╝      ╚════╝    ██║     ██║   ██║██║╚██╔╝██║██║╚██╔╝██║██╔══╝  ██╔══██╗██║     ██╔══╝  ",
                "███████╗              ╚██████╗╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║███████╗██║  ██║╚██████╗███████╗",
                "╚══════╝               ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝ ╚═════╝╚══════╝",
                "",
        };

        int terminalWidth = 200;
        int maxArtWidth = 0;
        for (String line : asciiArt) {
            if (line.length() > maxArtWidth) {
                maxArtWidth = line.length();
            }
        }

        int padding = (terminalWidth - maxArtWidth) / 2;

        for (int i = 0; i < asciiArt.length; i++) {
            System.out.println(colors[i] + " ".repeat(Math.max(0, padding)) + asciiArt[i] + ANSI_RESET);
        }
    }

}

