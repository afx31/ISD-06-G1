package model.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import model.*;

public class dbManager {

    private Statement st;

    public dbManager(Connection conn) throws SQLException {
        st = conn.createStatement();
    }

    public ResultSet findMovie(String search) throws SQLException {
        search = search.toLowerCase();
        String sql;
        if (search == "") {
            sql = "SELECT * FROM archive.movie";
        } else {
            sql = "SELECT * FROM archive.movie WHERE lower(title) LIKE '%" + search + "%' OR lower(genre) LIKE '%" + search + "%' OR lower(director) LIKE '%" + search + "%'";
        }
        ResultSet rs = st.executeQuery(sql);
        return rs;
    }

    public void addMovie(String ID, String Genre, String Director, String Price, String Stock, String Title, String Published) throws SQLException {
        //String sql = "INSERT INTO archive.movie VALUES ('" + ID + "', " + Genre + "', )";
        String sql = "INSERT INTO archive.movie VALUES('" + ID + "', '" + Genre + "', '" + Director + "', '" + Price + "', '" + Stock + "', '" + Title + "', '" + Published + "')";
        st.executeUpdate(sql);
    }

    public void deleteMovie(String ID) throws SQLException {
        String sql = "DELETE FROM movie WHERE ID = '" + ID + "'";
        st.executeUpdate(sql);
    }

    public void updateMovie(String ID, String Genre, String Director, String Price, String Stock, String Title, String Published) throws SQLException {
        //String sql = "UPDATE archive.movie SET ID='"+ID+"', GENRE='"+Genre+"', DIRECTOR='"+Director+"', PRICE='"+Price+"', STOCK='"+Stock+"', TITLE='"+Title+"', PUBLISHED='"+Published+"')";
        String sql = "UPDATE archive.movie SET GENRE='" + Genre + "', DIRECTOR='" + Director + "', PRICE='" + Price + "', STOCK='" + Stock + "', TITLE='" + Title + "', PUBLISHED='" + Published + "' WHERE ID='" + ID + "'";
        st.executeUpdate(sql);
    }

    public void updateMovieStock(String ID, String stock) throws SQLException {
        String sql = "UPDATE archive.movie SET STOCK='" + stock + "' WHERE ID='" + ID + "'";
        st.executeUpdate(sql);
    }

    public Movie findMovieID(String id) throws SQLException {
        String sql = "SELECT * FROM archive.movie WHERE id = '" + id + "'";
        ResultSet rs = st.executeQuery(sql);
        Movie movie = null;
        if (rs.next()) {
            String movieID = rs.getString("ID");
            String genre = rs.getString("GENRE");
            String director = rs.getString("DIRECTOR");
            String price = rs.getString("PRICE");
            String stock = rs.getString("STOCK");
            String title = rs.getString("TITLE");
            String published = rs.getString("PUBLISHED");
            movie = new Movie(movieID, genre, director, price, stock, title, published);
        }
        return movie;
    }

    /*
       Find student by Email in the database
       Convert email into lowercase and then construct sql string
       If there is a result then construct a new Users called user
       Then return the user
    */
    public Users findUser(String email) throws SQLException {

        String emailLower = email.toLowerCase();
        String sql = "SELECT * FROM archive.users WHERE Lower(EMAIL) = '" + emailLower + "' ";
        ResultSet rs = st.executeQuery(sql);
        Users user = null;
        if (rs.next()) {
            //System.out.println("after "+ rs.getString("EMAIL"));
            String id = rs.getString("ID");
            String fn = rs.getString("FIRSTNAME");
            String ln = rs.getString("LASTNAME");
            String phone = rs.getString("PHONE");
            String pw = rs.getString("PASSWORD");
            String rsemail = rs.getString("EMAIL");
            String role = rs.getString("ROLE");
            user = new Users(id, fn, ln, phone, pw, rsemail, role);
        }
        return user;
    }
    /*
       Find all customers or all users depending on who is requesting it in the database
       First check the role of the requester and if it is just a staff set sql string to customers only
       Take the results and loop through
       While looping construct a new user and add it to the arraylist
       Return the array list after the loop
    */
    public ArrayList<Users> findAllUsers(Users requester) throws SQLException {
        String sql = "SELECT * FROM archive.users";
        if (requester.getRole().equalsIgnoreCase("s")) {
            sql = "SELECT * FROM archive.users WHERE Lower(ROLE) = 'c'";
        }
        ResultSet rs = st.executeQuery(sql);
        ArrayList<Users> users = new ArrayList<>();
        while (rs.next()) {
            String id = rs.getString("ID");
            String fn = rs.getString("FIRSTNAME");
            String ln = rs.getString("LASTNAME");
            String phone = rs.getString("PHONE");
            String pw = rs.getString("PASSWORD");
            String rsemail = rs.getString("EMAIL");
            String role = rs.getString("ROLE");
            Users user = new Users(id, fn, ln, phone, pw, rsemail, role);
            users.add(user);
        }
        return users;
    }

    /* 
       Check if a student exist in the database
       Convert email to lower case
       Construct sql string and execute it
       Return true if there is a result, otherwise it returns false
    */
    public boolean checkUser(String email, String password) throws SQLException {
        String emailLower = email.toLowerCase();
        String sql = "SELECT * FROM archive.users WHERE Lower(EMAIL) = '" + emailLower + "' AND PASSWORD = '" + password + "' ";
        ResultSet rs = st.executeQuery(sql);
        return rs.next();
    }
    
    /* 
       Create a new access log in the database
       Setup date formats
       Create sql string find biggest accesslog ID in database
       Execute, take the ID and add 1 to it
       Construct new sql string to input accesslog into database and then execute it
    */
    public void addLog(String ID, String event) throws SQLException {
        DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.now();
        int rsCount = 0;
        String sqlcount = "SELECT * FROM archive.accesslog ORDER BY length(ID) DESC, ID DESC";
        ResultSet rs = st.executeQuery(sqlcount);
        if (rs.next()) {
            rsCount = Integer.parseInt(rs.getString("ID")) + 1;
        }
        //System.out.print("getID: "+rs.getString("ID"));
        //System.out.print("rsCount: "+rsCount);
        String sql = "INSERT INTO archive.accesslog VALUES('" + rsCount + "', '" + ID + "', '" + dft.format(ldt) + "', '" + event + "')";
        st.executeUpdate(sql);
    }
    
    // Delete access log from database
    // Construct sql query and execute
    public void deleteLog(String ID) throws SQLException {
        String sql = "DELETE FROM accesslog WHERE ID = '" + ID + "'";
        st.executeUpdate(sql);
    }
    
    /* 
       Find the access logs of the User
       Create date time format
       Construct sql query and execute
       While there are results from the query 
       Create a new AccessLog and add it to the accessLog array list
       return the arraylist 
    */
    public ArrayList<AccessLog> findAccessLogs(String UID) throws SQLException {
        DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sql = "SELECT * FROM archive.accesslog WHERE USERID='" + UID + "'";
        ResultSet rs = st.executeQuery(sql);
        ArrayList<AccessLog> accessLog = new ArrayList<>();
        while (rs.next()) {
            String id = rs.getString("ID");
            String uid = rs.getString("USERID");
            String datetime = rs.getString("DATETIME");
            String event = rs.getString("EVENT");
            String[] dt = datetime.split("\\.");
            //String[] dt = dt1[0].split("T");
            AccessLog al = new AccessLog(id, uid, LocalDateTime.parse(dt[0], dft), event);
            accessLog.add(al);
        }
        return accessLog;
    }

    /*
        Add the user into the database
        For a customer let the role be 'c' for customer
        The individual id of the user will increase by 1 for every new user created
        The sql is ordered by the user ID number in the database
        The user is added into the database when the sql is executed
    */
    public void addUser(String firstname, String lastname, String phone, String password, String email) throws SQLException {
        String role = "c";
        int rsCount = 0;
        String sqlcount = "SELECT * FROM archive.users ORDER BY length(ID) DESC, ID DESC";
        ResultSet rs = st.executeQuery(sqlcount);
        if (rs.next()) {
            rsCount = Integer.parseInt(rs.getString("ID")) + 1;
        }
        String sql = "INSERT INTO archive.users VALUES('" + rsCount + "', '" + firstname + "', '" + lastname + "', '" + phone + "', '" + password + "', '" + email + "', '" + role + "')";
        st.executeUpdate(sql);
    }

    /*
        Allow the staff to add any user into the database
        The individual id of the user will increase by 1 for every new user created
        The sql is ordered by the user ID number in the database
        The user is added into the database when the sql is executed
    */
    public void addStaff(String firstname, String lastname, String phone, String password, String email, String role) throws SQLException {
        int rsCount = 0;
        String sqlcount = "SELECT * FROM archive.users ORDER BY length(ID) DESC, ID DESC";
        ResultSet rs = st.executeQuery(sqlcount);
        if (rs.next()) {
            rsCount = Integer.parseInt(rs.getString("ID")) + 1;
        }
        String sql = "INSERT INTO archive.users VALUES('" + rsCount + "', '" + firstname + "', '" + lastname + "', '" + phone + "', '" + password + "', '" + email + "', '" + role + "')";
        st.executeUpdate(sql);
    }

    //update a user details in the database when the sql is executed 
    public void updateUser(String ID, String firstName, String lastName, String phone, String password, String email) throws SQLException {
        String sql = "UPDATE archive.users SET ID='" + ID + "', FIRSTNAME='" + firstName + "', LASTNAME='" + lastName + "', PHONE='" + phone + "', PASSWORD='" + password + "', EMAIL='" + email + "' WHERE ID='" + ID + "'";
        st.executeUpdate(sql);
    }

    //delete a user from the database when the sql is executed
    public void deleteUser(String ID) throws SQLException {
        String sql = "DELETE FROM archive.users WHERE ID = '" + ID + "'";
        st.executeUpdate(sql);
    }

    public String generateRandomIDNumber() throws SQLException {
        boolean isMatch = true;
        String ID = "";
        while (isMatch) {
            int tempRndID = (new Random()).nextInt(999999);
            String newID = Integer.toString(tempRndID);

            ArrayList<Orders> orders = findAllOrders();
            for (Orders order : orders) {
                if (newID != order.getID()) {
                    isMatch = false;
                    ID = newID;
                }
            }
        }
        return ID;
    }

    public void addOrder(String ID, String userID, String status, String payment, String totalCost) throws SQLException {
        DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.now();
        String sql = "INSERT INTO archive.orders VALUES('" + ID + "', '" + userID + "', '" + dft.format(ldt) + "', '" + status + "', '" + payment + "', '" + totalCost + "')";
        st.executeUpdate(sql);
    }

    public ArrayList<Orders> findAllOrders() throws SQLException {
        String sql = "SELECT * FROM archive.orders";
        ResultSet rs = st.executeQuery(sql);
        ArrayList<Orders> orders = new ArrayList<>();
        while (rs.next()) {
            String id = rs.getString("ID");
            String usersID = rs.getString("USERID");
            Date date = rs.getDate("PURCHASEDATE");
            String status = rs.getString("STATUS");
            String payment = rs.getString("PAYMENT");
            String totalCost = rs.getString("TOTALCOST");
            Orders order = new Orders(id, usersID, date, status, payment, totalCost);
            orders.add(order);
        }
        return orders;
    }

    public ArrayList<Orders> findAllUserOrders(String userID) throws SQLException {
        String sql = "SELECT * FROM archive.orders WHERE userID = '" + userID + "'";
        ResultSet rs = st.executeQuery(sql);
        ArrayList<Orders> orders = new ArrayList<>();
        while (rs.next()) {
            String id = rs.getString("ID");
            String usersID = rs.getString("USERID");
            Date date = rs.getDate("PURCHASEDATE");
            String status = rs.getString("STATUS");
            String payment = rs.getString("PAYMENT");
            String totalCost = rs.getString("TOTALCOST");
            Orders order = new Orders(id, usersID, date, status, payment, totalCost);
            orders.add(order);
        }
        return orders;
    }

    public ArrayList<Orders> findProcessingOrders() throws SQLException {
        String sql = "SELECT * FROM archive.orders WHERE STATUS = '" + "Processing" + "'";
        ResultSet rs = st.executeQuery(sql);
        ArrayList<Orders> orders = new ArrayList<>();
        while (rs.next()) {
            String id = rs.getString("ID");
            String usersID = rs.getString("USERID");
            Date date = rs.getDate("PURCHASEDATE");
            String status = rs.getString("STATUS");
            String payment = rs.getString("PAYMENT");
            String totalCost = rs.getString("TOTALCOST");
            Orders order = new Orders(id, usersID, date, status, payment, totalCost);
            orders.add(order);
        }
        return orders;
    }

    public void editOrderStatus(String orderID, String editStatus) throws SQLException {
        String sql = "UPDATE archive.orders SET STATUS='" + editStatus + "' WHERE ID='" + orderID + "'";
        st.executeUpdate(sql);
    }

    public void addMovieOrder(String orderID, String movieID, String quantity) throws SQLException {
        String sql = "INSERT INTO archive.movieorder VALUES('" + orderID + "', '" + movieID + "', '" + quantity + "')";
        st.executeUpdate(sql);
    }

    public ArrayList<MovieOrder> findAllUserMovieOrders(String orderIDSearch) throws SQLException {
        String sql = "SELECT * FROM archive.movieorder WHERE orderid = '" + orderIDSearch + "'";
        ResultSet rs = st.executeQuery(sql);
        ArrayList<MovieOrder> movieOrders = new ArrayList<>();
        while (rs.next()) {
            String orderID = rs.getString("ORDERID");
            String movieID = rs.getString("MOVIEID");
            String quantity = rs.getString("QUANTITY");
            MovieOrder movieOrder = new MovieOrder(orderID, movieID, quantity);
            movieOrders.add(movieOrder);
        }
        return movieOrders;
    }
}
