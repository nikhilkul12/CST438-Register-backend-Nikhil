package com.cst438.service;

import org.springframework.stereotype.Service;

@Service
public interface GradebookService {
	/*
	 * A student has add a course.  Send message to other services.
	 */
	public void enrollStudent(String student_email, String student_name, int course_id);

}
