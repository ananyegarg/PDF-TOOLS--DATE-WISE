package com.codeplanet.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.PRINT_SCALING;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.codeplanet.model.User;
import com.codeplanet.model.UserFile;
import com.codeplanet.service.HomeService;

@Controller
public class HomeController {
	@Autowired
	HomeService homeService;

	@RequestMapping(value = "/")
	public String home() throws SQLException {
		System.out.println("controller called");
		return "Home";
	}

	@RequestMapping(value = "/merge")
	public String merge() {
		System.out.println("controller called");
		return "Mergepdf";
	}

	@RequestMapping(value = "/unlock")
	public String ProtectPdf() {
		return "unlockpdf";
	}

	@RequestMapping(value = "/mergePdf", method = RequestMethod.POST)
	public String test(HttpServletRequest req, UserFile user, HttpServletResponse res)
			throws SQLException, IOException {
		System.out.println("Anany 1");
		String s = user.getName();
		ArrayList<String> filePath = uploadFileOnServer(user);
		System.out.println("Anany 2");
		merger(filePath, req, res);
		System.out.println(" test controller called " + s);

		return "downloadpdf";
	}

	private void merger(ArrayList<String> filePath, HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		// TODO Auto-generated method stub
		PDFMergerUtility pdf = new PDFMergerUtility();
		pdf.setDestinationFileName("E:\\download.pdf");

		for (String s : filePath) {
			File f = new File(s);
			pdf.addSource(f);
		}
		req.setAttribute("file", "E:\\download.pdf");

		pdf.mergeDocuments(null);
		System.out.println("pdf merged");
	}

	private ArrayList<String> uploadFileOnServer(UserFile user) {
		// TODO Auto-generated method stub
		String rootdirectory = "E:/files/merge";
		File directory = new File(rootdirectory);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		MultipartFile[] f = user.getUserfiles();
		String filepath = null;
		ArrayList<String> list = new ArrayList<String>();
		for (MultipartFile filedata : f) {
			String filename = filedata.getOriginalFilename();
			if (filename != null && filename.length() > 0) {
				try {
					filepath = directory.getCanonicalPath() + File.separator + filename;
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filepath));
					bos.write(filedata.getBytes());
					bos.close();
					list.add(filepath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public String download(HttpServletRequest req, HttpServletResponse res) throws IOException {
		System.out.println("Download is here");
		String mimeType = null;
		String file = req.getParameter("filepath");
		System.out.println("Download is here2");
		File f = new File(file);
		mimeType = getMimeType(f.getCanonicalPath());
		res.setContentType(mimeType);
		res.setHeader("Content-Disposition", "attachement;filename=\"" + f.getName() + "\"");
		res.setContentLength((int) f.length());
		InputStream is = new FileInputStream(f);
		ServletOutputStream out = res.getOutputStream();
		org.apache.commons.io.IOUtils.copy(is, out);
		out.flush();
		out.close();
		is.close();
		return "downloadpdf";

	}

	private String getMimeType(String canonicalPath) {
		// TODO Auto-generated method stub
		canonicalPath = canonicalPath.toLowerCase();
		if (canonicalPath.endsWith(".jpg") || canonicalPath.endsWith(".jpeg") || canonicalPath.endsWith(".jpe"))
			return "image/jpeg";
		else if (canonicalPath.endsWith(".pdf"))
			return "application/pdf";
		else
			return "application/pdf";
	}

	@RequestMapping(value = "/splitPdf")
	public String split() {
		return "splitpdf";
	}

	@RequestMapping(value = "/split", produces = "application/zip", method = RequestMethod.POST)
	public String splitPdf(HttpServletResponse res, HttpServletRequest req, UserFile user) throws IOException {
		ArrayList<String> filepath = uploadFileOnServer(user);
		splitter(filepath, req, res);
		return "downloadpdf";
	}

	public void splitter(ArrayList<String> filepath, HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		File file = new File(filepath.get(0));
		PDDocument pd = PDDocument.load(file);
		Splitter sp = new Splitter();
		List<PDDocument> pd1 = sp.split(pd);
		Iterator<PDDocument> it = pd1.listIterator();
		int i = 1;
		List<String> filepaths = new ArrayList<String>();
		System.out.println("loop outer call");
		while (it.hasNext()) {
			String x = "E:/files/merge/split" + i + ".pdf";
			File f = new File(x);
			PDDocument pd2 = it.next();
			pd2.save(f);
			i++;
			filepaths.add(x);
			System.out.println("loop call" + i);
		}
		pd.close();

		zipFiles(filepaths, res, "spliterzip");

	}

	public String zipFiles(List<String> filepaths, HttpServletResponse res, String zipFormateName) throws IOException {
		String zipFileName = "E:/" + zipFormateName + ".zip";
		FileOutputStream fos = new FileOutputStream(zipFileName);
		ZipOutputStream zos = new ZipOutputStream(fos);
		for (String s : filepaths) {
			zos.putNextEntry(new ZipEntry(new File(s).getName()));
			byte[] bytes = Files.readAllBytes(Paths.get(s));
			zos.write(bytes);
			zos.closeEntry();
		}
		zos.close();
		File f = new File(zipFileName);
		res.setContentType("application/zip");
		res.setHeader("Content-Disposition", "attachement;filename=\"" + f.getName() + "\"");
		res.setContentLength((int) f.length());
		InputStream is = new FileInputStream(f);
		ServletOutputStream out = res.getOutputStream();
		IOUtils.copy(is, out);
		is.close();
		out.flush();
		out.close();
		return "downloadpdf";

	}

	@RequestMapping(value = "/extract")
	public String extractimage() {
		return "pdftotext";
	}

	@RequestMapping(value = "/extractData")
	public String extractData(UserFile user, HttpServletRequest req) throws IOException {
		ArrayList<String> filepath = uploadFileOnServer(user);
		File file = new File(filepath.get(0));
		PDDocument pd = PDDocument.load(file);
		PDFTextStripper pdf = new PDFTextStripper();
		String s = pdf.getText(pd);
		req.setAttribute("data", s);
		System.out.println(s);
		pd.close();
		return "extractjsptext";

	}

	@RequestMapping(value = "/removePage")
	public String removePage() throws IOException {
		File file = new File("C:\\Users\\Anany\\Downloads\\Anany.pdf");
		PDDocument pd = PDDocument.load(file);

		int totalpage = pd.getNumberOfPages();
		System.out.println(totalpage);
		pd.removePage(2);
		pd.save("C:\\Users\\Anany\\Downloads\\Anany.pdf");
		pd.close();
		return "Home";
	}

	
	  @RequestMapping(value="/pdftoImg") public String pdftoimage() { 
		  
		  
		  return  "pdftoimg"; 
		  }
	  
	  
	  
	  @RequestMapping(value="/pdftoImage") public String pdftoImage(UserFile
	  user,HttpServletRequest req) throws InvalidPasswordException, IOException {
	  ArrayList<String> filepath=uploadFileOnServer(user); File file=new
	  File(filepath.get(0)); PDDocument pd=PDDocument.load(file); PDFRenderer
	  re=new PDFRenderer(pd);
	  
	  int totalpage=pd.getNumberOfPages();
	  
	  int i=0; while(i<totalpage) { BufferedImage img = re.renderImage(i);
	  ImageIO.write(img,"JPEG",new File("E:/files/image"+i+".jpg")); i++; }
	  
	  pd.close();
	  
	  return "downloadpdf"; }
	 

 @RequestMapping(value="/protectpdf") public String pdfprotection() { 
		  
		  
		  return  "lockpdf"; 
		  }

		@RequestMapping(value = "/protect")
		public String pdfprotection(UserFile user,HttpServletRequest req) throws InvalidPasswordException, IOException {
		 ArrayList<String> filepath=uploadFileOnServer(user); 
		 File file=new File(filepath.get(0)); PDDocument pd=PDDocument.load(file); 
		 PDFRenderer re=new PDFRenderer(pd);
		AccessPermission ap = new AccessPermission();
		StandardProtectionPolicy policy = new StandardProtectionPolicy("anany@123", "anany@123", ap);
		policy.setEncryptionKeyLength(128);
		pd.protect(policy);
		pd.save("E:\\Anany.pdf");
		pd.close();
		return "downloadpdf";
	}

	/*
	 * @RequestMapping(value="/unlockPdf", method=RequestMethod.POST) public String
	 * unlock(HttpServletRequest req,@ModelAttribute FileUpload user) throws
	 * SQLException, IOException { ArrayList<String>
	 * filePath=uploadFileOnServerUnlock(user); unlocker(filePath,req,user);
	 * System.out.println(" test controller called "); return "downloadpdf"; }
	 * 
	 * public void unlocker(ArrayList<String> filePath, HttpServletRequest
	 * req,@ModelAttribute FileUpload user) throws InvalidPasswordException,
	 * IOException { // TODO Auto-generated method stub File file=new
	 * File(filePath.get(0)); PDDocument
	 * pd=PDDocument.load(file,user.getPassword());
	 * pd.setAllSecurityToBeRemoved(true); pd.save("E:\\download.pdf");
	 * req.setAttribute("file", "E:\\download.pdf"); pd.close(); }
	 * 
	 * private ArrayList<String> uploadFileOnServerUnlock(FileUpload user) { // TODO
	 * Auto-generated method stub String rootdirectory="E:/files/Unlock"; File
	 * directory=new File(rootdirectory); if(!directory.exists()) {
	 * directory.mkdirs(); } MultipartFile[] f=user.getUserfiles(); String
	 * filepath=null; ArrayList<String> list = new ArrayList<String>();
	 * for(MultipartFile filedata:f){ String
	 * filename=filedata.getOriginalFilename(); if(filename != null &&
	 * filename.length() > 0) { try {
	 * filepath=directory.getCanonicalPath()+File.separator+filename;
	 * BufferedOutputStream bos=new BufferedOutputStream(new
	 * FileOutputStream(filepath)); bos.write(filedata.getBytes()); bos.close();
	 * list.add(filepath); } catch(Exception e) { e.printStackTrace(); } } } return
	 * list; }
	 */
	/*
	 * @RequestMapping(value="/mail") public String Mail() throws AddressException,
	 * MessagingException { Properties p = new Properties(); p.put("mail.smtp.host",
	 * "smtp.gmail.com"); p.put("mail.smtp.auth", "true");
	 * p.put("mail.smtp.starttls.enable", "true");
	 * p.put("mail.smtp.EnableSSL.enable", "true");
	 * p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	 * p.put("mail.smtp.socketFactory.fallback", "false"); p.put("mail.smtp.port",
	 * "323"); p.put("mail.smtp.socketFactory.port", "323");
	 * 
	 * Session session=Session.getInstance(p, new
	 * SimpleAuthenticator("ananyeagarg@gmail.com","8696954479")); Message msg=new
	 * MimeMessage(session); msg.setFrom(new
	 * InternetAddress("ananyeagarg@gmail.com"));
	 * msg.setRecipient(Message.RecipientType.TO, new
	 * InternetAddress("ananyrocks27@gmail.com"));
	 * msg.setSubject("email sending application");
	 * msg.setContent("Hello,this is my first email","text/html");
	 * Transport.send(msg); return "Home"; }
	 */
}