
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class HelloCV extends JFrame {
	
	public static void main(String[] args) {
	    Mat frame = new Mat();
	    //0; default video device id
	    VideoCapture camera = new VideoCapture(0);
	    JFrame jframe = new JFrame("Title");
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JLabel vidpanel = new JLabel();
	    jframe.setContentPane(vidpanel);
	    jframe.setVisible(true);

	    while (true) {
	        if (camera.read(frame)) {

//	            ImageIcon image = new ImageIcon(Mat2bufferedImage(frame));
//	            vidpanel.setIcon(image);
//	            vidpanel.repaint();

	        }
	    }
	}
}