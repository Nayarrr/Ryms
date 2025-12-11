package ry.ms;

import ry.ms.MODELS.User;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "--- Login Test ---" );

        try {
            SessionFacade facade = SessionFacade.getSessionFactory();

            System.out.println("TRying to connect with admin@ryms.com...");
            User user = facade.login("admin@ryms.com", "password_123");

            System.out.println("Succes : " + user.getUsername());
            
        } catch (Exception e) {
            System.err.println("Error : " + e.getMessage());
            e.printStackTrace();
        }
    }
}