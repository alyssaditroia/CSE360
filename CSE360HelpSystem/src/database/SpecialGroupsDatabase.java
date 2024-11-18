package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.SpecialGroup;
import models.User;
/**
 * The {@code SpecialGroupsDatabase} class handles all special group related users, articles, and identifiers
 * Special group articles and regular articles are encrypted using the parent Database class
 */
public class SpecialGroupsDatabase extends Database {
    private Database db;
    private Connection connection;

    /**
     * Constructor
     * @throws Exception
     */
    public SpecialGroupsDatabase() throws Exception {
        db = Database.getInstance();
        connection = db.getConnection();
        createSpecialGroupTables();
    }
    /**
     * createSpecialGroupTables
     * Creates special group tables if they do not exist
     * @throws SQLException
     */
    private void createSpecialGroupTables() throws SQLException {
        // Main table for special groups
        String groupsTable = "CREATE TABLE IF NOT EXISTS special_groups ("
            + "group_id INT AUTO_INCREMENT PRIMARY KEY, "
            + "group_name VARCHAR(255) UNIQUE NOT NULL)";

        // Table for group members with access level
        String membersTable = "CREATE TABLE IF NOT EXISTS special_group_members ("
        	    + "group_id INT, "
        	    + "user_id INT, "  
        	    + "access_level INT DEFAULT 1, "
        	    + "PRIMARY KEY (group_id, user_id))";

        // Table for group articles
        String articlesTable = "CREATE TABLE IF NOT EXISTS special_group_articles ("
        	    + "group_id INT, "
        	    + "article_id INT, "  
        	    + "PRIMARY KEY (group_id, article_id))";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(groupsTable);
            stmt.execute(membersTable);
            stmt.execute(articlesTable);
        }
    }

    /**
     * getUserGroups method
     * Gets all groups that a user has access to
     * @param userId
     * @return special groups the user has access to
     * @throws SQLException
     */
    public List<String[]> getUserGroups(String userId) throws SQLException {
        List<String[]> accessibleGroups = new ArrayList<>();
        
        String sql = "SELECT sg.group_id, sg.group_name, sgm.access_level " +
                    "FROM special_groups sg " +
                    "JOIN special_group_members sgm ON sg.group_id = sgm.group_id " +
                    "WHERE sgm.user_id = ?";
        
        System.out.println("\n=== Executing getUserGroups Query ===");
        System.out.println("SQL Query: " + sql);
        System.out.println("Searching for User ID: " + userId);
                  
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] groupInfo = new String[3];
                    groupInfo[0] = rs.getString("group_id");
                    groupInfo[1] = rs.getString("group_name");
                    groupInfo[2] = rs.getString("access_level");
                    accessibleGroups.add(groupInfo);
                    
                    System.out.println("Found group: ID=" + groupInfo[0] + 
                                     ", Name=" + groupInfo[1] + 
                                     ", Access=" + groupInfo[2]);
                }
            }
        }
        System.out.println("Total groups found: " + accessibleGroups.size());
        System.out.println("=================================\n");
        return accessibleGroups;
    }

    /**
     * getUserAccessLevel
     * Get user's access level for a specific group
     * @param userId
     * @param groupId
     * @return 0 for no access or access level (numeric value)
     * @throws SQLException
     */
    public int getUserAccessLevel(String userId, int groupId) throws SQLException {
        String sql = "SELECT access_level FROM special_group_members "
                  + "WHERE user_id = ? AND group_id = ?";
                  
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, groupId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("access_level");
                }
            }
        }
        return 0; // Return 0 if user has no access
    }
    
    
    /**
     * Creates a new special group
     * @param groupName the name of the group to create
     * @return the ID of the newly created group
     * @throws SQLException if database operation fails
     */
    public int createSpecialGroup(String groupName) throws SQLException {
        String sql = "INSERT INTO special_groups (group_name) VALUES (?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, groupName);
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Failed to get ID of created group");
            }
        }
    }

    
    /**
     * Deletes a special group and all associated records
     * @param groupId the ID of the group to delete
     * @throws SQLException if database operation fails
     */
    public void deleteSpecialGroup(int groupId) throws SQLException {
        // First query deletes all articles that are in this special group
        // Changed articleId parsing to ensure proper integer comparison
        String deleteArticlesSql = "DELETE FROM articles WHERE id IN " + "(SELECT CAST(article_id AS INT) FROM special_group_articles WHERE group_id = ?)";
        
        // These queries clean up all group associations and the group itself                         
        String[] deleteQueries = {
            deleteArticlesSql,                                            // Deletes the actual articles
            "DELETE FROM special_group_members WHERE group_id = ?",       // Removes all user associations
            "DELETE FROM special_group_articles WHERE group_id = ?",      // Removes article associations
            "DELETE FROM special_groups WHERE group_id = ?"               // Deletes the group itself
        };
        
        for (String sql : deleteQueries) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, groupId);	
                pstmt.executeUpdate();
            }
        }
    }
    
    
    /**
     * Adds a user to a special group with specified access level
     * Access levels: 1=view, 2=create/delete, 3=full access
     * @param groupId ID of the special group
     * @param userId user's ID to add
     * @param accessLevel permission level for the user
     * @throws SQLException if database operation fails
     */
    public void addUserToGroup(int groupId, String userId, int accessLevel) throws SQLException {
        String sql = "INSERT INTO special_group_members (group_id, user_id, access_level) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setString(2, userId);
            pstmt.setInt(3, accessLevel);
            pstmt.executeUpdate();
        }
    }

    
    /**
     * Removes a user from a special group
     * @param groupId ID of the special group
     * @param userId user's ID to remove
     * @throws SQLException if database operation fails
     */
    public void removeUserFromGroup(int groupId, String userId) throws SQLException {
        String sql = "DELETE FROM special_group_members WHERE group_id = ? AND user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        }
    }
    
    
    /**
     * Adds an article to a special group
     * @param groupId ID of the special group
     * @param articleId ID of the article to add
     * @throws SQLException if database operation fails
     */
    public void addArticleToGroup(int groupId, String articleId) throws SQLException {
        String sql = "INSERT INTO special_group_articles (group_id, article_id) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setString(2, articleId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Removes an article from a special group
     * @param groupId ID of the special group
     * @param articleId ID of the article to remove
     * @throws SQLException if database operation fails
     */
    public void removeArticleFromGroup(int groupId, String articleId) throws SQLException {
        String sql = "DELETE FROM special_group_articles WHERE group_id = ? AND article_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setString(2, articleId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Gets all articles in a special group
     * @param groupId ID of the special group
     * @return List of article IDs in the group
     * @throws SQLException if database operation fails
     */
    public List<String> getGroupArticles(int groupId) throws SQLException {
        List<String> articles = new ArrayList<>();
        String sql = "SELECT article_id FROM special_group_articles WHERE group_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(rs.getString("article_id"));
                }
            }
        }
        return articles;
    }

    public List<User> getGroupUsersByAccessLevel(int groupId, int accessLevel) throws SQLException {
        List<User> users = new ArrayList<>();
        // Debug print
        System.out.println("Fetching users for group " + groupId + " with access level " + accessLevel);
        
        String sql = "SELECT u.* FROM cse360users u " +
                     "INNER JOIN special_group_members sgm ON u.id = sgm.user_id " +
                     "WHERE sgm.group_id = ? AND sgm.access_level = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, accessLevel);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("preferredName"),
                        rs.getString("email"),
                        rs.getString("username"),
                        null,  // password not needed
                        rs.getBoolean("isAdmin"),
                        rs.getBoolean("isStudent"),
                        rs.getBoolean("isInstructor")
                    );
                    user.setId(rs.getInt("id"));
                    users.add(user);
                    // Debug print
                    System.out.println("Found user: " + user.getUsername() + " (ID: " + user.getId() + ")");
                }
            }
        }
        // Debug print
        System.out.println("Total users found: " + users.size());
        return users;
    }

    /**
     * 
     * @param groupId
     * @return userID of users that belong to a specific special group
     * @throws SQLException
     */
    public List<User> getAvailableUsers(int groupId) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM cse360users u WHERE u.id NOT IN " +
                     "(SELECT user_id FROM special_group_members WHERE group_id = ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("preferredName"),
                        rs.getString("email"),
                        rs.getString("username"),
                        null,  // password not needed
                        rs.getBoolean("isAdmin"),
                        rs.getBoolean("isStudent"),
                        rs.getBoolean("isInstructor")
                    );
                    user.setId(rs.getInt("id"));
                    users.add(user);
                }
            }
        }
        return users;
    }

    /**
     * Updates access(permissions) for user in a special group
     * @param groupId
     * @param userId
     * @param newAccessLevel
     * @throws SQLException
     */
    public void updateUserAccess(int groupId, int userId, int newAccessLevel) throws SQLException {
        String sql = "UPDATE special_group_members SET access_level = ? " +
                     "WHERE group_id = ? AND user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, newAccessLevel);
            pstmt.setInt(2, groupId);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Displays group members for special group in the console
     * @param groupId
     * @throws SQLException
     */
    public void printGroupMembers(int groupId) throws SQLException {
        String sql = "SELECT user_id, access_level FROM special_group_members WHERE group_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("\n=== Current Group Members ===");
            System.out.println("Group ID: " + groupId);
            while (rs.next()) {
                System.out.println("User ID: " + rs.getInt("user_id") + 
                                 ", Access Level: " + rs.getInt("access_level"));
            }
            System.out.println("=========================\n");
        }
    }
}