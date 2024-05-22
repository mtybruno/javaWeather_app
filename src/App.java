import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            
            public void run() {
                //Display app gui
            new appGui().setVisible(true);

            //System.out.println(weatherApp.getLocationData("Tokyo"));

            //System.out.println(weatherApp.getCurrentTime());
            }
        });
    }
}