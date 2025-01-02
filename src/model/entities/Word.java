package model.entities;

import java.time.LocalDateTime;

public class Word {
	
	private Long id;
	private String word;
	private Integer learned;
	private LocalDateTime createdAt;
	
	public Word() {
	}

	public Word(Long id, String word, Integer learned, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.word = word;
		this.learned = learned;
		this.createdAt = createdAt;
	}

	public Word(Long id, String word, Integer learned) {
		this.id = id;
		this.word = word;
		this.learned = learned;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Integer getLearned() {
		return learned;
	}

	public void setLearned(Integer learned) {
		this.learned = learned;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "Word [id=" + id + ", word=" + word + ", learned=" + learned + ", createdAt=" + createdAt + "]";
	}
	
	

}
