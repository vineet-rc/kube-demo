package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@RestController
@Profile("sftp")
public class SFTPController {
	@Value("${sftp.identity.file}")
	private String identityFile;

	@Value("${sfpt.known.hosts}")
	private String knownHostsFile;

	@Value("${sfpt.user}")
	private String user;

	@Value("${sftp.host}")
	private String host;

	@GetMapping("ls")
	public String ls() {
		try {
			JSch jSch = new JSch();
			jSch.addIdentity(identityFile);
			jSch.setKnownHosts(knownHostsFile);
			Session session = jSch.getSession(user, host);
			session.connect();
			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();
			return channel.ls(channel.pwd()).toString();
		} catch (JSchException | SftpException e) {
			e.printStackTrace();
			return e.toString();
		}
	}
}
