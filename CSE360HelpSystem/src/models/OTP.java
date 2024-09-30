/**
 * 
 */
package models;

import java.time.LocalDateTime;

import javax.management.relation.Role; 


/**
 * OneTimePassword Class
 */
public class OTP {
private String OTP;
private LocalDateTime expiration;
private Role userRoles; 
private boolean flag;
/**
 * @return the oTP
 */
public String getOTP() {
	return OTP;
}

/**
 * @param oTP the oTP to set
 */
public void setOTP(String oTP) {
	OTP = oTP;
}

/**
 * @return the expiration
 */
public LocalDateTime getExpiration() {
	return expiration;
}

/**
 * @param expiration the expiration to set
 */
public void setExpiration(LocalDateTime expiration) {
	this.expiration = expiration;
}

/**
 * @return the userRoles
 */
public Role getUserRoles() {
	return userRoles;
}

/**
 * @param userRoles the userRoles to set
 */
public void setUserRoles(Role userRoles) {
	this.userRoles = userRoles;
}

/**
 * @return the flag
 */
public boolean checkFlag() {
	return flag;
}

/**
 * @param flag the flag to set
 */
public void setFlag(boolean flag) {
	this.flag = flag;
}
public void generateOTP() {
	
}
public void sendOTP(User user) {
	
}
public boolean validateOTP(String otp) {
	return true;
	
}

}
