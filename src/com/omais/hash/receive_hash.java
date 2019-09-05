package com.omais.hash;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;


public class receive_hash {
	private List<byte[]> list;
	 private static Socket          socket   = null;
	    private static ServerSocket    server   = null;
	    private static DataInputStream in       =  null;
	    private static DataOutputStream out     = null;
	    static ResultSet rs;
	    static String name;
	    static String address;
	@SuppressWarnings("unchecked")
	
	
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
        String hexstr= AeSimpleSHA1.SHA1(orig_str);
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
				String return_text_hex=AeSimpleSHA1.SHA1(returntext);
				out.writeUTF(return_text_hex);
				out.writeUTF(returntext);
			    	
		}else{
			System.out.println("Sorry the signature could not be verified");
			out.writeUTF("Error");
			
			
			System.exit(0);
			
		}
		
		
		

	}
	
	
	
	
	
}
