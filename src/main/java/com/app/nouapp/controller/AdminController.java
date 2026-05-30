package com.app.nouapp.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.nouapp.API.SendEmailService;
import com.app.nouapp.dto.QuestionBankDto;
import com.app.nouapp.dto.StudyMaterialDto;
import com.app.nouapp.dto.TestInfoDto;
import com.app.nouapp.model.AdminInfo;
import com.app.nouapp.model.Enquiry;
import com.app.nouapp.model.QuestionBank;
import com.app.nouapp.model.Response;
import com.app.nouapp.model.StudentInfo;
import com.app.nouapp.model.StudyMaterial;
import com.app.nouapp.model.TestInfo;
import com.app.nouapp.model.TestResult;
import com.app.nouapp.service.AdminInfoRepository;
import com.app.nouapp.service.EnquiryRepository;
import com.app.nouapp.service.QuestionBankRepository;
import com.app.nouapp.service.ResponseRepository;
import com.app.nouapp.service.StudentInfoRepository;
import com.app.nouapp.service.StudyMaterialRepository;
import com.app.nouapp.service.TestInfoRepository;
import com.app.nouapp.service.TestResultRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	AdminInfoRepository adrepo;

	@Autowired
	StudentInfoRepository stdrepo;

	@Autowired
	ResponseRepository resrepo;
	
	@Autowired
	StudyMaterialRepository smrepo;
	
	@Autowired
	EnquiryRepository eqRepo;
	
	@Autowired
	TestInfoRepository tstrepo;
	
	@Autowired
	QuestionBankRepository qbrepo;
	
	@Autowired
	TestResultRepository resultrepo;
	
	@Autowired
	private SendEmailService emailService;

	@GetMapping("/adhome")
	public String ShowAdminDashboard(HttpSession session, RedirectAttributes attributes, Model model) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());
			
			model.addAttribute("scount", stdrepo.count());
			
			List<StudyMaterial> smlist = smrepo.findByMaterialType("studymaterial");
			model.addAttribute("smcount", smlist.size());
			
			List<StudyMaterial> aslist = smrepo.findByMaterialType("assignment");
			model.addAttribute("ascount", aslist.size());
			
			List<Response> cList = resrepo.findByResponseType("Complaint");
			model.addAttribute("ccount", cList.size());
			
			List<Response> fList = resrepo.findByResponseType("Complaint");
			model.addAttribute("fcount", fList.size());
			
			model.addAttribute("ecount", eqRepo.count());
			
			return "admin/admindashboard";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired");
			return "redirect:/adminlogin";
		}
	}
	
	//////////////////////////////////////////////////////////////
	///////////////TEST LOGICS STARTED FROM HERE//////////////////
	//////////////////////////////////////////////////////////////
	
	@GetMapping("/scheduletest")
	public String ShowScheduleTest(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());
			
			TestInfoDto dto = new TestInfoDto();
			model.addAttribute("dto", dto);
			
			
			return "admin/scheduletest";
		} else {
			redirectAttributes.addFlashAttribute("msg", "Your session has been ended.");
			return "redirect:/adminlogin";
		}
	}
	
	@PostMapping("scheduletest")
	public String ScheduleTest(@ModelAttribute TestInfoDto dto, RedirectAttributes attributes)
	{
		try {
			
			if (tstrepo.existsByTestname(dto.getTestname())) {
				attributes.addFlashAttribute("msg", "This test already exists!");
				return "redirect:/admin/scheduletest";
			}
			
			TestInfo tstinfo = new TestInfo();
			tstinfo.setTestname(dto.getTestname());
			tstinfo.setCourse(dto.getCourse());
			tstinfo.setBranch(dto.getBranch());
			tstinfo.setYear(dto.getYear());
			tstinfo.setNumberofquestion(dto.getNumberofquestion());
			tstinfo.setStarttime(dto.getStarttime());
			tstinfo.setActive(false);
			tstinfo.setTestDuration(dto.getTestDuration());
			tstrepo.save(tstinfo);
			
			//send email msg
			List<StudentInfo> stdlist = stdrepo.findAll();
			for(StudentInfo student:stdlist)
			{
				if(student.getProgram().equals(tstinfo.getCourse()) && student.getBranch().equals(tstinfo.getBranch()) && student.getYear().equals(tstinfo.getYear()))                    
				{
					emailService.SendTestId(student.getName(), tstinfo.getId(), dto.getTestname(), dto.getStarttime().toString(), student.getEmailaddress());
				}
			}
			
			attributes.addFlashAttribute("msg", "Test Successfully Scheduled");
			return "redirect:/admin/scheduletest";
		
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
			return "redirect:/admin/scheduletest";
		}
	}
	
	
	
	
	@GetMapping("/addquestion")
	public String ShowAddQuestion(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());

			QuestionBankDto dto = new QuestionBankDto();
			model.addAttribute("dto", dto);
			return "admin/addquestion";
		} else {
			redirectAttributes.addFlashAttribute("msg", "Your session has been ended.");
			return "redirect:/adminlogin";
		}
	}
	
	@PostMapping("/addquestion")
	public String AddQuestion(@ModelAttribute QuestionBankDto dto, RedirectAttributes attributes)
	{
		try {
			
			QuestionBank qb = new QuestionBank();
			qb.setQuestion(dto.getQuestion());
			qb.setA(dto.getA());
			qb.setB(dto.getB());
			qb.setC(dto.getC());
			qb.setD(dto.getD());
			qb.setCorrect(dto.getCorrect());
			qb.setYear(dto.getYear());
			qb.setCourse(dto.getCourse());
			qb.setBranch(dto.getBranch());
			qbrepo.save(qb);
			attributes.addFlashAttribute("msg", "Question Added Successfully!");
			return "redirect:/admin/addquestion";
			
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
			return "redirect:/admin/addquestion";
		}
	}
	
	@GetMapping("/uploadquestion")
	public String ShowUploadQuestion(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());
			
			model.addAttribute("dto", new QuestionBankDto());
			return "admin/uploadquestion";
		} else {
			redirectAttributes.addFlashAttribute("msg", "Your session has been ended.");
			return "redirect:/adminlogin";
		}
	}
	
	@PostMapping("/uploadquestion")
	public String UploadCSVQuestion(@RequestParam("csvfile") MultipartFile file,@ModelAttribute("dto") QuestionBankDto bankDto,RedirectAttributes attributes)
	{	
		if(file.isEmpty())
		{
			attributes.addFlashAttribute("msg", "File is Empty");
			return "redirect:/admin/uploadquestion";
		}
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) 
		{
			String line;
			List<QuestionBank> qblist = new ArrayList<>();
			reader.readLine();//remove header line
			
			while((line = reader.readLine()) != null)
			{
				String[] data = line.split(",");
				if(data.length==6)
				{
					QuestionBank qb = new QuestionBank();
					qb.setQuestion(data[0]);
					qb.setA(data[1]);
					qb.setB(data[2]);
					qb.setC(data[3]);
					qb.setD(data[4]);
					qb.setCorrect(data[5]);
					qb.setYear(bankDto.getYear());
					qb.setCourse(bankDto.getCourse());
					qb.setBranch(bankDto.getBranch());
					qblist.add(qb);			
				}
			}
			qbrepo.saveAll(qblist);
			attributes.addFlashAttribute("msg", "CSV File Question Uploaded Successfully");
			return "redirect:/admin/uploadquestion";
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
			return "redirect:/admin/uploadquestion";
		}
	}
	
	
	//View Question Bank
	@GetMapping("/questionbank")
	public String ShowViewQuestion(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());
			
			List<QuestionBank> questionList = qbrepo.findAll();
			model.addAttribute("questionList", questionList);
			return "admin/questionbank";
		} else {
			redirectAttributes.addFlashAttribute("msg", "Your session has been ended.");
			return "redirect:/adminlogin";
		}
	}
	
	@GetMapping("/deletequestion")
	public String DeleteQuestion(@RequestParam("id") int id)
	{
		QuestionBank question = qbrepo.findById(id).get();
		qbrepo.delete(question);
		return "redirect:/admin/questionbank";
	}
	
	@GetMapping("/viewresult")
	public String ShowViewResult(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());
			
			List<TestResult> resultList = resultrepo.findAll();
			model.addAttribute("resultList", resultList);
			return "admin/viewresult";
		} else {
			redirectAttributes.addFlashAttribute("msg", "Your session has been ended.");
			return "redirect:/adminlogin";
		}
	}
	
	@GetMapping("/deleteresult")
	public String DeleteResult(@RequestParam("id") long resultid)
	{
		TestResult result =	resultrepo.findById(resultid).get();
		resultrepo.delete(result);
		return "redirect:/admin/viewresult";
	}
	
	
	
	
	
	
	
	
	
	
	

	@GetMapping("/managestudent")
	public String ShowManageStudents(HttpSession session, RedirectAttributes attributes, Model model) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());

			List<StudentInfo> slist = stdrepo.findAll();
			model.addAttribute("slist", slist);
			// model.addAttribute("count", slist.size());
			return "admin/managestudent";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired!");
			return "redirect:/adminlogin";
		}
	}

	@GetMapping("/deletestudent")
	public String DeleteStudent(@RequestParam String enroll, HttpSession session, RedirectAttributes attributes,
			Model model) {
		if (session.getAttribute("admin") != null) {
			StudentInfo stdinfo = stdrepo.findById(enroll).get();
			stdrepo.delete(stdinfo);
			attributes.addFlashAttribute("msg", stdinfo.getName() + " is deleted Successfully");
			return "redirect:/admin/managestudent";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired");
			return "redirect:/adminlogin";
		}
	}

	@GetMapping("/addstudymaterial")
	public String ShowAddStudyMaterial(HttpSession session, RedirectAttributes attributes, Model model) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());
			
			StudyMaterialDto dto = new StudyMaterialDto();
			model.addAttribute("dto", dto);
			
			return "admin/addstudymaterial";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired");
			return "redirect:/adminlogin";
		}
	}
	
	@PostMapping("/addstudymaterial")
	public String AddStudyMaterial(@ModelAttribute StudyMaterialDto dto, RedirectAttributes attributes)
	{
		try {
			
			MultipartFile filedata = dto.getFilename();
			String storageFileName = filedata.getOriginalFilename();
			long size = filedata.getSize();
			int s =(int) size/(1024*1024);	//file size in MB
			if(s>5)
			{
				attributes.addFlashAttribute("msg", "File Should be less that 5 MB");
				return "redirect:/admin/addstudymaterial";
			}
			System.err.println(size);
			String uploadDir = "public/mat/";
			Path UploadPath = Paths.get(uploadDir);
			
			if(!Files.exists(UploadPath))
			{
				Files.createDirectories(UploadPath);
			}
			
			try(InputStream inputStream = filedata.getInputStream()){
				Files.copy(inputStream, Paths.get(uploadDir +storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
			
			StudyMaterial material = new StudyMaterial();
			material.setCourse(dto.getCourse());
			material.setBranch(dto.getBranch());
			material.setYear(dto.getYear());
			material.setSubject(dto.getSubject());
			material.setTopic(dto.getTopic());
			material.setMaterialtype(dto.getMaterialtype());
			material.setFilename(storageFileName);
			
			Date dt = new Date();
			SimpleDateFormat df = new SimpleDateFormat();
			String posteddate = df.format(dt);
			material.setPosteddate(posteddate);
			smrepo.save(material);
			attributes.addFlashAttribute("msg", "Material Uploaded Successfully");
			return "redirect:/admin/addstudymaterial";
			
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Something went wrong "+e.getMessage());
			return "redirect:/admin/addstudymaterial";
		}
	}
	
	@GetMapping("/managestudymaterial")
	public String ShowManageStudyMaterial(HttpSession session, RedirectAttributes attributes, Model model) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());
			
			List<StudyMaterial> stdm = smrepo.findAll();
			model.addAttribute("stdm", stdm);
			
			return "admin/managestudymaterial";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired");
			return "redirect:/adminlogin";
		}
	}
	
	
	@GetMapping("/deletestudymaterial")
	public String DeleteStudeyMaterial(@RequestParam("id") int id, HttpSession session, RedirectAttributes attributes,
			Model model) {
		if (session.getAttribute("admin") != null) {
			smrepo.deleteById(id);
			attributes.addFlashAttribute("msg", "Studymaterial is deleted Successfully");
			return "redirect:/admin/managestudymaterial";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired");
			return "redirect:/adminlogin";
		}
	}

	
	@GetMapping("/feedback")
	public String ShowFeedback(HttpSession session, RedirectAttributes attributes, Model model) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());

			List<Response> flist = resrepo.findByResponseType("feedback");
			model.addAttribute("flist", flist);

			return "admin/viewfeedback";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired");
			return "redirect:/adminlogin";
		}
	}

	@GetMapping("/deletefeedback")
	public String DeleteFeedback(@RequestParam("id") int id, HttpSession session, RedirectAttributes attributes,
			Model model) {
		if (session.getAttribute("admin") != null) {
			resrepo.deleteById(id);
			attributes.addFlashAttribute("msg", "Feedback is deleted Successfully");
			return "redirect:/admin/feedback";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired");
			return "redirect:/adminlogin";
		}
	}
	
	
	@GetMapping("/complaint")
	public String ShowComplaint(HttpSession session, RedirectAttributes attributes, Model model) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());

			List<Response> clist = resrepo.findByResponseType("complaint");
			model.addAttribute("clist", clist);
			return "admin/viewcomplaint";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired");
			return "redirect:/adminlogin";
		}
	}
	
	@GetMapping("/deletecomplaint")
	public String DeleteComplaint(@RequestParam("id") int id, HttpSession session, RedirectAttributes attributes,
			Model model) {
		if (session.getAttribute("admin") != null) {
			resrepo.deleteById(id);
			attributes.addFlashAttribute("msg", "Complaint is deleted Successfully");
			return "redirect:/admin/complaint";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired");
			return "redirect:/adminlogin";
		}
	}
	
	
	
	@GetMapping("/viewenquiry")
	public String ShowENquiry(HttpSession session, RedirectAttributes attributes, Model model) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());

			List<Enquiry> eqList = eqRepo.findAll();
			model.addAttribute("eqList", eqList);
			return "admin/viewenquiry";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired");
			return "redirect:/adminlogin";
		}
	}
	
	@GetMapping("/deleteenquiry")
	public String DeletedEnquiry(@RequestParam("id") int id, HttpSession session, RedirectAttributes attributes,
			Model model) {
		if (session.getAttribute("admin") != null) {
			eqRepo.deleteById(id);
			attributes.addFlashAttribute("msg", "Enquiry is deleted Successfully");
			return "redirect:/admin/viewenquiry";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired");
			return "redirect:/adminlogin";
		}
	}
	

	
	@GetMapping("/changepassword")
	public String ShowChangePassword(HttpSession session, Model model, RedirectAttributes attributes) {
		if (session.getAttribute("admin") != null) {
			AdminInfo ad = adrepo.findById(session.getAttribute("admin").toString()).get();
			model.addAttribute("name", ad.getName());
			return "admin/changepassword";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired");
			return "redirect:/adminlogin";
		}
	}

	@PostMapping("/changepassword")
	public String ChangePassword(HttpSession session, RedirectAttributes attributes, HttpServletRequest request) {
		try {

			AdminInfo adinfo = adrepo.findById(session.getAttribute("admin").toString()).get();
			String oldpass = request.getParameter("oldpass");
			String newpass = request.getParameter("newpass");
			String confirmpass = request.getParameter("confirmpass");

			if (newpass.equals(confirmpass)) 
			{
				if (oldpass.equals(adinfo.getPassword())) 
				{
					adinfo.setPassword(confirmpass);
					adrepo.save(adinfo);
					session.invalidate();
					attributes.addFlashAttribute("msg", "Password Changed Succesfully 😀");
					return "redirect:/adminlogin";
				}
				else {
					attributes.addFlashAttribute("message", "Invalid Old Password");
				}
			} 
			else {
				attributes.addFlashAttribute("message", "New Password & Confirm Password Not Matched!");
			}
			return "redirect:/admin/changepassword";

		} catch (Exception e) {
			attributes.addFlashAttribute("message", "Something went wrong " + e.getMessage());
			return "redirect:/admin/changepassword";
		}
	}

	@GetMapping("/logout")
	public String Logout(HttpSession session, RedirectAttributes attributes) {
		if (session.getAttribute("admin") != null) {
			session.invalidate();
			attributes.addFlashAttribute("msg", "Successfully Logout");
			return "redirect:/adminlogin";
		} else {
			attributes.addFlashAttribute("msg", "Session Expired!");
			return "redirect:/adminlogin";
		}
	}
}
