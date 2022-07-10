package com.tiger.employees.balance.vo;

// Generated Oct 14, 2014 4:13:47 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

/**
 * Balance generated by hbm2java
 */
public class BalanceVo implements java.io.Serializable {

	private String id;
	private String orgId;
	private String settleTime;
	private String subjectId;
	private String subjectCode;
	private String subjectName;
	private BigDecimal initMonDebit;
	private BigDecimal initMonCredit;
	private BigDecimal difference;
	private BigDecimal currentMonDebit;
	private BigDecimal currentMonCredit;
	private BigDecimal cumulativeDebit;
	private BigDecimal cumulativeCredit;
	private BigDecimal finalMonDebit;
	private BigDecimal finalMonCredit;
	private String updateTime;
	private String comment;

	public BalanceVo() {
	}

	public BalanceVo(String id, BigDecimal initMonDebit,
			BigDecimal initMonCredit, BigDecimal difference,
			BigDecimal currentMonDebit, BigDecimal currentMonCredit,
			BigDecimal cumulativeDebit, BigDecimal cumulativeCredit,
			BigDecimal finalMonDebit, BigDecimal finalMonCredit) {
		this.id = id;
		this.initMonDebit = initMonDebit;
		this.initMonCredit = initMonCredit;
		this.difference = difference;
		this.currentMonDebit = currentMonDebit;
		this.currentMonCredit = currentMonCredit;
		this.cumulativeDebit = cumulativeDebit;
		this.cumulativeCredit = cumulativeCredit;
		this.finalMonDebit = finalMonDebit;
		this.finalMonCredit = finalMonCredit;
	}

	public BalanceVo(String id, String orgId, String settleTime,
			String subjectId, String subjectCode, String subjectName,
			BigDecimal initMonDebit, BigDecimal initMonCredit,
			BigDecimal difference, BigDecimal currentMonDebit,
			BigDecimal currentMonCredit, BigDecimal cumulativeDebit,
			BigDecimal cumulativeCredit, BigDecimal finalMonDebit,
			BigDecimal finalMonCredit, String updateTime, String comment) {
		this.id = id;
		this.orgId = orgId;
		this.settleTime = settleTime;
		this.subjectId = subjectId;
		this.subjectCode = subjectCode;
		this.subjectName = subjectName;
		this.initMonDebit = initMonDebit;
		this.initMonCredit = initMonCredit;
		this.difference = difference;
		this.currentMonDebit = currentMonDebit;
		this.currentMonCredit = currentMonCredit;
		this.cumulativeDebit = cumulativeDebit;
		this.cumulativeCredit = cumulativeCredit;
		this.finalMonDebit = finalMonDebit;
		this.finalMonCredit = finalMonCredit;
		this.updateTime = updateTime;
		this.comment = comment;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrgId() {
		return this.orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getSettleTime() {
		return this.settleTime;
	}

	public void setSettleTime(String settleTime) {
		this.settleTime = settleTime;
	}

	public String getSubjectId() {
		return this.subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectCode() {
		return this.subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getSubjectName() {
		return this.subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public BigDecimal getInitMonDebit() {
		return this.initMonDebit;
	}

	public void setInitMonDebit(BigDecimal initMonDebit) {
		this.initMonDebit = initMonDebit;
	}

	public BigDecimal getInitMonCredit() {
		return this.initMonCredit;
	}

	public void setInitMonCredit(BigDecimal initMonCredit) {
		this.initMonCredit = initMonCredit;
	}

	public BigDecimal getDifference() {
		return this.difference;
	}

	public void setDifference(BigDecimal difference) {
		this.difference = difference;
	}

	public BigDecimal getCurrentMonDebit() {
		return this.currentMonDebit;
	}

	public void setCurrentMonDebit(BigDecimal currentMonDebit) {
		this.currentMonDebit = currentMonDebit;
	}

	public BigDecimal getCurrentMonCredit() {
		return this.currentMonCredit;
	}

	public void setCurrentMonCredit(BigDecimal currentMonCredit) {
		this.currentMonCredit = currentMonCredit;
	}

	public BigDecimal getCumulativeDebit() {
		return this.cumulativeDebit;
	}

	public void setCumulativeDebit(BigDecimal cumulativeDebit) {
		this.cumulativeDebit = cumulativeDebit;
	}

	public BigDecimal getCumulativeCredit() {
		return this.cumulativeCredit;
	}

	public void setCumulativeCredit(BigDecimal cumulativeCredit) {
		this.cumulativeCredit = cumulativeCredit;
	}

	public BigDecimal getFinalMonDebit() {
		return this.finalMonDebit;
	}

	public void setFinalMonDebit(BigDecimal finalMonDebit) {
		this.finalMonDebit = finalMonDebit;
	}

	public BigDecimal getFinalMonCredit() {
		return this.finalMonCredit;
	}

	public void setFinalMonCredit(BigDecimal finalMonCredit) {
		this.finalMonCredit = finalMonCredit;
	}

	public String getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}