package models;

import enums.SkillRating;
import enums.Topic;

public class TopicSkill {
	private Topic topic;
    private SkillRating skillLevel; // Enum for skill ratings

    public TopicSkill(Topic topic, SkillRating skillLevel) {
        this.topic = topic;
        this.skillLevel = skillLevel;
    }

    // Getters and Setters
    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public SkillRating getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(SkillRating skillLevel) {
        this.skillLevel = skillLevel;
    }
}
