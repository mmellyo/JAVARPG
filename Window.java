package game;
import javax.swing.JFrame;
public class Window extends JFrame {
    public Window() {
      setTitle("mAmE");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setContentPane(new GamePanel(1280, 720)); //adds the panel
      pack(); // Adjusts the frame to fit panel
      setLocationRelativeTo(null);
      setVisible(true);
    }
}
