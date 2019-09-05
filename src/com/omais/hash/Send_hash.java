package com.omais.hash;


import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;



public class Send_hash {
	private static List<byte[]> list;
	private static Socket socket            = null;
    private static DataInputStream  input   = null;
    private static DataOutputStream out     = null;
    static String pass;
    static String destinationFile="/home/omais/check2.png";

	
	private static String decodeQRCode(File qrCodeimage) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("There is no QR code in the image");
            return null;
        }
    }
	
	
	public static void main(String[] args) throws InvalidKeyException, IOException, Exception{
		
		
		
        	

    	try(InputStream in = new URL("http://10.237.23.43/NSS/Images/MyQRCode1.png").openStream()){
    	    Files.copy(in, Paths.get(destinationFile));
    	}
        File file = new File(destinationFile);
            String decodedText = decodeQRCode(file);
            if(decodedText == null) {
                System.out.println("No QR Code found in the image");
            } else {
                System.out.println("Decoded text = " + decodedText);
            }
		
            try { 
                System.out.println("SHA1 hash of string: " + AeSimpleSHA1.SHA1(decodedText));
            } catch (NoSuchAlgorithmException e) { 
                // TODO Auto-generated catch block 
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) { 
                // TODO Auto-generated catch block 
                e.printStackTrace();
            } 
		socket = new Socket("localhost", 2128);
        System.out.println("Connected");

        input = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));

         // sends output to the socket
         out    = new DataOutputStream(socket.getOutputStream());
		/*out.writeInt(signed.length);
		out.write(signed);*/
         out.writeUTF(AeSimpleSHA1.SHA1(decodedText));
		out.writeUTF(decodedText);
		
		
	
		
		
		//taking from server
		boolean flag;
		String hash_ret=input.readUTF();
		
	if(hash_ret.contentEquals("Error")){
		System.out.println("There is a mismatch");
		System.exit(0);
	}
		String original=input.readUTF();
		
		String original_hash=AeSimpleSHA1.SHA1(original);
		if(hash_ret.contentEquals(original_hash)){
			flag=true;
		}else{
			flag=false;
		}
        
        
 
        
        String[] a=original.split(",");
       
        if(flag==true){
        	System.out.println("The message returned is "+ a[0] + " "+ a[1]);
        }else{
        	System.out.println("There is a loss in integrity of the message");
        	
        }
        
		
        String  d_email = "omais.shafi@cse.iitd.ac.in",
	            d_uname = "csz168514",
	            d_host = "smtp.iitd.ernet.in",
	            d_port  = "465",
	            m_to = "omais.shafi@cse.iitd.ac.in",
	           m_subject="Verification mail",
	            m_text = "This message is from Indoor Positioning App. Required file(s) are attached.";
	    Properties props = new Properties();
	    props.put("mail.smtp.user", d_email);
	    props.put("mail.smtp.host", d_host);
	    props.put("mail.smtp.port", d_port);
	    props.put("mail.smtp.starttls.enable","true");
	    props.put("mail.smtp.debug", "true");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.socketFactory.port", d_port);
	    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    props.put("mail.smtp.socketFactory.fallback", "false");

	   //Authenticator auth = new SMTPAuthenticator();
	    Session session = Session.getInstance(props, null);
	    session.setDebug(true);

	    MimeMessage msg = new MimeMessage(session);
	    try {
	        msg.setSubject(m_subject);
	        msg.setFrom(new InternetAddress(d_email));
	        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(m_to));
	        if(a[0]==null && a[1]==null){
	        	msg.setText("The Driver id does not exist");
	        }
	        msg.setText("Name: "+a[0]+"\n"+"Address: "+a[1] );
	       Transport transport = session.getTransport("smtp");
	      System.out.println("Enter the password");	       
	      Scanner ob=new Scanner(System.in);
	      String password=ob.nextLine();
	     // System.out.println("Password entered was:" + password);
	            try {
					transport.connect(d_host, Integer.valueOf(d_port), d_uname, password);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("The message could not be send");
					e.printStackTrace();
				}
	            transport.sendMessage(msg, msg.getAllRecipients());
	            transport.close();

	        } catch (AddressException e) {
	            e.printStackTrace();
	         
	        } catch (MessagingException e) {
	            e.printStackTrace();
	         
	        }
		
	}
	
	
}
