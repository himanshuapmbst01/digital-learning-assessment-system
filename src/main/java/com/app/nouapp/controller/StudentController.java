package com.app.nouapp.controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

import com.app.nouapp.dto.ResponseDto;
import com.app.nouapp.model.QuestionBank;
import com.app.nouapp.model.Response;
import com.app.nouapp.model.StudentInfo;
import com.app.nouapp.model.StudyMaterial;
import com.app.nouapp.model.TestInfo;
import com.app.nouapp.model.TestResult;
import com.app.nouapp.service.QuestionBankRepository;
import com.app.nouapp.service.ResponseRepository;
import com.app.nouapp.service.StudentInfoRepository;
import com.app.nouapp.service.StudyMaterialRepository;
import com.app.nouapp.service.TestInfoRepository;
import com.app.nouapp.service.TestResultRepository;
import com.google.gson.Gson;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentController {
	
	@Autowired
	StudentInfoRepository stdrepo;
	
	@Autowired
	ResponseRepository resrepo;
	
	@Autowired
	StudyMaterialRepository smrepo;
	
	@Autowired
	TestInfoRepository testrepo;
	
	@Autowired
	QuestionBankRepository qbrepo;
	
	@Autowired
	TestResultRepository resultRepo;
	
	@Autowired
	private EntityManager entityManager;
	
	@GetMapping("/stdhome")
	public String ShowStudentDashboard(HttpSession session, Model model)
	{
		if(session.getAttribute("studentid")!=null)
		{
			StudentInfo stdinfo = stdrepo.findById(session.getAttribute("studentid").toString()).get();
			model.addAttribute("stdinfo", stdinfo);
			return "student/studentdashboard";
		}
		else {
			return "redirect:/stulogin";
		}
	}
	
	@PostMapping("/stdhome")
	public String UploadPic(@RequestParam MultipartFile file, RedirectAttributes attributes , HttpSession session)
	{
		try {
			String storageFileName = file.getOriginalFilename();
			String uploadDir = "public/profile/";
			Path uploadPath = Paths.get(uploadDir);
			
			if(!Files.exists(uploadPath))
			{
				Files.createDirectories(uploadPath);
			}
			try(InputStream inputStream = file.getInputStream())
			{
				Files.copy(inputStream, Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
			
			StudentInfo std = stdrepo.findById(session.getAttribute("studentid").toString()).get();
			std.setProfilepic(storageFileName);
			stdrepo.save(std);
			attributes.addFlashAttribute("msg", "Profile Pic Uploaded Successfully");
			return "redirect:/student/stdhome";
			
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Something went wrong "+e.getMessage());
			return "redirect:/student/stdhome";
		}
	}
	
	//Student Give Test Submit Test, See Result
	@GetMapping("/givetest")
	public String ShowGiveTest(HttpSession session, Model  model)
	{
		if (session.getAttribute("studentid")!=null) {
			
			StudentInfo stdinfo = stdrepo.findById(session.getAttribute("studentid").toString()).get();
			model.addAttribute("stdinfo", stdinfo);
			return "student/givetest";
		}
		else {
			return "redirect:/stulogin";
		}
	}
	
	
	private long tstid;

	@GetMapping("/starttest")
	public String StartTest(@RequestParam long testid, HttpSession session, RedirectAttributes attributes, Model model, HttpServletResponse response) {
	    this.tstid = testid;
	    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

	    if (session.getAttribute("studentid") != null) {
	        StudentInfo stdinfo = stdrepo.findById(session.getAttribute("studentid").toString()).get();
	        model.addAttribute("stdinfo", stdinfo);

	        try {
	            TestInfo tstinfo = testrepo.findById(testid).get();
	            if (tstinfo.isActive()) {
	                // Check if the student has already taken the test
	                if (resultRepo.existsByEnrollmentnoAndTestId(stdinfo.getEnrollmentno(), testid)) {
	                    attributes.addFlashAttribute("msg", "You have already given this test 😀🎊");
	                    return "redirect:/student/givetest";
	                }
	       

	                if (stdinfo.getProgram().equals(tstinfo.getCourse()) && 
	                    stdinfo.getBranch().equals(tstinfo.getBranch()) && 
	                    stdinfo.getYear().equals(tstinfo.getYear())) {
         	
	                    String year = stdinfo.getYear();
	                    String course = stdinfo.getProgram();
	                    String branch = stdinfo.getBranch();
	                    int numberOfQuestion = tstinfo.getNumberofquestion();

	                    List<QuestionBank> qblist = qbrepo.findQuestionbyYearAndCourseAndBranch(year, course, branch, numberOfQuestion, entityManager);
	                    Gson gson = new Gson();
	                    String json = gson.toJson(qblist);
	                    model.addAttribute("json", json);
	                    model.addAttribute("testname", tstinfo.getTestname());
	                    model.addAttribute("tt", (double) qblist.size() / 2);
	                    model.addAttribute("tq", qblist.size());
	                    return "student/starttest";
	                } else {
	                    attributes.addFlashAttribute("msg", "This test is not scheduled for you");
	                    return "redirect:/student/givetest";
	                }
	            } else {
	                attributes.addFlashAttribute("msg", "There is no active test with this ID");
	                return "redirect:/student/givetest";
	            }
	        } catch (Exception e) {
	            attributes.addFlashAttribute("msg", "No test found with this ID");
	            return "redirect:/student/givetest";
	        }
	    } else {
	        attributes.addFlashAttribute("msg", "Session has expired!");
	        return "redirect:/stulogin";
	    }
	}
	
	@GetMapping("/testover")
	public String ShowTestover(HttpSession session, Model  model)
	{
		if (session.getAttribute("studentid")!=null) {
			
			StudentInfo stdinfo = stdrepo.findById(session.getAttribute("studentid").toString()).get();
			model.addAttribute("stdinfo", stdinfo);
			return "student/testover";
		}
		else {
			return "redirect:/stulogin";
		}
	}
	

	
	@PostMapping("/testover")
	public String TestOver(HttpSession session, @RequestParam("t") int t, @RequestParam("s") int s, Model model, RedirectAttributes attributes)
	{
		if(session.getAttribute("studentid")!=null)
		{
			try {
				TestInfo testInfo = testrepo.findById(tstid).get();
				StudentInfo stdinfo = stdrepo.findById(session.getAttribute("studentid").toString()).get();
				TestResult result = new TestResult();
				result.setEmail(stdinfo.getEmailaddress());
				result.setEnrollmentno(stdinfo.getEnrollmentno());
				result.setName(stdinfo.getName());
				result.setContactno(stdinfo.getContactno());
				result.setTestId(tstid);
				result.setCourse(stdinfo.getProgram());
				result.setBranch(stdinfo.getBranch());
				result.setYear(stdinfo.getYear());
				result.setStatus("true");
				result.setTotalmarks(t);
				result.setGetmarks(s);
				result.setTestname(testInfo.getTestname());
				result.setSubmittedAt(LocalDateTime.now());
				resultRepo.save(result);
				model.addAttribute("stdinfo", stdinfo);
				return "redirect:/student/testover";
			} catch (Exception e) {
				attributes.addFlashAttribute("msg", "Error : "+e.getMessage());
				return "redirect:/student/givetest";
			}
		}
		else
		{
			attributes.addFlashAttribute("msg", "Session has been expired");
			return "redirect:/stulogin";
		}
	}
	
	
	@GetMapping("/result")
	public String ShowResult(HttpSession session, Model  model)
	{
		if (session.getAttribute("studentid")!=null) {
			
			StudentInfo stdinfo = stdrepo.findById(session.getAttribute("studentid").toString()).get();
			model.addAttribute("stdinfo", stdinfo);
			
			List<TestResult> resultList = resultRepo.findResultByEnrollmentno(stdinfo.getEnrollmentno());
			model.addAttribute("resultList", resultList);
			return "student/result";
		}
		else {
			return "redirect:/stulogin";
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@GetMapping("/studymaterial")
	public String ShowStudyMaterial(HttpSession session, Model model)
	{
		if(session.getAttribute("studentid")!=null)
		{
			StudentInfo stdinfo = stdrepo.findById(session.getAttribute("studentid").toString()).get();
			String program = stdinfo.getProgram();
			String branch = stdinfo.getBranch();
			String year = stdinfo.getYear();
			String materialtype = "studymaterial";
			
			List<StudyMaterial> smlist = smrepo.findAllbyType(program, branch, year, materialtype);
			model.addAttribute("smlist", smlist);
			model.addAttribute("stdinfo", stdinfo);
			return "student/studymaterial";
		}
		else {
			return "redirect:/stulogin";
		}
	}

	@GetMapping("/assignment")
	public String ShowAssignment(HttpSession session, Model model)
	{
		if(session.getAttribute("studentid")!=null)
		{
			StudentInfo stdinfo = stdrepo.findById(session.getAttribute("studentid").toString()).get();
			String program = stdinfo.getProgram();
			String branch = stdinfo.getBranch();
			String year = stdinfo.getYear();
			String materialtype = "assignment";
			
			List<StudyMaterial> smlist = smrepo.findAllbyType(program, branch, year, materialtype);
			model.addAttribute("smlist", smlist);
			model.addAttribute("stdinfo", stdinfo);
			
			return "student/assignment";
		}
		else {
			return "redirect:/stulogin";
		}
	}

	@GetMapping("/giveresponse")
	public String ShowGiveResponse(HttpSession session, Model model)
	{
		if(session.getAttribute("studentid")!=null)
		{
			ResponseDto dto = new ResponseDto();
			model.addAttribute("dto", dto);
			model.addAttribute("stdinfo", stdrepo.findById(session.getAttribute("studentid").toString()).get());
			
			return "student/giveresponse";
		}
		else {
			return "redirect:/stulogin";
		}
	}
	
	@PostMapping("/giveresponse")
	public String SubmitResponse(@ModelAttribute ResponseDto dto, HttpSession session, RedirectAttributes attributes)
	{
		if(session.getAttribute("studentid")!=null)
		{
			try {
				
				StudentInfo stdinfo = stdrepo.findById(session.getAttribute("studentid").toString()).get();
				
				Response res = new Response();
				res.setName(stdinfo.getName());
				res.setEnrollmentno(stdinfo.getEnrollmentno());
				res.setContactno(stdinfo.getContactno());
				res.setResponsetype(dto.getResponsetype());
				res.setResponsetitle(dto.getResponsetitle());
				res.setResponsetext(dto.getResponsetext());
				Date dt = new Date();
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String resdate = df.format(dt);
				res.setResdate(resdate);
				resrepo.save(res);
				attributes.addFlashAttribute("msg", "Response Submitted Successfully!");
				return "redirect:/student/giveresponse";
				
			} catch (Exception e) {
				attributes.addFlashAttribute("msg", "Something went Wrong "+e.getMessage());
				return "redirect:/student/giveresponse";
			}
		}
		else {
			return "redirect:/stulogin";
		}
	}
	
	@GetMapping("/changepassword")
	public String ShowChangePassword(HttpSession session, Model model)
	{
		if(session.getAttribute("studentid")!=null)
		{
			model.addAttribute("stdinfo", stdrepo.findById(session.getAttribute("studentid").toString()).get());
			return "student/changepassword";
		}
		else {
			return "redirect:/stulogin";
		}
	}
	
	@PostMapping("/changepassword")
	public String ChangePassword(RedirectAttributes attributes, HttpSession session, HttpServletRequest request)
	{
		try {
			
			StudentInfo studentInfo = stdrepo.findById(session.getAttribute("studentid").toString()).get();
			
			String oldpass = request.getParameter("oldpass");
			String newpass = request.getParameter("newpass");
			String confirmpass = request.getParameter("confirmpass");
			if(newpass.equals(confirmpass))
			{
				if (oldpass.equals(studentInfo.getPassword())) {
					studentInfo.setPassword(confirmpass);
					stdrepo.save(studentInfo);
					attributes.addFlashAttribute("msg", "Password Changed Succefully, Please Login Now!");
					return "redirect:/stulogin";
				}
				else {
					attributes.addFlashAttribute("message", "Invalid Old Password ⚠️");
					return "redirect:/student/changepassword";
				}
			}
			else {
				attributes.addFlashAttribute("message", "New Password and Confirm Password Does not matched ⚠️");
				return "redirect:/student/changepassword";
				
			}
			
		} catch (Exception e) {
			attributes.addFlashAttribute("message", "Error : "+e.getMessage());
			return "redirect:/student/changepassword";
		}
	}
	
	@GetMapping("/logout")
	public String Logout(HttpSession session)
	{
		if(session.getAttribute("studentid")!=null)
		{
			session.invalidate();
			return "redirect:/student/stdhome";
		}
		else {
			return "redirect:/stulogin";
		}
	}
	
	

}
