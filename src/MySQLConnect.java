import java.sql.*;
class MySQLConnect {
    Connection setup(String input) throws SQLException {
        Connection connection = null;
        String host = "jdbc:mysql://localhost:3306/password";
        String uName = "root";
        if(!(input.equals(System.getenv("password")))){
            throw new SQLException();
        }
        connection = DriverManager.getConnection(host, uName, input);
        return connection;
    }

    void addPassword(Connection connection,String site,String generated_password) throws SQLException{
        if(checkSiteExists(connection, site)){
            throw new SQLException();
        }else {
            PreparedStatement query = connection.prepareStatement("insert into sites(Site,Password)values(?,?)");
            query.setString(1,site);
            query.setString(2,generated_password);
            query.executeUpdate();
        }
    }

    boolean checkSiteExists(Connection connection,String site) throws SQLException {
        Statement query = connection.createStatement();
        ResultSet rs = query.executeQuery("SELECT * FROM sites");
        while(rs.next()){
            String s=rs.getString("Site");
            if(s.equalsIgnoreCase(site)){
                return true;
            }
        }
        return false;
    }

    void replacePassword(Connection connection,String site,String generatePassword) throws SQLException {
        if(!checkSiteExists(connection, site)){
            throw new SQLException();
        }
        String pass= findPassword(connection,site)[0];
        System.out.println(pass);
        PreparedStatement query = connection.prepareStatement("update sites set password=replace(password,?,?) where ID=?");
        query.setString(1,pass);
        query.setString(2,generatePassword);
        query.setInt(3, Integer.parseInt(findPassword(connection,site)[1]));
        query.executeUpdate();

    }

    String printAll(Connection connection) throws SQLException{
        Statement query = connection.createStatement();
        ResultSet rs = query.executeQuery("SELECT * FROM sites");
        String str="";
        while (rs.next()) {
            str+=rs.getString("Site")+"     "+rs.getString("Password")+"\n";
        }
        return str;
    }

    String printSite(Connection connection,String site) throws SQLException {
        Statement query = connection.createStatement();
        ResultSet rs = query.executeQuery("SELECT * FROM sites");
        String str="\n--------------------------------------------------------------------------------------------------------------------" +
                "\nSite       Password\n--------------------------------------------------------------------------------" +
                "------------------------------------\n";
        while (rs.next()) {
            if(rs.getString("Site").equalsIgnoreCase(site)) {
                str += rs.getString("Site") + "       " + rs.getString("Password") + "\n";
                return str+"--------------------------------------------------------------------------------------------------------------------\n";
            }
        }
        throw new SQLException();
    }

    String[] findPassword(Connection connection, String site) throws SQLException {
        Statement query = connection.createStatement();
        ResultSet rs = query.executeQuery("SELECT * FROM sites");
        while(rs.next()){
            String s=rs.getString("Site");
            if(s.equalsIgnoreCase(site)){
                return new String[]{rs.getString("Password"),Integer.toString(rs.getInt("ID"))};
            }
        }
        throw new SQLException();
    }

}

