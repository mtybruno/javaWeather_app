import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONObject;

public class appGui extends JFrame {
    
    private JSONObject weatherData;

    public appGui() {
        // setup our gui and add a title
        super("Weather App");

        // configure gui to end the program's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // set the size of our gui (in pixels)
        setSize(450, 650);

        // load our gui at the center of the screen
        setLocationRelativeTo(null);

        // make our layout manager null to manually position our components within the gui
        setLayout(null);

        // prevent any resize of our gui
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents() {
            //search field
        JTextField searchTextField = new JTextField();

            //set the location and size of the components
        searchTextField.setBounds (15,15,351,45);

            //Font style and sizes
        searchTextField.setFont(new Font("Dialog",Font.PLAIN,24));

        add(searchTextField);

            //weather img
        JLabel weatherConditionImage = new JLabel(loadImage("src\\assets\\cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

            //temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog",Font.BOLD,48));

            //center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

            //weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog",Font.PLAIN,32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

            //humidity icon
        JLabel humidityImage = new JLabel(loadImage("src\\assets\\humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

            //humidity text (html format to specific bold text while keeping the restplain)
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN,16));
        add(humidityText);

            //Wind speed text
        JLabel windSpeedImage = new JLabel(loadImage("src\\assets\\windspeed.png"));
        windSpeedImage.setBounds(220,500,74,66);
        add(windSpeedImage);

            //Wind speed Text
        JLabel windSpeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windSpeedText.setBounds(310,500,85,55);
        windSpeedText.setFont(new Font("Dialog",Font.PLAIN,16));
        add(windSpeedText);

            //Search Button
        JButton searchButton = new JButton(loadImage("src\\assets\\search.png"));

            //hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get location from user
                String userInput = searchTextField.getText();

                //validate input
                if(userInput.replaceAll("\\s", " ").length() <= 0) {
                    return;
                }

                //retrive weather data
                weatherData = weatherApp.getWeatherData(userInput);

                //update GUI

                //Update weather Img
                String weatherCondition = (String) weatherData.get("weather_condition");

                //update "weather condition" to accordingly
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.pngImage"));
                        break;
                }
                     // update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                    // update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                    // update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                    // update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
                        
            }
        });
        add(searchButton);
    }
    //used to create imagens in the gui components
    private ImageIcon loadImage(String resourcePath) {
        try{
            BufferedImage image = ImageIO.read(new File(resourcePath));

            //return the image so the components can render it
            return new ImageIcon(image); 
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
        
    }
}