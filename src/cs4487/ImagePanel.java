package cs4487;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel{

    private BufferedImage image;

    public ImagePanel(BufferedImage image) {
    	this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters            
    }
}