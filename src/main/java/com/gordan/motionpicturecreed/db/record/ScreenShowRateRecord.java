package com.gordan.motionpicturecreed.db.record;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sun.istack.NotNull;

@Entity
@Table(name = "screen_show_rates", uniqueConstraints = { @UniqueConstraint(columnNames = { "screen_show_id", "rate_id" }) })
public class ScreenShowRateRecord {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "screen_show_id", referencedColumnName = "id")
	private ScreenShowRecord screenShow; 
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rate_id", referencedColumnName = "id")
	private RateRecord rate;
	
	@NotNull
	private boolean valid;
	
	@Column(name = "created_date", nullable = false, updatable = false)
	@CreationTimestamp
	private Date dateCreated; 
	
	@Column(name = "modified_date")
	@UpdateTimestamp
	private Date dateModified; 

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ScreenShowRecord getScreenShow() {
		return screenShow;
	}

	public void setScreenShow(ScreenShowRecord screenShow) {
		this.screenShow = screenShow;
	}

	public RateRecord getRate() {
		return rate;
	}

	public void setRate(RateRecord rate) {
		this.rate = rate;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}
}
