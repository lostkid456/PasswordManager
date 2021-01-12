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
            if(s.equals(site)){
                return true;
            }
        }
        return false;
    }

    void replacePassword(Connection connection,String site,String generatePassword) throws SQLException {
        Statement query = connection.createStatement();
        ResultSet rs = query.executeQuery("SELECT * FROM sites");
        while (rs.next()) {
            String s = rs.getString("Site");
            String st = rs.getString("Password");
            if (s.equals(site)) {
                ResultSet r = query.executeQuery("SELECT REPLACE (Password, "+st+","+generatePassword);
            }
        }
    }

}

