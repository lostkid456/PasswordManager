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
                    4.) Replace a password for a site (Enter R/r)
                    5.) Quit (Enter Q/q)""");
            String input=inp.next().toUpperCase();
            if(input.equals("C")){
                System.out.println("Enter the site you want to create a new password for.");
                String site=inp.next();
                System.out.println("Enter the length for your new password");
                int length=inp.nextInt();
                System.out.println("Enter the key phrase for your new password");
                inp.nextLine();
                String keyphrase = inp.nextLine();
                createPassword(connect,c,site,keyphrase,length);
            }else if(input.equals("A")){
                System.out.println(printAll(c,connect));
            }else if(input.equals("F")){
                System.out.println("Enter the site you want the password for:");
                String in=inp.next();
                printSpecific(c,connect,in);
            }else if(input.equals("R")){
                try {
                    System.out.println("Enter the site you want to create a new password for.");
                    String site=inp.next();
                    System.out.println("Enter the length for your new password");
                    int length=inp.nextInt();
                    System.out.println("Enter the key phrase for your new password");
                    inp.nextLine();
                    String keyphrase = inp.nextLine();
                    String password = generatePassword(keyphrase,length);
                    connect.replacePassword(c,site,password);
                    System.out.println("Password has been replaced to "+password);
                }catch(SQLException e){
                    System.out.println("Site is not found");
                }
            }
            else if(input.equals("Q")){
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
            Scanner inp=new Scanner(System.in);
            int len=inp.nextInt();
            return generatePassword(keyphrase,len);
        }
        StringBuilder password=new StringBuilder();
        String special_characters=" !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        String all_Letters=keyphrase.replace(" ","");
        for(int i=0;i<keyphrase.length();i++){
            if(Character.isUpperCase(keyphrase.charAt(i))){
                all_Letters+=Character.toLowerCase(keyphrase.charAt(i));
            }
            else{
                all_Letters+=Character.toUpperCase(keyphrase.charAt(i));
            }
        }
        all_Letters=all_Letters.replace(" ", "");
        SecureRandom rand = new SecureRandom();
        int capital_counter=0;
        int special_char_counter=0;
        for(int i=0;i<length-1;i++){
            int choose=rand.nextInt(2);
            if(choose==1){
                int index=rand.nextInt(all_Letters.length());
                char letter=all_Letters.charAt(index);
                if(Character.isUpperCase(letter)){
                    capital_counter++;
                }
                password.append(letter);
            }else{
                int index=rand.nextInt(special_characters.length());
                char specialChar=special_characters.charAt(index);
                password.append(specialChar);
                special_char_counter++;
            }
        }
        if (capital_counter<1){
            int index=rand.nextInt(special_characters.length());
            char specialChar=all_Letters.charAt(index);
            password.append(specialChar);
        }
        if(special_char_counter<1){
            int index=rand.nextInt(all_Letters.length());
            char letter=special_characters.charAt(index);
            password.append(Character.toUpperCase(letter));
        }
        while(password.length() < length){
            int choose=rand.nextInt(2);
            if(choose==0){
                int index=rand.nextInt(all_Letters.length());
                char letter=all_Letters.charAt(index);
                password.append(letter);
            }else{
                int index=rand.nextInt(special_characters.length());
                char specialChar=special_characters.charAt(index);
                password.append(specialChar);
            }
        }
        return password.toString();
    }

    static void createPassword(MySQLConnect connect,Connection c,String site,String keyphrase,int length) throws SQLException {
        try{
            connect.addPassword(c,site,generatePassword(keyphrase, length));
            System.out.println("Successfully entered it in");
        }catch(SQLException e){
            System.out.println("The site you want to create a password for already exists");
        }
    }

    static String printAll(Connection connection,MySQLConnect connect) throws SQLException {
        return "Site       Password\n"+connect.printAll(connection);
    }

    static void printSpecific(Connection connection,MySQLConnect connect,String site){
        try{
            System.out.println(connect.printSite(connection,site));
        } catch (SQLException throwables) {
            System.out.println("Site not found\n");
        }
    }



}
