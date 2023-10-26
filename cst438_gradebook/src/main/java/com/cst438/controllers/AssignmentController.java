package com.cst438.controllers;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin 
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Autowired
	AssignmentGradeRepository assignmentGradeRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@GetMapping("/assignment")
	public AssignmentDTO[] getAllAssignmentsForInstructor() {
		// get all assignments for this instructor
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
		AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
		for (int i=0; i<assignments.size(); i++) {
			Assignment as = assignments.get(i);
			AssignmentDTO dto = new AssignmentDTO(
					as.getId(), 
					as.getName(), 
					as.getDueDate().toString(), 
					as.getCourse().getTitle(), 
					as.getCourse().getCourse_id());
			result[i]=dto;
		}
		return result;
	}
	
	// TODO create CRUD methods for Assignment
	// read
	@GetMapping("/assignment/{id}")
	public AssignmentDTO getAssignment(@PathVariable("id") int id)  {
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email)
		Assignment a = assignmentRepository.findById(id).orElse(null);
		if (a==null) {
			throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "assignment not found "+id);
		}
		// check that assignment is for a course of this instructor
		if (! a.getCourse().getInstructor().equals(instructorEmail)) {
			throw  new ResponseStatusException( HttpStatus.FORBIDDEN, "not authorized "+id);
		}
		AssignmentDTO adto = new AssignmentDTO(a.getId(), a.getName(), a.getDueDate().toString(), a.getCourse().getTitle(), a.getCourse().getCourse_id());
		return adto;

	}
	
	// create
	@PostMapping("/assignment")
	public int createAssignment(@RequestBody AssignmentDTO adto) {
		String instructorEmail = "dwisneski@csumb.edu";
		Assignment as = new Assignment();
		Course cs = courseRepository.findById(adto.courseId()).orElseThrow(() ->
		new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
		
		if (cs.getInstructor().equals(instructorEmail)) {
			as.setName(adto.assignmentName());
	        as.setDueDate(Date.valueOf(adto.dueDate()));
	        as.setCourse(cs);
		}
        assignmentRepository.save(as);
        return as.getId();
        // check that course id in AssignmentDTO exists and belongs 
        //  to this instructor
        // then create a new Assignment Entity and save to database
        // return the assignment id of the new assignment

	}
	
	// delete
	@DeleteMapping("/assignment/{id}")
	public void deleteAssignment(@PathVariable("id") int id, 
								 @RequestParam("force") Optional<String> force) {		
		Assignment as = assignmentRepository.findById(id).orElseThrow(() -> 
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
		String insEmail = "dwisneski@csumb.edu";
		Course course = as.getCourse();
		Optional<AssignmentGrade> gd = assignmentGradeRepository.findById(id);
		
		if (course != null) {
			String ins = course.getInstructor();
			if (ins.equals(insEmail)) {
				if (gd.isEmpty() || force.isEmpty()) {
					assignmentRepository.delete(as);
				}
				
				else {
					assignmentRepository.delete(as);
				}
			}
		}
		
		
		// check assignment belongs to this instructor
		// delete assignment if there are no grades.
		// if there are grades for the assignment, delete the 
		// assignment only if "force" is specified

	}
	
	// update
	@PostMapping("/assignment/{id}")
	public void updateAssignment(@RequestBody AssignmentDTO adto, 
								 @PathVariable("assignment_id") int id) {
		Assignment as = assignmentRepository.findById(id).orElseThrow(() -> 
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
		String insEmail = "dwisneski@csumb.edu";
		Course course = as.getCourse();		
		if (course != null) {
			String ins = course.getInstructor();
			if (ins.equals(insEmail)){
				as.setId(adto.id());
				as.setName(adto.assignmentName());
				as.setDueDate(Date.valueOf(adto.dueDate()));
			}
		}
		// check assignment belongs to a course for this instructor
		// update assignment with data in AssignmentDTO
		assignmentRepository.save(as);
	}
}
