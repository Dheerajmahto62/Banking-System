import java.sql.Connection;
import java.sql.*;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.Scanner;

public class AccountManager {
    private Connection conn;
    private Scanner sc;

    public AccountManager(Connection conn , Scanner sc){
        this.conn = conn;
        this.sc = sc;
    }
    public void credit_money(long account_number) throws SQLException{
        sc.nextLine();
        System.out.print("Enter Amount : ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = sc.nextLine();

        try{
            conn.setAutoCommit(false);
            if(account_number!= 0){
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM account WHERE account_number = ? and security_pin = ?");

                preparedStatement.setLong(1,account_number);
                preparedStatement.setString(2,security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    String credit_query = "UPDATE account SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement preparedStatement1 = conn.prepareStatement(credit_query);
                    preparedStatement1.setDouble(1,amount);
                    preparedStatement1.setLong(2,account_number);
                    int rowAffected = preparedStatement1.executeUpdate();

                    if(rowAffected>0){
                        System.out.println("Rs." + amount + "credited Successfully");
                        conn.commit();
                        conn.setAutoCommit(true);
                        return;
                    } else {
                        System.out.println("Transaction Failed!");
                        conn.rollback();
                        conn.setAutoCommit(true);
                    }
                }
            } else {
                System.out.println("Invalid Security Pin!");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        conn.setAutoCommit(true);
    }

    public void debit_money(long account_number) throws SQLException{
        sc.nextLine();
        System.out.print("Enter Amount :");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = sc.nextLine();

        try{
            conn.setAutoCommit(false);

            if(account_number != 0){
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM account WHERE account_number = ? and security_pin = ?");
                preparedStatement.setDouble(1,account_number);
                preparedStatement.setString(2,security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    double current_balance = resultSet.getDouble("balance");
                    if(amount<= current_balance){
                        String debit_query = "UPDATE account SET balance = balance- ? and WHERE account_number = ?";
                        PreparedStatement preparedStatement1 = conn.prepareStatement(debit_query);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, account_number);

                        int rowAffected = preparedStatement1.executeUpdate();
                        if(rowAffected > 0){
                            System.out.println("Rs." + amount + " Debited Successfully");
                            conn.commit();
                            conn.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed! ");
                            conn.rollback();
                            conn.setAutoCommit(true);
                        }
                    } else {
                        System.out.println("Insufficient Balance! ");
                    }
                } else {
                    System.out.println("Invalid Pin!");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        conn.setAutoCommit(true);
    }

    public void transfer_money(long sender_account_number) throws SQLException {
        sc.nextLine();
        System.out.print("Enter Receiver Account Number: ");
        long receiver_account_number = sc.nextLong();
        System.out.print("Enter Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = sc.nextLine();
        try{
            conn.setAutoCommit(false);
            if(sender_account_number!=0 && receiver_account_number!=0){
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM account WHERE account_number = ? AND security_pin = ? ");
                preparedStatement.setLong(1, sender_account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount<=current_balance){

                        // Write debit and credit queries
                        String debit_query = "UPDATE account SET balance = balance - ? WHERE account_number = ?";
                        String credit_query = "UPDATE account SET balance = balance + ? WHERE account_number = ?";

                        // Debit and Credit prepared Statements
                        PreparedStatement creditPreparedStatement = conn.prepareStatement(credit_query);
                        PreparedStatement debitPreparedStatement = conn.prepareStatement(debit_query);

                        // Set Values for debit and credit prepared statements
                        creditPreparedStatement.setDouble(1, amount);
                        creditPreparedStatement.setLong(2, receiver_account_number);
                        debitPreparedStatement.setDouble(1, amount);
                        debitPreparedStatement.setLong(2, sender_account_number);
                        int rowsAffected1 = debitPreparedStatement.executeUpdate();
                        int rowsAffected2 = creditPreparedStatement.executeUpdate();
                        if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                            System.out.println("Transaction Successful!");
                            System.out.println("Rs."+amount+" Transferred Successfully");
                            conn.commit();
                            conn.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed");
                            conn.rollback();
                            conn.setAutoCommit(true);
                        }
                    }else{
                        System.out.println("Insufficient Balance!");
                    }
                }else{
                    System.out.println("Invalid Security Pin!");
                }
            }else{
                System.out.println("Invalid account number");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        conn.setAutoCommit(true);
    }

    public void getBalance(long account_number){
        sc.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = sc.nextLine();
        try{
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT balance FROM account WHERE account_number = ? AND security_pin = ?");
            preparedStatement.setLong(1, account_number);
            preparedStatement.setString(2, security_pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                double balance = resultSet.getDouble("balance");
                System.out.println("Balance: "+balance);
            }else{
                System.out.println("Invalid Pin!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }




}
