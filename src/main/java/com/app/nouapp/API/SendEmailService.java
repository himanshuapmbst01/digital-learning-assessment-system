package com.app.nouapp.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendEmailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public void SendEmail(String name, String email)
	{
		String subject = "Welcome to NOU e-Gyan Portal";
		String message = "Hello Dear, "+name+"\nYour Registration is Successful on NOU e-Gyan Portal.\nNow you can login through your creadentials.\n\nThank You 🙂\nManagement Team ✨🙏";
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(email);
		msg.setSubject(subject);
		msg.setText(message);
		mailSender.send(msg);
	}
	
	public void SendTestId(String name, long testid, String testname, String starttime, String mailTo)
	{
		String subject = "Test Details From NOU";
		String message = "Hello Dear, "+name+"\nWe have been scheduled a test for you\nPlease Login on Portal by using your credentials.\nYou can access test through the test Id\n\nTest ID = "+testid+"\nTest Name = "+testname+"\nTest Start Date & Time = "+starttime+"\n\n\nThank You,\nTeam Admin";
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(mailTo);
		msg.setSubject(subject);
		msg.setText(message);
		mailSender.send(msg);
	}	
}
