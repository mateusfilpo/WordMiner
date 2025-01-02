package model.entities;

import java.time.LocalDateTime;

public class Meaning {

    private Long id;
    private Long wordId;
    private String meaning;
    private Integer quantity;
    private Integer learned;
    private LocalDateTime createdAt;
    
    public Meaning() {
    }

	public Meaning(Long id, Long wordId, String meaning, Integer quantity, Integer learned, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.wordId = wordId;
		this.meaning = meaning;
		this.quantity = quantity;
		this.learned = learned;
		this.createdAt = createdAt;
	}

	public Meaning(Long id, String meaning, Integer learned) {
		this.id = id;
		this.meaning = meaning;
		this.learned = learned;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getMeaning() {
		return meaning;
	}

	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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
		return "Meaning [id=" + id + ", wordId=" + wordId + ", meaning=" + meaning + ", quantity=" + quantity
				+ ", learned=" + learned + ", createdAt=" + createdAt + "]";
	}
}