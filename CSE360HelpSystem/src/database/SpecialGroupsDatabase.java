package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.SpecialGroup;

public class SpecialGroupsDatabase extends Database {
    private Database db;
    private Connection connection;

    public SpecialGroupsDatabase() throws Exception {
        db = Database.getInstance();
        connection = db.getConnection();
        createSpecialGroupTables();
    }

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

    // Get all groups a user has access to
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

    // Get user's access level for a specific group
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
        // Delete members first (due to foreign key)
        String deleteMembers = "DELETE FROM special_group_members WHERE group_id = ?";
        // Delete articles associations
        String deleteArticles = "DELETE FROM special_group_articles WHERE group_id = ?";
        // Delete the group itself
        String deleteGroup = "DELETE FROM special_groups WHERE group_id = ?";
        
        try (PreparedStatement pstmtMembers = connection.prepareStatement(deleteMembers);
             PreparedStatement pstmtArticles = connection.prepareStatement(deleteArticles);
             PreparedStatement pstmtGroup = connection.prepareStatement(deleteGroup)) {
            
            // Execute deletions in correct order
            pstmtMembers.setInt(1, groupId);
            pstmtMembers.executeUpdate();
            
            pstmtArticles.setInt(1, groupId);
            pstmtArticles.executeUpdate();
            
            pstmtGroup.setInt(1, groupId);
            pstmtGroup.executeUpdate();
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
}