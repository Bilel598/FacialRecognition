import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

class ObjectDetectionDemo1 {
	Mat tempo = Imgcodecs.imread("/home/pi/Bureau/Images/tempo.jpg");

	//	Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);

	public void detect(Mat frame, CascadeClassifier faceCascade, CascadeClassifier eyesCascade) throws Exception {

		Mat frameGray = new Mat();
		Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(frameGray, frameGray);

		// -- Detect faces
		MatOfRect faces = new MatOfRect();
		faceCascade.detectMultiScale(frameGray, faces);
		List<Rect> listOfFaces = faces.toList();
		Rect rectCrop=null;

		for (Rect face : listOfFaces) {
			//			Point center = new Point(face.x + face.width / 2, face.y + face.height / 2);
			//			Imgproc.ellipse(frame, center, new Size(face.width / 2, face.height / 2), 0, 0, 360,
			//					new Scalar(255, 0, 255));
			Mat faceROI = frameGray.submat(face);
			// -- In each face, detect eyes
			MatOfRect eyes = new MatOfRect();
			eyesCascade.detectMultiScale(faceROI, eyes);
			//			List<Rect> listOfEyes = eyes.toList();
			//
			//			for (Rect eye : listOfEyes) {
			//				Point eyeCenter = new Point(face.x + eye.x + eye.width / 2, face.y + eye.y + eye.height / 2);
			//				int radius = (int) Math.round((eye.width + eye.height) * 0.25);
			//				Imgproc.circle(frame, eyeCenter, radius, new Scalar(255, 0, 0), 4);

			System.out.println("Face !");

			if(faces.toArray().length == 1){

				rectCrop = new Rect(face.x, face.y, face.width, face.height);
				Mat image_roi = new Mat(frame,rectCrop);
				Mat destination = new Mat();
				Imgproc.cvtColor(image_roi, destination, Imgproc.COLOR_RGB2GRAY);
				// Saving the image
				Imgcodecs.imwrite("/home/pi/Bureau/Images/tempo.jpg",destination);
				Comparaison();
			}
			else{

				System.out.println("OUPS, " + faces.toArray().length + "detected!");
				for(int i=0; i<faces.toArray().length; i++){
					rectCrop = new Rect(face.x, face.y, 240, 240);
					Mat image_roi = new Mat(frame,rectCrop);
					Mat destination = new Mat();
					Imgproc.cvtColor(image_roi, destination, Imgproc.COLOR_RGB2GRAY);
					// Saving the image
					Imgcodecs.imwrite("/home/pi/Bureau/Images/tempo" + i + ".jpg",destination);
					try {
						Comparaison();
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println(e);
					}
				}
			}

			//			}
		}

	}
	public void run(String[] args) {
		String filenameFaceCascade = args.length > 2 ? args[0] : "../haarcascade_frontalface_alt.xml";
		String filenameEyesCascade = args.length > 2 ? args[1] : "../haarcascade_eye_tree_eyeglasses.xml";
		int cameraDevice = args.length > 2 ? Integer.parseInt(args[2]) : 0;
		CascadeClassifier faceCascade = new CascadeClassifier();
		CascadeClassifier eyesCascade = new CascadeClassifier();
		if (!faceCascade.load(filenameFaceCascade)) {
			System.err.println("--(!)Error loading face cascade: " + filenameFaceCascade);
			System.exit(0);
		}
		if (!eyesCascade.load(filenameEyesCascade)) {
			System.err.println("--(!)Error loading eyes cascade: " + filenameEyesCascade);
			System.exit(0);
		}
		VideoCapture capture = new VideoCapture(cameraDevice);
		if (!capture.isOpened()) {
			System.err.println("--(!)Error opening video capture");
			System.exit(0);
		}
		Mat frame = new Mat();
		while (capture.read(frame)) {
			if (frame.empty()) {
				System.err.println("--(!) No captured frame -- Break!");
				break;
			}
			//-- 3. Apply the classifier to the frame
			try {
				detect(frame, faceCascade, eyesCascade);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	static int countFiles() throws Exception {
		String parent= "/home/pi/Bureau/Images/DB";
		File file = new File (parent);

		if (!file.exists ())
			throw new FileNotFoundException ();
		return file.list ().length;
	}
	public static void Comparaison() throws Exception{
		File repertoire = new File("/home/pi/Bureau/Images/DB");
		String liste[] = repertoire.list();
		
		double ressemblance = 100;
		String personne="inconnue";
		double p= 0;
		if (liste != null) {         
			for (int i = 0; i < liste.length; i++) {
				BufferedImage img1 = ImageIO.read(new File("/home/pi/Bureau/Images/DB/" + liste[i]));
				BufferedImage img2 = ImageIO.read(new File("/home/pi/Bureau/Images/tempo.jpg"));
				p = getDifferencePercent(resize(img1, img2.getWidth(), img2.getHeight()), img2);
				
				if(ressemblance>p){
					ressemblance = p;
					personne= liste[i];
					
				}
			} 
			System.out.println("Il y a " + (100-ressemblance) + "% de chance que cette personne soit: " + personne);
		}
		else System.err.println("Nom de repertoire invalide");

	}
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	} 

	private static double getDifferencePercent(BufferedImage img1, BufferedImage img2) {
		int width = img1.getWidth();
		int height = img1.getHeight();
		int width2 = img2.getWidth();
		int height2 = img2.getHeight();
		if (width != width2 || height != height2) {
			throw new IllegalArgumentException(String.format("Images must have the same dimensions: (%d,%d) vs. (%d,%d)", width, height, width2, height2));
		}

		long diff = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				diff += pixelDiff(img1.getRGB(x, y), img2.getRGB(x, y));
			}
		}
		long maxDiff = 3L * 255 * width * height;

		return 100.0 * diff / maxDiff;
	}

	private static int pixelDiff(int rgb1, int rgb2) {
		int r1 = (rgb1 >> 16) & 0xff;
		int g1 = (rgb1 >>  8) & 0xff;
		int b1 =  rgb1        & 0xff;
		int r2 = (rgb2 >> 16) & 0xff;
		int g2 = (rgb2 >>  8) & 0xff;
		int b2 =  rgb2        & 0xff;
		return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
	}
}

public class ObjectDetection1 {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		new ObjectDetectionDemo1().run(args);
	}
}

