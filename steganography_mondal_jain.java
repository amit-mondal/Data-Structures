//Shreyan Jain and Amit Mondal Period 5
//Data Structures Final Project - Nifty Steganography ("Secrets in Images")

import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.BorderFactory;

import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.*;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader; 
import java.io.IOException;

/**
   The public Steganography_Jain_Mondal_Test class, which tests the functionality of the 
   Steganography class and also organizes and displays all the GUI elements.
   
   This was a very fun and educational project. We had to spend a lot of time learning many
   new concepts, particularly bitwise operators and bit operations, such as &, |, and >>>.
   The hardest part of the project was understanding the algorithmic basis of steganography,
   or manipulating the bytes of an image to hide a secret image. Once we understood the approach
   we had to take, implementing the algorithm in code was fairly easy. The GUI also provided some
   challenges. Overall, the individual elements, such as the panels, buttons, and action listeners,
   themselves were easy to create, but creating the overall scaled layout of all the GUI elements
   was challenging. The end result was a great succes, as any String message the user enters
   is successfully encoded into a chosen image with no visible changes made to the image. Moreover,
   the message is always successfully decoded from the image.
   
   In order to run the code, make sure to select an image file (JPEG or PNG) from your local file
   system and load that image once the program runs. This will be the image used to hide the secret
   message. Once a secret message is hidden, the new, altered image will be saved to the user's desktop
   by default.
   
   Note that, if the user wishes, he can encode an extremely long text message (several paragraphs or
   pages of text) into the image by copying and pasting text into the text field. Of course, if the 
   text is too large to be hidden within the selected image, an error will be thrown. But if the
   text is of a smaller size than the image, the text will be successfully encoded into the image. One
   weakness of our code, however, is that the decoded message is displayed in a non-scrollable,
   non-resizable JLabel. Thus, the user will not be able to view the entirety of the decoded message.
*/
public class Steganography_Jain_Mondal_New_Button{
   //GUI elements
   private static JFrame mainFrame;
   private static JPanel originalImagePanel;
   private static JPanel buttonPanel;
   private static JPanel textPanel;
   private static JPanel encodedImagePanel;
   private static JButton openImage;
   private static JButton encodeMessage;
   private static JButton displayNewImage;
   private static JButton decodeMessage;
   private static JButton loadEncoded;
   private static JTextField originalMessageField;
   private static JLabel decodedMessageField;
   
   //Variables to hold the initial and decoded messages
   private static String messageText;
   private static String decodeText;
   
   //Booleans to check whether or not the initial and encoded images are initialized
   private static boolean loadImage = false;
   private static boolean newImage = false;
   private static boolean isencodedImage = false;
   
   //The references to the original and encoded images
   private static BufferedImage originalImage;
   private static BufferedImage encodedImage;
   
   //The variable that allows for browsing of the file system to select an image file
   private static JFileChooser fc;
   
   //The variable that stores the byte representation of the initial image
   private static byte[] imgBytes;
   
   /**
      The main method of the public Steganography_Jain_Mondal_Test class. Contains all
      the code for the GUI.
   */
   public static int unsignedToBytes(byte b) {
     return b & 0xFF;
   }
   public static void main (String[] args) throws Exception {
      final Steganographer stego = new Steganographer();
      mainFrame = new JFrame("Steganography");
      mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      /**
         Original Image Panel component. This will contain the original image into which 
         the text message will be encoded.
      */
      originalImagePanel = new JPanel() {
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (loadImage == true)
            {
               g.drawImage(originalImage, 0, 0, null);
            }
         }
      };
      originalImagePanel.setBackground(Color.WHITE);
      originalImagePanel.setBorder(BorderFactory.createLineBorder(Color.black));

      //mainFrame.add(originalImagePanel, BorderLayout.WEST);
      
      /**
         Encoded Image Panel component. This will contain the encoded image which contains the 
         hidden text message.
      */
      encodedImagePanel = new JPanel() {
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (newImage == true)
            {
               g.drawImage(encodedImage, 0, 0, null);
            }
         }
      };
      encodedImagePanel.setBackground(Color.WHITE);
      encodedImagePanel.setBorder(BorderFactory.createLineBorder(Color.black));
      //mainFrame.add(encodedImagePanel, BorderLayout.EAST);
      
      /**
         Text Panel Component. Has a text field for user to enter a text message to be encoded,
         as well as a label to display the decoded message.
      */
      textPanel = new JPanel();
      textPanel.setBackground(Color.WHITE);
      textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
      originalMessageField = new JTextField();
      decodedMessageField = new JLabel("Decoded Message: ");
      textPanel.add(originalMessageField);
      textPanel.add(decodedMessageField);
      //mainFrame.add(textPanel, BorderLayout.NORTH);

      /**
         Button Panel component. Contains the buttons to load an image into the originalImagePanel,
         encode the text message into the original image, display the new image, and decode the text
         message from the new image.
      */
      JPanel top = new JPanel();
      top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
      top.add(originalImagePanel);
      top.add(encodedImagePanel);
      mainFrame.add(top, BorderLayout.CENTER);
      buttonPanel = new JPanel();
      //openImage button, which allows the user to select an image
      openImage = new JButton("Load Image");
      fc = new JFileChooser();
      openImage.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            try {
               int returnVal = fc.showOpenDialog(originalImagePanel);
               if (returnVal == JFileChooser.APPROVE_OPTION) {
                  File file = fc.getSelectedFile();
                  try {
                     originalImage = ImageIO.read(file);
                     imgBytes = stego.getImageBytes(originalImage);
                  } catch (IOException E) {
                     E.printStackTrace();
                  }
                  loadImage = true;
                  originalImagePanel.repaint();
               }
            } catch (Exception except) {
               System.out.println("Error: file of wrong format chosen. Please choose image file.");
            }
         }
      });
      buttonPanel.add(openImage);
      //encodeMessage button, which allows user to encode his secret message into the image
      encodeMessage = new JButton("Encode Message into Picture");
      encodeMessage.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            messageText = originalMessageField.getText();
            byte bytes[] = null;
            byte[] msgbytes = null;
            byte[] encoded = null;
            try{
               bytes = stego.getImageBytes(originalImage);
               msgbytes = messageText.getBytes();
               encoded = new byte[bytes.length];
               encoded = stego.encode(bytes, msgbytes);
               encodedImage = stego.saveNewImage(encoded,"newimg.jpg");
               newImage = true;
            }catch (Exception E){
               if (originalImage == null)
                  System.out.println("Error: Original image not initialized.");
               else
                  System.out.println("Error: No message text entered.");
            }
         }
      });
      buttonPanel.add(encodeMessage);
      //displayNewImage button, which displays the encoded image onto the screen
      displayNewImage = new JButton("Display Encoded Image");
      displayNewImage.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            encodedImagePanel.repaint();
            if (originalImage == null)
               System.out.println("Error: Original image not initizialized.");
            else if (encodedImage == null)
               System.out.println("Error: No message encoded.");
         }
      });
      buttonPanel.add(displayNewImage);
      //decodeMessage button, which deciphers the secret message from the manipulated image
      decodeMessage = new JButton("Decode Message");
      decodeMessage.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            try {
               if(isencodedImage){
                  byte[] bytes = stego.getImageBytes(encodedImage);
                  byte[] unsignedbytes = new byte[bytes.length];
                  for(int i = 0;i<bytes.length;i++){
                     unsignedbytes[i] = (byte)unsignedToBytes(bytes[i]);
                  }
                  decodeText = stego.decode(unsignedbytes);
               }
               else decodeText = stego.decode(stego.getImageBytes(encodedImage));
               decodedMessageField.setText("Decoded Message: " + decodeText);
            }
            catch (Exception E) {
               E.printStackTrace();
               /**if (encodedImage == null)
                  System.out.println("Error: Encoded image not initialized.");
               else
                  System.out.println("Error: No message encoded.");*/
            }
         }
      });
      buttonPanel.add(decodeMessage);
      loadEncoded = new JButton("Load Encoded Image");
      loadEncoded.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            try {
               int returnVal = fc.showOpenDialog(originalImagePanel);
               if (returnVal == JFileChooser.APPROVE_OPTION) {
                  File file = fc.getSelectedFile();
                  try {
                     encodedImage = ImageIO.read(file);
                  } catch (IOException E) {
                     E.printStackTrace();
                  }
                  isencodedImage = true;
                  newImage = true;
                  encodedImagePanel.repaint();
               }
            } catch (Exception except) {
               System.out.println("Error: file of wrong format chosen. Please choose image file.");
            }
         }
      });
      buttonPanel.add(loadEncoded);
     
      /**
         Opening the main GUI frame.
      */
      JPanel bottom = new JPanel();
      bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
      bottom.add(textPanel);
      bottom.add(buttonPanel);
      mainFrame.add(bottom, BorderLayout.SOUTH);
      mainFrame.setSize(900, 700);
      mainFrame.setVisible(true);
   }
}

/**
   The Steganographer class. This class contains methods that implement the technique of hiding
   secret messages known as steganography. In this process, a secret String message is converted
   into bytes and each bit of the message is hidden within a single byte of an image. The image
   bytes are only affected in the least significant bit, which causes a very minor, imperceptible
   change to the picture. Thus, the picture seems the same to the human eye but now encodes a 
   secret message, which can be retrieved or decoded. The first 32 bytes of the image are used to
   store the length of the message, the following bytes are used to store the message itself. Our
   code uses the convention of using the first n bytes of the image to store the message rather than
   a more scattered array of image bytes. Also note that, since only one bit of information is hidden
   within each image byte, the code requires 8 image bytes to hide one message byte.
*/
class Steganographer{
   //variable to store specific attribtues of the image
   private int imgHeight = 0;
   private int imgWidth = 0;
   
   /**
      Returns a byte array representation of the image passed in as a parameter.
   */
   public byte[] getImageBytes(BufferedImage bufferedImage) throws IOException{
      imgHeight = bufferedImage.getHeight();
      imgWidth = bufferedImage.getWidth();
      WritableRaster raster = bufferedImage.getRaster();
      DataBufferByte data = (DataBufferByte)raster.getDataBuffer();
      return data.getData();
   }
   
   /**
      Returns a byte array representation of the text appearing in the text file whose name
      is passed in as a parameter.
   */
   public byte[] getMessageBytes(String msg){
      try{
         File imgPath = new File(msg);
         BufferedReader reader = new BufferedReader(new FileReader(imgPath));
         String test = reader.readLine();
         String result = "";
         while(test != null){
            result+=test;
            test = reader.readLine();
         }
         return result.getBytes();
      }catch (Exception e){
         e.printStackTrace();
         return null;
      }
   }
   
   /**
      Saves an image represented by the byte array passed in as a parameter onto the user's local desktop.
   */
   public BufferedImage saveNewImage(byte[] bytes, String name){
		try {
         BufferedImage img=new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_3BYTE_BGR);
         img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(bytes, bytes.length), new Point() ) );
         ImageIO.write(img, "jpg", new File(System.getProperty("user.home") + "/Desktop/" + name));
         return img;
 		} catch (IOException e) {
			System.out.println(e.getMessage());
         return null;
		}
   }
   
   /**
      The encode method, which hides the secret message (represented by a byte array) into the
      image (also represented by a byte array). The encoding is achieved by converting the LSB
      (least significant bit) of every byte in the image's byte array into a bit from the message's
      byte array.
   */
   public byte[] encode(byte[] imgBytes, byte[] msgBytes) throws Exception {
      int size = msgBytes.length;
      if (8*size + 4 > imgBytes.length)
         throw new Exception("Message too large to be encoded in this image.");
      //Encoding the size of the image within the first 32 bytes of the image.
      byte[] lenBytes = new byte[4];
      for (int i = 0; i < 4; i++) {
          lenBytes[i] = (byte)(size >>> (i * 8));
      }
      int count = 0;
      int lenIndex = 0;
      byte temp = lenBytes[0];
      for (int i = 0; i < 32; i++) {
         if (count >= 8) {
            lenIndex++;
            count = 0;
         }
         if (count == 0)
            temp = (byte)(lenBytes[lenIndex]);
         else
            temp = (byte)((temp >>> 1));
         if((byte)(temp & 1) == 1)
            imgBytes[i] = (byte)(imgBytes[i] | 1);
         else
            imgBytes[i] = (byte)(imgBytes[i] & 254);
         count++;
      }
      //Encoding the actual message itself into the image.
      count = 0;
      int msgIndex = 0;
      temp = msgBytes[0];
      for(int i = 32;i<(8*msgBytes.length + 32);i++){
         if(count >= 8){
            msgIndex++;
            count = 0;
            //System.out.println("Reset, msgIndex is: " + msgIndex);
         }
         if(count == 0){
            temp = (byte)(msgBytes[msgIndex]);
         }
         else{
            temp = (byte)((temp >>> 1));
         }
         //System.out.println("temp is " + String.format("%8s", Integer.toBinaryString(temp & 0xFF)).replace(' ', '0'));
         if((byte)(temp & 1) == 1){
            imgBytes[i] = (byte)(imgBytes[i] | 1);
         }
         else{
            imgBytes[i] = (byte)(imgBytes[i] & 254);
         }
         count++;
      }
      return imgBytes;
   }
   
   /**
      The decode method, which returns a String representation of the secret message hidden within
      the image represented by the byte array imgBytes. Retrieves all LSBs (least significant bits)
      from the desired range of the image bytes and then converts them into a byte array, that is
      then converted into the String message.
   */
   public String decode(byte[] imgBytes) {
      //Decoding the first 32 bytes to retrieve the size of the message
      byte[] lenBytes = new byte[4];
      for (int i = 0; i < 4; i++) {
         for (int j = 8*i; j < 8*(i+1); j++) {
            byte temp = imgBytes[j];
            if ((byte)(temp & 1) == 1)
               lenBytes[i] = (byte)(lenBytes[i] | 128);
            else
               lenBytes[i] = (byte)(lenBytes[i] & 127);
            if (j%8 != 7)
               lenBytes[i] = (byte)((lenBytes[i] >>> 1));
         }
      }
      int size = 0;
      for (int i = 0; i < 4; i++) {
         size = size + ((int)lenBytes[i] & 0xFF)*integralPower(256, i); 
      }
      //Decoding the remaining bytes to retrieve the message itself
      byte[] msgBytes = new byte[size];
      for (int i = 4; i < size + 4; i++) {
         for (int j = 8*i; j < 8*(i+1); j++) {
            byte temp = imgBytes[j];
            if ((byte)(temp & 1) == 1)
               msgBytes[i-4] = (byte)(msgBytes[i-4] | 128);
            else
               msgBytes[i-4] = (byte)(msgBytes[i-4] & 127);
            if (j%8 != 7)
               msgBytes[i-4] = (byte)((msgBytes[i-4] >>> 1));
         }
      }
      String decoded = new String(msgBytes);
      return decoded;
   }
   
   /**
      A method that carries out integer exponentiation.
   */
   public int integralPower(int a, int b) {
      if (b == 0)
         return 1;
      else if (b == 1)
         return a;
      else
         return a*integralPower(a, b-1);
   }
}
