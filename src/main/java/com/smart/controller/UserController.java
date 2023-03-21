package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository  userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName=principal.getName();
		System.out.println("USERNAME " + userName);
		
		// get the user using username(Email)
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER" + user);
		model.addAttribute("user",user);
		
	}
	
	//dashboard home
	@GetMapping("/index")
	public String dashboard(Model model,Principal principal) {
		model.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
	
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title","Add contact");
		model.addAttribute("contact", new Contact());		
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
							@RequestParam("profileImage") MultipartFile file,
							Principal principal,HttpSession session) {
		try {
			String name=principal.getName();
			User user=this.userRepository.getUserByUserName(name);
			
			// processing and uploading file..
			if(file.isEmpty()){
				//if the file is empty then try our message
				System.out.println("File is empty");
				contact.setImage("contact.png");
			}else {
				// upload the file to folder and update the name to contact
				contact.setImage(file.getOriginalFilename());
				File saveFile=new ClassPathResource("static/image").getFile();
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
			}
			
			user.getContacts().add(contact);
			contact.setUser(user);
			
			this.userRepository.save(user);
			System.out.println("DATA "+ contact);
			System.out.println("Added to data base");
			//message success....
			session.setAttribute("message", new Message("Your contact is added !! Added more..","success"));
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
			//message error....
			session.setAttribute("message", new Message("Something went wrong !!Try again...","danger"));
		}
		return "normal/add_contact_form";
	}
	
	//per page =5[n]
	//current page=0[page]
	//show contacts handler
	@GetMapping("/show-contacts")
	public String showContacts(Model m,Principal principal) {
		m.addAttribute("title","Show user contacts");
		
		//contact ki list ko bhejni hai
		String userName=principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		List<Contact> contacts = this.contactRepository.findContactsByUser(user.getId());
		
		m.addAttribute("contacts",contacts);
		return "normal/show_contacts";
	}
	
	//showing particular contact details
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		System.out.println("CID " +cId);
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		//
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact",contact);
			model.addAttribute("title",contact.getName());
		}
		return "normal/contact_detail";
	}
	
	
	//delete specific contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId,Model model,Principal principal,HttpSession session) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
				
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		System.out.println("Contact " + contact.getcId());

		//check.. Assignment.. image delete
		if(user.getId()==contact.getUser().getId()) {
			//DELETE
			// contact.setUser(null);
			// this.contactRepository.delete(contact);
			user.getContacts().remove(contact);
			this.userRepository.save(user);
			
			System.out.println("DELETED");
			session.setAttribute("message", new Message("Contact deleted successfully...", "success"));
		}
		return "redirect:/user/show-contacts";
		
//		Contact contact1=contactRepository.findById(cId)
//				.orElseThrow(()-> new ResourceNotFoundException("contact not exist with id: " + cId));
//		contactRepository.delete(contact1);
//		Map<String, Boolean> response=new HashMap<>();
//		response.put("deletes", Boolean.TRUE);
//		return "redirect:/user/show-contacts";
	}
	
	//open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model m) {
		m.addAttribute("title","Update Contact");
		Contact contact = this.contactRepository.findById(cid).get();
		m.addAttribute("contact",contact);
		return "normal/update_form";
	}
	
	//Update Contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,
								@RequestParam("profileImage") MultipartFile file,
								Model m,
								HttpSession session,
								Principal principal) {
		try {
			
			//old contact details
			Contact oldcontactDetail=this.contactRepository.findById(contact.getcId()).get();
			//image..
			if(!file.isEmpty()) {
				//file work..
				//rewrite
				
				//delete old photo
				
				File deleteFile=new ClassPathResource("static/image").getFile();
				File file1=new File(deleteFile,oldcontactDetail.getImage());
				file1.delete();
				
				//update new photo
				File saveFile=new ClassPathResource("static/image").getFile();
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}else {
				contact.setImage(oldcontactDetail.getImage());
			}
			
			User user=this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Your contact is updated...", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("CONTACT NAME : " + contact.getName());
		System.out.println("CONTACT ID : " + contact.getcId());
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	// your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title","Profile Page");
		return "normal/profile";
	}
}



