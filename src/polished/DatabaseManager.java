import java.sql.*;
import java.time.LocalDateTime;

public class DatabaseManager {
    // --- UPDATED FOR MYSQL ---
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/college";
    // NOTE: REPLACE THESE WITH YOUR ACTUAL CREDENTIALS
    private static final String USER = "root"; 
    private static final String PASSWORD = "inJectR00t";
    // ---------------------------

    /**
     * Initializes the database connection and creates the 'highscores' table if it doesn't exist.
     */
    public static void initializeDatabase() {
        try {
            // Load the MySQL JDBC Driver
            Class.forName(DRIVER);
            
            // Connect using the MySQL URL, user, and password
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                if (conn != null) {
                    // SQL command to create table if it doesn't exist
                    String sql = "CREATE TABLE IF NOT EXISTS highscores (" +
                                 "id INT PRIMARY KEY AUTO_INCREMENT," + // Use INT and AUTO_INCREMENT for MySQL
                                 "generation INT NOT NULL," +
                                 "best_score INT NOT NULL," +
                                 "avg_fitness DOUBLE," + // Use DOUBLE for floating point numbers
                                 "date_logged VARCHAR(50) NOT NULL" +
                                 ");";
                    
                    Statement stmt = conn.createStatement();
                    stmt.execute(sql);
                    System.out.println("MySQL database 'college' initialized successfully.");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found. Ensure the JAR is in the classpath.");
        } catch (SQLException e) {
            System.err.println("Database connection/initialization error: " + e.getMessage());
        }
    }

    /**
     * Logs the key metrics of a finished generation to the database.
     */
    public static void logGenerationMetrics(int generation, int bestScore, double avgFitness) {
        String sql = "INSERT INTO highscores(generation, best_score, avg_fitness, date_logged) VALUES(?, ?, ?, ?)";
        
        try {
            // Load the MySQL JDBC Driver
            Class.forName(DRIVER); 
            
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, generation);
                pstmt.setInt(2, bestScore);
                pstmt.setDouble(3, avgFitness);
                pstmt.setString(4, LocalDateTime.now().toString());
                
                pstmt.executeUpdate();
                
            }
        } catch (ClassNotFoundException e) {
            // Error handled in initializeDatabase, but good practice to catch here too
            System.err.println("Error: MySQL JDBC Driver not found.");
        } catch (SQLException e) {
            System.err.println("Error logging metrics to DB: " + e.getMessage());
        }
    }
}