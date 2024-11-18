package models;

import java.util.ArrayList;

public class SpecialGroup {
    private int groupId;
    private String name;
    private ArrayList<String> groupMembers;
    private ArrayList<String> groupAdmins;
    private ArrayList<String> groupArticles;
    
    // Constructor for new groups
    public SpecialGroup(String name) {
        this.name = name;
        this.groupMembers = new ArrayList<>();
        this.groupAdmins = new ArrayList<>();
        this.groupArticles = new ArrayList<>();
    }
    
    // Constructor for loading existing groups
    public SpecialGroup(int groupId, String name, ArrayList<String> members, 
                       ArrayList<String> admins, ArrayList<String> articles) {
        this.groupId = groupId;
        this.name = name;
        this.groupMembers = members;
        this.groupAdmins = admins;
        this.groupArticles = articles;
    }
    
    // Getters
    public int getGroupId() {
        return groupId;
    }
    
    public String getName() {
        return name;
    }
    
    public ArrayList<String> getGroupMembers() {
        return groupMembers;
    }
    
    public ArrayList<String> getGroupAdmins() {
        return groupAdmins;
    }
    
    public ArrayList<String> getGroupArticles() {
        return groupArticles;
    }
    
    // Setters
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    // Member management
    public void addMember(String userId) {
        if (!groupMembers.contains(userId)) {
            groupMembers.add(userId);
        }
    }
    
    public void addAdmin(String userId) {
        if (!groupAdmins.contains(userId)) {
            groupAdmins.add(userId);
            if (!groupMembers.contains(userId)) {
                groupMembers.add(userId);
            }
        }
    }
    
    public void removeMember(String userId) {
        groupMembers.remove(userId);
        groupAdmins.remove(userId);
    }
    
    // Article management
    public void addArticle(String articleId) {
        if (!groupArticles.contains(articleId)) {
            groupArticles.add(articleId);
        }
    }
    
    public void removeArticle(String articleId) {
        groupArticles.remove(articleId);
    }
    
    public boolean isAdmin(String userId) {
        return groupAdmins.contains(userId);
    }
    
    public boolean isMember(String userId) {
        return groupMembers.contains(userId);
    }
}