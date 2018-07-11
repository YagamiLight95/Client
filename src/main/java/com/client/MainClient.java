package com.client;

import java.time.Duration;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.server1.User;

import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class MainClient {

	//static JedisPool jp;
	
	public static void main(String[] args) {
		
		System.out.println("Script partito...");
		try {
		//	jp = new JedisPool(buildPoolConfig(),"172.30.118.49", 6379);
			Jedis jClient = new Jedis("172.30.118.49", 6379); 
					//jp.getResource();
			System.out.println(System.getenv("REDIS_PWD"));
			jClient.auth(System.getenv("REDIS_PWD"));
			jClient.connect();
			System.out.println("Mi metto in ascolto sul canale Nuovi_Utenti");
			jClient.subscribe(new JedisPubSub() {
			    @Override
			    public void onMessage(String channel, String message) {
			    	User user = null;
			    	Jedis jClient1 = new Jedis("172.30.118.49", 6379);
					jClient1.auth(System.getenv("REDIS_PWD"));
					jClient1.connect();
					Gson g = new Gson();
		    		user = g.fromJson(jClient1.rpop("Users"),User.class);
		    		ArrayList<User> users = new ArrayList<User>();
		    		while(user!=null){
		    			//StringTokenizer st = new StringTokenizer(useIt,",");
			    		//user = new User(st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken());
			    		users.add(user);
			    		RestTemplate rt = new RestTemplate();
			    		String gson = g.toJson(user);
			    		ResponseEntity<Boolean> re = rt.postForEntity("http://server-myproject.192.168.99.100.nip.io/insertrequest/", gson, Boolean.class);
			    		System.out.println(re.getBody());
			    		
			    		user = g.fromJson(jClient1.rpop("Users"), User.class);
			    		
			    	}
		    		jClient1.disconnect();
		    		if(users.size()>0) {
		    			System.out.println(users.toString() + " " + users.size());
		    			System.out.println();
		    		}
			    }
			}, "Nuovi_Utenti");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	private static JedisPoolConfig buildPoolConfig() {
	    final JedisPoolConfig poolConfig = new JedisPoolConfig();
	    poolConfig.setMaxTotal(128);
	    poolConfig.setMaxIdle(128);
	    poolConfig.setMinIdle(16);
	    poolConfig.setTestOnBorrow(true);
	    poolConfig.setTestOnReturn(true);
	    poolConfig.setTestWhileIdle(true);
	    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
	    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
	    poolConfig.setNumTestsPerEvictionRun(3);
	    poolConfig.setBlockWhenExhausted(true);
	    return poolConfig;
	}
	
	*/
}
