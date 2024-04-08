package MainFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelReservationSystem {

	public static void main(String[] args) {
		
		final String url = "jdbc:mysql://127.0.0.1:3306/hotel_db";
		final String username = "root";
		final String password = "Vaibhav@123";
		
		try {	
			
			while(true) {
				Connection con = DriverManager.getConnection(url, username, password);
				
				System.out.println();
				System.out.println("HOTEL MANAGEMENT SYSTEM");
				Scanner sc = new Scanner(System.in);
				System.out.println("1. Reserve a room");
				System.out.println("2. View reservations");
				System.out.println("3. Get room");
				System.out.println("4. Update reservations");
				System.out.println("5. Delete reservations");
				System.out.println("0. Exit");
				System.out.println("Choose an option: ");
				int choice = sc.nextInt();
				
				switch(choice) {
				
				case 1:
					reserveRoom(con, sc);
					break;
					
				case 2:
					viewReservations(con);
					break;
					
				case 3:
					getRoomNumber(con, sc);
					break;
					
				case 4:
					updateReservation(con, sc);
					break;
					
				case 5:
					deleteReservation(con, sc);
					break;
					
				case 0:
					exit();
					sc.close();
					return;
				
				default:
					System.out.println("Invalid choice. Please check again.");
				}
			}
			
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	private static void reserveRoom(Connection con, Scanner sc) {
		
		try {
			System.out.println("Enter guest name:");
			String guestName = sc.next();
//			System.out.println();
			
			System.out.println("Enter room number:");
			int roomNumber = sc.nextInt();
			
			System.out.println("Enter contact number:");
			String contactNumber = sc.next();
			
			String query = "INSERT INTO reservations (guest_name, room_number, contact_number) VALUES ('"+guestName+"',"+roomNumber+",'"+contactNumber+"');";
			
//			String query = INSERT INTO reservations (guest_name, room_number, contact_number)
//							VALUES
//							('Vaibhav',108,'9197789789');
			
			
			
			try(Statement stmt = con.createStatement()){
				
				int rowsAffected = stmt.executeUpdate(query);
				
				if(rowsAffected > 0) {
					System.out.println("Reservation successful");
				}else {
					System.out.println("Reservation failed");
				}
			}
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	private static void viewReservations(Connection con) throws SQLException {
		String query = 	"SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

		
		try (Statement statement = con.createStatement();
	             ResultSet resultSet = statement.executeQuery(query)) {

	            System.out.println("Current Reservations:");
	            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
	            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
	            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

	            while (resultSet.next()) {
	                int reservationId = resultSet.getInt("reservation_id");
	                String guestName = resultSet.getString("guest_name");
	                int roomNumber = resultSet.getInt("room_number");
	                String contactNumber = resultSet.getString("contact_number");
	                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

	                // Format and display the reservation data in a table-like format
	                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
	                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
	            }

	            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
	        }
	    }
	
	private static void getRoomNumber(Connection con, Scanner sc) {
		 try {
	            System.out.print("Enter reservation ID: ");
	            int reservationId = sc.nextInt();
	            System.out.print("Enter guest name: ");
	            String guestName = sc.next();

	            String sql = "SELECT room_number FROM reservations " +
	                    "WHERE reservation_id = " + reservationId +
	                    " AND guest_name = '" + guestName + "'";

	            try (Statement statement = con.createStatement();
	                 ResultSet resultSet = statement.executeQuery(sql)) {

	                if (resultSet.next()) {
	                    int roomNumber = resultSet.getInt("room_number");
	                    System.out.println("Room number for Reservation ID " + reservationId +
	                            " and Guest " + guestName + " is: " + roomNumber);
	                } else {
	                    System.out.println("Reservation not found for the given ID and guest name.");
	                }
	            }
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	}
	
	private static void updateReservation(Connection con, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine(); // Consume the newline character

            if (!reservationExists(con, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = sc.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try (Statement statement = con.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	private static void deleteReservation(Connection con, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = sc.nextInt();

            if (!reservationExists(con, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = con.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }
	
	public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
            

}
