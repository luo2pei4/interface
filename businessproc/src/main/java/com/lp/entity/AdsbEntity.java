package com.lp.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "adsb")
public class AdsbEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "arr")
	private String arr;
	
	@Column(name = "dep")
	private String dep;
	
	@Column(name = "fi")
	private String fi;
	
	@Column(name = "icao24")
	private String icao24;
	
	@Column(name = "an")
	private String an;
	
	@Column(name = "alt")
	private String alt;
	
	@Column(name = "lon")
	private String lon;
	
	@Column(name = "cas")
	private String cas;
	
	@Column(name = "lat")
	private String lat;
	
	@Column(name = "vec")
	private BigDecimal vec;
	
	@Column(name = "ssr")
	private String ssr;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "sendtime")
	private LocalDateTime sendTime;
	
	@Column(name = "threadid")
	private String threadid;
	
	@Column(name = "createtime")
	private LocalDateTime createtime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getArr() {
		return arr;
	}

	public void setArr(String arr) {
		this.arr = arr;
	}

	public String getDep() {
		return dep;
	}

	public void setDep(String dep) {
		this.dep = dep;
	}

	public String getFi() {
		return fi;
	}

	public void setFi(String fi) {
		this.fi = fi;
	}

	public String getIcao24() {
		return icao24;
	}

	public void setIcao24(String icao24) {
		this.icao24 = icao24;
	}

	public String getAn() {
		return an;
	}

	public void setAn(String an) {
		this.an = an;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getCas() {
		return cas;
	}

	public void setCas(String cas) {
		this.cas = cas;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public BigDecimal getVec() {
		return vec;
	}

	public void setVec(BigDecimal vec) {
		this.vec = vec;
	}

	public String getSsr() {
		return ssr;
	}

	public void setSsr(String ssr) {
		this.ssr = ssr;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocalDateTime getSendTime() {
		return sendTime;
	}

	public void setSendTime(LocalDateTime sendTime) {
		this.sendTime = sendTime;
	}

	public String getThreadid() {
		return threadid;
	}

	public void setThreadid(String threadid) {
		this.threadid = threadid;
	}

	public LocalDateTime getCreatetime() {
		return createtime;
	}

	public void setCreatetime(LocalDateTime createtime) {
		this.createtime = createtime;
	}
	
}
