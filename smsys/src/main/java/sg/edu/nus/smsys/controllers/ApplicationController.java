package sg.edu.nus.smsys.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sg.edu.nus.smsys.models.Application;
import sg.edu.nus.smsys.models.Course;

import sg.edu.nus.smsys.models.Leave;
import sg.edu.nus.smsys.models.Semester;
import sg.edu.nus.smsys.models.Staff;
import sg.edu.nus.smsys.models.Student;
import sg.edu.nus.smsys.models.User;
import sg.edu.nus.smsys.repository.ApplicationRepository;
import sg.edu.nus.smsys.repository.CourseRepository;
import sg.edu.nus.smsys.repository.SemesterRepository;
import sg.edu.nus.smsys.repository.StudentRepository;
import sg.edu.nus.smsys.security.SmsUserDetailsService;
import sg.edu.nus.smsys.service.ApplicationService;
import sg.edu.nus.smsys.service.UserService;

@Controller
@RequestMapping("/student")
public class ApplicationController {
	@Autowired
	private ApplicationService as;
	@Autowired
	private SemesterRepository semrepo;
	@Autowired
	private StudentRepository sturepo;
	@Autowired
	private CourseRepository crepo;
	@Autowired
	private ApplicationRepository apprepo;
	@Autowired
	private UserService us;
	@Autowired
	private SmsUserDetailsService suds;
	
	
	@GetMapping("/applycourse")
	public String applyCourse(Model model) {
		List<Course> eligibleCourse = new ArrayList<Course>();
		model.addAttribute("courselist", eligibleCourse);

		User user = us.getUserByUsername(suds.getAuthUsername());
		Student student = us.getStudentByUser(user);
		
		model.addAttribute("student", student);
		
		LocalDate today = LocalDate.now();
		String semCode = as.displayNextSemCode(today);
		model.addAttribute("semester", semCode);
		
		boolean eligible = as.checkEligibility(student);
		System.out.println("eligible student? " + eligible);
		model.addAttribute("eligibility", eligible);
		
		if(eligible) {
			eligibleCourse = as.displayEligibleCourses(student);
			List<Course> availableCourses = as.displayAvailableCourses();
			System.out.println("Available Courses: " + availableCourses.size());
			model.addAttribute("courselist", eligibleCourse);
		}
		return "applycourse";
	}
	
	@PostMapping("/submitapplication")
	public String applyCourse(@RequestParam int selectedcourse) {
		User user = us.getUserByUsername(suds.getAuthUsername());
		Student student = us.getStudentByUser(user);
		System.out.println( student.getFirstName() + " has applied for courseid = " + selectedcourse);
		Course course = crepo.findByCourseId(selectedcourse);
		as.saveApplication(course, student);
		
//		Application app = new Application(course, student);
//		apprepo.save(app);
		return "redirect:/student/home/appliedcourse";
	}
	
	@GetMapping("/home")
	public String displayHome(Model model, Student student) {
		User user = us.getUserByUsername(suds.getAuthUsername());
		student = us.getStudentByUser(user);
		model.addAttribute("studentid", student.getStudentId());
		
		return "studenthome";
	}
	
	@GetMapping("/home/appliedcourse")
	public String displayAppliedCourse(Model model, Student student) {
		System.out.println("Entered displayAppliedCourse");
		User user = us.getUserByUsername(suds.getAuthUsername());
		student = us.getStudentByUser(user);
		System.out.println(student.getFirstName() + " applied");
		
		model.addAttribute("studentid", student.getStudentId());
		
		List<Application> myApp = as.displayMyApplication(student);
		model.addAttribute("myapplicationlist", myApp);
		
		return "appliedcourse";
	}
	
//	@GetMapping("/myapplication")
//	public String myApplicationList(Model model, @RequestParam(defaultValue = "") String id) {
//		List<Application> myApplicationList = new ArrayList<Application>();
//		if (!id.equals("")) {
//			Student student = sturepo.findByStudentId(Integer.parseInt(id));
//			myApplicationList = as.displayMyApplication(student);
//		} 
//		model.addAttribute("myapplicationlist", myApplicationList);
//		return "";
//	}

//	@GetMapping("/delete/{id}")
//	public String deleteApplication(Model model, @PathVariable("id") Integer id) {
//		Application application = apprepo.findById(id).get();
//		lrepo.delete(leave);
//		String staffId = String.valueOf(leave.getSubmittedByStaffID().getStaffId());
//		return "redirect:/leave/leavelist?id=" + staffId;
//	}
//
//	@GetMapping("/view/{id}")
//	public String viewLeaveDetails(Model model, @ModelAttribute Leave leave, @PathVariable("id") Integer id) {
//		leave = lrepo.findById(id).get();
//
//		model.addAttribute("leave", leave);
//
//		return "leavedetails";
//	}

	
}


