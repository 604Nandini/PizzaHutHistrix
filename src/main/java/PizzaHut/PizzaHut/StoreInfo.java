package PizzaHut.PizzaHut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/pizzas")
public class StoreInfo {
	
	@Autowired
	RestTemplate restTemplate;

	@Bean
	public RestTemplate restTemplate() {
	RestTemplate rtemp=new RestTemplate();
	return rtemp;
	}
	List<Pizza> pizzaList=new ArrayList();
	public StoreInfo() {
		pizzaList.add(new Pizza("Spicy","Exotica"));
		pizzaList.add(new Pizza("Spicy","Barbeque"));
		pizzaList.add(new Pizza("Veggie","FarmHouse"));
		pizzaList.add(new Pizza("Veggie","PannerExotica"));
		pizzaList.add(new Pizza("Cheese","Margherita"));
		pizzaList.add(new Pizza("Cheese","GarlicBread"));
	}
	
	@RequestMapping(value="/info")
	public String getStoreInfo(HttpServletRequest  request, HttpServletResponse response)
	{
		String r="";
		for(Pizza p:pizzaList)
		{
			r+="<tr><td>"+p.getPizzaType()+"</td> : <td>"+p.getPizzaName()+"</td></tr>";
		}
		String res="<html><body><B>Instance Name : " + request.getLocalName() + "<BR>";
		res += "<B>Port : </B>" +  + request.getLocalPort() + "<BR>";
		res += "<table border=\"2\"><thead><th>Pizza Type</th><th>Pizza Name</th></thead>";
		res+=  r;
		res += "</table></body></html>";
		System.out.println("sever running" + request.getLocalPort());
		return res;
	}
	
	@RequestMapping(value="/updatePage")
	public String updatePage(HttpServletRequest  request, HttpServletResponse response)
	{
//		String r="";
//		for(Pizza p:pizzaList)
//		{
//			r+="<tr><td>"+p.getPizzaType()+"</td> : <td>"+p.getPizzaName()+"</td></tr>";
//		}
		String res="<html><body><B>Instance Name : " + request.getLocalName() + "<BR>";
		res += "<B>Port : </B>" +  + request.getLocalPort() + "<BR>";
		res += "<form method=\"POST\" action=\"http://localhost:8999/pizzas/updatePizza\">";
		res+=  "Pizza Type:<input type=\"text\" name=\"pizzaType\">";
		res+=  "Pizza Name:<input type=\"text\" name=\"pizzaName\">";
		res+=  "Old Name:<input type=\"text\" name=\"oldName\">";
		res+=  "<input type=\"submit\" name=\"submit\">";
		res += "</form></body></html>";
		System.out.println("sever running" + request.getLocalPort());
		return res;
	}
	
	@PostMapping("/updatePizza")
	public String updatePizza(Pizza fPizza,String oldName)
	{
		//pizzaList.add(fPizza);
		int c=0;
		for(Pizza pizza:pizzaList)
		{
			if(pizza.getPizzaName().equalsIgnoreCase(oldName))
			{
				pizzaList.set(c, fPizza);
			}
			c++;
		}
		return "Succesfully updated "+oldName+" to " +fPizza.toString();
	}
	
	@PostMapping(value="/add",consumes=MediaType.APPLICATION_JSON_VALUE)
	public String addPizza(@RequestBody Pizza p,HttpServletRequest request, HttpServletResponse response) {
	pizzaList.add(p);
	int localPort=request.getLocalPort();
	HashMap hmap=new HashMap();
	hmap.put("pizzaName",p.getPizzaName());
	hmap.put("pizzaType", p.getPizzaType());
	System.out.println(hmap);
	String json=JSONObject.toJSONString(hmap);
	System.out.println("json value "+localPort);

	HttpHeaders httpHead=new HttpHeaders();
	httpHead.setContentType(MediaType.APPLICATION_JSON);
	HttpEntity<String> ent=new HttpEntity(json,httpHead);
	if(localPort==8999) {
		String res=restTemplate.exchange("http://localhost:9000/pizzas/add",
		HttpMethod.POST,ent, java.lang.String.class).getBody();
		System.out.println("Send data to 9000");
		System.out.println("res "+ent.getBody());
	}
	else if(localPort==9000) {
		String res=restTemplate.exchange("http://localhost:9001/pizzas/add",
		HttpMethod.POST,ent, java.lang.String.class).getBody();
		System.out.println("send data to 9001");
		System.out.println("res "+ent.getBody());
	}
//	else if(localPort==9001) {
//		String res=restTemplate.exchange("http://localhost:8999/pizzas/add",
//		HttpMethod.POST,ent, java.lang.String.class).getBody();
//		System.out.println("send data to 8999");
//	}
	return "<HTML><BODY><B>ADDED THE PIZZA</B></BODY></HTML>";
	}

	
}
