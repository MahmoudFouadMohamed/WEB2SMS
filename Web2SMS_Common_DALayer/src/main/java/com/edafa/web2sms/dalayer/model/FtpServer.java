package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author loay
 */
@Entity
@Table(name = "FTP_SERVER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FtpServer.findAll", query = "SELECT f FROM FtpServer f")
    , @NamedQuery(name = "FtpServer.findByRealIp", query = "SELECT f FROM FtpServer f WHERE f.realIp = :realIp")
    , @NamedQuery(name = "FtpServer.findByUserName", query = "SELECT f FROM FtpServer f WHERE f.userName = :userName")
    , @NamedQuery(name = "FtpServer.findByPassWord", query = "SELECT f FROM FtpServer f WHERE f.passWord = :passWord")
    , @NamedQuery(name = "FtpServer.findByServerName", query = "SELECT f FROM FtpServer f WHERE f.serverName = :serverName")
    , @NamedQuery(name = "FtpServer.findByServerId", query = "SELECT f FROM FtpServer f WHERE f.serverId = :serverId")
    , @NamedQuery(name = "FtpServer.findByFtpPort", query = "SELECT f FROM FtpServer f WHERE f.ftpPort = :ftpPort")
    , @NamedQuery(name = "FtpServer.findByPassLength", query = "SELECT f FROM FtpServer f WHERE f.passLength = :passLength")
    , @NamedQuery(name = "FtpServer.findByUserLength", query = "SELECT f FROM FtpServer f WHERE f.userLength = :userLength")})
public class FtpServer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "REAL_IP")
    private String realIp;
    @Size(max = 50)
    @Column(name = "USER_NAME")
    private String userName;
    @Size(max = 50)
    @Column(name = "PASS_WORD")
    private String passWord;
    @Size(max = 50)
    @Column(name = "SERVER_NAME")
    private String serverName;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "SERVER_ID")
    private Integer serverId;
    @Column(name = "FTP_PORT")
    private Integer ftpPort;
    @Column(name = "PASS_LENGTH")
    private Integer passLength;
    @Column(name = "USER_LENGTH")
    private Integer userLength;

    public FtpServer() {
    }

    public FtpServer(Integer serverId) {
        this.serverId = serverId;
    }

    public FtpServer(Integer serverId, String realIp) {
        this.serverId = serverId;
        this.realIp = realIp;
    }

    public String getRealIp() {
        return realIp;
    }

    public void setRealIp(String realIp) {
        this.realIp = realIp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public Integer getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(Integer ftpPort) {
        this.ftpPort = ftpPort;
    }

    public Integer getPassLength() {
        return passLength;
    }

    public void setPassLength(Integer passLength) {
        this.passLength = passLength;
    }

    public Integer getUserLength() {
        return userLength;
    }

    public void setUserLength(Integer userLength) {
        this.userLength = userLength;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serverId != null ? serverId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FtpServer)) {
            return false;
        }
        FtpServer other = (FtpServer) object;
        if ((this.serverId == null && other.serverId != null) || (this.serverId != null && !this.serverId.equals(other.serverId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FtpServer{" + "realIp=" + realIp + ", userName=" + userName + ", passWord=" + passWord + ", serverName=" + serverName + ", serverId=" + serverId + ", ftpPort=" + ftpPort + ", passLength=" + passLength + ", userLength=" + userLength + '}';
    }
    
}
