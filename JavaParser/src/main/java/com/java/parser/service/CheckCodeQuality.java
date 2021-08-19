package com.java.parser.service;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.visitor.VoidVisitorAdapter;



@RestController
public class CheckCodeQuality {	


	
	@GetMapping("read")
	public String createCompilationUnit() {		
		
	try {
		File file = new File("\\SampleApp.java");		 
		CompilationUnit cu = JavaParser.parse(new FileInputStream(file));
		System.out.println("compilation unit " + cu);
		System.out.println("___________________________________________________________________");
		System.out.println("cu.getPackage() " + cu.getPackage().getName());
		/*System.out.println("cu.getTypes() + package " + cu.getTypes());		
		System.out.println("cu.getParentNode() " + cu.getParentNode());
		System.out.println("cu.getData() " + cu.getData());
		System.out.println("cu.getClass() " + cu.getClass());
		System.out.println("cu.getBeginColumn() " + cu.getBeginColumn());
		System.out.println("cu.getOrphanComments() " + cu.getOrphanComments());
		System.out.println("cu.getComment() " + cu.getComment());
		System.out.println("cu.getEndLine() " + cu.getEndLine());
		System.out.println("cu.getBeginLine() " + cu.getBeginLine());*/
		
		 //Scan the project and upload the results in Sonar Server
		runSonarScanner();
		
		//Read and print Sonar report
	    List<List> listContainer = printSonarReport();
	    for(List<String> container : listContainer) {	    	
	    	System.out.println("squid values : " + container.get(0));
	    	
	    }
	    
		//Get Visitor classes from visitor package	
		List<Class> classes = getClassesOfPackage("com.java.parser.visitor");
		
		//Get Handler
		getHandler(listContainer, classes, cu);
		
		
		
		/*for(Class c : classes) {
			VoidVisitorAdapter o;
			if(c.getSimpleName().startsWith("Constructor")){
				System.out.println("test");
			}
			try {
				o = (VoidVisitorAdapter) c.newInstance();
				cu.accept(o, cu);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}*/
		
		FileOutputStream fos = new FileOutputStream(file);
		byte[] strToBytes = cu.toString().getBytes();
		fos.write(strToBytes);
	    fos.close();
	   
	    runSonarScanner();
	  //Read and print Sonar report
	    List<List> listContainer1 = printSonarReport();
	    for(List<String> container1 : listContainer1) {	    	
	    	System.out.println("squid values : " + container1.get(0));
	    	
	    }
	   
	    return cu.toString();
		
	} catch (ParseException  | IOException pfi ) {
		// TODO Auto-generated catch block
		pfi.printStackTrace();
		return "Fail";
	} 
}
	
	
	private void getHandler(List<List> listContainer, List<Class> handlers, CompilationUnit cu) {
		
		String portion = null;
		
		for(List<String> container : listContainer) {	    	
	    	System.out.println("squid values : " + container.get(0));
	    	if(container.get(0).contains(":S")) {
	    		 int index1 = container.get(0).indexOf('S');
	    		 portion = container.get(0).substring(index1);
	    	}
	    	for(Class c : handlers) {
				VoidVisitorAdapter o;
				
				int index = c.getSimpleName().indexOf('_');
				String rule = c.getSimpleName().substring(0,index);
				
				if(portion.equals(rule)) {
				try {
					o = (VoidVisitorAdapter) c.newInstance();
					cu.accept(o, cu);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				}
				
			}
	    	
	    	
	    }
		
		
	}
	
	
	private static List<Class> getClassesOfPackage(String pkgname){
		
		List<Class> classes = new ArrayList<Class>();

	    // Get a File object for the package
	    File directory = null;
	    String fullPath;
	    
	    //String pkgname = "com.java.parser.visitor";
	    String relPath = pkgname.replace('.', '/');
	   // System.out.println("ClassDiscovery: Package: " + pkgname + " becomes Path:" + relPath);
	    
	    URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
	    //System.out.println("ClassDiscovery: Resource = " + resource);
	    if (resource == null) {
	        throw new RuntimeException("No resource for " + relPath);
	    }
	    
	    fullPath = resource.getFile();
	   // System.out.println("ClassDiscovery: FullPath = " + resource);
	    
	    try {
	        directory = new File(resource.toURI());
	    } catch (URISyntaxException e) {
	        throw new RuntimeException(pkgname + " (" + resource + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
	    } catch (IllegalArgumentException e) {
	        directory = null;
	    }
	    System.out.println("ClassDiscovery: Directory = " + directory);
	    
	    if (directory != null && directory.exists()) {

	        // Get the list of the files contained in the package
	        String[] files = directory.list();
	        for (int i = 0; i < files.length; i++) {

	            // we are only interested in .class files
	            if (files[i].endsWith(".class")) {

	                // removes the .class extension
	                String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);

	                System.out.println("ClassDiscovery: className = " + className);

	                try {
	                    classes.add(Class.forName(className));
	                } catch (ClassNotFoundException e) {
	                    throw new RuntimeException("ClassNotFoundException loading " + className);
	                }
	            }
	        }
	    }
	    return classes;
		
	}

	
	
	private void runSonarScanner() {
		try {
			Runtime.getRuntime().exec("cmd /c sonar-scanner.bat", null, new File("/workspace4/SampleTest"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private List printSonarReport() throws JsonMappingException, JsonProcessingException {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = null;		
		List<List> rulesContainer = new ArrayList<>();;
		List<String> rules = null;
		
		//Connect to Sonar server using WEB API, to get the report
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				HttpEntity<String> request = new HttpEntity<String>(headers);
				
				String url = "http://localhost:9000/api/issues/search";
			    url += "?additionalFields=rules";
			    url += "&componentKeys=SampleTest";
			    response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
			    
			    //System.out.println("SONAR Response ---------" + response.getBody());
			    
				// Get the JSON response
				ObjectMapper mapper = new ObjectMapper();
				JsonNode root = mapper.readTree(response.getBody());
			    
				String result = root.get("total").asText();
				//System.out.println("result: " + result);
				
				//Get paging
				JsonNode pagingNode = root.path("paging");
				//if rules node exist
				/*if(!pagingNode.isMissingNode()) {
					System.out.println("pageIndex: " + pagingNode.path("pageIndex").asText());
					System.out.println("pageSize: " +  pagingNode.path("pageSize").asText());
					System.out.println("total: " +  pagingNode.path("total").asText());
				}*/
				
				//Get rules
				JsonNode rulesNode = root.path("rules");
					if(rulesNode.isArray()) {
						//System.out.println("Is this node an Array? " + rulesNode.isArray());
						for(JsonNode node : rulesNode) {
							String key = node.path("key").asText();
							String name = node.path("name").asText();
							String status = node.path("status").asText();
							String lang = node.path("lang").asText();
							/*System.out.println("key : " + key);
							System.out.println("name : " + name);
							System.out.println("status : " + status);
							System.out.println("lang : " + lang);*/
							rules = new ArrayList<>();							
							rules.add(key);
							rules.add(name);
							rules.add(status);
							rules.add(lang);
							rulesContainer.add(rules);
							/*System.out.println("size " + rules.size());
							System.out.println("container size " + rulesContainer.size());	*/						
						}
					}
		return rulesContainer;
	}

}
