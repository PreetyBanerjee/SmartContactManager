console.log("This is script file")


const toggleSidebar=()=>{
	if($(".sidebar").is(":visible")){
	    // band krna ha
	    $(".sidebar").css("display","none");
	    $(".content").css("margin-left","0%");
	}else{
		// sidebar show krna ha
	    $(".sidebar").css("display","block");
	    $(".content").css("margin-left","20%");
	}
}

function deleteContact(cid){
	swal({
	  title: "Are you sure?",
	  text: "you want to delete this contact..",
	  icon: "warning",
	  buttons: true,
	  dangerMode: true,
	})
	.then((willDelete) => {
	  if (willDelete) {
	    window.location="/user/delete/" + cid;
	  } else {
	    swal("Your contact is safe");
	  }
	});	
}

function myFunction() {
	alert("Sign in to Start...");
}