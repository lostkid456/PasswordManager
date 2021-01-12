import java.sql.Connection;
import java.sql.SQLException;
import java.security.SecureRandom;
import java.util.*;

public class Manager {
    public static void main(String... args) throws SQLException {
        MySQLConnect connect=new MySQLConnect();
        Scanner inp=new Scanner(System.in);
        while(true) {
            System.out.println("Enter the master password");
            String input = inp.next();
            try {
                connect.setup(input);
                break;
            } catch (SQLException e) {
                System.out.println("Incorrect Password or couldn't connect");
            }
        }
        Connection c=connect.setup(System.getenv("password"));
        System.out.println("\n\nWelcome! You have logged in successfully");
        while(true){
            System.out.println("""
                    What do you want to do
                    1.) Create a new password (Enter C/c)
                    2.) Find all sites and passwords related to your account (Enter A/a)
                    3.) Find the password for a site (Enter F/f)
                    4.) Quit (Enter Q/q)""");
            String input=inp.next().toUpperCase();
            if(input.equals("C")){
                System.out.println("Enter the site you want to create a new password for.");
                String site=inp.next();
                System.out.println("Enter the length for your new password");
                int length=inp.nextInt();
                System.out.println("Enter a keyphrase for your password");
                String keyphrase=inp.next();
                createPassword(connect,c,site,keyphrase,length);
            }else if(input.equals("A")){
                System.out.println(printAll());
            }else if(input.equals("F")){
                System.out.println("Enter the site you want the password for:");
            }else if(input.equals("Q")){
                c.close();
                System.exit(1);
            }else{
                System.out.println("This is not a proper option. Please try agian");
            }
        }
    }

    static String generatePassword(String keyphrase,int length){
        if(length<12){
            System.out.println("The password must be longer in length");
        }
        StringBuilder password=new StringBuilder();
        String special_characters=" !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        String all_Letters=keyphrase+special_characters;
        for(int i=0;i<keyphrase.length();i++){
            if(Character.isUpperCase(keyphrase.charAt(i))){
                all_Letters+=Character.toLowerCase(keyphrase.charAt(i));
            }
            else{
                all_Letters+=Character.toUpperCase(keyphrase.charAt(i));
            }
        }
        SecureRandom rand = new SecureRandom();
        int capital_counter=0;
        int special_char_counter=0;
        for(int i=0;i<length-1;i++){
            int random_index=rand.nextInt(all_Letters.length());
            char random_char=all_Letters.charAt(random_index);
            String rc=all_Letters.substring(random_index,random_index+1);
            if(Character.isUpperCase(random_char)){
                capital_counter++;
            }
            if(special_characters.contains(rc)){
                special_char_counter++;
            }
            password.append(random_char);
        }
        if(capital_counter<1){
            int random_index=rand.nextInt(keyphrase.length());
            char random_char=keyphrase.charAt(random_index);
            password.append(Character.toUpperCase(random_char));
        } else if(special_char_counter<1){
            int random_index=rand.nextInt(special_characters.length());
            char random_char=special_characters.charAt(random_index);
            password.append(random_char);
        }else{
            int random_index=rand.nextInt(all_Letters.length());
            char random_char=all_Letters.charAt(random_index);
            password.append(random_char);
        }
        return password.toString();
    }

    static void createPassword(MySQLConnect connect,Connection c,String site,String keyphrase,int length) throws SQLException {
        try{
            connect.addPassword(c,site,generatePassword(keyphrase, length));
            System.out.println("Successfully entered it in");
        }catch(SQLException e){
            System.out.println(e.getMessage());
            System.out.println(e.getErrorCode());
            System.out.println("The site you want to create a password for already exists");
            System.out.println("Maybe you want to replace the password that you already made. Yes(Y/y) or " +
                    "No(Anything else but Y/y) ?");
            Scanner inp=new Scanner(System.in);
            String input = inp.next();
            if(input.equalsIgnoreCase("Y")){
                String password=generatePassword(keyphrase,length);
                connect.replacePassword(c,site,password);
                System.out.println("Password has been replaced");
            }
        }
    }

    static String printAll(){
        String str = "";
        return str;
    }


}
