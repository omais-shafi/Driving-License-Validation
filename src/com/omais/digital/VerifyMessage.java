package com.omais.digital;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class VerifyMessage {
	private List<byte[]> list;
	 private static Socket          socket   = null;
	    private static ServerSocket    server   = null;
	    private static DataInputStream in       =  null;
	    private static DataOutputStream out     = null;
	    static ResultSet rs;
	    static String name;
	    static String address;
	@SuppressWarnings("unchecked")
	
	private static  boolean verifySignature(byte[] data, byte[] signature, String keyFile) throws Exception {
		Signature sig = Signature.getInstance("SHA1withRSA");
		sig.initVerify(getPublic(keyFile));
		sig.update(data);
		
		return sig.verify(signature);
	}
	
	//Method to retrieve the Public Key from a file
	public static PublicKey getPublic(String filename) throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}
	
	public static void main(String[] args) throws Exception{
		
		  
        server = new ServerSocket(2128);
      	
      	System.out.println("Waiting for client");
      	try {
    		Thread.sleep(1000);
    	} catch (InterruptedException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
          socket = server.accept();
          System.out.println("Client A accepted");
          
          in = new DataInputStream(
                  new BufferedInputStream(socket.getInputStream()));
              out    = new DataOutputStream(socket.getOutputStream());
          
         
         
		 byte[] message = null;
         int length = in.readInt(); 
         // read length of incoming message
         System.out.println("Th message length is "+ length);
         if(length>0) {
             message = new byte[length];
             in.readFully(message, 0, message.length); // read the message
         }
        String data=in.readUTF();
        
		boolean abc=verifySignature(data.getBytes(), message, "MyKeys/publicKey");
		if(abc==true){
			System.out.println("yes verifeid");
			   try{
			         Class.forName("com.mysql.jdbc.Driver");  
			         Connection con=DriverManager.getConnection(  
			         "jdbc:mysql://localhost:9090/omais","root","newroot");  
			         //here sonoo is database name, root is username and password  
			         Statement stmt=con.createStatement();  
			         rs=stmt.executeQuery("select * from Driver_Info");  
			         while(rs.next())  {
			        	
			       //  System.out.println(rs.getString(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
			        if(data.contentEquals(rs.getString(1))){
			        	 name=rs.getString(2);
			        	 address=rs.getString(4);
			        	 break;
			         }
			         }
			         con.close();  
			         }catch(Exception e){ System.out.println(e);}  
			   
			   System.out.println("The name is "+ name);
				String returntext=name+","+address;
				byte[] textinbytes=signature_done(returntext, "MyKeys/privateKey");
				out.writeInt(textinbytes.length);
				out.write(textinbytes);
				out.writeUTF(returntext);
			    	
		}else{
			System.out.println("Sorry the signature could not be verified");
			System.exit(0);
			
		}
		
		
		

	}
	
	
	
	public static byte[] signature_done(String data, String keyfile) throws InvalidKeyException, Exception{
		return sign(data,keyfile);
	}
	//The method that signs the data using the private key that is stored in keyFile path
	public static byte[] sign(String data, String keyFile) throws InvalidKeyException, Exception{
		Signature dsa = Signature.getInstance("SHA1withRSA"); 
		dsa.initSign(getPrivate(keyFile));
		dsa.update(data.getBytes());
		return dsa.sign();
	}
	
	//Method to retrieve the Private Key from a file
	public static PrivateKey getPrivate(String filename) throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}
	
	
}
