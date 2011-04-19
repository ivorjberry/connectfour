import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;

import javax.swing.ImageIcon;


public class TileCanvas extends Canvas {
	
	ImageIcon underImage;
	ImageIcon overImage;

	public TileCanvas() {
		underImage = null;
		overImage = null;
	}

	public void setUnderImage(ImageIcon newImage)
	{
		underImage = newImage;
		repaint();
	}
	
	public void setOverImage(ImageIcon newImage)
	{
		overImage = newImage;
		repaint();
	}
	
	public void paint(Graphics g)
	{
		if (underImage != null)
		{
			g.drawImage(underImage.getImage(), 4, 4, this);
		}
		if (overImage != null)
		{
			g.drawImage(overImage.getImage(), 0, 0, this);
		}
	}

}
