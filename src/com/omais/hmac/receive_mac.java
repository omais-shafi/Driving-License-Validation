package com.omais.hmac;



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
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class receive_mac {
	private List<byte[]> list;
	 private static Socket          socket   = null;
	    private static ServerSocket    server   = null;
	    private static DataInputStream in       =  null;
	    private static DataOutputStream out     = null;
	    static ResultSet rs;
	    static String name;
	    static String address;
	    static String key = "Bar12345Bar12345"; 
	    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	@SuppressWarnings("unchecked")
	
	
	private static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}

		return formatter.toString();
	}

	public static String calculateRFC2104HMAC(String data, String key)
		throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
	{
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		return toHexString(mac.doFinal(data.getBytes()));
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
        
              // 71c79c40fdcf53a072b1795b91c9a09c48bf6630a4f3f30923bd070e00765e65
              // 71c79c40fdcf53a072b1795b91c9a09c48bf6630a4f3f30923bd070e00765e65
         
             
		boolean abc;
        String hashstring=in.readUTF();
        String orig_str=in.readUTF();
        String hexstr= calculateRFC2104HMAC(orig_str, key);
        System.out.println("Hashstr"+ hashstring +" "+ "hextr" + hexstr);
        
        
        if(hexstr.contentEquals(hashstring))
        {	
        	abc=true;
        }else{
        	abc=false;
        }
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
			        if(orig_str.contentEquals(rs.getString(1))){
			        	 name=rs.getString(2);
			        	 address=rs.getString(4);
			        	 break;
			         }
			         }
			         con.close();  
			         }catch(Exception e){ System.out.println(e);}  
			   
			   System.out.println("The name is "+ name);
				String returntext=name+","+address;
				String return_text_hex=calculateRFC2104HMAC(returntext, key);
				out.writeUTF(return_text_hex);
				out.writeUTF(returntext);
			    	
		}else{
			System.out.println("Sorry the signature could not be verified");
			out.writeUTF("Error");
			
			
			System.exit(0);
			
		}
		
		
		

	}
	
	
	
	
	
	
}
